<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>用户管理 - 运营商后台</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin.css?v=2">
</head>
<body>
<div class="admin-layout">
    <aside class="admin-sidebar">
        <div class="brand">
            <div class="icon">🛡️</div>
            <div class="title">运营商后台</div>
            <div class="subtitle">Taobao Operator</div>
        </div>
        <nav>
            <a href="${pageContext.request.contextPath}/admin/stat/dashboard"><span class="nav-icon">📊</span><span>数据概览</span></a>
            <a href="${pageContext.request.contextPath}/admin/user/list" class="active"><span class="nav-icon">👥</span><span>用户管理</span></a>
            <a href="${pageContext.request.contextPath}/admin/shop/auditList"><span class="nav-icon">🏪</span><span>店铺审核</span></a>
            <a href="${pageContext.request.contextPath}/admin/shop/allShops"><span class="nav-icon">🏬</span><span>全部店铺</span></a>
            <a href="${pageContext.request.contextPath}/admin/announcement/list"><span class="nav-icon">📢</span><span>公告管理</span></a>
            <a href="${pageContext.request.contextPath}/admin/order/list"><span class="nav-icon">📦</span><span>订单监控</span></a>
            <a href="${pageContext.request.contextPath}/admin/order/abnormal"><span class="nav-icon">⚠️</span><span>异常订单</span></a>
        </nav>
        <div class="logout">
            <a href="${pageContext.request.contextPath}/admin/logout" style="color:rgba(255,255,255,0.8);text-decoration:none;">
                <span class="nav-icon">🚪</span><span>退出登录</span>
            </a>
        </div>
    </aside>

    <main class="admin-main">
        <header class="admin-header">
            <div class="page-title">👥 用户管理</div>
            <div class="user-info">
                <div style="text-align:right;"><strong>${user.nickname}</strong></div>
                <div class="avatar">管</div>
            </div>
        </header>

        <section class="admin-content">

            <c:if test="${not empty param.msg}">
                <div class="alert alert-success">
                    <c:choose>
                        <c:when test="${param.msg == 'banned'}">✅ 已封禁该用户账号。</c:when>
                        <c:when test="${param.msg == 'unbanned'}">✅ 已解封该用户账号。</c:when>
                        <c:when test="${param.msg == 'reset'}">✅ 已重置密码为默认值 (123456)。</c:when>
                        <c:when test="${param.msg == 'roleChanged'}">✅ 角色已变更。</c:when>
                        <c:otherwise>✅ 操作成功。</c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <div class="filter-bar">
                <form method="get" action="${pageContext.request.contextPath}/admin/user/list" style="display:flex;gap:12px;align-items:flex-end;flex-wrap:wrap;">
                    <div class="filter-item">
                        <label>关键字搜索</label>
                        <input type="text" class="form-control" name="keyword" placeholder="用户名 / 昵称 / 手机号" value="${param.keyword}">
                    </div>
                    <div class="filter-item">
                        <label>角色筛选</label>
                        <select class="form-control" name="role">
                            <option value="">全部</option>
                            <option value="customer" ${param.role == 'customer' ? 'selected' : ''}>顾客</option>
                            <option value="shopkeeper" ${param.role == 'shopkeeper' ? 'selected' : ''}>商家</option>
                            <option value="operator" ${param.role == 'operator' ? 'selected' : ''}>运营商</option>
                            <option value="browser" ${param.role == 'browser' ? 'selected' : ''}>浏览者</option>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-primary">🔍 查询</button>
                    <a href="${pageContext.request.contextPath}/admin/user/list" class="btn btn-outline">重置</a>
                </form>
            </div>

            <div class="table-wrapper">
                <table class="admin-table" style="width:100%;">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>用户名</th>
                            <th>昵称</th>
                            <th>手机号</th>
                            <th>邮箱</th>
                            <th>角色</th>
                            <th>状态</th>
                            <th>注册时间</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:if test="${empty requestScope.users}">
                            <tr><td colspan="9" style="text-align:center;padding:40px;color:#95a5a6;">📭 暂无符合条件的用户</td></tr>
                        </c:if>
                        <c:forEach var="u" items="${requestScope.users}">
                            <c:set var="roleText" value="浏览者"/>
                            <c:set var="badgeClass" value="badge-secondary"/>
                            <c:if test="${u.role == 'operator'}">
                                <c:set var="roleText" value="运营商"/>
                                <c:set var="badgeClass" value="badge-primary"/>
                            </c:if>
                            <c:if test="${u.role == 'shopkeeper'}">
                                <c:set var="roleText" value="商家"/>
                                <c:set var="badgeClass" value="badge-info"/>
                            </c:if>
                            <c:if test="${u.role == 'customer'}">
                                <c:set var="roleText" value="顾客"/>
                                <c:set var="badgeClass" value="badge-success"/>
                            </c:if>
                            <tr>
                                <td>${u.id}</td>
                                <td><strong>${u.username != null ? u.username : ''}</strong></td>
                                <td>${u.nickname != null ? u.nickname : '-'}</td>
                                <td>${u.phone != null ? u.phone : '-'}</td>
                                <td>${u.email != null ? u.email : '-'}</td>
                                <td><span class="badge ${badgeClass}">${roleText}</span></td>
                                <td>
                                    <c:if test="${u.status == 1}">
                                        <span class="badge badge-success">正常</span>
                                    </c:if>
                                    <c:if test="${u.status != 1}">
                                        <span class="badge badge-danger">已封禁</span>
                                    </c:if>
                                </td>
                                <td>${u.createTime != null ? u.createTime : '-'}</td>
                                <td>
                                    <a class="btn btn-info btn-sm" href="${pageContext.request.contextPath}/admin/user/detail?id=${u.id}">详情</a>
                                    <c:if test="${u.status == 1}">
                                        <form method="post" action="${pageContext.request.contextPath}/admin/user/ban" style="display:inline;">
                                            <input type="hidden" name="id" value="${u.id}">
                                            <button class="btn btn-warning btn-sm" type="submit" onclick="return confirm('确认封禁该用户？');">封禁</button>
                                        </form>
                                    </c:if>
                                    <c:if test="${u.status != 1}">
                                        <form method="post" action="${pageContext.request.contextPath}/admin/user/unban" style="display:inline;">
                                            <input type="hidden" name="id" value="${u.id}">
                                            <button class="btn btn-success btn-sm" type="submit" onclick="return confirm('确认解封该用户？');">解封</button>
                                        </form>
                                    </c:if>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/user/resetPassword" style="display:inline;">
                                        <input type="hidden" name="id" value="${u.id}">
                                        <button class="btn btn-secondary btn-sm" type="submit" onclick="return confirm('确认重置密码为 123456？');">重置密码</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </section>
    </main>
</div>
</body>
</html>