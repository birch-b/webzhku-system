package com.taobao.service.impl;

import com.taobao.dao.StatDAO;
import com.taobao.dao.impl.StatDAOImpl;
import com.taobao.service.StatService;

import java.util.HashMap;
import java.util.Map;

public class StatServiceImpl implements StatService {
    private StatDAO statDAO = new StatDAOImpl();

    @Override
    public Map<String, Object> getDashboardTotalStat() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", statDAO.getTotalUsers());
        stats.put("activeShops", statDAO.getActiveShops());
        stats.put("activeProducts", statDAO.getActiveProducts());
        stats.put("totalOrders", statDAO.getTotalOrders());
        stats.put("totalRevenue", statDAO.getTotalRevenue());
        return stats;
    }

    @Override
    public Map<String, Object> getProductSalesTop10() {
        return statDAO.getProductSalesTop10();
    }

    @Override
    public Map<String, Object> getShopRevenueTop10() {
        return statDAO.getShopRevenueTop10();
    }

    @Override
    public Map<String, Object> getLast12MonthOrderCount() {
        return statDAO.getLast12MonthOrderCount();
    }

    @Override
    public Map<String, Object> getLast12MonthUserGrowth() {
        return statDAO.getLast12MonthUserGrowth();
    }
}