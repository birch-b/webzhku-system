package com.taobao.servlet.shop.servlet;

import com.taobao.util.DBUtil;
import com.taobao.util.FileUploadUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/shop/product/*")
public class ShopProductServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/list";
        switch (pathInfo) {
            case "/list": listProducts(req, resp); break;
            case "/edit": showEdit(req, resp); break;
            default: listProducts(req, resp);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";
        switch (pathInfo) {
            case "/save": saveProduct(req, resp); break;
            case "/updateStatus": updateStatus(req, resp); break;
            default: resp.sendError(404);
        }
    }

    private long getShopId(Long userId) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps0 = conn.prepareStatement("SELECT id FROM shop WHERE user_id = ?");
            ps0.setLong(1, userId); ResultSet rs0 = ps0.executeQuery();
            return rs0.next() ? rs0.getLong("id") : 0;
        }
    }

    private void listProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        try (Connection conn = DBUtil.getConnection()) {
            long shopId = getShopId(userId);
            PreparedStatement ps = conn.prepareStatement(
                "SELECT p.*, c.name AS cat_name FROM product p LEFT JOIN category c ON p.category_id = c.id WHERE p.shop_id = ? ORDER BY p.id DESC");
            ps.setLong(1, shopId); ResultSet rs = ps.executeQuery();
            List<String[]> products = new ArrayList<>();
            while (rs.next()) {
                products.add(new String[]{String.valueOf(rs.getLong("id")), rs.getString("name"),
                    rs.getString("cat_name"), rs.getString("price"), String.valueOf(rs.getInt("stock")),
                    String.valueOf(rs.getInt("sales")), String.valueOf(rs.getInt("status")),
                    rs.getString("cover_image")});
            }
            req.setAttribute("products", products);
            req.getRequestDispatcher("/shop/product_list.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); req.getRequestDispatcher("/shop/product_list.jsp").forward(req, resp); }
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        String idStr = req.getParameter("id");
        try (Connection conn = DBUtil.getConnection()) {
            long shopId = getShopId(userId);
            PreparedStatement psCat = conn.prepareStatement("SELECT * FROM category WHERE shop_id = ? AND status = 1");
            psCat.setLong(1, shopId); ResultSet rsCat = psCat.executeQuery();
            List<String[]> categories = new ArrayList<>();
            while (rsCat.next()) { categories.add(new String[]{String.valueOf(rsCat.getLong("id")), rsCat.getString("name")}); }
            req.setAttribute("categories", categories);
            if (idStr != null) {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM product WHERE id = ?");
                ps.setLong(1, Long.parseLong(idStr)); ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    req.setAttribute("prodId", rs.getLong("id")); req.setAttribute("prodName", rs.getString("name"));
                    req.setAttribute("prodDesc", rs.getString("description")); req.setAttribute("prodPrice", rs.getString("price"));
                    req.setAttribute("prodStock", rs.getInt("stock")); req.setAttribute("prodCatId", rs.getLong("category_id"));
                    req.setAttribute("prodImages", rs.getString("images")); req.setAttribute("prodCover", rs.getString("cover_image"));
                }
            }
            req.setAttribute("shopId", shopId);
            req.getRequestDispatcher("/shop/product_edit.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void saveProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        try (Connection conn = DBUtil.getConnection()) {
            long shopId = getShopId(userId);
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setSizeMax(10 * 1024 * 1024);
            List<FileItem> items = upload.parseRequest(req);
            String name = "", description = "", price = "", stock = "", categoryId = "", idStr = "";
            List<String> imageUrls = new ArrayList<>(); String coverImage = "";
            for (FileItem item : items) {
                if (item.isFormField()) {
                    switch (item.getFieldName()) {
                        case "name": name = item.getString("UTF-8"); break;
                        case "description": description = item.getString("UTF-8"); break;
                        case "price": price = item.getString(); break;
                        case "stock": stock = item.getString(); break;
                        case "categoryId": categoryId = item.getString(); break;
                        case "id": idStr = item.getString(); break;
                    }
                } else {
                    String url = FileUploadUtil.upload(item, req, "product");
                    if (url != null) { imageUrls.add(url); if (coverImage.isEmpty()) coverImage = url; }
                }
            }
            String imagesJson = imageUrls.isEmpty() ? "" : "[\"" + String.join("\",\"", imageUrls) + "\"]";
            if (idStr != null && !idStr.isEmpty()) {
                StringBuilder updateSql = new StringBuilder("UPDATE product SET name=?, description=?, price=?, stock=?, category_id=?");
                if (!coverImage.isEmpty()) updateSql.append(", cover_image=?");
                if (!imagesJson.isEmpty()) updateSql.append(", images=?");
                updateSql.append(" WHERE id=?");
                PreparedStatement ps = conn.prepareStatement(updateSql.toString());
                int idx = 1;
                ps.setString(idx++, name); ps.setString(idx++, description);
                ps.setDouble(idx++, Double.parseDouble(price)); ps.setInt(idx++, Integer.parseInt(stock));
                ps.setLong(idx++, Long.parseLong(categoryId));
                if (!coverImage.isEmpty()) ps.setString(idx++, coverImage);
                if (!imagesJson.isEmpty()) ps.setString(idx++, imagesJson);
                ps.setLong(idx, Long.parseLong(idStr)); ps.executeUpdate();
            } else {
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO product (shop_id, category_id, name, description, price, stock, cover_image, images, status, publish_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1, NOW())");
                ps.setLong(1, shopId); ps.setLong(2, Long.parseLong(categoryId));
                ps.setString(3, name); ps.setString(4, description);
                ps.setDouble(5, Double.parseDouble(price)); ps.setInt(6, Integer.parseInt(stock));
                ps.setString(7, coverImage); ps.setString(8, imagesJson);
                ps.executeUpdate();
            }
            resp.sendRedirect(req.getContextPath() + "/shop/product/list");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void updateStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long id = Long.parseLong(req.getParameter("id"));
        int status = Integer.parseInt(req.getParameter("status"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE product SET status = ? WHERE id = ?");
            ps.setInt(1, status); ps.setLong(2, id); ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/shop/product/list");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }
}
