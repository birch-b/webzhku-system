package com.taobao.service.impl;

import com.taobao.service.CategoryService;
import com.taobao.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryServiceImpl implements CategoryService {
    @Override
    public List<Map<String, Object>> listRootCategory() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT DISTINCT name FROM category WHERE parent_id = 0 AND status = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            long idx = 1;
            while (rs.next()) {
                Map<String, Object> cat = new HashMap<>();
                cat.put("id", idx++);
                cat.put("name", rs.getString("name"));
                list.add(cat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询分类失败", e);
        }
        return list;
    }
}