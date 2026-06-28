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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            case "/abnormal": listAbnormal(req, resp); break;
            default: listOrders(req, resp); break;
        }
    }

    private String statusText(int s) {
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

    private void listOrders(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String status = req.getParameter("status");
        String keyword = req.getParameter("keyword");
        int page = PageUtil.getPage(req);
        try (Connection conn = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder(
                "SELECT o.*, u.nickname AS buyer_name, s.shop_name FROM `order` o " +
                "LEFT JOIN user u ON o.user_id = u.id " +
                "LEFT JOIN shop s ON o.shop_id = s.id WHERE 1=1");
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
            List<Map<String, Object>> orders = new ArrayList<>();
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
                m.put("statusText", statusText(st));
                m.put("createTime", rs.getTimestamp("create_time"));
                orders.add(m);
            }
            req.setAttribute("orders", orders); req.setAttribute("page", page);
            req.getRequestDispatcher("/admin/order_list.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace(); req.setAttribute("error", "查询订单失败");
            req.getRequestDispatcher("/admin/order_list.jsp").forward(req, resp);
        }
    }

    private void listAbnormal(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 异常订单：状态 5/6/7（已取消/退款中/已退款）
        String keyword = req.getParameter("keyword");
        int page = PageUtil.getPage(req);
        try (Connection conn = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder(
                "SELECT o.*, u.nickname AS buyer_name, s.shop_name FROM `order` o " +
                "LEFT JOIN user u ON o.user_id = u.id " +
                "LEFT JOIN shop s ON o.shop_id = s.id WHERE o.status IN (5,6,7)");
            if (keyword != null && !keyword.trim().isEmpty()) sql.append(" AND (o.order_no LIKE ? OR u.nickname LIKE ?)");
            sql.append(" ORDER BY o.create_time DESC LIMIT ?, 20");
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(idx++, kw); ps.setString(idx++, kw);
            }
            ps.setInt(idx++, (page - 1) * 20);
            ResultSet rs = ps.executeQuery();
            List<Map<String, Object>> orders = new ArrayList<>();
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
                m.put("statusText", statusText(st));
                m.put("createTime", rs.getTimestamp("create_time"));
                orders.add(m);
            }
            req.setAttribute("orders", orders);
            req.setAttribute("page", page);
            req.setAttribute("abnormal", true);
            req.getRequestDispatcher("/admin/order_list.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); req.setAttribute("error", "查询异常订单失败");
            req.getRequestDispatcher("/admin/order_list.jsp").forward(req, resp); }
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT o.*, u.nickname AS buyer_name, u.phone AS buyer_phone, " +
                "s.shop_name, l.company, l.tracking_no, l.status AS logistics_status " +
                "FROM `order` o LEFT JOIN user u ON o.user_id = u.id " +
                "LEFT JOIN shop s ON o.shop_id = s.id " +
                "LEFT JOIN logistics l ON o.id = l.order_id WHERE o.id = ?");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> o = new HashMap<>();
                o.put("id", rs.getLong("id"));
                o.put("orderNo", rs.getString("order_no"));
                o.put("buyerName", rs.getString("buyer_name"));
                o.put("buyerPhone", rs.getString("buyer_phone"));
                o.put("shopName", rs.getString("shop_name"));
                o.put("totalAmount", rs.getBigDecimal("total_amount"));
                o.put("payAmount", rs.getBigDecimal("pay_amount"));
                int st = rs.getInt("status");
                o.put("status", st);
                o.put("statusText", statusText(st));
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
                o.put("logisticsStatusText",
                    ls == 0 ? "未发货" : ls == 1 ? "运输中" : ls == 2 ? "派送中" : ls == 3 ? "已签收" : "未知");
                req.setAttribute("order", o);
            }
            PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM order_item WHERE order_id = ?");
            ps2.setLong(1, id);
            ResultSet rs2 = ps2.executeQuery();
            List<Map<String, Object>> items = new ArrayList<>();
            while (rs2.next()) {
                Map<String, Object> it = new HashMap<>();
                it.put("productName", rs2.getString("product_name"));
                it.put("coverImage", rs2.getString("cover_image"));
                it.put("price", rs2.getBigDecimal("price"));
                it.put("quantity", rs2.getInt("quantity"));
                it.put("subtotal", rs2.getBigDecimal("subtotal"));
                items.add(it);
            }
            req.setAttribute("items", items);
            req.getRequestDispatcher("/admin/order_detail.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }
}
