package com.taobao.entity;

import java.sql.Timestamp;

/**
 * 商品评价实体类
 * 对应数据库表：review
 */
public class Review {
    private Long id;
    private Long userId;
    private Long productId;
    private Long shopId;
    private Long orderId;
    private String content;
    private Integer rating;        // 1-5评分
    private String images;        // 评价图片，JSON数组
    private String reply;         // 商家回复
    private Timestamp replyTime;  // 商家回复时间
    private Integer status;       // 0=隐藏, 1=显示
    private Timestamp createTime;

    // 额外字段
    private String username;
    private String nickname;
    private String userAvatar;
    private String productName;
    private String productImage;
    private String orderNo;

    public Review() {}

    // Getter and Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public Timestamp getReplyTime() { return replyTime; }
    public void setReplyTime(Timestamp replyTime) { this.replyTime = replyTime; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
}
