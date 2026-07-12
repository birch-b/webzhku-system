package com.taobao.service;

import java.util.List;
import java.util.Map;

public interface ProductService {
    // 根据一级分类名查询热销商品，最多12条
    List<Map<String,Object>> listProductByCategory(String categoryName);
}