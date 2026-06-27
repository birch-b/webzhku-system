package com.taobao.customer.servlet;

import com.taobao.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 买家售后Servlet
 * 处理售后申请、查看售后记录
 */
@WebServlet("/aftersale/*")
public class AftersaleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            list(req, resp);
        } else if (pathInfo.equals("/apply")) {
            showApplyForm(req, resp);
        } else if (pathInfo.equals("/detail")) {
            detail(req, resp);
        } else {
            list(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.equals("/submit")) {
            submit(req, resp);
        } else {
            resp.sendError(404);
        }
    }

    /**
     * 售后列表页
     */
    private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT a.*, o.order_no, o.shop_id, s.shop_name " +
                         "FROM aftersale a " +
                         "LEFT JOIN `order` o ON a.order_id = o.id " +
                         "LEFT JOIN shop s ON o.shop_id = s.id " +
                         "WHERE a.user_id = ? " +
                         "ORDER BY a.create_time DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> aftersale = new HashMap<>();
                aftersale.put("id", rs.getLong("id"));
                aftersale.put("orderId", rs.getLong("order_id"));
                aftersale.put("orderNo", rs.getString("order_no"));
                aftersale.put("shopName", rs.getString("shop_name"));
                aftersale.put("type", rs.getInt("type"));
                aftersale.put("reason", rs.getString("reason"));
                aftersale.put("amount", rs.getBigDecimal("amount"));
                aftersale.put("status", rs.getInt("status"));
                aftersale.put("shopReply", rs.getString("shop_reply"));
                aftersale.put("createTime", rs.getTimestamp("create_time"));
                aftersale.put("handleTime", rs.getTimestamp("handle_time"));
                list.add(aftersale);
            }
            req.setAttribute("aftersales", list);
            req.getRequestDispatcher("/customer/aftersale_list.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "加载售后列表失败");
            req.getRequestDispatcher("/customer/aftersale_list.jsp").forward(req, resp);
        }
    }

    /**
     * 显示售后申请表单
     */
    private void showApplyForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long orderId = Long.parseLong(req.getParameter("orderId"));
        HttpSession session = req.getSession();
        Long userId = (Long) session.getAttribute("userId");

        try (Connection conn = DBUtil.getConnection()) {
            // 检查订单是否属于该用户且已收货/已完成
            PreparedStatement ps = conn.prepareStatement(
                "SELECT o.*, s.shop_name FROM `order` o " +
                "LEFT JOIN shop s ON o.shop_id = s.id " +
                "WHERE o.id = ? AND o.user_id = ?");
            ps.setLong(1, orderId);
            ps.setLong(2, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int status = rs.getInt("status");
                if (status < 3 || status == 5 || status == 7) {
                    req.setAttribute("error", "该订单不支持售后申请");
                    resp.sendRedirect(req.getContextPath() + "/aftersale");
                    return;
                }
                req.setAttribute("orderId", orderId);
                req.setAttribute("orderNo", rs.getString("order_no"));
                req.setAttribute("shopName", rs.getString("shop_name"));
                req.setAttribute("payAmount", rs.getBigDecimal("pay_amount"));
            } else {
                req.setAttribute("error", "订单不存在");
                resp.sendRedirect(req.getContextPath() + "/aftersale");
                return;
            }
            req.getRequestDispatcher("/customer/aftersale_apply.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "加载申请表单失败");
        }
    }

    /**
     * 提交售后申请
     */
    private void submit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            long orderId = Long.parseLong(req.getParameter("orderId"));
            int type = Integer.parseInt(req.getParameter("type"));
            String reason = req.getParameter("reason");
            double amount = Double.parseDouble(req.getParameter("amount"));

            if (reason == null || reason.trim().isEmpty()) {
                req.setAttribute("error", "请填写申请原因");
                showApplyForm(req, resp);
                return;
            }

            try (Connection conn = DBUtil.getConnection()) {
                String sql = "INSERT INTO aftersale (order_id, user_id, type, reason, amount, status, create_time) " +
                             "VALUES (?, ?, ?, ?, ?, 0, NOW())";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setLong(1, orderId);
                ps.setLong(2, userId);
                ps.setInt(3, type);
                ps.setString(4, reason);
                ps.setDouble(5, amount);
                ps.executeUpdate();

                // 更新订单状态为退款中
                PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE `order` SET status = 6 WHERE id = ?");
                ps2.setLong(1, orderId);
                ps2.executeUpdate();
            }

            session.setAttribute("success", "售后申请已提交，请等待商家处理");
            resp.sendRedirect(req.getContextPath() + "/aftersale");

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "提交失败：" + e.getMessage());
            showApplyForm(req, resp);
        }
    }

    /**
     * 售后详情
     */
    private void detail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("id"));
        HttpSession session = req.getSession();
        Long userId = (Long) session.getAttribute("userId");

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT a.*, o.order_no, o.shop_id, s.shop_name " +
                         "FROM aftersale a " +
                         "LEFT JOIN `order` o ON a.order_id = o.id " +
                         "LEFT JOIN shop s ON o.shop_id = s.id " +
                         "WHERE a.id = ? AND a.user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ps.setLong(2, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Map<String, Object> aftersale = new HashMap<>();
                aftersale.put("id", rs.getLong("id"));
                aftersale.put("orderId", rs.getLong("order_id"));
                aftersale.put("orderNo", rs.getString("order_no"));
                aftersale.put("shopName", rs.getString("shop_name"));
                aftersale.put("type", rs.getInt("type"));
                aftersale.put("reason", rs.getString("reason"));
                aftersale.put("description", rs.getString("description"));
                aftersale.put("amount", rs.getBigDecimal("amount"));
                aftersale.put("status", rs.getInt("status"));
                aftersale.put("shopReply", rs.getString("shop_reply"));
                aftersale.put("createTime", rs.getTimestamp("create_time"));
                aftersale.put("handleTime", rs.getTimestamp("handle_time"));
                req.setAttribute("aftersale", aftersale);
            } else {
                req.setAttribute("error", "售后记录不存在");
            }
            req.getRequestDispatcher("/customer/aftersale_detail.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "加载详情失败");
        }
    }
}
