package com.taobao.dao;

import java.util.List;
import java.util.Map;

public interface StatDAO {
    int getTotalUsers();
    int getActiveShops();
    int getActiveProducts();
    int getTotalOrders();
    double getTotalRevenue();
    Map<String, Object> getProductSalesTop10();
    Map<String, Object> getShopRevenueTop10();
    Map<String, Object> getLast12MonthOrderCount();
    Map<String, Object> getLast12MonthUserGrowth();
}