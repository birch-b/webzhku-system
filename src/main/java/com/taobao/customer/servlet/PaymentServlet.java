package com.taobao.customer.servlet;

import com.taobao.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet("/payment/*")
public class PaymentServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("orderId", req.getParameter("orderId")); req.setAttribute("payAmount", req.getParameter("amount"));
        req.getRequestDispatcher("/payment.jsp").forward(req, resp);
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); Long uid = (Long) req.getSession().getAttribute("userId");
        if (uid == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        long oid = Long.parseLong(req.getParameter("orderId")); int pm = Integer.parseInt(req.getParameter("payMethod"));
        try (Connection c = DBUtil.getConnection()) {
            PreparedStatement pc = c.prepareStatement("SELECT user_id,total_amount FROM `order` WHERE id=? AND status=0");
            pc.setLong(1, oid); ResultSet rc = pc.executeQuery();
            if (!rc.next()) { req.setAttribute("error", "订单不存在或已支付"); req.getRequestDispatcher("/payment.jsp").forward(req, resp); return; }
            if (rc.getLong("user_id") != uid) { req.setAttribute("error", "无权操作"); req.getRequestDispatcher("/payment.jsp").forward(req, resp); return; }
            PreparedStatement p = c.prepareStatement("UPDATE `order` SET status=1,pay_method=?,pay_time=NOW() WHERE id=? AND status=0");
            p.setInt(1, pm); p.setLong(2, oid); int rows = p.executeUpdate();
            if (rows > 0) { PreparedStatement p2 = c.prepareStatement("INSERT INTO logistics(order_id,status) VALUES(?,0)"); p2.setLong(1, oid); p2.executeUpdate(); }
            req.setAttribute("orderId", oid); req.setAttribute("success", true);
            req.getRequestDispatcher("/payment.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); req.setAttribute("error", "支付失败"); req.getRequestDispatcher("/payment.jsp").forward(req, resp); }
    }
}
