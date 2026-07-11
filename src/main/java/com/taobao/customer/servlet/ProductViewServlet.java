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
        String sort = req.getParameter("sort");
        if (sort == null || sort.isEmpty()) sort = "sales";
        int page = PageUtil.getPage(req);
        int pageSize = 12;
        try (Connection conn = DBUtil.getConnection()) {
            // 查询总数
            StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM product WHERE status = 1");
            if (categoryId != null && !categoryId.isEmpty()) countSql.append(" AND category_id = ?");
            PreparedStatement psCount = conn.prepareStatement(countSql.toString());
            int idx = 1;
            if (categoryId != null && !categoryId.isEmpty()) psCount.setLong(idx++, Long.parseLong(categoryId));
            ResultSet rsCount = psCount.executeQuery();
            int totalCount = rsCount.next() ? rsCount.getInt(1) : 0;
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // 排序
            String orderClause = "sales DESC";
            switch (sort) {
                case "price_asc": orderClause = "price ASC"; break;
                case "price_desc": orderClause = "price DESC"; break;
                case "newest": orderClause = "publish_time DESC"; break;
                default: orderClause = "sales DESC"; break;
            }

            // 查询商品列表
            StringBuilder sql = new StringBuilder("SELECT p.*, s.shop_name FROM product p LEFT JOIN shop s ON p.shop_id = s.id WHERE p.status = 1");
            if (categoryId != null && !categoryId.isEmpty()) sql.append(" AND p.category_id = ?");
            sql.append(" ORDER BY p.").append(orderClause).append(" LIMIT ?, ?");
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            idx = 1;
            if (categoryId != null && !categoryId.isEmpty()) ps.setLong(idx++, Long.parseLong(categoryId));
            ps.setInt(idx++, (page - 1) * pageSize);
            ps.setInt(idx++, pageSize);
            ResultSet rs = ps.executeQuery();
            List<String[]> products = new ArrayList<>();
            while (rs.next()) {
                products.add(new String[]{String.valueOf(rs.getLong("id")), rs.getString("name"),
                        rs.getString("price"), rs.getString("cover_image"),
                        String.valueOf(rs.getInt("sales")), rs.getString("shop_name")});
            }
            req.setAttribute("products", products);
            req.setAttribute("page", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("totalCount", totalCount);
            req.setAttribute("categoryId", categoryId != null ? categoryId : "");
            req.setAttribute("sort", sort);
            req.getRequestDispatcher("/product_list.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); req.getRequestDispatcher("/product_list.jsp").forward(req, resp); }
    }

    private void searchProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        String sort = req.getParameter("sort");
        if (sort == null || sort.isEmpty()) sort = "sales";
        int page = PageUtil.getPage(req);
        int pageSize = 12;
        try (Connection conn = DBUtil.getConnection()) {
            // 查询总数
            PreparedStatement psCount = conn.prepareStatement(
                    "SELECT COUNT(*) FROM product p LEFT JOIN category c ON p.category_id = c.id " +
                    "LEFT JOIN category pc ON c.parent_id = pc.id " +
                    "WHERE p.status = 1 AND (p.name LIKE ? OR p.description LIKE ? OR c.name LIKE ? OR pc.name LIKE ?)");
            psCount.setString(1, "%" + keyword + "%");
            psCount.setString(2, "%" + keyword + "%");
            psCount.setString(3, "%" + keyword + "%");
            psCount.setString(4, "%" + keyword + "%");
            ResultSet rsCount = psCount.executeQuery();
            int totalCount = rsCount.next() ? rsCount.getInt(1) : 0;
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // 排序
            String orderClause = "sales DESC";
            switch (sort) {
                case "price_asc": orderClause = "price ASC"; break;
                case "price_desc": orderClause = "price DESC"; break;
                case "newest": orderClause = "publish_time DESC"; break;
                default: orderClause = "sales DESC"; break;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT p.*, s.shop_name FROM product p LEFT JOIN shop s ON p.shop_id = s.id " +
                    "LEFT JOIN category c ON p.category_id = c.id " +
                    "LEFT JOIN category pc ON c.parent_id = pc.id " +
                    "WHERE p.status = 1 AND (p.name LIKE ? OR p.description LIKE ? OR c.name LIKE ? OR pc.name LIKE ?) " +
                    "ORDER BY p." + orderClause + " LIMIT ?, ?");
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");
            ps.setString(4, "%" + keyword + "%");
            ps.setInt(5, (page - 1) * pageSize);
            ps.setInt(6, pageSize);
            ResultSet rs = ps.executeQuery();
            List<String[]> products = new ArrayList<>();
            while (rs.next()) {
                products.add(new String[]{String.valueOf(rs.getLong("id")), rs.getString("name"),
                        rs.getString("price"), rs.getString("cover_image"),
                        String.valueOf(rs.getInt("sales")), rs.getString("shop_name")});
            }
            req.setAttribute("products", products);
            req.setAttribute("keyword", keyword);
            req.setAttribute("page", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("totalCount", totalCount);
            req.setAttribute("sort", sort);
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
