<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>申请售后 - 淘宝购物系统</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body { background-color: #f5f5f5; }
        .form-panel { background: white; padding: 30px; border-radius: 4px; margin-top: 20px; }
        .order-info { background: #f9f9f9; padding: 15px; border-radius: 4px; margin-bottom: 20px; }
    </style>
</head>
<body>
    <%@ include file="../header.jsp"%>

    <div class="container">
        <div class="row">
            <div class="col-md-8 col-md-offset-2">
                <h2 class="page-header">
                    <span class="glyphicon glyphicon-edit"></span> 申请售后
                </h2>

                <div class="form-panel">
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">${error}</div>
                    </c:if>

                    <!-- 订单信息 -->
                    <div class="order-info">
                        <h4>订单信息</h4>
                        <p><strong>订单编号：</strong>${orderNo}</p>
                        <p><strong>店铺：</strong>${shopName}</p>
                        <p><strong>实付金额：</strong><span class="text-danger h4">￥${payAmount}</span></p>
                    </div>

                    <form method="post" action="${pageContext.request.contextPath}/aftersale/submit">
                        <input type="hidden" name="orderId" value="${orderId}">
                        <input type="hidden" name="amount" value="${payAmount}">

                        <div class="form-group">
                            <label>售后类型 <span class="text-danger">*</span></label>
                            <div class="radio">
                                <label>
                                    <input type="radio" name="type" value="1" checked>
                                    仅退款（未收到货）
                                </label>
                            </div>
                            <div class="radio">
                                <label>
                                    <input type="radio" name="type" value="2">
                                    退货退款（已收到货，需退货）
                                </label>
                            </div>
                        </div>

                        <div class="form-group">
                            <label>退款金额</label>
                            <p class="form-control-static text-danger h4">￥${payAmount}</p>
                            <span class="help-block">退款金额不能超过实际支付金额</span>
                        </div>

                        <div class="form-group">
                            <label>申请原因 <span class="text-danger">*</span></label>
                            <textarea name="reason" class="form-control" rows="4" required
                                      placeholder="请详细描述您申请售后的原因..."></textarea>
                        </div>

                        <div class="form-group">
                            <label>补充说明（可选）</label>
                            <textarea name="description" class="form-control" rows="2"
                                      placeholder="如有其他补充说明，请在这里填写..."></textarea>
                        </div>

                        <div class="alert alert-warning">
                            <strong>温馨提示：</strong>
                            <ul class="mb-0">
                                <li>提交申请后，请等待商家处理</li>
                                <li>商家同意后，将进行退款操作</li>
                                <li>如有疑问，可联系商家客服</li>
                            </ul>
                        </div>

                        <div class="form-group">
                            <button type="submit" class="btn btn-primary btn-lg">提交申请</button>
                            <a href="${pageContext.request.contextPath}/aftersale" class="btn btn-default btn-lg">取消</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <%@ include file="../footer.jsp"%>

    <script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</body>
</html>
