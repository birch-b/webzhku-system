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

@WebServlet("/shop/review/*")
public class ShopReviewServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps0 = conn.prepareStatement("SELECT id FROM shop WHERE user_id = ?");
            ps0.setLong(1, userId); ResultSet rs0 = ps0.executeQuery();
            long shopId = rs0.next() ? rs0.getLong("id") : 0;
            PreparedStatement ps = conn.prepareStatement(
                "SELECT r.*, p.name AS product_name, u.nickname AS buyer_name FROM review r LEFT JOIN product p ON r.product_id = p.id LEFT JOIN user u ON r.user_id = u.id WHERE r.shop_id = ? ORDER BY r.create_time DESC");
            ps.setLong(1, shopId); ResultSet rs = ps.executeQuery();
            List<String[]> reviews = new ArrayList<>();
            while (rs.next()) {
                reviews.add(new String[]{String.valueOf(rs.getLong("id")), rs.getString("product_name"),
                    rs.getString("buyer_name"), String.valueOf(rs.getInt("rating")),
                    rs.getString("content"), rs.getString("reply"), rs.getTimestamp("create_time").toString()});
            }
            req.setAttribute("reviews", reviews);
            req.getRequestDispatcher("/shop/review_list.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); req.getRequestDispatcher("/shop/review_list.jsp").forward(req, resp); }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        long reviewId = Long.parseLong(req.getParameter("id"));
        String reply = req.getParameter("reply");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE review SET reply = ?, reply_time = NOW() WHERE id = ?");
            ps.setString(1, reply); ps.setLong(2, reviewId); ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/shop/review/list?msg=replied");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }
}
