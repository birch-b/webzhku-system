package com.taobao.dao.impl;

import com.taobao.dao.StatDAO;
import com.taobao.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatDAOImpl implements StatDAO {
    @Override
    public int getTotalUsers() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM user");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询用户总数失败", e);
        }
    }

    @Override
    public int getActiveShops() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM shop WHERE status = 1");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询活跃店铺数失败", e);
        }
    }

    @Override
    public int getActiveProducts() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM product WHERE status = 1");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询活跃商品数失败", e);
        }
    }

    @Override
    public int getTotalOrders() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM `order`");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询订单总数失败", e);
        }
    }

    @Override
    public double getTotalRevenue() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT SUM(pay_amount) FROM `order` WHERE status >= 1");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询总收入失败", e);
        }
    }

    @Override
    public Map<String, Object> getProductSalesTop10() {
        List<String> names = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        String sql = "SELECT p.name, p.sales FROM product WHERE status = 1 ORDER BY sales DESC LIMIT 10";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                names.add(rs.getString("name"));
                values.add(rs.getInt("sales"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询商品销量排行失败", e);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("names", names);
        data.put("values", values);
        return data;
    }

    @Override
    public Map<String, Object> getShopRevenueTop10() {
        List<String> names = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        String sql = "SELECT s.shop_name, COALESCE(SUM(o.pay_amount),0) AS revenue " +
                "FROM shop s LEFT JOIN `order` o ON s.id = o.shop_id AND o.status >= 1 " +
                "GROUP BY s.id ORDER BY revenue DESC LIMIT 10";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                names.add(rs.getString("shop_name"));
                values.add(rs.getDouble("revenue"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询店铺营收排行失败", e);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("names", names);
        data.put("values", values);
        return data;
    }

    @Override
    public Map<String, Object> getLast12MonthOrderCount() {
        List<String> months = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(create_time,'%Y-%m') AS month, COUNT(*) AS count " +
                "FROM `order` GROUP BY month ORDER BY month DESC LIMIT 12";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                months.add(rs.getString("month"));
                counts.add(rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询月度订单统计失败", e);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("months", months);
        data.put("counts", counts);
        return data;
    }

    @Override
    public Map<String, Object> getLast12MonthUserGrowth() {
        List<String> months = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(create_time,'%Y-%m') AS month, COUNT(*) AS count " +
                "FROM user GROUP BY month ORDER BY month DESC LIMIT 12";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                months.add(rs.getString("month"));
                counts.add(rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询用户增长统计失败", e);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("months", months);
        data.put("counts", counts);
        return data;
    }
}