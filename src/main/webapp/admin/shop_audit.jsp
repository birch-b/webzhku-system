<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>店铺管理 - 运营商后台</title>
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
            <a href="${pageContext.request.contextPath}/admin/shop/auditList" class="active">
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
            <div class="page-title">
                <c:choose>
                    <c:when test="${not empty shops}">🏬 全平台店铺列表</c:when>
                    <c:otherwise>🏪 商家入驻审核</c:otherwise>
                </c:choose>
            </div>
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
                            <c:when test="${msg=='approved'}">审核通过，店铺已开通</c:when>
                            <c:when test="${msg=='rejected'}">已驳回申请</c:when>
                            <c:when test="${msg=='closed'}">店铺已强制关闭</c:when>
                            <c:otherwise>${msg}</c:otherwise>
                        </c:choose>
                    </span>
                </div>
            </c:if>

            <div class="action-bar">
                <c:choose>
                    <c:when test="${not empty shops}">
                        <a href="${pageContext.request.contextPath}/admin/shop/auditList" class="btn btn-info">
                            <span>📝</span><span>切换到入驻审核</span>
                        </a>
                        <div class="ml-auto stat-pill">
                            <span>🏬</span><span>共 ${shops.size()} 家店铺</span>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/admin/shop/allShops" class="btn btn-primary">
                            <span>🏬</span><span>查看全部店铺</span>
                        </a>
                        <div class="ml-auto stat-pill warning">
                            <span>⏳</span><span>${applies == null ? 0 : applies.size()} 条待审核申请</span>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- 全部店铺列表 -->
            <c:if test="${not empty shops}">
                <div class="table-wrapper">
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>店铺名</th>
                                <th>类目</th>
                                <th>店主</th>
                                <th>评分</th>
                                <th>订单数</th>
                                <th>销售额</th>
                                <th>状态</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="s" items="${shops}">
                                <tr>
                                    <td><strong>#${s.id}</strong></td>
                                    <td>
                                        <div style="font-weight:600">${s.shopName}</div>
                                    </td>
                                    <td>
                                        <span class="badge badge-secondary">${s.shopCategory}</span>
                                    </td>
                                    <td>${s.ownerName}</td>
                                    <td>
                                        <span class="star-rating">★ ${s.rating}</span>
                                    </td>
                                    <td style="font-weight:600">${s.totalOrders}</td>
                                    <td style="color:#ef4444;font-weight:700">￥${s.totalSales}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${s.status == -1}">
                                                <span class="badge badge-danger">${s.statusText}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-success">${s.statusText}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:if test="${s.status != -1}">
                                            <form action="${pageContext.request.contextPath}/admin/shop/close" method="post" style="display:inline-flex">
                                                <input type="hidden" name="id" value="${s.id}">
                                                <button class="btn btn-danger btn-sm" onclick="return confirm('确认强制关闭该店铺？所有商品将下架，此操作需谨慎')">
                                                    <span>🚫</span><span>强制关闭</span>
                                                </button>
                                            </form>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty shops}">
                                <tr>
                                    <td colspan="9">
                                        <div class="empty-state">
                                            <span class="empty-icon">🏬</span>
                                            <p>暂无店铺数据</p>
                                        </div>
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </c:if>

            <!-- 入驻审核列表 -->
            <c:if test="${empty shops}">
                <div class="table-wrapper">
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>店铺名</th>
                                <th>类目</th>
                                <th>申请人</th>
                                <th>联系人</th>
                                <th>联系电话</th>
                                <th>营业执照</th>
                                <th>简介</th>
                                <th>申请时间</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="a" items="${applies}">
                                <tr>
                                    <td><strong>#${a.id}</strong></td>
                                    <td>
                                        <div style="font-weight:600">${a.shopName}</div>
                                    </td>
                                    <td>
                                        <span class="badge badge-secondary">${a.shopCategory}</span>
                                    </td>
                                    <td>${a.nickname}</td>
                                    <td>${a.contactName}</td>
                                    <td style="font-family:monospace;font-size:12.5px">${a.contactPhone}</td>
                                    <td style="font-family:monospace;font-size:12.5px">${a.licenseNo}</td>
                                    <td class="text-muted" style="font-size:12.5px;max-width:200px">${a.description}</td>
                                    <td class="text-muted" style="font-size:12.5px">${a.applyTime}</td>
                                    <td>
                                        <div class="table-actions">
                                            <form action="${pageContext.request.contextPath}/admin/shop/approve" method="post" style="display:inline-flex">
                                                <input type="hidden" name="id" value="${a.id}">
                                                <button class="btn btn-success btn-sm" onclick="return confirm('确认通过此申请？店铺将正式开通')">
                                                    <span>✅</span><span>通过</span>
                                                </button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/admin/shop/reject" method="post" style="display:inline-flex;flex-direction:column;gap:4px">
                                                <input type="hidden" name="id" value="${a.id}">
                                                <div style="display:flex;gap:4px">
                                                    <input type="text" name="reason" class="form-control" placeholder="拒绝原因" style="height:28px;font-size:12px;padding:0 8px;min-width:140px">
                                                    <button class="btn btn-danger btn-sm" onclick="return confirm('确认驳回此申请？')">
                                                        <span>❌</span><span>驳回</span>
                                                    </button>
                                                </div>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty applies}">
                                <tr>
                                    <td colspan="10">
                                        <div class="empty-state">
                                            <span class="empty-icon">🎉</span>
                                            <p>暂无待审核申请</p>
                                        </div>
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </c:if>

            <div class="footer-note">
                © 运营商管理系统 · Operator Console
            </div>
        </section>
    </main>
</div>
</body>
</html>
