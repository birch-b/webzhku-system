<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>商家入驻申请</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css"></head>
<body style="background:#f5f5f5;padding-top:50px"><div class="container"><h2>🏪 商家入驻申请</h2>
<c:choose><c:when test="${applyId!=null && applyStatus==0}"><div class="alert alert-warning">申请正在审核中</div></c:when>
<c:when test="${applyId!=null && applyStatus==1}"><div class="alert alert-success">申请已通过！<a href="${pageContext.request.contextPath}/shop/home" class="btn btn-primary">进入后台</a></div></c:when>
<c:when test="${applyId!=null && applyStatus==2}"><div class="alert alert-danger">被拒绝：${rejectReason}</div></c:when></c:choose>
<c:if test="${applyId==null || applyStatus==2}"><form action="${pageContext.request.contextPath}/shop/apply/submit" method="post" style="max-width:500px">
<div class="form-group"><label>店铺名称</label><input type="text" name="shopName" class="form-control" required></div>
<div class="form-group"><label>主营类目</label><input type="text" name="shopCategory" class="form-control"></div>
<div class="form-group"><label>描述</label><textarea name="description" class="form-control" rows="4"></textarea></div>
<div class="form-group"><label>联系人</label><input type="text" name="contactName" class="form-control"></div>
<div class="form-group"><label>联系电话</label><input type="text" name="contactPhone" class="form-control"></div>
<button type="submit" class="btn btn-primary btn-block">提交申请</button></form></c:if></div></body></html>