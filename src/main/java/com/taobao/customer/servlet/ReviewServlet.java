package com.taobao.customer.servlet;

import com.taobao.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet("/review/*")
public class ReviewServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("orderId", req.getParameter("orderId"));
        req.setAttribute("productId", req.getParameter("productId"));
        req.getRequestDispatcher("/customer/review_edit.jsp").forward(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Long userId = (Long) req.getSession().getAttribute("userId");
        long orderId = Long.parseLong(req.getParameter("orderId"));
        long productId = Long.parseLong(req.getParameter("productId"));
        int rating = Integer.parseInt(req.getParameter("rating"));
        String content = req.getParameter("content");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps0 = conn.prepareStatement("SELECT shop_id FROM product WHERE id = ?");
            ps0.setLong(1, productId); ResultSet rs0 = ps0.executeQuery();
            long shopId = rs0.next() ? rs0.getLong("shop_id") : 0;
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO review (user_id, shop_id, product_id, order_id, rating, content, status, create_time) VALUES (?, ?, ?, ?, ?, ?, 1, NOW())");
            ps.setLong(1, userId); ps.setLong(2, shopId); ps.setLong(3, productId);
            ps.setLong(4, orderId); ps.setInt(5, rating); ps.setString(6, content);
            ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/order/list?msg=reviewed");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }
}
