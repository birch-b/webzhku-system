package com.taobao.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taobao.util.DBUtil;

public class CategoryDAO {

    public List<Map<String, Object>> getCategoriesByShopId(long shopId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE shop_id = ? AND status = 1 ORDER BY sort_order ASC, id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractCategory(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询分类列表失败", e);
        }
        return list;
    }

    public List<Map<String, Object>> getAllCategoriesByShopId(long shopId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE shop_id = ? ORDER BY sort_order ASC, id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractCategory(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询分类列表失败", e);
        }
        return list;
    }

    public Map<String, Object> getCategoryById(long id) {
        String sql = "SELECT * FROM category WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractCategory(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询分类失败", e);
        }
        return null;
    }

    public boolean addCategory(long shopId, String name, long parentId, int sortOrder) {
        String sql = "INSERT INTO category (shop_id, parent_id, name, sort_order) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            ps.setLong(2, parentId);
            ps.setString(3, name);
            ps.setInt(4, sortOrder);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("添加分类失败", e);
        }
    }

    public boolean updateCategory(long id, String name, int sortOrder, int status, long shopId) {
        String sql = "UPDATE category SET name = ?, sort_order = ?, status = ? WHERE id = ? AND shop_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, sortOrder);
            ps.setInt(3, status);
            ps.setLong(4, id);
            ps.setLong(5, shopId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新分类失败", e);
        }
    }

    public boolean deleteCategory(long id) {
        String sql = "UPDATE category SET status = 0 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("删除分类失败", e);
        }
    }

    // 硬删除分类：物理删除（保持原 ShopCategoryServlet 的业务行为，区别于上面的软删除）
    public boolean deleteCategoryHard(long id, long shopId) {
        String sql = "DELETE FROM category WHERE id = ? AND shop_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, shopId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("硬删除分类失败", e);
        }
    }

    public boolean isNameExists(long shopId, String name, Long excludeId) {
        String sql = excludeId != null
            ? "SELECT COUNT(*) FROM category WHERE shop_id = ? AND name = ? AND id != ?"
            : "SELECT COUNT(*) FROM category WHERE shop_id = ? AND name = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            ps.setString(2, name);
            if (excludeId != null) ps.setLong(3, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("检查分类名称失败", e);
        }
        return false;
    }

    private Map<String, Object> extractCategory(ResultSet rs) throws SQLException {
        Map<String, Object> cat = new HashMap<>();
        cat.put("id", rs.getLong("id"));
        cat.put("shop_id", rs.getLong("shop_id"));
        cat.put("parent_id", rs.getLong("parent_id"));
        cat.put("name", rs.getString("name"));
        cat.put("sort_order", rs.getInt("sort_order"));
        cat.put("status", rs.getInt("status"));
        cat.put("create_time", rs.getTimestamp("create_time"));
        return cat;
    }

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
