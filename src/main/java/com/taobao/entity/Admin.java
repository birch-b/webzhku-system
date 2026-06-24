package com.taobao.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员实体类
 */
public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer adminId;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private Integer status;  // 1=正常, 0=禁用
    private Date createdAt;
    private Date lastLogin;
    private String role;    // operator=运营商

    public Admin() {}

    public Admin(Integer adminId, String username) {
        this.adminId = adminId;
        this.username = username;
    }

    // --- Getter & Setter ---
    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getLastLogin() { return lastLogin; }
    public void setLastLogin(Date lastLogin) { this.lastLogin = lastLogin; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    /** 状态文字描述 */
    public String getStatusText() {
        return status != null && status == 1 ? "正常" : "禁用";
    }

    /** 角色文字描述 */
    public String getRoleText() {
        return "operator".equals(role) ? "运营商" : role;
    }
}
