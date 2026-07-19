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

public class ShopDAO {

    public Map<String, Object> getShopByUserId(long userId) {
        String sql = "SELECT * FROM shop WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractShop(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询店铺信息失败", e);
        }
        return null;
    }

    public Map<String, Object> getShopById(long shopId) {
        String sql = "SELECT * FROM shop WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractShop(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询店铺信息失败", e);
        }
        return null;
    }

    public boolean updateShop(long userId, String shopName, String description, String avatar) {
        StringBuilder sql = new StringBuilder("UPDATE shop SET update_time = NOW()");
        if (shopName != null) sql.append(", shop_name = ?");
        if (description != null) sql.append(", description = ?");
        if (avatar != null) sql.append(", avatar = ?");
        sql.append(" WHERE user_id = ?");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (shopName != null) ps.setString(idx++, shopName);
            if (description != null) ps.setString(idx++, description);
            if (avatar != null) ps.setString(idx++, avatar);
            ps.setLong(idx++, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新店铺信息失败", e);
        }
    }

    public boolean createShop(long userId, String shopName, String category) {
        String sql = "INSERT INTO shop (user_id, shop_name, shop_category) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, shopName);
            ps.setString(3, category);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("创建店铺失败", e);
        }
    }

    public int getShopStatus(long userId) {
        String shopSql = "SELECT status FROM shop WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(shopSql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("status");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询店铺状态失败", e);
        }

        String applySql = "SELECT status FROM shop_apply WHERE user_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(applySql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return -2;
                }
                int status = rs.getInt("status");
                if (status == 0) return 0;
                if (status == 1) return 0;
                if (status == 2) return -2;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询店铺申请状态失败", e);
        }
        return -2;
    }

    public Map<String, Object> getApplyByUserId(long userId) {
        String sql = "SELECT * FROM shop_apply WHERE user_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询店铺申请失败", e);
        }
        return null;
    }

    public boolean submitApply(long userId, String shopName, String category, String description,
                                String contactName, String contactPhone, String contactEmail,
                                String idCard, String licenseNo, String licenseImg) {
        String sql = "INSERT INTO shop_apply (user_id, shop_name, shop_category, description, " +
                     "contact_name, contact_phone, contact_email, id_card, license_no, license_img) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
            throw new RuntimeException("提交店铺申请失败", e);
        }
    }

    public boolean reviewApply(long applyId, int status, String rejectReason) {
        String sql = "UPDATE shop_apply SET status = ?, reject_reason = ?, review_time = NOW() WHERE id = ?";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, status);
                ps.setString(2, rejectReason);
                ps.setLong(3, applyId);
                boolean ok = ps.executeUpdate() > 0;

                if (ok && status == 1) {
                    String selectSql = "SELECT user_id, shop_name, shop_category FROM shop_apply WHERE id = ?";
                    try (PreparedStatement ps2 = conn.prepareStatement(selectSql)) {
                        ps2.setLong(1, applyId);
                        try (ResultSet rs = ps2.executeQuery()) {
                            if (rs.next()) {
                                long userId = rs.getLong("user_id");
                                String shopName = rs.getString("shop_name");
                                String category = rs.getString("shop_category");
                                createShop(conn, userId, shopName, category);
                            }
                        }
                    }
                }
                conn.commit();
                return ok;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("审核店铺申请失败", e);
        }
    }

    private boolean createShop(Connection conn, long userId, String shopName, String category) throws SQLException {
        String sql = "INSERT INTO shop (user_id, shop_name, shop_category) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, shopName);
            ps.setString(3, category);
            return ps.executeUpdate() > 0;
        }
    }

    public Map<String, Object> getShopStats(long shopId) {
        String sql = "SELECT s.total_orders, s.total_sales, s.rating, " +
                     "(SELECT COUNT(*) FROM product WHERE shop_id = ? AND status = 1) as onSaleCount, " +
                     "(SELECT COUNT(*) FROM product WHERE shop_id = ?) as totalProductCount " +
                     "FROM shop s WHERE s.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            ps.setLong(2, shopId);
            ps.setLong(3, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> stats = new HashMap<>();
                    stats.put("totalOrders", rs.getInt("total_orders"));
                    stats.put("totalSales", rs.getDouble("total_sales"));
                    stats.put("rating", rs.getDouble("rating"));
                    stats.put("onSaleCount", rs.getInt("onSaleCount"));
                    stats.put("totalProductCount", rs.getInt("totalProductCount"));
                    return stats;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询店铺统计信息失败", e);
        }
        return null;
    }

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

    /**
     * 商家更新店铺信息（包含 shop_category 字段）
     */
    public boolean updateShopInfo(long userId, String shopName, String shopCategory, String description, String avatar) {
        String sql = "UPDATE shop SET shop_name = ?, shop_category = ?, description = ?, avatar = ?, update_time = NOW() WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, shopName);
            ps.setString(2, shopCategory);
            ps.setString(3, description);
            ps.setString(4, avatar);
            ps.setLong(5, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新店铺信息失败", e);
        }
    }

    /**
     * 查询店铺头像（用于更新时保留旧头像）
     */
    public String getShopAvatarByUserId(long userId) {
        String sql = "SELECT avatar FROM shop WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("avatar");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询店铺头像失败", e);
        }
        return null;
    }

    /**
     * 获取店铺首页KPI数据（实时COUNT，不依赖冗余字段）
     */
    public Map<String, Object> getShopKPIs(long shopId) {
        Map<String, Object> kpis = new HashMap<>();
        kpis.put("totalProducts", 0);
        kpis.put("totalOrders", 0);
        kpis.put("totalReviews", 0);
        kpis.put("pendingAftersales", 0);

        String[] sqls = {
            "SELECT COUNT(*) FROM product WHERE shop_id = ?",
            "SELECT COUNT(*) FROM `order` WHERE shop_id = ?",
            "SELECT COUNT(*) FROM review WHERE shop_id = ?",
            "SELECT COUNT(*) FROM aftersale WHERE shop_id = ? AND status < 3"
        };
        String[] keys = {"totalProducts", "totalOrders", "totalReviews", "pendingAftersales"};

        for (int i = 0; i < sqls.length; i++) {
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sqls[i])) {
                ps.setLong(1, shopId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) kpis.put(keys[i], rs.getInt(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return kpis;
    }

    public List<Map<String, Object>> listPendingShopApply() {
        List<Map<String, Object>> applies = new ArrayList<>();
        String sql = "SELECT sa.*, u.username, u.nickname FROM shop_apply sa " +
                "LEFT JOIN user u ON sa.user_id = u.id " +
                "WHERE sa.status = 0 ORDER BY sa.apply_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getLong("id"));
                m.put("userId", rs.getLong("user_id"));
                m.put("shopName", rs.getString("shop_name"));
                m.put("shopCategory", rs.getString("shop_category"));
                m.put("description", rs.getString("description"));
                m.put("contactName", rs.getString("contact_name"));
                m.put("contactPhone", rs.getString("contact_phone"));
                m.put("contactEmail", rs.getString("contact_email"));
                m.put("licenseNo", rs.getString("license_no"));
                m.put("applyTime", rs.getTimestamp("apply_time"));
                m.put("nickname", rs.getString("nickname"));
                applies.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询待审核商家申请失败", e);
        }
        return applies;
    }

    public List<Map<String, Object>> listAllShop() {
        List<Map<String, Object>> shops = new ArrayList<>();
        String sql = "SELECT s.*, u.nickname AS owner_name FROM shop s " +
                "LEFT JOIN user u ON s.user_id = u.id ORDER BY s.id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getLong("id"));
                m.put("shopName", rs.getString("shop_name"));
                m.put("shopCategory", rs.getString("shop_category"));
                m.put("description", rs.getString("description"));
                m.put("status", rs.getInt("status"));
                m.put("rating", rs.getBigDecimal("rating"));
                m.put("totalOrders", rs.getInt("total_orders"));
                m.put("totalSales", rs.getBigDecimal("total_sales"));
                m.put("ownerName", rs.getString("owner_name"));
                m.put("createTime", rs.getTimestamp("create_time"));
                shops.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询全部店铺列表失败", e);
        }
        return shops;
    }

    public void approveShopApply(Long applyId, Long operatorId) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String selectSql = "SELECT * FROM shop_apply WHERE id = ?";
                try (PreparedStatement ps1 = conn.prepareStatement(selectSql)) {
                    ps1.setLong(1, applyId);
                    try (ResultSet rs = ps1.executeQuery()) {
                        if (rs.next()) {
                            long userId = rs.getLong("user_id");
                            String shopName = rs.getString("shop_name");
                            String category = rs.getString("shop_category");
                            String desc = rs.getString("description");

                            String insertShop = "INSERT INTO shop (user_id, shop_name, shop_category, description, status, rating, total_orders, total_sales, create_time) " +
                                    "VALUES (?, ?, ?, ?, 1, 5.0, 0, 0.00, NOW())";
                            try (PreparedStatement ps2 = conn.prepareStatement(insertShop)) {
                                ps2.setLong(1, userId);
                                ps2.setString(2, shopName);
                                ps2.setString(3, category);
                                ps2.setString(4, desc);
                                ps2.executeUpdate();
                            }

                            String updateApply = "UPDATE shop_apply SET status = 1, review_time = NOW(), reviewer_id = ? WHERE id = ?";
                            try (PreparedStatement ps3 = conn.prepareStatement(updateApply)) {
                                ps3.setLong(1, operatorId);
                                ps3.setLong(2, applyId);
                                ps3.executeUpdate();
                            }

                            String updateRole = "UPDATE user SET role = 'shopkeeper' WHERE id = ?";
                            try (PreparedStatement ps4 = conn.prepareStatement(updateRole)) {
                                ps4.setLong(1, userId);
                                ps4.executeUpdate();
                            }
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("审核通过店铺申请失败", e);
        }
    }

    public void rejectShopApply(Long applyId, String rejectReason, Long operatorId) {
        String sql = "UPDATE shop_apply SET status = 2, reject_reason = ?, review_time = NOW(), reviewer_id = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rejectReason);
            ps.setLong(2, operatorId);
            ps.setLong(3, applyId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("驳回店铺申请失败", e);
        }
    }

    public void closeShop(Long shopId) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String updateShop = "UPDATE shop SET status = -1 WHERE id = ?";
                try (PreparedStatement ps1 = conn.prepareStatement(updateShop)) {
                    ps1.setLong(1, shopId);
                    ps1.executeUpdate();
                }

                String updateProduct = "UPDATE product SET status = 2 WHERE shop_id = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(updateProduct)) {
                    ps2.setLong(1, shopId);
                    ps2.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("关闭店铺失败", e);
        }
    }

    public String getShopNameByOwnerId(Long ownerId) {
        String sql = "SELECT shop_name FROM shop WHERE user_id = ? AND status = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("shop_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询店铺名称失败", e);
        }
        return "";
    }

    public boolean hasShop(Long userId) {
        String sql = "SELECT id FROM shop WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询店铺失败", e);
        }
    }

    public boolean hasPendingApply(Long userId) {
        String sql = "SELECT id FROM shop_apply WHERE user_id = ? AND status = 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询申请状态失败", e);
        }
    }

    public void submitShopApply(Long userId, String shopName, String shopCategory, String description,
                                  String contactName, String contactPhone) {
        String sql = "INSERT INTO shop_apply (user_id, shop_name, shop_category, description, contact_name, contact_phone, status) VALUES (?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, shopName);
            ps.setString(3, shopCategory);
            ps.setString(4, description);
            ps.setString(5, contactName);
            ps.setString(6, contactPhone);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("提交店铺申请失败", e);
        }
    }
}
