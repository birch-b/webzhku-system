<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // 已合并到统一登录系统，直接跳转到 /login
    response.sendRedirect(request.getContextPath() + "/login");
%>
