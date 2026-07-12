<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>编辑公告</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<script src="https://cdn.bootcdn.net/ajax/libs/ckeditor/4.20.0/ckeditor.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><div class="container-fluid"><div class="row">
<div class="col-md-2 sidebar"><h4>🛡️ 运营商后台</h4><a href="${pageContext.request.contextPath}/admin/announcement/list" class="active">公告管理</a></div>
<div class="col-md-10" style="padding:20px"><h3>${annId!=null?'编辑公告':'新建公告'}</h3>
<form action="${pageContext.request.contextPath}/admin/announcement/save" method="post">
<c:if test="${annId!=null}"><input type="hidden" name="id" value="${annId}"></c:if>
<div class="form-group"><label>标题</label><input type="text" name="title" class="form-control" value="${annTitle}" required></div>
<div class="form-group"><label>内容（CKEdit）</label><textarea name="content" id="editor1" rows="10" class="form-control">${annContent}</textarea></div>
<button type="submit" class="btn btn-primary">保存</button>
<a href="${pageContext.request.contextPath}/admin/announcement/list" class="btn btn-default">返回</a></form>
<script>CKEDITOR.replace('editor1');</script></div></div></div></body></html>