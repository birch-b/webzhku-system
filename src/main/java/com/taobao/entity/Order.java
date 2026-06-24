package com.taobao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 订单实体类
 * 对应数据库表：order
 */
public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long shopId;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;
    private Integer payMethod;     // 1=微信, 2=支付宝, 3=银行卡
    private Integer status;        // 0=待付款, 1=待发货, 2=已发货, 3=已收货, 4=已完成, 5=已取消, 6=退款中, 7=已退款
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String buyerMessage;
    private Timestamp createTime;
    private Timestamp payTime;
    private Timestamp shipTime;
    private Timestamp receiveTime;
    private Timestamp finishTime;

    // 额外字段
    private String shopName;
    private String userNickname;
    private List<OrderItem> items;

    public Order() {}

    /** 获取状态中文描述 */
    public String getStatusText() {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "已发货";
            case 3: return "已收货";
            case 4: return "已完成";
            case 5: return "已取消";
            case 6: return "退款中";
            case 7: return "已退款";
            default: return "未知";
        }
    }

    /** 获取支付方式中文 */
    public String getPayMethodText() {
        if (payMethod == null) return "未支付";
        switch (payMethod) {
            case 1: return "微信支付";
            case 2: return "支付宝";
            case 3: return "银行卡";
            default: return "其他";
        }
    }

    // Getter and Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getPayAmount() { return payAmount; }
    public void setPayAmount(BigDecimal payAmount) { this.payAmount = payAmount; }

    public Integer getPayMethod() { return payMethod; }
    public void setPayMethod(Integer payMethod) { this.payMethod = payMethod; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }

    public String getReceiverAddress() { return receiverAddress; }
    public void setReceiverAddress(String receiverAddress) { this.receiverAddress = receiverAddress; }

    public String getBuyerMessage() { return buyerMessage; }
    public void setBuyerMessage(String buyerMessage) { this.buyerMessage = buyerMessage; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getPayTime() { return payTime; }
    public void setPayTime(Timestamp payTime) { this.payTime = payTime; }

    public Timestamp getShipTime() { return shipTime; }
    public void setShipTime(Timestamp shipTime) { this.shipTime = shipTime; }

    public Timestamp getReceiveTime() { return receiveTime; }
    public void setReceiveTime(Timestamp receiveTime) { this.receiveTime = receiveTime; }

    public Timestamp getFinishTime() { return finishTime; }
    public void setFinishTime(Timestamp finishTime) { this.finishTime = finishTime; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getUserNickname() { return userNickname; }
    public void setUserNickname(String userNickname) { this.userNickname = userNickname; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
