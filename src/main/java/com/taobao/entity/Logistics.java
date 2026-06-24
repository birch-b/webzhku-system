package com.taobao.entity;

import java.sql.Timestamp;

/**
 * 物流实体类
 * 对应数据库表：logistics
 */
public class Logistics {
    private Long id;
    private Long orderId;
    private String trackingNo;    // 快递单号
    private String company;       // 快递公司
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private Integer status;       // 0=未发货, 1=运输中, 2=派送中, 3=已签收
    private Timestamp shipTime;
    private Timestamp createTime;
    private Timestamp updateTime;

    public Logistics() {}

    /** 获取物流状态中文描述 */
    public String getStatusText() {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "未发货";
            case 1: return "运输中";
            case 2: return "派送中";
            case 3: return "已签收";
            default: return "未知";
        }
    }

    // Getter and Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getTrackingNo() { return trackingNo; }
    public void setTrackingNo(String trackingNo) { this.trackingNo = trackingNo; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }

    public String getReceiverAddress() { return receiverAddress; }
    public void setReceiverAddress(String receiverAddress) { this.receiverAddress = receiverAddress; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Timestamp getShipTime() { return shipTime; }
    public void setShipTime(Timestamp shipTime) { this.shipTime = shipTime; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }
}
