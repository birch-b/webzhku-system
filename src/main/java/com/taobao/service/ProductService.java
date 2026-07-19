package com.taobao.service;

import java.util.List;
import java.util.Map;

public interface ProductService {
    // 根据一级分类名查询热销商品，最多12条
    List<Map<String,Object>> listProductByCategory(String categoryName);

    // 分页查询商品列表（支持分类筛选、排序）
    Map<String, Object> listProducts(String categoryId, String sort, int page, int pageSize);

    // 搜索商品（关键词、排序、分页）
    Map<String, Object> searchProducts(String keyword, String sort, int page, int pageSize);

    // 获取商品详情（含评价列表）
    Map<String, Object> getProductDetail(long id);

    // 商家查询商品列表（含分类名）
    List<Map<String, Object>> listShopProducts(long shopId);

    // 商家查询商品详情（用于编辑，校验 shopId 归属防止越权）
    Map<String, Object> getShopProductById(long id, long shopId);

    // 商家更新商品（coverImage/imagesJson 为空时不更新，校验 shopId 归属防止越权）
    boolean updateShopProduct(long id, long shopId, String name, String description, double price,
                              int stock, long categoryId, String coverImage, String imagesJson);

    // 商家新增商品（status=1, publish_time=NOW()），返回新ID
    long createShopProduct(long shopId, long categoryId, String name, String description,
                           double price, int stock, String coverImage, String imagesJson);

    // 更新商品状态（校验 shopId 归属防止越权）
    boolean updateProductStatus(long id, int status, long shopId);
}
