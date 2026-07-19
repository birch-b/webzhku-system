package com.taobao.dao.impl;

import com.taobao.dao.UserDAO;
import com.taobao.entity.User;
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

public class UserDAOImpl implements UserDAO {
    @Override
    public User login(String username, String rawPwd) {
        User user = null;
        String sql = "SELECT * FROM user WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPwd = rs.getString("password");
                    if (MD5Util.verify(rawPwd, storedPwd)) {
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
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("登录查询用户失败", e);
        }
        return user;
    }

    @Override
    public boolean existsByUsername(String username) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id FROM user WHERE username = ?")) {
            ps.setString(1, username);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询用户名是否存在失败", e);
        }
    }

    @Override
    public void register(String username, String md5Pwd, String nickname, String email, String phone) {
        String insertSql = "INSERT INTO user (username, password, role, nickname, email, phone, status, create_time) " +
                "VALUES (?, ?, 'customer', ?, ?, ?, 1, NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, username);
            ps.setString(2, md5Pwd);
            ps.setString(3, nickname != null ? nickname : username);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("注册数据库操作异常", e);
        }
    }

    @Override
    public List<Map<String, Object>> listByPage(String keyword, String role, int page, int pageSize) {
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
                u.put("role", rs.getString("role"));
                u.put("status", rs.getInt("status"));
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
    public Map<String, Object> getById(Long userId) {
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
                u.put("role", rs.getString("role"));
                u.put("status", rs.getInt("status"));
                u.put("createTime", rs.getTimestamp("create_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询用户详情失败", e);
        }
        return u;
    }

    @Override
    public void updateStatus(Long userId, int status) {
        String sql = "UPDATE user SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新用户状态失败", e);
        }
    }

    @Override
    public void resetPassword(Long userId, String md5Pwd) {
        String sql = "UPDATE user SET password = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, md5Pwd);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("重置密码失败", e);
        }
    }

    @Override
    public void updateRole(Long userId, String role) {
        String sql = "UPDATE user SET role = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("修改用户角色失败", e);
        }
    }
}