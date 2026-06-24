package com.taobao.servlet;

import com.taobao.util.DBUtil;
import com.taobao.util.MD5Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * 注册Servlet - 处理用户注册请求
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String nickname = req.getParameter("nickname");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");

        // 参数校验
        if (username == null || username.trim().isEmpty()) {
            req.setAttribute("error", "用户名不能为空");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }
        if (password == null || password.trim().length() < 6) {
            req.setAttribute("error", "密码长度至少6位");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        // 检查用户名是否已存在
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement checkPs = conn.prepareStatement("SELECT id FROM user WHERE username = ?");
            checkPs.setString(1, username);
            if (checkPs.executeQuery().next()) {
                req.setAttribute("error", "用户名已存在");
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
                return;
            }

            // 插入新用户
            String sql = "INSERT INTO user (username, password, role, nickname, email, phone, status) VALUES (?, ?, 'customer', ?, ?, ?, 1)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, MD5Util.encrypt(password));
            ps.setString(3, nickname != null ? nickname : username);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.executeUpdate();

            req.setAttribute("success", "注册成功，请登录");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "注册失败，请稍后重试");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }
}
