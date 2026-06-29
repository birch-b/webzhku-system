<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>售后详情 - 淘宝购物系统</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body { background-color: #f5f5f5; }
        .detail-panel { background: white; padding: 30px; border-radius: 4px; }
        .status-badge { font-size: 16px; padding: 5px 15px; }
        .status-0 { background: #f0ad4e; color: white; }
        .status-1 { background: #5bc0de; color: white; }
        .status-2 { background: #d9534f; color: white; }
        .status-3 { background: #5cb85c; color: white; }
        .info-row { padding: 10px 0; border-bottom: 1px solid #eee; }
        .info-label { color: #999; }
    </style>
</head>
<body>
    <%@ include file="../header.jsp"%>

    <div class="container">
        <div class="row">
            <div class="col-md-8 col-md-offset-2">
                <h2 class="page-header">
                    <span class="glyphicon glyphicon-file"></span> 售后详情
                    <a href="${pageContext.request.contextPath}/aftersale" class="btn btn-default btn-sm pull-right">
                        &larr; 返回列表
                    </a>
                </h2>

                <c:choose>
                    <c:when test="${not empty aftersale}">
                        <div class="detail-panel">
                            <!-- 状态 -->
                            <div class="text-center" style="margin-bottom: 30px;">
                                <c:choose>
                                    <c:when test="${aftersale.status == 0}">
                                        <span class="status-badge status-0">待处理</span>
                                    </c:when>
                                    <c:when test="${aftersale.status == 1}">
                                        <span class="status-badge status-1">已同意，等待退款</span>
                                    </c:when>
                                    <c:when test="${aftersale.status == 2}">
                                        <span class="status-badge status-2">已拒绝</span>
                                    </c:when>
                                    <c:when test="${aftersale.status == 3}">
                                        <span class="status-badge status-3">已退款</span>
                                    </c:when>
                                </c:choose>
                            </div>

                            <!-- 基本信息 -->
                            <h4>订单信息</h4>
                            <div class="row info-row">
                                <div class="col-xs-4 info-label">订单编号</div>
                                <div class="col-xs-8">${aftersale.orderNo}</div>
                            </div>
                            <div class="row info-row">
                                <div class="col-xs-4 info-label">店铺</div>
                                <div class="col-xs-8">${aftersale.shopName}</div>
                            </div>

                            <h4 style="margin-top: 20px;">售后信息</h4>
                            <div class="row info-row">
                                <div class="col-xs-4 info-label">售后类型</div>
                                <div class="col-xs-8">
                                    <c:choose>
                                        <c:when test="${aftersale.type == 1}">仅退款</c:when>
                                        <c:when test="${aftersale.type == 2}">退货退款</c:when>
                                    </c:choose>
                                </div>
                            </div>
                            <div class="row info-row">
                                <div class="col-xs-4 info-label">退款金额</div>
                                <div class="col-xs-8 text-danger h4">￥${aftersale.amount}</div>
                            </div>
                            <div class="row info-row">
                                <div class="col-xs-4 info-label">申请原因</div>
                                <div class="col-xs-8">${aftersale.reason}</div>
                            </div>
                            <c:if test="${not empty aftersale.description}">
                                <div class="row info-row">
                                    <div class="col-xs-4 info-label">补充说明</div>
                                    <div class="col-xs-8">${aftersale.description}</div>
                                </div>
                            </c:if>
                            <div class="row info-row">
                                <div class="col-xs-4 info-label">申请时间</div>
                                <div class="col-xs-8">
                                    <fmt:formatDate value="${aftersale.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                </div>
                            </div>

                            <!-- 商家处理结果 -->
                            <c:if test="${aftersale.status > 0}">
                                <h4 style="margin-top: 20px;">商家处理</h4>
                                <div class="row info-row">
                                    <div class="col-xs-4 info-label">处理结果</div>
                                    <div class="col-xs-8">
                                        <c:choose>
                                            <c:when test="${aftersale.status == 1}">同意申请</c:when>
                                            <c:when test="${aftersale.status == 2}">拒绝申请</c:when>
                                            <c:when test="${aftersale.status == 3}">已退款</c:when>
                                        </c:choose>
                                    </div>
                                </div>
                                <c:if test="${not empty aftersale.shopReply}">
                                    <div class="row info-row">
                                        <div class="col-xs-4 info-label">商家回复</div>
                                        <div class="col-xs-8">${aftersale.shopReply}</div>
                                    </div>
                                </c:if>
                                <c:if test="${not empty aftersale.handleTime}">
                                    <div class="row info-row">
                                        <div class="col-xs-4 info-label">处理时间</div>
                                        <div class="col-xs-8">
                                            <fmt:formatDate value="${aftersale.handleTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                        </div>
                                    </div>
                                </c:if>
                            </c:if>

                            <div class="text-center" style="margin-top: 30px;">
                                <a href="${pageContext.request.contextPath}/aftersale" class="btn btn-default">
                                    &larr; 返回列表
                                </a>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-warning">${error}</div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <%@ include file="../footer.jsp"%>

    <script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>
