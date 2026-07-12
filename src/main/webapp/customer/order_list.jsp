<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>我的订单</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body>
<jsp:include page="../header.jsp"/>
<div class="order-page">
<h2>我的订单</h2>
<div class="order-tabs">
    <a href="${pageContext.request.contextPath}/order/list" class="tab-item ${empty currentStatus ? 'active' : ''}">全部</a>
    <a href="${pageContext.request.contextPath}/order/list?status=0" class="tab-item ${currentStatus == '0' ? 'active' : ''}">待付款</a>
    <a href="${pageContext.request.contextPath}/order/list?status=1" class="tab-item ${currentStatus == '1' ? 'active' : ''}">待发货</a>
    <a href="${pageContext.request.contextPath}/order/list?status=2" class="tab-item ${currentStatus == '2' ? 'active' : ''}">已发货</a>
    <a href="${pageContext.request.contextPath}/order/list?status=3" class="tab-item ${currentStatus == '3' ? 'active' : ''}">已收货</a>
    <a href="${pageContext.request.contextPath}/order/list?status=4" class="tab-item ${currentStatus == '4' ? 'active' : ''}">已完成</a>
    <a href="${pageContext.request.contextPath}/order/list?status=5" class="tab-item ${currentStatus == '5' ? 'active' : ''}">已取消</a>
</div>
<c:forEach var="o" items="${orders}">
<div class="order-card">
    <div class="order-card-header">
        <span class="order-no">订单号：<strong>${o[1]}</strong></span>
        <span class="status-badge status-${o[4]}">${o[4]=='0'?'待付款':o[4]=='1'?'待发货':o[4]=='2'?'已发货':o[4]=='3'?'已收货':o[4]=='4'?'已完成':'已取消'}</span>
    </div>
    <div class="order-card-body">
        <div class="order-product-row">
            <div class="order-product-info">
                <h4>${o[2]}</h4>
                <span class="order-product-price">￥${o[3]}</span>
                <span class="order-product-quantity">下单时间：${o[5]}</span>
            </div>
        </div>
    </div>
    <div class="order-card-footer">
        <div class="order-total">共 <strong>￥${o[3]}</strong></div>
        <div class="order-actions">
            <a href="${pageContext.request.contextPath}/order/detail?id=${o[0]}" class="btn btn-default">查看详情</a>
            <c:if test="${o[4]=='0'}">
                <form action="${pageContext.request.contextPath}/order/cancel" method="post" style="display:inline">
                    <input type="hidden" name="id" value="${o[0]}">
                    <button class="btn btn-default" onclick="return confirm('确定取消订单?')">取消订单</button>
                </form>
            </c:if>
            <c:if test="${o[4]=='2'}">
                <form action="${pageContext.request.contextPath}/order/confirm" method="post" style="display:inline">
                    <input type="hidden" name="id" value="${o[0]}">
                    <button class="btn btn-primary">确认收货</button>
                </form>
            </c:if>
        </div>
    </div>
</div>
</c:forEach>
</div>
<jsp:include page="../footer.jsp"/></body></html>