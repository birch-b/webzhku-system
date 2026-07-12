<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>购物车 - 淘宝</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body>
<jsp:include page="header.jsp"/>
<div class="container"><h2>我的购物车</h2>
<table class="table table-striped"><thead><tr><th>选择</th><th>商品</th><th>单价</th><th>数量</th><th>小计</th><th>操作</th></tr></thead>
<tbody><c:forEach var="ci" items="${cartItems}"><tr>
<td><input type="checkbox" name="selected" value="${ci[7]}" ${ci[7]=='1'?'checked':''}></td>
<td><img src="${ci[6]}" style="width:60px" alt=""> ${ci[2]}</td><td>￥${ci[3]}</td>
<td><form action="${pageContext.request.contextPath}/cart/update" method="post" style="display:inline">
<input type="hidden" name="id" value="${ci[0]}"><input type="number" name="quantity" value="${ci[4]}" min="1" class="form-control input-sm" style="width:60px;display:inline">
<button class="btn btn-xs btn-default">更新</button></form></td>
<td>￥${ci[5]}</td>
<td><a href="${pageContext.request.contextPath}/cart/delete?id=${ci[0]}" class="btn btn-danger btn-sm" onclick="return confirm('删除?')">删除</a></td></tr></c:forEach></tbody></table>
<div class="text-right"><h3>合计: ￥${totalAmount}</h3>
<a href="${pageContext.request.contextPath}/order/checkout" class="btn btn-danger btn-lg">去结算</a></div></div>
<jsp:include page="footer.jsp"/></body></html>