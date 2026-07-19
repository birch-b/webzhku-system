package com.taobao.service.impl;

import com.taobao.dao.ReviewDAO;
import com.taobao.service.ReviewService;

import java.util.List;
import java.util.Map;

public class ReviewServiceImpl implements ReviewService {
    private final ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    public void submitReview(Long orderId, Long userId, Long productId, int rating, String content) {
        reviewDAO.submitReview(orderId, userId, productId, rating, content);
    }

    @Override
    public boolean canReview(Long orderId, Long userId) {
        return reviewDAO.canReview(orderId, userId);
    }

    @Override
    public boolean hasReviewed(Long orderId, Long productId, Long userId) {
        return reviewDAO.hasReviewed(orderId, productId, userId);
    }

    @Override
    public Map<String, Object> getReviewFormData(Long orderId, Long productId, Long userId) {
        return reviewDAO.getReviewFormData(orderId, productId, userId);
    }

    @Override
    public List<Map<String, Object>> listShopReviews(Long shopId) {
        return reviewDAO.listShopReviews(shopId);
    }

    @Override
    public void replyReview(Long reviewId, Long shopId, String reply) {
        reviewDAO.replyReview(reviewId, shopId, reply);
    }
}
