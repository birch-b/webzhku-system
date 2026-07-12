package com.taobao.service.impl;

import com.taobao.dao.AnnouncementDAO;
import com.taobao.dao.impl.AnnouncementDAOImpl;
import com.taobao.entity.Announcement;
import com.taobao.service.AnnouncementService;

import java.util.List;
import java.util.Map;

public class AnnouncementServiceImpl implements AnnouncementService {
    private AnnouncementDAO announcementDAO = new AnnouncementDAOImpl();

    @Override
    public List<Map<String, Object>> listPublishedAnnouncement() {
        return announcementDAO.listPublished();
    }

    @Override
    public Announcement getPublishedAnnouncementById(Long id) {
        return announcementDAO.getPublishedById(id);
    }

    @Override
    public List<Map<String, Object>> listAllAnnouncement() {
        return announcementDAO.listAll();
    }

    @Override
    public Map<String, Object> getAnnouncementById(Long id) {
        return announcementDAO.getById(id);
    }

    @Override
    public void saveAnnouncement(Long id, String title, String content, int priority, Long operatorId) {
        announcementDAO.save(id, title, content, priority, operatorId);
    }

    @Override
    public void deleteAnnouncement(Long id) {
        announcementDAO.delete(id);
    }
}