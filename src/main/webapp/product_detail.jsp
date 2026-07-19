<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title><c:out value="${prodName}"/> - 淘宝</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body>
<jsp:include page="header.jsp"/>
<div class="container"><div class="row">
<div class="col-md-5">
    <c:choose>
        <c:when test="${prodCover == null || prodCover.isEmpty() || (prodCover != null && prodCover.startsWith('/upload/'))}">
            <img src="https://picsum.photos/seed/${prodId}/300/300" class="img-responsive" alt="<c:out value='${prodName}'/>">
        </c:when>
        <c:when test="${prodCover.startsWith('http://') || prodCover.startsWith('https://')}">
            <img src="${prodCover}" class="img-responsive" alt="<c:out value='${prodName}'/>">
        </c:when>
        <c:otherwise>
            <img src="${pageContext.request.contextPath}${prodCover}" class="img-responsive" alt="<c:out value='${prodName}'/>">
        </c:otherwise>
    </c:choose>
</div>
<div class="col-md-7"><h2><c:out value="${prodName}"/></h2><p>店铺: <c:out value="${shopName}"/></p>
<h3 class="price">￥${prodPrice}</h3><p>库存: ${prodStock} | 销量: ${prodSales}</p>
<form action="${pageContext.request.contextPath}/cart/add" method="post" class="form-inline">
<input type="hidden" name="productId" value="${prodId}">
<input type="number" name="quantity" value="1" min="1" max="${prodStock}" class="form-control" style="width:80px">
<button class="btn btn-danger btn-lg">加入购物车</button></form>
<hr><div><c:out value="${prodDesc}"/></div></div></div>
<h3>💬 买家评价</h3>
<div class="review-list">
    <c:choose>
        <c:when test="${empty reviews}">
            <div class="review-empty">
                <div class="review-empty-icon">📭</div>
                <div class="review-empty-text">暂无评价，快来成为第一个评价的人吧！</div>
            </div>
        </c:when>
        <c:otherwise>
            <c:forEach var="r" items="${reviews}">
                <div class="review-card">
                    <div class="review-header">
                        <div class="review-user-info">
                            <div class="review-avatar"><c:out value="${fn:substring(r[0], 0, 1)}"/></div>
                            <div>
                                <div class="review-nickname"><c:out value="${r[0]}"/></div>
                                <div class="review-date">${r[4]}</div>
                            </div>
                        </div>
                        <div class="review-rating-display">
                            <c:forEach var="i" begin="1" end="5">
                                <span class="star${i > r[1] ? ' empty' : ''}">⭐</span>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="review-body"><c:out value="${r[2]}"/></div>
                    <c:if test="${r[3] != null}">
                        <div class="review-reply">
                            <div class="review-reply-header">商家回复</div>
                            <div class="review-reply-content"><c:out value="${r[3]}"/></div>
                        </div>
                    </c:if>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div></div>
<jsp:include page="footer.jsp"/></body></html>