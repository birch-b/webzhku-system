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
            <div class="card" style="background:#fff;border-radius:var(--radius-lg);box-shadow:0 1px 3px rgba(0,0,0,0.05);padding:16px;">
                <ul class="nav nav-pills nav-stacked" style="margin:0;">
                    <li><a href="${pageContext.request.contextPath}/shop/home" style="color:#64748b;padding:10px 12px;border-radius:4px;margin-bottom:4px;">🏠 店铺首页</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop/info/view" style="color:#64748b;padding:10px 12px;border-radius:4px;margin-bottom:4px;">🏪 店铺信息</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop/category/list" style="color:#64748b;padding:10px 12px;border-radius:4px;margin-bottom:4px;">📂 分类管理</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop/product/list" style="color:#64748b;padding:10px 12px;border-radius:4px;margin-bottom:4px;">📦 商品管理</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop/order/list" style="color:#64748b;padding:10px 12px;border-radius:4px;margin-bottom:4px;">🧾 订单管理</a></li>
                    <li class="active"><a href="${pageContext.request.contextPath}/shop/review/list" style="background:var(--primary);color:#fff;padding:10px 12px;border-radius:4px;margin-bottom:4px;">💬 评价管理</a></li>
                    <li><a href="${pageContext.request.contextPath}/shop/aftersale/list" style="color:#64748b;padding:10px 12px;border-radius:4px;margin-bottom:4px;">🔁 售后管理</a></li>
                </ul>
            </div>
        </div>

        <div class="col-md-10">
            <div style="background:#fff;border-radius:var(--radius-lg);box-shadow:0 1px 3px rgba(0,0,0,0.05);padding:24px;margin-bottom:20px;">
                <h2 style="margin:0 0 20px;padding-bottom:14px;border-bottom:2px solid var(--primary);font-size:22px;font-weight:700;color:#1e293b;">💬 评价管理</h2>

                <c:choose>
                    <c:when test="${empty reviews}">
                        <div class="review-empty">
                            <div class="review-empty-icon">📭</div>
                            <div class="review-empty-text">暂无评价</div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="order-detail-table">
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
                                            <td>
                                                <div class="review-rating-display">
                                                    <c:forEach var="i" begin="1" end="5">
                                                        <span class="star${i > r[3] ? ' empty' : ''}">⭐</span>
                                                    </c:forEach>
                                                </div>
                                            </td>
                                            <td style="max-width:280px;word-break:break-all;">${r[4]}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${r[5]!=null}">
                                                        <span class="badge badge-success">已回复</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-warning">未回复</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:if test="${r[5]==null}">
                                                    <form action="${pageContext.request.contextPath}/shop/review/reply" method="post" style="display:flex;gap:8px;align-items:center;flex-wrap:wrap;">
                                                        <input type="hidden" name="id" value="${r[0]}">
                                                        <input type="text" name="reply" class="form-control" placeholder="回复内容" style="width:180px;padding:6px 10px;font-size:13px;border-radius:4px;border:1px solid #e2e8f0;" required>
                                                        <button class="btn btn-success btn-sm" style="padding:6px 14px;">回复</button>
                                                    </form>
                                                </c:if>
                                                <c:if test="${r[5]!=null}">
                                                    <div style="background:#f8fafc;padding:8px 12px;border-radius:4px;border-left:3px solid var(--primary);">
                                                        <div style="font-size:12px;color:var(--primary);font-weight:600;margin-bottom:4px;">商家回复：</div>
                                                        <div style="font-size:13px;color:#64748b;">${r[5]}</div>
                                                    </div>
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
</div>

<jsp:include page="../footer.jsp"/>

<script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>