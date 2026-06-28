package com.taobao.admin.servlet;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            default: listUsers(req, resp); break;
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
            default: resp.sendError(404); break;
        }
    }

    private void listUsers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        String role = req.getParameter("role");
        int page = PageUtil.getPage(req);
        int pageSize = PageUtil.getPageSize(req);
        try (Connection conn = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder(
                "SELECT u.* FROM user u WHERE 1=1");
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (u.username LIKE ? OR u.nickname LIKE ? OR u.phone LIKE ?)");
            }
            if (role != null && !role.trim().isEmpty()) {
                sql.append(" AND u.role = ?");
            }
            sql.append(" ORDER BY u.id DESC LIMIT ?, ?");
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
            List<Map<String, Object>> users = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> u = new HashMap<>();
                u.put("id", rs.getLong("id"));
                u.put("username", rs.getString("username"));
                u.put("nickname", rs.getString("nickname"));
                u.put("phone", rs.getString("phone"));
                u.put("email", rs.getString("email"));
                String r = rs.getString("role");
                u.put("role", r);
                u.put("roleText", "operator".equals(r) ? "运营商" : "shopkeeper".equals(r) ? "商家" : "customer".equals(r) ? "顾客" : "浏览者");
                int st = rs.getInt("status");
                u.put("status", st);
                u.put("statusText", st == 1 ? "正常" : "封禁");
                u.put("createTime", rs.getTimestamp("create_time"));
                users.add(u);
            }
            req.setAttribute("users", users);
            req.setAttribute("page", page);
            req.setAttribute("keyword", keyword);
            req.setAttribute("role", role);
            req.setAttribute("msg", req.getParameter("msg"));
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
                Map<String, Object> u = new HashMap<>();
                u.put("id", rs.getLong("id"));
                u.put("username", rs.getString("username"));
                u.put("nickname", rs.getString("nickname"));
                u.put("phone", rs.getString("phone"));
                u.put("email", rs.getString("email"));
                u.put("avatar", rs.getString("avatar"));
                String r = rs.getString("role");
                u.put("role", r);
                u.put("roleText", "operator".equals(r) ? "运营商" : "shopkeeper".equals(r) ? "商家" : "customer".equals(r) ? "顾客" : "浏览者");
                int st = rs.getInt("status");
                u.put("status", st);
                u.put("statusText", st == 1 ? "正常" : "封禁");
                u.put("createTime", rs.getTimestamp("create_time"));
                req.setAttribute("userDetail", u);
                req.getRequestDispatcher("/admin/user_detail.jsp").forward(req, resp);
            } else { resp.sendError(404); }
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void banUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long id = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE user SET status = 0 WHERE id = ?");
            ps.setLong(1, id); ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/admin/user/list?msg=banned");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void unbanUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long id = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE user SET status = 1 WHERE id = ?");
            ps.setLong(1, id); ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/admin/user/list?msg=unbanned");
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
            resp.sendRedirect(req.getContextPath() + "/admin/user/list?msg=roleChanged");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }
}
