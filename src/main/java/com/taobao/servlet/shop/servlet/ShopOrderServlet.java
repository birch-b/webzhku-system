package com.taobao.servlet.shop.servlet;

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

@WebServlet("/shop/order/*")
public class ShopOrderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/list";
        switch (pathInfo) {
            case "/list": listOrders(req, resp); break;
            case "/detail": showDetail(req, resp); break;
            default: listOrders(req, resp);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        switch (pathInfo != null ? pathInfo : "") {
            case "/ship": shipOrder(req, resp); break;
            default: resp.sendError(404);
        }
    }

    private long getShopId(Long userId) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps0 = conn.prepareStatement("SELECT id FROM shop WHERE user_id = ?");
            ps0.setLong(1, userId); ResultSet rs0 = ps0.executeQuery();
            return rs0.next() ? rs0.getLong("id") : 0;
        }
    }

    private void listOrders(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        String status = req.getParameter("status");
        try (Connection conn = DBUtil.getConnection()) {
            long shopId = getShopId(userId);
            StringBuilder sql = new StringBuilder(
                "SELECT o.*, u.nickname AS buyer_name FROM `order` o LEFT JOIN user u ON o.user_id = u.id WHERE o.shop_id = ?");
            if (status != null && !status.isEmpty()) sql.append(" AND o.status = ?");
            sql.append(" ORDER BY o.create_time DESC");
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            ps.setLong(1, shopId);
            if (status != null && !status.isEmpty()) ps.setInt(2, Integer.parseInt(status));
            ResultSet rs = ps.executeQuery();
            List<String[]> orders = new ArrayList<>();
            while (rs.next()) {
                orders.add(new String[]{String.valueOf(rs.getLong("id")), rs.getString("order_no"),
                    rs.getString("buyer_name"), rs.getString("pay_amount"),
                    String.valueOf(rs.getInt("status")), rs.getTimestamp("create_time").toString(),
                    rs.getString("receiver_name")});
            }
            req.setAttribute("orders", orders);
            req.getRequestDispatcher("/shop/order_list.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); req.getRequestDispatcher("/shop/order_list.jsp").forward(req, resp); }
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long orderId = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT o.*, u.nickname AS buyer_name, l.company, l.tracking_no, l.status AS logistics_status FROM `order` o LEFT JOIN user u ON o.user_id = u.id LEFT JOIN logistics l ON o.id = l.order_id WHERE o.id = ?");
            ps.setLong(1, orderId); ResultSet rs = ps.executeQuery();
            if (rs.next()) req.setAttribute("order", rs);
            PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM order_item WHERE order_id = ?");
            ps2.setLong(1, orderId); ResultSet rs2 = ps2.executeQuery();
            List<String[]> items = new ArrayList<>();
            while (rs2.next()) {
                items.add(new String[]{rs2.getString("product_name"), rs2.getString("cover_image"),
                    rs2.getString("price"), String.valueOf(rs2.getInt("quantity")), rs2.getString("subtotal")});
            }
            req.setAttribute("orderItems", items);
            req.getRequestDispatcher("/shop/order_detail.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void shipOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long orderId = Long.parseLong(req.getParameter("id"));
        String company = req.getParameter("company");
        String trackingNo = req.getParameter("trackingNo");
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement ps1 = conn.prepareStatement("UPDATE `order` SET status = 2, ship_time = NOW() WHERE id = ? AND status = 1");
            ps1.setLong(1, orderId); ps1.executeUpdate();
            PreparedStatement ps2 = conn.prepareStatement(
                "INSERT INTO logistics (order_id, company, tracking_no, status, ship_time) VALUES (?, ?, ?, 1, NOW()) ON DUPLICATE KEY UPDATE company=?, tracking_no=?, status=1, ship_time=NOW()");
            ps2.setLong(1, orderId); ps2.setString(2, company); ps2.setString(3, trackingNo);
            ps2.setString(4, company); ps2.setString(5, trackingNo); ps2.executeUpdate();
            conn.commit();
            resp.sendRedirect(req.getContextPath() + "/shop/order/list?msg=shipped");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }
}
