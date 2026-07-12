<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>用户管理</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><div class="container-fluid"><div class="row">
<div class="col-md-2 sidebar"><h4>🛡️ 运营商后台</h4>
<a href="${pageContext.request.contextPath}/admin/stat/dashboard">数据概览</a>
<a href="${pageContext.request.contextPath}/admin/user/list" class="active">用户管理</a>
<a href="${pageContext.request.contextPath}/admin/shop/auditList">商家审核</a>
<a href="${pageContext.request.contextPath}/admin/announcement/list">公告管理</a></div>
<div class="col-md-10" style="padding:20px"><h3>用户管理</h3>
<form class="form-inline" method="get" style="margin-bottom:15px">
<input type="text" name="keyword" class="form-control" placeholder="搜索用户名/昵称/手机号" value="${param.keyword}">
<select name="role" class="form-control"><option value="">全部角色</option><option value="customer">顾客</option><option value="shopkeeper">商家</option><option value="operator">运营商</option></select>
<button class="btn btn-primary">搜索</button></form>
<table class="table table-striped admin-table"><thead><tr><th>ID</th><th>用户名</th><th>昵称</th><th>手机</th><th>状态</th><th>操作</th></tr></thead>
<tbody><c:forEach var="u" items="${users}"><tr>
<td>${u.id}</td><td>${u.username}</td><td>${u.nickname}</td><td>${u.phone}</td>
<td><span class="status-badge ${u.status==1?'status-active':'status-banned'}">${u.status==1?'正常':'禁用'}</span></td>
<td><a href="${pageContext.request.contextPath}/admin/user/detail?id=${u.id}" class="btn btn-info btn-sm">详情</a>
<c:if test="${u.status==1}"><form action="${pageContext.request.contextPath}/admin/user/ban" method="post" style="display:inline"><input type="hidden" name="id" value="${u.id}"><button class="btn btn-warning btn-sm">封禁</button></form></c:if>
<c:if test="${u.status==0}"><form action="${pageContext.request.contextPath}/admin/user/unban" method="post" style="display:inline"><input type="hidden" name="id" value="${u.id}"><button class="btn btn-success btn-sm">解封</button></form></c:if>
<form action="${pageContext.request.contextPath}/admin/user/resetPassword" method="post" style="display:inline"><input type="hidden" name="id" value="${u.id}"><button class="btn btn-default btn-sm" onclick="return confirm('重置密码为123456?')">重置密码</button></form>
</td></tr></c:forEach></tbody></table></div></div></div></body></html>