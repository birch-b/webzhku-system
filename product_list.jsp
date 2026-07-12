<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>商品列表 - 淘宝</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body>
<jsp:include page="header.jsp"/>
<div class="container"><h3>${not empty keyword ? '搜索: '+keyword : '全部商品'}</h3>
<div class="row"><c:forEach var="p" items="${products}">
<div class="col-md-3 col-sm-6"><div class="thumbnail product-card">
<a href="${pageContext.request.contextPath}/product/detail?id=${p[0]}">
<img src="${p[3]}" alt="${p[1]}" style="height:200px"><div class="caption">
<h5>${p[1]}</h5><p class="price">￥${p[2]}</p><p>销量: ${p[4]} | ${p[5]}</p></div></a></div></div>
</c:forEach></div></div>
<jsp:include page="footer.jsp"/></body></html>