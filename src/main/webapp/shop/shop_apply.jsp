<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>商家入驻申请</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body { background: #f5f7fa; padding-top: 40px; }
        .apply-container {
            max-width: 680px;
            margin: 0 auto 40px;
        }
        .apply-title {
            font-size: 26px;
            font-weight: 700;
            color: var(--text);
            margin-bottom: 8px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .apply-desc { color: var(--text-secondary); margin-bottom: 28px; }
        .apply-card {
            background: #fff;
            border-radius: 12px;
            box-shadow: var(--shadow);
            padding: 32px;
            border-top: 4px solid var(--primary);
        }
    </style>
</head>
<body data-role="shopkeeper">
<div class="container apply-container">
    <h2 class="apply-title">🏪 商家入驻申请</h2>
    <p class="apply-desc">提交店铺资料，经平台审核通过后即可开启您的电商之旅</p>

    <div class="apply-card">
        <c:choose>
            <c:when test="${applyId!=null && applyStatus==0}">
                <div class="alert alert-warning"><strong>申请正在审核中</strong>，请耐心等待平台审核</div>
            </c:when>
            <c:when test="${applyId!=null && applyStatus==1}">
                <div class="alert alert-success">
                    <strong>申请已通过！</strong>
                    <a href="${pageContext.request.contextPath}/shop/home" class="btn btn-primary btn-sm" style="margin-left:12px;">进入后台</a>
                </div>
            </c:when>
            <c:when test="${applyId!=null && applyStatus==2}">
                <div class="alert alert-danger"><strong>被拒绝：</strong>${rejectReason}</div>
            </c:when>
        </c:choose>

        <c:if test="${applyId==null || applyStatus==2}">
            <form action="${pageContext.request.contextPath}/shop/apply/submit" method="post">
                <div class="form-group">
                    <label>店铺名称 <span style="color:var(--danger);">*</span></label>
                    <input type="text" name="shopName" class="form-control" placeholder="请输入店铺名称，例如：数码小铺" required>
                </div>
                <div class="form-group">
                    <label>主营类目</label>
                    <input type="text" name="shopCategory" class="form-control" placeholder="例如：手机数码 / 服饰鞋包 / 家居日用">
                </div>
                <div class="form-group">
                    <label>店铺描述</label>
                    <textarea name="description" class="form-control" rows="4" placeholder="简单介绍您的店铺特色、货源优势等"></textarea>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label>联系人</label>
                            <input type="text" name="contactName" class="form-control" placeholder="店铺负责人姓名">
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label>联系电话</label>
                            <input type="text" name="contactPhone" class="form-control" placeholder="11位手机号码">
                        </div>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary btn-lg" style="width:100%;margin-top:12px;">🚀 提交入驻申请</button>
            </form>
        </c:if>
    </div>
</div>
</body>
</html>
