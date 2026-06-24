<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>注册 - 淘宝购物系统</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .register-container { max-width: 450px; margin: 60px auto; padding: 30px; border: 1px solid #ddd; border-radius: 5px; background: #fff; }
        .register-container h2 { text-align: center; color: #e4393c; }
        .btn-register { background: #e4393c; color: white; width: 100%; }
        .btn-register:hover { background: #c9302c; color: white; }
    </style>
</head>
<body>
    <div class="register-container">
        <h2>用户注册</h2>
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <form action="${pageContext.request.contextPath}/register" method="post">
            <div class="form-group">
                <label for="username">用户名 *</label>
                <input type="text" class="form-control" id="username" name="username" placeholder="请输入用户名" required>
            </div>
            <div class="form-group">
                <label for="password">密码 *</label>
                <input type="password" class="form-control" id="password" name="password" placeholder="至少6位" required>
            </div>
            <div class="form-group">
                <label for="nickname">昵称</label>
                <input type="text" class="form-control" id="nickname" name="nickname" placeholder="请输入昵称">
            </div>
            <div class="form-group">
                <label for="email">邮箱</label>
                <input type="email" class="form-control" id="email" name="email" placeholder="请输入邮箱">
            </div>
            <div class="form-group">
                <label for="phone">手机号</label>
                <input type="text" class="form-control" id="phone" name="phone" placeholder="请输入手机号">
            </div>
            <button type="submit" class="btn btn-register">注册</button>
        </form>
        <p class="text-center" style="margin-top:15px;">
            已有账号？<a href="${pageContext.request.contextPath}/login">立即登录</a>
        </p>
    </div>

    <script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>
