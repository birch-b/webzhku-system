package com.taobao.service.impl;

import com.taobao.service.CartService;
import com.taobao.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车业务实现层
 * 所有 SQL + 业务校验（商品下架/超库存/状态判断）都在这里
 * 唯一允许开 Connection / PreparedStatement / ResultSet 的业务层
 * 错误 RuntimeException 上抛，Servlet 捕获后跳转指定页面
 */
public class CartServiceImpl implements CartService {

    /**
     * 加购失败时用此标记前缀抛异常 → Servlet 捕获判断后跳 offshelf
     * （不直接写中文匹配，稳定）
     */
    public static final String ERR_PRODUCT_OFFSHELF = "PRODUCT_OFFSHELF";

    @Override
    public Map<String, Object> listCart(Long userId) {
        Map<String, Object> result = new HashMap<>();
        List<String[]> cartItems = new ArrayList<>();
        double totalAmount = 0;

        String sql = "SELECT ci.*, p.name, p.price, p.cover_image, p.stock, p.status AS pstatus " +
                "FROM cart_item ci LEFT JOIN product p ON ci.product_id = p.id " +
                "WHERE ci.user_id = ? ORDER BY ci.create_time DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int qty = rs.getInt("quantity");
                    double price = rs.getDouble("price");
                    int selected = rs.getInt("selected");
                    if (selected == 1) totalAmount += qty * price;

                    cartItems.add(new String[]{
                            String.valueOf(rs.getLong("id")),           // 0 cart_item_id
                            String.valueOf(rs.getLong("product_id")),   // 1 product_id
                            rs.getString("name"),                        // 2 name
                            rs.getString("price"),                       // 3 price (string form)
                            String.valueOf(qty),                         // 4 quantity
                            String.valueOf(qty * price),                 // 5 subTotal
                            rs.getString("cover_image"),                 // 6 cover_image
                            String.valueOf(selected),                    // 7 selected (0/1)
                            String.valueOf(rs.getInt("stock")),          // 8 stock
                            String.valueOf(rs.getInt("pstatus"))         // 9 product_status
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询购物车失败", e);
        }

        result.put("cartItems", cartItems);
        result.put("totalAmount", totalAmount);
        return result;
    }

    @Override
    public void addToCart(Long userId, Long productId, int quantity) {
        if (quantity <= 0) quantity = 1;   // 非法数量默认 1

        try (Connection conn = DBUtil.getConnection()) {

            // ---- STEP 1: 校验商品状态+库存 ----
            String checkSql = "SELECT status, stock FROM product WHERE id = ?";
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setLong(1, productId);
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next()) {
                        if (rsCheck.getInt("status") != 1) {
                            // ⭐ 商品已下架 → 抛带前缀的 RuntimeException，Servlet 捕获跳 offshelf
                            throw new RuntimeException(ERR_PRODUCT_OFFSHELF);
                        }
                        int stock = rsCheck.getInt("stock");
                        if (quantity > stock) quantity = stock;   // 超库存截短
                        if (quantity <= 0) quantity = 1;          // 库存为0保底 1（至少给用户看）
                    } else {
                        throw new RuntimeException("商品不存在 id=" + productId);
                    }
                }
            }

            // ---- STEP 2: 查找是否已在购物车里（同用户+同商品）----
            long existingCartId = -1;
            try (PreparedStatement psExist = conn.prepareStatement(
                    "SELECT id, quantity FROM cart_item WHERE user_id = ? AND product_id = ?")) {
                psExist.setLong(1, userId);
                psExist.setLong(2, productId);
                try (ResultSet rs = psExist.executeQuery()) {
                    if (rs.next()) existingCartId = rs.getLong("id");
                }
            }

            // ---- STEP 3: 存在 → update qty；不存在 → insert ----
            if (existingCartId > 0) {
                try (PreparedStatement psUpd = conn.prepareStatement(
                        "UPDATE cart_item SET quantity = quantity + ? WHERE id = ?")) {
                    psUpd.setInt(1, quantity);
                    psUpd.setLong(2, existingCartId);
                    psUpd.executeUpdate();
                }
            } else {
                try (PreparedStatement psIns = conn.prepareStatement(
                        "INSERT INTO cart_item (user_id, product_id, quantity, selected, create_time) " +
                                "VALUES (?, ?, ?, 1, NOW())")) {
                    psIns.setLong(1, userId);
                    psIns.setLong(2, productId);
                    psIns.setInt(3, quantity);
                    psIns.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("添加商品到购物车失败", e);
        }
    }

    @Override
    public void updateCart(Long cartId, int quantity) {
        try (Connection conn = DBUtil.getConnection()) {
            if (quantity <= 0) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM cart_item WHERE id = ?")) {
                    ps.setLong(1, cartId);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE cart_item SET quantity = ? WHERE id = ?")) {
                    ps.setInt(1, quantity);
                    ps.setLong(2, cartId);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新购物车失败", e);
        }
    }

    @Override
    public void deleteCart(Long cartId) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM cart_item WHERE id = ?")) {
            ps.setLong(1, cartId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("删除购物车项失败", e);
        }
    }

    @Override
    public void selectAll(Long userId, int selected) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE cart_item SET selected = ? WHERE user_id = ?")) {
            ps.setInt(1, selected);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("全选/取消全选失败", e);
        }
    }

    @Override
    public void toggleSelect(Long cartId) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE cart_item SET selected = IF(selected=1, 0, 1) WHERE id = ?")) {
            ps.setLong(1, cartId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("切换购物车选中状态失败", e);
        }
    }
}
