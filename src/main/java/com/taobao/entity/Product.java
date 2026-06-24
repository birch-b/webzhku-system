package com.taobao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 商品实体类
 * 对应数据库表：product
 */
public class Product {
    private Long id;
    private Long shopId;
    private Long categoryId;
    private String name;
    private String subtitle;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sales;
    private Integer status;      // 0=待上架, 1=上架中, 2=已下架, 3=已售罄
    private String coverImage;
    private String images;       // JSON数组字符串
    private BigDecimal weight;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Timestamp publishTime;

    // 额外字段（不存数据库，用于展示）
    private String categoryName;
    private String shopName;
    private List<String> imageList;  // 解析后的图片列表
    private Double avgRating;        // 平均评分
    private Integer reviewCount;     // 评价数

    public Product() {}

    /** 获取状态中文描述 */
    public String getStatusText() {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待上架";
            case 1: return "上架中";
            case 2: return "已下架";
            case 3: return "已售罄";
            default: return "未知";
        }
    }

    // Getter and Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Integer getSales() { return sales; }
    public void setSales(Integer sales) { this.sales = sales; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }

    public Timestamp getPublishTime() { return publishTime; }
    public void setPublishTime(Timestamp publishTime) { this.publishTime = publishTime; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public List<String> getImageList() { return imageList; }
    public void setImageList(List<String> imageList) { this.imageList = imageList; }

    public Double getAvgRating() { return avgRating; }
    public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
}
