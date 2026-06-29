<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>我的订单</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body>
<jsp:include page="../header.jsp"/>
<div class="container"><h2>我的订单</h2>
<table class="table table-striped"><thead><tr><th>订单号</th><th>店铺</th><th>金额</th><th>状态</th><th>时间</th><th>操作</th></tr></thead>
<tbody><c:forEach var="o" items="${orders}"><tr>
<td>${o[1]}</td><td>${o[2]}</td><td>￥${o[3]}</td>
<td><span class="status-badge">${o[4]=='0'?'待付款':o[4]=='1'?'待发货':o[4]=='2'?'已发货':o[4]=='3'?'已收货':o[4]=='4'?'已完成':'已取消'}</span></td>
<td>${o[5]}</td>
<td><a href="${pageContext.request.contextPath}/order/detail?id=${o[0]}" class="btn btn-info btn-sm">查看</a>
<c:if test="${o[4]=='0'}"><form action="${pageContext.request.contextPath}/order/cancel" method="post" style="display:inline">
<input type="hidden" name="id" value="${o[0]}"><button class="btn btn-warning btn-sm" onclick="return confirm('取消订单?')">取消</button></form></c:if>
<c:if test="${o[4]=='2'}"><form action="${pageContext.request.contextPath}/order/confirm" method="post" style="display:inline">
<input type="hidden" name="id" value="${o[0]}"><button class="btn btn-success btn-sm">确认收货</button></form></c:if></td></tr></c:forEach></tbody></table></div>
<jsp:include page="../footer.jsp"/></body></html>