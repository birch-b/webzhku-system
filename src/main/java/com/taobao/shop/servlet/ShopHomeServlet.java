package com.taobao.shop.servlet;

import com.taobao.dao.ShopDAO;
import com.taobao.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

@WebServlet("/shop/home")
public class ShopHomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        if (userId != null) {
            ShopDAO shopDAO = new ShopDAO();
            Map<String, Object> shop = shopDAO.getShopByUserId(userId);
            if (shop != null) {
                long shopId = (Long) shop.get("id");
                req.setAttribute("shopId", shopId);
                req.setAttribute("shopName", shop.get("shop_name"));
                req.setAttribute("shopAvatar", shop.get("avatar"));
                req.setAttribute("shopRating", shop.get("rating"));

                // ⚠️ 全部 5 个 KPI = 实时 COUNT 表数据！不依赖 shop 表的冗余统计字段（total_orders/total_products）
                // 冗余字段可能没同步/手动插入订单不会更新，只有实时COUNT才100%准确。
                // 订单表名是 `order`（单数反引号），因为 order 是 MySQL 关键字；全项目 OrderDAO/ShopOrderServlet 都这么用
                try (Connection conn = DBUtil.getConnection()) {
                    // 1. 📦 商品总数 = 实时 COUNT product 表（不依赖 shop 冗余）
                    try {
                        PreparedStatement psProduct = conn.prepareStatement(
                            "SELECT COUNT(*) FROM product WHERE shop_id = ?");
                        psProduct.setLong(1, shopId);
                        ResultSet rsProduct = psProduct.executeQuery();
                        if (rsProduct.next()) req.setAttribute("totalProducts", rsProduct.getInt(1));
                    } catch (Exception e) { e.printStackTrace(); req.setAttribute("totalProducts", 0); }

                    // 2. 🧾 订单总数 = 实时 COUNT `order` 表（不依赖 shop 冗余 total_orders 字段！之前0就是因为冗余没同步）
                    try {
                        PreparedStatement psOrder = conn.prepareStatement(
                            "SELECT COUNT(*) FROM `order` WHERE shop_id = ?");
                        psOrder.setLong(1, shopId);
                        ResultSet rsOrder = psOrder.executeQuery();
                        if (rsOrder.next()) req.setAttribute("totalOrders", rsOrder.getInt(1));
                    } catch (Exception e) { e.printStackTrace(); req.setAttribute("totalOrders", 0); }

                    // 3. 💬 累计评价数 = 直接 COUNT review 表（评价管理页有几条，这里就显示几条！避免用户对「待评价」概念误解，直接显示评价总数，和评价管理页列表数100%一致）
                    try {
                        PreparedStatement psReview = conn.prepareStatement(
                            "SELECT COUNT(*) FROM review WHERE shop_id = ?");
                        psReview.setLong(1, shopId);
                        ResultSet rsReview = psReview.executeQuery();
                        if (rsReview.next()) req.setAttribute("totalReviews", rsReview.getInt(1));
                    } catch (Exception e) { e.printStackTrace(); req.setAttribute("totalReviews", 0); }

                    // 4. 🔁 待处理售后数 = aftersale status < 3（0待审核/1同意待退款/2拒绝待沟通），不包含3=退款完成
                    try {
                        PreparedStatement psAftersale = conn.prepareStatement(
                            "SELECT COUNT(*) FROM aftersale WHERE shop_id = ? AND status < 3");
                        psAftersale.setLong(1, shopId);
                        ResultSet rsAftersale = psAftersale.executeQuery();
                        if (rsAftersale.next()) req.setAttribute("pendingAftersales", rsAftersale.getInt(1));
                    } catch (Exception e) { e.printStackTrace(); req.setAttribute("pendingAftersales", 0); }
                } catch (Exception ignored) { ignored.printStackTrace();
                    // 总兜底：任何连接异常全部 KPI=0，不抛 500
                    req.setAttribute("totalProducts", 0); req.setAttribute("totalOrders", 0);
                    req.setAttribute("totalReviews", 0); req.setAttribute("pendingAftersales", 0);
                }
            }
            shopDAO.close();
        }
        req.getRequestDispatcher("/shop/shop_home.jsp").forward(req, resp);
    }
}
