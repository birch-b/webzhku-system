package com.taobao.entity;

import java.sql.Timestamp;

/**
 * 用户实体类
 * 对应数据库表：user
 */
public class User {
    private Long id;
    private String username;
    private String password;
    private String role;      // browser, customer, shopkeeper, operator
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private Integer status;   // 1=正常, 0=封禁
    private Timestamp createTime;
    private Timestamp updateTime;

    public User() {}

    public User(Long id, String username, String nickname, String role) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
    }

    /** 获取状态中文描述 */
    public String getStatusText() {
        if (status == null) return "未知";
        switch (status) {
            case 1:  return "正常";
            case 0:  return "已封禁";
            default: return "未知";
        }
    }

    /** 获取角色中文描述 */
    public String getRoleText() {
        if (role == null) return "浏览者";
        switch (role) {
            case "operator":   return "运营商";
            case "shopkeeper": return "商家";
            case "customer":   return "顾客";
            case "browser":    return "浏览者";
            default:           return role;
        }
    }

    // Getter and Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }
}
