<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>订单监控 - 运营商后台</title>
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
            <a href="${pageContext.request.contextPath}/admin/announcement/list">
                <span class="nav-icon">📢</span><span>公告管理</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/order/list" class="active">
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
                    <c:when test="${abnormal}">⚠️ 异常订单监控</c:when>
                    <c:otherwise>📦 全平台订单监控</c:otherwise>
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
            <div class="filter-bar">
                <form method="get" style="display:flex;gap:12px;flex-wrap:wrap;align-items:flex-end;width:100%">
                    <div class="filter-item">
                        <label>订单状态</label>
                        <select name="status" class="form-control">
                            <option value="">全部状态</option>
                            <option value="0" ${param.status == '0' ? 'selected' : ''}>待付款</option>
                            <option value="1" ${param.status == '1' ? 'selected' : ''}>待发货</option>
                            <option value="2" ${param.status == '2' ? 'selected' : ''}>已发货</option>
                            <option value="3" ${param.status == '3' ? 'selected' : ''}>已收货</option>
                            <option value="4" ${param.status == '4' ? 'selected' : ''}>已完成</option>
                            <option value="5" ${param.status == '5' ? 'selected' : ''}>已取消</option>
                            <option value="6" ${param.status == '6' ? 'selected' : ''}>退款中</option>
                            <option value="7" ${param.status == '7' ? 'selected' : ''}>已退款</option>
                        </select>
                    </div>
                    <div class="filter-item">
                        <label>搜索关键字</label>
                        <input type="text" name="keyword" class="form-control" placeholder="订单号 / 买家昵称" value="${param.keyword}">
                    </div>
                    <div class="filter-actions">
                        <button type="submit" class="btn btn-primary">
                            <span>🔍</span><span>筛选查询</span>
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/order/list" class="btn btn-outline">重置</a>
                    </div>
                </form>
            </div>

            <div class="action-bar">
                <c:if test="${not abnormal}">
                    <a href="${pageContext.request.contextPath}/admin/order/abnormal" class="btn btn-warning">
                        <span>⚠️</span><span>查看异常订单</span>
                    </a>
                </c:if>
                <c:if test="${abnormal}">
                    <a href="${pageContext.request.contextPath}/admin/order/list" class="btn btn-info">
                        <span>📦</span><span>查看全部订单</span>
                    </a>
                </c:if>
                <div class="ml-auto stat-pill ${abnormal ? 'danger' : 'info'}">
                    <span>📊</span><span>共 ${orders == null ? 0 : orders.size()} 条订单记录</span>
                </div>
            </div>

            <div class="table-wrapper">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>订单号</th>
                            <th>买家</th>
                            <th>店铺</th>
                            <th>金额</th>
                            <th>状态</th>
                            <th>下单时间</th>
                            <th style="width:120px">操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="o" items="${orders}">
                            <tr>
                                <td>
                                    <div style="font-family:monospace;font-weight:600;color:var(--primary)">${o.orderNo}</div>
                                </td>
                                <td>
                                    <div style="font-weight:500">${o.buyerName}</div>
                                </td>
                                <td>
                                    <div style="font-weight:500">${o.shopName}</div>
                                </td>
                                <td>
                                    <span style="color:var(--danger);font-weight:700;font-size:15px">￥${o.payAmount}</span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${o.status >= 5}">
                                            <span class="badge badge-danger">${o.statusText}</span>
                                        </c:when>
                                        <c:when test="${o.status == 0}">
                                            <span class="badge badge-warning">${o.statusText}</span>
                                        </c:when>
                                        <c:when test="${o.status == 4}">
                                            <span class="badge badge-success">${o.statusText}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-info">${o.statusText}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-muted" style="font-size:12.5px">${o.createTime}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/order/detail?id=${o.id}" class="btn btn-info btn-sm">
                                        <span>📋</span><span>详情</span>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty orders}">
                            <tr>
                                <td colspan="7">
                                    <div class="empty-state">
                                        <span class="empty-icon">📭</span>
                                        <p>暂无订单数据</p>
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
