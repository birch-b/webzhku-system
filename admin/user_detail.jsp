<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>用户详情</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><div class="container-fluid"><div class="row">
<div class="col-md-2 sidebar"><h4>🛡️ 运营商后台</h4><a href="${pageContext.request.contextPath}/admin/user/list" class="active">用户管理</a></div>
<div class="col-md-10" style="padding:20px"><h3>用户详情</h3>
<table class="table table-bordered"><tr><th>ID</th><td>${userDetail.id}</td></tr><tr><th>用户名</th><td>${userDetail.username}</td></tr>
<tr><th>昵称</th><td>${userDetail.nickname}</td></tr><tr><th>手机</th><td>${userDetail.phone}</td></tr>
<tr><th>邮箱</th><td>${userDetail.email}</td></tr><tr><th>状态</th><td>${userDetail.status==1?'正常':'禁用'}</td></tr>
<tr><th>注册时间</th><td>${userDetail.createTime}</td></tr></table>
<h4>角色变更</h4><form action="${pageContext.request.contextPath}/admin/user/changeRole" method="post" class="form-inline">
<input type="hidden" name="id" value="${userDetail.id}">
<select name="role" class="form-control"><option value="visitor">浏览者</option><option value="customer">顾客</option><option value="shopkeeper">商家</option><option value="operator">运营商</option></select>
<button class="btn btn-primary">变更</button></form>
<a href="${pageContext.request.contextPath}/admin/user/list" class="btn btn-default" style="margin-top:10px">返回</a>
</div></div></div></body></html>