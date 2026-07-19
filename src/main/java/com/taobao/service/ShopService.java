package com.taobao.service;

import java.util.List;
import java.util.Map;

public interface ShopService {
    // 查询待审核商家申请列表 status=0
    List<Map<String, Object>> listPendingShopApply();

    // 查询全部已入驻商家
    List<Map<String, Object>> listAllShop();

    // 审核通过：创建店铺、更新申请单、修改用户角色为商家
    void approveShopApply(Long applyId, Long operatorId);

    // 驳回申请
    void rejectShopApply(Long applyId, String rejectReason, Long operatorId);

    // 关闭店铺：店铺状态改为-1，旗下商品下架
    void closeShop(Long shopId);

    // 获取店铺状态文本
    String getShopStatusText(int status);

    // 根据店主ID获取店铺名称
    String getShopNameByOwnerId(Long ownerId);

    // 检查用户是否已有店铺
    boolean hasShop(Long userId);

    // 检查用户是否有审核中的申请
    boolean hasPendingApply(Long userId);

    // 提交店铺申请
    void submitShopApply(Long userId, String shopName, String shopCategory, String description,
                          String contactName, String contactPhone);

    // 获取用户的申请信息
    Map<String, Object> getApplyByUserId(Long userId);
}