package com.taobao.servlet.admin.servlet;

import com.taobao.service.StatService;
import com.taobao.service.impl.StatServiceImpl;
import com.taobao.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/admin/stat/*")
public class AdminStatServlet extends HttpServlet {
    private final StatService statService = new StatServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/dashboard";
        }
        switch (pathInfo) {
            case "/dashboard":
                showDashboard(req, resp);
                break;
            case "/salesRank":
                salesRank(resp);
                break;
            case "/shopRevenue":
                shopRevenue(resp);
                break;
            case "/monthlyOrders":
                monthlyOrders(resp);
                break;
            case "/userGrowth":
                userGrowth(resp);
                break;
            default:
                showDashboard(req, resp);
        }
    }

    // 后台大盘首页
    private void showDashboard(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Map<String, Object> stats = statService.getDashboardTotalStat();
            req.setAttribute("stats", stats);
        } catch (RuntimeException e) {
            e.printStackTrace();
            req.setAttribute("error", "加载统计数据失败");
        }
        req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);
    }

    // 商品销量TOP10 JSON接口
    private void salesRank(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            Map<String, Object> data = statService.getProductSalesTop10();
            resp.getWriter().write(JsonUtil.toJson(data));
        } catch (RuntimeException e) {
            e.printStackTrace();
            resp.getWriter().write("{\"error\":\"查询失败\"}");
        }
    }

    // 店铺营收TOP10 JSON接口
    private void shopRevenue(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            Map<String, Object> data = statService.getShopRevenueTop10();
            resp.getWriter().write(JsonUtil.toJson(data));
        } catch (RuntimeException e) {
            e.printStackTrace();
            resp.getWriter().write("{\"error\":\"查询失败\"}");
        }
    }

    // 近12月订单统计
    private void monthlyOrders(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            Map<String, Object> data = statService.getLast12MonthOrderCount();
            resp.getWriter().write(JsonUtil.toJson(data));
        } catch (RuntimeException e) {
            e.printStackTrace();
            resp.getWriter().write("{\"error\":\"查询失败\"}");
        }
    }

    // 用户月度增长统计
    private void userGrowth(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            Map<String, Object> data = statService.getLast12MonthUserGrowth();
            resp.getWriter().write(JsonUtil.toJson(data));
        } catch (RuntimeException e) {
            e.printStackTrace();
            resp.getWriter().write("{\"error\":\"查询失败\"}");
        }
    }
}