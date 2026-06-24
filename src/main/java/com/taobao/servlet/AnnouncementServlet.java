package com.taobao.servlet;

import com.taobao.entity.Announcement;
import com.taobao.util.DBUtil;

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

/**
 * 公告前台Servlet（公共接口）
 * 公告列表和详情页，前台所有用户都可以访问
 */
@WebServlet("/announcement/*")
public class AnnouncementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            list(req, resp);
        } else if (pathInfo.equals("/detail")) {
            detail(req, resp);
        } else if (pathInfo.equals("/list")) {
            list(req, resp);
        } else {
            list(req, resp);
        }
    }

    /**
     * 公告列表页
     */
    private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT id, title, content, priority, create_time, published_at " +
                         "FROM announcement WHERE status = 1 ORDER BY priority DESC, create_time DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            List<Map<String, Object>> announcements = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> ann = new HashMap<>();
                ann.put("id", rs.getLong("id"));
                ann.put("title", rs.getString("title"));
                // 摘要：截取前100个字符
                String content = rs.getString("content");
                if (content != null && content.length() > 100) {
                    content = content.substring(0, 100) + "...";
                }
                ann.put("summary", content);
                ann.put("priority", rs.getInt("priority"));
                ann.put("createTime", rs.getTimestamp("create_time"));
                ann.put("publishedAt", rs.getTimestamp("published_at"));
                announcements.add(ann);
            }
            req.setAttribute("announcements", announcements);
            req.getRequestDispatcher("/announcement_list.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "加载公告列表失败");
            req.getRequestDispatcher("/announcement_list.jsp").forward(req, resp);
        }
    }

    /**
     * 公告详情页
     */
    private void detail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM announcement WHERE id = ? AND status = 1");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Announcement ann = new Announcement();
                ann.setId(rs.getLong("id"));
                ann.setTitle(rs.getString("title"));
                ann.setContent(rs.getString("content"));
                ann.setPriority(rs.getInt("priority"));
                ann.setStatus(rs.getInt("status"));
                ann.setCreateTime(rs.getTimestamp("create_time"));
                ann.setPublishedAt(rs.getTimestamp("published_at"));
                req.setAttribute("announcement", ann);
            } else {
                req.setAttribute("error", "公告不存在或已下架");
            }
            req.getRequestDispatcher("/announcement_detail.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "加载公告详情失败");
        }
    }
}
