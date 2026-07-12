<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>店铺审核中</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <style>
        body { background: #f5f5f5; }
        .notice-box {
            max-width: 500px;
            margin: 100px auto;
            padding: 40px;
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            text-align: center;
        }
        .notice-icon { font-size: 60px; color: #f0ad4e; margin-bottom: 20px; }
        .notice-title { font-size: 24px; color: #333; margin-bottom: 15px; }
        .notice-msg { color: #666; margin-bottom: 30px; line-height: 1.8; }
    </style>
</head>
<body>
<div class="notice-box">
    <div class="notice-icon">⏳</div>
    <h2 class="notice-title">店铺审核中</h2>
    <p class="notice-msg">${msg != null ? msg : '您的店铺入驻申请已提交，正在等待平台管理员审核。'}</p>
    <a href="${pageContext.request.contextPath}/" class="btn btn-default">返回首页</a>
    <a href="${pageContext.request.contextPath}/customer/profile" class="btn btn-primary">个人中心</a>
</div>
</body>
</html>
