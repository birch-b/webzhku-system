<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="com.taobao.entity.User"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
    String basePath = request.getContextPath();
    User user = (User) session.getAttribute("user");
    String userRole = (String) session.getAttribute("userRole");
%>
<!-- 全局导航栏 -->
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar">
                <span class="icon-bar"></span><span class="icon-bar"></span><span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="${basePath}/">淘宝购物系统</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="${basePath}/">首页</a></li>
                <li><a href="${basePath}/product/list">商品列表</a></li>
                <c:if test="${userRole == 'customer'}">
                    <li><a href="${basePath}/cart">购物车</a></li>
                    <li><a href="${basePath}/order/list">我的订单</a></li>
                    <li><a href="${basePath}/customer/profile">个人中心</a></li>
                </c:if>
                <c:if test="${userRole == 'shopkeeper'}">
                    <li><a href="${basePath}/shop/home">商家后台</a></li>
                </c:if>
                <c:if test="${userRole == 'operator'}">
                    <li><a href="${basePath}/admin/dashboard">运营商后台</a></li>
                </c:if>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <c:choose>
                    <c:when test="${sessionScope.user != null}">
                        <li><a href="#">欢迎，${sessionScope.user.nickname}</a></li>
                        <li><a href="${basePath}/logout">退出</a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="${basePath}/login">登录</a></li>
                        <li><a href="${basePath}/register">注册</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
            <form class="navbar-form navbar-right" action="${basePath}/product/search" method="get">
                <div class="form-group">
                    <input type="text" name="keyword" class="form-control" placeholder="搜索商品...">
                </div>
                <button type="submit" class="btn btn-default">搜索</button>
            </form>
        </div>
    </div>
</nav>
