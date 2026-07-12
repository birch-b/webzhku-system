<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>购物车 - 淘宝</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container" style="margin-top:20px;">
    <h2>我的购物车</h2>
    <c:if test="${not empty param.msg}">
        <div class="alert alert-warning">
            <c:choose>
                <c:when test="${param.msg == 'offshelf'}">该商品已下架，无法加入购物车</c:when>
                <c:otherwise>${param.msg}</c:otherwise>
            </c:choose>
        </div>
    </c:if>
    <c:choose>
        <c:when test="${empty cartItems}">
            <div class="alert alert-info">
                购物车是空的，快去 <a href="${pageContext.request.contextPath}/product/list">选购商品</a> 吧！
            </div>
        </c:when>
        <c:otherwise>
            <table class="table table-striped">
                <thead>
                <tr>
                    <th><input type="checkbox" id="checkAll" checked> 全选</th>
                    <th>商品</th>
                    <th>单价</th>
                    <th>数量</th>
                    <th>小计</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="ci" items="${cartItems}">
                    <tr>
                        <td>
                            <input type="checkbox" class="cart-check" data-id="${ci[0]}" ${ci[7]=='1' ? 'checked' : ''}>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/product/detail?id=${ci[1]}">
                                <img src="${pageContext.request.contextPath}${ci[6]}" style="width:60px"> ${ci[2]}
                            </a>
                        </td>
                        <td>￥${ci[3]}</td>
                        <td>
                            <form action="${pageContext.request.contextPath}/cart/update" method="post" style="display:inline">
                                <input type="hidden" name="id" value="${ci[0]}">
                                <input type="number" name="quantity" value="${ci[4]}" min="1" max="${ci[8]}" class="form-control input-sm" style="width:60px;display:inline">
                                <button class="btn btn-xs btn-default">更新</button>
                            </form>
                        </td>
                        <td>￥${ci[5]}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/cart/delete?id=${ci[0]}" class="btn btn-danger btn-sm" onclick="return confirm('确定删除?')">删除</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <div class="text-right">
                <h3>合计: ￥${totalAmount}</h3>
                <a href="${pageContext.request.contextPath}/order/checkout" class="btn btn-danger btn-lg">去结算</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<script>
var ctx = '${pageContext.request.contextPath}';
var checkAll = document.getElementById('checkAll');
var checks = document.querySelectorAll('.cart-check');

function syncCheckAll() {
    var total = checks.length;
    var checked = document.querySelectorAll('.cart-check:checked').length;
    checkAll.checked = (total > 0 && checked === total);
}

checkAll.addEventListener('change', function() {
    var s = this.checked ? 1 : 0;
    location.href = ctx + '/cart/selectAll?selected=' + s;
});

checks.forEach(function(cb) {
    cb.addEventListener('change', function() {
        var id = this.getAttribute('data-id');
        location.href = ctx + '/cart/toggle?id=' + id;
    });
});

syncCheckAll();
</script>
<jsp:include page="footer.jsp"/>
</body>
</html>
