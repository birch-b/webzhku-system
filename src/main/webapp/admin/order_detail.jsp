<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>订单详情 - 运营商后台</title>
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
            <div class="page-title">📋 订单详情</div>
            <div class="user-info">
                <div class="text-info">
                    <strong>管理员</strong>
                    <span>Operator</span>
                </div>
                <div class="avatar">管</div>
            </div>
        </header>

        <section class="admin-content">

            <c:if test="${not empty order}">
                <!-- 订单头信息 -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">
                            <span>📋</span><span>订单基本信息</span>
                        </div>
                        <div class="flex" style="gap:12px">
                            <span class="stat-pill" style="font-family:monospace">#${order.orderNo}</span>
                            <c:choose>
                                <c:when test="${order.status >= 5}">
                                    <span class="stat-pill danger">${order.statusText}</span>
                                </c:when>
                                <c:when test="${order.status == 0}">
                                    <span class="stat-pill warning">${order.statusText}</span>
                                </c:when>
                                <c:when test="${order.status == 4}">
                                    <span class="stat-pill success">${order.statusText}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="stat-pill info">${order.statusText}</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="detail-grid">
                        <div>
                            <div class="detail-item">
                                <div class="detail-label">订单号</div>
                                <div class="detail-value" style="font-family:monospace">${order.orderNo}</div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">买家</div>
                                <div class="detail-value">${order.buyerName}</div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">所属店铺</div>
                                <div class="detail-value">${order.shopName}</div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">收件人</div>
                                <div class="detail-value">${order.receiverName}</div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">联系电话</div>
                                <div class="detail-value">${order.receiverPhone}</div>
                            </div>
                        </div>

                        <div>
                            <div class="detail-item">
                                <div class="detail-label">收货地址</div>
                                <div class="detail-value">${order.receiverAddress}</div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">商品金额</div>
                                <div class="detail-value">￥${order.totalAmount}</div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">实付金额</div>
                                <div class="detail-value highlight" style="font-size:18px">￥${order.payAmount}</div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">下单时间</div>
                                <div class="detail-value muted">${order.createTime}</div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">付款时间</div>
                                <div class="detail-value muted">${order.payTime}</div>
                            </div>
                        </div>
                    </div>

                    <div class="divider"></div>

                    <div class="detail-grid">
                        <div>
                            <div class="detail-item">
                                <div class="detail-label">发货时间</div>
                                <div class="detail-value muted">${order.shipTime}</div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">物流公司</div>
                                <div class="detail-value">${order.logisticsCompany}</div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">物流单号</div>
                                <div class="detail-value" style="font-family:monospace">${order.trackingNo}</div>
                            </div>
                        </div>
                        <div>
                            <div class="detail-item">
                                <div class="detail-label">物流状态</div>
                                <div class="detail-value">
                                    <c:choose>
                                        <c:when test="${not empty order.logisticsStatusText}">
                                            <span class="badge badge-info">${order.logisticsStatusText}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">暂无物流信息</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            <div class="detail-item">
                                <div class="detail-label">买家留言</div>
                                <div class="detail-value muted">${order.buyerMessage}</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 商品明细 -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">
                            <span>🛒</span><span>商品明细</span>
                        </div>
                        <span class="stat-pill">共 ${items == null ? 0 : items.size()} 件商品</span>
                    </div>

                    <table class="order-items-table">
                        <thead>
                            <tr>
                                <th>商品名称</th>
                                <th style="width:120px">单价</th>
                                <th style="width:80px">数量</th>
                                <th style="width:140px">小计</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="it" items="${items}">
                                <tr>
                                    <td style="font-weight:500">${it.productName}</td>
                                    <td class="text-muted">￥${it.price}</td>
                                    <td style="text-align:center;font-weight:600">× ${it.quantity}</td>
                                    <td style="color:#ef4444;font-weight:700">￥${it.subtotal}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty items}">
                                <tr>
                                    <td colspan="4">
                                        <div class="empty-state" style="padding:40px 20px">
                                            <span class="empty-icon" style="font-size:40px">📦</span>
                                            <p>暂无商品明细</p>
                                        </div>
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>

                <div class="flex" style="gap:12px;justify-content:flex-end;margin-bottom:24px">
                    <a href="${pageContext.request.contextPath}/admin/order/list" class="btn btn-outline">
                        <span>←</span><span>返回订单列表</span>
                    </a>
                </div>
            </c:if>

            <c:if test="${empty order}">
                <div class="alert alert-warning" style="padding:20px">
                    <span>⚠️</span><span>该订单不存在或已被删除</span>
                </div>
                <a href="${pageContext.request.contextPath}/admin/order/list" class="btn btn-primary">
                    <span>←</span><span>返回订单列表</span>
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
