<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>评价管理</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><div class="container-fluid"><div class="row">
<div class="col-md-2 sidebar"><h4>🏪 商家后台</h4><a href="${pageContext.request.contextPath}/shop/review/list" class="active">评价管理</a></div>
<div class="col-md-10" style="padding:20px"><h3>评价管理</h3>
<table class="table table-striped admin-table"><thead><tr><th>ID</th><th>商品</th><th>买家</th><th>评分</th><th>内容</th><th>回复</th><th>操作</th></tr></thead>
<tbody><c:forEach var="r" items="${reviews}"><tr>
<td>${r[0]}</td><td>${r[1]}</td><td>${r[2]}</td><td>${r[3]}⭐</td><td>${r[4]}</td><td>${r[5]!=null?r[5]:'未回复'}</td>
<td><c:if test="${r[5]==null}"><form action="${pageContext.request.contextPath}/shop/review/reply" method="post" style="display:inline">
<input type="hidden" name="id" value="${r[0]}"><input type="text" name="reply" class="form-control input-sm" placeholder="回复" style="width:100px;display:inline">
<button class="btn btn-success btn-sm">回复</button></form></c:if></td></tr></c:forEach></tbody></table></div></div></div></body></html>