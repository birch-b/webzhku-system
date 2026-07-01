package com.taobao.servlet;

import com.taobao.entity.User;
import com.taobao.util.DBUtil;
import com.taobao.util.MD5Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 登录Servlet - 处理用户登录请求
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 使用相对路径转发，避免路径解析问题
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            req.setAttribute("error", "用户名和密码不能为空");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        String md5Password = MD5Util.encrypt(password);

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, md5Password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int status = rs.getInt("status");
                if (status == 0) {
                    req.setAttribute("error", "账号已被封禁，请联系管理员");
                    req.getRequestDispatcher("/login.jsp").forward(req, resp);
                    return;
                }

                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setNickname(rs.getString("nickname"));
                user.setRole(rs.getString("role"));
                user.setAvatar(rs.getString("avatar"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setStatus(status);

                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("userRole", user.getRole());
                session.setAttribute("nickname", user.getNickname() != null ? user.getNickname() : user.getUsername());

                // 根据用户角色自动跳转到对应的工作台
                String role = user.getRole();
                if ("operator".equals(role)) {
                    // 运营商 → 后台仪表盘
                    resp.sendRedirect(req.getContextPath() + "/admin/stat/dashboard");
                } else if ("shopkeeper".equals(role)) {
                    // 商家 → 商家后台
                    resp.sendRedirect(req.getContextPath() + "/shop/home");
                } else {
                    // 普通顾客 → 首页
                    resp.sendRedirect(req.getContextPath() + "/");
                }
            } else {
                req.setAttribute("error", "用户名或密码错误");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "系统错误，请稍后重试");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
