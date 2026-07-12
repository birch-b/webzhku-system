package com.taobao.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AftersaleDAO {
    List<Map<String, Object>> listByUserId(Long userId);
    Map<String, Object> getOrderForAftersale(Long orderId, Long userId);
    void submit(Long orderId, Long userId, int type, String reason, BigDecimal amount);
    Map<String, Object> getDetail(Long aftersaleId, Long userId);
}