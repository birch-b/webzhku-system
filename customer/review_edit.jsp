<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>评价</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<script src="https://cdn.bootcdn.net/ajax/libs/ckeditor/4.20.0/ckeditor.js"></script></head>
<body><jsp:include page="../header.jsp"/>
<div class="container"><h2>发表评价</h2>
<form action="${pageContext.request.contextPath}/review/submit" method="post">
<input type="hidden" name="orderId" value="${orderId}">
<input type="hidden" name="productId" value="${productId}">
<div class="form-group"><label>评分</label>
<div><input type="radio" name="rating" value="1">1⭐ <input type="radio" name="rating" value="2">2⭐
<input type="radio" name="rating" value="3" checked>3⭐ <input type="radio" name="rating" value="4">4⭐
<input type="radio" name="rating" value="5">5⭐</div></div>
<div class="form-group"><label>评价内容（CKEdit）</label><textarea name="content" id="editor1" rows="6" class="form-control"></textarea></div>
<button type="submit" class="btn btn-primary">提交评价</button>
<a href="${pageContext.request.contextPath}/order/list" class="btn btn-default">返回</a></form>
<script>CKEDITOR.replace('editor1');</script></div>
<jsp:include page="../footer.jsp"/></body></html>