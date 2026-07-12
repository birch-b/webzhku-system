<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>订单详情</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <style>
        .status-badge { padding: 5px 12px; border-radius: 3px; font-size: 14px; }
        .status-0 { background: #f0ad4e; color: #fff; }
        .status-1 { background: #5bc0de; color: #fff; }
        .status-2 { background: #337ab7; color: #fff; }
        .status-3 { background: #5cb85c; color: #fff; }
        .status-4 { background: #d9534f; color: #fff; }
        .status-5 { background: #777; color: #fff; }
    </style>
</head>
<body>
<jsp:include page="../header.jsp"/>
<div class="container" style="margin-top:20px;">
    <h2>订单详情</h2>
    <table class="table table-bordered">
        <tr><th>订单号</th><td>${order.orderNo}</td></tr>
        <tr><th>店铺</th><td>${order.shopName}</td></tr>
        <tr><th>收货人</th><td>${order.receiverName} ${order.receiverPhone}</td></tr>
        <tr><th>收货地址</th><td>${order.receiverAddress}</td></tr>
        <tr><th>订单金额</th><td><strong class="text-danger">￥${order.payAmount}</strong></td></tr>
        <tr><th>订单状态</th><td>
            <c:choose>
                <c:when test="${order.status == 0}"><span class="status-badge status-0">待付款</span></c:when>
                <c:when test="${order.status == 1}"><span class="status-badge status-1">待发货</span></c:when>
                <c:when test="${order.status == 2}"><span class="status-badge status-2">已发货</span></c:when>
                <c:when test="${order.status == 3}"><span class="status-badge status-3">已收货</span></c:when>
                <c:when test="${order.status == 4}"><span class="status-badge status-4">已完成</span></c:when>
                <c:when test="${order.status == 5}"><span class="status-badge status-5">已取消</span></c:when>
                <c:when test="${order.status == 6}"><span class="status-badge status-0">退款中</span></c:when>
                <c:when test="${order.status == 7}"><span class="status-badge status-5">已退款</span></c:when>
                <c:otherwise><span class="status-badge status-5">未知</span></c:otherwise>
            </c:choose>
        </td></tr>
        <tr><th>下单时间</th><td>${order.createTime}</td></tr>
        <c:if test="${order.payTime != null}"><tr><th>支付时间</th><td>${order.payTime}</td></tr></c:if>
        <c:if test="${order.shipTime != null}"><tr><th>发货时间</th><td>${order.shipTime}</td></tr></c:if>
        <c:if test="${order.finishTime != null}"><tr><th>完成时间</th><td>${order.finishTime}</td></tr></c:if>
    </table>

    <h4>商品明细</h4>
    <table class="table">
        <thead><tr><th>商品</th><th>封面</th><th>单价</th><th>数量</th><th>小计</th></tr></thead>
        <tbody>
        <c:forEach var="item" items="${orderItems}">
            <tr>
                <td>${item[1]}</td>
                <td><c:if test="${not empty item[2]}"><img src="${item[2]}" style="width:50px;"></c:if></td>
                <td>￥${item[3]}</td>
                <td>${item[4]}</td>
                <td>￥${item[5]}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <c:if test="${order.status >= 2 && not empty order.company}">
    <h4>物流信息</h4>
    <p>快递公司：${order.company} &nbsp; 单号：${order.trackingNo}</p>
    </c:if>

    <div class="text-center" style="margin:30px 0;">
        <c:if test="${order.status == 0}">
            <a href="${pageContext.request.contextPath}/payment/view?orderId=${order.id}&amount=${order.payAmount}" class="btn btn-primary">去支付</a>
            <a href="${pageContext.request.contextPath}/order/cancel?id=${order.id}" class="btn btn-default" onclick="return confirm('确定取消订单?')">取消订单</a>
        </c:if>
        <c:if test="${order.status == 2}">
            <form action="${pageContext.request.contextPath}/order/confirm" method="post" style="display:inline;">
                <input type="hidden" name="id" value="${order.id}">
                <button type="submit" class="btn btn-success">确认收货</button>
            </form>
        </c:if>
        <c:if test="${order.status == 3 || order.status == 4}">
            <a href="${pageContext.request.contextPath}/review/edit?orderId=${order.id}&productId=${orderItems[0][0]}" class="btn btn-primary">评价</a>
        </c:if>
        <c:if test="${(order.status == 3 || order.status == 4) && empty hasAftersale}">
            <a href="${pageContext.request.contextPath}/aftersale/apply?orderId=${order.id}" class="btn btn-warning">申请售后</a>
        </c:if>
        <a href="${pageContext.request.contextPath}/order/list" class="btn btn-default">返回订单列表</a>
    </div>
</div>
<jsp:include page="../footer.jsp"/>
</body>
</html>
