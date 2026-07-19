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

public class OrderDAO {

    public List<Map<String, Object>> getOrdersByShopId(long shopId, Integer status, int page, int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT o.*, u.nickname as buyer_nickname ");
        sql.append("FROM `order` o LEFT JOIN `user` u ON o.user_id = u.id ");
        sql.append("WHERE o.shop_id = ? ");
        if (status != null) sql.append("AND o.status = ? ");
        sql.append("ORDER BY o.create_time DESC LIMIT ? OFFSET ?");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setLong(idx++, shopId);
            if (status != null) ps.setInt(idx++, status);
            ps.setInt(idx++, pageSize);
            ps.setInt(idx++, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> order = extractOrder(rs);
                    order.put("items", getOrderItems(conn, rs.getLong("id")));
                    list.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getOrderCount(long shopId, Integer status) {
        String sql = status != null
            ? "SELECT COUNT(*) FROM `order` WHERE shop_id = ? AND status = ?"
            : "SELECT COUNT(*) FROM `order` WHERE shop_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            if (status != null) ps.setInt(2, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String, Object> getOrderById(long orderId) {
        String sql = "SELECT o.*, u.nickname as buyer_nickname, u.phone as buyer_phone " +
                     "FROM `order` o LEFT JOIN `user` u ON o.user_id = u.id WHERE o.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> order = extractOrder(rs);
                    order.put("items", getOrderItems(conn, orderId));
                    return order;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Map<String, Object>> getOrderItems(Connection conn, long orderId) throws SQLException {
        List<Map<String, Object>> items = new ArrayList<>();
        String sql = "SELECT * FROM order_item WHERE order_id = ?";
        try (PreparedStatement ps2 = conn.prepareStatement(sql)) {
            ps2.setLong(1, orderId);
            try (ResultSet rs2 = ps2.executeQuery()) {
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
            }
        }
        return items;
    }

    public boolean shipOrder(long orderId) {
        String sql = "UPDATE `order` SET status = 2, ship_time = NOW() WHERE id = ? AND status = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean confirmReceive(long orderId) {
        String sql = "UPDATE `order` SET status = 3, receive_time = NOW() WHERE id = ? AND status = 2";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean finishOrder(long orderId) {
        String sql = "UPDATE `order` SET status = 4, finish_time = NOW() WHERE id = ? AND status = 3";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Integer> getOrderStats(long shopId) {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as cnt FROM `order` WHERE shop_id = ? GROUP BY status";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.put("status_" + rs.getInt("status"), rs.getInt("cnt"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

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

    public List<Map<String, Object>> listAllOrder(String status, String keyword, int page) {
        List<Map<String, Object>> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.nickname AS buyer_name, s.shop_name FROM `order` o " +
                "LEFT JOIN user u ON o.user_id = u.id " +
                "LEFT JOIN shop s ON o.shop_id = s.id WHERE 1=1";
        List<Object> params = new ArrayList<>();
        if (status != null && !status.isEmpty()) {
            sql += " AND o.status = ?";
            params.add(Integer.parseInt(status));
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = "%" + keyword.trim() + "%";
            sql += " AND (o.order_no LIKE ? OR u.nickname LIKE ?)";
            params.add(kw);
            params.add(kw);
        }
        sql += " ORDER BY o.create_time DESC LIMIT ?, 20";
        params.add((page - 1) * 20);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", rs.getLong("id"));
                    m.put("orderNo", rs.getString("order_no"));
                    m.put("buyerName", rs.getString("buyer_name"));
                    m.put("shopName", rs.getString("shop_name"));
                    m.put("totalAmount", rs.getBigDecimal("total_amount"));
                    m.put("payAmount", rs.getBigDecimal("pay_amount"));
                    m.put("status", rs.getInt("status"));
                    m.put("createTime", rs.getTimestamp("create_time"));
                    orders.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询订单列表失败", e);
        }
        return orders;
    }

    public List<Map<String, Object>> listAbnormalOrder(String keyword, int page) {
        List<Map<String, Object>> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.nickname AS buyer_name, s.shop_name FROM `order` o " +
                "LEFT JOIN user u ON o.user_id = u.id " +
                "LEFT JOIN shop s ON o.shop_id = s.id WHERE o.status IN (5,6,7)";
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = "%" + keyword.trim() + "%";
            sql += " AND (o.order_no LIKE ? OR u.nickname LIKE ?)";
            params.add(kw);
            params.add(kw);
        }
        sql += " ORDER BY o.create_time DESC LIMIT ?, 20";
        params.add((page - 1) * 20);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", rs.getLong("id"));
                    m.put("orderNo", rs.getString("order_no"));
                    m.put("buyerName", rs.getString("buyer_name"));
                    m.put("shopName", rs.getString("shop_name"));
                    m.put("totalAmount", rs.getBigDecimal("total_amount"));
                    m.put("payAmount", rs.getBigDecimal("pay_amount"));
                    m.put("status", rs.getInt("status"));
                    m.put("createTime", rs.getTimestamp("create_time"));
                    orders.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询异常订单失败", e);
        }
        return orders;
    }

    public Map<String, Object> getOrderDetailById(Long orderId) {
        Map<String, Object> o = null;
        String sql = "SELECT o.*, u.nickname AS buyer_name, u.phone AS buyer_phone, " +
                "s.shop_name, l.company, l.tracking_no, l.status AS logistics_status " +
                "FROM `order` o LEFT JOIN user u ON o.user_id = u.id " +
                "LEFT JOIN shop s ON o.shop_id = s.id " +
                "LEFT JOIN logistics l ON o.id = l.order_id WHERE o.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    o = new HashMap<>();
                    o.put("id", rs.getLong("id"));
                    o.put("orderNo", rs.getString("order_no"));
                    o.put("buyerName", rs.getString("buyer_name"));
                    o.put("buyerPhone", rs.getString("buyer_phone"));
                    o.put("shopName", rs.getString("shop_name"));
                    o.put("totalAmount", rs.getBigDecimal("total_amount"));
                    o.put("payAmount", rs.getBigDecimal("pay_amount"));
                    o.put("status", rs.getInt("status"));
                    o.put("receiverName", rs.getString("receiver_name"));
                    o.put("receiverPhone", rs.getString("receiver_phone"));
                    o.put("receiverAddress", rs.getString("receiver_address"));
                    o.put("buyerMessage", rs.getString("buyer_message"));
                    o.put("createTime", rs.getTimestamp("create_time"));
                    o.put("payTime", rs.getTimestamp("pay_time"));
                    o.put("shipTime", rs.getTimestamp("ship_time"));
                    o.put("logisticsCompany", rs.getString("company"));
                    o.put("trackingNo", rs.getString("tracking_no"));
                    o.put("logisticsStatus", rs.getInt("logistics_status"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询订单详情失败", e);
        }
        return o;
    }

    public List<Map<String, Object>> listOrderItemByOrderId(Long orderId) {
        List<Map<String, Object>> items = new ArrayList<>();
        String sql = "SELECT * FROM order_item WHERE order_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> it = new HashMap<>();
                    it.put("productName", rs.getString("product_name"));
                    it.put("coverImage", rs.getString("cover_image"));
                    it.put("price", rs.getBigDecimal("price"));
                    it.put("quantity", rs.getInt("quantity"));
                    it.put("subtotal", rs.getBigDecimal("subtotal"));
                    items.add(it);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询订单项失败", e);
        }
        return items;
    }
}
