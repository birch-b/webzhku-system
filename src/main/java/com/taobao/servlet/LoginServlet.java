package com.taobao.servlet;

import com.taobao.entity.User;
import com.taobao.service.UserService;
import com.taobao.service.impl.UserServiceImpl;
import com.taobao.util.MD5Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录Servlet - 仅负责接收参数、调用业务层、页面跳转
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    // 业务层对象
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // 1. 简单参数校验（属于控制层基础校验，不用下移Service）
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            req.setAttribute("error", "用户名和密码不能为空");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        // 密码加密
        String md5Password = MD5Util.encrypt(password);
        User loginUser;
        try {
            // 2. 调用业务层查询用户
            loginUser = userService.login(username, md5Password);
        } catch (RuntimeException e) {
            req.setAttribute("error", "系统错误，请稍后重试");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        // 3. 处理登录结果
        if (loginUser == null) {
            req.setAttribute("error", "用户名或密码错误");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        // 账号封禁判断（业务规则，也可后续移入Service）
        if (loginUser.getStatus() == 0) {
            req.setAttribute("error", "账号已被封禁，请联系管理员");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        // 4. 存入Session
        HttpSession session = req.getSession();
        session.setAttribute("user", loginUser);
        session.setAttribute("userId", loginUser.getId());
        session.setAttribute("userRole", loginUser.getRole());
        String nick = loginUser.getNickname() != null ? loginUser.getNickname() : loginUser.getUsername();
        session.setAttribute("nickname", nick);

        // 5. 根据角色跳转页面
        String role = loginUser.getRole();
        String ctx = req.getContextPath();
        if ("operator".equals(role)) {
            resp.sendRedirect(ctx + "/admin/stat/dashboard");
        } else if ("shopkeeper".equals(role)) {
            resp.sendRedirect(ctx + "/shop/home");
        } else {
            resp.sendRedirect(ctx + "/");
        }
    }
}