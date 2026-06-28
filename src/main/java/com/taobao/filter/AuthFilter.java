package com.taobao.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 统一权限认证过滤器
 * 拦截所有需要登录才能访问的路径，检查用户是否已登录
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
        HttpSession session = req.getSession(false);

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        // 放行静态资源
        if (path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png")
                || path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".gif")
                || path.endsWith(".ico") || path.endsWith(".woff") || path.endsWith(".woff2")
                || path.endsWith(".ttf") || path.endsWith(".svg")) {
            chain.doFilter(request, response);
            return;
        }

        // 放行公开页面（使用精确路径匹配，避免误拦截）
        if (path.equals("/") || path.equals("/index")
                || path.startsWith("/login") || path.startsWith("/register")
                || path.startsWith("/logout")
                || path.startsWith("/product/")
                || path.startsWith("/announcement/")
                || path.startsWith("/category/")) {
            chain.doFilter(request, response);
            return;
        }

        // 检查登录状态
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        // 将用户信息传递给后续处理
        req.setAttribute("userId", session.getAttribute("userId"));
        req.setAttribute("userRole", session.getAttribute("userRole"));

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("AuthFilter 销毁");
    }
}
