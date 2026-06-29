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
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pi = req.getPathInfo(); if (pi == null) pi = "/edit";
        if ("/edit".equals(pi)) editForm(req, resp); else editForm(req, resp);
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); String pi = req.getPathInfo();
        if ("/submit".equals(pi)) submit(req, resp); else resp.sendError(404);
    }
    private void editForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long uid = (Long) req.getSession().getAttribute("userId");
        if (uid == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        long oid = Long.parseLong(req.getParameter("orderId")), pid = Long.parseLong(req.getParameter("productId"));
        try (Connection c = DBUtil.getConnection()) {
            PreparedStatement p0 = c.prepareStatement("SELECT status FROM `order` WHERE id=? AND user_id=?");
            p0.setLong(1, oid); p0.setLong(2, uid); ResultSet r0 = p0.executeQuery();
            if (!r0.next()) { resp.sendRedirect(req.getContextPath() + "/order/list"); return; }
            int s = r0.getInt("status"); if (s != 3 && s != 4) { resp.sendRedirect(req.getContextPath() + "/order/list"); return; }
            PreparedStatement p1 = c.prepareStatement("SELECT id FROM review WHERE order_id=? AND product_id=? AND user_id=?");
            p1.setLong(1, oid); p1.setLong(2, pid); p1.setLong(3, uid);
            if (p1.executeQuery().next()) { resp.sendRedirect(req.getContextPath() + "/order/list?msg=duplicateReview"); return; }
            req.setAttribute("orderId", oid); req.setAttribute("productId", pid);
            req.getRequestDispatcher("/customer/review_edit.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }
    private void submit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8"); Long uid = (Long) req.getSession().getAttribute("userId");
        if (uid == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        long oid = Long.parseLong(req.getParameter("orderId")), pid = Long.parseLong(req.getParameter("productId"));
        int r = Integer.parseInt(req.getParameter("rating")); String ct = req.getParameter("content");
        if (ct == null || ct.trim().isEmpty()) {
            req.setAttribute("error", "请填写评价内容"); req.setAttribute("orderId", oid); req.setAttribute("productId", pid);
            try { req.getRequestDispatcher("/customer/review_edit.jsp").forward(req, resp); } catch (ServletException e) { e.printStackTrace(); } return;
        }
        try (Connection c = DBUtil.getConnection()) {
            PreparedStatement p0 = c.prepareStatement("SELECT id FROM review WHERE order_id=? AND product_id=? AND user_id=?");
            p0.setLong(1, oid); p0.setLong(2, pid); p0.setLong(3, uid);
            if (p0.executeQuery().next()) { resp.sendRedirect(req.getContextPath() + "/order/list?msg=duplicateReview"); return; }
            PreparedStatement ps = c.prepareStatement("SELECT shop_id FROM product WHERE id=?"); ps.setLong(1, pid);
            ResultSet rs = ps.executeQuery(); long sid = rs.next() ? rs.getLong("shop_id") : 0;
            PreparedStatement p = c.prepareStatement("INSERT INTO review(user_id,shop_id,product_id,order_id,rating,content,status,create_time) VALUES(?,?,?,?,?,?,1,NOW())");
            p.setLong(1, uid); p.setLong(2, sid); p.setLong(3, pid); p.setLong(4, oid); p.setInt(5, r); p.setString(6, ct); p.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/order/list?msg=reviewed");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }
}
