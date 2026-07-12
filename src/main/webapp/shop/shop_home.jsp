<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>商家后台 - 淘宝购物系统</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body data-role="shopkeeper">
<jsp:include page="../header.jsp"/>

<div class="container" style="margin-top:20px;">
    <div class="row">
        <!-- 左侧二级子导航 -->
        <div class="col-md-2">
            <ul class="nav nav-pills nav-stacked">
                <li class="active"><a href="${pageContext.request.contextPath}/shop/home">🏠 店铺首页</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/info/view">🏪 店铺信息</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/category/list">📂 分类管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/product/list">📦 商品管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/order/list">🧾 订单管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/review/list">💬 评价管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/aftersale/list">🔁 售后管理</a></li>
            </ul>
        </div>

        <!-- 右侧内容区 -->
        <div class="col-md-10">
            <h2 style="margin:10px 0 24px;padding-bottom:14px;border-bottom:2px solid var(--primary);">
                🏪 商家后台
                <small style="color:var(--text-secondary);margin-left:10px;">欢迎，${sessionScope.nickname != null ? sessionScope.nickname : sessionScope.user != null ? sessionScope.user.nickname : '店主'}！</small>
            </h2>

            <div class="kpi-row" style="display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:16px;margin-bottom:28px;">
                <div class="kpi-card" style="background:#fff;border-radius:8px;padding:20px;box-shadow:0 1px 3px rgba(0,0,0,0.06), 0 1px 2px rgba(0,0,0,0.04);border-top:3px solid var(--primary);">
                    <div class="kpi-label" style="color:var(--text-secondary);font-size:13px;margin-bottom:8px;">📦 商品总数</div>
                    <div class="kpi-value small" style="font-size:22px;font-weight:700;color:var(--text);">${totalProducts != null ? totalProducts : 0}</div>
                </div>
                <div class="kpi-card" style="background:#fff;border-radius:8px;padding:20px;box-shadow:0 1px 3px rgba(0,0,0,0.06), 0 1px 2px rgba(0,0,0,0.04);border-top:3px solid var(--primary);">
                    <div class="kpi-label" style="color:var(--text-secondary);font-size:13px;margin-bottom:8px;">🧾 订单总数</div>
                    <div class="kpi-value small" style="font-size:22px;font-weight:700;color:var(--text);">${totalOrders != null ? totalOrders : 0}</div>
                </div>
                <div class="kpi-card" style="background:#fff;border-radius:8px;padding:20px;box-shadow:0 1px 3px rgba(0,0,0,0.06), 0 1px 2px rgba(0,0,0,0.04);border-top:3px solid var(--primary);">
                    <div class="kpi-label" style="color:var(--text-secondary);font-size:13px;margin-bottom:8px;">💬 收到的评价</div>
                    <div class="kpi-value small" style="font-size:22px;font-weight:700;color:var(--text);">${totalReviews != null ? totalReviews : 0}</div>
                </div>
                <div class="kpi-card" style="background:#fff;border-radius:8px;padding:20px;box-shadow:0 1px 3px rgba(0,0,0,0.06), 0 1px 2px rgba(0,0,0,0.04);border-top:3px solid var(--primary);">
                    <div class="kpi-label" style="color:var(--text-secondary);font-size:13px;margin-bottom:8px;">🔁 待处理售后</div>
                    <div class="kpi-value small" style="font-size:22px;font-weight:700;color:var(--text);">${pendingAftersales != null ? pendingAftersales : 0}</div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-4">
                    <div class="card" style="background:#fff;border-radius:8px;box-shadow:0 1px 3px rgba(0,0,0,0.06), 0 1px 2px rgba(0,0,0,0.04);padding:24px;margin-bottom:20px;text-align:center;">
                        <h4 style="margin:0 0 12px;font-weight:600;color:var(--text);">📦 商品管理</h4>
                        <p style="color:var(--text-secondary);margin-bottom:16px;">发布新商品、编辑库存、上架/下架</p>
                        <a href="${pageContext.request.contextPath}/shop/product/list" class="btn btn-primary">进入</a>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card" style="background:#fff;border-radius:8px;box-shadow:0 1px 3px rgba(0,0,0,0.06), 0 1px 2px rgba(0,0,0,0.04);padding:24px;margin-bottom:20px;text-align:center;">
                        <h4 style="margin:0 0 12px;font-weight:600;color:var(--text);">🧾 订单处理</h4>
                        <p style="color:var(--text-secondary);margin-bottom:16px;">查看订单、安排发货、处理退款</p>
                        <a href="${pageContext.request.contextPath}/shop/order/list" class="btn btn-primary">进入</a>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card" style="background:#fff;border-radius:8px;box-shadow:0 1px 3px rgba(0,0,0,0.06), 0 1px 2px rgba(0,0,0,0.04);padding:24px;margin-bottom:20px;text-align:center;">
                        <h4 style="margin:0 0 12px;font-weight:600;color:var(--text);">💬 评价管理</h4>
                        <p style="color:var(--text-secondary);margin-bottom:16px;">查看买家评价、回复评论</p>
                        <a href="${pageContext.request.contextPath}/shop/review/list" class="btn btn-primary">进入</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../footer.jsp"/>

<script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>
