package com.taobao.service;

import com.taobao.entity.Announcement;
import java.util.List;
import java.util.Map;

public interface AnnouncementService {
    // 前台：查询所有上架公告（列表，带摘要）
    List<Map<String, Object>> listPublishedAnnouncement();
    // 前台：根据id查询单条上架公告详情
    Announcement getPublishedAnnouncementById(Long id);

    // 后台管理员接口
    // 查询全部公告（含下架）
    List<Map<String, Object>> listAllAnnouncement();
    // 根据ID查询完整公告（编辑回显）
    Map<String, Object> getAnnouncementById(Long id);
    // 新增/编辑公告
    void saveAnnouncement(Long id, String title, String content, int priority, Long operatorId);
    // 删除公告
    void deleteAnnouncement(Long id);
}