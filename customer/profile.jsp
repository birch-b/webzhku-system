<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>个人中心</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css"></head>
<body><jsp:include page="../header.jsp"/>
<div class="container"><h2>个人中心</h2>
<c:if test="${not empty msg}"><div class="alert alert-success">${msg}</div></c:if>
<c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>
<div class="row"><div class="col-md-6"><h3>基本信息</h3>
<form action="${pageContext.request.contextPath}/customer/profile/update" method="post">
<input type="hidden" name="action" value="updateInfo">
<div class="form-group"><label>用户名</label><input type="text" class="form-control" value="${username}" readonly></div>
<div class="form-group"><label>昵称</label><input type="text" name="nickname" class="form-control" value="${nickname}"></div>
<div class="form-group"><label>手机</label><input type="text" name="phone" class="form-control" value="${phone}"></div>
<div class="form-group"><label>邮箱</label><input type="text" name="email" class="form-control" value="${email}"></div>
<button class="btn btn-primary">保存</button></form></div>
<div class="col-md-6"><h3>修改密码</h3>
<form action="${pageContext.request.contextPath}/customer/profile/changePassword" method="post">
<input type="hidden" name="action" value="changePassword">
<div class="form-group"><label>原密码</label><input type="password" name="oldPassword" class="form-control" required></div>
<div class="form-group"><label>新密码</label><input type="password" name="newPassword" class="form-control" required></div>
<button class="btn btn-danger">修改密码</button></form></div></div></div>
<jsp:include page="../footer.jsp"/></body></html>