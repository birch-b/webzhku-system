<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${announcement.title} - 公告详情 - 淘宝购物系统</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body { background-color: #f5f5f5; padding-top: 50px; }
        .announcement-detail { background: white; padding: 30px; border-radius: 4px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .announcement-title { font-size: 24px; font-weight: bold; margin-bottom: 20px; color: #333; }
        .announcement-meta { color: #999; font-size: 14px; margin-bottom: 30px; padding-bottom: 15px; border-bottom: 1px solid #eee; }
        .announcement-meta span { margin-right: 20px; }
        .announcement-content { font-size: 16px; line-height: 1.8; color: #333; }
        .announcement-content p { margin-bottom: 15px; }
        .priority-badge { color: #e4393c; font-weight: bold; }
        .back-btn { margin-top: 20px; }
    </style>
</head>
<body>
    <%@ include file="header.jsp"%>

    <div class="container">
        <div class="row">
            <div class="col-md-8 col-md-offset-2">
                <div class="announcement-detail">
                    <c:choose>
                        <c:when test="${not empty announcement}">
                            <div class="announcement-title">
                                <c:if test="${announcement.priority == 1}">
                                    <span class="priority-badge">[重要]</span>
                                </c:if>
                                ${announcement.title}
                            </div>
                            <div class="announcement-meta">
                                <span>发布时间：<fmt:formatDate value="${announcement.publishedAt}" pattern="yyyy-MM-dd HH:mm"/></span>
                                <span>发布者：平台管理员</span>
                            </div>
                            <div class="announcement-content">
                                ${announcement.content}
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-warning">${error}</div>
                        </c:otherwise>
                    </c:choose>

                    <div class="back-btn">
                        <a href="${pageContext.request.contextPath}/announcement/list" class="btn btn-default">
                            &larr; 返回公告列表
                        </a>
                        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                            &larr; 返回首页
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%@ include file="footer.jsp"%>

    <script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>
