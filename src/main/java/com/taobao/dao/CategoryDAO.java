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

/**
 * 商品分类数据访问层
 */
public class CategoryDAO {

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    /**
     * 查询某店铺所有分类
     */
    public List<Map<String, Object>> getCategoriesByShopId(long shopId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE shop_id = ? AND status = 1 ORDER BY sort_order ASC, id ASC";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, shopId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 查询某店铺所有分类（含禁用的）
     */
    public List<Map<String, Object>> getAllCategoriesByShopId(long shopId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE shop_id = ? ORDER BY sort_order ASC, id ASC";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, shopId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 根据ID查询分类
     */
    public Map<String, Object> getCategoryById(long id) {
        String sql = "SELECT * FROM category WHERE id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> cat = extractCategory(rs);
                return cat;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    /**
     * 新增分类
     */
    public boolean addCategory(long shopId, String name, long parentId, int sortOrder) {
        String sql = "INSERT INTO category (shop_id, parent_id, name, sort_order) VALUES (?, ?, ?, ?)";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, shopId);
            ps.setLong(2, parentId);
            ps.setString(3, name);
            ps.setInt(4, sortOrder);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 修改分类
     */
    public boolean updateCategory(long id, String name, int sortOrder, int status) {
        String sql = "UPDATE category SET name = ?, sort_order = ?, status = ? WHERE id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, sortOrder);
            ps.setInt(3, status);
            ps.setLong(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 删除分类（逻辑删除，改为禁用）
     */
    public boolean deleteCategory(long id) {
        String sql = "UPDATE category SET status = 0 WHERE id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 检查分类名称是否重复（同一店铺下）
     */
    public boolean isNameExists(long shopId, String name, Long excludeId) {
        String sql = excludeId != null
            ? "SELECT COUNT(*) FROM category WHERE shop_id = ? AND name = ? AND id != ?"
            : "SELECT COUNT(*) FROM category WHERE shop_id = ? AND name = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, shopId);
            ps.setString(2, name);
            if (excludeId != null) ps.setLong(3, excludeId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return false;
    }

    /**
     * 从ResultSet提取分类
     */
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

    public void close() {
        DBUtil.close(conn, ps, rs);
    }
}
