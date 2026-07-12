package com.taobao.servlet;

import com.taobao.service.UserService;
import com.taobao.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 注册Servlet，仅做参数接收、基础校验、调用业务层、页面转发
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String nickname = req.getParameter("nickname");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");

        // 控制层基础参数校验
        if (username == null || username.trim().isEmpty()) {
            req.setAttribute("error", "用户名不能为空");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }
        if (password == null || password.trim().length() < 6) {
            req.setAttribute("error", "密码长度至少6位");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        try {
            // 调用业务层完成注册逻辑
            boolean registerSuccess = userService.register(username, password, nickname, email, phone);
            if (!registerSuccess) {
                req.setAttribute("error", "用户名已存在");
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
                return;
            }
            // 注册成功跳转登录页
            req.setAttribute("success", "注册成功，请登录");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        } catch (RuntimeException e) {
            e.printStackTrace();
            req.setAttribute("error", "注册失败，请稍后重试");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }
}