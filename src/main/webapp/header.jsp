<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<nav class="navbar navbar-inverse navbar-static-top">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">淘宝购物系统</a>
        </div>
        <div class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li><a href="${pageContext.request.contextPath}/">首页</a></li>
                <li><a href="${pageContext.request.contextPath}/product/list">全部商品</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <c:choose>
                    <%-- 未登录状态 --%>
                    <c:when test="${empty sessionScope.userId}">
                        <li><a href="${pageContext.request.contextPath}/login">登录</a></li>
                        <li><a href="${pageContext.request.contextPath}/register">注册</a></li>
                    </c:when>
                    <%-- 已登录状态 --%>
                    <c:otherwise>
                        <li><a href="${pageContext.request.contextPath}/cart/list">
                            <span class="glyphicon glyphicon-shopping-cart"></span> 购物车
                        </a></li>
                        <li><a href="${pageContext.request.contextPath}/order/list">
                            <span class="glyphicon glyphicon-list"></span> 我的订单
                        </a></li>
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                <span class="glyphicon glyphicon-user"></span>
                                ${not empty sessionScope.nickname ? sessionScope.nickname : sessionScope.user.username}
                                <span class="caret"></span>
                            </a>
                            <ul class="dropdown-menu">
                                <li><a href="${pageContext.request.contextPath}/customer/profile">个人中心</a></li>
                                <li><a href="${pageContext.request.contextPath}/address/list">收货地址</a></li>
                                <li><a href="${pageContext.request.contextPath}/aftersale">售后记录</a></li>
                                <li role="separator" class="divider"></li>
                                <li><a href="${pageContext.request.contextPath}/logout">退出登录</a></li>
                            </ul>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>

