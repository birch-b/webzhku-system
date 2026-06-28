package com.taobao.customer.servlet;

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
import java.sql.ResultSet;

/**
 * 个人中心Servlet
 * 处理买家个人信息查看、修改和密码修改
 */
@WebServlet("/customer/profile/*")
public class CustomerInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        Long userId = (Long) req.getSession().getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        switch (pathInfo) {
            case "/":
            case "/view":
                showProfile(req, resp, userId);
                break;
            case "/update":
                // GET请求更新页面，直接跳转到个人中心
                showProfile(req, resp, userId);
                break;
            case "/changePassword":
                showProfile(req, resp, userId);
                break;
            default:
                showProfile(req, resp, userId);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        Long userId = (Long) req.getSession().getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        switch (pathInfo) {
            case "/update":
                updateProfile(req, resp, userId);
                break;
            case "/changePassword":
                changePassword(req, resp, userId);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/customer/profile");
        }
    }

    /**
     * 显示个人信息页面
     */
    private void showProfile(HttpServletRequest req, HttpServletResponse resp, Long userId) throws ServletException, IOException {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT username, nickname, phone, email FROM user WHERE id = ?");
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                req.setAttribute("username", rs.getString("username"));
                req.setAttribute("nickname", rs.getString("nickname"));
                req.setAttribute("phone", rs.getString("phone"));
                req.setAttribute("email", rs.getString("email"));
            }
            req.getRequestDispatcher("/customer/profile.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "加载个人信息失败");
            req.getRequestDispatcher("/customer/profile.jsp").forward(req, resp);
        }
    }

    /**
     * 更新基本信息（昵称、手机、邮箱）
     */
    private void updateProfile(HttpServletRequest req, HttpServletResponse resp, Long userId) throws ServletException, IOException {
        String nickname = req.getParameter("nickname");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");

        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE user SET nickname = ?, phone = ?, email = ? WHERE id = ?");
            ps.setString(1, nickname);
            ps.setString(2, phone);
            ps.setString(3, email);
            ps.setLong(4, userId);
            ps.executeUpdate();

            // 更新session中的昵称
            req.getSession().setAttribute("nickname", nickname);

            resp.sendRedirect(req.getContextPath() + "/customer/profile?msg=updated");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/customer/profile?error=updateFail");
        }
    }

    /**
     * 修改密码
     */
    private void changePassword(HttpServletRequest req, HttpServletResponse resp, Long userId) throws ServletException, IOException {
        String oldPassword = req.getParameter("oldPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        // 参数校验
        if (oldPassword == null || oldPassword.trim().isEmpty() ||
                newPassword == null || newPassword.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/customer/profile?error=empty");
            return;
        }

        // 确认密码一致性检查
        if (!newPassword.equals(confirmPassword)) {
            resp.sendRedirect(req.getContextPath() + "/customer/profile?error=mismatch");
            return;
        }

        // 密码长度检查
        if (newPassword.length() < 6) {
            resp.sendRedirect(req.getContextPath() + "/customer/profile?error=tooShort");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            // 验证原密码
            PreparedStatement ps1 = conn.prepareStatement(
                    "SELECT password FROM user WHERE id = ?");
            ps1.setLong(1, userId);
            ResultSet rs = ps1.executeQuery();
            if (!rs.next()) {
                resp.sendRedirect(req.getContextPath() + "/customer/profile?error=userNotFound");
                return;
            }

            String dbPassword = rs.getString("password");
            String oldMd5 = MD5Util.encrypt(oldPassword);

            if (!dbPassword.equals(oldMd5)) {
                resp.sendRedirect(req.getContextPath() + "/customer/profile?error=wrongPassword");
                return;
            }

            // 更新新密码
            String newMd5 = MD5Util.encrypt(newPassword);
            PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE user SET password = ? WHERE id = ?");
            ps2.setString(1, newMd5);
            ps2.setLong(2, userId);
            ps2.executeUpdate();

            resp.sendRedirect(req.getContextPath() + "/customer/profile?msg=passwordChanged");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/customer/profile?error=changeFail");
        }
    }
}
