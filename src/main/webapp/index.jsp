<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         import="java.util.List, java.util.Map" %>
<%
    String ctx = request.getContextPath();
    // 读取 session 信息（角色判断）
    Object userRole = session.getAttribute("userRole");
    Object user = session.getAttribute("user");
    boolean isCustomer = user != null && "customer".equals(userRole != null ? userRole.toString() : "");

    // 处理 categoryId 的 active 判断
    String paramCategoryId = request.getParameter("categoryId");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>淘宝购物系统 - 首页</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="<%=ctx%>/css/style.css">
    <style>
        .announcement-bar { background: #fff3cd; padding: 10px; text-align: center; }
        .announcement-bar a { color: #856404; font-weight: bold; }
        .product-card { border: 1px solid #ddd; border-radius: 4px; padding: 10px; margin-bottom: 20px; transition: box-shadow 0.3s; }
        .product-card:hover { box-shadow: 0 4px 8px rgba(0,0,0,0.15); }
        .product-card img { width: 100%; height: 200px; object-fit: cover; }
        .product-card .price { color: #e4393c; font-size: 20px; font-weight: bold; }
        .product-card .name { font-size: 14px; margin: 8px 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
        .category-nav { margin: 20px 0; }
        .category-nav a { display: inline-block; padding: 5px 15px; margin: 3px; border: 1px solid #ddd; border-radius: 3px; color: #333; text-decoration: none; }
        .category-nav a:hover, .category-nav a.active { background: #e4393c; color: white; border-color: #e4393c; text-decoration: none; }
        .carousel-inner img { width: 100%; height: 300px; }
    </style>
</head>
<body>
    <%@ include file="header.jsp"%>

    <!-- 公告栏 -->
    <%
        Object announcements = request.getAttribute("announcements");
        if (announcements instanceof List && !((List<?>) announcements).isEmpty()) {
    %>
        <div class="announcement-bar">
    <%
            for (Object annObj : (List<?>) announcements) {
                pageContext.setAttribute("ann", annObj);
    %>
                <span>📢 <a href="<%=ctx%>/announcement/detail?id=${ann.id}">${ann.title}</a>&nbsp;&nbsp;|&nbsp;&nbsp;</span>
    <%
            }
    %>
        </div>
    <%
        }
    %>

    <!-- 轮播图 -->
    <div id="homeCarousel" class="carousel slide" data-ride="carousel" style="margin-top:50px;">
        <div class="carousel-inner">
            <div class="item active">
                <img src="<%=ctx%>/images/banner1.jpg" alt="Banner1">
                <div class="carousel-caption"><h3>欢迎来到淘宝购物系统</h3></div>
            </div>
            <div class="item">
                <img src="<%=ctx%>/images/banner2.jpg" alt="Banner2">
                <div class="carousel-caption"><h3>海量商品任你挑选</h3></div>
            </div>
        </div>
        <a class="left carousel-control" href="#homeCarousel" data-slide="prev">&lsaquo;</a>
        <a class="right carousel-control" href="#homeCarousel" data-slide="next">&rsaquo;</a>
    </div>

    <!-- 分类导航 -->
    <div class="container category-nav">
        <h4>商品分类</h4>
    <%
        Object categories = request.getAttribute("categories");
        if (categories instanceof List) {
            for (Object catObj : (List<?>) categories) {
                pageContext.setAttribute("cat", catObj);
                // 获取 id
                String idStr = "";
                String nameStr = "";
                try {
                    java.lang.reflect.Method mId = catObj.getClass().getMethod("getId");
                    Object idVal = mId.invoke(catObj);
                    if (idVal != null) idStr = idVal.toString();
                    java.lang.reflect.Method mName = catObj.getClass().getMethod("getName");
                    Object nameVal = mName.invoke(catObj);
                    if (nameVal != null) nameStr = nameVal.toString();
                } catch (Exception ignored) {}

                boolean isActive = (paramCategoryId != null && paramCategoryId.equals(idStr));
    %>
        <a href="<%=ctx%>/product/list?categoryId=<%=idStr%>" class="<%=isActive ? "active" : ""%>"><%=nameStr%></a>
    <%
            }
        }
    %>
    </div>

    <!-- 商品推荐 -->
    <div class="container">
        <h3>🔥 热门推荐</h3>
        <div class="row">
    <%
        Object products = request.getAttribute("recommendProducts");
        if (products instanceof List) {
            for (Object prodObj : (List<?>) products) {
                // 反射获取 product 的属性
                String idStr = "";
                String nameStr = "";
                String imageStr = "";
                String priceStr = "";
                try {
                    Class<?> cls = prodObj.getClass();
                    Object idVal = cls.getMethod("getId").invoke(prodObj);
                    if (idVal != null) idStr = idVal.toString();
                    Object nameVal = cls.getMethod("getName").invoke(prodObj);
                    if (nameVal != null) nameStr = nameVal.toString();
                    try {
                        Object imgVal = cls.getMethod("getMain_image").invoke(prodObj);
                        if (imgVal != null) imageStr = imgVal.toString();
                    } catch (NoSuchMethodException e1) {
                        try {
                            Object imgVal = cls.getMethod("getMainImage").invoke(prodObj);
                            if (imgVal != null) imageStr = imgVal.toString();
                        } catch (Exception ignored2) {}
                    }
                    Object priceVal = cls.getMethod("getPrice").invoke(prodObj);
                    if (priceVal != null) priceStr = priceVal.toString();
                } catch (Exception ignored) {}
    %>
            <div class="col-md-3 col-sm-6">
                <div class="product-card">
                    <a href="<%=ctx%>/product/detail?id=<%=idStr%>">
                        <img src="<%=imageStr%>" alt="<%=nameStr%>">
                    </a>
                    <div class="name"><a href="<%=ctx%>/product/detail?id=<%=idStr%>"><%=nameStr%></a></div>
                    <div class="price">￥<%=priceStr%></div>
    <%              if (isCustomer) { %>
                    <a href="<%=ctx%>/cart/add?productId=<%=idStr%>" class="btn btn-danger btn-sm">加入购物车</a>
    <%              } %>
                </div>
            </div>
    <%
            }
        }
    %>
        </div>
    </div>

    <%@ include file="footer.jsp"%>

    <script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>
