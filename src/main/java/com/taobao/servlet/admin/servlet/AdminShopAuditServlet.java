package com.taobao.servlet.admin.servlet;

import com.taobao.service.ShopService;
import com.taobao.service.impl.ShopServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/shop/*")
public class AdminShopAuditServlet extends HttpServlet {
    private final ShopService shopService = new ShopServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/auditList";
        }
        switch (pathInfo) {
            case "/auditList":
                listPending(req, resp);
                break;
            case "/allShops":
                listAll(req, resp);
                break;
            default:
                listPending(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        // 修复局部变量未初始化编译报错
        String actionPath = "";
        if (pathInfo != null) {
            actionPath = pathInfo;
        }
        switch (actionPath) {
            case "/approve":
                approve(req, resp);
                break;
            case "/reject":
                reject(req, resp);
                break;
            case "/close":
                closeShop(req, resp);
                break;
            default:
                resp.sendError(404);
                break;
        }
    }

    // 查询待审核商家申请列表
    private void listPending(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Map<String, Object>> applies = shopService.listPendingShopApply();
            req.setAttribute("applies", applies);
        } catch (RuntimeException e) {
            e.printStackTrace();
            req.setAttribute("error", "查询审核列表失败");
        }
        req.setAttribute("msg", req.getParameter("msg"));
        req.getRequestDispatcher("/admin/shop_audit.jsp").forward(req, resp);
    }

    // 查询全部已入驻店铺
    private void listAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Map<String, Object>> shops = shopService.listAllShop();
            req.setAttribute("shops", shops);
        } catch (RuntimeException e) {
            e.printStackTrace();
            resp.sendError(500);
            return;
        }
        req.setAttribute("msg", req.getParameter("msg"));
        req.getRequestDispatcher("/admin/shop_audit.jsp").forward(req, resp);
    }

    // 通过店铺入驻审核
    private void approve(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long applyId = Long.parseLong(req.getParameter("id"));
            HttpSession session = req.getSession();
            Long operatorId = (Long) session.getAttribute("userId");
            shopService.approveShopApply(applyId, operatorId);
            resp.sendRedirect(req.getContextPath() + "/admin/shop/auditList?msg=approved");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500);
        }
    }

    // 驳回入驻申请
    private void reject(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long applyId = Long.parseLong(req.getParameter("id"));
            String rejectReason = req.getParameter("reason");
            HttpSession session = req.getSession();
            Long operatorId = (Long) session.getAttribute("userId");
            shopService.rejectShopApply(applyId, rejectReason, operatorId);
            resp.sendRedirect(req.getContextPath() + "/admin/shop/auditList?msg=rejected");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500);
        }
    }

    // 关闭店铺，下架店铺所有商品
    private void closeShop(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long shopId = Long.parseLong(req.getParameter("id"));
            shopService.closeShop(shopId);
            resp.sendRedirect(req.getContextPath() + "/admin/shop/allShops?msg=closed");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500);
        }
    }
}