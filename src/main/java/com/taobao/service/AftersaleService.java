package com.taobao.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AftersaleService {
    // 查询当前用户全部售后记录
    List<Map<String, Object>> listUserAftersale(Long userId);

    // 获取订单信息用于售后申请表单，并校验订单是否可发起售后
    Map<String, Object> getOrderForAftersale(Long orderId, Long userId);

    // 提交售后申请，同步更新订单状态为退款中 status=6
    void submitAftersale(Long orderId, Long userId, int type, String reason, BigDecimal amount);

    // 根据售后ID+用户ID查询售后详情
    Map<String, Object> getAftersaleDetail(Long aftersaleId, Long userId);

    // ========== 商家端方法 ==========

    // 商家查询售后列表，statusFilter 为 null 时查询全部状态
    List<Map<String, Object>> listShopAftersales(Long shopId, Integer statusFilter);

    // 商家查询售后详情（包含 pay_method）
    Map<String, Object> getShopAftersaleDetail(Long id, Long shopId);

    // 同意售后申请（需校验 shopId 防越权）
    void approveAftersale(Long id, Long shopId, String reply);

    // 拒绝售后申请（事务，需校验 shopId 防越权）
    void rejectAftersale(Long id, Long shopId, String rejectReason);

    // 确认退款（事务，需校验 shopId 防越权）
    void refundAftersale(Long id, Long shopId);
}