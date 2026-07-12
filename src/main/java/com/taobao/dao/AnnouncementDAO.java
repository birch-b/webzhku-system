package com.taobao.dao;

import com.taobao.entity.Announcement;

import java.util.List;
import java.util.Map;

public interface AnnouncementDAO {
    List<Map<String, Object>> listPublished();
    Announcement getPublishedById(Long id);
    List<Map<String, Object>> listAll();
    Map<String, Object> getById(Long id);
    void save(Long id, String title, String content, int priority, Long operatorId);
    void delete(Long id);
}