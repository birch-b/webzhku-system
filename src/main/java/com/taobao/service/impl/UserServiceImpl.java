package com.taobao.service.impl;

import com.taobao.entity.User;
import com.taobao.service.UserService;
import com.taobao.util.DBUtil;
import com.taobao.util.MD5Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    @Override
    public User login(String username, String md5Pwd) {
        User user = null;
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, md5Pwd);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setNickname(rs.getString("nickname"));
                user.setRole(rs.getString("role"));
                user.setAvatar(rs.getString("avatar"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setStatus(rs.getInt("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("登录查询用户失败", e);
        }
        return user;
    }

    @Override
    public boolean register(String username, String rawPwd, String nickname, String email, String phone) {
        try (Connection conn = DBUtil.getConnection()) {
            // 查重
            PreparedStatement checkPs = conn.prepareStatement("SELECT id FROM user WHERE username = ?");
            checkPs.setString(1, username);
            if (checkPs.executeQuery().next()) {
                return false;
            }
            String md5Pwd = MD5Util.encrypt(rawPwd);
            String insertSql = "INSERT INTO user (username, password, role, nickname, email, phone, status, create_time) " +
                    "VALUES (?, ?, 'customer', ?, ?, ?, 1, NOW())";
            PreparedStatement ps = conn.prepareStatement(insertSql);
            ps.setString(1, username);
            ps.setString(2, md5Pwd);
            ps.setString(3, nickname != null ? nickname : username);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("注册数据库操作异常", e);
        }
    }

    // ===================== 后台管理实现 =====================
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
        List<Map<String, Object>> userList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT u.* FROM user u WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = "%" + keyword.trim() + "%";
            sql.append(" AND (u.username LIKE ? OR u.nickname LIKE ? OR u.phone LIKE ?)");
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        if (role != null && !role.trim().isEmpty()) {
            sql.append(" AND u.role = ?");
            params.add(role);
        }
        sql.append(" ORDER BY u.id DESC LIMIT ?, ?");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> u = new HashMap<>();
                u.put("id", rs.getLong("id"));
                u.put("username", rs.getString("username"));
                u.put("nickname", rs.getString("nickname"));
                u.put("phone", rs.getString("phone"));
                u.put("email", rs.getString("email"));
                String r = rs.getString("role");
                u.put("role", r);
                u.put("roleText", getRoleText(r));
                int st = rs.getInt("status");
                u.put("status", st);
                u.put("statusText", getStatusText(st));
                u.put("createTime", rs.getTimestamp("create_time"));
                userList.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("分页查询用户列表失败", e);
        }
        return userList;
    }

    @Override
    public Map<String, Object> getUserDetailById(Long userId) {
        Map<String, Object> u = null;
        String sql = "SELECT * FROM user WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                u = new HashMap<>();
                u.put("id", rs.getLong("id"));
                u.put("username", rs.getString("username"));
                u.put("nickname", rs.getString("nickname"));
                u.put("phone", rs.getString("phone"));
                u.put("email", rs.getString("email"));
                u.put("avatar", rs.getString("avatar"));
                String r = rs.getString("role");
                u.put("role", r);
                u.put("roleText", getRoleText(r));
                int st = rs.getInt("status");
                u.put("status", st);
                u.put("statusText", getStatusText(st));
                u.put("createTime", rs.getTimestamp("create_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询用户详情失败", e);
        }
        return u;
    }

    @Override
    public void banUser(Long userId) {
        String sql = "UPDATE user SET status = 0 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("封禁用户失败", e);
        }
    }

    @Override
    public void unbanUser(Long userId) {
        String sql = "UPDATE user SET status = 1 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("解封用户失败", e);
        }
    }

    @Override
    public void resetUserPwd(Long userId) {
        String defaultPwd = MD5Util.encrypt("123456");
        String sql = "UPDATE user SET password = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, defaultPwd);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("重置密码失败", e);
        }
    }

    @Override
    public void updateUserRole(Long userId, String newRole) {
        String sql = "UPDATE user SET role = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newRole);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("修改用户角色失败", e);
        }
    }
}