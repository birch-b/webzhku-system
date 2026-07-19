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

    @Override
    public Map<String, Object> listProducts(String categoryId, String sort, int page, int pageSize) {
        return productDAO.listProducts(categoryId, sort, page, pageSize);
    }

    @Override
    public Map<String, Object> searchProducts(String keyword, String sort, int page, int pageSize) {
        return productDAO.searchProducts(keyword, sort, page, pageSize);
    }

    @Override
    public Map<String, Object> getProductDetail(long id) {
        return productDAO.getProductDetail(id);
    }

    @Override
    public List<Map<String, Object>> listShopProducts(long shopId) {
        return productDAO.listShopProducts(shopId);
    }

    @Override
    public Map<String, Object> getShopProductById(long id, long shopId) {
        return productDAO.getShopProductById(id, shopId);
    }

    @Override
    public boolean updateShopProduct(long id, long shopId, String name, String description, double price,
                                     int stock, long categoryId, String coverImage, String imagesJson) {
        return productDAO.saveShopProduct(id, shopId, name, description, price, stock, categoryId, coverImage, imagesJson);
    }

    @Override
    public long createShopProduct(long shopId, long categoryId, String name, String description,
                                  double price, int stock, String coverImage, String imagesJson) {
        return productDAO.saveShopProduct(shopId, categoryId, name, description, price, stock, coverImage, imagesJson);
    }

    @Override
    public boolean updateProductStatus(long id, int status, long shopId) {
        return productDAO.updateProductStatus(id, status, shopId);
    }
}
