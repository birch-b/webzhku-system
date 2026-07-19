package com.taobao.service.impl;

import com.taobao.dao.CategoryDAO;
import com.taobao.service.CategoryService;

import java.util.List;
import java.util.Map;

public class CategoryServiceImpl implements CategoryService {
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    public List<Map<String, Object>> listRootCategory() {
        return categoryDAO.listRootCategory();
    }

    @Override
    public List<Map<String, Object>> listShopCategories(long shopId) {
        return categoryDAO.getAllCategoriesByShopId(shopId);
    }

    @Override
    public List<Map<String, Object>> listActiveCategories(long shopId) {
        return categoryDAO.getCategoriesByShopId(shopId);
    }

    @Override
    public boolean addCategory(long shopId, String name, long parentId, int sortOrder) {
        return categoryDAO.addCategory(shopId, name, parentId, sortOrder);
    }

    @Override
    public boolean updateCategory(long id, String name, int sortOrder, int status, long shopId) {
        return categoryDAO.updateCategory(id, name, sortOrder, status, shopId);
    }

    @Override
    public boolean deleteCategory(long id, long shopId) {
        return categoryDAO.deleteCategoryHard(id, shopId);
    }
}
