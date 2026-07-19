package com.taobao.service.impl;

import com.taobao.dao.OrderDAO;
import com.taobao.service.OrderService;

import java.util.List;
import java.util.Map;

public class OrderServiceImpl implements OrderService {
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    public String getOrderStatusText(int s) {
        switch (s) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "已发货";
            case 3: return "已收货";
            case 4: return "已完成";
            case 5: return "已取消";
            case 6: return "退款中";
            case 7: return "已退款";
            default: return "未知";
        }
    }

    @Override
    public String getLogisticsStatusText(int ls) {
        return ls == 0 ? "未发货" : ls == 1 ? "运输中" : ls == 2 ? "派送中" : ls == 3 ? "已签收" : "未知";
    }

    @Override
    public List<Map<String, Object>> listAllOrder(String status, String keyword, int page) {
        List<Map<String, Object>> orders = orderDAO.listAllOrder(status, keyword, page);
        for (Map<String, Object> o : orders) {
            o.put("statusText", getOrderStatusText((int) o.get("status")));
        }
        return orders;
    }

    @Override
    public List<Map<String, Object>> listAbnormalOrder(String keyword, int page) {
        List<Map<String, Object>> orders = orderDAO.listAbnormalOrder(keyword, page);
        for (Map<String, Object> o : orders) {
            o.put("statusText", getOrderStatusText((int) o.get("status")));
        }
        return orders;
    }

    @Override
    public Map<String, Object> getOrderDetailById(Long orderId) {
        Map<String, Object> order = orderDAO.getOrderDetailById(orderId);
        if (order != null) {
            order.put("statusText", getOrderStatusText((int) order.get("status")));
            order.put("logisticsStatusText", getLogisticsStatusText((int) order.get("logisticsStatus")));
            order.put("items", orderDAO.listOrderItemByOrderId(orderId));
        }
        return order;
    }

    @Override
    public List<Map<String, Object>> listOrderItemByOrderId(Long orderId) {
        return orderDAO.listOrderItemByOrderId(orderId);
    }
}
