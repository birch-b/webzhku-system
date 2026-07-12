<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>售后管理 - 商家后台 - 淘宝购物系统</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .status-badge { display:inline-block; padding:3px 10px; border-radius:12px; font-size:12px; color:#fff; font-weight:500; }
        .status-0 { background:#f59e0b; }
        .status-1 { background:#3b82f6; }
        .status-2 { background:#ef4444; }
        .status-3 { background:#10b981; }
        .filter-bar { background:#fff; padding:16px 20px; border-radius:8px; box-shadow:var(--shadow); margin-bottom:20px; display:flex; gap:8px; flex-wrap:wrap; }
    </style>
</head>
<body data-role="shopkeeper">
    <%@ include file="../header.jsp"%>

    <div class="container" style="margin-top:20px;">
        <div class="row">
            <!-- 左侧菜单 -->
            <div class="col-md-2">
                <ul class="nav nav-pills nav-stacked">
                    <li><a href="${pageContext.request.contextPath}/shop/home">🏠 店铺首页</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop/info/view">🏪 店铺信息</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop/category/list">📂 分类管理</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop/product/list">📦 商品管理</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop/order/list">🧾 订单管理</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop/review/list">💬 评价管理</a></li>
                    <li class="active"><a href="${pageContext.request.contextPath}/shop/aftersale/list">🔁 售后管理</a></li>
                </ul>
            </div>

            <!-- 右侧内容 -->
            <div class="col-md-10">
                <h2 style="margin:10px 0 20px;padding-bottom:14px;border-bottom:2px solid var(--primary);">🔁 售后管理</h2>

                <c:if test="${not empty param.msg}">
                    <div class="alert alert-success">操作成功！</div>
                </c:if>

                <!-- 状态筛选 -->
                <div class="filter-bar">
                    <a href="${pageContext.request.contextPath}/shop/aftersale/list"
                       class="btn ${empty currentStatus ? 'btn-primary' : 'btn-default'}">全部</a>
                    <a href="?status=0"
                       class="btn ${currentStatus == '0' ? 'btn-warning' : 'btn-default'}">待处理</a>
                    <a href="?status=1"
                       class="btn ${currentStatus == '1' ? 'btn-info' : 'btn-default'}">已同意</a>
                    <a href="?status=2"
                       class="btn ${currentStatus == '2' ? 'btn-danger' : 'btn-default'}">已拒绝</a>
                    <a href="?status=3"
                       class="btn ${currentStatus == '3' ? 'btn-success' : 'btn-default'}">已退款</a>
                </div>

                <c:choose>
                    <c:when test="${not empty aftersales}">
                        <div class="card" style="background:#fff;border-radius:8px;box-shadow:var(--shadow);padding:0;overflow:hidden;">
                            <table class="table" style="margin:0;">
                                <thead>
                                    <tr>
                                        <th>订单编号</th>
                                        <th>买家</th>
                                        <th>售后类型</th>
                                        <th>退款金额</th>
                                        <th>申请原因</th>
                                        <th>状态</th>
                                        <th>申请时间</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="a" items="${aftersales}">
                                        <tr>
                                            <td><code style="background:var(--bg-alt);padding:2px 6px;border-radius:4px;">${a.orderNo}</code></td>
                                            <td>
                                                ${a.nickname}(${a.username})<br>
                                                <small class="text-muted">${a.phone}</small>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${a.type == 1}">仅退款</c:when>
                                                    <c:when test="${a.type == 2}">退货退款</c:when>
                                                </c:choose>
                                            </td>
                                            <td style="color:var(--danger);font-weight:600;">￥${a.amount}</td>
                                            <td>
                                                <span title="${a.reason}">
                                                    <c:choose>
                                                        <c:when test="${a.reason.length() > 15}">${a.reason.substring(0, 15)}...</c:when>
                                                        <c:otherwise>${a.reason}</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${a.status == 0}">
                                                        <span class="status-badge status-0">待处理</span>
                                                    </c:when>
                                                    <c:when test="${a.status == 1}">
                                                        <span class="status-badge status-1">已同意</span>
                                                    </c:when>
                                                    <c:when test="${a.status == 2}">
                                                        <span class="status-badge status-2">已拒绝</span>
                                                    </c:when>
                                                    <c:when test="${a.status == 3}">
                                                        <span class="status-badge status-3">已退款</span>
                                                    </c:when>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${a.createTime}" pattern="yyyy-MM-dd"/>
                                            </td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/shop/aftersale/detail?id=${a.id}" class="btn btn-info btn-sm">处理</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-info">
                            暂无售后记录
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <%@ include file="../footer.jsp"%>

    <script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>
