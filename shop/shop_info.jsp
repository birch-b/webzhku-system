<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>店铺信息</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://cdn.bootcdn.net/ajax/libs/ckeditor/4.20.0/ckeditor.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .avatar-preview { width: 120px; height: 120px; border-radius: 50%; object-fit: cover; border: 2px solid #ddd; }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-2 sidebar">
            <h4>🏪 商家后台</h4>
            <a href="${pageContext.request.contextPath}/shop/info/view">店铺信息</a>
            <a href="${pageContext.request.contextPath}/shop/product/list">商品管理</a>
            <a href="${pageContext.request.contextPath}/shop/order/list">订单管理</a>
            <a href="${pageContext.request.contextPath}/shop/category/list">分类管理</a>
            <a href="${pageContext.request.contextPath}/shop/review/list">评价管理</a>
            <a href="${pageContext.request.contextPath}/shop/aftersale/list">售后管理</a>
        </div>
        <div class="col-md-10" style="padding:20px;">
            <h3>店铺信息编辑</h3>
            <c:if test="${not empty param.msg}"><div class="alert alert-success">保存成功！</div></c:if>
            <form action="${pageContext.request.contextPath}/shop/info/update" method="post" enctype="multipart/form-data">
                <div class="form-group">
                    <label>店铺头像</label>
                    <div style="margin:10px 0;">
                        <img src="${not empty shopAvatar ? shopAvatar : '/upload/shop/default.png'}" class="avatar-preview" id="avatarPreview">
                    </div>
                    <input type="file" name="avatar" accept="image/*" onchange="previewAvatar(this)">
                    <p class="help-block">请上传正方形图片，建议尺寸200x200</p>
                </div>
                <div class="form-group">
                    <label>店铺名称</label>
                    <input type="text" name="shopName" class="form-control" value="${shopName}" required>
                </div>
                <div class="form-group">
                    <label>主营类目</label>
                    <input type="text" name="shopCategory" class="form-control" value="${shopCategory}">
                </div>
                <div class="form-group">
                    <label>店铺简介（CKEdit）</label>
                    <textarea name="description" id="editor1" class="form-control" rows="8">${shopDesc}</textarea>
                </div>
                <button type="submit" class="btn btn-primary">保存</button>
            </form>
            <script>
                CKEDITOR.replace('editor1');
                function previewAvatar(input) {
                    if (input.files && input.files[0]) {
                        var reader = new FileReader();
                        reader.onload = function(e) { document.getElementById('avatarPreview').src = e.target.result; };
                        reader.readAsDataURL(input.files[0]);
                    }
                }
            </script>
        </div>
    </div>
</div>
</body>
</html>
