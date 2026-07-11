package com.taobao.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 统一权限认证过滤器
 * 拦截所有需要登录才能访问的路径，检查用户是否已登录以及角色权限
 * 
 * 四类角色权限映射：
 * - browser（浏览者）：仅可访问公开页面（首页、商品浏览、公告）
 * - customer（顾客）：可访问前台买家功能（购物车、订单、支付、评价、地址）
 * - shopkeeper（商家）：可访问商家后台功能（店铺管理、商品管理、订单处理）
 * - operator（运营商）：可访问运营商后台功能（用户管理、店铺审核、数据统计）
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
        if (isStaticResource(path)) {
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

        // ============ 公开页面：浏览者可访问 ============
        // 首页、商品浏览、公告、店铺首页、商品详情等
        if (isPublicPage(path)) {
            chain.doFilter(request, response);
            return;
        }

        // ============ 需要登录的检查 ============
        boolean isLoggedIn = (session != null && session.getAttribute("userId") != null);
        if (!isLoggedIn) {
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        // ============ 角色权限检查 ============
        String userRole = (String) session.getAttribute("userRole");
        
        // 运营商后台：仅 operator 可访问
        if (path.startsWith("/admin/")) {
            if (!"operator".equals(userRole)) {
                resp.sendRedirect(contextPath + "/admin/login");
                return;
            }
        }
        
        // 商家后台：仅 shopkeeper 可访问
        else if (path.startsWith("/shop/")) {
            if (!"shopkeeper".equals(userRole)) {
                resp.sendRedirect(contextPath + "/login");
                return;
            }
        }
        
        // 顾客前台：仅 customer 可访问
        else if (path.startsWith("/customer/") || path.startsWith("/cart/") 
                || path.startsWith("/order/") || path.startsWith("/payment/")
                || path.startsWith("/review/") || path.startsWith("/aftersale/")
                || path.startsWith("/address/")) {
            if (!"customer".equals(userRole)) {
                resp.sendRedirect(contextPath + "/login");
                return;
            }
        }

        // 将用户信息传递给后续处理
        req.setAttribute("userId", session.getAttribute("userId"));
        req.setAttribute("userRole", userRole);

        chain.doFilter(request, response);
    }

    /**
     * 判断是否为静态资源
     */
    private boolean isStaticResource(String path) {
        String lowerPath = path.toLowerCase();
        return lowerPath.endsWith(".css") || lowerPath.endsWith(".js") || lowerPath.endsWith(".png")
                || lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg") || lowerPath.endsWith(".gif")
                || lowerPath.endsWith(".ico") || lowerPath.endsWith(".woff") || lowerPath.endsWith(".woff2")
                || lowerPath.endsWith(".ttf") || lowerPath.endsWith(".svg") || lowerPath.endsWith(".map")
                || lowerPath.endsWith(".html") || lowerPath.endsWith(".json") || lowerPath.endsWith(".txt");
    }

    /**
     * 判断是否为公开页面（浏览者可访问）
     */
    private boolean isPublicPage(String path) {
        return path.equals("/") || path.equals("/index") || path.equals("/index.jsp")
                || path.startsWith("/product/")
                || path.startsWith("/announcement/")
                || path.startsWith("/category/")
                || path.equals("/shop/home") || path.startsWith("/shop/product/")
                || path.startsWith("/shop/info/")
                || path.startsWith("/shop/category/")
                || path.startsWith("/shop/review/")
                || path.startsWith("/checkout");
    }

    @Override
    public void destroy() {
        System.out.println("AuthFilter 销毁");
    }
}