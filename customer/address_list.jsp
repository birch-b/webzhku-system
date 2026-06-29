<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>收货地址</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css"></head>
<body><jsp:include page="../header.jsp"/>
<div class="container"><h2>收货地址管理</h2>
<c:forEach var="addr" items="${addresses}">
<div class="panel panel-default"><div class="panel-body">
<strong>${addr[1]}</strong> ${addr[2]} ${addr[3]}
<c:if test="${addr[4]=='1'}"><span class="label label-primary">默认</span></c:if>
<form method="post" style="display:inline"><input type="hidden" name="action" value="setDefault"><input type="hidden" name="id" value="${addr[0]}"><button class="btn btn-xs btn-default">设为默认</button></form>
<form method="post" style="display:inline"><input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="${addr[0]}"><button class="btn btn-xs btn-danger" onclick="return confirm('删除?')">删除</button></form>
</div></div></c:forEach>
<h3>新增地址</h3>
<form method="post" style="max-width:500px">
<input type="hidden" name="action" value="add">
<div class="form-group"><label>收货人</label><input type="text" name="receiverName" class="form-control" required></div>
<div class="form-group"><label>电话</label><input type="text" name="phone" class="form-control" required></div>
<div class="form-group"><label>省</label><input type="text" name="province" class="form-control"></div>
<div class="form-group"><label>市</label><input type="text" name="city" class="form-control"></div>
<div class="form-group"><label>区</label><input type="text" name="district" class="form-control"></div>
<div class="form-group"><label>详细地址</label><input type="text" name="detail" class="form-control"></div>
<div class="checkbox"><label><input type="checkbox" name="isDefault" value="1">设为默认地址</label></div>
<button class="btn btn-primary">保存</button></form></div>
<jsp:include page="../footer.jsp"/></body></html>