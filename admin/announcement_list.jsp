<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>公告管理</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><div class="container-fluid"><div class="row">
<div class="col-md-2 sidebar"><h4>🛡️ 运营商后台</h4><a href="${pageContext.request.contextPath}/admin/announcement/list" class="active">公告管理</a></div>
<div class="col-md-10" style="padding:20px"><h3>公告管理</h3>
<a href="${pageContext.request.contextPath}/admin/announcement/edit" class="btn btn-primary">新建公告</a>
<table class="table table-striped admin-table" style="margin-top:15px"><thead><tr><th>ID</th><th>标题</th><th>时间</th><th>操作</th></tr></thead>
<tbody><c:forEach var="ann" items="${announcements}"><tr>
<td>${ann[0]}</td><td>${ann[1]}</td><td>${ann[3]}</td>
<td><a href="${pageContext.request.contextPath}/admin/announcement/edit?id=${ann[0]}" class="btn btn-info btn-sm">编辑</a>
<a href="${pageContext.request.contextPath}/admin/announcement/delete?id=${ann[0]}" class="btn btn-danger btn-sm" onclick="return confirm('确定删除?')">删除</a></td></tr></c:forEach></tbody></table></div></div></div></body></html>