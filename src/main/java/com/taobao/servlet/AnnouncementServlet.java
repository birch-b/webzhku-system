package com.taobao.servlet;

import com.taobao.entity.Announcement;
import com.taobao.service.AnnouncementService;
import com.taobao.service.impl.AnnouncementServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 公告前台Servlet（公共接口）
 * 仅负责接收请求、调用业务层、页面转发，无任何数据库与业务处理逻辑
 */
@WebServlet("/announcement/*")
public class AnnouncementServlet extends HttpServlet {
    // 声明业务层对象
    private AnnouncementService announcementService = new AnnouncementServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/list")) {
            list(req, resp);
        } else if (pathInfo.equals("/detail")) {
            detail(req, resp);
        } else {
            list(req, resp);
        }
    }

    /**
     * 公告列表页：只调用Service，不操作数据库
     */
    private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // 调用业务层获取数据
            List<Map<String, Object>> announcements = announcementService.listPublishedAnnouncement();
            req.setAttribute("announcements", announcements);
        } catch (RuntimeException e) {
            req.setAttribute("error", "加载公告列表失败");
        }
        // 页面转发
        req.getRequestDispatcher("/announcement_list.jsp").forward(req, resp);
    }

    /**
     * 公告详情页：只调用Service，不操作数据库
     */
    private void detail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            long id = Long.parseLong(req.getParameter("id"));
            // 调用业务层查询
            Announcement ann = announcementService.getPublishedAnnouncementById(id);
            if (ann != null) {
                req.setAttribute("announcement", ann);
            } else {
                req.setAttribute("error", "公告不存在或已下架");
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "公告ID格式错误");
        } catch (RuntimeException e) {
            resp.sendError(500, "加载公告详情失败");
            return;
        }
        req.getRequestDispatcher("/announcement_detail.jsp").forward(req, resp);
    }
}