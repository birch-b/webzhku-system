package com.taobao.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.taobao.util.DBUtil;

public class ProductDAO {

    public List<Map<String, Object>> getProductsByShopId(long shopId, int status, int page, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT p.*, c.name as category_name FROM product p ");
        sql.append("LEFT JOIN category c ON p.category_id = c.id ");
        sql.append("WHERE p.shop_id = ? ");
        if (status >= 0) sql.append("AND p.status = ? ");
        sql.append("ORDER BY p.update_time DESC LIMIT ? OFFSET ?");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setLong(idx++, shopId);
            if (status >= 0) ps.setInt(idx++, status);
            ps.setInt(idx++, pageSize);
            ps.setInt(idx++, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> p = extractProduct(rs);
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getProductCount(long shopId, int status) {
        String sql = status >= 0
            ? "SELECT COUNT(*) FROM product WHERE shop_id = ? AND status = ?"
            : "SELECT COUNT(*) FROM product WHERE shop_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            if (status >= 0) ps.setInt(2, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String, Object> getProductById(long id) {
        String sql = "SELECT p.*, c.name as category_name FROM product p " +
                     "LEFT JOIN category c ON p.category_id = c.id WHERE p.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractProduct(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long addProduct(long shopId, long categoryId, String name, String subtitle,
                           String description, double price, double originalPrice, int stock,
                           String images, String coverImage) {
        String sql = "INSERT INTO product (shop_id, category_id, name, subtitle, description, " +
                     "price, original_price, stock, images, cover_image, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, shopId);
            if (categoryId > 0) ps.setLong(2, categoryId); else ps.setNull(2, java.sql.Types.BIGINT);
            ps.setString(3, name);
            ps.setString(4, subtitle);
            ps.setString(5, description);
            ps.setDouble(6, price);
            ps.setDouble(7, originalPrice);
            ps.setInt(8, stock);
            ps.setString(9, images);
            ps.setString(10, coverImage);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateProduct(long id, long categoryId, String name, String subtitle,
                                  String description, double price, double originalPrice,
                                  String images, String coverImage) {
        String sql = "UPDATE product SET category_id = ?, name = ?, subtitle = ?, description = ?, " +
                     "price = ?, original_price = ?, images = ?, cover_image = ?, update_time = NOW() " +
                     "WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            if (categoryId > 0) ps.setLong(idx++, categoryId); else ps.setNull(idx++, java.sql.Types.BIGINT);
            ps.setString(idx++, name);
            ps.setString(idx++, subtitle);
            ps.setString(idx++, description);
            ps.setDouble(idx++, price);
            ps.setDouble(idx++, originalPrice);
            ps.setString(idx++, images);
            ps.setString(idx++, coverImage);
            ps.setLong(idx++, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean adjustStock(long id, int delta) {
        String sql = "UPDATE product SET stock = stock + ?, update_time = NOW() WHERE id = ? AND (stock + ?) >= 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setLong(2, id);
            ps.setInt(3, delta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean publishProduct(long id) {
        String sql = "UPDATE product SET status = 1, publish_time = NOW(), update_time = NOW() WHERE id = ? AND status = 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean unpublishProduct(long id) {
        String sql = "UPDATE product SET status = 2, update_time = NOW() WHERE id = ? AND status IN (1,3)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(long id) {
        String sql = "DELETE FROM product WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateImages(long id, String images, String coverImage) {
        String sql = "UPDATE product SET images = ?, cover_image = ?, update_time = NOW() WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, images);
            ps.setString(2, coverImage);
            ps.setLong(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Map<String, Object> extractProduct(ResultSet rs) throws SQLException {
        Map<String, Object> p = new HashMap<>();
        p.put("id", rs.getLong("id"));
        p.put("shop_id", rs.getLong("shop_id"));
        p.put("category_id", rs.getObject("category_id"));
        p.put("category_name", rs.getString("category_name"));
        p.put("name", rs.getString("name"));
        p.put("subtitle", rs.getString("subtitle"));
        p.put("description", rs.getString("description"));
        p.put("price", rs.getDouble("price"));
        p.put("original_price", rs.getDouble("original_price"));
        p.put("stock", rs.getInt("stock"));
        p.put("sales", rs.getInt("sales"));
        p.put("status", rs.getInt("status"));
        p.put("images", rs.getString("images"));
        p.put("cover_image", rs.getString("cover_image"));
        p.put("weight", rs.getObject("weight"));
        p.put("create_time", rs.getTimestamp("create_time"));
        p.put("update_time", rs.getTimestamp("update_time"));
        p.put("publish_time", rs.getTimestamp("publish_time"));
        try {
            String imagesStr = rs.getString("images");
            if (imagesStr != null && !imagesStr.isEmpty()) {
                JSONArray arr = new JSONArray(imagesStr);
                List<String> imageList = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    imageList.add(arr.getString(i));
                }
                p.put("imageList", imageList);
            }
        } catch (Exception e) {
            p.put("imageList", new ArrayList<>());
        }
        return p;
    }

    public List<Map<String, Object>> listByCategory(String categoryName) {
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
            try (ResultSet rs = ps.executeQuery()) {
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
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询商品列表失败", e);
        }
        return list;
    }
}
