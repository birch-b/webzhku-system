package com.taobao.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 统一权限认证过滤器
 * 拦截所有需要登录才能访问的路径，检查用户是否已登录
 * 并将用户角色信息传递给后续处理
 * 
 * @author taobao
 * @version 1.0
 */
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthFilter 初始化");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession();

        // 允许静态资源和登录注册页面通过
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        
        // 放行静态资源
        if (uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png") 
            || uri.endsWith(".jpg") || uri.endsWith(".jpeg") || uri.endsWith(".gif")
            || uri.endsWith(".ico") || uri.endsWith(".woff") || uri.endsWith(".woff2")
            || uri.endsWith(".ttf") || uri.endsWith(".svg")) {
            chain.doFilter(request, response);
            return;
        }

        // 放行登录、注册、公告等公开页面
        if (uri.contains("/login") || uri.contains("/register") 
            || uri.contains("/announcement") || uri.contains("/product/list")
            || uri.contains("/category") || uri.equals(contextPath + "/")
            || uri.equals(contextPath)) {
            chain.doFilter(request, response);
            return;
        }

        // 获取当前用户ID
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            // 用户未登录，跳转到登录页面
            resp.sendRedirect(contextPath + "/login.jsp");
            return;
        }

        // 获取用户角色
        String role = (String) session.getAttribute("role");
        req.setAttribute("userId", userId);
        req.setAttribute("role", role);

        // 继续处理请求
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("AuthFilter 销毁");
    }
}
