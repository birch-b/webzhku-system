package com.taobao.admin.servlet;

import com.taobao.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/shop/*")
public class AdminShopAuditServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/auditList";
        switch (pathInfo) {
            case "/auditList": listPending(req, resp); break;
            case "/allShops": listAll(req, resp); break;
            default: listPending(req, resp); break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        switch (pathInfo != null ? pathInfo : "") {
            case "/approve": approve(req, resp); break;
            case "/reject": reject(req, resp); break;
            case "/close": closeShop(req, resp); break;
            default: resp.sendError(404); break;
        }
    }

    private void listPending(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT sa.*, u.username, u.nickname FROM shop_apply sa " +
                "LEFT JOIN user u ON sa.user_id = u.id " +
                "WHERE sa.status = 0 ORDER BY sa.apply_time DESC");
            ResultSet rs = ps.executeQuery();
            List<Map<String, Object>> applies = new ArrayList<>();
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
            req.setAttribute("applies", applies);
            req.setAttribute("msg", req.getParameter("msg"));
            req.getRequestDispatcher("/admin/shop_audit.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace(); req.setAttribute("error", "查询审核列表失败");
            req.getRequestDispatcher("/admin/shop_audit.jsp").forward(req, resp);
        }
    }

    private void listAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT s.*, u.nickname AS owner_name FROM shop s " +
                "LEFT JOIN user u ON s.user_id = u.id ORDER BY s.id DESC");
            ResultSet rs = ps.executeQuery();
            List<Map<String, Object>> shops = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getLong("id"));
                m.put("shopName", rs.getString("shop_name"));
                m.put("shopCategory", rs.getString("shop_category"));
                m.put("description", rs.getString("description"));
                m.put("status", rs.getInt("status"));
                int st = rs.getInt("status");
                m.put("statusText", st == 1 ? "营业中" : st == 0 ? "休息中" : "已关闭");
                m.put("rating", rs.getBigDecimal("rating"));
                m.put("totalOrders", rs.getInt("total_orders"));
                m.put("totalSales", rs.getBigDecimal("total_sales"));
                m.put("ownerName", rs.getString("owner_name"));
                m.put("createTime", rs.getTimestamp("create_time"));
                shops.add(m);
            }
            req.setAttribute("shops", shops);
            req.setAttribute("msg", req.getParameter("msg"));
            req.getRequestDispatcher("/admin/shop_audit.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void approve(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long applyId = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps1 = conn.prepareStatement("SELECT * FROM shop_apply WHERE id = ?");
            ps1.setLong(1, applyId);
            ResultSet rs = ps1.executeQuery();
            if (rs.next()) {
                long userId = rs.getLong("user_id");
                String shopName = rs.getString("shop_name");
                String category = rs.getString("shop_category");
                String desc = rs.getString("description");
                PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO shop (user_id, shop_name, shop_category, description, status, rating, total_orders, total_sales, create_time) " +
                    "VALUES (?, ?, ?, ?, 1, 5.0, 0, 0.00, NOW())");
                ps2.setLong(1, userId); ps2.setString(2, shopName);
                ps2.setString(3, category); ps2.setString(4, desc);
                ps2.executeUpdate();
                PreparedStatement ps3 = conn.prepareStatement(
                    "UPDATE shop_apply SET status = 1, review_time = NOW(), reviewer_id = ? WHERE id = ?");
                ps3.setLong(1, (Long) req.getSession().getAttribute("userId"));
                ps3.setLong(2, applyId);
                ps3.executeUpdate();
                PreparedStatement ps4 = conn.prepareStatement("UPDATE user SET role = 'shopkeeper' WHERE id = ?");
                ps4.setLong(1, userId); ps4.executeUpdate();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/shop/auditList?msg=approved");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void reject(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long applyId = Long.parseLong(req.getParameter("id"));
        String reason = req.getParameter("reason");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE shop_apply SET status = 2, reject_reason = ?, review_time = NOW(), reviewer_id = ? WHERE id = ?");
            ps.setString(1, reason);
            ps.setLong(2, (Long) req.getSession().getAttribute("userId"));
            ps.setLong(3, applyId);
            ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/admin/shop/auditList?msg=rejected");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void closeShop(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long shopId = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps1 = conn.prepareStatement("UPDATE shop SET status = -1 WHERE id = ?");
            ps1.setLong(1, shopId); ps1.executeUpdate();
            PreparedStatement ps2 = conn.prepareStatement("UPDATE product SET status = 2 WHERE shop_id = ?");
            ps2.setLong(1, shopId); ps2.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/admin/shop/allShops?msg=closed");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }
}
