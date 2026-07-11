package com.taobao.servlet;

import com.taobao.util.DBUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

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

@WebServlet("/product/ajax")
public class ProductAjaxServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        
        String categoryName = req.getParameter("category");
        
        try (Connection conn = DBUtil.getConnection()) {
            List<Map<String, Object>> products = getProductsByCategory(conn, categoryName);
            String json = new ObjectMapper().writeValueAsString(products);
            resp.getWriter().write(json);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("[]");
        }
    }

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
}