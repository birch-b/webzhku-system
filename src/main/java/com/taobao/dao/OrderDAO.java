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
 * 商家订单数据访问层
 */
public class OrderDAO {

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    /**
     * 分页查询店铺订单（按状态筛选）
     */
    public List<Map<String, Object>> getOrdersByShopId(long shopId, Integer status, int page, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT o.*, u.nickname as buyer_nickname ");
        sql.append("FROM `order` o LEFT JOIN `user` u ON o.user_id = u.id ");
        sql.append("WHERE o.shop_id = ? ");
        if (status != null) sql.append("AND o.status = ? ");
        sql.append("ORDER BY o.create_time DESC LIMIT ? OFFSET ?");

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            ps.setLong(idx++, shopId);
            if (status != null) ps.setInt(idx++, status);
            ps.setInt(idx++, pageSize);
            ps.setInt(idx++, (page - 1) * pageSize);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> order = extractOrder(rs);
                // 查询订单商品明细
                order.put("items", getOrderItems(rs.getLong("id")));
                list.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 查询订单总数
     */
    public int getOrderCount(long shopId, Integer status) {
        String sql = status != null
            ? "SELECT COUNT(*) FROM `order` WHERE shop_id = ? AND status = ?"
            : "SELECT COUNT(*) FROM `order` WHERE shop_id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, shopId);
            if (status != null) ps.setInt(2, status);
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
     * 根据订单ID查询订单
     */
    public Map<String, Object> getOrderById(long orderId) {
        String sql = "SELECT o.*, u.nickname as buyer_nickname, u.phone as buyer_phone " +
                     "FROM `order` o LEFT JOIN `user` u ON o.user_id = u.id WHERE o.id = ?";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> order = extractOrder(rs);
                order.put("items", getOrderItems(orderId));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    /**
     * 查询订单商品明细
     */
    private List<Map<String, Object>> getOrderItems(long orderId) {
        List<Map<String, Object>> items = new ArrayList<>();
        String sql = "SELECT * FROM order_item WHERE order_id = ?";
        try {
            PreparedStatement ps2 = conn.prepareStatement(sql);
            ps2.setLong(1, orderId);
            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", rs2.getLong("id"));
                item.put("order_id", rs2.getLong("order_id"));
                item.put("product_id", rs2.getLong("product_id"));
                item.put("product_name", rs2.getString("product_name"));
                item.put("cover_image", rs2.getString("cover_image"));
                item.put("price", rs2.getDouble("price"));
                item.put("quantity", rs2.getInt("quantity"));
                item.put("subtotal", rs2.getDouble("subtotal"));
                items.add(item);
            }
            rs2.close();
            ps2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * 发货（更新订单状态 + 创建物流记录）
     */
    public boolean shipOrder(long orderId) {
        String sql = "UPDATE `order` SET status = 2, ship_time = NOW() WHERE id = ? AND status = 1";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 确认收货
     */
    public boolean confirmReceive(long orderId) {
        String sql = "UPDATE `order` SET status = 3, receive_time = NOW() WHERE id = ? AND status = 2";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 完成订单
     */
    public boolean finishOrder(long orderId) {
        String sql = "UPDATE `order` SET status = 4, finish_time = NOW() WHERE id = ? AND status = 3";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 获取各状态订单数量（统计）
     */
    public Map<String, Integer> getOrderStats(long shopId) {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as cnt FROM `order` WHERE shop_id = ? GROUP BY status";
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, shopId);
            rs = ps.executeQuery();
            while (rs.next()) {
                stats.put("status_" + rs.getInt("status"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return stats;
    }

    /**
     * 从ResultSet提取订单信息
     */
    private Map<String, Object> extractOrder(ResultSet rs) throws SQLException {
        Map<String, Object> order = new HashMap<>();
        order.put("id", rs.getLong("id"));
        order.put("order_no", rs.getString("order_no"));
        order.put("user_id", rs.getLong("user_id"));
        order.put("shop_id", rs.getLong("shop_id"));
        order.put("buyer_nickname", rs.getString("buyer_nickname"));
        order.put("total_amount", rs.getDouble("total_amount"));
        order.put("discount_amount", rs.getDouble("discount_amount"));
        order.put("pay_amount", rs.getDouble("pay_amount"));
        order.put("pay_method", rs.getObject("pay_method"));
        order.put("status", rs.getInt("status"));
        order.put("receiver_name", rs.getString("receiver_name"));
        order.put("receiver_phone", rs.getString("receiver_phone"));
        order.put("receiver_address", rs.getString("receiver_address"));
        order.put("buyer_message", rs.getString("buyer_message"));
        order.put("create_time", rs.getTimestamp("create_time"));
        order.put("pay_time", rs.getTimestamp("pay_time"));
        order.put("ship_time", rs.getTimestamp("ship_time"));
        order.put("receive_time", rs.getTimestamp("receive_time"));
        order.put("finish_time", rs.getTimestamp("finish_time"));
        return order;
    }

    public void close() {
        DBUtil.close(conn, ps, rs);
    }
}
