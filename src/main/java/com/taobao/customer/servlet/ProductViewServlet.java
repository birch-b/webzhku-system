package com.taobao.customer.servlet;

import com.taobao.util.DBUtil;
import com.taobao.util.PageUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/product/*")
public class ProductViewServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/list";
        switch (pathInfo) {
            case "/list": listProducts(req, resp); break;
            case "/search": searchProducts(req, resp); break;
            case "/detail": showDetail(req, resp); break;
            default: listProducts(req, resp);
        }
    }

    private void listProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String categoryId = req.getParameter("categoryId");
        int page = PageUtil.getPage(req);
        try (Connection conn = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT p.*, s.shop_name FROM product p LEFT JOIN shop s ON p.shop_id = s.id WHERE p.status = 1");
            if (categoryId != null && !categoryId.isEmpty()) sql.append(" AND p.category_id = ?");
            sql.append(" ORDER BY p.sales DESC LIMIT ?, 20");
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            if (categoryId != null && !categoryId.isEmpty()) ps.setLong(idx++, Long.parseLong(categoryId));
            ps.setInt(idx++, (page - 1) * 20);
            ResultSet rs = ps.executeQuery();
            List<String[]> products = new ArrayList<>();
            while (rs.next()) {
                products.add(new String[]{String.valueOf(rs.getLong("id")), rs.getString("name"),
                    rs.getString("price"), rs.getString("cover_image"),
                    String.valueOf(rs.getInt("sales")), rs.getString("shop_name")});
            }
            req.setAttribute("products", products); req.setAttribute("page", page);
            req.getRequestDispatcher("/product_list.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); req.getRequestDispatcher("/product_list.jsp").forward(req, resp); }
    }

    private void searchProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT p.*, s.shop_name FROM product p LEFT JOIN shop s ON p.shop_id = s.id WHERE p.status = 1 AND p.name LIKE ? ORDER BY p.sales DESC LIMIT 20");
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            List<String[]> products = new ArrayList<>();
            while (rs.next()) {
                products.add(new String[]{String.valueOf(rs.getLong("id")), rs.getString("name"),
                    rs.getString("price"), rs.getString("cover_image"),
                    String.valueOf(rs.getInt("sales")), rs.getString("shop_name")});
            }
            req.setAttribute("products", products); req.setAttribute("keyword", keyword);
            req.getRequestDispatcher("/product_list.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); req.getRequestDispatcher("/product_list.jsp").forward(req, resp); }
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT p.*, s.shop_name, s.id AS shop_id FROM product p LEFT JOIN shop s ON p.shop_id = s.id WHERE p.id = ?");
            ps.setLong(1, id); ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                req.setAttribute("prodId", rs.getLong("id")); req.setAttribute("prodName", rs.getString("name"));
                req.setAttribute("prodDesc", rs.getString("description")); req.setAttribute("prodPrice", rs.getString("price"));
                req.setAttribute("prodStock", rs.getInt("stock")); req.setAttribute("prodSales", rs.getInt("sales"));
                req.setAttribute("prodImages", rs.getString("images")); req.setAttribute("prodCover", rs.getString("cover_image"));
                req.setAttribute("shopName", rs.getString("shop_name"));
            }
            PreparedStatement ps2 = conn.prepareStatement(
                "SELECT r.*, u.nickname FROM review r LEFT JOIN user u ON r.user_id = u.id WHERE r.product_id = ? AND r.status = 1 ORDER BY r.create_time DESC LIMIT 10");
            ps2.setLong(1, id); ResultSet rs2 = ps2.executeQuery();
            List<String[]> reviews = new ArrayList<>();
            while (rs2.next()) {
                reviews.add(new String[]{rs2.getString("nickname"), String.valueOf(rs2.getInt("rating")),
                    rs2.getString("content"), rs2.getString("reply"), rs2.getTimestamp("create_time").toString()});
            }
            req.setAttribute("reviews", reviews);
            req.getRequestDispatcher("/product_detail.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }
}
