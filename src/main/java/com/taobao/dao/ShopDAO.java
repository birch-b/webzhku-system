package com.taobao.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.taobao.util.DBUtil;

/**
 * 店铺信息数据访问层
 */
public class ShopDAO {

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    /**
     * 根据用户ID查询店铺信息
     */
    public Map<String, Object> getShopByUserId(long userId) {
        String sql = "SELECT * FROM shop WHERE user_id = ?";
        Map<String, Object> shop = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                shop = extractShop(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return shop;
    }

    /**
     * 根据店铺ID查询店铺信息
     */
    public Map<String, Object> getShopById(long shopId) {
        String sql = "SELECT * FROM shop WHERE id = ?";
        Map<String, Object> shop = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, shopId);
            rs = ps.executeQuery();
            if (rs.next()) {
                shop = extractShop(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return shop;
    }

    /**
     * 更新店铺信息
     */
    public boolean updateShop(long userId, String shopName, String description, String avatar) {
        StringBuilder sql = new StringBuilder("UPDATE shop SET update_time = NOW()");
        if (shopName != null) sql.append(", shop_name = ?");
        if (description != null) sql.append(", description = ?");
        if (avatar != null) sql.append(", avatar = ?");
        sql.append(" WHERE user_id = ?");

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            if (shopName != null) ps.setString(idx++, shopName);
            if (description != null) ps.setString(idx++, description);
            if (avatar != null) ps.setString(idx++, avatar);
            ps.setLong(idx++, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 创建店铺（审核通过后）
     */
    public boolean createShop(long userId, String shopName, String category) {
        String sql = "INSERT INTO shop (user_id, shop_name, shop_category) VALUES (?, ?, ?)";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setString(2, shopName);
            ps.setString(3, category);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 获取店铺审核/营业状态（供Filter使用）
     * 返回值：-2=未开店，-1=关闭，0=待审核，1=营业中
     */
    public int getShopStatus(long userId) {
        // 先检查是否在申请表中
        String applySql = "SELECT status FROM shop_apply WHERE user_id = ? ORDER BY id DESC LIMIT 1";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(applySql);
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            if (!rs.next()) {
                return -2; // 未申请开店
            }
            int status = rs.getInt("status");
            rs.close();
            ps.close();
            if (status == 0) return 0; // 待审核
            if (status == 2) return -2; // 拒绝，重新申请

            // 审核通过，查询店铺表
            String shopSql = "SELECT status FROM shop WHERE user_id = ?";
            ps = conn.prepareStatement(shopSql);
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("status");
            }
            return -2;
        } catch (SQLException e) {
            e.printStackTrace();
            return -2;
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 查询入驻申请记录
     */
    public Map<String, Object> getApplyByUserId(long userId) {
        String sql = "SELECT * FROM shop_apply WHERE user_id = ? ORDER BY id DESC LIMIT 1";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> apply = new HashMap<>();
                apply.put("id", rs.getLong("id"));
                apply.put("shop_name", rs.getString("shop_name"));
                apply.put("shop_category", rs.getString("shop_category"));
                apply.put("description", rs.getString("description"));
                apply.put("contact_name", rs.getString("contact_name"));
                apply.put("contact_phone", rs.getString("contact_phone"));
                apply.put("contact_email", rs.getString("contact_email"));
                apply.put("id_card", rs.getString("id_card"));
                apply.put("license_no", rs.getString("license_no"));
                apply.put("license_img", rs.getString("license_img"));
                apply.put("status", rs.getInt("status"));
                apply.put("reject_reason", rs.getString("reject_reason"));
                apply.put("apply_time", rs.getTimestamp("apply_time"));
                apply.put("review_time", rs.getTimestamp("review_time"));
                return apply;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    /**
     * 提交入驻申请
     */
    public boolean submitApply(long userId, String shopName, String category, String description,
                                String contactName, String contactPhone, String contactEmail,
                                String idCard, String licenseNo, String licenseImg) {
        String sql = "INSERT INTO shop_apply (user_id, shop_name, shop_category, description, " +
                     "contact_name, contact_phone, contact_email, id_card, license_no, license_img) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setString(2, shopName);
            ps.setString(3, category);
            ps.setString(4, description);
            ps.setString(5, contactName);
            ps.setString(6, contactPhone);
            ps.setString(7, contactEmail);
            ps.setString(8, idCard);
            ps.setString(9, licenseNo);
            ps.setString(10, licenseImg);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 审核入驻申请（管理员调用）
     */
    public boolean reviewApply(long applyId, int status, String rejectReason) {
        String sql = "UPDATE shop_apply SET status = ?, reject_reason = ?, review_time = NOW() WHERE id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
            ps.setString(2, rejectReason);
            ps.setLong(3, applyId);
            boolean ok = ps.executeUpdate() > 0;
            ps.close();

            // 如果审核通过，创建店铺记录
            if (ok && status == 1) {
                // 从申请记录获取店铺信息
                String selectSql = "SELECT user_id, shop_name, shop_category FROM shop_apply WHERE id = ?";
                ps = conn.prepareStatement(selectSql);
                ps.setLong(1, applyId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    long userId = rs.getLong("user_id");
                    String shopName = rs.getString("shop_name");
                    String category = rs.getString("shop_category");
                    rs.close();
                    ps.close();
                    createShop(userId, shopName, category);
                }
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 获取店铺统计数据
     */
    public Map<String, Object> getShopStats(long shopId) {
        String sql = "SELECT s.total_orders, s.total_sales, s.rating, " +
                     "(SELECT COUNT(*) FROM product WHERE shop_id = ? AND status = 1) as onSaleCount, " +
                     "(SELECT COUNT(*) FROM product WHERE shop_id = ?) as totalProductCount " +
                     "FROM shop s WHERE s.id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, shopId);
            ps.setLong(2, shopId);
            ps.setLong(3, shopId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> stats = new HashMap<>();
                stats.put("totalOrders", rs.getInt("total_orders"));
                stats.put("totalSales", rs.getDouble("total_sales"));
                stats.put("rating", rs.getDouble("rating"));
                stats.put("onSaleCount", rs.getInt("onSaleCount"));
                stats.put("totalProductCount", rs.getInt("totalProductCount"));
                return stats;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    /**
     * 从ResultSet提取店铺信息
     */
    private Map<String, Object> extractShop(ResultSet rs) throws SQLException {
        Map<String, Object> shop = new HashMap<>();
        shop.put("id", rs.getLong("id"));
        shop.put("user_id", rs.getLong("user_id"));
        shop.put("shop_name", rs.getString("shop_name"));
        shop.put("shop_category", rs.getString("shop_category"));
        shop.put("description", rs.getString("description"));
        shop.put("avatar", rs.getString("avatar"));
        shop.put("status", rs.getInt("status"));
        shop.put("rating", rs.getDouble("rating"));
        shop.put("total_orders", rs.getInt("total_orders"));
        shop.put("total_sales", rs.getDouble("total_sales"));
        shop.put("create_time", rs.getTimestamp("create_time"));
        shop.put("update_time", rs.getTimestamp("update_time"));
        return shop;
    }

    public void close() {
        DBUtil.close(conn, ps, rs);
    }
}
