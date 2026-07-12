<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>分类管理</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><div class="container-fluid"><div class="row">
<div class="col-md-2 sidebar"><h4>🏪 商家后台</h4><a href="${pageContext.request.contextPath}/shop/category/list" class="active">分类管理</a></div>
<div class="col-md-10" style="padding:20px"><h3>分类管理</h3>
<form action="${pageContext.request.contextPath}/shop/category/list" method="post" class="form-inline" style="margin-bottom:15px">
<input type="hidden" name="action" value="add"><input type="hidden" name="shopId" value="${shopId}">
<input type="text" name="name" class="form-control" placeholder="分类名称" required>
<input type="number" name="parentId" class="form-control" value="0" placeholder="父ID">
<input type="number" name="sortOrder" class="form-control" value="0">
<button class="btn btn-success">添加</button></form>
<table class="table table-striped admin-table"><thead><tr><th>ID</th><th>名称</th><th>父分类</th><th>排序</th><th>操作</th></tr></thead>
<tbody><c:forEach var="cat" items="${categories}"><tr>
<td>${cat[0]}</td><td>${cat[1]}</td><td>${cat[2]}</td><td>${cat[3]}</td>
<td><form method="post" style="display:inline"><input type="hidden" name="action" value="delete">
<input type="hidden" name="id" value="${cat[0]}"><input type="hidden" name="shopId" value="${shopId}">
<button class="btn btn-danger btn-sm" onclick="return confirm('删除?')">删除</button></form></td></tr></c:forEach></tbody></table></div></div></div></body></html>