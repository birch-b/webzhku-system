package com.taobao.filter;

import java.io.IOException;
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
 * 未开店或未审核的商家无法进入商家后台
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
        if (requestURI.endsWith("shop_auditing.jsp") || requestURI.endsWith("shop_closed.jsp")) {
            chain.doFilter(req, resp);
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        ShopDAO shopDAO = new ShopDAO();
        int shopStatus = shopDAO.getShopStatus(userId);
        shopDAO.close();

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

        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
    }
}
