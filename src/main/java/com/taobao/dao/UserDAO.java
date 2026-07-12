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
}