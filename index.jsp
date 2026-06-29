<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>淘宝购物系统 - 首页</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .announcement-bar { background: #fff3cd; padding: 10px; text-align: center; }
        .announcement-bar a { color: #856404; font-weight: bold; }
        .product-card { border: 1px solid #ddd; border-radius: 4px; padding: 10px; margin-bottom: 20px; transition: box-shadow 0.3s; }
        .product-card:hover { box-shadow: 0 4px 8px rgba(0,0,0,0.15); }
        .product-card img { width: 100%; height: 200px; object-fit: cover; }
        .product-card .price { color: #e4393c; font-size: 20px; font-weight: bold; }
        .product-card .name { font-size: 14px; margin: 8px 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
        .category-nav { margin: 20px 0; }
        .category-nav a { display: inline-block; padding: 5px 15px; margin: 3px; border: 1px solid #ddd; border-radius: 3px; }
        .category-nav a:hover, .category-nav a.active { background: #e4393c; color: white; border-color: #e4393c; }
        .carousel-inner img { width: 100%; height: 300px; }
    </style>
</head>
<body>
    <%@ include file="header.jsp"%>

    <!-- 公告栏 -->
    <c:if test="${not empty announcements}">
        <div class="announcement-bar">
            <c:forEach var="ann" items="${announcements}">
                <span>📢 <a href="${pageContext.request.contextPath}/announcement/detail?id=${ann.id}">${ann.title}</a>&nbsp;&nbsp;|&nbsp;&nbsp;</span>
            </c:forEach>
        </div>
    </c:if>

    <!-- 轮播图 -->
    <div id="homeCarousel" class="carousel slide" data-ride="carousel" style="margin-top:50px;">
        <div class="carousel-inner">
            <div class="item active">
                <img src="${pageContext.request.contextPath}/images/banner1.jpg" alt="Banner1">
                <div class="carousel-caption"><h3>欢迎来到淘宝购物系统</h3></div>
            </div>
            <div class="item">
                <img src="${pageContext.request.contextPath}/images/banner2.jpg" alt="Banner2">
                <div class="carousel-caption"><h3>海量商品任你挑选</h3></div>
            </div>
        </div>
        <a class="left carousel-control" href="#homeCarousel" data-slide="prev">&lsaquo;</a>
        <a class="right carousel-control" href="#homeCarousel" data-slide="next">&rsaquo;</a>
    </div>

    <!-- 分类导航 -->
    <div class="container category-nav">
        <h4>商品分类</h4>
        <c:forEach var="cat" items="${categories}">
            <a href="${pageContext.request.contextPath}/product/list?categoryId=${cat.id}" 
               class="${param.categoryId == cat.id ? 'active' : ''}">${cat.name}</a>
        </c:forEach>
    </div>

    <!-- 商品推荐 -->
    <div class="container">
        <h3>🔥 热门推荐</h3>
        <div class="row">
            <c:forEach var="product" items="${recommendProducts}">
                <div class="col-md-3 col-sm-6">
                    <div class="product-card">
                        <a href="${pageContext.request.contextPath}/product/detail?id=${product.id}">
                            <img src="${product.main_image}" alt="${product.name}">
                        </a>
                        <div class="name"><a href="${pageContext.request.contextPath}/product/detail?id=${product.id}">${product.name}</a></div>
                        <div class="price">￥${product.price}</div>
                        <c:if test="${sessionScope.user != null && sessionScope.userRole == 'customer'}">
                            <a href="${pageContext.request.contextPath}/cart/add?productId=${product.id}" class="btn btn-danger btn-sm">加入购物车</a>
                        </c:if>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <%@ include file="footer.jsp"%>

    <script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>
