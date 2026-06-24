package com.taobao.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 店铺实体类
 * 对应数据库表：shop
 */
public class Shop implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String shopName;
    private String shopCategory;
    private String description;
    private String avatar;
    private Integer status;       // 1=营业中, 0=休息中, -1=关闭
    private BigDecimal rating;
    private Integer totalOrders;
    private BigDecimal totalSales;
    private Timestamp createTime;
    private Timestamp updateTime;

    // 扩展字段（用于联表查询展示）
    private String ownerUsername;
    private String ownerNickname;
    private String ownerPhone;

    public Shop() {}

    /** 获取状态中文描述 */
    public String getStatusText() {
        if (status == null) return "未知";
        switch (status) {
            case 1:  return "营业中";
            case 0:  return "休息中";
            case -1: return "已关闭";
            default: return "未知";
        }
    }

    /** 获取状态CSS样式 */
    public String getStatusClass() {
        if (status == null) return "badge-secondary";
        switch (status) {
            case 1:  return "badge-success";
            case 0:  return "badge-warning";
            case -1: return "badge-danger";
            default: return "badge-secondary";
        }
    }

    // --- Getter & Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getShopCategory() { return shopCategory; }
    public void setShopCategory(String shopCategory) { this.shopCategory = shopCategory; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }

    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public String getOwnerNickname() { return ownerNickname; }
    public void setOwnerNickname(String ownerNickname) { this.ownerNickname = ownerNickname; }

    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }
}
