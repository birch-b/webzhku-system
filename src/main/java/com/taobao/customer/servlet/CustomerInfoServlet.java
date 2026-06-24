package com.taobao.customer.servlet;

import com.taobao.util.DBUtil;
import com.taobao.util.MD5Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet("/customer/profile/*")
public class CustomerInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM user WHERE id = ?");
            ps.setLong(1, userId); ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                req.setAttribute("username", rs.getString("username")); req.setAttribute("nickname", rs.getString("nickname"));
                req.setAttribute("phone", rs.getString("phone")); req.setAttribute("email", rs.getString("email"));
                req.setAttribute("avatar", rs.getString("avatar"));
            }
            req.getRequestDispatcher("/customer/profile.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); req.getRequestDispatcher("/customer/profile.jsp").forward(req, resp); }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Long userId = (Long) req.getSession().getAttribute("userId");
        String action = req.getParameter("action");
        try (Connection conn = DBUtil.getConnection()) {
            switch (action != null ? action : "") {
                case "updateInfo":
                    PreparedStatement ps1 = conn.prepareStatement("UPDATE user SET nickname=?, phone=?, email=? WHERE id=?");
                    ps1.setString(1, req.getParameter("nickname")); ps1.setString(2, req.getParameter("phone"));
                    ps1.setString(3, req.getParameter("email")); ps1.setLong(4, userId); ps1.executeUpdate(); break;
                case "changePassword":
                    String oldPwd = MD5Util.encrypt(req.getParameter("oldPassword"));
                    String newPwd = MD5Util.encrypt(req.getParameter("newPassword"));
                    PreparedStatement ps2 = conn.prepareStatement("SELECT password FROM user WHERE id=?");
                    ps2.setLong(1, userId); ResultSet rs = ps2.executeQuery();
                    if (rs.next() && rs.getString("password").equals(oldPwd)) {
                        PreparedStatement ps3 = conn.prepareStatement("UPDATE user SET password=? WHERE id=?");
                        ps3.setString(1, newPwd); ps3.setLong(2, userId); ps3.executeUpdate();
                        req.setAttribute("msg", "密码修改成功");
                    } else { req.setAttribute("error", "原密码错误"); }
                    break;
            }
            doGet(req, resp);
        } catch (Exception e) { e.printStackTrace(); req.setAttribute("error", "操作失败"); doGet(req, resp); }
    }
}
