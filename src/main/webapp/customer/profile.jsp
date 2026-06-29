<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>个人中心 - 淘宝购物系统</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
.sidebar-nav { padding-top: 20px; }
.sidebar-nav .list-group-item { cursor: pointer; }
.sidebar-nav .list-group-item.active { background-color: #337ab7; color: #fff; }
</style>
</head>
<body><jsp:include page="../header.jsp"/>
<div class="container" style="margin-top:20px;">
<div class="row">
<!-- 左侧导航菜单 -->
<div class="col-md-3">
    <div class="list-group sidebar-nav">
        <a href="${pageContext.request.contextPath}/customer/profile" class="list-group-item active">个人信息</a>
        <a href="${pageContext.request.contextPath}/address/list" class="list-group-item">收货地址</a>
        <a href="${pageContext.request.contextPath}/order/list" class="list-group-item">我的订单</a>
        <a href="${pageContext.request.contextPath}/cart/list" class="list-group-item">购物车</a>
        <a href="${pageContext.request.contextPath}/aftersale" class="list-group-item">售后记录</a>
    </div>
</div>
<!-- 右侧内容 -->
<div class="col-md-9">
<h2>个人中心</h2>

<!-- 成功提示 -->
<c:if test="${not empty param.msg}">
<div class="alert alert-success">
    <c:choose>
        <c:when test="${param.msg=='updated'}">个人信息修改成功！</c:when>
        <c:when test="${param.msg=='passwordChanged'}">密码修改成功！</c:when>
        <c:otherwise>${param.msg}</c:otherwise>
    </c:choose>
</div>
</c:if>

<!-- 错误提示 -->
<c:if test="${not empty param.error}">
<div class="alert alert-danger">
    <c:choose>
        <c:when test="${param.error=='empty'}">请填写完整信息！</c:when>
        <c:when test="${param.error=='mismatch'}">两次输入的新密码不一致！</c:when>
        <c:when test="${param.error=='tooShort'}">新密码长度不能少于6位！</c:when>
        <c:when test="${param.error=='wrongPassword'}">原密码错误！</c:when>
        <c:when test="${param.error=='updateFail'}">信息修改失败，请重试！</c:when>
        <c:when test="${param.error=='changeFail'}">密码修改失败，请重试！</c:when>
        <c:when test="${param.error=='userNotFound'}">用户不存在！</c:when>
        <c:otherwise>操作失败，请重试！</c:otherwise>
    </c:choose>
</div>
</c:if>

<div class="row">
<div class="col-md-6">
<div class="panel panel-default">
<div class="panel-heading"><h3 class="panel-title">基本信息</h3></div>
<div class="panel-body">
<form action="${pageContext.request.contextPath}/customer/profile/update" method="post">
<div class="form-group"><label>用户名</label><input type="text" class="form-control" value="${username}" readonly></div>
<div class="form-group"><label>昵称</label><input type="text" name="nickname" class="form-control" value="${nickname}"></div>
<div class="form-group"><label>手机</label><input type="text" name="phone" class="form-control" value="${phone}"></div>
<div class="form-group"><label>邮箱</label><input type="text" name="email" class="form-control" value="${email}"></div>
<button type="submit" class="btn btn-primary">保存修改</button>
</form>
</div></div>
</div>
<div class="col-md-6">
<div class="panel panel-default">
<div class="panel-heading"><h3 class="panel-title">修改密码</h3></div>
<div class="panel-body">
<form action="${pageContext.request.contextPath}/customer/profile/changePassword" method="post" onsubmit="return checkPassword()">
<div class="form-group"><label>原密码</label><input type="password" name="oldPassword" class="form-control" required placeholder="请输入原密码"></div>
<div class="form-group"><label>新密码</label><input type="password" name="newPassword" class="form-control" required placeholder="请输入新密码（至少6位）"></div>
<div class="form-group"><label>确认新密码</label><input type="password" name="confirmPassword" class="form-control" required placeholder="请再次输入新密码"></div>
<button type="submit" class="btn btn-danger">修改密码</button>
</form>
</div></div>
</div>
</div>
</div>
</div>
</div>
<jsp:include page="../footer.jsp"/>
<script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script>
function checkPassword() {
    var newPwd = document.querySelector('input[name="newPassword"]').value;
    var confirmPwd = document.querySelector('input[name="confirmPassword"]').value;
    if (newPwd !== confirmPwd) {
        alert('两次输入的新密码不一致');
        return false;
    }
    if (newPwd.length < 6) {
        alert('新密码长度不能少于6位');
        return false;
    }
    return true;
}
</script>
</body></html>
