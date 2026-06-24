package com.taobao.shop.servlet;

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
 * 商家售后处理Servlet
 * 处理买家的售后申请
 */
@WebServlet("/shop/aftersale/*")
public class ShopAftersaleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            list(req, resp);
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
        if (pathInfo == null) {
            resp.sendError(404);
            return;
        }
        switch (pathInfo) {
            case "/approve":
                approve(req, resp);
                break;
            case "/reject":
                reject(req, resp);
                break;
            case "/refund":
                refund(req, resp);
                break;
            default:
                resp.sendError(404);
        }
    }

    /**
     * 获取当前商家ID
     */
    private long getShopId(HttpSession session, Connection conn) throws SQLException {
        Long userId = (Long) session.getAttribute("userId");
        PreparedStatement ps = conn.prepareStatement("SELECT id FROM shop WHERE user_id = ?");
        ps.setLong(1, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getLong("id");
        }
        return -1;
    }

    /**
     * 售后列表
     */
    private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            long shopId = getShopId(session, conn);
            if (shopId < 0) {
                resp.sendRedirect(req.getContextPath() + "/shop/apply");
                return;
            }

            String statusFilter = req.getParameter("status");
            StringBuilder sql = new StringBuilder(
                "SELECT a.*, o.order_no, u.username, u.nickname, u.phone, " +
                "o.receiver_name, o.receiver_phone, o.receiver_address " +
                "FROM aftersale a " +
                "LEFT JOIN `order` o ON a.order_id = o.id " +
                "LEFT JOIN user u ON a.user_id = u.id " +
                "WHERE o.shop_id = ? "
            );
            if (statusFilter != null && !statusFilter.isEmpty()) {
                sql.append("AND a.status = ? ");
            }
            sql.append("ORDER BY a.create_time DESC");

            PreparedStatement ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            ps.setLong(idx++, shopId);
            if (statusFilter != null && !statusFilter.isEmpty()) {
                ps.setInt(idx++, Integer.parseInt(statusFilter));
            }
            ResultSet rs = ps.executeQuery();

            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> aftersale = new HashMap<>();
                aftersale.put("id", rs.getLong("id"));
                aftersale.put("orderId", rs.getLong("order_id"));
                aftersale.put("orderNo", rs.getString("order_no"));
                aftersale.put("userId", rs.getLong("user_id"));
                aftersale.put("username", rs.getString("username"));
                aftersale.put("nickname", rs.getString("nickname"));
                aftersale.put("phone", rs.getString("phone"));
                aftersale.put("type", rs.getInt("type"));
                aftersale.put("reason", rs.getString("reason"));
                aftersale.put("amount", rs.getBigDecimal("amount"));
                aftersale.put("description", rs.getString("description"));
                aftersale.put("status", rs.getInt("status"));
                aftersale.put("shopReply", rs.getString("shop_reply"));
                aftersale.put("createTime", rs.getTimestamp("create_time"));
                aftersale.put("handleTime", rs.getTimestamp("handle_time"));
                aftersale.put("receiverName", rs.getString("receiver_name"));
                aftersale.put("receiverPhone", rs.getString("receiver_phone"));
                aftersale.put("receiverAddress", rs.getString("receiver_address"));
                list.add(aftersale);
            }

            req.setAttribute("aftersales", list);
            req.setAttribute("currentStatus", statusFilter);
            req.getRequestDispatcher("/shop/aftersale_list.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "加载售后列表失败");
            req.getRequestDispatcher("/shop/aftersale_list.jsp").forward(req, resp);
        }
    }

    /**
     * 售后详情
     */
    private void detail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("id"));
        HttpSession session = req.getSession();

        try (Connection conn = DBUtil.getConnection()) {
            long shopId = getShopId(session, conn);

            String sql = "SELECT a.*, o.order_no, o.pay_method, u.username, u.nickname, u.phone, " +
                         "o.receiver_name, o.receiver_phone, o.receiver_address " +
                         "FROM aftersale a " +
                         "LEFT JOIN `order` o ON a.order_id = o.id " +
                         "LEFT JOIN user u ON a.user_id = u.id " +
                         "WHERE a.id = ? AND o.shop_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ps.setLong(2, shopId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Map<String, Object> aftersale = new HashMap<>();
                aftersale.put("id", rs.getLong("id"));
                aftersale.put("orderId", rs.getLong("order_id"));
                aftersale.put("orderNo", rs.getString("order_no"));
                aftersale.put("payMethod", rs.getInt("pay_method"));
                aftersale.put("userId", rs.getLong("user_id"));
                aftersale.put("username", rs.getString("username"));
                aftersale.put("nickname", rs.getString("nickname"));
                aftersale.put("phone", rs.getString("phone"));
                aftersale.put("type", rs.getInt("type"));
                aftersale.put("reason", rs.getString("reason"));
                aftersale.put("description", rs.getString("description"));
                aftersale.put("amount", rs.getBigDecimal("amount"));
                aftersale.put("status", rs.getInt("status"));
                aftersale.put("shopReply", rs.getString("shop_reply"));
                aftersale.put("createTime", rs.getTimestamp("create_time"));
                aftersale.put("handleTime", rs.getTimestamp("handle_time"));
                aftersale.put("receiverName", rs.getString("receiver_name"));
                aftersale.put("receiverPhone", rs.getString("receiver_phone"));
                aftersale.put("receiverAddress", rs.getString("receiver_address"));
                req.setAttribute("aftersale", aftersale);
            } else {
                req.setAttribute("error", "售后记录不存在");
            }
            req.getRequestDispatcher("/shop/aftersale_detail.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "加载详情失败");
        }
    }

    /**
     * 同意售后申请
     */
    private void approve(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long id = Long.parseLong(req.getParameter("id"));
        String reply = req.getParameter("reply");

        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE aftersale SET status = 1, shop_reply = ?, handle_time = NOW() WHERE id = ?");
            ps.setString(1, reply != null ? reply : "商家已同意售后申请");
            ps.setLong(2, id);
            ps.executeUpdate();

            resp.sendRedirect(req.getContextPath() + "/shop/aftersale?msg=approved");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "处理失败");
        }
    }

    /**
     * 拒绝售后申请
     */
    private void reject(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long id = Long.parseLong(req.getParameter("id"));
        String reply = req.getParameter("reply");
        String rejectReason = req.getParameter("rejectReason");

        if (rejectReason == null || rejectReason.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/shop/aftersale/detail?id=" + id + "&error=请填写拒绝原因");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            // 拒绝后恢复订单状态为已完成
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE aftersale SET status = 2, shop_reply = ?, handle_time = NOW() WHERE id = ?");
            ps.setString(1, rejectReason);
            ps.setLong(2, id);
            ps.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(
                "UPDATE `order` SET status = 4 WHERE id = (SELECT order_id FROM aftersale WHERE id = ?)");
            ps2.setLong(1, id);
            ps2.executeUpdate();

            resp.sendRedirect(req.getContextPath() + "/shop/aftersale?msg=rejected");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "处理失败");
        }
    }

    /**
     * 确认退款（模拟）
     */
    private void refund(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long id = Long.parseLong(req.getParameter("id"));

        try (Connection conn = DBUtil.getConnection()) {
            // 模拟退款：更新售后状态为已退款，更新订单状态为已退款
            PreparedStatement ps1 = conn.prepareStatement(
                "UPDATE aftersale SET status = 3, handle_time = NOW() WHERE id = ? AND status = 1");
            ps1.setLong(1, id);
            int updated = ps1.executeUpdate();

            if (updated > 0) {
                PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE `order` SET status = 7 WHERE id = (SELECT order_id FROM aftersale WHERE id = ?)");
                ps2.setLong(1, id);
                ps2.executeUpdate();
            }

            resp.sendRedirect(req.getContextPath() + "/shop/aftersale?msg=refunded");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "退款处理失败");
        }
    }
}
