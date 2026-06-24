<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>商家后台</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><div class="container-fluid"><div class="row">
<div class="col-md-2 sidebar"><h4>🏪 商家后台</h4>
<a href="${pageContext.request.contextPath}/shop/home" class="active">首页</a>
<a href="${pageContext.request.contextPath}/shop/info/view">店铺信息</a>
<a href="${pageContext.request.contextPath}/shop/category/list">分类管理</a>
<a href="${pageContext.request.contextPath}/shop/product/list">商品管理</a>
<a href="${pageContext.request.contextPath}/shop/order/list">订单管理</a>
<a href="${pageContext.request.contextPath}/shop/review/list">评价管理</a></div>
<div class="col-md-10" style="padding:20px"><h2>🏪 商家后台</h2><p>欢迎，${sessionScope.user.nickname}！</p>
<div class="row" style="margin-top:20px">
<div class="col-md-4"><div class="panel panel-default"><div class="panel-body text-center"><h4>商品管理</h4><a href="${pageContext.request.contextPath}/shop/product/list" class="btn btn-primary">进入</a></div></div></div>
<div class="col-md-4"><div class="panel panel-default"><div class="panel-body text-center"><h4>订单处理</h4><a href="${pageContext.request.contextPath}/shop/order/list" class="btn btn-primary">进入</a></div></div></div>
<div class="col-md-4"><div class="panel panel-default"><div class="panel-body text-center"><h4>评价管理</h4><a href="${pageContext.request.contextPath}/shop/review/list" class="btn btn-primary">进入</a></div></div></div></div></div></div></div>
<script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script></body></html>