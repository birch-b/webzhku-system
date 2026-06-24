package com.taobao.admin.servlet;

import com.taobao.entity.Shop;
import com.taobao.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            default: listPending(req, resp);
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
            default: resp.sendError(404);
        }
    }

    private void listPending(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT sa.*, u.username, u.nickname FROM shop_apply sa LEFT JOIN user u ON sa.user_id = u.id WHERE sa.status = 0 ORDER BY sa.apply_time DESC");
            ResultSet rs = ps.executeQuery();
            List<Shop> applies = new ArrayList<>();
            while (rs.next()) {
                Shop s = new Shop();
                s.setId(rs.getLong("id")); s.setShopName(rs.getString("shop_name"));
                s.setDescription(rs.getString("description")); s.setCreateTime(rs.getTimestamp("apply_time"));
                applies.add(s);
            }
            req.setAttribute("applies", applies);
            req.getRequestDispatcher("/admin/shop_audit.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace(); req.setAttribute("error", "查询审核列表失败");
            req.getRequestDispatcher("/admin/shop_audit.jsp").forward(req, resp);
        }
    }

    private void listAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM shop ORDER BY id DESC");
            ResultSet rs = ps.executeQuery();
            List<Shop> shops = new ArrayList<>();
            while (rs.next()) { shops.add(mapShop(rs)); }
            req.setAttribute("shops", shops);
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
                    "INSERT INTO shop (user_id, shop_name, shop_category, description, status) VALUES (?, ?, ?, ?, 1)");
                ps2.setLong(1, userId); ps2.setString(2, shopName);
                ps2.setString(3, category); ps2.setString(4, desc);
                ps2.executeUpdate();
                PreparedStatement ps3 = conn.prepareStatement("UPDATE shop_apply SET status = 1, review_time = NOW() WHERE id = ?");
                ps3.setLong(1, applyId); ps3.executeUpdate();
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
                "UPDATE shop_apply SET status = 2, reject_reason = ?, review_time = NOW() WHERE id = ?");
            ps.setString(1, reason); ps.setLong(2, applyId); ps.executeUpdate();
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

    private Shop mapShop(ResultSet rs) throws SQLException {
        Shop s = new Shop();
        s.setId(rs.getLong("id")); s.setShopName(rs.getString("shop_name"));
        s.setDescription(rs.getString("description")); s.setAvatar(rs.getString("avatar"));
        s.setStatus(rs.getInt("status")); s.setCreateTime(rs.getTimestamp("create_time"));
        return s;
    }
}
