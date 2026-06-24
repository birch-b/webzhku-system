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

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

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
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, shopId);
            ps.setInt(2, pageSize);
            ps.setInt(3, (page - 1) * pageSize);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractReview(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 查询评价总数
     */
    public int getReviewCount(long shopId) {
        String sql = "SELECT COUNT(*) FROM review WHERE shop_id = ? AND status = 1";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, shopId);
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
     * 根据ID查询评价
     */
    public Map<String, Object> getReviewById(long id) {
        String sql = "SELECT r.*, p.name as product_name, u.nickname as buyer_nickname " +
                     "FROM review r " +
                     "LEFT JOIN product p ON r.product_id = p.id " +
                     "LEFT JOIN `user` u ON r.user_id = u.id " +
                     "WHERE r.id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractReview(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    /**
     * 商家回复评价
     */
    public boolean replyReview(long id, String reply) {
        String sql = "UPDATE review SET reply = ?, reply_time = NOW() WHERE id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, reply);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 获取平均评分
     */
    public double getAverageRating(long shopId) {
        String sql = "SELECT AVG(rating) as avg_rating FROM review WHERE shop_id = ? AND status = 1";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, shopId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return 5.0;
    }

    /**
     * 统计各评分数量
     */
    public Map<String, Integer> getRatingStats(long shopId) {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT rating, COUNT(*) as cnt FROM review WHERE shop_id = ? AND status = 1 GROUP BY rating";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, shopId);
            rs = ps.executeQuery();
            while (rs.next()) {
                stats.put("rating_" + rs.getInt("rating"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
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

    public void close() {
        DBUtil.close(conn, ps, rs);
    }
}
