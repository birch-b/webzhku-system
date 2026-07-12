package com.taobao.shop.servlet;

import com.taobao.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet("/shop/apply/*")
public class ShopApplyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps0 = conn.prepareStatement("SELECT id FROM shop WHERE user_id = ?");
            ps0.setLong(1, userId);
            if (ps0.executeQuery().next()) {
                resp.sendRedirect(req.getContextPath() + "/shop/info/view"); return;
            }
            PreparedStatement ps1 = conn.prepareStatement("SELECT * FROM shop_apply WHERE user_id = ? ORDER BY apply_time DESC LIMIT 1");
            ps1.setLong(1, userId); ResultSet rs = ps1.executeQuery();
            if (rs.next()) {
                req.setAttribute("applyId", rs.getLong("id"));
                req.setAttribute("shopName", rs.getString("shop_name"));
                req.setAttribute("applyStatus", rs.getInt("status"));
                req.setAttribute("rejectReason", rs.getString("reject_reason"));
            }
            req.getRequestDispatcher("/shop/shop_apply.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace(); req.getRequestDispatcher("/shop/shop_apply.jsp").forward(req, resp);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Long userId = (Long) req.getSession().getAttribute("userId");
        String shopName = req.getParameter("shopName");
        String shopCategory = req.getParameter("shopCategory");
        String description = req.getParameter("description");
        String contactName = req.getParameter("contactName");
        String contactPhone = req.getParameter("contactPhone");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO shop_apply (user_id, shop_name, shop_category, description, contact_name, contact_phone, status) VALUES (?, ?, ?, ?, ?, ?, 0)");
            ps.setLong(1, userId); ps.setString(2, shopName);
            ps.setString(3, shopCategory); ps.setString(4, description);
            ps.setString(5, contactName); ps.setString(6, contactPhone);
            ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/shop/apply/view?msg=submitted");
        } catch (Exception e) {
            e.printStackTrace(); req.setAttribute("error", "提交申请失败");
            req.getRequestDispatcher("/shop/shop_apply.jsp").forward(req, resp);
        }
    }
}
