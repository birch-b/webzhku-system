package com.taobao.service.impl;

import com.taobao.dao.CartDAO;
import com.taobao.dao.impl.CartDAOImpl;
import com.taobao.service.CartService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartServiceImpl implements CartService {
    public static final String ERR_PRODUCT_OFFSHELF = "PRODUCT_OFFSHELF";
    private CartDAO cartDAO = new CartDAOImpl();

    @Override
    public Map<String, Object> listCart(Long userId) {
        Map<String, Object> result = new HashMap<>();
        List<String[]> cartItems = cartDAO.listByUserId(userId);
        double totalAmount = 0;
        for (String[] ci : cartItems) {
            int qty = Integer.parseInt(ci[4]);
            double price = Double.parseDouble(ci[3]);
            int selected = Integer.parseInt(ci[7]);
            if (selected == 1) totalAmount += qty * price;
        }
        result.put("cartItems", cartItems);
        result.put("totalAmount", totalAmount);
        return result;
    }

    @Override
    public void addToCart(Long userId, Long productId, int quantity) {
        if (quantity <= 0) quantity = 1;

        Map<String, Object> productStatus = cartDAO.getProductStatus(productId);
        if (productStatus == null) {
            throw new RuntimeException("商品不存在 id=" + productId);
        }
        if ((int) productStatus.get("status") != 1) {
            throw new RuntimeException(ERR_PRODUCT_OFFSHELF);
        }
        int stock = (int) productStatus.get("stock");
        if (quantity > stock) quantity = stock;
        if (quantity <= 0) quantity = 1;

        cartDAO.add(userId, productId, quantity);
    }

    @Override
    public void updateCart(Long cartId, Long userId, int quantity) {
        cartDAO.update(cartId, userId, quantity);
    }

    @Override
    public void deleteCart(Long cartId, Long userId) {
        cartDAO.delete(cartId, userId);
    }

    @Override
    public void selectAll(Long userId, int selected) {
        cartDAO.selectAll(userId, selected);
    }

    @Override
    public void toggleSelect(Long cartId, Long userId) {
        cartDAO.toggleSelect(cartId, userId);
    }
}