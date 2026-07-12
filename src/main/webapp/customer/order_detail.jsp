<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>订单详情</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body>
<jsp:include page="../header.jsp"/>
<div class="order-page">
<h2>订单详情</h2>

<div class="order-detail-section">
    <h3>订单信息</h3>
    <div class="order-detail-info">
        <div class="order-detail-info-item"><label>订单号</label><span>${order.orderNo}</span></div>
        <div class="order-detail-info-item"><label>店铺</label><span>${order.shopName}</span></div>
        <div class="order-detail-info-item"><label>订单金额</label><span class="price-value">￥${order.payAmount}</span></div>
        <div class="order-detail-info-item"><label>订单状态</label>
            <span class="status-badge status-${order.status}">
                <c:choose>
                    <c:when test="${order.status == 0}">待付款</c:when>
                    <c:when test="${order.status == 1}">待发货</c:when>
                    <c:when test="${order.status == 2}">已发货</c:when>
                    <c:when test="${order.status == 3}">已收货</c:when>
                    <c:when test="${order.status == 4}">已完成</c:when>
                    <c:when test="${order.status == 5}">已取消</c:when>
                    <c:when test="${order.status == 6}">退款中</c:when>
                    <c:when test="${order.status == 7}">已退款</c:when>
                    <c:otherwise>未知</c:otherwise>
                </c:choose>
            </span>
        </div>
        <div class="order-detail-info-item"><label>收货人</label><span>${order.receiverName} ${order.receiverPhone}</span></div>
        <div class="order-detail-info-item"><label>收货地址</label><span>${order.receiverAddress}</span></div>
        <div class="order-detail-info-item"><label>下单时间</label><span>${order.createTime}</span></div>
        <c:if test="${order.payTime != null}"><div class="order-detail-info-item"><label>支付时间</label><span>${order.payTime}</span></div></c:if>
        <c:if test="${order.shipTime != null}"><div class="order-detail-info-item"><label>发货时间</label><span>${order.shipTime}</span></div></c:if>
        <c:if test="${order.finishTime != null}"><div class="order-detail-info-item"><label>完成时间</label><span>${order.finishTime}</span></div></c:if>
    </div>
</div>

<div class="order-detail-section">
    <h3>商品明细</h3>
    <table class="order-detail-table">
        <thead><tr><th>商品</th><th>封面</th><th>单价</th><th>数量</th><th>小计</th></tr></thead>
        <tbody>
        <c:forEach var="item" items="${orderItems}">
            <tr>
                <td>${item[1]}</td>
                <td><c:if test="${not empty item[2]}"><img src="${pageContext.request.contextPath}${item[2]}" style="width:60px;height:60px;object-fit:cover;border-radius:4px;"></c:if></td>
                <td>￥${item[3]}</td>
                <td>${item[4]}</td>
                <td><strong>￥${item[5]}</strong></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<c:if test="${order.status >= 2 && not empty order.company}">
<div class="order-detail-section">
    <h3>物流信息</h3>
    <div class="order-detail-info">
        <div class="order-detail-info-item"><label>快递公司</label><span>${order.company}</span></div>
        <div class="order-detail-info-item"><label>运单号</label><span>${order.trackingNo}</span></div>
    </div>
</div>
</c:if>

<div class="order-detail-actions">
    <c:if test="${order.status == 0}">
        <a href="${pageContext.request.contextPath}/payment/view?orderId=${order.id}&amount=${order.payAmount}" class="btn btn-primary">去支付</a>
        <form action="${pageContext.request.contextPath}/order/cancel" method="post" style="display:inline;">
            <input type="hidden" name="id" value="${order.id}">
            <button type="submit" class="btn btn-default" onclick="return confirm('确定取消订单?')">取消订单</button>
        </form>
    </c:if>
    <c:if test="${order.status == 2}">
        <form action="${pageContext.request.contextPath}/order/confirm" method="post" style="display:inline;">
            <input type="hidden" name="id" value="${order.id}">
            <button type="submit" class="btn btn-primary">确认收货</button>
        </form>
    </c:if>
    <c:if test="${order.status == 3 || order.status == 4}">
        <c:choose>
            <c:when test="${not empty hasReview}">
                <span class="btn btn-default" style="cursor:not-allowed;">已评价</span>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/review/edit?orderId=${order.id}&productId=${orderItems[0][0]}" class="btn btn-primary">评价</a>
            </c:otherwise>
        </c:choose>
    </c:if>
    <c:if test="${(order.status == 3 || order.status == 4) && empty hasAftersale}">
        <a href="${pageContext.request.contextPath}/aftersale/apply?orderId=${order.id}" class="btn btn-warning">申请售后</a>
    </c:if>
    <a href="${pageContext.request.contextPath}/order/list" class="btn btn-default">返回订单列表</a>
</div>

</div>
<jsp:include page="../footer.jsp"/></body></html>