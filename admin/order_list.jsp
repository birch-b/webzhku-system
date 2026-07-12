<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>订单监控</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><div class="container-fluid"><div class="row">
<div class="col-md-2 sidebar"><h4>🛡️ 运营商后台</h4><a href="${pageContext.request.contextPath}/admin/order/list" class="active">订单监控</a></div>
<div class="col-md-10" style="padding:20px"><h3>全平台订单监控</h3>
<form class="form-inline" method="get" style="margin-bottom:15px">
<select name="status" class="form-control"><option value="">全部</option><option value="0">待付款</option><option value="1">待发货</option><option value="2">已发货</option><option value="3">已收货</option></select>
<input type="text" name="keyword" class="form-control" placeholder="订单号/买家昵称" value="${param.keyword}">
<button class="btn btn-primary">筛选</button></form>
<table class="table table-striped admin-table"><thead><tr><th>订单号</th><th>买家</th><th>店铺</th><th>金额</th><th>状态</th><th>时间</th><th>操作</th></tr></thead>
<tbody><c:forEach var="o" items="${orders}"><tr>
<td>${o[1]}</td><td>${o[2]}</td><td>${o[3]}</td><td>￥${o[5]}</td>
<td><span class="status-badge">${o[6]=='0'?'待付款':o[6]=='1'?'待发货':o[6]=='2'?'已发货':o[6]=='3'?'已收货':'已取消'}</span></td>
<td>${o[7]}</td><td><a href="${pageContext.request.contextPath}/admin/order/detail?id=${o[0]}" class="btn btn-info btn-sm">查看</a></td></tr></c:forEach></tbody></table></div></div></div></body></html>