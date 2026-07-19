package com.taobao.dao.impl;

import com.taobao.dao.AddressDAO;
import com.taobao.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressDAOImpl implements AddressDAO {
    @Override
    public List<Map<String, Object>> listByUserId(Long userId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT id, receiver_name, phone, province, city, district, detail, is_default " +
                "FROM address WHERE user_id = ? ORDER BY is_default DESC, create_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", rs.getLong("id"));
                    m.put("receiverName", rs.getString("receiver_name"));
                    m.put("phone", rs.getString("phone"));
                    m.put("province", rs.getString("province"));
                    m.put("city", rs.getString("city"));
                    m.put("district", rs.getString("district"));
                    m.put("detail", rs.getString("detail"));
                    m.put("isDefault", rs.getInt("is_default"));
                    String fullAddr = rs.getString("province") + rs.getString("city")
                            + rs.getString("district") + rs.getString("detail");
                    m.put("fullAddress", fullAddr);
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询地址列表失败", e);
        }
        return list;
    }

    @Override
    public Map<String, Object> getByIdAndUserId(Long addrId, Long userId) {
        Map<String, Object> data = null;
        String sql = "SELECT * FROM address WHERE id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, addrId);
            ps.setLong(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data = new HashMap<>();
                    data.put("id", rs.getLong("id"));
                    data.put("receiverName", rs.getString("receiver_name"));
                    data.put("phone", rs.getString("phone"));
                    data.put("province", rs.getString("province"));
                    data.put("city", rs.getString("city"));
                    data.put("district", rs.getString("district"));
                    data.put("detail", rs.getString("detail"));
                    data.put("isDefault", rs.getInt("is_default"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询编辑地址失败", e);
        }
        return data;
    }

    @Override
    public void add(Long userId, String receiverName, String phone, String province,
                    String city, String district, String detail, int isDefault) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (isDefault == 1) {
                    try (PreparedStatement ps0 = conn.prepareStatement(
                            "UPDATE address SET is_default = 0 WHERE user_id = ? AND is_default = 1")) {
                        ps0.setLong(1, userId);
                        ps0.executeUpdate();
                    }
                }
                String insertSql = "INSERT INTO address (user_id, receiver_name, phone, province, city, district, detail, is_default) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setLong(1, userId);
                    ps.setString(2, receiverName);
                    ps.setString(3, phone);
                    ps.setString(4, province);
                    ps.setString(5, city);
                    ps.setString(6, district);
                    ps.setString(7, detail);
                    ps.setInt(8, isDefault);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("新增地址失败", e);
        }
    }

    @Override
    public void update(Long addrId, Long userId, String receiverName, String phone,
                       String province, String city, String district, String detail, int isDefault) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (isDefault == 1) {
                    try (PreparedStatement ps0 = conn.prepareStatement(
                            "UPDATE address SET is_default = 0 WHERE user_id = ? AND is_default = 1")) {
                        ps0.setLong(1, userId);
                        ps0.executeUpdate();
                    }
                }
                String updateSql = "UPDATE address SET receiver_name=?, phone=?, province=?, city=?, district=?, detail=?, is_default=? " +
                        "WHERE id=? AND user_id=?";
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setString(1, receiverName);
                    ps.setString(2, phone);
                    ps.setString(3, province);
                    ps.setString(4, city);
                    ps.setString(5, district);
                    ps.setString(6, detail);
                    ps.setInt(7, isDefault);
                    ps.setLong(8, addrId);
                    ps.setLong(9, userId);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("修改地址失败", e);
        }
    }

    @Override
    public void delete(Long addrId, Long userId) {
        String sql = "DELETE FROM address WHERE id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, addrId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("删除地址失败", e);
        }
    }

    @Override
    public void clearDefault(Long userId) {
        String sql = "UPDATE address SET is_default = 0 WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("清除默认地址失败", e);
        }
    }

    @Override
    public void setDefault(Long addrId, Long userId) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps0 = conn.prepareStatement("UPDATE address SET is_default = 0 WHERE user_id = ?")) {
                    ps0.setLong(1, userId);
                    ps0.executeUpdate();
                }

                try (PreparedStatement ps1 = conn.prepareStatement("UPDATE address SET is_default = 1 WHERE id = ? AND user_id = ?")) {
                    ps1.setLong(1, addrId);
                    ps1.setLong(2, userId);
                    ps1.executeUpdate();
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("设置默认地址失败", e);
        }
    }
}
