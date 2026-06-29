<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Object userIdObj = session.getAttribute("userId");
    Object userRoleObj = session.getAttribute("userRole");
    Object userObj = session.getAttribute("user");

    boolean loggedIn = userIdObj != null;
    String role = userRoleObj != null ? userRoleObj.toString() : "";
    String nickname = "";
    if (userObj != null) {
        try {
            java.lang.reflect.Method m = userObj.getClass().getMethod("getNickname");
            Object n = m.invoke(userObj);
            if (n != null) nickname = n.toString();
        } catch (Exception ignored) {}
    }
    if (nickname.isEmpty() && userObj != null) {
        try {
            java.lang.reflect.Method m = userObj.getClass().getMethod("getUsername");
            Object n = m.invoke(userObj);
            if (n != null) nickname = n.toString();
        } catch (Exception ignored) {}
    }
    if (nickname.isEmpty()) nickname = "用户";
%>
<nav class="navbar">
    <div class="container">
        <a class="navbar-brand" href="<%=request.getContextPath()%>/">淘宝购物系统</a>
        <ul class="navbar-nav">
            <li><a href="<%=request.getContextPath()%>/">首页</a></li>
            <li><a href="<%=request.getContextPath()%>/product/list">全部商品</a></li>
            <% if ("operator".equals(role)) { %>
                <li><a href="<%=request.getContextPath()%>/admin/stat/dashboard">后台管理</a></li>
            <% } else if ("shopkeeper".equals(role)) { %>
                <li><a href="<%=request.getContextPath()%>/shop/home">商家后台</a></li>
            <% } %>
        </ul>
        <ul class="navbar-nav navbar-right">
            <% if (!loggedIn) { %>
                <li><a href="<%=request.getContextPath()%>/login">登录</a></li>
                <li><a href="<%=request.getContextPath()%>/register">注册</a></li>
            <% } else { %>
                <% if ("customer".equals(role)) { %>
                    <li><a href="<%=request.getContextPath()%>/cart/list">🛒 购物车</a></li>
                    <li><a href="<%=request.getContextPath()%>/order/list">📋 我的订单</a></li>
                <% } %>
                <li class="dropdown">
                    <a href="#">👤 <%=nickname%> ▾</a>
                    <ul class="dropdown-menu">
                        <% if ("customer".equals(role)) { %>
                            <li><a href="<%=request.getContextPath()%>/customer/profile">个人中心</a></li>
                            <li><a href="<%=request.getContextPath()%>/address/list">收货地址</a></li>
                            <li><a href="<%=request.getContextPath()%>/aftersale">售后记录</a></li>
                        <% } else if ("shopkeeper".equals(role)) { %>
                            <li><a href="<%=request.getContextPath()%>/shop/info/view">店铺信息</a></li>
                            <li><a href="<%=request.getContextPath()%>/shop/product/list">商品管理</a></li>
                            <li><a href="<%=request.getContextPath()%>/shop/order/list">订单管理</a></li>
                        <% } else if ("operator".equals(role)) { %>
                            <li><a href="<%=request.getContextPath()%>/admin/stat/dashboard">数据概览</a></li>
                            <li><a href="<%=request.getContextPath()%>/admin/user/list">用户管理</a></li>
                            <li><a href="<%=request.getContextPath()%>/admin/shop/auditList">店铺审核</a></li>
                            <li><a href="<%=request.getContextPath()%>/admin/order/list">订单监控</a></li>
                        <% } %>
                        <li role="separator" class="divider"></li>
                        <li><a href="<%=request.getContextPath()%>/logout">退出登录</a></li>
                    </ul>
                </li>
            <% } %>
        </ul>
    </div>
</nav>
