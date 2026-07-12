package com.taobao.service.impl;

import com.taobao.service.ProductService;
import com.taobao.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductServiceImpl implements ProductService {
    @Override
    public List<Map<String, Object>> listProductByCategory(String categoryName) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.id, p.name, p.price, p.cover_image AS main_image, p.sales, s.shop_name ");
        sql.append("FROM product p LEFT JOIN shop s ON p.shop_id = s.id ");
        sql.append("LEFT JOIN category c ON p.category_id = c.id ");
        sql.append("LEFT JOIN category pc ON c.parent_id = pc.id ");
        sql.append("WHERE p.status = 1 ");

        boolean hasCat = categoryName != null && !categoryName.isEmpty();
        if (hasCat) {
            sql.append("AND (c.name = ? OR pc.name = ?) ");
        }
        sql.append("ORDER BY p.sales DESC LIMIT 12");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (hasCat) {
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
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询商品列表失败", e);
        }
        return list;
    }
}