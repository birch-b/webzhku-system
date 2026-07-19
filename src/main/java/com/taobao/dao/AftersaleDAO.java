package com.taobao.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AftersaleDAO {
    List<Map<String, Object>> listByUserId(Long userId);
    Map<String, Object> getOrderForAftersale(Long orderId, Long userId);
    void submit(Long orderId, Long userId, int type, String reason, BigDecimal amount);
    Map<String, Object> getDetail(Long aftersaleId, Long userId);

    // ========== 商家端方法 ==========

    // 商家查询售后列表，statusFilter 为 null 时查询全部状态
    List<Map<String, Object>> listShopAftersales(Long shopId, Integer statusFilter);

    // 商家查询售后详情（包含 pay_method）
    Map<String, Object> getShopAftersaleDetail(Long id, Long shopId);

    // 同意售后申请，status 置为 1（需校验 shopId 防越权）
    void approveAftersale(Long id, Long shopId, String reply);

    // 拒绝售后申请（事务）：售后 status 置为 2，订单 status 恢复为 4（需校验 shopId 防越权）
    void rejectAftersale(Long id, Long shopId, String rejectReason);

    // 确认退款（事务）：售后 status 置为 3，订单 status 置为 7（需校验 shopId 防越权）
    void refundAftersale(Long id, Long shopId);
}