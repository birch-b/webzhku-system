<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>评价管理 - 商家后台 - 淘宝购物系统</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
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
                <li><a href="${pageContext.request.contextPath}/shop/order/list">🧾 订单管理</a></li>
                <li class="active"><a href="${pageContext.request.contextPath}/shop/review/list">💬 评价管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/aftersale/list">🔁 售后管理</a></li>
            </ul>
        </div>

        <div class="col-md-10">
            <h2 style="margin:10px 0 24px;padding-bottom:14px;border-bottom:2px solid var(--primary);">💬 评价管理</h2>

            <c:choose>
                <c:when test="${empty reviews}">
                    <div class="card" style="background:#fff;border-radius:8px;box-shadow:var(--shadow);padding:40px;text-align:center;">
                        <div style="font-size:48px;margin-bottom:12px;">💬</div>
                        <p style="color:var(--text-secondary);">暂无评价</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="card" style="background:#fff;border-radius:8px;box-shadow:var(--shadow);padding:0;overflow:hidden;">
                        <table class="table" style="margin:0;">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>商品</th>
                                    <th>买家</th>
                                    <th>评分</th>
                                    <th>内容</th>
                                    <th>回复状态</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="r" items="${reviews}">
                                    <tr>
                                        <td>${r[0]}</td>
                                        <td>${r[1]}</td>
                                        <td>${r[2]}</td>
                                        <td style="color:#f59e0b;font-weight:600;">${r[3]}⭐</td>
                                        <td style="max-width:280px;">${r[4]}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${r[5]!=null}">${r[5]}</c:when>
                                                <c:otherwise><span style="color:var(--warning);">未回复</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:if test="${r[5]==null}">
                                                <form action="${pageContext.request.contextPath}/shop/review/reply" method="post" style="display:flex;gap:6px;align-items:center;flex-wrap:wrap;">
                                                    <input type="hidden" name="id" value="${r[0]}">
                                                    <input type="text" name="reply" class="form-control input-sm" placeholder="回复内容" style="width:200px;display:inline-block;" required>
                                                    <button class="btn btn-success btn-sm">回复</button>
                                                </form>
                                            </c:if>
                                            <c:if test="${r[5]!=null}">
                                                <span class="label label-success">已回复</span>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
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
