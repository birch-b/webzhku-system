<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>售后详情 - 商家后台 - 淘宝购物系统</title>
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
        .action-panel { background: #f9f9f9; padding: 20px; border-radius: 4px; margin-top: 20px; }
    </style>
</head>
<body>
    <%@ include file="../header.jsp"%>

    <div class="container">
        <div class="row">
            <div class="col-md-10 col-md-offset-1">
                <h2 class="page-header">
                    <span class="glyphicon glyphicon-file"></span> 售后详情
                    <a href="${pageContext.request.contextPath}/shop/aftersale" class="btn btn-default btn-sm pull-right">
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

                            <!-- 买家信息 -->
                            <h4>买家信息</h4>
                            <div class="row info-row">
                                <div class="col-xs-3 info-label">买家账号</div>
                                <div class="col-xs-9">${aftersale.username} (${aftersale.nickname})</div>
                            </div>
                            <div class="row info-row">
                                <div class="col-xs-3 info-label">联系电话</div>
                                <div class="col-xs-9">${aftersale.phone}</div>
                            </div>

                            <!-- 订单信息 -->
                            <h4 style="margin-top: 20px;">订单信息</h4>
                            <div class="row info-row">
                                <div class="col-xs-3 info-label">订单编号</div>
                                <div class="col-xs-9">${aftersale.orderNo}</div>
                            </div>
                            <div class="row info-row">
                                <div class="col-xs-3 info-label">收货人</div>
                                <div class="col-xs-9">${aftersale.receiverName} ${aftersale.receiverPhone}</div>
                            </div>
                            <div class="row info-row">
                                <div class="col-xs-3 info-label">收货地址</div>
                                <div class="col-xs-9">${aftersale.receiverAddress}</div>
                            </div>

                            <!-- 售后信息 -->
                            <h4 style="margin-top: 20px;">售后信息</h4>
                            <div class="row info-row">
                                <div class="col-xs-3 info-label">售后类型</div>
                                <div class="col-xs-9">
                                    <c:choose>
                                        <c:when test="${aftersale.type == 1}">仅退款（未收到货）</c:when>
                                        <c:when test="${aftersale.type == 2}">退货退款（已收到货）</c:when>
                                    </c:choose>
                                </div>
                            </div>
                            <div class="row info-row">
                                <div class="col-xs-3 info-label">退款金额</div>
                                <div class="col-xs-9 text-danger h4">￥${aftersale.amount}</div>
                            </div>
                            <div class="row info-row">
                                <div class="col-xs-3 info-label">申请原因</div>
                                <div class="col-xs-9">${aftersale.reason}</div>
                            </div>
                            <c:if test="${not empty aftersale.description}">
                                <div class="row info-row">
                                    <div class="col-xs-3 info-label">补充说明</div>
                                    <div class="col-xs-9">${aftersale.description}</div>
                                </div>
                            </c:if>
                            <div class="row info-row">
                                <div class="col-xs-3 info-label">申请时间</div>
                                <div class="col-xs-9">
                                    <fmt:formatDate value="${aftersale.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                </div>
                            </div>

                            <!-- 商家处理 -->
                            <c:if test="${aftersale.status > 0}">
                                <h4 style="margin-top: 20px;">处理结果</h4>
                                <div class="row info-row">
                                    <div class="col-xs-3 info-label">处理状态</div>
                                    <div class="col-xs-9">
                                        <c:choose>
                                            <c:when test="${aftersale.status == 1}">同意申请，等待退款</c:when>
                                            <c:when test="${aftersale.status == 2}">拒绝申请</c:when>
                                            <c:when test="${aftersale.status == 3}">已退款完成</c:when>
                                        </c:choose>
                                    </div>
                                </div>
                                <c:if test="${not empty aftersale.shopReply}">
                                    <div class="row info-row">
                                        <div class="col-xs-3 info-label">处理备注</div>
                                        <div class="col-xs-9">${aftersale.shopReply}</div>
                                    </div>
                                </c:if>
                                <c:if test="${not empty aftersale.handleTime}">
                                    <div class="row info-row">
                                        <div class="col-xs-3 info-label">处理时间</div>
                                        <div class="col-xs-9">
                                            <fmt:formatDate value="${aftersale.handleTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                        </div>
                                    </div>
                                </c:if>
                            </c:if>

                            <!-- 处理操作 -->
                            <c:if test="${aftersale.status == 0}">
                                <div class="action-panel">
                                    <h4>处理申请</h4>

                                    <!-- 同意 -->
                                    <div style="margin-bottom: 20px;">
                                        <h5>同意申请</h5>
                                        <form method="post" action="${pageContext.request.contextPath}/shop/aftersale/approve" style="display: inline-block;">
                                            <input type="hidden" name="id" value="${aftersale.id}">
                                            <input type="hidden" name="reply" value="商家已同意您的售后申请">
                                            <button type="submit" class="btn btn-success">确认同意</button>
                                        </form>
                                        <span class="help-block" style="display: inline-block; margin-left: 10px;">
                                            同意后，买家可以申请退款
                                        </span>
                                    </div>

                                    <!-- 拒绝 -->
                                    <div>
                                        <h5>拒绝申请</h5>
                                        <form method="post" action="${pageContext.request.contextPath}/shop/aftersale/reject">
                                            <input type="hidden" name="id" value="${aftersale.id}">
                                            <input type="hidden" name="reply" value="商家已拒绝您的售后申请">
                                            <div class="form-group">
                                                <textarea name="rejectReason" class="form-control" rows="2"
                                                          placeholder="请填写拒绝原因（必填）" required></textarea>
                                            </div>
                                            <button type="submit" class="btn btn-danger">确认拒绝</button>
                                        </form>
                                    </div>
                                </div>
                            </c:if>

                            <!-- 退款操作 -->
                            <c:if test="${aftersale.status == 1}">
                                <div class="action-panel">
                                    <h4>退款操作</h4>
                                    <p>买家已收到退款通知，请点击下方按钮确认退款完成（模拟）</p>
                                    <form method="post" action="${pageContext.request.contextPath}/shop/aftersale/refund">
                                        <input type="hidden" name="id" value="${aftersale.id}">
                                        <button type="submit" class="btn btn-primary btn-lg">确认退款 ￥${aftersale.amount}</button>
                                    </form>
                                </div>
                            </c:if>

                            <div class="text-center" style="margin-top: 30px;">
                                <a href="${pageContext.request.contextPath}/shop/aftersale" class="btn btn-default">
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
