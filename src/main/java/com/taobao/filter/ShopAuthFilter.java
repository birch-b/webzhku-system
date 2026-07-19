package com.taobao.filter;

import java.io.IOException;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.taobao.dao.ShopDAO;

/**
 * 商家后台权限拦截过滤器
 * 未开店或未审核的商家无法进入商家后台；同时把店铺名/头像写入 session 供 header.jsp 渲染导航栏
 */
public class ShopAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpSession session = request.getSession();

        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        if (requestURI.endsWith("shop_auditing.jsp") || requestURI.endsWith("shop_closed.jsp")) {
            chain.doFilter(req, resp);
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (path.equals("/shop/apply") || path.startsWith("/shop/apply/")
                || requestURI.endsWith("shop_apply.jsp")) {
            ShopDAO shopDAO = new ShopDAO();
            int shopStatus = shopDAO.getShopStatus(userId);
            if (shopStatus == 1) {
                response.sendRedirect(request.getContextPath() + "/shop/info/view");
                return;
            }
            chain.doFilter(req, resp);
            return;
        }

        ShopDAO shopDAO = new ShopDAO();
        int shopStatus = shopDAO.getShopStatus(userId);

        if (shopStatus == -2) {
            response.sendRedirect(request.getContextPath() + "/shop/shop_apply.jsp");
            return;
        }

        if (shopStatus == 0) {
            request.setAttribute("msg", "您的店铺正在审核中，请耐心等待！");
            request.getRequestDispatcher("/shop/shop_auditing.jsp").forward(request, response);
            return;
        }

        if (shopStatus == -1) {
            request.setAttribute("msg", "您的店铺已被关闭，请联系平台管理员！");
            request.getRequestDispatcher("/shop/shop_closed.jsp").forward(request, response);
            return;
        }

        // ===== status == 1 营业中：把店铺信息写进 session（缺失时才写，ShopInfoServlet 改完会主动更新 session，这里不覆盖） =====
        if (session.getAttribute("shopId") == null
                || session.getAttribute("shopName") == null
                || session.getAttribute("shopAvatar") == null) {
            Map<String, Object> shop = shopDAO.getShopByUserId(userId);
            if (shop != null) {
                session.setAttribute("shopId", shop.get("id"));
                session.setAttribute("shopName", shop.get("shop_name"));
                session.setAttribute("shopAvatar", shop.get("avatar"));
            }
        }

        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
    }
}
