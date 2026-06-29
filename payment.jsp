<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>支付</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<style>.pay-box{max-width:400px;margin:60px auto;padding:30px;border:1px solid #ddd;border-radius:8px;background:#fff}
.pay-method{padding:15px;border:2px solid #ddd;border-radius:5px;margin:10px 0;cursor:pointer;text-align:center}
.pay-method:hover,.pay-method.active{border-color:#ff5000;background:#fff5f0}</style></head>
<body style="background:#f5f5f5">
<c:choose><c:when test="${success}">
<div class="pay-box text-center"><h2>✅ 支付成功！</h2><p>订单号: ${orderId}</p>
<a href="${pageContext.request.contextPath}/order/list" class="btn btn-primary btn-block">查看我的订单</a></div>
</c:when><c:otherwise>
<div class="pay-box"><h2 class="text-center">💰 模拟支付</h2><p class="text-center">支付金额: ￥${payAmount}</p>
<c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>
<form action="${pageContext.request.contextPath}/payment/pay" method="post">
<input type="hidden" name="orderId" value="${orderId}">
<div class="pay-method active" onclick="selectPay(1)"><input type="radio" name="payMethod" value="1" checked> 微信支付 📱</div>
<div class="pay-method" onclick="selectPay(2)"><input type="radio" name="payMethod" value="2"> 支付宝 💳</div>
<div class="pay-method" onclick="selectPay(3)"><input type="radio" name="payMethod" value="3"> 银行卡 🏦</div>
<button class="btn btn-danger btn-block btn-lg" style="margin-top:15px">确认支付</button></form></div>
<script>function selectPay(v){document.querySelectorAll('.pay-method').forEach(function(el){el.classList.remove('active')});el=event.currentTarget;el.classList.add('active')}</script>
</c:otherwise></c:choose></body></html>