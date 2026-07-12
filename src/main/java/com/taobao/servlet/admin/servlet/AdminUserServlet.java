package com.taobao.servlet.admin.servlet;

import com.taobao.service.UserService;
import com.taobao.service.impl.UserServiceImpl;
import com.taobao.util.PageUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/user/*")
public class AdminUserServlet extends HttpServlet {
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/list";
        }
        switch (pathInfo) {
            case "/list":
                listUsers(req, resp);
                break;
            case "/detail":
                showDetail(req, resp);
                break;
            default:
                listUsers(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        String actionPath = "";
        if (pathInfo != null) {
            actionPath = pathInfo;
        }
        switch (actionPath) {
            case "/ban":
                banUser(req, resp);
                break;
            case "/unban":
                unbanUser(req, resp);
                break;
            case "/resetPassword":
                resetPassword(req, resp);
                break;
            case "/changeRole":
                changeRole(req, resp);
                break;
            default:
                resp.sendError(404);
                break;
        }
    }

    // 用户分页列表
    private void listUsers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        String role = req.getParameter("role");
        int page = PageUtil.getPage(req);
        int pageSize = PageUtil.getPageSize(req);
        try {
            List<Map<String, Object>> users = userService.listUserByPage(keyword, role, page, pageSize);
            req.setAttribute("users", users);
        } catch (RuntimeException e) {
            e.printStackTrace();
            req.setAttribute("error", "查询用户列表失败");
        }
        req.setAttribute("page", page);
        req.setAttribute("keyword", keyword);
        req.setAttribute("role", role);
        req.setAttribute("msg", req.getParameter("msg"));
        req.getRequestDispatcher("/admin/user_list.jsp").forward(req, resp);
    }

    // 用户详情
    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            long id = Long.parseLong(req.getParameter("id"));
            Map<String, Object> userDetail = userService.getUserDetailById(id);
            if (userDetail == null) {
                resp.sendError(404);
                return;
            }
            req.setAttribute("userDetail", userDetail);
        } catch (NumberFormatException e) {
            resp.sendError(400, "用户ID格式错误");
            return;
        } catch (RuntimeException e) {
            e.printStackTrace();
            resp.sendError(500);
            return;
        }
        req.getRequestDispatcher("/admin/user_detail.jsp").forward(req, resp);
    }

    // 封禁用户
    private void banUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long id = Long.parseLong(req.getParameter("id"));
            userService.banUser(id);
            resp.sendRedirect(req.getContextPath() + "/admin/user/list?msg=banned");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500);
        }
    }

    // 解封用户
    private void unbanUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long id = Long.parseLong(req.getParameter("id"));
            userService.unbanUser(id);
            resp.sendRedirect(req.getContextPath() + "/admin/user/list?msg=unbanned");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500);
        }
    }

    // 重置密码
    private void resetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long id = Long.parseLong(req.getParameter("id"));
            userService.resetUserPwd(id);
            resp.sendRedirect(req.getContextPath() + "/admin/user/list?msg=reset");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500);
        }
    }

    // 修改角色
    private void changeRole(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long id = Long.parseLong(req.getParameter("id"));
            String newRole = req.getParameter("role");
            userService.updateUserRole(id, newRole);
            resp.sendRedirect(req.getContextPath() + "/admin/user/list?msg=roleChanged");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500);
        }
    }
}