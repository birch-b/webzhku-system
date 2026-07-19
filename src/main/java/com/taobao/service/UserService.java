package com.taobao.service;

import com.taobao.entity.User;
import java.util.List;
import java.util.Map;

public interface UserService {
    // 前台登录
    User login(String username, String md5Pwd);
    // 前台注册
    boolean register(String username, String rawPwd, String nickname, String email, String phone);

    // ========== 后台管理员用户管理新增方法 ==========
    /**
     * 分页多条件查询用户列表
     */
    List<Map<String, Object>> listUserByPage(String keyword, String role, int page, int pageSize);

    /**
     * 根据ID查询用户详情
     */
    Map<String, Object> getUserDetailById(Long userId);

    /**
     * 封禁用户 status=0
     */
    void banUser(Long userId);

    /**
     * 解封用户 status=1
     */
    void unbanUser(Long userId);

    /**
     * 重置密码为123456（MD5加密）
     */
    void resetUserPwd(Long userId);

    /**
     * 修改用户角色
     */
    void updateUserRole(Long userId, String newRole);

    // 角色中文文本转换
    String getRoleText(String role);
    // 状态中文文本转换
    String getStatusText(int status);

    // ========== 个人中心相关方法 ==========
    /**
     * 查询用户个人信息
     */
    Map<String, Object> getProfile(Long userId);

    /**
     * 更新个人信息（昵称、手机、邮箱）
     */
    void updateProfile(Long userId, String nickname, String phone, String email);

    /**
     * 修改密码（含原密码验证）
     * 验证通过返回 true，失败返回 false
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
}