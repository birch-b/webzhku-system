package com.taobao.dao;

import java.util.List;
import java.util.Map;

public interface CartDAO {
    List<String[]> listByUserId(Long userId);
    void add(Long userId, Long productId, int quantity);
    void update(Long cartId, int quantity);
    void delete(Long cartId);
    void selectAll(Long userId, int selected);
    void toggleSelect(Long cartId);
    long findExistingCart(Long userId, Long productId);
    Map<String, Object> getProductStatus(Long productId);
}