<%
    // header.jsp - 公共导航栏（被 @include 包含，不要声明和主文件重复的变量！）
    // 假设主文件（如 index.jsp）已经声明了变量: ctx, 这里直接内联调用 request.getContextPath()
    Object userIdObj = session.getAttribute("userId");
    Object userRoleObj = session.getAttribute("userRole");
    Object nicknameObj = session.getAttribute("nickname");

    boolean loggedIn = userIdObj != null;
    String role = userRoleObj != null ? userRoleObj.toString() : "";
    String nickname = nicknameObj != null ? nicknameObj.toString() : "用户";
    String base = request.getContextPath();
%>
<nav class="navbar">
    <div class="container">
        <a class="navbar-brand" href="<%=base%>/">淘宝购物系统</a>
        <ul class="navbar-nav">
            <li><a href="<%=base%>/">首页</a></li>
            <li><a href="<%=base%>/product/list">全部商品</a></li>
            <% if ("operator".equals(role)) { %>
                <li><a href="<%=base%>/admin/stat/dashboard">后台管理</a></li>
            <% } else if ("shopkeeper".equals(role)) { %>
                <li><a href="<%=base%>/shop/home">商家后台</a></li>
            <% } %>
        </ul>
        <ul class="navbar-nav navbar-right">
            <% if (!loggedIn) { %>
                <li><a href="<%=base%>/login">登录</a></li>
                <li><a href="<%=base%>/register">注册</a></li>
            <% } else { %>
                <% if ("customer".equals(role)) { %>
                    <li><a href="<%=base%>/cart/list">🛒 购物车</a></li>
                    <li><a href="<%=base%>/order/list">📋 我的订单</a></li>
                <% } %>
                <li class="dropdown">
                    <a href="#">👤 <%=nickname%> ▾</a>
                    <ul class="dropdown-menu">
                        <% if ("customer".equals(role)) { %>
                            <li><a href="<%=base%>/customer/profile">个人中心</a></li>
                            <li><a href="<%=base%>/address/list">收货地址</a></li>
                            <li><a href="<%=base%>/aftersale">售后记录</a></li>
                        <% } else if ("shopkeeper".equals(role)) { %>
                            <li><a href="<%=base%>/shop/info/view">店铺信息</a></li>
                            <li><a href="<%=base%>/shop/product/list">商品管理</a></li>
                            <li><a href="<%=base%>/shop/order/list">订单管理</a></li>
                        <% } else if ("operator".equals(role)) { %>
                            <li><a href="<%=base%>/admin/stat/dashboard">数据概览</a></li>
                            <li><a href="<%=base%>/admin/user/list">用户管理</a></li>
                            <li><a href="<%=base%>/admin/shop/auditList">店铺审核</a></li>
                            <li><a href="<%=base%>/admin/order/list">订单监控</a></li>
                        <% } %>
                        <li role="separator" class="divider"></li>
                        <li><a href="<%=base%>/logout">退出登录</a></li>
                    </ul>
                </li>
            <% } %>
        </ul>
    </div>
</nav>
