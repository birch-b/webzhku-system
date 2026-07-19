package com.taobao.dao;

import com.taobao.entity.User;

import java.util.List;
import java.util.Map;

public interface UserDAO {
    User login(String username, String md5Pwd);
    boolean existsByUsername(String username);
    void register(String username, String md5Pwd, String nickname, String email, String phone);
    List<Map<String, Object>> listByPage(String keyword, String role, int page, int pageSize);
    Map<String, Object> getById(Long userId);
    void updateStatus(Long userId, int status);
    void resetPassword(Long userId, String md5Pwd);
    void updateRole(Long userId, String role);

    // ========== 个人中心相关方法 ==========
    /**
     * 查询用户个人信息（username, nickname, phone, email）
     */
    Map<String, Object> getProfileById(Long userId);

    /**
     * 更新个人信息（昵称、手机、邮箱）
     */
    void updateProfile(Long userId, String nickname, String phone, String email);

    /**
     * 查询密码（用于修改密码时验证原密码）
     */
    String getPasswordById(Long userId);

    /**
     * 更新密码
     */
    void updatePassword(Long userId, String newMd5Pwd);
}