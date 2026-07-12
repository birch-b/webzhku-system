<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>我的售后 - 淘宝购物系统</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body { background-color: #f5f5f5; }
        .page-header { margin: 20px 0; }
        .status-0 { color: #f0ad4e; }
        .status-1 { color: #5bc0de; }
        .status-2 { color: #d9534f; }
        .status-3 { color: #5cb85c; }
    </style>
</head>
<body>
    <%@ include file="../header.jsp"%>

    <div class="container">
        <div class="row">
            <!-- 左侧菜单 -->
            <div class="col-md-2">
                <ul class="nav nav-pills nav-stacked">
                    <li><a href="${pageContext.request.contextPath}/customer/profile">个人信息</a></li>
                    <li><a href="${pageContext.request.contextPath}/customer/address">收货地址</a></li>
                    <li><a href="${pageContext.request.contextPath}/order/list">我的订单</a></li>
                    <li class="active"><a href="${pageContext.request.contextPath}/aftersale">售后记录</a></li>
                </ul>
            </div>

            <!-- 右侧内容 -->
            <div class="col-md-10">
                <h2 class="page-header">
                    <span class="glyphicon glyphicon-repeat"></span> 我的售后记录
                </h2>

                <c:if test="${not empty param.msg}">
                    <div class="alert alert-success">操作成功！</div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <c:choose>
                    <c:when test="${not empty aftersales}">
                        <table class="table table-bordered table-hover">
                            <thead>
                                <tr>
                                    <th>订单编号</th>
                                    <th>店铺</th>
                                    <th>售后类型</th>
                                    <th>申请原因</th>
                                    <th>退款金额</th>
                                    <th>状态</th>
                                    <th>申请时间</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="a" items="${aftersales}">
                                    <tr>
                                        <td>${a.orderNo}</td>
                                        <td>${a.shopName}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${a.type == 1}">仅退款</c:when>
                                                <c:when test="${a.type == 2}">退货退款</c:when>
                                            </c:choose>
                                        </td>
                                        <td>${a.reason}</td>
                                        <td class="text-danger">￥${a.amount}</td>
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
                                            <a href="${pageContext.request.contextPath}/aftersale/detail?id=${a.id}" class="btn btn-xs btn-default">查看详情</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-info">
                            您还没有售后记录。
                            <a href="${pageContext.request.contextPath}/order/list">查看订单</a>
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
