package com.taobao.servlet.customer.servlet;

import com.taobao.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

/**
 * 确认收货Servlet
 * 处理买家确认收货操作，更新订单状态为已收货，同时增加商品销量
 * 使用事务保证数据一致性
 */
@WebServlet("/order/confirm")
public class ConfirmServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        // 登录检查
        Long userId = (Long) req.getSession().getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // 参数校验
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/order/list?error=param");
            return;
        }

        long orderId;
        try {
            orderId = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/order/list?error=param");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. 验证订单归属和状态（只有status=2已发货才能确认收货）
                PreparedStatement checkPs = conn.prepareStatement(
                        "SELECT id, status FROM `order` WHERE id = ? AND user_id = ? FOR UPDATE");
                checkPs.setLong(1, orderId);
                checkPs.setLong(2, userId);
                ResultSet checkRs = checkPs.executeQuery();
                if (!checkRs.next()) {
                    conn.rollback();
                    resp.sendRedirect(req.getContextPath() + "/order/list?error=notFound");
                    return;
                }
                int currentStatus = checkRs.getInt("status");
                if (currentStatus != 2) {
                    conn.rollback();
                    resp.sendRedirect(req.getContextPath() + "/order/list?error=status");
                    return;
                }

                // 2. 更新订单状态为已收货（status=3）
                PreparedStatement ps1 = conn.prepareStatement(
                        "UPDATE `order` SET status = 3, receive_time = NOW() WHERE id = ? AND user_id = ? AND status = 2");
                ps1.setLong(1, orderId);
                ps1.setLong(2, userId);
                int updated = ps1.executeUpdate();
                if (updated == 0) {
                    conn.rollback();
                    resp.sendRedirect(req.getContextPath() + "/order/list?error=status");
                    return;
                }

                // 3. 更新物流状态
                PreparedStatement ps2 = conn.prepareStatement("UPDATE logistics SET status = 3 WHERE order_id = ?");
                ps2.setLong(1, orderId);
                ps2.executeUpdate();

                // 4. 增加商品销量
                PreparedStatement ps3 = conn.prepareStatement(
                        "SELECT product_id, quantity FROM order_item WHERE order_id = ?");
                ps3.setLong(1, orderId);
                ResultSet rs = ps3.executeQuery();
                while (rs.next()) {
                    PreparedStatement ps4 = conn.prepareStatement(
                            "UPDATE product SET sales = sales + ? WHERE id = ?");
                    ps4.setInt(1, rs.getInt("quantity"));
                    ps4.setLong(2, rs.getLong("product_id"));
                    ps4.executeUpdate();
                }

                conn.commit();
                resp.sendRedirect(req.getContextPath() + "/order/detail?id=" + orderId + "&msg=confirmed");
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                resp.sendRedirect(req.getContextPath() + "/order/list?error=fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/order/list?error=fail");
        }
    }
}
