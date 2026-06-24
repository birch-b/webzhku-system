<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>商品管理</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><div class="container-fluid"><div class="row">
<div class="col-md-2 sidebar"><h4>🏪 商家后台</h4><a href="${pageContext.request.contextPath}/shop/product/list" class="active">商品管理</a></div>
<div class="col-md-10" style="padding:20px"><h3>商品管理</h3>
<a href="${pageContext.request.contextPath}/shop/product/edit" class="btn btn-primary" style="margin-bottom:15px">新增商品</a>
<table class="table table-striped admin-table"><thead><tr><th>ID</th><th>封面</th><th>名称</th><th>分类</th><th>价格</th><th>库存</th><th>销量</th><th>状态</th><th>操作</th></tr></thead>
<tbody><c:forEach var="p" items="${products}"><tr>
<td>${p[0]}</td><td><img src="${p[7]}" style="width:50px;height:50px" alt=""></td><td>${p[1]}</td><td>${p[2]}</td><td>￥${p[3]}</td><td>${p[4]}</td><td>${p[5]}</td>
<td><span class="status-badge">${p[6]=='1'?'上架':'下架'}</span></td>
<td><a href="${pageContext.request.contextPath}/shop/product/edit?id=${p[0]}" class="btn btn-info btn-sm">编辑</a>
<c:if test="${p[6]=='1'}"><a href="${pageContext.request.contextPath}/shop/product/updateStatus?id=${p[0]}&status=2" class="btn btn-warning btn-sm">下架</a></c:if>
<c:if test="${p[6]=='2'}"><a href="${pageContext.request.contextPath}/shop/product/updateStatus?id=${p[0]}&status=1" class="btn btn-success btn-sm">上架</a></c:if></td></tr></c:forEach></tbody></table></div></div></div></body></html>