<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>淘宝购物系统 - 首页</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=4">
</head>
<body>
    <%@ include file="header.jsp"%>

    <div class="announcement-bar">
        <c:forEach var="ann" items="${requestScope.announcements}">
            <a href="${pageContext.request.contextPath}/announcement/detail?id=${ann.id}">${ann.title}</a>
        </c:forEach>
    </div>

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
            <div class="item">
                <img src="<%=ctx%>/common/images/3.webp" alt="Banner3">
                <div class="carousel-caption"><h3>品质保障 放心购物</h3></div>
            </div>
            <div class="item">
                <img src="<%=ctx%>/common/images/4.webp" alt="Banner4">
                <div class="carousel-caption"><h3>每日新品 限时特惠</h3></div>
            </div>
        </div>
    </div>

    <div class="page-container category-section">
        <div class="category-nav">
            <h4>商品分类</h4>
            <a href="javascript:void(0)" onclick="loadProducts('')" class="${empty requestScope.selectedCategory ? 'active' : ''}">全部</a>
            <c:forEach var="cat" items="${requestScope.categories}">
                <a href="javascript:void(0)" onclick="loadProducts('${cat.name}')" class="${requestScope.selectedCategory == cat.name ? 'active' : ''}">${cat.name}</a>
            </c:forEach>
        </div>
    </div>

    <div class="page-container product-section">
        <div class="section-header">
            <h2>
                <c:choose>
                    <c:when test="${not empty requestScope.selectedCategory}">${requestScope.selectedCategory}</c:when>
                    <c:otherwise>全部商品</c:otherwise>
                </c:choose>
            </h2>
            <a href="${pageContext.request.contextPath}/product/list">查看全部 →</a>
        </div>
        <div class="product-grid">
            <c:if test="${empty requestScope.products}">
                <div style="text-align: center; padding: 40px; color: #999;">暂无商品</div>
            </c:if>
            <c:forEach var="prod" items="${requestScope.products}">
                <c:set var="image" value="${prod.main_image}"/>
                <c:if test="${empty image or image.startsWith('/upload/')}">
                    <c:set var="image" value="https://picsum.photos/300/300?random=${prod.id}"/>
                </c:if>
                <div class="product-card">
                    <div class="product-image">
                        <a href="${pageContext.request.contextPath}/product/detail?id=${prod.id}">
                            <img src="${image}" alt="${prod.name}">
                        </a>
                    </div>
                    <div class="product-info">
                        <div class="product-name"><a href="${pageContext.request.contextPath}/product/detail?id=${prod.id}">${prod.name}</a></div>
                        <div class="product-desc">精选品质，限时特惠</div>
                        <div class="product-price-row">
                            <div class="product-price">
                                ￥${prod.price}
                                <c:if test="${not empty prod.originalPrice and prod.originalPrice != prod.price}">
                                    <small>￥${prod.originalPrice}</small>
                                </c:if>
                            </div>
                            <c:if test="${sessionScope.userRole == 'customer'}">
                                <a href="${pageContext.request.contextPath}/cart/add?productId=${prod.id}" class="add-cart-btn">加入购物车</a>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:forEach>
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
                url: '${pageContext.request.contextPath}/product/ajax',
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
                                '<a href="${pageContext.request.contextPath}/product/detail?id=' + p.id + '">' +
                                '<img src="' + image + '" alt="' + p.name + '">' +
                                '</a></div>' +
                                '<div class="product-info">' +
                                '<div class="product-name"><a href="${pageContext.request.contextPath}/product/detail?id=' + p.id + '">' + p.name + '</a></div>' +
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