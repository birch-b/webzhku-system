<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>运营商登录 - 淘宝后台</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin.css?v=2">
</head>
<body>
<div class="login-wrapper">
    <div class="login-card">
        <div class="logo">🛡️</div>
        <h1>运营商管理后台</h1>
        <div class="subtitle">Taobao Operator Console</div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger">⚠️ <%= request.getAttribute("error") %></div>
        <% } %>

        <form action="${pageContext.request.contextPath}/admin/login" method="post">
            <div class="form-group">
                <label>用户名</label>
                <input type="text" class="form-control" name="username" placeholder="请输入管理员账号" required autofocus>
            </div>
            <div class="form-group">
                <label>密码</label>
                <input type="password" class="form-control" name="password" placeholder="请输入密码" required>
            </div>
            <button type="submit" class="btn btn-primary btn-block btn-lg">
                <span>🚪</span>
                <span>登 录</span>
            </button>
        </form>

        <div style="margin-top:24px;padding-top:20px;border-top:1px solid #edf0f3;text-align:center;color:#95a5a6;font-size:12px;">
            仅支持运营商账号登录 · Powered by Java Servlet + MySQL
        </div>
    </div>
</div>
</body>
</html>
