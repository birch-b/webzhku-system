<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.List,java.lang.reflect.Method" %>
<%
    String ctx = request.getContextPath();
    Object userRole = session.getAttribute("userRole");
    Object userObj = session.getAttribute("user");
    boolean isCustomer = userObj != null && "customer".equals(userRole != null ? userRole.toString() : "");
%>
<%!
    public String getProperty(Object obj, String propName) {
        if (obj == null) return "";
        if (obj instanceof java.util.Map) {
            java.util.Map map = (java.util.Map) obj;
            Object val = map.get(propName);
            return val == null ? "" : val.toString();
        }
        try {
            String getter = "get" + Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
            Method m = obj.getClass().getMethod(getter);
            Object val = m.invoke(obj);
            return val == null ? "" : val.toString();
        } catch (Exception e1) {
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
    <link rel="stylesheet" href="<%=ctx%>/css/style.css?v=4">
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
        <a href="<%=ctx%>/announcement/detail?id=<%=id%>"><%=title%></a>
    <%
            }
    %>
    </div>
    <%
        }
    %>

    <!-- 轮播图 -->
    <div class="carousel-section">
        <div class="carousel-wrapper">
            <div class="carousel">
                <div class="carousel-inner">
                    <div class="slide active">
                        <img src="https://picsum.photos/1200/400?random=1" alt="Banner1">
                        <div class="carousel-caption">
                            <h3>欢迎来到淘宝购物系统</h3>
                            <p>海量精选商品，品质保证，无忧购物</p>
                        </div>
                    </div>
                    <div class="slide">
                        <img src="https://picsum.photos/1200/400?random=2" alt="Banner2">
                        <div class="carousel-caption">
                            <h3>限时特惠 惊喜不断</h3>
                            <p>每日精选爆款，超低价格等你来抢</p>
                        </div>
                    </div>
                    <div class="slide">
                        <img src="https://picsum.photos/1200/400?random=3" alt="Banner3">
                        <div class="carousel-caption">
                            <h3>新品首发 抢先体验</h3>
                            <p>潮流新品第一时间上架，引领时尚前沿</p>
                        </div>
                    </div>
                    <div class="slide">
                        <img src="https://picsum.photos/1200/400?random=4" alt="Banner4">
                        <div class="carousel-caption">
                            <h3>品质保障 放心购物</h3>
                            <p>正品保证，七天无理由退换，售后无忧</p>
                        </div>
                    </div>
                    <div class="slide">
                        <img src="https://picsum.photos/1200/400?random=5" alt="Banner5">
                        <div class="carousel-caption">
                            <h3>会员专享 超值福利</h3>
                            <p>注册即送优惠券，积分兑换好礼不停</p>
                        </div>
                    </div>
                </div>
                <button class="carousel-btn prev" onclick="carouselPrev()">&lsaquo;</button>
                <button class="carousel-btn next" onclick="carouselNext()">&rsaquo;</button>
                <div class="carousel-dots">
                    <span class="dot active" onclick="goToSlideDot(0)"></span>
                    <span class="dot" onclick="goToSlideDot(1)"></span>
                    <span class="dot" onclick="goToSlideDot(2)"></span>
                    <span class="dot" onclick="goToSlideDot(3)"></span>
                    <span class="dot" onclick="goToSlideDot(4)"></span>
                </div>
            </div>
        </div>
    </div>

    <!-- 分类导航 -->
    <div class="page-container category-section">
        <div class="category-nav">
            <h4>商品分类</h4>
            <a href="javascript:void(0)" onclick="loadProducts('')" class="<%=request.getAttribute("selectedCategory") == null ? "active" : ""%>">全部</a>
            <%
                Object categories = request.getAttribute("categories");
                if (categories instanceof List) {
                    List catList = (List) categories;
                    String selectedCategory = (String) request.getAttribute("selectedCategory");
                    for (int i = 0; i < catList.size(); i++) {
                        Object cat = catList.get(i);
                        String name = getProperty(cat, "name");
                        boolean isActive = selectedCategory != null && selectedCategory.equals(name);
            %>
            <a href="javascript:void(0)" onclick="loadProducts('<%=name%>')" class="<%=isActive ? "active" : ""%>"><%=name%></a>
            <%
                    }
                }
            %>
        </div>
    </div>

    <!-- 商品列表 -->
    <div class="page-container product-section">
        <div class="section-header">
            <h2>
                <%
                    String selectedCategory = (String) request.getAttribute("selectedCategory");
                    if (selectedCategory != null && !selectedCategory.isEmpty()) {
                        out.print(selectedCategory);
                    } else {
                        out.print("全部商品");
                    }
                %>
            </h2>
            <a href="<%=ctx%>/product/list">查看全部 →</a>
        </div>
        <div class="product-grid">
            <%
                Object products = request.getAttribute("products");
                if (products instanceof List) {
                    List prodList = (List) products;
                    if (prodList.isEmpty()) {
            %>
            <div style="text-align: center; padding: 40px; color: #999;">暂无商品</div>
            <%
                    } else {
                        for (int i = 0; i < prodList.size(); i++) {
                            Object prod = prodList.get(i);
                            String id = getProperty(prod, "id");
                            String name = getProperty(prod, "name");
                            String image = getProperty(prod, "main_image");
                            if (image.equals("")) image = getProperty(prod, "mainImage");
                            if (image.equals("") || image.startsWith("/upload/")) {
                                image = "https://picsum.photos/300/300?random=" + id;
                            }
                            String price = getProperty(prod, "price");
                            String originalPrice = getProperty(prod, "originalPrice");
            %>
            <div class="product-card">
                <div class="product-image">
                    <a href="<%=ctx%>/product/detail?id=<%=id%>">
                        <img src="<%=image%>" alt="<%=name%>">
                    </a>
                </div>
                <div class="product-info">
                    <div class="product-name"><a href="<%=ctx%>/product/detail?id=<%=id%>"><%=name%></a></div>
                    <div class="product-desc">精选品质，限时特惠</div>
                    <div class="product-price-row">
                        <div class="product-price">
                            ￥<%=price%>
                            <% if (originalPrice != null && !originalPrice.equals("") && !originalPrice.equals(price)) { %>
                                <small>￥<%=originalPrice%></small>
                            <% } %>
                        </div>
                        <% if (isCustomer) { %>
                            <a href="<%=ctx%>/cart/add?productId=<%=id%>" class="add-cart-btn">加入购物车</a>
                        <% } %>
                    </div>
                </div>
            </div>
            <%
                        }
                    }
                }
            %>
        </div>
    </div>

    <%@ include file="footer.jsp"%>

    <script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script>
        var carouselIndex = 0;
        var carouselSlides = document.querySelectorAll('.carousel-inner .slide');
        var carouselDots = document.querySelectorAll('.carousel-dots .dot');
        var autoPlayTimer = null;
        
        function updateDots() {
            carouselDots.forEach(function(dot, index) {
                dot.classList.toggle('active', index === carouselIndex);
            });
        }
        
        function goToSlide(index, direction) {
            if (index === carouselIndex) return;
            
            var currentSlide = carouselSlides[carouselIndex];
            var targetSlide = carouselSlides[index];
            
            currentSlide.classList.remove('prev', 'next');
            targetSlide.classList.remove('prev', 'next');
            currentSlide.classList.add('active');
            
            if (direction === 'next') {
                targetSlide.style.transform = 'translateX(100%)';
                targetSlide.style.opacity = '0';
                targetSlide.style.transition = 'none';
                
                setTimeout(function() {
                    targetSlide.style.transition = '';
                    currentSlide.classList.remove('active');
                    currentSlide.classList.add('prev');
                    targetSlide.classList.add('active');
                    targetSlide.style.transform = '';
                    targetSlide.style.opacity = '';
                }, 20);
            } else {
                targetSlide.style.transform = 'translateX(-100%)';
                targetSlide.style.opacity = '0';
                targetSlide.style.transition = 'none';
                
                setTimeout(function() {
                    targetSlide.style.transition = '';
                    currentSlide.classList.remove('active');
                    currentSlide.classList.add('next');
                    targetSlide.classList.add('active');
                    targetSlide.style.transform = '';
                    targetSlide.style.opacity = '';
                }, 20);
            }
            
            setTimeout(function() {
                currentSlide.classList.remove('active', 'prev', 'next');
            }, 520);
            
            carouselIndex = index;
            updateDots();
        }
        
        function goToSlideDot(index) {
            if (index === carouselIndex) return;
            var direction = index > carouselIndex ? 'next' : 'prev';
            goToSlide(index, direction);
        }
        
        function carouselNext() {
            var nextIndex = (carouselIndex + 1) % carouselSlides.length;
            goToSlide(nextIndex, 'next');
        }
        
        function carouselPrev() {
            var prevIndex = (carouselIndex - 1 + carouselSlides.length) % carouselSlides.length;
            goToSlide(prevIndex, 'prev');
        }
        
        function startAutoPlay() {
            autoPlayTimer = setInterval(carouselNext, 5000);
        }
        
        function stopAutoPlay() {
            if (autoPlayTimer) {
                clearInterval(autoPlayTimer);
                autoPlayTimer = null;
            }
        }
        
        var carouselEl = document.querySelector('.carousel');
        if (carouselEl) {
            carouselEl.addEventListener('mouseenter', stopAutoPlay);
            carouselEl.addEventListener('mouseleave', startAutoPlay);
        }
        
        startAutoPlay();
        
        function loadProducts(category) {
            $('.category-nav a').removeClass('active');
            if (category === '') {
                $('.category-nav a:first').addClass('active');
            } else {
                $('.category-nav a:contains("' + category + '")').addClass('active');
            }
            
            var title = category === '' ? '全部商品' : category;
            $('.section-header h2').text(title);
            
            $.ajax({
                url: '<%=ctx%>/product/ajax',
                type: 'GET',
                data: { category: category },
                dataType: 'json',
                success: function(products) {
                    var html = '';
                    if (products.length === 0) {
                        html = '<div style="text-align: center; padding: 40px; color: #999;">暂无商品</div>';
                    } else {
                        $.each(products, function(index, p) {
                            var image = p.main_image && p.main_image !== '' && !p.main_image.startsWith('/upload/') 
                                ? p.main_image : 'https://picsum.photos/300/300?random=' + p.id;
                            html += '<div class="product-card">' +
                                '<div class="product-image">' +
                                '<a href="<%=ctx%>/product/detail?id=' + p.id + '">' +
                                '<img src="' + image + '" alt="' + p.name + '">' +
                                '</a></div>' +
                                '<div class="product-info">' +
                                '<div class="product-name"><a href="<%=ctx%>/product/detail?id=' + p.id + '">' + p.name + '</a></div>' +
                                '<div class="product-desc">精选品质，限时特惠</div>' +
                                '<div class="product-price-row">' +
                                '<div class="product-price">￥' + p.price + '</div>' +
                                '</div></div></div>';
                        });
                    }
                    $('.product-grid').html(html);
                }
            });
        }
    </script>
</body>
</html>