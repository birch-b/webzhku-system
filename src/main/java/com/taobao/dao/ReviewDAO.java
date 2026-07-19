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
 * 评价数据访问层
 */
public class ReviewDAO {

    /**
     * 商家后台查询店铺所有评价（无分页，无status过滤）
     * 返回字段：id, product_name, buyer_name, rating, content, reply, create_time
     */
    public List<Map<String, Object>> listShopReviews(long shopId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT r.*, p.name AS product_name, u.nickname AS buyer_name " +
                     "FROM review r " +
                     "LEFT JOIN product p ON r.product_id = p.id " +
                     "LEFT JOIN `user` u ON r.user_id = u.id " +
                     "WHERE r.shop_id = ? ORDER BY r.create_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> r = new HashMap<>();
                    r.put("id", rs.getLong("id"));
                    r.put("product_name", rs.getString("product_name"));
                    r.put("buyer_name", rs.getString("buyer_name"));
                    r.put("rating", rs.getInt("rating"));
                    r.put("content", rs.getString("content"));
                    r.put("reply", rs.getString("reply"));
                    r.put("create_time", rs.getTimestamp("create_time"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询店铺评价列表失败", e);
        }
        return list;
    }

    /**
     * 分页查询店铺评价
     */
    public List<Map<String, Object>> getReviewsByShopId(long shopId, int page, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT r.*, p.name as product_name, u.nickname as buyer_nickname " +
                     "FROM review r " +
                     "LEFT JOIN product p ON r.product_id = p.id " +
                     "LEFT JOIN `user` u ON r.user_id = u.id " +
                     "WHERE r.shop_id = ? AND r.status = 1 " +
                     "ORDER BY r.create_time DESC LIMIT ? OFFSET ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            ps.setInt(2, pageSize);
            ps.setInt(3, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractReview(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询店铺评价失败", e);
        }
        return list;
    }

    /**
     * 查询评价总数
     */
    public int getReviewCount(long shopId) {
        String sql = "SELECT COUNT(*) FROM review WHERE shop_id = ? AND status = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询评价总数失败", e);
        }
        return 0;
    }

    /**
     * 根据ID查询评价
     */
    public Map<String, Object> getReviewById(long id) {
        String sql = "SELECT r.*, p.name as product_name, u.nickname as buyer_nickname " +
                     "FROM review r " +
                     "LEFT JOIN product p ON r.product_id = p.id " +
                     "LEFT JOIN `user` u ON r.user_id = u.id " +
                     "WHERE r.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractReview(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询评价详情失败", e);
        }
        return null;
    }

    /**
     * 商家回复评价（需校验 shopId 防止越权）
     */
    public void replyReview(long id, long shopId, String reply) {
        String sql = "UPDATE review SET reply = ?, reply_time = NOW() WHERE id = ? AND shop_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reply);
            ps.setLong(2, id);
            ps.setLong(3, shopId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("回复评价失败", e);
        }
    }

    /**
     * 获取平均评分
     */
    public double getAverageRating(long shopId) {
        String sql = "SELECT AVG(rating) as avg_rating FROM review WHERE shop_id = ? AND status = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取平均评分失败", e);
        }
        return 5.0;
    }

    /**
     * 统计各评分数量
     */
    public Map<String, Integer> getRatingStats(long shopId) {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT rating, COUNT(*) as cnt FROM review WHERE shop_id = ? AND status = 1 GROUP BY rating";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.put("rating_" + rs.getInt("rating"), rs.getInt("cnt"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("统计评分数量失败", e);
        }
        return stats;
    }

    /**
     * 从ResultSet提取评价信息
     */
    private Map<String, Object> extractReview(ResultSet rs) throws SQLException {
        Map<String, Object> r = new HashMap<>();
        r.put("id", rs.getLong("id"));
        r.put("order_id", rs.getLong("order_id"));
        r.put("product_id", rs.getLong("product_id"));
        r.put("product_name", rs.getString("product_name"));
        r.put("user_id", rs.getLong("user_id"));
        r.put("shop_id", rs.getLong("shop_id"));
        r.put("buyer_nickname", rs.getString("buyer_nickname"));
        r.put("rating", rs.getInt("rating"));
        r.put("content", rs.getString("content"));
        r.put("images", rs.getString("images"));
        r.put("reply", rs.getString("reply"));
        r.put("reply_time", rs.getTimestamp("reply_time"));
        r.put("status", rs.getInt("status"));
        r.put("create_time", rs.getTimestamp("create_time"));
        // 解析评价图片
        try {
            String imagesStr = rs.getString("images");
            if (imagesStr != null && !imagesStr.isEmpty()) {
                JSONArray arr = new JSONArray(imagesStr);
                List<String> imageList = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    imageList.add(arr.getString(i));
                }
                r.put("imageList", imageList);
            } else {
                r.put("imageList", new ArrayList<>());
            }
        } catch (Exception e) {
            r.put("imageList", new ArrayList<>());
        }
        return r;
    }

    public void submitReview(Long orderId, Long userId, Long productId, int rating, String content) {
        try (Connection conn = DBUtil.getConnection()) {
            String checkSql = "SELECT id FROM review WHERE order_id = ? AND product_id = ? AND user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setLong(1, orderId);
                ps.setLong(2, productId);
                ps.setLong(3, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new RuntimeException("已评价过该商品");
                    }
                }
            }

            String shopSql = "SELECT shop_id FROM product WHERE id = ?";
            long shopId = 0;
            try (PreparedStatement ps = conn.prepareStatement(shopSql)) {
                ps.setLong(1, productId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        shopId = rs.getLong("shop_id");
                    }
                }
            }

            String insertSql = "INSERT INTO review(user_id, shop_id, product_id, order_id, rating, content, status, create_time) VALUES(?, ?, ?, ?, ?, ?, 1, NOW())";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setLong(1, userId);
                ps.setLong(2, shopId);
                ps.setLong(3, productId);
                ps.setLong(4, orderId);
                ps.setInt(5, rating);
                ps.setString(6, content);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("提交评价失败", e);
        }
    }

    public boolean canReview(Long orderId, Long userId) {
        String sql = "SELECT status FROM `order` WHERE id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.setLong(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
                int status = rs.getInt("status");
                return status == 3 || status == 4;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询订单状态失败", e);
        }
    }

    public boolean hasReviewed(Long orderId, Long productId, Long userId) {
        String sql = "SELECT id FROM review WHERE order_id = ? AND product_id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.setLong(2, productId);
            ps.setLong(3, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询是否已评价失败", e);
        }
    }

    public Map<String, Object> getReviewFormData(Long orderId, Long productId, Long userId) {
        Map<String, Object> data = new HashMap<>();
        if (!canReview(orderId, userId)) {
            throw new RuntimeException("订单状态不允许评价");
        }
        if (hasReviewed(orderId, productId, userId)) {
            throw new RuntimeException("已评价过该商品");
        }
        data.put("orderId", orderId);
        data.put("productId", productId);
        return data;
    }
}
