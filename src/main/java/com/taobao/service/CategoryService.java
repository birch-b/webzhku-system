package com.taobao.service;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    // 查询所有一级分类
    List<Map<String,Object>> listRootCategory();
}