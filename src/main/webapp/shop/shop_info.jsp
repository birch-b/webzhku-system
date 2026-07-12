<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>店铺信息 - 商家后台 - 淘宝购物系统</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://cdn.bootcdn.net/ajax/libs/ckeditor/4.20.0/ckeditor.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        /* ⭐⭐⭐ 头像显示三层防护：外层圆形容器锁死尺寸+溢出裁剪；img撑满100% cover，多大图都只裁120px圆 */
        .avatar-preview-wrap {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            overflow: hidden;
            border: 2px solid var(--border);
            background: #f8fafc;
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }
        .avatar-preview-wrap img {
            display: block;
            width: 100% !important;
            height: 100% !important;
            object-fit: cover;
            border: 0 !important;
            border-radius: 0 !important;
            max-width: none !important;
            max-height: none !important;
        }
    </style>
</head>
<body data-role="shopkeeper">
<jsp:include page="../header.jsp"/>

<div class="container" style="margin-top:20px;">
    <div class="row">
        <div class="col-md-2">
            <ul class="nav nav-pills nav-stacked">
                <li><a href="${pageContext.request.contextPath}/shop/home">🏠 店铺首页</a></li>
                <li class="active"><a href="${pageContext.request.contextPath}/shop/info/view">🏪 店铺信息</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/category/list">📂 分类管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/product/list">📦 商品管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/order/list">🧾 订单管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/review/list">💬 评价管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/aftersale/list">🔁 售后管理</a></li>
            </ul>
        </div>

        <div class="col-md-10">
            <h2 style="margin:10px 0 24px;padding-bottom:14px;border-bottom:2px solid var(--primary);">🏪 店铺信息编辑</h2>

            <c:if test="${not empty param.msg}"><div class="alert alert-success">保存成功！</div></c:if>
            <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

            <div class="card" style="background:#fff;border-radius:8px;box-shadow:0 1px 3px rgba(0,0,0,0.06), 0 1px 2px rgba(0,0,0,0.04);padding:24px;">
                <form action="${pageContext.request.contextPath}/shop/info/update" method="post" enctype="multipart/form-data">
                    <div class="form-group">
                        <label>店铺头像</label>
                        <div style="margin:10px 0;">
                            <%-- ⭐⭐⭐ 头像路径拼接：和header.jsp L22-29同逻辑，保证路径正确永不404 --%>
                            <c:set var="ctx" value="${pageContext.request.contextPath}"/>
                            <c:set var="rawAvatar" value="${not empty shopAvatar ? shopAvatar : '/upload/shop/default.png'}"/>
                            <c:choose>
                                <c:when test="${fn:startsWith(rawAvatar,'http://') or fn:startsWith(rawAvatar,'https://')}">
                                    <c:set var="avatarFullUrl" value="${rawAvatar}"/>
                                </c:when>
                                <c:otherwise>
                                    <%-- EL 不支持 += 复合赋值，必须用 concat() 方法拼接，否则 JSP 编译 500 --%>
                                    <c:set var="avatarFullUrl" value="${rawAvatar.startsWith('/') ? ctx.concat(rawAvatar) : ctx.concat('/').concat(rawAvatar)}"/>
                                </c:otherwise>
                            </c:choose>
                            <%-- 外层容器 overflow:hidden 强制裁剪，10000×10000大照片也只会显示120px圆 --%>
                            <div class="avatar-preview-wrap">
                                <img src="${avatarFullUrl}" id="avatarPreview" alt="店铺头像">
                            </div>
                        </div>
                        <input type="file" name="avatar" accept="image/*" onchange="previewAvatar(this)">
                        <p class="help-block">请上传正方形图片，建议尺寸200×200（无论上传多大都会被裁成圆形预览，不会撑爆页面）</p>
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
                    <button type="submit" class="btn btn-primary">💾 保存</button>
                    <a href="${pageContext.request.contextPath}/shop/home" class="btn btn-default">返回</a>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../footer.jsp"/>

<script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
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
</body>
</html>
