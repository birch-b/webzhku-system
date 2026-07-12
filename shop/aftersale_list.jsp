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
        body { background-color: #f5f5f5; }
        .page-header { margin: 20px 0; }
        .status-0 { color: #f0ad4e; }
        .status-1 { color: #5bc0de; }
        .status-2 { color: #d9534f; }
        .status-3 { color: #5cb85c; }
        .filter-bar { margin-bottom: 15px; }
    </style>
</head>
<body>
    <%@ include file="../header.jsp"%>

    <div class="container">
        <div class="row">
            <!-- 左侧菜单 -->
            <div class="col-md-2">
                <ul class="nav nav-pills nav-stacked">
                    <li><a href="${pageContext.request.contextPath}/shop/home">店铺首页</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop/order/list">订单管理</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop/product/list">商品管理</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop/review/list">评价管理</a></li>
                    <li class="active"><a href="${pageContext.request.contextPath}/shop/aftersale">售后管理</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop/info">店铺设置</a></li>
                </ul>
            </div>

            <!-- 右侧内容 -->
            <div class="col-md-10">
                <h2 class="page-header">
                    <span class="glyphicon glyphicon-repeat"></span> 售后管理
                </h2>

                <c:if test="${not empty param.msg}">
                    <div class="alert alert-success">操作成功！</div>
                </c:if>

                <!-- 状态筛选 -->
                <div class="filter-bar">
                    <a href="${pageContext.request.contextPath}/shop/aftersale"
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
                        <table class="table table-bordered table-hover">
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
                                        <td>${a.orderNo}</td>
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
                                        <td class="text-danger">￥${a.amount}</td>
                                        <td>
                                            <span title="${a.reason}">
                                                ${a.reason.length() > 15 ? a.reason.substring(0, 15) + '...' : a.reason}
                                            </span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${a.status == 0}">
                                                    <span class="status-0">待处理</span>
                                                </c:when>
                                                <c:when test="${a.status == 1}">
                                                    <span class="status-1">已同意</span>
                                                </c:when>
                                                <c:when test="${a.status == 2}">
                                                    <span class="status-2">已拒绝</span>
                                                </c:when>
                                                <c:when test="${a.status == 3}">
                                                    <span class="status-3">已退款</span>
                                                </c:when>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${a.createTime}" pattern="yyyy-MM-dd"/>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/shop/aftersale/detail?id=${a.id}" class="btn btn-xs btn-info">处理</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
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
