<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>订单详情 - 商家后台 - 淘宝购物系统</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .info-label { color:var(--text-secondary); font-weight:500; }
    </style>
</head>
<body data-role="shopkeeper">
<jsp:include page="../header.jsp"/>

<div class="container" style="margin-top:20px;">
    <div class="row">
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

        <div class="col-md-10">
            <h2 style="margin:10px 0 24px;padding-bottom:14px;border-bottom:2px solid var(--primary);">🧾 订单详情</h2>

            <div class="card" style="background:#fff;border-radius:8px;box-shadow:var(--shadow);padding:24px;margin-bottom:20px;">
                <h4 style="margin:0 0 16px;padding-bottom:12px;border-bottom:1px solid var(--border);">📋 订单信息</h4>
                <table class="table" style="margin:0;">
                    <tbody>
                        <tr><th class="info-label" style="width:120px;">订单号</th><td><code style="background:var(--bg-alt);padding:3px 8px;border-radius:4px;">${order.getString("order_no")}</code></td></tr>
                        <tr><th class="info-label">买家</th><td>${order.getString("buyer_name")}</td></tr>
                        <tr><th class="info-label">支付金额</th><td style="color:var(--primary);font-size:18px;font-weight:700;">￥${order.getString("pay_amount")}</td></tr>
                        <tr><th class="info-label">收货人</th><td>${order.getString("receiver_name")} &nbsp; 📞 ${order.getString("receiver_phone")}</td></tr>
                        <tr><th class="info-label">收货地址</th><td>${order.getString("receiver_address")}</td></tr>
                    </tbody>
                </table>
            </div>

            <div class="card" style="background:#fff;border-radius:8px;box-shadow:var(--shadow);padding:24px;margin-bottom:20px;">
                <h4 style="margin:0 0 16px;padding-bottom:12px;border-bottom:1px solid var(--border);">🛒 商品明细</h4>
                <div class="table-responsive">
                    <table class="table" style="margin:0;">
                        <thead>
                            <tr><th>商品</th><th>单价</th><th>数量</th><th>小计</th></tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${orderItems}">
                                <tr>
                                    <td style="font-weight:500;">${item[0]}</td>
                                    <td>￥${item[2]}</td>
                                    <td>${item[3]}</td>
                                    <td style="color:var(--primary);font-weight:600;">￥${item[4]}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

            <c:if test="${order.getInt('status')==1}">
                <div class="card" style="background:#fff;border-radius:8px;box-shadow:var(--shadow);padding:24px;margin-bottom:20px;">
                    <h4 style="margin:0 0 16px;padding-bottom:12px;border-bottom:1px solid var(--border);">🚚 发货操作</h4>
                    <form action="${pageContext.request.contextPath}/shop/order/ship" method="post" class="row" style="display:flex;gap:12px;align-items:flex-end;flex-wrap:wrap;margin:0;">
                        <input type="hidden" name="id" value="${order.getLong('id')}">
                        <div class="form-group" style="margin:0;flex:1;min-width:200px;">
                            <label>快递公司</label>
                            <select name="company" class="form-control">
                                <option>顺丰速运</option><option>中通快递</option><option>圆通速递</option><option>韵达快递</option><option>申通快递</option><option>京东物流</option><option>邮政EMS</option>
                            </select>
                        </div>
                        <div class="form-group" style="margin:0;flex:2;min-width:260px;">
                            <label>快递单号</label>
                            <input type="text" name="trackingNo" class="form-control" placeholder="请输入快递单号" required>
                        </div>
                        <div style="flex:0 0 auto;">
                            <button class="btn btn-primary">🚚 确认发货</button>
                        </div>
                    </form>
                </div>
            </c:if>

            <c:if test="${order.getInt('status')>=2}">
                <div class="card" style="background:#fff;border-radius:8px;box-shadow:var(--shadow);padding:24px;margin-bottom:20px;">
                    <h4 style="margin:0 0 16px;padding-bottom:12px;border-bottom:1px solid var(--border);">📦 物流信息</h4>
                    <div class="row">
                        <div class="col-md-4"><span class="info-label">快递公司：</span><strong>${order.getString("company")}</strong></div>
                        <div class="col-md-8"><span class="info-label">快递单号：</span><code style="background:var(--bg-alt);padding:3px 8px;border-radius:4px;">${order.getString("tracking_no")}</code></div>
                    </div>
                </div>
            </c:if>

            <div style="margin-top:20px;">
                <a href="${pageContext.request.contextPath}/shop/order/list" class="btn btn-default">← 返回订单列表</a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../footer.jsp"/>

<script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>
