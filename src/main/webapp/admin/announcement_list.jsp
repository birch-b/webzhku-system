<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>公告管理 - 运营商后台</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin.css?v=2">
</head>
<body>
<div class="admin-layout">
    <aside class="admin-sidebar">
        <div class="brand">
            <div class="icon">🛡️</div>
            <div class="title">运营商后台</div>
            <div class="subtitle">Operator Console</div>
        </div>
        <nav>
            <a href="${pageContext.request.contextPath}/admin/stat/dashboard">
                <span class="nav-icon">📊</span><span>数据概览</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/user/list">
                <span class="nav-icon">👥</span><span>用户管理</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/shop/auditList">
                <span class="nav-icon">🏪</span><span>店铺审核</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/shop/allShops">
                <span class="nav-icon">🏬</span><span>全部店铺</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/announcement/list" class="active">
                <span class="nav-icon">📢</span><span>公告管理</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/order/list">
                <span class="nav-icon">📦</span><span>订单监控</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/order/abnormal">
                <span class="nav-icon">⚠️</span><span>异常订单</span>
            </a>
        </nav>
        <div class="logout">
            <a href="${pageContext.request.contextPath}/admin/logout">
                <span class="nav-icon">🚪</span><span>退出登录</span>
            </a>
        </div>
    </aside>

    <main class="admin-main">
        <header class="admin-header">
            <div class="page-title">📢 公告管理</div>
            <div class="user-info">
                <div class="text-info">
                    <strong>管理员</strong>
                    <span>Operator</span>
                </div>
                <div class="avatar">管</div>
            </div>
        </header>

        <section class="admin-content">
            <c:if test="${not empty msg}">
                <div class="alert alert-success">
                    <span>✅</span>
                    <span>
                        <c:choose>
                            <c:when test="${msg=='saved'}">公告已成功保存发布</c:when>
                            <c:when test="${msg=='deleted'}">公告已删除</c:when>
                            <c:otherwise>${msg}</c:otherwise>
                        </c:choose>
                    </span>
                </div>
            </c:if>

            <div class="action-bar">
                <a href="${pageContext.request.contextPath}/admin/announcement/edit" class="btn btn-primary">
                    <span>✏️</span><span>发布新公告</span>
                </a>
                <a href="${pageContext.request.contextPath}/announcement/list" target="_blank" class="btn btn-outline">
                    <span>👁️</span><span>前台预览</span>
                </a>
                <div class="ml-auto text-muted">
                    共 <strong style="color:var(--text-primary)">${announcements == null ? 0 : announcements.size()}</strong> 条公告
                </div>
            </div>

            <div class="table-wrapper">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th style="width:80px">ID</th>
                            <th>标题</th>
                            <th style="width:120px">优先级</th>
                            <th style="width:180px">发布时间</th>
                            <th style="width:180px">操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="ann" items="${announcements}">
                            <tr>
                                <td><strong>#${ann.id}</strong></td>
                                <td>
                                    <div style="font-weight:600">${ann.title}</div>
                                    <div class="text-muted" style="font-size:12px;margin-top:4px">
                                        点击编辑查看详情内容
                                    </div>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${ann.priority==1}">
                                            <span class="badge badge-danger">🔥 重要</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-secondary">普通</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-muted">${ann.createTime}</td>
                                <td>
                                    <div class="table-actions">
                                        <a href="${pageContext.request.contextPath}/admin/announcement/edit?id=${ann.id}" class="btn btn-info btn-sm">
                                            <span>✏️</span><span>编辑</span>
                                        </a>
                                        <a href="${pageContext.request.contextPath}/admin/announcement/delete?id=${ann.id}" class="btn btn-danger btn-sm" onclick="return confirm('确定删除该公告？此操作不可撤销')">
                                            <span>🗑️</span><span>删除</span>
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty announcements}">
                            <tr>
                                <td colspan="5">
                                    <div class="empty-state">
                                        <span class="empty-icon">📭</span>
                                        <p>暂无公告，点击上方按钮发布第一条公告</p>
                                    </div>
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <div class="footer-note">
                © 运营商管理系统 · Operator Console
            </div>
        </section>
    </main>
</div>
</body>
</html>
