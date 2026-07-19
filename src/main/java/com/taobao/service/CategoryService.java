package com.taobao.service;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    // 查询所有一级分类
    List<Map<String,Object>> listRootCategory();

    // 商家查询店铺所有分类（按sort_order排序）
    List<Map<String, Object>> listShopCategories(long shopId);

    // 商家查询店铺启用中的分类（status=1）
    List<Map<String, Object>> listActiveCategories(long shopId);

    // 商家新增分类
    boolean addCategory(long shopId, String name, long parentId, int sortOrder);

    // 商家更新分类
    boolean updateCategory(long id, String name, int sortOrder, int status, long shopId);

    // 商家硬删除分类（物理删除，需校验 shopId 归属）
    boolean deleteCategory(long id, long shopId);
}
