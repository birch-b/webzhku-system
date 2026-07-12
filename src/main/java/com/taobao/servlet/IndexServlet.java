package com.taobao.servlet;

import com.taobao.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页数据加载Servlet
 * 加载公告、分类、热门推荐商品
 */
@WebServlet("/index")
public class IndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String categoryName = req.getParameter("category");

        try (Connection conn = DBUtil.getConnection()) {
            // 1. 查询最新公告（最多3条）
            List<Map<String, Object>> announcements = getAnnouncements(conn);
            req.setAttribute("announcements", announcements);

            // 2. 查询所有一级分类
            List<Map<String, Object>> categories = getCategories(conn);
            req.setAttribute("categories", categories);

            // 3. 查询商品（支持分类筛选）
            List<Map<String, Object>> products = getProductsByCategory(conn, categoryName);
            req.setAttribute("products", products);
            req.setAttribute("selectedCategory", categoryName);

            // 4. 查询商家店铺名称（如果是商家角色）
            String role = (String) req.getSession().getAttribute("userRole");
            if ("shopkeeper".equals(role)) {
                Long userId = (Long) req.getSession().getAttribute("userId");
                String shopName = getShopNameByUserId(conn, userId);
                req.setAttribute("shopName", shopName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    /**
     * 查询最新公告
     */
    private List<Map<String, Object>> getAnnouncements(Connection conn) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT id, title, priority, create_time FROM announcement WHERE status = 1 ORDER BY priority DESC, create_time DESC LIMIT 3";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> ann = new HashMap<>();
                ann.put("id", rs.getLong("id"));
                ann.put("title", rs.getString("title"));
                ann.put("priority", rs.getInt("priority"));
                ann.put("createTime", rs.getTimestamp("create_time"));
                list.add(ann);
            }
        }
        return list;
    }

    /**
     * 查询所有一级分类
     */
    private List<Map<String, Object>> getCategories(Connection conn) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT DISTINCT name FROM category WHERE parent_id = 0 AND status = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            long idx = 1;
            while (rs.next()) {
                Map<String, Object> cat = new HashMap<>();
                cat.put("id", idx++);
                cat.put("name", rs.getString("name"));
                list.add(cat);
            }
        }
        return list;
    }

    /**
     * 查询商品（支持分类筛选）
     */
    private List<Map<String, Object>> getProductsByCategory(Connection conn, String categoryName) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.id, p.name, p.price, p.cover_image AS main_image, p.sales, s.shop_name ");
        sql.append("FROM product p LEFT JOIN shop s ON p.shop_id = s.id ");
        sql.append("LEFT JOIN category c ON p.category_id = c.id ");
        sql.append("LEFT JOIN category pc ON c.parent_id = pc.id ");
        sql.append("WHERE p.status = 1 ");
        
        if (categoryName != null && !categoryName.isEmpty()) {
            sql.append("AND (c.name = ? OR pc.name = ?) ");
        }
        
        sql.append("ORDER BY p.sales DESC LIMIT 12");
        
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (categoryName != null && !categoryName.isEmpty()) {
                ps.setString(1, categoryName);
                ps.setString(2, categoryName);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> prod = new HashMap<>();
                prod.put("id", rs.getLong("id"));
                prod.put("name", rs.getString("name"));
                prod.put("price", rs.getBigDecimal("price"));
                prod.put("main_image", rs.getString("main_image"));
                prod.put("sales", rs.getInt("sales"));
                prod.put("shop_name", rs.getString("shop_name"));
                list.add(prod);
            }
        }
        return list;
    }

    private String getShopNameByUserId(Connection conn, Long userId) throws SQLException {
        String sql = "SELECT shop_name FROM shop WHERE owner_id = ? AND status = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("shop_name");
            }
        }
        return "";
    }
}
