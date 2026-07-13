<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>${prodName} - 淘宝</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body>
<jsp:include page="header.jsp"/>
<div class="container"><div class="row">
<div class="col-md-5">
    <c:choose>
        <c:when test="${prodCover == null || prodCover.isEmpty() || (prodCover != null && prodCover.startsWith('/upload/'))}">
            <img src="https://picsum.photos/seed/${prodId}/300/300" class="img-responsive" alt="${prodName}">
        </c:when>
        <c:when test="${prodCover.startsWith('http://') || prodCover.startsWith('https://')}">
            <img src="${prodCover}" class="img-responsive" alt="${prodName}">
        </c:when>
        <c:otherwise>
            <img src="${pageContext.request.contextPath}${prodCover}" class="img-responsive" alt="${prodName}">
        </c:otherwise>
    </c:choose>
</div>
<div class="col-md-7"><h2>${prodName}</h2><p>店铺: ${shopName}</p>
<h3 class="price">￥${prodPrice}</h3><p>库存: ${prodStock} | 销量: ${prodSales}</p>
<form action="${pageContext.request.contextPath}/cart/add" method="post" class="form-inline">
<input type="hidden" name="productId" value="${prodId}">
<input type="number" name="quantity" value="1" min="1" max="${prodStock}" class="form-control" style="width:80px">
<button class="btn btn-danger btn-lg">加入购物车</button></form>
<hr><div>${prodDesc}</div></div></div>
<h3>买家评价</h3><c:forEach var="r" items="${reviews}">
<div class="panel panel-default"><div class="panel-heading">${r[0]} | ${r[1]}⭐ | ${r[4]}</div>
<div class="panel-body">${r[2]}<c:if test="${r[3]!=null}"><br><b>商家回复：</b>${r[3]}</c:if></div></div>
</c:forEach></div>
<jsp:include page="footer.jsp"/></body></html>