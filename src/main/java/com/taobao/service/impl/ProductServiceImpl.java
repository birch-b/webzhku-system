package com.taobao.service.impl;

import com.taobao.dao.ProductDAO;
import com.taobao.service.ProductService;

import java.util.List;
import java.util.Map;

public class ProductServiceImpl implements ProductService {
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    public List<Map<String, Object>> listProductByCategory(String categoryName) {
        return productDAO.listByCategory(categoryName);
    }
}
