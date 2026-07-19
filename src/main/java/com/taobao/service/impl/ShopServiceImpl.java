package com.taobao.service.impl;

import com.taobao.dao.ShopDAO;
import com.taobao.service.ShopService;

import java.util.List;
import java.util.Map;

public class ShopServiceImpl implements ShopService {
    private final ShopDAO shopDAO = new ShopDAO();

    @Override
    public String getShopStatusText(int st) {
        return st == 1 ? "营业中" : st == 0 ? "休息中" : "已关闭";
    }

    @Override
    public List<Map<String, Object>> listPendingShopApply() {
        return shopDAO.listPendingShopApply();
    }

    @Override
    public List<Map<String, Object>> listAllShop() {
        List<Map<String, Object>> shops = shopDAO.listAllShop();
        for (Map<String, Object> s : shops) {
            s.put("statusText", getShopStatusText((int) s.get("status")));
        }
        return shops;
    }

    @Override
    public void approveShopApply(Long applyId, Long operatorId) {
        shopDAO.approveShopApply(applyId, operatorId);
    }

    @Override
    public void rejectShopApply(Long applyId, String rejectReason, Long operatorId) {
        shopDAO.rejectShopApply(applyId, rejectReason, operatorId);
    }

    @Override
    public void closeShop(Long shopId) {
        shopDAO.closeShop(shopId);
    }

    @Override
    public String getShopNameByOwnerId(Long ownerId) {
        return shopDAO.getShopNameByOwnerId(ownerId);
    }

    @Override
    public boolean hasShop(Long userId) {
        return shopDAO.hasShop(userId);
    }

    @Override
    public boolean hasPendingApply(Long userId) {
        return shopDAO.hasPendingApply(userId);
    }

    @Override
    public void submitShopApply(Long userId, String shopName, String shopCategory, String description,
                                 String contactName, String contactPhone) {
        shopDAO.submitShopApply(userId, shopName, shopCategory, description, contactName, contactPhone);
    }

    @Override
    public Map<String, Object> getApplyByUserId(Long userId) {
        return shopDAO.getApplyByUserId(userId);
    }
}
