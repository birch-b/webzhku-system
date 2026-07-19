package com.taobao.dao;

import java.util.List;
import java.util.Map;

public interface CartDAO {
    List<String[]> listByUserId(Long userId);
    void add(Long userId, Long productId, int quantity);
    // 修复 IDOR 越权：更新/删除/切换选中均需校验 user_id 归属
    void update(Long cartId, Long userId, int quantity);
    void delete(Long cartId, Long userId);
    void selectAll(Long userId, int selected);
    void toggleSelect(Long cartId, Long userId);
    long findExistingCart(Long userId, Long productId);
    Map<String, Object> getProductStatus(Long productId);
}