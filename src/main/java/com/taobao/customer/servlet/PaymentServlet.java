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
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String orderId = req.getParameter("orderId");
        String amount = req.getParameter("amount");
        req.setAttribute("orderId", orderId); req.setAttribute("payAmount", amount);
        req.getRequestDispatcher("/payment.jsp").forward(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        long orderId = Long.parseLong(req.getParameter("orderId"));
        int payMethod = Integer.parseInt(req.getParameter("payMethod"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE `order` SET status = 1, pay_method = ?, pay_time = NOW() WHERE id = ? AND status = 0");
            ps.setInt(1, payMethod); ps.setLong(2, orderId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                PreparedStatement ps2 = conn.prepareStatement("INSERT INTO logistics (order_id, status) VALUES (?, 0)");
                ps2.setLong(1, orderId); ps2.executeUpdate();
            }
            req.setAttribute("orderId", orderId); req.setAttribute("success", true);
            req.getRequestDispatcher("/payment.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); req.setAttribute("error", "支付失败"); req.getRequestDispatcher("/payment.jsp").forward(req, resp); }
    }
}
