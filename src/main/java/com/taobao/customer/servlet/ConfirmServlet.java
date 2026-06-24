package com.taobao.customer.servlet;

import com.taobao.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet("/order/confirm")
public class ConfirmServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
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
