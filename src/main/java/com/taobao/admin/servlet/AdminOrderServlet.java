package com.taobao.admin.servlet;

import com.taobao.util.DBUtil;
import com.taobao.util.PageUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/order/*")
public class AdminOrderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/list";
        switch (pathInfo) {
            case "/list": listOrders(req, resp); break;
            case "/detail": showDetail(req, resp); break;
            default: listOrders(req, resp);
        }
    }

    private void listOrders(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String status = req.getParameter("status");
        String keyword = req.getParameter("keyword");
        int page = PageUtil.getPage(req);
        try (Connection conn = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder(
                "SELECT o.*, u.nickname AS buyer_name, s.shop_name FROM `order` o LEFT JOIN user u ON o.user_id = u.id LEFT JOIN shop s ON o.shop_id = s.id WHERE 1=1");
            if (status != null && !status.isEmpty()) sql.append(" AND o.status = ?");
            if (keyword != null && !keyword.trim().isEmpty()) sql.append(" AND (o.order_no LIKE ? OR u.nickname LIKE ?)");
            sql.append(" ORDER BY o.create_time DESC LIMIT ?, 20");
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            if (status != null && !status.isEmpty()) ps.setInt(idx++, Integer.parseInt(status));
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(idx++, kw); ps.setString(idx++, kw);
            }
            ps.setInt(idx++, (page - 1) * 20);
            ResultSet rs = ps.executeQuery();
            List<String[]> orders = new ArrayList<>();
            while (rs.next()) {
                orders.add(new String[]{
                    String.valueOf(rs.getLong("id")), rs.getString("order_no"),
                    rs.getString("buyer_name"), rs.getString("shop_name"),
                    rs.getString("total_amount"), rs.getString("pay_amount"),
                    String.valueOf(rs.getInt("status")), rs.getTimestamp("create_time").toString()
                });
            }
            req.setAttribute("orders", orders); req.setAttribute("page", page);
            req.getRequestDispatcher("/admin/order_list.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace(); req.setAttribute("error", "查询订单失败");
            req.getRequestDispatcher("/admin/order_list.jsp").forward(req, resp);
        }
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT o.*, u.nickname AS buyer_name, s.shop_name, l.company, l.tracking_no, l.status AS logistics_status FROM `order` o LEFT JOIN user u ON o.user_id = u.id LEFT JOIN shop s ON o.shop_id = s.id LEFT JOIN logistics l ON o.id = l.order_id WHERE o.id = ?");
            ps.setLong(1, id); ResultSet rs = ps.executeQuery();
            if (rs.next()) req.setAttribute("orderDetail", rs);
            PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM order_item WHERE order_id = ?");
            ps2.setLong(1, id); ResultSet rs2 = ps2.executeQuery();
            List<String[]> items = new ArrayList<>();
            while (rs2.next()) {
                items.add(new String[]{rs2.getString("product_name"), rs2.getString("cover_image"),
                    rs2.getString("price"), String.valueOf(rs2.getInt("quantity")), rs2.getString("subtotal")});
            }
            req.setAttribute("orderItems", items);
            req.getRequestDispatcher("/admin/order_detail.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }
}
