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
    public User login(String username, String rawPwd) {
        return userDAO.login(username, rawPwd);
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

    @Override
    public Map<String, Object> getProfile(Long userId) {
        // 查询用户个人信息，委托给DAO
        return userDAO.getProfileById(userId);
    }

    @Override
    public void updateProfile(Long userId, String nickname, String phone, String email) {
        // 更新个人信息，委托给DAO
        userDAO.updateProfile(userId, nickname, phone, email);
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        // 修改密码：先验证原密码，验证通过后再更新
        String dbPassword = userDAO.getPasswordById(userId);
        if (dbPassword == null) {
            // 用户不存在
            return false;
        }
        // 验证原密码（BCrypt 每次加密生成不同盐，必须用 verify 比较）
        if (!MD5Util.verify(oldPassword, dbPassword)) {
            // 原密码错误
            return false;
        }
        // 更新新密码
        String newHash = MD5Util.encrypt(newPassword);
        userDAO.updatePassword(userId, newHash);
        return true;
    }
}