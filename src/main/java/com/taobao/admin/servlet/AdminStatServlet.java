package com.taobao.admin.servlet;

import com.taobao.util.DBUtil;
import com.taobao.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/admin/stat/*")
public class AdminStatServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/dashboard";
        switch (pathInfo) {
            case "/dashboard": showDashboard(req, resp); break;
            case "/salesRank": salesRank(resp); break;
            case "/shopRevenue": shopRevenue(resp); break;
            case "/monthlyOrders": monthlyOrders(resp); break;
            case "/userGrowth": userGrowth(resp); break;
            default: showDashboard(req, resp);
        }
    }

    private void showDashboard(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection conn = DBUtil.getConnection()) {
            Map<String, Object> stats = new HashMap<>();
            Statement st = conn.createStatement();
            ResultSet rs1 = st.executeQuery("SELECT COUNT(*) FROM user");
            rs1.next(); stats.put("totalUsers", rs1.getInt(1));
            ResultSet rs2 = st.executeQuery("SELECT COUNT(*) FROM shop WHERE status = 1");
            rs2.next(); stats.put("activeShops", rs2.getInt(1));
            ResultSet rs3 = st.executeQuery("SELECT COUNT(*) FROM product WHERE status = 1");
            rs3.next(); stats.put("activeProducts", rs3.getInt(1));
            ResultSet rs4 = st.executeQuery("SELECT COUNT(*) FROM `order`");
            rs4.next(); stats.put("totalOrders", rs4.getInt(1));
            ResultSet rs5 = st.executeQuery("SELECT SUM(pay_amount) FROM `order` WHERE status >= 1");
            rs5.next(); stats.put("totalRevenue", rs5.getDouble(1));
            req.setAttribute("stats", stats);
            req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace(); req.setAttribute("error", "加载统计数据失败");
            req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);
        }
    }

    private void salesRank(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT p.name, p.sales FROM product WHERE status = 1 ORDER BY sales DESC LIMIT 10");
            ResultSet rs = ps.executeQuery();
            List<String> names = new ArrayList<>(); List<Integer> values = new ArrayList<>();
            while (rs.next()) { names.add(rs.getString("name")); values.add(rs.getInt("sales")); }
            Map<String, Object> data = new HashMap<>(); data.put("names", names); data.put("values", values);
            resp.getWriter().write(JsonUtil.toJson(data));
        } catch (Exception e) { e.printStackTrace(); resp.getWriter().write("{\"error\":\"查询失败\"}"); }
    }

    private void shopRevenue(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT s.shop_name, COALESCE(SUM(o.pay_amount),0) AS revenue FROM shop s LEFT JOIN `order` o ON s.id = o.shop_id AND o.status >= 1 GROUP BY s.id ORDER BY revenue DESC LIMIT 10");
            ResultSet rs = ps.executeQuery();
            List<String> names = new ArrayList<>(); List<Double> values = new ArrayList<>();
            while (rs.next()) { names.add(rs.getString("shop_name")); values.add(rs.getDouble("revenue")); }
            Map<String, Object> data = new HashMap<>(); data.put("names", names); data.put("values", values);
            resp.getWriter().write(JsonUtil.toJson(data));
        } catch (Exception e) { e.printStackTrace(); resp.getWriter().write("{\"error\":\"查询失败\"}"); }
    }

    private void monthlyOrders(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT DATE_FORMAT(create_time,'%Y-%m') AS month, COUNT(*) AS count FROM `order` GROUP BY month ORDER BY month DESC LIMIT 12");
            ResultSet rs = ps.executeQuery();
            List<String> months = new ArrayList<>(); List<Integer> counts = new ArrayList<>();
            while (rs.next()) { months.add(rs.getString("month")); counts.add(rs.getInt("count")); }
            Map<String, Object> data = new HashMap<>(); data.put("months", months); data.put("counts", counts);
            resp.getWriter().write(JsonUtil.toJson(data));
        } catch (Exception e) { e.printStackTrace(); resp.getWriter().write("{\"error\":\"查询失败\"}"); }
    }

    private void userGrowth(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT DATE_FORMAT(create_time,'%Y-%m') AS month, COUNT(*) AS count FROM user GROUP BY month ORDER BY month DESC LIMIT 12");
            ResultSet rs = ps.executeQuery();
            List<String> months = new ArrayList<>(); List<Integer> counts = new ArrayList<>();
            while (rs.next()) { months.add(rs.getString("month")); counts.add(rs.getInt("count")); }
            Map<String, Object> data = new HashMap<>(); data.put("months", months); data.put("counts", counts);
            resp.getWriter().write(JsonUtil.toJson(data));
        } catch (Exception e) { e.printStackTrace(); resp.getWriter().write("{\"error\":\"查询失败\"}"); }
    }
}
