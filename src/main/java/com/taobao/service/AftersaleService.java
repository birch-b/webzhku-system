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
}