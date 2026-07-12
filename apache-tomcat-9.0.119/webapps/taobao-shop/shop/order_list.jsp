<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head><meta charset="UTF-8"><title>我的订单</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
.status-badge{padding:3px 10px;border-radius:3px;font-size:12px;color:#fff}
.status-0{background:#f0ad4e}.status-1{background:#5bc0de}.status-2{background:#337ab7}
.status-3{background:#5cb85c}.status-4{background:#d9534f}.status-5{background:#777}
.status-6{background:#f0ad4e}.status-7{background:#777}
</style>
</head>
<body>
<jsp:include page="../header.jsp"/>
<div class="container" style="margin-top:20px;">
<h2>我的订单</h2>
<c:if test="${not empty param.msg}">
<div class="alert alert-success">
<c:choose>
<c:when test="${param.msg=='reviewed'}">评价成功</c:when>
<c:when test="${param.msg=='cancelled'}">订单已取消</c:when>
<c:when test="${param.msg=='duplicateReview'}">已评价过</c:when>
</c:choose>
</div>
</c:if>
<ul class="nav nav-tabs">
<li ${empty currentStatus ? 'class="active"' : ''}><a href="${pageContext.request.contextPath}/order/list">全部</a></li>
<li ${currentStatus=='0' ? 'class="active"' : ''}><a href="${pageContext.request.contextPath}/order/list?status=0">待付款</a></li>
<li ${currentStatus=='1' ? 'class="active"' : ''}><a href="${pageContext.request.contextPath}/order/list?status=1">待发货</a></li>
<li ${currentStatus=='2' ? 'class="active"' : ''}><a href="${pageContext.request.contextPath}/order/list?status=2">已发货</a></li>
<li ${currentStatus=='3' ? 'class="active"' : ''}><a href="${pageContext.request.contextPath}/order/list?status=3">已收货</a></li>
<li ${currentStatus=='4' ? 'class="active"' : ''}><a href="${pageContext.request.contextPath}/order/list?status=4">已完成</a></li>
</ul>
<c:choose>
<c:when test="${empty orders}"><div class="alert alert-info">暂无订单</div></c:when>
<c:otherwise>
<table class="table table-striped">
<thead><tr><th>订单号</th><th>店铺</th><th>金额</th><th>状态</th><th>时间</th><th>操作</th></tr></thead>
<tbody>
<c:forEach var="o" items="${orders}">
<tr>
<td>${o[1]}</td><td>${o[2]}</td><td>￥${o[3]}</td>
<td><span class="status-badge status-${o[4]}">${o[4]=='0'?'待付款':o[4]=='1'?'待发货':o[4]=='2'?'已发货':o[4]=='3'?'已收货':o[4]=='4'?'已完成':o[4]=='5'?'已取消':o[4]=='6'?'退款中':'已退款'}</span></td>
<td>${o[5]}</td>
<td>
<a href="${pageContext.request.contextPath}/order/detail?id=${o[0]}" class="btn btn-info btn-sm">查看</a>
<c:if test="${o[4]=='0'}">
<a href="${pageContext.request.contextPath}/payment/view?orderId=${o[0]}&amount=${o[6]}" class="btn btn-primary btn-sm">去支付</a>
<form action="${pageContext.request.contextPath}/order/cancel" method="post" style="display:inline">
<input type="hidden" name="id" value="${o[0]}"><button class="btn btn-warning btn-sm" onclick="return confirm('取消订单?')">取消</button></form>
</c:if>
<c:if test="${o[4]=='2'}">
<form action="${pageContext.request.contextPath}/order/confirm" method="post" style="display:inline">
<input type="hidden" name="id" value="${o[0]}"><button class="btn btn-success btn-sm">确认收货</button></form>
</c:if>
<c:if test="${o[4]=='3' || o[4]=='4'}">
<a href="${pageContext.request.contextPath}/review/edit?orderId=${o[0]}&productId=0" class="btn btn-primary btn-sm">评价</a>
<a href="${pageContext.request.contextPath}/aftersale/apply?orderId=${o[0]}" class="btn btn-warning btn-sm">申请售后</a>
</c:if>
</td>
</tr>
</c:forEach>
</tbody>
</table>
<c:if test="${totalPages > 1}">
<nav><ul class="pagination">
<li ${page<=1 ? 'class="disabled"' : ''}><a href="${pageContext.request.contextPath}/order/list?status=${currentStatus}&page=${page-1}">&laquo;</a></li>
<c:forEach begin="1" end="${totalPages}" var="i">
<li ${page==i ? 'class="active"' : ''}><a href="${pageContext.request.contextPath}/order/list?status=${currentStatus}&page=${i}">${i}</a></li>
</c:forEach>
<li ${page>=totalPages ? 'class="disabled"' : ''}><a href="${pageContext.request.contextPath}/order/list?status=${currentStatus}&page=${page+1}">&raquo;</a></li>
</ul></nav>
</c:if>
</c:otherwise>
</c:choose>
</div>
<jsp:include page="../footer.jsp"/>
</body>
</html>
