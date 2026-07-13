<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>商品列表 - 淘宝</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body>
<jsp:include page="header.jsp"/>
<div class="container product-section"><div class="section-header"><h2><c:if test="${not empty keyword}">搜索: ${keyword}</c:if><c:if test="${empty keyword}">全部商品</c:if></h2></div>
<div class="product-grid"><c:forEach var="p" items="${products}">
<div class="product-card"><div class="product-image"><a href="${pageContext.request.contextPath}/product/detail?id=${p[0]}">
<c:choose><c:when test="${p[3] == null || p[3].isEmpty() || (p[3] != null && p[3].startsWith('/upload/'))}">
<img src="https://picsum.photos/seed/${p[0]}/300/300" alt="${p[1]}">
</c:when><c:otherwise><img src="${p[3]}" alt="${p[1]}"></c:otherwise></c:choose></a></div>
<div class="product-info"><div class="product-name"><a href="${pageContext.request.contextPath}/product/detail?id=${p[0]}">${p[1]}</a></div>
<div class="product-price-row"><div class="product-price">￥${p[2]}</div></div></div></div>
</c:forEach></div></div>
<jsp:include page="footer.jsp"/></body></html>