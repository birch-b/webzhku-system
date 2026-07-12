<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>商品管理 - 商家后台 - 淘宝购物系统</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .status-badge { display:inline-block; padding:3px 10px; border-radius:12px; font-size:12px; color:#fff; font-weight:500; }
        .status-on { background:var(--success); }
        .status-off { background:var(--muted); }
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
                <li class="active"><a href="${pageContext.request.contextPath}/shop/product/list">📦 商品管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/order/list">🧾 订单管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/review/list">💬 评价管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/aftersale/list">🔁 售后管理</a></li>
            </ul>
        </div>

        <div class="col-md-10">
            <div style="display:flex;justify-content:space-between;align-items:center;margin:10px 0 20px;padding-bottom:14px;border-bottom:2px solid var(--primary);flex-wrap:wrap;gap:10px;">
                <h2 style="margin:0;font-size:22px;font-weight:600;color:var(--text);">📦 商品管理</h2>
                <a href="${pageContext.request.contextPath}/shop/product/edit" class="btn btn-primary">+ 新增商品</a>
            </div>

            <c:choose>
                <c:when test="${empty products}">
                    <div class="card" style="background:#fff;border-radius:8px;box-shadow:var(--shadow);padding:40px;text-align:center;">
                        <div style="font-size:48px;margin-bottom:12px;">📭</div>
                        <p style="color:var(--text-secondary);margin-bottom:20px;">暂无商品，快去发布第一个商品吧！</p>
                        <a href="${pageContext.request.contextPath}/shop/product/edit" class="btn btn-primary">+ 新增商品</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="card" style="background:#fff;border-radius:8px;box-shadow:var(--shadow);padding:0;overflow:hidden;">
                        <table class="table" style="margin:0;">
                            <thead>
                                <tr>
                                    <th>封面</th>
                                    <th>商品名</th>
                                    <th>分类</th>
                                    <th>价格</th>
                                    <th>库存</th>
                                    <th>销量</th>
                                    <th>状态</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="p" items="${products}">
                                    <c:set var="coverImg" value="${p[7]}"/>
                                    <c:if test="${empty coverImg or coverImg.startsWith('/upload/')}">
                                        <c:set var="coverImg" value="https://picsum.photos/50/50?random=${p[0]}"/>
                                    </c:if>
                                    <tr>
                                        <td><img src="${coverImg}" style="width:50px;height:50px;object-fit:cover;border-radius:4px;" alt="${p[1]}"></td>
                                        <td>${p[1]}</td>
                                        <td>${p[2]}</td>
                                        <td style="color:var(--primary);font-weight:600;">￥${p[3]}</td>
                                        <td>${p[4]}</td>
                                        <td>${p[5]}</td>
                                        <td><span class="status-badge ${p[6]=='1' ? 'status-on' : 'status-off'}">${p[6]=='1'?'上架':'下架'}</span></td>
                                        <td style="display:flex;gap:6px;flex-wrap:wrap;">
                                            <a href="${pageContext.request.contextPath}/shop/product/edit?id=${p[0]}" class="btn btn-info btn-sm">编辑</a>
                                            <c:if test="${p[6]=='1'}">
                                                <a href="${pageContext.request.contextPath}/shop/product/updateStatus?id=${p[0]}&status=2" class="btn btn-warning btn-sm" onclick="return confirm('确定要下架吗？')">下架</a>
                                            </c:if>
                                            <c:if test="${p[6]=='2'}">
                                                <a href="${pageContext.request.contextPath}/shop/product/updateStatus?id=${p[0]}&status=1" class="btn btn-success btn-sm" onclick="return confirm('确定要上架吗？')">上架</a>
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
