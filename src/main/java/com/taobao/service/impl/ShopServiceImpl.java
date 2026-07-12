package com.taobao.service.impl;

import com.taobao.service.ShopService;
import com.taobao.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopServiceImpl implements ShopService {

    @Override
    public String getShopStatusText(int st) {
        return st == 1 ? "营业中" : st == 0 ? "休息中" : "已关闭";
    }

    @Override
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

    @Override
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
                int st = rs.getInt("status");
                m.put("status", st);
                m.put("statusText", getShopStatusText(st));
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

    @Override
    public void approveShopApply(Long applyId, Long operatorId) {
        try (Connection conn = DBUtil.getConnection()) {
            // 1. 查询申请单信息
            PreparedStatement ps1 = conn.prepareStatement("SELECT * FROM shop_apply WHERE id = ?");
            ps1.setLong(1, applyId);
            ResultSet rs = ps1.executeQuery();
            if (rs.next()) {
                long userId = rs.getLong("user_id");
                String shopName = rs.getString("shop_name");
                String category = rs.getString("shop_category");
                String desc = rs.getString("description");

                // 2. 插入店铺记录
                String insertShop = "INSERT INTO shop (user_id, shop_name, shop_category, description, status, rating, total_orders, total_sales, create_time) " +
                        "VALUES (?, ?, ?, ?, 1, 5.0, 0, 0.00, NOW())";
                PreparedStatement ps2 = conn.prepareStatement(insertShop);
                ps2.setLong(1, userId);
                ps2.setString(2, shopName);
                ps2.setString(3, category);
                ps2.setString(4, desc);
                ps2.executeUpdate();

                // 3. 更新申请单为已通过
                String updateApply = "UPDATE shop_apply SET status = 1, review_time = NOW(), reviewer_id = ? WHERE id = ?";
                PreparedStatement ps3 = conn.prepareStatement(updateApply);
                ps3.setLong(1, operatorId);
                ps3.setLong(2, applyId);
                ps3.executeUpdate();

                // 4. 修改用户角色为商家 shopkeeper
                PreparedStatement ps4 = conn.prepareStatement("UPDATE user SET role = 'shopkeeper' WHERE id = ?");
                ps4.setLong(1, userId);
                ps4.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("审核通过店铺申请失败", e);
        }
    }

    @Override
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

    @Override
    public void closeShop(Long shopId) {
        try (Connection conn = DBUtil.getConnection()) {
            // 店铺状态改为 -1 关闭
            PreparedStatement ps1 = conn.prepareStatement("UPDATE shop SET status = -1 WHERE id = ?");
            ps1.setLong(1, shopId);
            ps1.executeUpdate();
            // 店铺所有商品下架 status=2
            PreparedStatement ps2 = conn.prepareStatement("UPDATE product SET status = 2 WHERE shop_id = ?");
            ps2.setLong(1, shopId);
            ps2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("关闭店铺失败", e);
        }
    }

    @Override
    public String getShopNameByOwnerId(Long ownerId) {
        String sql = "SELECT shop_name FROM shop WHERE user_id = ? AND status = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, ownerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("shop_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询店铺名称失败", e);
        }
        return "";
    }
}