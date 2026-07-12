package com.taobao.service.impl;

import com.taobao.service.StatService;
import com.taobao.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatServiceImpl implements StatService {

    @Override
    public Map<String, Object> getDashboardTotalStat() {
        Map<String, Object> stats = new HashMap<>();
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement()) {

            ResultSet rs1 = st.executeQuery("SELECT COUNT(*) FROM user");
            rs1.next();
            stats.put("totalUsers", rs1.getInt(1));

            ResultSet rs2 = st.executeQuery("SELECT COUNT(*) FROM shop WHERE status = 1");
            rs2.next();
            stats.put("activeShops", rs2.getInt(1));

            ResultSet rs3 = st.executeQuery("SELECT COUNT(*) FROM product WHERE status = 1");
            rs3.next();
            stats.put("activeProducts", rs3.getInt(1));

            ResultSet rs4 = st.executeQuery("SELECT COUNT(*) FROM `order`");
            rs4.next();
            stats.put("totalOrders", rs4.getInt(1));

            ResultSet rs5 = st.executeQuery("SELECT SUM(pay_amount) FROM `order` WHERE status >= 1");
            rs5.next();
            stats.put("totalRevenue", rs5.getDouble(1));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询大盘统计数据失败", e);
        }
        return stats;
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