<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>订单详情</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><div class="container-fluid"><div class="row">
<div class="col-md-2 sidebar"><h4>🏪 商家后台</h4><a href="${pageContext.request.contextPath}/shop/order/list" class="active">订单管理</a></div>
<div class="col-md-10" style="padding:20px"><h3>订单详情</h3>
<table class="table table-bordered">
<tr><th>订单号</th><td>${order.getString("order_no")}</td></tr>
<tr><th>买家</th><td>${order.getString("buyer_name")}</td></tr>
<tr><th>金额</th><td>￥${order.getString("pay_amount")}</td></tr>
<tr><th>收货人</th><td>${order.getString("receiver_name")} ${order.getString("receiver_phone")}</td></tr>
<tr><th>地址</th><td>${order.getString("receiver_address")}</td></tr></table>
<h4>商品明细</h4><table class="table table-striped"><thead><tr><th>商品</th><th>单价</th><th>数量</th><th>小计</th></tr></thead>
<tbody><c:forEach var="item" items="${orderItems}"><tr><td>${item[0]}</td><td>￥${item[2]}</td><td>${item[3]}</td><td>￥${item[4]}</td></tr></c:forEach></tbody></table>
<c:if test="${order.getInt('status')==1}"><h4>发货</h4>
<form action="${pageContext.request.contextPath}/shop/order/ship" method="post" class="form-inline">
<input type="hidden" name="id" value="${order.getLong('id')}">
<select name="company" class="form-control"><option>顺丰速运</option><option>中通快递</option><option>圆通速递</option><option>韵达快递</option></select>
<input type="text" name="trackingNo" class="form-control" placeholder="快递单号" required>
<button class="btn btn-success">发货</button></form></c:if>
<c:if test="${order.getInt('status')>=2}"><h4>物流</h4><p>${order.getString("company")} ${order.getString("tracking_no")}</p></c:if>
<a href="${pageContext.request.contextPath}/shop/order/list" class="btn btn-default">返回</a></div></div></div></body></html>