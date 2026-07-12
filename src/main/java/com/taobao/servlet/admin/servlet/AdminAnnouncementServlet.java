package com.taobao.servlet.admin.servlet;

import com.taobao.service.AnnouncementService;
import com.taobao.service.impl.AnnouncementServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/announcement/*")
public class AdminAnnouncementServlet extends HttpServlet {
    private final AnnouncementService announcementService = new AnnouncementServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/list";
        switch (pathInfo) {
            case "/list":
                listAnnouncements(req, resp);
                break;
            case "/edit":
                showEdit(req, resp);
                break;
            case "/delete":
                delete(req, resp);
                break;
            default:
                listAnnouncements(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";
        switch (pathInfo) {
            case "/save":
                save(req, resp);
                break;
            default:
                resp.sendError(404);
        }
    }

    // 后台公告列表
    private void listAnnouncements(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Map<String, Object>> announcements = announcementService.listAllAnnouncement();
            req.setAttribute("announcements", announcements);
        } catch (RuntimeException e) {
            e.printStackTrace();
            req.setAttribute("error", "查询公告失败");
        }
        req.getRequestDispatcher("/admin/announcement_list.jsp").forward(req, resp);
    }

    // 编辑回显
    private void showEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                Long id = Long.parseLong(idStr);
                Map<String, Object> ann = announcementService.getAnnouncementById(id);
                if (ann != null) {
                    req.setAttribute("annId", ann.get("id"));
                    req.setAttribute("annTitle", ann.get("title"));
                    req.setAttribute("annContent", ann.get("content"));
                    req.setAttribute("annPriority", ann.get("priority"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        req.getRequestDispatcher("/admin/announcement_edit.jsp").forward(req, resp);
    }

    // 新增/保存公告
    private void save(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String idStr = req.getParameter("id");
        String priorityStr = req.getParameter("priority");
        int priority = (priorityStr != null && !priorityStr.isEmpty()) ? Integer.parseInt(priorityStr) : 0;

        HttpSession session = req.getSession();
        Long operatorId = (Long) session.getAttribute("userId");
        Long editId = null;
        if (idStr != null && !idStr.isEmpty()) {
            editId = Long.parseLong(idStr);
        }

        try {
            announcementService.saveAnnouncement(editId, title, content, priority, operatorId);
            resp.sendRedirect(req.getContextPath() + "/admin/announcement/list?msg=saved");
        } catch (RuntimeException e) {
            e.printStackTrace();
            resp.sendError(500);
        }
    }

    // 删除公告
    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long id = Long.parseLong(req.getParameter("id"));
            announcementService.deleteAnnouncement(id);
            resp.sendRedirect(req.getContextPath() + "/admin/announcement/list?msg=deleted");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500);
        }
    }
}