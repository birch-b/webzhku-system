<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>商家审核</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><div class="container-fluid"><div class="row">
<div class="col-md-2 sidebar"><h4>🛡️ 运营商后台</h4>
<a href="${pageContext.request.contextPath}/admin/shop/auditList" class="active">商家审核</a></div>
<div class="col-md-10" style="padding:20px"><h3>商家入驻审核</h3>
<c:if test="${not empty msg}"><div class="alert alert-success">${msg}</div></c:if>
<table class="table table-striped admin-table"><thead><tr><th>ID</th><th>店铺名</th><th>简介</th><th>时间</th><th>操作</th></tr></thead>
<tbody><c:forEach var="a" items="${applies}"><tr>
<td>${a.id}</td><td>${a.shopName}</td><td>${a.description}</td><td>${a.createTime}</td>
<td><form action="${pageContext.request.contextPath}/admin/shop/approve" method="post" style="display:inline"><input type="hidden" name="id" value="${a.id}"><button class="btn btn-success btn-sm">通过</button></form>
<form action="${pageContext.request.contextPath}/admin/shop/reject" method="post" style="display:inline"><input type="hidden" name="id" value="${a.id}">
<input type="text" name="reason" class="form-control input-sm" placeholder="拒绝原因" style="width:150px;display:inline">
<button class="btn btn-danger btn-sm">驳回</button></form></td></tr></c:forEach></tbody></table></div></div></div></body></html>