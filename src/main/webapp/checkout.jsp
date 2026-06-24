<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>确认订单</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body>
<jsp:include page="header.jsp"/>
<div class="container"><h2>确认订单</h2>
<h4>收货地址</h4><div class="well">${addrName} ${addrPhone} ${addrDetail}</div>
<h4>商品清单</h4><table class="table"><thead><tr><th>商品</th><th>单价</th><th>数量</th><th>小计</th></tr></thead>
<tbody><c:forEach var="item" items="${checkoutItems}"><tr>
<td>${item[1]}</td><td>￥${item[2]}</td><td>${item[3]}</td><td>￥${item[4]}</td></tr></c:forEach></tbody></table>
<h3 class="text-right">总计: ￥${totalAmount}</h3>
<form action="${pageContext.request.contextPath}/order/create" method="post">
<input type="hidden" name="addressId" value="${addrId}">
<button class="btn btn-danger btn-lg btn-block">提交订单</button></form></div>
<jsp:include page="footer.jsp"/></body></html>