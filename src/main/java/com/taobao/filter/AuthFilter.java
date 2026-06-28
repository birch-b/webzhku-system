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

        // 放行运营商独立登录入口（即使未登录也允许访问）
        if (uri.contains("/admin/login")) {
            chain.doFilter(request, response);
            return;
        }

        // 放行公开页面：登录、注册、公告、商品浏览、首页
        if (uri.contains("/login.jsp") || uri.contains("/login")
            || uri.contains("/register") || uri.contains("/announcement")
            || uri.contains("/product/list") || uri.contains("/category")
            || uri.contains("/product_detail")
            || uri.equals(contextPath + "/") || uri.equals(contextPath)) {
            chain.doFilter(request, response);
            return;
        }

        // —— 以下路径需要登录 ——
        Long userId = (Long) session.getAttribute("userId");
        String role = (String) session.getAttribute("userRole"); // 与 LoginServlet 统一

        if (userId == null) {
            // 未登录：根据路径区分登录页
            if (uri.contains("/admin/")) {
                resp.sendRedirect(contextPath + "/admin/login");
            } else {
                resp.sendRedirect(contextPath + "/login.jsp");
            }
            return;
        }

        // 角色权限检查
        if (uri.contains("/admin/") && !"operator".equals(role)) {
            // 非运营商访问运营商后台 -> 403
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "无权访问运营商后台");
            return;
        }
        if (uri.contains("/shop/") && !"shopkeeper".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "无权访问商家后台");
            return;
        }

        req.setAttribute("userId", userId);
        req.setAttribute("role", role);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("AuthFilter 销毁");
    }
}
