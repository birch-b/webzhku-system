package com.taobao.admin.servlet;

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

@WebServlet("/admin/announcement/*")
public class AdminAnnouncementServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/list";
        switch (pathInfo) {
            case "/list": listAnnouncements(req, resp); break;
            case "/edit": showEdit(req, resp); break;
            case "/delete": delete(req, resp); break;
            default: listAnnouncements(req, resp);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";
        switch (pathInfo) {
            case "/save": save(req, resp); break;
            default: resp.sendError(404);
        }
    }

    private void listAnnouncements(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM announcement ORDER BY create_time DESC");
            ResultSet rs = ps.executeQuery();
            List<String[]> announcements = new ArrayList<>();
            while (rs.next()) {
                announcements.add(new String[]{
                    String.valueOf(rs.getLong("id")), rs.getString("title"),
                    rs.getString("content"), rs.getTimestamp("create_time").toString()
                });
            }
            req.setAttribute("announcements", announcements);
            req.getRequestDispatcher("/admin/announcement_list.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace(); req.setAttribute("error", "查询公告失败");
            req.getRequestDispatcher("/admin/announcement_list.jsp").forward(req, resp);
        }
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr != null) {
            try (Connection conn = DBUtil.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM announcement WHERE id = ?");
                ps.setLong(1, Long.parseLong(idStr));
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    req.setAttribute("annId", rs.getLong("id"));
                    req.setAttribute("annTitle", rs.getString("title"));
                    req.setAttribute("annContent", rs.getString("content"));
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
        req.getRequestDispatcher("/admin/announcement_edit.jsp").forward(req, resp);
    }

    private void save(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String idStr = req.getParameter("id");
        try (Connection conn = DBUtil.getConnection()) {
            if (idStr != null && !idStr.isEmpty()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE announcement SET title = ?, content = ? WHERE id = ?");
                ps.setString(1, title); ps.setString(2, content); ps.setLong(3, Long.parseLong(idStr));
                ps.executeUpdate();
            } else {
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO announcement (title, content, operator_id, create_time) VALUES (?, ?, ?, NOW())");
                ps.setString(1, title); ps.setString(2, content);
                ps.setLong(3, (Long) req.getSession().getAttribute("userId"));
                ps.executeUpdate();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/announcement/list?msg=saved");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long id = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM announcement WHERE id = ?");
            ps.setLong(1, id); ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/admin/announcement/list?msg=deleted");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }
}
