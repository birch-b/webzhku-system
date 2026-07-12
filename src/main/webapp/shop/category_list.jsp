<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>分类管理 - 商家后台 - 淘宝购物系统</title>
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
                <li class="active"><a href="${pageContext.request.contextPath}/shop/category/list">📂 分类管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/product/list">📦 商品管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/order/list">🧾 订单管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/review/list">💬 评价管理</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/aftersale/list">🔁 售后管理</a></li>
            </ul>
        </div>

        <div class="col-md-10">
            <h2 style="margin:10px 0 24px;padding-bottom:14px;border-bottom:2px solid var(--primary);">📂 分类管理</h2>

            <div class="card" style="background:#fff;border-radius:8px;box-shadow:var(--shadow);padding:22px 20px;margin-bottom:20px;">
                <form action="${pageContext.request.contextPath}/shop/category/list" method="post">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="shopId" value="${shopId}">
                    <%-- 栅格三列 + 按钮列：4+4+2+2=12，每个label固定高度20px顶对齐→标签齐；提示小字固定高度16px占位→输入框齐 --%>
                    <div class="row" style="display:flex;align-items:flex-start;flex-wrap:wrap;gap:0;margin:0 -10px;">
                        <div class="col-sm-4" style="padding:0 10px;">
                            <label style="display:block;height:20px;line-height:20px;margin-bottom:6px;font-weight:500;">分类名称</label>
                            <input type="text" name="name" class="form-control" placeholder="如：手机数码" required style="height:34px;">
                            <%-- 空占位行，高度和另一个字段的提示小字一致，保证输入框底对齐 --%>
                            <div style="display:block;height:16px;margin-top:4px;line-height:16px;"></div>
                        </div>
                        <div class="col-sm-4" style="padding:0 10px;">
                            <label style="display:block;height:20px;line-height:20px;margin-bottom:6px;font-weight:500;">所属上级分类</label>
                            <select name="parentId" class="form-control" style="height:34px;">
                                <option value="0" selected>🏷️ 顶级分类（不传上级）</option>
                                <c:forEach var="top" items="${topCategories}">
                                    <option value="${top[0]}">📂 ${top[1]}</option>
                                </c:forEach>
                            </select>
                            <%-- 提示小字固定高度16px占位（和上面空行等高），三个输入框底部自动对齐 --%>
                            <small style="display:block;height:16px;margin-top:4px;line-height:16px;color:var(--text-secondary);font-size:12px;">提示：选顶级=建一级分类（如「手机数码」）；选已有顶级=建二级分类（如「智能手机」）</small>
                        </div>
                        <div class="col-sm-2" style="padding:0 10px;">
                            <label style="display:block;height:20px;line-height:20px;margin-bottom:6px;font-weight:500;">排序</label>
                            <input type="number" name="sortOrder" class="form-control" value="0" style="height:34px;">
                            <div style="display:block;height:16px;margin-top:4px;line-height:16px;"></div>
                        </div>
                        <div class="col-sm-2" style="padding:0 10px;display:flex;align-items:flex-end;">
                            <button class="btn btn-primary btn-block" style="height:34px;">➕ 添加分类</button>
                        </div>
                    </div>
                </form>
            </div>

            <div class="card" style="background:#fff;border-radius:8px;box-shadow:var(--shadow);padding:0;overflow:hidden;">
                <table class="table" style="margin:0;">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>分类名称</th>
                            <th>所属上级分类</th>
                            <th>排序</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="cat" items="${categories}">
                            <tr>
                                <td>${cat[0]}</td>
                                <td style="font-weight:500;">${cat[1]}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${cat[2] == null || cat[2]==0}"><span class="label label-primary" style="background:var(--primary);">🏷️ 顶级</span></c:when>
                                        <c:otherwise>
                                            <c:set var="pname" value="${parentMap[cat[2]]}"/>
                                            <span class="label label-info" style="background:#409eff;">📂 ${not empty pname ? pname : '未知'}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${cat[3]}</td>
                                <td>
                                    <form method="post" style="display:inline;" onsubmit="return confirm('确定要删除该分类吗？')">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${cat[0]}">
                                        <input type="hidden" name="shopId" value="${shopId}">
                                        <button class="btn btn-danger btn-sm">删除</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty categories}">
                            <tr><td colspan="5" style="text-align:center;padding:30px;color:var(--text-secondary);">📭 暂无分类</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../footer.jsp"/>

<script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>
