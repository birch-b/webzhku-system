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

/**
 * 商品数据访问层
 */
public class ProductDAO {

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    /**
     * 分页查询店铺商品列表
     */
    public List<Map<String, Object>> getProductsByShopId(long shopId, int status, int page, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT p.*, c.name as category_name FROM product p ");
        sql.append("LEFT JOIN category c ON p.category_id = c.id ");
        sql.append("WHERE p.shop_id = ? ");
        if (status >= 0) sql.append("AND p.status = ? ");
        sql.append("ORDER BY p.update_time DESC LIMIT ? OFFSET ?");

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            ps.setLong(idx++, shopId);
            if (status >= 0) ps.setInt(idx++, status);
            ps.setInt(idx++, pageSize);
            ps.setInt(idx++, (page - 1) * pageSize);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> p = extractProduct(rs);
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 查询商品总数
     */
    public int getProductCount(long shopId, int status) {
        String sql = status >= 0
            ? "SELECT COUNT(*) FROM product WHERE shop_id = ? AND status = ?"
            : "SELECT COUNT(*) FROM product WHERE shop_id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, shopId);
            if (status >= 0) ps.setInt(2, status);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return 0;
    }

    /**
     * 根据ID查询商品
     */
    public Map<String, Object> getProductById(long id) {
        String sql = "SELECT p.*, c.name as category_name FROM product p " +
                     "LEFT JOIN category c ON p.category_id = c.id WHERE p.id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractProduct(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    /**
     * 新增商品（待上架）
     */
    public long addProduct(long shopId, long categoryId, String name, String subtitle,
                           String description, double price, double originalPrice, int stock,
                           String images, String coverImage) {
        String sql = "INSERT INTO product (shop_id, category_id, name, subtitle, description, " +
                     "price, original_price, stock, images, cover_image, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
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
                rs = ps.getGeneratedKeys();
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return -1;
    }

    /**
     * 更新商品信息
     */
    public boolean updateProduct(long id, long categoryId, String name, String subtitle,
                                  String description, double price, double originalPrice,
                                  String images, String coverImage) {
        String sql = "UPDATE product SET category_id = ?, name = ?, subtitle = ?, description = ?, " +
                     "price = ?, original_price = ?, images = ?, cover_image = ?, update_time = NOW() " +
                     "WHERE id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
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
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 调整库存
     */
    public boolean adjustStock(long id, int delta) {
        String sql = "UPDATE product SET stock = stock + ?, update_time = NOW() WHERE id = ? AND (stock + ?) >= 0";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, delta);
            ps.setLong(2, id);
            ps.setInt(3, delta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 上架商品
     */
    public boolean publishProduct(long id) {
        String sql = "UPDATE product SET status = 1, publish_time = NOW(), update_time = NOW() WHERE id = ? AND status = 0";
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
     * 下架商品
     */
    public boolean unpublishProduct(long id) {
        String sql = "UPDATE product SET status = 2, update_time = NOW() WHERE id = ? AND status IN (1,3)";
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
     * 删除商品（物理删除）
     */
    public boolean deleteProduct(long id) {
        String sql = "DELETE FROM product WHERE id = ?";
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
     * 更新商品多图
     */
    public boolean updateImages(long id, String images, String coverImage) {
        String sql = "UPDATE product SET images = ?, cover_image = ?, update_time = NOW() WHERE id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, images);
            ps.setString(2, coverImage);
            ps.setLong(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 从ResultSet提取商品信息
     */
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
        // 解析images为List
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

    public void close() {
        DBUtil.close(conn, ps, rs);
    }
}
