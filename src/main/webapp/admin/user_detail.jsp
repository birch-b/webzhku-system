<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>用户详情 - 运营商后台</title>
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
            <a href="${pageContext.request.contextPath}/admin/user/list" class="active">
                <span class="nav-icon">👥</span><span>用户管理</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/shop/auditList">
                <span class="nav-icon">🏪</span><span>店铺审核</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/shop/allShops">
                <span class="nav-icon">🏬</span><span>全部店铺</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/announcement/list">
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
            <div class="page-title">👤 用户详情</div>
            <div class="user-info">
                <div class="text-info">
                    <strong>管理员</strong>
                    <span>Operator</span>
                </div>
                <div class="avatar">管</div>
            </div>
        </header>

        <section class="admin-content">

            <c:if test="${not empty userDetail}">
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">
                            <span>👤</span><span>账号信息</span>
                        </div>
                        <div class="flex" style="gap:10px">
                            <c:choose>
                                <c:when test="${userDetail.status == 1}">
                                    <span class="stat-pill success">✅ ${userDetail.statusText}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="stat-pill danger">🚫 ${userDetail.statusText}</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="detail-grid">
                        <div>
                            <div class="detail-item">
                                <div class="detail-label">用户 ID</div>
                                <div class="detail-value" style="font-family:monospace">#${userDetail.id}</div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">用户名</div>
                                <div class="detail-value" style="font-weight:700;font-size:15px">${userDetail.username}</div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">昵称</div>
                                <div class="detail-value">${userDetail.nickname}</div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">手机号</div>
                                <div class="detail-value" style="font-family:monospace">${userDetail.phone}</div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">邮箱</div>
                                <div class="detail-value muted">${userDetail.email}</div>
                            </div>
                        </div>

                        <div>
                            <div class="detail-item">
                                <div class="detail-label">头像</div>
                                <div class="detail-value">
                                    <c:choose>
                                        <c:when test="${not empty userDetail.avatar}">
                                            <span style="background:linear-gradient(135deg,#667eea,#764ba2);color:white;padding:8px 14px;border-radius:8px;font-size:18px;display:inline-block">${userDetail.avatar}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">未设置</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">用户角色</div>
                                <div class="detail-value">
                                    <c:choose>
                                        <c:when test="${userDetail.role == 'operator'}">
                                            <span class="badge badge-primary">🛡️ ${userDetail.roleText}</span>
                                        </c:when>
                                        <c:when test="${userDetail.role == 'shopkeeper'}">
                                            <span class="badge badge-info">🏪 ${userDetail.roleText}</span>
                                        </c:when>
                                        <c:when test="${userDetail.role == 'customer'}">
                                            <span class="badge badge-success">🛒 ${userDetail.roleText}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-secondary">👁️ ${userDetail.roleText}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">账号状态</div>
                                <div class="detail-value">
                                    <c:choose>
                                        <c:when test="${userDetail.status == 1}">
                                            <span class="badge badge-success">正常使用中</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-danger">已封禁</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">注册时间</div>
                                <div class="detail-value muted">${userDetail.createTime}</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 角色变更与账号管理 -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">
                            <span>⚙️</span><span>账号管理操作</span>
                        </div>
                        <span class="text-muted" style="font-size:12.5px">所有操作将立即生效</span>
                    </div>

                    <div style="padding:8px 0">
                        <div class="detail-item" style="border:none;padding:16px 0">
                            <div class="detail-label" style="min-width:140px">变更用户角色</div>
                            <div class="flex" style="gap:10px;flex:1;align-items:center">
                                <form action="${pageContext.request.contextPath}/admin/user/changeRole" method="post" style="display:flex;gap:10px;align-items:center;flex-wrap:wrap">
                                    <input type="hidden" name="id" value="${userDetail.id}">
                                    <select name="role" class="form-control" style="height:40px;min-width:160px">
                                        <option value="browser" ${userDetail.role == 'browser' ? 'selected' : ''}>浏览者</option>
                                        <option value="customer" ${userDetail.role == 'customer' ? 'selected' : ''}>顾客</option>
                                        <option value="shopkeeper" ${userDetail.role == 'shopkeeper' ? 'selected' : ''}>商家</option>
                                        <option value="operator" ${userDetail.role == 'operator' ? 'selected' : ''}>运营商</option>
                                    </select>
                                    <button class="btn btn-primary" onclick="return confirm('确认变更此用户的角色？')">
                                        <span>🔄</span><span>变更角色</span>
                                    </button>
                                </form>
                            </div>
                        </div>

                        <div class="divider"></div>

                        <div class="flex" style="gap:12px;flex-wrap:wrap;padding:8px 0">
                            <c:if test="${userDetail.status == 1}">
                                <form action="${pageContext.request.contextPath}/admin/user/ban" method="post">
                                    <input type="hidden" name="id" value="${userDetail.id}">
                                    <button class="btn btn-warning" onclick="return confirm('确认封禁此用户账号？封禁后用户将无法登录')">
                                        <span>🚫</span><span>封禁账号</span>
                                    </button>
                                </form>
                            </c:if>
                            <c:if test="${userDetail.status == 0}">
                                <form action="${pageContext.request.contextPath}/admin/user/unban" method="post">
                                    <input type="hidden" name="id" value="${userDetail.id}">
                                    <button class="btn btn-success" onclick="return confirm('确认解除封禁？用户将恢复正常使用')">
                                        <span>✅</span><span>解除封禁</span>
                                    </button>
                                </form>
                            </c:if>
                            <form action="${pageContext.request.contextPath}/admin/user/resetPassword" method="post">
                                <input type="hidden" name="id" value="${userDetail.id}">
                                <button class="btn btn-secondary" onclick="return confirm('确认重置用户密码为默认值 123456？')">
                                    <span>🔑</span><span>重置密码 (123456)</span>
                                </button>
                            </form>
                            <a href="${pageContext.request.contextPath}/admin/user/list" class="btn btn-outline ml-auto">
                                <span>←</span><span>返回用户列表</span>
                            </a>
                        </div>
                    </div>
                </div>
            </c:if>

            <c:if test="${empty userDetail}">
                <div class="alert alert-warning" style="padding:20px">
                    <span>⚠️</span><span>用户信息不存在</span>
                </div>
                <a href="${pageContext.request.contextPath}/admin/user/list" class="btn btn-primary">
                    <span>←</span><span>返回用户列表</span>
                </a>
            </c:if>

            <div class="footer-note">
                © 运营商管理系统 · Operator Console
            </div>
        </section>
    </main>
</div>
</body>
</html>
