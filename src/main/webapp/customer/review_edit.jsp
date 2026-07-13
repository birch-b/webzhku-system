<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>发表评价 - 淘宝购物系统</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://cdn.bootcdn.net/ajax/libs/ckeditor/4.20.0/ckeditor.js"></script>
</head>
<body>
<jsp:include page="../header.jsp"/>

<div class="review-page">
    <h2>📝 发表评价</h2>
    
    <div class="review-form-card">
        <form action="${pageContext.request.contextPath}/review/submit" method="post">
            <input type="hidden" name="orderId" value="${orderId}">
            <input type="hidden" name="productId" value="${productId}">
            
            <div class="form-group">
                <label>🌟 商品评分</label>
                <div class="star-rating">
                    <input type="radio" name="rating" id="star5" value="5">
                    <label for="star5">⭐</label>
                    <input type="radio" name="rating" id="star4" value="4">
                    <label for="star4">⭐</label>
                    <input type="radio" name="rating" id="star3" value="3" checked>
                    <label for="star3">⭐</label>
                    <input type="radio" name="rating" id="star2" value="2">
                    <label for="star2">⭐</label>
                    <input type="radio" name="rating" id="star1" value="1">
                    <label for="star1">⭐</label>
                </div>
            </div>
            
            <div class="form-group">
                <label>📄 评价内容</label>
                <textarea name="content" id="editor1" rows="6" class="review-content" placeholder="请描述您对商品的使用体验和感受..."></textarea>
            </div>
            
            <div style="display: flex; gap: 12px; justify-content: flex-end;">
                <a href="${pageContext.request.contextPath}/order/list" class="btn btn-default">返回订单</a>
                <button type="submit" class="btn btn-primary">提交评价</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../footer.jsp"/>

<script>CKEDITOR.replace('editor1');</script>
</body>
</html>