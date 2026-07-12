package com.taobao.service.impl;

import com.taobao.service.AftersaleService;
import com.taobao.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AftersaleServiceImpl implements AftersaleService {

    @Override
    public List<Map<String, Object>> listUserAftersale(Long userId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT a.*, o.order_no, o.shop_id, s.shop_name " +
                "FROM aftersale a " +
                "LEFT JOIN `order` o ON a.order_id = o.id " +
                "LEFT JOIN shop s ON o.shop_id = s.id " +
                "WHERE a.user_id = ? " +
                "ORDER BY a.create_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> aftersale = new HashMap<>();
                aftersale.put("id", rs.getLong("id"));
                aftersale.put("orderId", rs.getLong("order_id"));
                aftersale.put("orderNo", rs.getString("order_no"));
                aftersale.put("shopName", rs.getString("shop_name"));
                aftersale.put("type", rs.getInt("type"));
                aftersale.put("reason", rs.getString("reason"));
                aftersale.put("amount", rs.getBigDecimal("amount"));
                aftersale.put("status", rs.getInt("status"));
                aftersale.put("shopReply", rs.getString("shop_reply"));
                aftersale.put("createTime", rs.getTimestamp("create_time"));
                aftersale.put("handleTime", rs.getTimestamp("handle_time"));
                list.add(aftersale);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("加载售后列表失败", e);
        }
        return list;
    }

    @Override
    public Map<String, Object> getOrderForAftersale(Long orderId, Long userId) {
        Map<String, Object> data = null;
        String sql = "SELECT o.*, s.shop_name FROM `order` o " +
                "LEFT JOIN shop s ON o.shop_id = s.id " +
                "WHERE o.id = ? AND o.user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.setLong(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int status = rs.getInt("status");
                // 校验：仅3已收货 /4已完成 可售后
                if (status < 3 || status == 5 || status == 7) {
                    throw new RuntimeException("该订单不支持售后申请");
                }
                data = new HashMap<>();
                data.put("orderId", orderId);
                data.put("orderNo", rs.getString("order_no"));
                data.put("shopName", rs.getString("shop_name"));
                data.put("payAmount", rs.getBigDecimal("pay_amount"));
            } else {
                throw new RuntimeException("订单不存在");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询售后订单信息失败", e);
        }
        return data;
    }

    @Override
    public void submitAftersale(Long orderId, Long userId, int type, String reason, BigDecimal amount) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 插入售后单
                String insertSql = "INSERT INTO aftersale (order_id, user_id, type, reason, amount, status, create_time) " +
                        "VALUES (?, ?, ?, ?, ?, 0, NOW())";
                PreparedStatement ps = conn.prepareStatement(insertSql);
                ps.setLong(1, orderId);
                ps.setLong(2, userId);
                ps.setInt(3, type);
                ps.setString(4, reason);
                ps.setBigDecimal(5, amount);
                ps.executeUpdate();

                // 修改订单状态为退款中6
                String updateOrderSql = "UPDATE `order` SET status = 6 WHERE id = ?";
                PreparedStatement ps2 = conn.prepareStatement(updateOrderSql);
                ps2.setLong(1, orderId);
                ps2.executeUpdate();

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("提交售后申请失败", e);
        }
    }

    @Override
    public Map<String, Object> getAftersaleDetail(Long aftersaleId, Long userId) {
        Map<String, Object> aftersale = null;
        String sql = "SELECT a.*, o.order_no, o.shop_id, s.shop_name " +
                "FROM aftersale a " +
                "LEFT JOIN `order` o ON a.order_id = o.id " +
                "LEFT JOIN shop s ON o.shop_id = s.id " +
                "WHERE a.id = ? AND a.user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, aftersaleId);
            ps.setLong(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                aftersale = new HashMap<>();
                aftersale.put("id", rs.getLong("id"));
                aftersale.put("orderId", rs.getLong("order_id"));
                aftersale.put("orderNo", rs.getString("order_no"));
                aftersale.put("shopName", rs.getString("shop_name"));
                aftersale.put("type", rs.getInt("type"));
                aftersale.put("reason", rs.getString("reason"));
                aftersale.put("description", rs.getString("description"));
                aftersale.put("amount", rs.getBigDecimal("amount"));
                aftersale.put("status", rs.getInt("status"));
                aftersale.put("shopReply", rs.getString("shop_reply"));
                aftersale.put("createTime", rs.getTimestamp("create_time"));
                aftersale.put("handleTime", rs.getTimestamp("handle_time"));
            } else {
                throw new RuntimeException("售后记录不存在");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("加载售后详情失败", e);
        }
        return aftersale;
    }
}