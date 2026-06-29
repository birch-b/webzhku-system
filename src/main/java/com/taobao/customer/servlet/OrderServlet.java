package com.taobao.customer.servlet;

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

@WebServlet("/order/*")
public class OrderServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pi = req.getPathInfo(); if (pi == null) pi = "/list";
        switch (pi) { case "/list": listOrders(req, resp); break; case "/detail": showDetail(req, resp); break; case "/checkout": showCheckout(req, resp); break; default: listOrders(req, resp); }
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); String pi = req.getPathInfo();
        switch (pi != null ? pi : "") { case "/create": createOrder(req, resp); break; case "/cancel": cancelOrder(req, resp); break; case "/confirm": confirmOrder(req, resp); break; default: resp.sendError(404); }
    }

    private void showCheckout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long uid = (Long) req.getSession().getAttribute("userId");
        try (Connection c = DBUtil.getConnection()) {
            PreparedStatement ps = c.prepareStatement("SELECT ci.*,p.name,p.price,p.cover_image,p.shop_id FROM cart_item ci LEFT JOIN product p ON ci.product_id=p.id WHERE ci.user_id=? AND ci.selected=1");
            ps.setLong(1, uid); ResultSet rs = ps.executeQuery();
            List<String[]> items = new ArrayList<>(); double total = 0;
            while (rs.next()) { double st = rs.getDouble("price")*rs.getInt("quantity"); total += st;
                items.add(new String[]{String.valueOf(rs.getLong("product_id")), rs.getString("name"), rs.getString("price"),
                        String.valueOf(rs.getInt("quantity")), String.valueOf(st), rs.getString("cover_image"), String.valueOf(rs.getLong("shop_id"))}); }
            req.setAttribute("checkoutItems", items); req.setAttribute("totalAmount", total);
            PreparedStatement pa = c.prepareStatement("SELECT id,receiver_name,phone,province,city,district,detail,is_default FROM address WHERE user_id=? ORDER BY is_default DESC,create_time DESC");
            pa.setLong(1, uid); ResultSet ra = pa.executeQuery();
            List<String[]> addrs = new ArrayList<>();
            while (ra.next()) { addrs.add(new String[]{String.valueOf(ra.getLong("id")), ra.getString("receiver_name"),
                    ra.getString("phone"), ra.getString("province")+ra.getString("city")+ra.getString("district")+ra.getString("detail"),
                    String.valueOf(ra.getInt("is_default"))}); }
            req.setAttribute("addresses", addrs);
            for (String[] a : addrs) if ("1".equals(a[4])) { req.setAttribute("addrId", a[0]); req.setAttribute("addrName", a[1]);
                req.setAttribute("addrPhone", a[2]); req.setAttribute("addrDetail", a[3]); break; }
            if (req.getAttribute("addrId") == null && !addrs.isEmpty()) { req.setAttribute("addrId", addrs.get(0)[0]);
                req.setAttribute("addrName", addrs.get(0)[1]); req.setAttribute("addrPhone", addrs.get(0)[2]); req.setAttribute("addrDetail", addrs.get(0)[3]); }
            req.getRequestDispatcher("/checkout.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void createOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long uid = (Long) req.getSession().getAttribute("userId");
        String aid = req.getParameter("addressId"), msg = req.getParameter("buyerMessage");
        if (msg == null) msg = "";
        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false);
            try {
                PreparedStatement p0 = c.prepareStatement("SELECT ci.*,p.name,p.price,p.cover_image,p.shop_id,p.stock FROM cart_item ci LEFT JOIN product p ON ci.product_id=p.id WHERE ci.user_id=? AND ci.selected=1");
                p0.setLong(1, uid); ResultSet r0 = p0.executeQuery();
                long sid = 0; double total = 0; List<String[]> ois = new ArrayList<>();
                while (r0.next()) { int stock = r0.getInt("stock"), qty = r0.getInt("quantity");
                    if (qty > stock) { c.rollback(); resp.sendRedirect(req.getContextPath() + "/cart/list?msg=stock"); return; }
                    sid = r0.getLong("shop_id"); double price = r0.getDouble("price"); total += price * qty;
                    ois.add(new String[]{String.valueOf(r0.getLong("product_id")), r0.getString("name"), String.valueOf(price), String.valueOf(qty), String.valueOf(price*qty), r0.getString("cover_image")}); }
                if (ois.isEmpty()) { c.rollback(); resp.sendRedirect(req.getContextPath() + "/cart/list"); return; }
                PreparedStatement pa = c.prepareStatement("SELECT * FROM address WHERE id=?"); pa.setLong(1, Long.parseLong(aid)); ResultSet ra = pa.executeQuery();
                String rn = "", rp = "", rad = ""; if (ra.next()) { rn = ra.getString("receiver_name"); rp = ra.getString("phone"); rad = ra.getString("province")+ra.getString("city")+ra.getString("district")+ra.getString("detail"); }
                String ono = "TB" + System.currentTimeMillis() + uid;
                PreparedStatement p1 = c.prepareStatement("INSERT INTO `order`(order_no,user_id,shop_id,total_amount,pay_amount,status,receiver_name,receiver_phone,receiver_address,buyer_message,create_time) VALUES(?,?,?,?,?,0,?,?,?,?,NOW())", Statement.RETURN_GENERATED_KEYS);
                p1.setString(1, ono); p1.setLong(2, uid); p1.setLong(3, sid); p1.setDouble(4, total); p1.setDouble(5, total); p1.setString(6, rn); p1.setString(7, rp); p1.setString(8, rad); p1.setString(9, msg); p1.executeUpdate();
                ResultSet rg = p1.getGeneratedKeys(); long oid = rg.next() ? rg.getLong(1) : 0;
                for (String[] it : ois) { PreparedStatement p2 = c.prepareStatement("INSERT INTO order_item(order_id,product_id,product_name,cover_image,price,quantity,subtotal) VALUES(?,?,?,?,?,?,?)");
                    p2.setLong(1, oid); p2.setLong(2, Long.parseLong(it[0])); p2.setString(3, it[1]); p2.setString(4, it[5]); p2.setDouble(5, Double.parseDouble(it[2])); p2.setInt(6, Integer.parseInt(it[3])); p2.setDouble(7, Double.parseDouble(it[4])); p2.executeUpdate(); }
                for (String[] it : ois) { PreparedStatement ps = c.prepareStatement("UPDATE product SET stock=stock-? WHERE id=? AND stock>=?");
                    ps.setInt(1, Integer.parseInt(it[3])); ps.setLong(2, Long.parseLong(it[0])); ps.setInt(3, Integer.parseInt(it[3]));
                    if (ps.executeUpdate() == 0) { c.rollback(); resp.sendRedirect(req.getContextPath() + "/cart/list?msg=stock"); return; } }
                PreparedStatement p3 = c.prepareStatement("DELETE FROM cart_item WHERE user_id=? AND selected=1"); p3.setLong(1, uid); p3.executeUpdate();
                c.commit(); resp.sendRedirect(req.getContextPath() + "/payment/view?orderId=" + oid + "&amount=" + total);
            } catch (Exception e) { c.rollback(); throw e; }
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void listOrders(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long uid = (Long) req.getSession().getAttribute("userId"); String st = req.getParameter("status"); int page = PageUtil.getPage(req), ps = 10;
        try (Connection c = DBUtil.getConnection()) {
            StringBuilder w = new StringBuilder(" WHERE o.user_id=?"); List<Object> params = new ArrayList<>(); params.add(uid);
            if (st != null && !st.isEmpty()) { w.append(" AND o.status=?"); params.add(Integer.parseInt(st)); }
            PreparedStatement pc = c.prepareStatement("SELECT COUNT(*) FROM `order` o" + w.toString());
            for (int i = 0; i < params.size(); i++) pc.setObject(i + 1, params.get(i)); ResultSet rc = pc.executeQuery();
            int total = rc.next() ? rc.getInt(1) : 0, pages = (int) Math.ceil((double) total / ps);
            PreparedStatement p = c.prepareStatement("SELECT o.*,s.shop_name FROM `order` o LEFT JOIN shop s ON o.shop_id=s.id" + w.toString() + " ORDER BY o.create_time DESC LIMIT ?,?");
            for (int i = 0; i < params.size(); i++) p.setObject(i + 1, params.get(i)); p.setInt(params.size() + 1, (page - 1) * ps); p.setInt(params.size() + 2, ps);
            ResultSet rs = p.executeQuery(); List<String[]> list = new ArrayList<>();
            while (rs.next()) { list.add(new String[]{String.valueOf(rs.getLong("id")), rs.getString("order_no"), rs.getString("shop_name"),
                    rs.getString("total_amount"), String.valueOf(rs.getInt("status")), rs.getTimestamp("create_time").toString(), String.valueOf(rs.getBigDecimal("pay_amount"))}); }
            req.setAttribute("orders", list); req.setAttribute("page", page); req.setAttribute("totalPages", pages); req.setAttribute("totalCount", total); req.setAttribute("currentStatus", st != null ? st : "");
            req.getRequestDispatcher("/customer/order_list.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); req.getRequestDispatcher("/customer/order_list.jsp").forward(req, resp); }
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long oid = Long.parseLong(req.getParameter("id")); Long uid = (Long) req.getSession().getAttribute("userId");
        try (Connection c = DBUtil.getConnection()) {
            PreparedStatement p = c.prepareStatement("SELECT o.*,s.shop_name,l.company,l.tracking_no,l.status AS ls FROM `order` o LEFT JOIN shop s ON o.shop_id=s.id LEFT JOIN logistics l ON o.id=l.order_id WHERE o.id=? AND o.user_id=?");
            p.setLong(1, oid); p.setLong(2, uid); ResultSet rs = p.executeQuery();
            if (rs.next()) { Map<String, Object> m = new HashMap<>(); m.put("id", rs.getLong("id")); m.put("orderNo", rs.getString("order_no")); m.put("shopName", rs.getString("shop_name")); m.put("totalAmount", rs.getBigDecimal("total_amount")); m.put("payAmount", rs.getBigDecimal("pay_amount")); m.put("status", rs.getInt("status")); m.put("receiverName", rs.getString("receiver_name")); m.put("receiverPhone", rs.getString("receiver_phone")); m.put("receiverAddress", rs.getString("receiver_address")); m.put("buyerMessage", rs.getString("buyer_message")); m.put("createTime", rs.getTimestamp("create_time")); m.put("payTime", rs.getTimestamp("pay_time")); m.put("shipTime", rs.getTimestamp("ship_time")); m.put("receiveTime", rs.getTimestamp("receive_time")); m.put("finishTime", rs.getTimestamp("finish_time")); m.put("company", rs.getString("company")); m.put("trackingNo", rs.getString("tracking_no")); m.put("logisticsStatus", rs.getInt("logistics_status")); req.setAttribute("order", m); }
            PreparedStatement p2 = c.prepareStatement("SELECT * FROM order_item WHERE order_id=?"); p2.setLong(1, oid); ResultSet r2 = p2.executeQuery(); List<String[]> items = new ArrayList<>();
            while (r2.next()) items.add(new String[]{String.valueOf(r2.getLong("product_id")), r2.getString("product_name"), r2.getString("cover_image"), r2.getString("price"), String.valueOf(r2.getInt("quantity")), r2.getString("subtotal")}); req.setAttribute("orderItems", items);
            PreparedStatement pas = c.prepareStatement("SELECT id FROM aftersale WHERE order_id=?"); pas.setLong(1, oid); if (pas.executeQuery().next()) req.setAttribute("hasAftersale", true);
            req.getRequestDispatcher("/customer/order_detail.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void cancelOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long oid = Long.parseLong(req.getParameter("id")); Long uid = (Long) req.getSession().getAttribute("userId");
        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false);
            try { PreparedStatement p = c.prepareStatement("UPDATE `order` SET status=5 WHERE id=? AND user_id=? AND status=0"); p.setLong(1, oid); p.setLong(2, uid); int rows = p.executeUpdate();
                if (rows > 0) { PreparedStatement p2 = c.prepareStatement("SELECT product_id,quantity FROM order_item WHERE order_id=?"); p2.setLong(1, oid); ResultSet r = p2.executeQuery(); while (r.next()) { PreparedStatement p3 = c.prepareStatement("UPDATE product SET stock=stock+? WHERE id=?"); p3.setInt(1, r.getInt("quantity")); p3.setLong(2, r.getLong("product_id")); p3.executeUpdate(); } } c.commit();
            } catch (Exception e) { c.rollback(); throw e; } resp.sendRedirect(req.getContextPath() + "/order/list?msg=cancelled");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void confirmOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long oid = Long.parseLong(req.getParameter("id")); Long uid = (Long) req.getSession().getAttribute("userId");
        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false);
            try { PreparedStatement p1 = c.prepareStatement("UPDATE `order` SET status=3,receive_time=NOW() WHERE id=? AND user_id=? AND status=2"); p1.setLong(1, oid); p1.setLong(2, uid); p1.executeUpdate();
                PreparedStatement p2 = c.prepareStatement("UPDATE logistics SET status=3 WHERE order_id=?"); p2.setLong(1, oid); p2.executeUpdate();
                PreparedStatement p3 = c.prepareStatement("SELECT product_id,quantity FROM order_item WHERE order_id=?"); p3.setLong(1, oid); ResultSet r = p3.executeQuery(); while (r.next()) { PreparedStatement p4 = c.prepareStatement("UPDATE product SET sales=sales+? WHERE id=?"); p4.setInt(1, r.getInt("quantity")); p4.setLong(2, r.getLong("product_id")); p4.executeUpdate(); } c.commit();
            } catch (Exception e) { c.rollback(); throw e; } resp.sendRedirect(req.getContextPath() + "/order/detail?id=" + oid);
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }
}
