<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>${prodId!=null?'编辑商品':'新增商品'} - 商家后台 - 淘宝购物系统</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://cdn.bootcdn.net/ajax/libs/ckeditor/4.20.0/ckeditor.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body data-role="shopkeeper">
<jsp:include page="../header.jsp"/>

<div class="container" style="margin-top:20px;">
    <div class="row">
        <div class="col-md-2">
            <ul class="nav nav-pills nav-stacked">
                <li><a href="${pageContext.request.contextPath}/shop/home">🏠 店铺首页</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/info/view">🏪 店铺信息</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/category/list">📂 分类管理</a></li>
                <li class="active"><a href="${pageContext.request.contextPath}/shop/product/list">📦 商品管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/order/list">🧾 订单管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/review/list">💬 评价管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/aftersale/list">🔁 售后管理</a></li>
            </ul>
        </div>

        <div class="col-md-10">
            <h2 style="margin:10px 0 24px;padding-bottom:14px;border-bottom:2px solid var(--primary);">${prodId!=null?'✏️ 编辑商品':'➕ 新增商品'}</h2>

            <div class="card" style="background:#fff;border-radius:8px;box-shadow:var(--shadow);padding:24px;">
                <form action="${pageContext.request.contextPath}/shop/product/save" method="post" enctype="multipart/form-data">
                    <c:if test="${prodId!=null}"><input type="hidden" name="id" value="${prodId}"></c:if>
                    <div class="form-group">
                        <label>商品名称</label>
                        <input type="text" name="name" class="form-control" value="${prodName}" required>
                    </div>
                    <div class="form-group">
                        <label>所属分类</label>
                        <select name="categoryId" class="form-control">
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat[0]}" ${cat[0]==prodCatId?'selected':''}>${cat[1]}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>价格（元）</label>
                                <input type="number" name="price" step="0.01" min="0" class="form-control" value="${prodPrice}" required>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>库存数量</label>
                                <input type="number" name="stock" min="0" class="form-control" value="${prodStock}" required>
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>商品详情（CKEdit）</label>
                        <textarea name="description" id="editor1" class="form-control" rows="10">${prodDesc}</textarea>
                    </div>
                    <div class="form-group">
                        <label>上传图片（可多张）</label>
                        <input type="file" name="images" multiple class="form-control">
                        <p class="help-block">支持 jpg/png/webp 格式，建议首图为商品正面图</p>
                    </div>
                    <div style="margin-top:20px;">
                        <button type="submit" class="btn btn-primary">${prodId!=null?'💾 更新商品':'🚀 发布商品'}</button>
                        <a href="${pageContext.request.contextPath}/shop/product/list" class="btn btn-default">取消返回</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../footer.jsp"/>

<script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script>CKEDITOR.replace('editor1');</script>
</body>
</html>
