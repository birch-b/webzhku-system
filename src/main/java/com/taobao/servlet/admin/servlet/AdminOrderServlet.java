package com.taobao.servlet.admin.servlet;

import com.taobao.service.OrderService;
import com.taobao.service.impl.OrderServiceImpl;
import com.taobao.util.PageUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/order/*")
public class AdminOrderServlet extends HttpServlet {
    private final OrderService orderService = new OrderServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/list";
        }
        switch (pathInfo) {
            case "/list":
                listOrders(req, resp);
                break;
            case "/detail":
                showDetail(req, resp);
                break;
            case "/abnormal":
                listAbnormal(req, resp);
                break;
            default:
                listOrders(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        // 修复局部变量未初始化报错
        String actionPath = "";
        if (pathInfo != null) {
            actionPath = pathInfo;
        }
        switch (actionPath) {
            case "/save":
                save(req, resp);
                break;
            default:
                resp.sendError(404);
                break;
        }
    }

    /**
     * 分页查询全部订单
     */
    private void listOrders(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String status = req.getParameter("status");
        String keyword = req.getParameter("keyword");
        int page = PageUtil.getPage(req);
        try {
            List<Map<String, Object>> orders = orderService.listAllOrder(status, keyword, page);
            req.setAttribute("orders", orders);
            req.setAttribute("page", page);
        } catch (RuntimeException e) {
            e.printStackTrace();
            req.setAttribute("error", "查询订单失败");
        }
        req.getRequestDispatcher("/admin/order_list.jsp").forward(req, resp);
    }

    /**
     * 查询异常订单（取消/退款类）
     */
    private void listAbnormal(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        int page = PageUtil.getPage(req);
        try {
            List<Map<String, Object>> orders = orderService.listAbnormalOrder(keyword, page);
            req.setAttribute("orders", orders);
            req.setAttribute("page", page);
            req.setAttribute("abnormal", true);
        } catch (RuntimeException e) {
            e.printStackTrace();
            req.setAttribute("error", "查询异常订单失败");
        }
        req.getRequestDispatcher("/admin/order_list.jsp").forward(req, resp);
    }

    /**
     * 订单详情
     */
    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            long id = Long.parseLong(req.getParameter("id"));
            Map<String, Object> order = orderService.getOrderDetailById(id);
            List<Map<String, Object>> itemList = orderService.listOrderItemByOrderId(id);
            req.setAttribute("order", order);
            req.setAttribute("items", itemList);
        } catch (NumberFormatException e) {
            req.setAttribute("error", "订单ID格式错误");
            req.getRequestDispatcher("/admin/order_list.jsp").forward(req, resp);
            return;
        } catch (RuntimeException e) {
            e.printStackTrace();
            resp.sendError(500, "查询订单详情异常");
            return;
        }
        req.getRequestDispatcher("/admin/order_detail.jsp").forward(req, resp);
    }

    /**
     * 订单保存/修改入口
     */
    private void save(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/admin/order/list?msg=operateSuccess");
    }
}