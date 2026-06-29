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
                || path.endsWith(".ttf") || path.endsWith(".svg")
                || path.endsWith(".map")) {
            chain.doFilter(request, response);
            return;
        }

        // ============ 放行所有登录/注册/登出入口 ============
        if (path.startsWith("/login") || path.startsWith("/register") || path.startsWith("/logout")) {
            chain.doFilter(request, response);
            return;
        }

        // ============ 运营商后台登录页放行 ============
        if (path.equals("/admin/login") || path.equals("/admin/admin_login.jsp")
                || path.equals("/admin/logout")) {
            chain.doFilter(request, response);
            return;
        }

        // ============ 商家开店申请页面放行 ============
        if (path.equals("/shop/apply") || path.startsWith("/shop/apply")) {
            chain.doFilter(request, response);
            return;
        }

        // ============ 公开页面：首页、商品浏览、公告、店铺首页 ============
        if (path.equals("/") || path.equals("/index") || path.equals("/index.jsp")
                || path.startsWith("/product/")
                || path.startsWith("/announcement/")
                || path.startsWith("/category/")
                || path.equals("/shop/home") || path.startsWith("/shop/product/")
                || path.startsWith("/shop/info/")
                || path.startsWith("/shop/category/")
                || path.startsWith("/shop/review/")) {
            chain.doFilter(request, response);
            return;
        }

        // ============ 需要登录的检查 ============
        boolean isLoggedIn = (session != null && session.getAttribute("userId") != null);
        if (!isLoggedIn) {
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        // ============ 角色检查：/admin/* 只允许 operator ============
        String userRole = (String) session.getAttribute("userRole");
        if (path.startsWith("/admin/")) {
            if (userRole == null || !userRole.equals("operator")) {
                resp.sendRedirect(contextPath + "/admin/login");
                return;
            }
        }

        // ============ 角色检查：/shop/* 只允许 shopkeeper（ShopAuthFilter 会做更细检查） ============
        // （ShopAuthFilter 会在之后检查店铺状态，这里只保证已登录）

        // 将用户信息传递给后续处理
        req.setAttribute("userId", session.getAttribute("userId"));
        req.setAttribute("userRole", userRole);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("AuthFilter 销毁");
    }
}
