<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head><meta charset="UTF-8"><title>确认订单</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container" style="margin-top:20px;">
<h2>确认订单</h2>
<h4>收货地址</h4>
<c:choose>
<c:when test="${empty addresses}">
<div class="alert alert-warning">没有收货地址，请先<a href="${pageContext.request.contextPath}/address/list">添加地址</a></div>
</c:when>
<c:otherwise>
<c:forEach var="a" items="${addresses}">
<div class="radio"><label>
<input type="radio" name="addressId" value="${a[0]}" ${a[0]==addrId ? 'checked' : ''}>
${a[1]} ${a[2]} ${a[3]} <c:if test="${a[4]=='1'}"><span class="label label-primary">默认</span></c:if>
</label></div>
</c:forEach>
</c:otherwise>
</c:choose>
<h4>买家留言</h4>
<div class="form-group">
<textarea name="buyerMessage" class="form-control" rows="2" placeholder="给商家留言（选填）" maxlength="200"></textarea>
</div>
<h4>商品清单</h4>
<table class="table"><thead><tr><th>商品</th><th>单价</th><th>数量</th><th>小计</th></tr></thead>
<tbody><c:forEach var="it" items="${checkoutItems}"><tr>
<td>${it[1]}</td><td>￥${it[2]}</td><td>${it[3]}</td><td>￥${it[4]}</td>
</tr></c:forEach></tbody></table>
<h3 class="text-right">总计: ￥${totalAmount}</h3>
<c:if test="${not empty addresses}">
<form action="${pageContext.request.contextPath}/order/create" method="post">
<input type="hidden" name="addressId" value="${addrId}" id="aid">
<input type="hidden" name="buyerMessage" id="bmsg">
<button class="btn btn-danger btn-lg btn-block">提交订单</button>
</form>
</c:if>
</div>
<script>
document.querySelector('form[action*="order/create"]').addEventListener('submit', function() {
    var ca = document.querySelector('input[name="addressId"]:checked');
    if (ca) document.getElementById('aid').value = ca.value;
    var m = document.querySelector('textarea[name="buyerMessage"]');
    document.getElementById('bmsg').value = m ? m.value : '';
});
</script>
<jsp:include page="footer.jsp"/>
</body>
</html>
