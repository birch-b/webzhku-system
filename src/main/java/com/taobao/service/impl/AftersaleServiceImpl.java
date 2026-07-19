package com.taobao.service.impl;

import com.taobao.dao.AftersaleDAO;
import com.taobao.dao.impl.AftersaleDAOImpl;
import com.taobao.service.AftersaleService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AftersaleServiceImpl implements AftersaleService {
    private AftersaleDAO aftersaleDAO = new AftersaleDAOImpl();

    @Override
    public List<Map<String, Object>> listUserAftersale(Long userId) {
        return aftersaleDAO.listByUserId(userId);
    }

    @Override
    public Map<String, Object> getOrderForAftersale(Long orderId, Long userId) {
        return aftersaleDAO.getOrderForAftersale(orderId, userId);
    }

    @Override
    public void submitAftersale(Long orderId, Long userId, int type, String reason, BigDecimal amount) {
        aftersaleDAO.submit(orderId, userId, type, reason, amount);
    }

    @Override
    public Map<String, Object> getAftersaleDetail(Long aftersaleId, Long userId) {
        return aftersaleDAO.getDetail(aftersaleId, userId);
    }

    // ========== 商家端方法 ==========

    @Override
    public List<Map<String, Object>> listShopAftersales(Long shopId, Integer statusFilter) {
        return aftersaleDAO.listShopAftersales(shopId, statusFilter);
    }

    @Override
    public Map<String, Object> getShopAftersaleDetail(Long id, Long shopId) {
        return aftersaleDAO.getShopAftersaleDetail(id, shopId);
    }

    @Override
    public void approveAftersale(Long id, Long shopId, String reply) {
        aftersaleDAO.approveAftersale(id, shopId, reply);
    }

    @Override
    public void rejectAftersale(Long id, Long shopId, String rejectReason) {
        aftersaleDAO.rejectAftersale(id, shopId, rejectReason);
    }

    @Override
    public void refundAftersale(Long id, Long shopId) {
        aftersaleDAO.refundAftersale(id, shopId);
    }
}