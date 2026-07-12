<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>编辑商品</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<script src="https://cdn.bootcdn.net/ajax/libs/ckeditor/4.20.0/ckeditor.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><div class="container-fluid"><div class="row">
<div class="col-md-2 sidebar"><h4>🏪 商家后台</h4><a href="${pageContext.request.contextPath}/shop/product/list" class="active">商品管理</a></div>
<div class="col-md-10" style="padding:20px"><h3>${prodId!=null?'编辑商品':'新增商品'}</h3>
<form action="${pageContext.request.contextPath}/shop/product/save" method="post" enctype="multipart/form-data">
<c:if test="${prodId!=null}"><input type="hidden" name="id" value="${prodId}"></c:if>
<div class="form-group"><label>名称</label><input type="text" name="name" class="form-control" value="${prodName}" required></div>
<div class="form-group"><label>分类</label><select name="categoryId" class="form-control">
<c:forEach var="cat" items="${categories}"><option value="${cat[0]}" ${cat[0]==prodCatId?'selected':''}>${cat[1]}</option></c:forEach></select></div>
<div class="form-group"><label>价格</label><input type="number" name="price" step="0.01" class="form-control" value="${prodPrice}" required></div>
<div class="form-group"><label>库存</label><input type="number" name="stock" class="form-control" value="${prodStock}" required></div>
<div class="form-group"><label>详情（CKEdit）</label><textarea name="description" id="editor1" class="form-control" rows="10">${prodDesc}</textarea></div>
<div class="form-group"><label>图片</label><input type="file" name="images" multiple class="form-control"></div>
<button type="submit" class="btn btn-primary">${prodId!=null?'更新':'发布'}</button>
<a href="${pageContext.request.contextPath}/shop/product/list" class="btn btn-default">返回</a></form>
<script>CKEDITOR.replace('editor1');</script></div></div></div></body></html>