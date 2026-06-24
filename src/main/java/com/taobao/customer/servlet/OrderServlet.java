package com.taobao.customer.servlet;

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

@WebServlet("/order/*")
public class OrderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/list";
        switch (pathInfo) {
            case "/list": listOrders(req, resp); break;
            case "/detail": showDetail(req, resp); break;
            case "/checkout": showCheckout(req, resp); break;
            default: listOrders(req, resp);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        switch (pathInfo != null ? pathInfo : "") {
            case "/create": createOrder(req, resp); break;
            case "/cancel": cancelOrder(req, resp); break;
            case "/confirm": confirmOrder(req, resp); break;
            default: resp.sendError(404);
        }
    }

    private void showCheckout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT ci.*, p.name, p.price, p.cover_image, p.shop_id FROM cart_item ci LEFT JOIN product p ON ci.product_id = p.id WHERE ci.user_id = ? AND ci.selected = 1");
            ps.setLong(1, userId); ResultSet rs = ps.executeQuery();
            List<String[]> items = new ArrayList<>(); double total = 0;
            while (rs.next()) {
                double subtotal = rs.getDouble("price") * rs.getInt("quantity"); total += subtotal;
                items.add(new String[]{String.valueOf(rs.getLong("product_id")), rs.getString("name"),
                    rs.getString("price"), String.valueOf(rs.getInt("quantity")),
                    String.valueOf(subtotal), rs.getString("cover_image"), String.valueOf(rs.getLong("shop_id"))});
            }
            req.setAttribute("checkoutItems", items); req.setAttribute("totalAmount", total);
            PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM address WHERE user_id=? AND is_default=1 LIMIT 1");
            ps2.setLong(1, userId); ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                req.setAttribute("addrId", rs2.getLong("id")); req.setAttribute("addrName", rs2.getString("receiver_name"));
                req.setAttribute("addrPhone", rs2.getString("phone"));
                req.setAttribute("addrDetail", rs2.getString("province")+rs2.getString("city")+rs2.getString("district")+rs2.getString("detail"));
            }
            req.getRequestDispatcher("/checkout.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void createOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        String addressId = req.getParameter("addressId");
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement ps0 = conn.prepareStatement(
                "SELECT ci.*, p.name, p.price, p.cover_image, p.shop_id FROM cart_item ci LEFT JOIN product p ON ci.product_id = p.id WHERE ci.user_id = ? AND ci.selected = 1");
            ps0.setLong(1, userId); ResultSet rs0 = ps0.executeQuery();
            long shopId = 0; double totalAmount = 0;
            List<String[]> orderItems = new ArrayList<>();
            while (rs0.next()) {
                shopId = rs0.getLong("shop_id");
                double price = rs0.getDouble("price"); int qty = rs0.getInt("quantity");
                totalAmount += price * qty;
                orderItems.add(new String[]{String.valueOf(rs0.getLong("product_id")), rs0.getString("name"),
                    String.valueOf(price), String.valueOf(qty), String.valueOf(price * qty), rs0.getString("cover_image")});
            }
            if (orderItems.isEmpty()) { conn.rollback(); resp.sendRedirect(req.getContextPath() + "/cart/list"); return; }

            PreparedStatement psAddr = conn.prepareStatement("SELECT * FROM address WHERE id=?");
            psAddr.setLong(1, Long.parseLong(addressId)); ResultSet rsAddr = psAddr.executeQuery();
            String receiverName = "", receiverPhone = "", receiverAddr = "";
            if (rsAddr.next()) {
                receiverName = rsAddr.getString("receiver_name"); receiverPhone = rsAddr.getString("phone");
                receiverAddr = rsAddr.getString("province")+rsAddr.getString("city")+rsAddr.getString("district")+rsAddr.getString("detail");
            }

            String orderNo = "TB" + System.currentTimeMillis() + userId;
            PreparedStatement ps1 = conn.prepareStatement(
                "INSERT INTO `order` (order_no, user_id, shop_id, total_amount, pay_amount, status, receiver_name, receiver_phone, receiver_address, create_time) VALUES (?,?,?,?,0,?,?,?,?,NOW())",
                Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, orderNo); ps1.setLong(2, userId); ps1.setLong(3, shopId);
            ps1.setDouble(4, totalAmount); ps1.setString(5, receiverName);
            ps1.setString(6, receiverPhone); ps1.setString(7, receiverAddr);
            ps1.executeUpdate();
            ResultSet rs1 = ps1.getGeneratedKeys(); long orderId = rs1.next() ? rs1.getLong(1) : 0;

            for (String[] item : orderItems) {
                PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO order_item (order_id, product_id, product_name, cover_image, price, quantity, subtotal) VALUES (?,?,?,?,?,?,?)");
                ps2.setLong(1, orderId); ps2.setLong(2, Long.parseLong(item[0]));
                ps2.setString(3, item[1]); ps2.setString(4, item[5]);
                ps2.setDouble(5, Double.parseDouble(item[2])); ps2.setInt(6, Integer.parseInt(item[3]));
                ps2.setDouble(7, Double.parseDouble(item[4])); ps2.executeUpdate();
            }

            PreparedStatement ps3 = conn.prepareStatement("DELETE FROM cart_item WHERE user_id = ? AND selected = 1");
            ps3.setLong(1, userId); ps3.executeUpdate();
            conn.commit();
            resp.sendRedirect(req.getContextPath() + "/payment/view?orderId=" + orderId + "&amount=" + totalAmount);
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void listOrders(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT o.*, s.shop_name FROM `order` o LEFT JOIN shop s ON o.shop_id = s.id WHERE o.user_id = ? ORDER BY o.create_time DESC");
            ps.setLong(1, userId); ResultSet rs = ps.executeQuery();
            List<String[]> orders = new ArrayList<>();
            while (rs.next()) {
                orders.add(new String[]{String.valueOf(rs.getLong("id")), rs.getString("order_no"),
                    rs.getString("shop_name"), rs.getString("total_amount"),
                    String.valueOf(rs.getInt("status")), rs.getTimestamp("create_time").toString()});
            }
            req.setAttribute("orders", orders);
            req.getRequestDispatcher("/customer/order_list.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); req.getRequestDispatcher("/customer/order_list.jsp").forward(req, resp); }
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long orderId = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT o.*, s.shop_name, l.company, l.tracking_no, l.status AS logistics_status FROM `order` o LEFT JOIN shop s ON o.shop_id = s.id LEFT JOIN logistics l ON o.id = l.order_id WHERE o.id = ?");
            ps.setLong(1, orderId); ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("id", rs.getLong("id"));
                orderMap.put("orderNo", rs.getString("order_no"));
                orderMap.put("shopName", rs.getString("shop_name"));
                orderMap.put("totalAmount", rs.getBigDecimal("total_amount"));
                orderMap.put("payAmount", rs.getBigDecimal("pay_amount"));
                orderMap.put("status", rs.getInt("status"));
                orderMap.put("receiverName", rs.getString("receiver_name"));
                orderMap.put("receiverPhone", rs.getString("receiver_phone"));
                orderMap.put("receiverAddress", rs.getString("receiver_address"));
                orderMap.put("buyerMessage", rs.getString("buyer_message"));
                orderMap.put("createTime", rs.getTimestamp("create_time"));
                orderMap.put("payTime", rs.getTimestamp("pay_time"));
                orderMap.put("shipTime", rs.getTimestamp("ship_time"));
                orderMap.put("receiveTime", rs.getTimestamp("receive_time"));
                orderMap.put("finishTime", rs.getTimestamp("finish_time"));
                orderMap.put("company", rs.getString("company"));
                orderMap.put("trackingNo", rs.getString("tracking_no"));
                orderMap.put("logisticsStatus", rs.getInt("logistics_status"));
                req.setAttribute("order", orderMap);
            }
            PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM order_item WHERE order_id = ?");
            ps2.setLong(1, orderId); ResultSet rs2 = ps2.executeQuery();
            List<String[]> items = new ArrayList<>();
            while (rs2.next()) {
                items.add(new String[]{String.valueOf(rs2.getLong("product_id")),
                    rs2.getString("product_name"), rs2.getString("cover_image"),
                    rs2.getString("price"), String.valueOf(rs2.getInt("quantity")), rs2.getString("subtotal")});
            }
            req.setAttribute("orderItems", items);
            req.getRequestDispatcher("/customer/order_detail.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void cancelOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long orderId = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE `order` SET status = 5 WHERE id = ? AND status = 0");
            ps.setLong(1, orderId); ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/order/list");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void confirmOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long orderId = Long.parseLong(req.getParameter("id"));
        Long userId = (Long) req.getSession().getAttribute("userId");
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement ps1 = conn.prepareStatement(
                "UPDATE `order` SET status = 3, receive_time = NOW() WHERE id = ? AND user_id = ? AND status = 2");
            ps1.setLong(1, orderId); ps1.setLong(2, userId); ps1.executeUpdate();
            PreparedStatement ps2 = conn.prepareStatement("UPDATE logistics SET status = 3 WHERE order_id = ?");
            ps2.setLong(1, orderId); ps2.executeUpdate();
            PreparedStatement ps3 = conn.prepareStatement("SELECT product_id, quantity FROM order_item WHERE order_id = ?");
            ps3.setLong(1, orderId); ResultSet rs = ps3.executeQuery();
            while (rs.next()) {
                PreparedStatement ps4 = conn.prepareStatement("UPDATE product SET sales = sales + ? WHERE id = ?");
                ps4.setInt(1, rs.getInt("quantity")); ps4.setLong(2, rs.getLong("product_id")); ps4.executeUpdate();
            }
            conn.commit();
            resp.sendRedirect(req.getContextPath() + "/order/detail?id=" + orderId);
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }
}
