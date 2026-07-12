package com.taobao.servlet.customer.servlet;

import com.taobao.service.CartService;
import com.taobao.service.impl.CartServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 🟢 四层架构纯净版 CartServlet（表示层 · 唯一职责）
 * 只做 4 件事：① 收参 + 基本类型转换  ② 调用 cartService 业务方法
 *            ③ 结果塞 request.setAttribute  ④ try-catch 捕获 RuntimeException → forward/redirect
 *                                                              ↓
 *                                    绝对不出现：DBUtil / Connection / PreparedStatement / ResultSet / SQL
 *                                                              ↓
 *                              业务全在 CartServiceImpl（Service层），SQL 和校验全在那里
 */
@WebServlet("/cart/*")
public class CartServlet extends HttpServlet {

    // Service 实例（Servlet 是单例，复用同一份 Service 即可）
    private final CartService cartService = new CartServiceImpl();

    // ============== GET 分发：list / delete / selectAll / toggle ==============
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            switch (pathInfo) {
                case "/delete":    deleteCart(req, resp);    return;
                case "/selectAll": selectAll(req, resp);     return;
                case "/toggle":    toggleSelect(req, resp);  return;
            }
        }
        // 默认走 list（/cart/list 或 /cart/* 都进这里）
        listCart(req, resp);
    }

    // ============== POST 分发：add / update / delete / selectAll / toggle ==============
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        switch (pathInfo != null ? pathInfo : "") {
            case "/add":       addToCart(req, resp);    break;
            case "/update":    updateCart(req, resp);   break;
            case "/delete":    deleteCart(req, resp);   break;
            case "/selectAll": selectAll(req, resp);    break;
            case "/toggle":    toggleSelect(req, resp); break;
            default:           doGet(req, resp);
        }
    }

    // ======================================================================================
    // 业务方法体 · 全部瘦到 5 行以内（收参 → 调 svc → 放结果/捕获异常 → 跳转）
    // ======================================================================================

    private void listCart(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        try {
            Map<String, Object> data = cartService.listCart(userId);
            req.setAttribute("cartItems",   data.get("cartItems"));
            req.setAttribute("totalAmount", data.get("totalAmount"));
        } catch (RuntimeException ignored) { /* 查询失败：直接 forward 空列表 JSP 兜底 */ }
        req.getRequestDispatcher("/cart.jsp").forward(req, resp);
    }

    private void addToCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId    = (Long) req.getSession().getAttribute("userId");
        long productId = Long.parseLong(req.getParameter("productId"));
        int  quantity  = 1;
        try { quantity = Integer.parseInt(req.getParameter("quantity")); } catch (Exception ignored) {}
        try {
            cartService.addToCart(userId, productId, quantity);
            resp.sendRedirect(req.getContextPath() + "/cart/list");
        } catch (RuntimeException e) {
            // ⭐ Service 抛 "PRODUCT_OFFSHELF" 标记 → 按原逻辑跳详情页带 offshelf msg；其他异常跳回购物车
            if (e.getMessage() != null && e.getMessage().startsWith(CartServiceImpl.ERR_PRODUCT_OFFSHELF)) {
                resp.sendRedirect(req.getContextPath() + "/product/detail?id=" + productId + "&msg=offshelf");
            } else {
                resp.sendRedirect(req.getContextPath() + "/cart/list");
            }
        }
    }

    private void updateCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long cartId   = Long.parseLong(req.getParameter("id"));
        int  quantity = Integer.parseInt(req.getParameter("quantity"));
        try {
            cartService.updateCart(cartId, quantity);
            resp.sendRedirect(req.getContextPath() + "/cart/list");
        } catch (RuntimeException e) { resp.sendError(500); }
    }

    private void deleteCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long cartId = Long.parseLong(req.getParameter("id"));
        try {
            cartService.deleteCart(cartId);
            resp.sendRedirect(req.getContextPath() + "/cart/list");
        } catch (RuntimeException e) { resp.sendError(500); }
    }

    private void selectAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId   = (Long) req.getSession().getAttribute("userId");
        int  selected = Integer.parseInt(req.getParameter("selected"));
        try {
            cartService.selectAll(userId, selected);
            resp.sendRedirect(req.getContextPath() + "/cart/list");
        } catch (RuntimeException e) { resp.sendError(500); }
    }

    private void toggleSelect(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long cartId = Long.parseLong(req.getParameter("id"));
        try {
            cartService.toggleSelect(cartId);
            resp.sendRedirect(req.getContextPath() + "/cart/list");
        } catch (RuntimeException e) { resp.sendError(500); }
    }
}
