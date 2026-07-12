<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.List,java.lang.reflect.Method" %>
<%
    String ctx = request.getContextPath();
    Object userRole = session.getAttribute("userRole");
    Object userObj = session.getAttribute("user");
    boolean isCustomer = userObj != null && "customer".equals(userRole != null ? userRole.toString() : "");
    String paramCategoryId = request.getParameter("categoryId");

    // 工具方法：从对象中通过 getter 取属性
    // 注意：这里直接在页面内用反射，避免额外依赖
    // helper function via inline
%>
<%!
    // JSP 声明：辅助方法（对本文件内所有 scriptlet 可见）
    public String getProperty(Object obj, String propName) {
        if (obj == null) return "";
        try {
            // 尝试 getXxx()
            String getter = "get" + Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
            Method m = obj.getClass().getMethod(getter);
            Object val = m.invoke(obj);
            return val == null ? "" : val.toString();
        } catch (Exception e1) {
            // 尝试直接 get_property_name (下划线风格)
            try {
                Method m = obj.getClass().getMethod("get_" + propName);
                Object val = m.invoke(obj);
                return val == null ? "" : val.toString();
            } catch (Exception e2) {
                return "";
            }
        }
    }
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

        /* 轮播图尺寸控制：不铺满全屏，中等宽度，自适应高度 */
        #homeCarousel {
            max-width: 900px;          /* 桌面端最大宽度，不再是 100% 全屏 */
            margin: 0 auto;            /* 居中 */
            border-radius: 6px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        #homeCarousel .carousel-inner > .item {
            height: 240px;             /* 固定高度，比原来 300px 更小 */
            background: #f5f5f5;       /* object-fit 留空时的背景色，避免白边突兀 */
        }
        #homeCarousel .carousel-inner img {
            width: 100%;
            height: 100%;
            object-fit: contain;       /* 图片完整显示，不变形不裁切 */
        }
        /* 移动端再小一档 */
        @media (max-width: 768px) {
            #homeCarousel { max-width: 100%; border-radius: 0; }
            #homeCarousel .carousel-inner > .item { height: 180px; }
        }
    </style>
</head>
<body>
    <%@ include file="header.jsp"%>

    <!-- 公告栏 -->
    <%
        Object announcements = request.getAttribute("announcements");
        if (announcements instanceof List && !((List) announcements).isEmpty()) {
    %>
    <div class="announcement-bar">
    <%
            List annList = (List) announcements;
            for (int i = 0; i < annList.size(); i++) {
                Object ann = annList.get(i);
                String id = getProperty(ann, "id");
                String title = getProperty(ann, "title");
    %>
        <span><a href="<%=ctx%>/announcement/detail?id=<%=id%>"><%=title%></a>&nbsp;&nbsp;|&nbsp;&nbsp;</span>
    <%
            }
    %>
    </div>
    <%
        }
    %>

    <!-- 轮播图 -->
    <div id="homeCarousel" class="carousel slide" data-ride="carousel" style="margin-top:50px;">
        <!-- 轮播指示器 -->
        <ol class="carousel-indicators">
            <li data-target="#homeCarousel" data-slide-to="0" class="active"></li>
            <li data-target="#homeCarousel" data-slide-to="1"></li>
            <li data-target="#homeCarousel" data-slide-to="2"></li>
            <li data-target="#homeCarousel" data-slide-to="3"></li>
        </ol>
        <div class="carousel-inner">
            <div class="item active">
                <img src="<%=ctx%>/common/images/1.webp" alt="Banner1">
                <div class="carousel-caption"><h3>欢迎来到淘宝购物系统</h3></div>
            </div>
            <div class="item">
                <img src="<%=ctx%>/common/images/2.webp" alt="Banner2">
                <div class="carousel-caption"><h3>海量商品任你挑选</h3></div>
            </div>
            <div class="item">
                <img src="<%=ctx%>/common/images/3.webp" alt="Banner3">
                <div class="carousel-caption"><h3>品质保障 放心购物</h3></div>
            </div>
            <div class="item">
                <img src="<%=ctx%>/common/images/4.webp" alt="Banner4">
                <div class="carousel-caption"><h3>每日新品 限时特惠</h3></div>
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
            List catList = (List) categories;
            for (int i = 0; i < catList.size(); i++) {
                Object cat = catList.get(i);
                String id = getProperty(cat, "id");
                String name = getProperty(cat, "name");
                boolean isActive = paramCategoryId != null && paramCategoryId.equals(id);
    %>
        <a href="<%=ctx%>/product/list?categoryId=<%=id%>" class="<%=isActive ? "active" : ""%>"><%=name%></a>
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
            List prodList = (List) products;
            for (int i = 0; i < prodList.size(); i++) {
                Object prod = prodList.get(i);
                String id = getProperty(prod, "id");
                String name = getProperty(prod, "name");
                String image = getProperty(prod, "main_image");
                if (image.equals("")) image = getProperty(prod, "mainImage");
                String price = getProperty(prod, "price");
    %>
            <div class="col-md-3 col-sm-6">
                <div class="product-card">
                    <a href="<%=ctx%>/product/detail?id=<%=id%>">
                        <img src="<%=image%>" alt="<%=name%>">
                    </a>
                    <div class="name"><a href="<%=ctx%>/product/detail?id=<%=id%>"><%=name%></a></div>
                    <div class="price">￥<%=price%></div>
    <%          if (isCustomer) { %>
                    <a href="<%=ctx%>/cart/add?productId=<%=id%>" class="btn btn-danger btn-sm">加入购物车</a>
    <%          } %>
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
