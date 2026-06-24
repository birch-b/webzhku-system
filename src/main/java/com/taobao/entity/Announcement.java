package com.taobao.entity;

import java.sql.Timestamp;

/**
 * 系统公告实体类
 * 对应数据库表：announcement
 */
public class Announcement {
    private Long id;
    private String title;
    private String content;
    private Long operatorId;
    private Integer priority;    // 0=普通, 1=重要
    private Integer status;     // 0=草稿, 1=已发布, 2=已归档
    private Timestamp createTime;
    private Timestamp updateTime;
    private Timestamp publishedAt;

    public Announcement() {}

    /** 获取状态中文描述 */
    public String getStatusText() {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "草稿";
            case 1: return "已发布";
            case 2: return "已归档";
            default: return "未知";
        }
    }

    // Getter and Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }

    public Timestamp getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Timestamp publishedAt) { this.publishedAt = publishedAt; }
}
