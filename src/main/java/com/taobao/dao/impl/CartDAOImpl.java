package com.taobao.dao.impl;

import com.taobao.dao.CartDAO;
import com.taobao.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartDAOImpl implements CartDAO {
    @Override
    public List<String[]> listByUserId(Long userId) {
        List<String[]> cartItems = new ArrayList<>();
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
                    cartItems.add(new String[]{
                            String.valueOf(rs.getLong("id")),
                            String.valueOf(rs.getLong("product_id")),
                            rs.getString("name"),
                            rs.getString("price"),
                            String.valueOf(qty),
                            String.valueOf(qty * price),
                            rs.getString("cover_image"),
                            String.valueOf(rs.getInt("selected")),
                            String.valueOf(rs.getInt("stock")),
                            String.valueOf(rs.getInt("pstatus"))
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询购物车失败", e);
        }
        return cartItems;
    }

    @Override
    public void add(Long userId, Long productId, int quantity) {
        try (Connection conn = DBUtil.getConnection()) {
            long existingCartId = findExistingCart(userId, productId);
            if (existingCartId > 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE cart_item SET quantity = quantity + ? WHERE id = ?")) {
                    ps.setInt(1, quantity);
                    ps.setLong(2, existingCartId);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO cart_item (user_id, product_id, quantity, selected, create_time) " +
                                "VALUES (?, ?, ?, 1, NOW())")) {
                    ps.setLong(1, userId);
                    ps.setLong(2, productId);
                    ps.setInt(3, quantity);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("添加商品到购物车失败", e);
        }
    }

    @Override
    public void update(Long cartId, int quantity) {
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
    public void delete(Long cartId) {
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

    @Override
    public long findExistingCart(Long userId, Long productId) {
        long existingCartId = -1;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id, quantity FROM cart_item WHERE user_id = ? AND product_id = ?")) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) existingCartId = rs.getLong("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询购物车项失败", e);
        }
        return existingCartId;
    }

    @Override
    public Map<String, Object> getProductStatus(Long productId) {
        Map<String, Object> result = null;
        String sql = "SELECT status, stock FROM product WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = new HashMap<>();
                    result.put("status", rs.getInt("status"));
                    result.put("stock", rs.getInt("stock"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询商品状态失败", e);
        }
        return result;
    }
}