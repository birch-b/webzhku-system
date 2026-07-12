<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>订单管理 - 商家后台 - 淘宝购物系统</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .status-badge { display:inline-block; padding:3px 10px; border-radius:12px; font-size:12px; color:#fff; font-weight:500; }
        .status-0 { background:#f0ad4e; }
        .status-1 { background:#5bc0de; }
        .status-2 { background:#337ab7; }
        .status-3 { background:#5cb85c; }
        .status-4 { background:#d9534f; }
        .status-5 { background:#777; }
        .status-6 { background:#f0ad4e; }
        .status-7 { background:#777; }
        .nav-tabs > li.active > a { color: var(--primary) !important; font-weight:600; border-top:2px solid var(--primary); }
    </style>
</head>
<body data-role="shopkeeper">
<jsp:include page="../header.jsp"/>

<div class="container" style="margin-top:20px;">
    <div class="row">
        <!-- 左侧二级子导航 -->
        <div class="col-md-2">
            <ul class="nav nav-pills nav-stacked">
                <li><a href="${pageContext.request.contextPath}/shop/home">🏠 店铺首页</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/info/view">🏪 店铺信息</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/category/list">📂 分类管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/product/list">📦 商品管理</a></li>
                <li class="active"><a href="${pageContext.request.contextPath}/shop/order/list">🧾 订单管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/review/list">💬 评价管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/aftersale/list">🔁 售后管理</a></li>
            </ul>
        </div>

        <!-- 右侧内容区 -->
        <div class="col-md-10">
            <h2 style="margin:10px 0 20px;padding-bottom:14px;border-bottom:2px solid var(--primary);">🧾 订单管理</h2>

            <c:if test="${not empty param.msg}">
                <div class="alert alert-success">
                    <c:choose>
                        <c:when test="${param.msg=='cancelled'}">订单已取消</c:when>
                        <c:when test="${param.msg=='shipped'}">发货成功</c:when>
                        <c:otherwise>操作成功</c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <ul class="nav nav-tabs" style="margin-bottom:16px;">
                <li ${empty currentStatus ? 'class="active"' : ''}><a href="${pageContext.request.contextPath}/shop/order/list">全部</a></li>
                <li ${currentStatus=='0' ? 'class="active"' : ''}><a href="${pageContext.request.contextPath}/shop/order/list?status=0">待付款</a></li>
                <li ${currentStatus=='1' ? 'class="active"' : ''}><a href="${pageContext.request.contextPath}/shop/order/list?status=1">待发货</a></li>
                <li ${currentStatus=='2' ? 'class="active"' : ''}><a href="${pageContext.request.contextPath}/shop/order/list?status=2">已发货</a></li>
                <li ${currentStatus=='3' ? 'class="active"' : ''}><a href="${pageContext.request.contextPath}/shop/order/list?status=3">已收货</a></li>
                <li ${currentStatus=='4' ? 'class="active"' : ''}><a href="${pageContext.request.contextPath}/shop/order/list?status=4">已完成</a></li>
            </ul>

            <c:choose>
                <c:when test="${empty orders}">
                    <div class="card" style="background:#fff;border-radius:8px;box-shadow:var(--shadow);padding:40px;text-align:center;">
                        <div style="font-size:48px;margin-bottom:12px;">🧾</div>
                        <p style="color:var(--text-secondary);">暂无订单</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="card" style="background:#fff;border-radius:8px;box-shadow:var(--shadow);padding:0;overflow:hidden;">
                        <table class="table" style="margin:0;">
                            <thead>
                                <tr><th>订单号</th><th>买家</th><th>金额</th><th>状态</th><th>时间</th><th>操作</th></tr>
                            </thead>
                            <tbody>
                                <c:forEach var="o" items="${orders}">
                                    <tr>
                                        <td><code style="background:var(--bg-alt);padding:2px 6px;border-radius:4px;">${o[1]}</code></td>
                                        <td>${o[2]}</td>
                                        <td style="color:var(--primary);font-weight:600;">￥${o[3]}</td>
                                        <td><span class="status-badge status-${o[4]}">${o[4]=='0'?'待付款':o[4]=='1'?'待发货':o[4]=='2'?'已发货':o[4]=='3'?'已收货':o[4]=='4'?'已完成':o[4]=='5'?'已取消':o[4]=='6'?'退款中':'已退款'}</span></td>
                                        <td>${o[5]}</td>
                                        <td style="display:flex;gap:6px;flex-wrap:wrap;">
                                            <a href="${pageContext.request.contextPath}/shop/order/detail?id=${o[0]}" class="btn btn-info btn-sm">查看详情</a>
                                            <%-- ====== 商家专属操作：绝对不能出现支付/收货/评价/申请售后！====== --%>
                                            <%-- 待付款(0)：商家只能关闭订单 --%>
                                            <c:if test="${o[4]=='0'}">
                                                <form action="${pageContext.request.contextPath}/order/cancel" method="post" style="display:inline">
                                                    <input type="hidden" name="id" value="${o[0]}">
                                                    <button class="btn btn-warning btn-sm" onclick="return confirm('确认关闭该待付款订单?')">关闭订单</button>
                                                </form>
                                            </c:if>
                                            <%-- 待发货(1)：商家核心操作——发货 --%>
                                            <c:if test="${o[4]=='1'}">
                                                <form action="${pageContext.request.contextPath}/order/ship" method="post" style="display:inline">
                                                    <input type="hidden" name="id" value="${o[0]}">
                                                    <button class="btn btn-success btn-sm">🚚 发货</button>
                                                </form>
                                            </c:if>
                                            <%-- 已发货(2)：商家查看物流 --%>
                                            <c:if test="${o[4]=='2'}">
                                                <a href="${pageContext.request.contextPath}/logistics/detail?orderId=${o[0]}" class="btn btn-primary btn-sm">📦 物流详情</a>
                                            </c:if>
                                            <%-- 已收货(3)/已完成(4)：商家无额外操作，只可查看 --%>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <c:if test="${totalPages > 1}">
                        <nav style="margin-top:20px;text-align:center;">
                            <ul class="pagination">
                                <li ${page<=1 ? 'class="disabled"' : ''}><a href="${pageContext.request.contextPath}/shop/order/list?status=${currentStatus}&page=${page-1}">&laquo;</a></li>
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li ${page==i ? 'class="active"' : ''}><a href="${pageContext.request.contextPath}/shop/order/list?status=${currentStatus}&page=${i}">${i}</a></li>
                                </c:forEach>
                                <li ${page>=totalPages ? 'class="disabled"' : ''}><a href="${pageContext.request.contextPath}/shop/order/list?status=${currentStatus}&page=${page+1}">&raquo;</a></li>
                            </ul>
                        </nav>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="../footer.jsp"/>

<script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>
