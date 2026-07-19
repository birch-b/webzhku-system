package com.taobao.service;

import java.util.List;
import java.util.Map;

public interface ReviewService {
    void submitReview(Long orderId, Long userId, Long productId, int rating, String content);

    boolean canReview(Long orderId, Long userId);

    boolean hasReviewed(Long orderId, Long productId, Long userId);

    Map<String, Object> getReviewFormData(Long orderId, Long productId, Long userId);

    // 商家查询店铺所有评价（无分页，无status过滤）
    List<Map<String, Object>> listShopReviews(Long shopId);

    // 商家回复评价（需传入 shopId 进行越权校验）
    void replyReview(Long reviewId, Long shopId, String reply);
}
