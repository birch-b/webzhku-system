<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav class="navbar">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/">淘宝购物系统</a>
        <ul class="navbar-nav">
            <li><a href="${pageContext.request.contextPath}/">首页</a></li>
            <li><a href="${pageContext.request.contextPath}/product/list">全部商品</a></li>
            <c:if test="${sessionScope.userRole == 'operator'}">
                <li><a href="${pageContext.request.contextPath}/admin/stat/dashboard">后台管理</a></li>
            </c:if>
            <c:if test="${sessionScope.userRole == 'shopkeeper'}">
                <li><a href="${pageContext.request.contextPath}/shop/home">商家后台</a></li>
            </c:if>
        </ul>
        <form class="navbar-search" action="${pageContext.request.contextPath}/product/search" method="get">
            <input type="text" name="keyword" placeholder="搜索商品..." value="">
            <button type="submit">搜索</button>
        </form>
        <ul class="navbar-nav navbar-right">
            <c:if test="${empty sessionScope.userId}">
                <li><a href="${pageContext.request.contextPath}/login">登录</a></li>
                <li><a href="${pageContext.request.contextPath}/register">注册</a></li>
            </c:if>
            <c:if test="${not empty sessionScope.userId}">
                <c:if test="${sessionScope.userRole == 'customer'}">
                    <li><a href="${pageContext.request.contextPath}/cart/list">🛒 购物车</a></li>
                    <li><a href="${pageContext.request.contextPath}/order/list">📋 我的订单</a></li>
                </c:if>
                <li class="dropdown">
                    <c:if test="${sessionScope.userRole == 'shopkeeper'}">
                        <a href="#" class="shopkeeper-nav-link">
                            👤 ${sessionScope.shopName != null ? sessionScope.shopName : sessionScope.nickname != null ? sessionScope.nickname : '用户'} ▾
                        </a>
                    </c:if>
                    <c:if test="${sessionScope.userRole != 'shopkeeper'}">
                        <a href="#">👤 ${sessionScope.nickname != null ? sessionScope.nickname : '用户'} ▾</a>
                    </c:if>
                    <ul class="dropdown-menu">
                        <c:if test="${sessionScope.userRole == 'customer'}">
                            <li><a href="${pageContext.request.contextPath}/customer/profile">个人中心</a></li>
                            <li><a href="${pageContext.request.contextPath}/address/list">收货地址</a></li>
                            <li><a href="${pageContext.request.contextPath}/aftersale">售后记录</a></li>
                        </c:if>
                        <c:if test="${sessionScope.userRole == 'shopkeeper'}">
                            <li><a href="${pageContext.request.contextPath}/shop/info/view">店铺信息</a></li>
                            <li><a href="${pageContext.request.contextPath}/shop/product/list">商品管理</a></li>
                            <li><a href="${pageContext.request.contextPath}/shop/order/list">订单管理</a></li>
                        </c:if>
                        <c:if test="${sessionScope.userRole == 'operator'}">
                            <li><a href="${pageContext.request.contextPath}/admin/stat/dashboard">数据概览</a></li>
                            <li><a href="${pageContext.request.contextPath}/admin/user/list">用户管理</a></li>
                            <li><a href="${pageContext.request.contextPath}/admin/shop/auditList">店铺审核</a></li>
                            <li><a href="${pageContext.request.contextPath}/admin/order/list">订单监控</a></li>
                        </c:if>
                        <li role="separator" class="divider"></li>
                        <li><a href="${pageContext.request.contextPath}/logout">退出登录</a></li>
                    </ul>
                </li>
            </c:if>
        </ul>
    </div>
</nav>