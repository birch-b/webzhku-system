<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>收货地址 - 淘宝购物系统</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
body { background-color: #f5f5f5; }
.addr-panel { margin-bottom: 15px; }
.addr-default { border-left: 3px solid #337ab7; }
</style>
</head>
<body><jsp:include page="../header.jsp"/>
<div class="container" style="margin-top:20px;">
<div class="row">
<!-- 左侧导航菜单 -->
<div class="col-md-3">
    <div class="list-group">
        <a href="${pageContext.request.contextPath}/customer/profile" class="list-group-item">个人信息</a>
        <a href="${pageContext.request.contextPath}/address/list" class="list-group-item active">收货地址</a>
        <a href="${pageContext.request.contextPath}/order/list" class="list-group-item">我的订单</a>
        <a href="${pageContext.request.contextPath}/cart/list" class="list-group-item">购物车</a>
        <a href="${pageContext.request.contextPath}/aftersale" class="list-group-item">售后记录</a>
    </div>
</div>
<!-- 右侧内容 -->
<div class="col-md-9">
<h2>收货地址管理</h2>

<!-- 成功提示 -->
<c:if test="${not empty param.msg}"><div class="alert alert-success">
    <c:choose>
        <c:when test="${param.msg=='added'}">地址添加成功</c:when>
        <c:when test="${param.msg=='updated'}">地址修改成功</c:when>
        <c:when test="${param.msg=='deleted'}">地址删除成功</c:when>
        <c:when test="${param.msg=='defaultSet'}">默认地址设置成功</c:when>
    </c:choose></div></c:if>

<!-- 错误提示 -->
<c:if test="${not empty param.error}"><div class="alert alert-danger">
    <c:choose>
        <c:when test="${param.error=='empty'}">请填写完整信息（收货人、电话、详细地址为必填）</c:when>
        <c:otherwise>操作失败，请重试</c:otherwise>
    </c:choose></div></c:if>

<!-- 地址列表 -->
<c:choose>
    <c:when test="${empty addresses}">
        <div class="alert alert-warning">您还没有添加收货地址，请在下方添加。</div>
    </c:when>
    <c:otherwise>
        <c:forEach var="addr" items="${addresses}">
        <div class="panel panel-default addr-panel ${addr[4]=='1' ? 'addr-default' : ''}">
        <div class="panel-body">
            <div class="row">
                <div class="col-md-8">
                    <strong>${addr[1]}</strong> &nbsp; ${addr[2]}
                    <br><span class="text-muted">${addr[3]}</span>
                    <c:if test="${addr[4]=='1'}"><span class="label label-primary" style="margin-left:10px;">默认</span></c:if>
                </div>
                <div class="col-md-4 text-right">
                    <c:if test="${addr[4]!='1'}">
                    <form method="post" action="${pageContext.request.contextPath}/address/list" style="display:inline">
                        <input type="hidden" name="action" value="setDefault">
                        <input type="hidden" name="id" value="${addr[0]}">
                        <button class="btn btn-xs btn-default">设为默认</button>
                    </form>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/address/edit?id=${addr[0]}" class="btn btn-xs btn-info">编辑</a>
                    <form method="post" action="${pageContext.request.contextPath}/address/list" style="display:inline">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="${addr[0]}">
                        <button class="btn btn-xs btn-danger" onclick="return confirm('确定删除该地址?')">删除</button>
                    </form>
                </div>
            </div>
        </div></div>
        </c:forEach>
    </c:otherwise>
</c:choose>

<!-- 编辑表单（仅在编辑模式下显示） -->
<c:if test="${editMode}">
<div class="panel panel-primary" style="margin-top:20px;">
<div class="panel-heading"><h3 class="panel-title">编辑地址</h3></div>
<div class="panel-body">
<form method="post" action="${pageContext.request.contextPath}/address/list" style="max-width:500px">
    <input type="hidden" name="action" value="edit">
    <input type="hidden" name="id" value="${addrId}">
    <div class="form-group"><label>收货人 <span class="text-danger">*</span></label>
        <input type="text" name="receiverName" class="form-control" value="${receiverName}" required></div>
    <div class="form-group"><label>电话 <span class="text-danger">*</span></label>
        <input type="text" name="phone" class="form-control" value="${phone}" required></div>
    <div class="row">
        <div class="col-md-4"><div class="form-group"><label>省</label>
            <input type="text" name="province" class="form-control" value="${province}"></div></div>
        <div class="col-md-4"><div class="form-group"><label>市</label>
            <input type="text" name="city" class="form-control" value="${city}"></div></div>
        <div class="col-md-4"><div class="form-group"><label>区</label>
            <input type="text" name="district" class="form-control" value="${district}"></div></div>
    </div>
    <div class="form-group"><label>详细地址 <span class="text-danger">*</span></label>
        <input type="text" name="detail" class="form-control" value="${detail}" required></div>
    <div class="checkbox"><label>
        <input type="checkbox" name="isDefault" value="1" ${isDefault==1 ? 'checked' : ''}> 设为默认地址
    </label></div>
    <button type="submit" class="btn btn-primary">保存修改</button>
    <a href="${pageContext.request.contextPath}/address/list" class="btn btn-default">取消</a>
</form>
</div></div>
</c:if>

<!-- 新增地址表单 -->
<div class="panel panel-default" style="margin-top:20px;">
<div class="panel-heading"><h3 class="panel-title">新增地址</h3></div>
<div class="panel-body">
<form method="post" action="${pageContext.request.contextPath}/address/list" style="max-width:500px">
    <input type="hidden" name="action" value="add">
    <div class="form-group"><label>收货人 <span class="text-danger">*</span></label>
        <input type="text" name="receiverName" class="form-control" required></div>
    <div class="form-group"><label>电话 <span class="text-danger">*</span></label>
        <input type="text" name="phone" class="form-control" required></div>
    <div class="row">
        <div class="col-md-4"><div class="form-group"><label>省</label>
            <input type="text" name="province" class="form-control"></div></div>
        <div class="col-md-4"><div class="form-group"><label>市</label>
            <input type="text" name="city" class="form-control"></div></div>
        <div class="col-md-4"><div class="form-group"><label>区</label>
            <input type="text" name="district" class="form-control"></div></div>
    </div>
    <div class="form-group"><label>详细地址 <span class="text-danger">*</span></label>
        <input type="text" name="detail" class="form-control" required></div>
    <div class="checkbox"><label>
        <input type="checkbox" name="isDefault" value="1"> 设为默认地址
    </label></div>
    <button type="submit" class="btn btn-primary">添加地址</button>
</form>
</div></div>

</div>
</div>
</div>
<jsp:include page="../footer.jsp"/>
<script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body></html>
