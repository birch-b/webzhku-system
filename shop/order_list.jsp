<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>订单管理</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><div class="container-fluid"><div class="row">
<div class="col-md-2 sidebar"><h4>🏪 商家后台</h4><a href="${pageContext.request.contextPath}/shop/order/list" class="active">订单管理</a></div>
<div class="col-md-10" style="padding:20px"><h3>订单管理</h3>
<div class="btn-group" style="margin-bottom:15px">
<a href="${pageContext.request.contextPath}/shop/order/list" class="btn btn-default">全部</a>
<a href="${pageContext.request.contextPath}/shop/order/list?status=1" class="btn btn-warning">待发货</a>
<a href="${pageContext.request.contextPath}/shop/order/list?status=2" class="btn btn-info">已发货</a></div>
<c:if test="${not empty msg}"><div class="alert alert-success">${msg}</div></c:if>
<table class="table table-striped admin-table"><thead><tr><th>订单号</th><th>买家</th><th>金额</th><th>收货人</th><th>状态</th><th>操作</th></tr></thead>
<tbody><c:forEach var="o" items="${orders}"><tr>
<td>${o[1]}</td><td>${o[2]}</td><td>￥${o[3]}</td><td>${o[6]}</td>
<td><span class="status-badge">${o[4]=='0'?'待付款':o[4]=='1'?'待发货':o[4]=='2'?'已发货':'已完成'}</span></td>
<td><a href="${pageContext.request.contextPath}/shop/order/detail?id=${o[0]}" class="btn btn-info btn-sm">查看</a></td></tr></c:forEach></tbody></table></div></div></div></body></html>