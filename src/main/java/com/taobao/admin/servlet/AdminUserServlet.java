package com.taobao.admin.servlet;

import com.taobao.entity.User;
import com.taobao.util.DBUtil;
import com.taobao.util.PageUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/user/*")
public class AdminUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/list";
        switch (pathInfo) {
            case "/list": listUsers(req, resp); break;
            case "/detail": showDetail(req, resp); break;
            default: listUsers(req, resp);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";
        switch (pathInfo) {
            case "/ban": banUser(req, resp); break;
            case "/unban": unbanUser(req, resp); break;
            case "/resetPassword": resetPassword(req, resp); break;
            case "/changeRole": changeRole(req, resp); break;
            default: resp.sendError(404);
        }
    }

    private void listUsers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        String role = req.getParameter("role");
        int page = PageUtil.getPage(req);
        int pageSize = PageUtil.getPageSize(req);
        try (Connection conn = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT * FROM user WHERE 1=1");
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (username LIKE ? OR nickname LIKE ? OR phone LIKE ?)");
            }
            if (role != null && !role.trim().isEmpty()) {
                sql.append(" AND role = ?");
            }
            sql.append(" ORDER BY id DESC LIMIT ?, ?");
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(idx++, kw); ps.setString(idx++, kw); ps.setString(idx++, kw);
            }
            if (role != null && !role.trim().isEmpty()) {
                ps.setString(idx++, role);
            }
            ps.setInt(idx++, (page - 1) * pageSize);
            ps.setInt(idx, pageSize);
            ResultSet rs = ps.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()) { users.add(mapUser(rs)); }
            req.setAttribute("users", users);
            req.setAttribute("page", page);
            req.getRequestDispatcher("/admin/user_list.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "查询用户列表失败");
            req.getRequestDispatcher("/admin/user_list.jsp").forward(req, resp);
        }
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM user WHERE id = ?");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                req.setAttribute("userDetail", mapUser(rs));
                req.getRequestDispatcher("/admin/user_detail.jsp").forward(req, resp);
            } else { resp.sendError(404); }
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void banUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long id = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE user SET status = 0 WHERE id = ?");
            ps.setLong(1, id); ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/admin/user/list");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void unbanUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long id = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE user SET status = 1 WHERE id = ?");
            ps.setLong(1, id); ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/admin/user/list");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void resetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long id = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE user SET password = MD5('123456') WHERE id = ?");
            ps.setLong(1, id); ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/admin/user/list?msg=reset");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void changeRole(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long id = Long.parseLong(req.getParameter("id"));
        String role = req.getParameter("role");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE user SET role = ? WHERE id = ?");
            ps.setString(1, role); ps.setLong(2, id); ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/admin/user/list");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setNickname(rs.getString("nickname"));
        u.setPhone(rs.getString("phone"));
        u.setEmail(rs.getString("email"));
        u.setAvatar(rs.getString("avatar"));
        u.setRole(rs.getString("role"));
        u.setStatus(rs.getInt("status"));
        u.setCreateTime(rs.getTimestamp("create_time"));
        u.setUpdateTime(rs.getTimestamp("update_time"));
        return u;
    }
}
