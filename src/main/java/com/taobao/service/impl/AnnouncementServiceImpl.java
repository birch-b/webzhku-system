package com.taobao.service.impl;

import com.taobao.entity.Announcement;
import com.taobao.service.AnnouncementService;
import com.taobao.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnouncementServiceImpl implements AnnouncementService {

    @Override
    public List<Map<String, Object>> listPublishedAnnouncement() {
        List<Map<String, Object>> announcements = new ArrayList<>();
        String sql = "SELECT id, title, content, priority, create_time, published_at " +
                "FROM announcement WHERE status = 1 ORDER BY priority DESC, create_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> ann = new HashMap<>();
                ann.put("id", rs.getLong("id"));
                ann.put("title", rs.getString("title"));
                String content = rs.getString("content");
                if (content != null && content.length() > 100) {
                    content = content.substring(0, 100) + "...";
                }
                ann.put("summary", content);
                ann.put("priority", rs.getInt("priority"));
                ann.put("createTime", rs.getTimestamp("create_time"));
                ann.put("publishedAt", rs.getTimestamp("published_at"));
                announcements.add(ann);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询公告列表失败", e);
        }
        return announcements;
    }

    @Override
    public Announcement getPublishedAnnouncementById(Long id) {
        Announcement ann = null;
        String sql = "SELECT * FROM announcement WHERE id = ? AND status = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ann = new Announcement();
                ann.setId(rs.getLong("id"));
                ann.setTitle(rs.getString("title"));
                ann.setContent(rs.getString("content"));
                ann.setPriority(rs.getInt("priority"));
                ann.setStatus(rs.getInt("status"));
                ann.setCreateTime(rs.getTimestamp("create_time"));
                ann.setPublishedAt(rs.getTimestamp("published_at"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询公告详情失败", e);
        }
        return ann;
    }

    // 后台：查询所有公告（管理员）
    @Override
    public List<Map<String, Object>> listAllAnnouncement() {
        List<Map<String, Object>> announcements = new ArrayList<>();
        String sql = "SELECT id, title, content, priority, status, create_time, update_time, published_at " +
                "FROM announcement ORDER BY priority DESC, create_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getLong("id"));
                m.put("title", rs.getString("title"));
                m.put("content", rs.getString("content"));
                m.put("priority", rs.getInt("priority"));
                m.put("status", rs.getInt("status"));
                m.put("createTime", rs.getTimestamp("create_time"));
                m.put("updateTime", rs.getTimestamp("update_time"));
                m.put("publishedAt", rs.getTimestamp("published_at"));
                announcements.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("管理员查询公告列表失败", e);
        }
        return announcements;
    }

    // 后台：根据ID查询单条公告用于编辑回显
    @Override
    public Map<String, Object> getAnnouncementById(Long id) {
        Map<String, Object> map = null;
        String sql = "SELECT * FROM announcement WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                map = new HashMap<>();
                map.put("id", rs.getLong("id"));
                map.put("title", rs.getString("title"));
                map.put("content", rs.getString("content"));
                map.put("priority", rs.getInt("priority"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询待编辑公告失败", e);
        }
        return map;
    }

    // 后台：新增/更新公告
    @Override
    public void saveAnnouncement(Long id, String title, String content, int priority, Long operatorId) {
        try (Connection conn = DBUtil.getConnection()) {
            if (id != null) {
                // 更新
                String updateSql = "UPDATE announcement SET title = ?, content = ?, priority = ?, update_time = NOW() WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(updateSql);
                ps.setString(1, title);
                ps.setString(2, content);
                ps.setInt(3, priority);
                ps.setLong(4, id);
                ps.executeUpdate();
            } else {
                // 新增
                String insertSql = "INSERT INTO announcement (title, content, operator_id, priority, status, create_time, update_time, published_at) " +
                        "VALUES (?, ?, ?, ?, 1, NOW(), NOW(), NOW())";
                PreparedStatement ps = conn.prepareStatement(insertSql);
                ps.setString(1, title);
                ps.setString(2, content);
                ps.setLong(3, operatorId);
                ps.setInt(4, priority);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("保存公告失败", e);
        }
    }

    // 后台：删除公告
    @Override
    public void deleteAnnouncement(Long id) {
        String sql = "DELETE FROM announcement WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("删除公告失败", e);
        }
    }
}