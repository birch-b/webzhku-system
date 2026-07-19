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

    /**
     * 获取结算页面数据（购物车选中商品+地址列表）
     */
    Map<String, Object> getCheckoutData(Long userId);

    /**
     * 创建订单（事务）
     * @return orderId
     */
    long createOrder(Long userId, Long addressId, String buyerMessage);

    /**
     * 查询用户订单列表（分页、状态筛选）
     */
    Map<String, Object> listUserOrders(Long userId, String status, int page, int pageSize);

    /**
     * 获取订单详情（包含订单项、售后状态、评价状态）
     */
    Map<String, Object> getUserOrderDetail(Long orderId, Long userId);

    /**
     * 取消订单（事务）
     */
    void cancelOrder(Long orderId, Long userId);

    /**
     * 确认收货（事务）
     */
    void confirmOrder(Long orderId, Long userId);

    /**
     * 支付订单
     */
    boolean payOrder(Long orderId, Long userId, int payMethod);

    /**
     * 商家查询订单列表（无分页，按状态可选过滤）
     */
    List<Map<String, Object>> listShopOrders(Long shopId, Integer status);

    /**
     * 商家查询订单详情（含物流信息）
     * 越权防护：必须传入 shopId 校验订单归属
     */
    Map<String, Object> getShopOrderDetail(Long orderId, Long shopId);

    /**
     * 查询订单项商品列表
     */
    List<Map<String, Object>> getOrderItemsByOrderId(Long orderId);

    /**
     * 商家发货（事务）：更新订单状态 + 写入/更新物流记录
     * 越权防护：必须传入 shopId 校验订单归属
     */
    void shipOrder(Long orderId, Long shopId, String company, String trackingNo);

    /**
     * 商家取消订单（事务，恢复库存）
     */
    void cancelShopOrder(Long orderId, Long shopId);
}