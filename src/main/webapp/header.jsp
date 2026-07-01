<%@ page contentType="text/html;charset=UTF-8" language="java"
         import="com.taobao.entity.User" %>
<%
    Object userIdObj = session.getAttribute("userId");
    Object userRoleObj = session.getAttribute("userRole");
    Object nicknameObj = session.getAttribute("nickname");

    boolean loggedIn = userIdObj != null;
    String role = userRoleObj != null ? userRoleObj.toString() : "";
    String nickname = nicknameObj != null ? nicknameObj.toString() : "用户";
    String ctx = request.getContextPath();
%>
<nav class="navbar">
    <div class="container">
        <a class="navbar-brand" href="<%=ctx%>/">淘宝购物系统</a>
        <ul class="navbar-nav">
            <li><a href="<%=ctx%>/">首页</a></li>
            <li><a href="<%=ctx%>/product/list">全部商品</a></li>
            <% if ("operator".equals(role)) { %>
                <li><a href="<%=ctx%>/admin/stat/dashboard">后台管理</a></li>
            <% } else if ("shopkeeper".equals(role)) { %>
                <li><a href="<%=ctx%>/shop/home">商家后台</a></li>
            <% } %>
        </ul>
        <ul class="navbar-nav navbar-right">
            <% if (!loggedIn) { %>
                <li><a href="<%=ctx%>/login">登录</a></li>
                <li><a href="<%=ctx%>/register">注册</a></li>
            <% } else { %>
                <% if ("customer".equals(role)) { %>
                    <li><a href="<%=ctx%>/cart/list">🛒 购物车</a></li>
                    <li><a href="<%=ctx%>/order/list">📋 我的订单</a></li>
                <% } %>
                <li class="dropdown">
                    <a href="#">👤 <%=nickname%> ▾</a>
                    <ul class="dropdown-menu">
                        <% if ("customer".equals(role)) { %>
                            <li><a href="<%=ctx%>/customer/profile">个人中心</a></li>
                            <li><a href="<%=ctx%>/address/list">收货地址</a></li>
                            <li><a href="<%=ctx%>/aftersale">售后记录</a></li>
                        <% } else if ("shopkeeper".equals(role)) { %>
                            <li><a href="<%=ctx%>/shop/info/view">店铺信息</a></li>
                            <li><a href="<%=ctx%>/shop/product/list">商品管理</a></li>
                            <li><a href="<%=ctx%>/shop/order/list">订单管理</a></li>
                        <% } else if ("operator".equals(role)) { %>
                            <li><a href="<%=ctx%>/admin/stat/dashboard">数据概览</a></li>
                            <li><a href="<%=ctx%>/admin/user/list">用户管理</a></li>
                            <li><a href="<%=ctx%>/admin/shop/auditList">店铺审核</a></li>
                            <li><a href="<%=ctx%>/admin/order/list">订单监控</a></li>
                        <% } %>
                        <li role="separator" class="divider"></li>
                        <li><a href="<%=ctx%>/logout">退出登录</a></li>
                    </ul>
                </li>
            <% } %>
        </ul>
    </div>
</nav>
