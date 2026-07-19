<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>平台公告 - 淘宝购物系统</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body { background-color: #f5f5f5; padding-top: 50px; }
        .page-header { margin: 0 0 20px 0; padding-bottom: 10px; border-bottom: 2px solid #e4393c; }
        .announcement-item { background: white; padding: 20px; margin-bottom: 15px; border-radius: 4px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); transition: box-shadow 0.3s; }
        .announcement-item:hover { box-shadow: 0 4px 8px rgba(0,0,0,0.15); }
        .announcement-item h4 { margin-top: 0; }
        .announcement-item h4 a { color: #333; text-decoration: none; }
        .announcement-item h4 a:hover { color: #e4393c; }
        .priority-badge { background: #e4393c; color: white; padding: 2px 8px; border-radius: 3px; font-size: 12px; margin-right: 10px; }
        .normal-badge { background: #999; color: white; padding: 2px 8px; border-radius: 3px; font-size: 12px; margin-right: 10px; }
        .announcement-summary { color: #666; margin: 10px 0; }
        .announcement-meta { color: #999; font-size: 13px; }
        .empty-msg { text-align: center; padding: 50px; color: #999; }
    </style>
</head>
<body>
    <%@ include file="header.jsp"%>

    <div class="container">
        <div class="row">
            <div class="col-md-9">
                <h2 class="page-header">
                    <span class="glyphicon glyphicon-bullhorn"></span> 平台公告
                </h2>

                <c:choose>
                    <c:when test="${not empty announcements}">
                        <c:forEach var="ann" items="${announcements}">
                            <div class="announcement-item">
                                <h4>
                                    <c:choose>
                                        <c:when test="${ann.priority == 1}">
                                            <span class="priority-badge">重要</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="normal-badge">普通</span>
                                        </c:otherwise>
                                    </c:choose>
                                    <a href="${pageContext.request.contextPath}/announcement/detail?id=${ann.id}">
                                            <c:out value="${ann.title}"/>
                                    </a>
                                </h4>
                                <div class="announcement-summary">
                                        <c:out value="${ann.summary}"/>
                                </div>
                                <div class="announcement-meta">
                                    <span class="glyphicon glyphicon-time"></span>
                                    <fmt:formatDate value="${ann.publishedAt}" pattern="yyyy-MM-dd HH:mm"/>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-msg">
                            <p class="text-muted">暂无公告</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- 右侧边栏 -->
            <div class="col-md-3">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <span class="glyphicon glyphicon-info-sign"></span> 帮助中心
                        </h4>
                    </div>
                    <div class="panel-body">
                        <ul class="list-unstyled">
                            <li><a href="#">购物指南</a></li>
                            <li><a href="#">支付方式</a></li>
                            <li><a href="#">配送方式</a></li>
                            <li><a href="#">售后服务</a></li>
                            <li><a href="#">联系客服</a></li>
                        </ul>
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
