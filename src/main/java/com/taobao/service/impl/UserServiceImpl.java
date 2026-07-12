package com.taobao.service.impl;

import com.taobao.dao.UserDAO;
import com.taobao.dao.impl.UserDAOImpl;
import com.taobao.entity.User;
import com.taobao.service.UserService;
import com.taobao.util.MD5Util;

import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    private UserDAO userDAO = new UserDAOImpl();

    @Override
    public User login(String username, String md5Pwd) {
        return userDAO.login(username, md5Pwd);
    }

    @Override
    public boolean register(String username, String rawPwd, String nickname, String email, String phone) {
        if (userDAO.existsByUsername(username)) {
            return false;
        }
        String md5Pwd = MD5Util.encrypt(rawPwd);
        userDAO.register(username, md5Pwd, nickname, email, phone);
        return true;
    }

    @Override
    public String getRoleText(String r) {
        if ("operator".equals(r)) return "运营商";
        if ("shopkeeper".equals(r)) return "商家";
        if ("customer".equals(r)) return "顾客";
        return "浏览者";
    }

    @Override
    public String getStatusText(int st) {
        return st == 1 ? "正常" : "封禁";
    }

    @Override
    public List<Map<String, Object>> listUserByPage(String keyword, String role, int page, int pageSize) {
        List<Map<String, Object>> userList = userDAO.listByPage(keyword, role, page, pageSize);
        for (Map<String, Object> u : userList) {
            u.put("roleText", getRoleText((String) u.get("role")));
            u.put("statusText", getStatusText((int) u.get("status")));
        }
        return userList;
    }

    @Override
    public Map<String, Object> getUserDetailById(Long userId) {
        Map<String, Object> u = userDAO.getById(userId);
        if (u != null) {
            u.put("roleText", getRoleText((String) u.get("role")));
            u.put("statusText", getStatusText((int) u.get("status")));
        }
        return u;
    }

    @Override
    public void banUser(Long userId) {
        userDAO.updateStatus(userId, 0);
    }

    @Override
    public void unbanUser(Long userId) {
        userDAO.updateStatus(userId, 1);
    }

    @Override
    public void resetUserPwd(Long userId) {
        String defaultPwd = MD5Util.encrypt("123456");
        userDAO.resetPassword(userId, defaultPwd);
    }

    @Override
    public void updateUserRole(Long userId, String newRole) {
        userDAO.updateRole(userId, newRole);
    }
}