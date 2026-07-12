package com.taobao.service;

import java.util.List;
import java.util.Map;

public interface StatService {
    // 首页大盘总统计数据
    Map<String, Object> getDashboardTotalStat();

    // 商品销量TOP10
    Map<String, Object> getProductSalesTop10();

    // 店铺营收TOP10
    Map<String, Object> getShopRevenueTop10();

    // 近12个月订单月度统计
    Map<String, Object> getLast12MonthOrderCount();

    // 近12个月用户增长月度统计
    Map<String, Object> getLast12MonthUserGrowth();
}