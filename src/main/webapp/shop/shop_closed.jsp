<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>店铺已关闭</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body { background: #f5f7fa; padding-top: 80px; }
        .notice-box {
            max-width: 520px;
            margin: 0 auto;
            padding: 48px 36px;
            background: #fff;
            border-radius: 12px;
            box-shadow: var(--shadow-md);
            text-align: center;
            border-top: 4px solid var(--danger);
        }
        .notice-icon { font-size: 64px; color: var(--danger); margin-bottom: 16px; }
        .notice-title { font-size: 24px; color: var(--text); margin: 0 0 12px; font-weight:600; }
        .notice-msg { color: var(--text-secondary); margin-bottom: 32px; line-height: 1.8; }
    </style>
</head>
<body data-role="shopkeeper">
<div class="notice-box">
    <div class="notice-icon">🔒</div>
    <h2 class="notice-title">店铺已关闭</h2>
    <p class="notice-msg">${msg != null ? msg : '您的店铺已被平台关闭，请联系平台管理员了解详情。'}</p>
    <a href="${pageContext.request.contextPath}/" class="btn btn-default" style="margin-right:8px;">返回首页</a>
    <a href="${pageContext.request.contextPath}/customer/profile" class="btn btn-primary">联系客服</a>
</div>
</body>
</html>
