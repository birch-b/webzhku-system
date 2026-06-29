<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>运营商登录</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<style>.login-box{max-width:400px;margin:80px auto;padding:30px;border:1px solid #ddd;border-radius:5px;background:#fff}
.login-box h2{text-align:center;color:#2c3e50}.btn-admin{background:#2c3e50;color:white;width:100%}</style>
</head><body style="background:#ecf0f1">
<div class="login-box"><h2>🛡️ 运营商登录</h2>
<c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>
<form action="${pageContext.request.contextPath}/admin/login" method="post">
<div class="form-group"><label>用户名</label><input type="text" class="form-control" name="username" required></div>
<div class="form-group"><label>密码</label><input type="password" class="form-control" name="password" required></div>
<button type="submit" class="btn btn-admin">登录</button></form></div></body></html>