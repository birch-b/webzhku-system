package com.taobao.service.impl;

import com.taobao.service.OrderService;
import com.taobao.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderServiceImpl implements OrderService {

    @Override
    public String getOrderStatusText(int s) {
        switch (s) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "已发货";
            case 3: return "已收货";
            case 4: return "已完成";
            case 5: return "已取消";
            case 6: return "退款中";
            case 7: return "已退款";
            default: return "未知";
        }
    }

    @Override
    public String getLogisticsStatusText(int ls) {
        return ls == 0 ? "未发货" : ls == 1 ? "运输中" : ls == 2 ? "派送中" : ls == 3 ? "已签收" : "未知";
    }

    @Override
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
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getLong("id"));
                m.put("orderNo", rs.getString("order_no"));
                m.put("buyerName", rs.getString("buyer_name"));
                m.put("shopName", rs.getString("shop_name"));
                m.put("totalAmount", rs.getBigDecimal("total_amount"));
                m.put("payAmount", rs.getBigDecimal("pay_amount"));
                int st = rs.getInt("status");
                m.put("status", st);
                m.put("statusText", getOrderStatusText(st));
                m.put("createTime", rs.getTimestamp("create_time"));
                orders.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询订单列表失败", e);
        }
        return orders;
    }

    @Override
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
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getLong("id"));
                m.put("orderNo", rs.getString("order_no"));
                m.put("buyerName", rs.getString("buyer_name"));
                m.put("shopName", rs.getString("shop_name"));
                m.put("totalAmount", rs.getBigDecimal("total_amount"));
                m.put("payAmount", rs.getBigDecimal("pay_amount"));
                int st = rs.getInt("status");
                m.put("status", st);
                m.put("statusText", getOrderStatusText(st));
                m.put("createTime", rs.getTimestamp("create_time"));
                orders.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询异常订单失败", e);
        }
        return orders;
    }

    @Override
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
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                o = new HashMap<>();
                o.put("id", rs.getLong("id"));
                o.put("orderNo", rs.getString("order_no"));
                o.put("buyerName", rs.getString("buyer_name"));
                o.put("buyerPhone", rs.getString("buyer_phone"));
                o.put("shopName", rs.getString("shop_name"));
                o.put("totalAmount", rs.getBigDecimal("total_amount"));
                o.put("payAmount", rs.getBigDecimal("pay_amount"));
                int st = rs.getInt("status");
                o.put("status", st);
                o.put("statusText", getOrderStatusText(st));
                o.put("receiverName", rs.getString("receiver_name"));
                o.put("receiverPhone", rs.getString("receiver_phone"));
                o.put("receiverAddress", rs.getString("receiver_address"));
                o.put("buyerMessage", rs.getString("buyer_message"));
                o.put("createTime", rs.getTimestamp("create_time"));
                o.put("payTime", rs.getTimestamp("pay_time"));
                o.put("shipTime", rs.getTimestamp("ship_time"));
                o.put("logisticsCompany", rs.getString("company"));
                o.put("trackingNo", rs.getString("tracking_no"));
                int ls = rs.getInt("logistics_status");
                o.put("logisticsStatus", ls);
                o.put("logisticsStatusText", getLogisticsStatusText(ls));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询订单详情失败", e);
        }
        return o;
    }

    @Override
    public List<Map<String, Object>> listOrderItemByOrderId(Long orderId) {
        List<Map<String, Object>> items = new ArrayList<>();
        String sql = "SELECT * FROM order_item WHERE order_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> it = new HashMap<>();
                it.put("productName", rs.getString("product_name"));
                it.put("coverImage", rs.getString("cover_image"));
                it.put("price", rs.getBigDecimal("price"));
                it.put("quantity", rs.getInt("quantity"));
                it.put("subtotal", rs.getBigDecimal("subtotal"));
                items.add(it);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询订单项失败", e);
        }
        return items;
    }
}