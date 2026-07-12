package com.taobao.service;

import java.util.List;
import java.util.Map;

public interface OrderService {
    /**
     * 分页查询全部订单（支持状态、关键词筛选）
     */
    List<Map<String, Object>> listAllOrder(String status, String keyword, int page);

    /**
     * 分页查询异常订单（5,6,7）
     */
    List<Map<String, Object>> listAbnormalOrder(String keyword, int page);

    /**
     * 根据订单ID查询订单详情+物流
     */
    Map<String, Object> getOrderDetailById(Long orderId);

    /**
     * 根据订单ID查询订单项商品列表
     */
    List<Map<String, Object>> listOrderItemByOrderId(Long orderId);

    /**
     * 订单状态文本转换（业务规则）
     */
    String getOrderStatusText(int status);

    /**
     * 物流状态文本转换
     */
    String getLogisticsStatusText(int logisticsStatus);
}