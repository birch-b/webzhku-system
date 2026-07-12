<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${annId != null ? '编辑公告' : '发布新公告'} - 运营商后台</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin.css?v=2">
    <script src="https://cdn.bootcdn.net/ajax/libs/ckeditor/4.20.0/ckeditor.js"></script>
</head>
<body>
<div class="admin-layout">
    <aside class="admin-sidebar">
        <div class="brand">
            <div class="icon">🛡️</div>
            <div class="title">运营商后台</div>
            <div class="subtitle">Operator Console</div>
        </div>
        <nav>
            <a href="${pageContext.request.contextPath}/admin/stat/dashboard">
                <span class="nav-icon">📊</span><span>数据概览</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/user/list">
                <span class="nav-icon">👥</span><span>用户管理</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/shop/auditList">
                <span class="nav-icon">🏪</span><span>店铺审核</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/shop/allShops">
                <span class="nav-icon">🏬</span><span>全部店铺</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/announcement/list" class="active">
                <span class="nav-icon">📢</span><span>公告管理</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/order/list">
                <span class="nav-icon">📦</span><span>订单监控</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/order/abnormal">
                <span class="nav-icon">⚠️</span><span>异常订单</span>
            </a>
        </nav>
        <div class="logout">
            <a href="${pageContext.request.contextPath}/admin/logout">
                <span class="nav-icon">🚪</span><span>退出登录</span>
            </a>
        </div>
    </aside>

    <main class="admin-main">
        <header class="admin-header">
            <div class="page-title">📢 ${annId != null ? '编辑公告' : '发布新公告'}</div>
            <div class="user-info">
                <div class="text-info">
                    <strong>管理员</strong>
                    <span>Operator</span>
                </div>
                <div class="avatar">管</div>
            </div>
        </header>

        <section class="admin-content">
            <div class="card">
                <div class="card-header">
                    <div class="card-title">
                        <span>📝</span><span>公告内容填写</span>
                    </div>
                    <span class="text-muted" style="font-size:13px">* 为必填项</span>
                </div>

                <form action="${pageContext.request.contextPath}/admin/announcement/save" method="post">
                    <c:if test="${annId != null}">
                        <input type="hidden" name="id" value="${annId}">
                    </c:if>

                    <div class="form-group">
                        <label>* 公告标题</label>
                        <input type="text" name="title" class="form-control" value="${annTitle}" required placeholder="请输入公告标题（将显示在公告列表中）">
                    </div>

                    <div class="form-group">
                        <label>* 优先级</label>
                        <select name="priority" class="form-control">
                            <option value="0" ${annPriority == 0 ? 'selected' : ''}>普通公告（常规通知）</option>
                            <option value="1" ${annPriority == 1 ? 'selected' : ''}>🔥 重要公告（置顶高亮显示）</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>* 公告正文内容</label>
                        <textarea name="content" id="editor1" class="rich-editor" rows="15" placeholder="请输入公告正文内容，支持富文本格式...">${annContent}</textarea>
                    </div>

                    <div class="divider"></div>

                    <div class="flex-between">
                        <a href="${pageContext.request.contextPath}/admin/announcement/list" class="btn btn-outline">
                            <span>←</span><span>返回公告列表</span>
                        </a>
                        <div class="flex" style="gap:10px">
                            <button type="reset" class="btn btn-secondary">
                                <span>🔄</span><span>重置内容</span>
                            </button>
                            <button type="submit" class="btn btn-primary">
                                <span>✅</span><span>${annId != null ? '保存修改' : '发布公告'}</span>
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </section>
    </main>
</div>

<script>
    // 初始化 CKEditor 富文本编辑器
    if (typeof CKEDITOR !== 'undefined') {
        CKEDITOR.replace('editor1', {
            height: 400,
            language: 'zh-cn',
            toolbar: [
                { name: 'basic', items: ['Bold', 'Italic', 'Underline', 'Strike'] },
                { name: 'colors', items: ['TextColor', 'BGColor'] },
                { name: 'paragraph', items: ['NumberedList', 'BulletedList', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight'] },
                { name: 'links', items: ['Link', 'Unlink'] },
                { name: 'insert', items: ['Image', 'Table', 'HorizontalRule'] },
                { name: 'styles', items: ['Format', 'FontSize'] },
                { name: 'tools', items: ['Maximize', 'Source'] }
            ]
        });
    }
</script>
</body>
</html>
