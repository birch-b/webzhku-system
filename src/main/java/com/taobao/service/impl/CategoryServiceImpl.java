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
}
