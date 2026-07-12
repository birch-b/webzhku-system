<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%-- 统一消息提示组件 --%>
<%-- 使用方式：在JSP中引入 <%@ include file="msg.jsp"%> --%>
<%-- 然后设置 session.setAttribute("success", "xxx") 或 session.setAttribute("error", "xxx") --%>

<script>
/**
 * 显示消息提示
 * @param msg 消息内容
 * @param type success|error|warning|info
 */
function showMsg(msg, type) {
    if (!msg) return;

    var icon = '';
    var className = '';

    switch(type) {
        case 'success':
            icon = '✓';
            className = 'alert-success';
            break;
        case 'error':
            icon = '✕';
            className = 'alert-danger';
            break;
        case 'warning':
            icon = '⚠';
            className = 'alert-warning';
            break;
        default:
            icon = 'ℹ';
            className = 'alert-info';
    }

    var html = '<div class="alert ' + className + ' alert-dismissible" style="margin: 10px 0;" id="统一消息提示">' +
               '<button type="button" class="close" data-dismiss="alert">&times;</button>' +
               '<strong>' + icon + '</strong> ' + msg +
               '</div>';

    // 在页面顶部显示
    var container = document.querySelector('.container');
    if (container) {
        container.insertAdjacentHTML('afterbegin', html);
    } else {
        document.body.insertAdjacentHTML('afterbegin', html);
    }

    // 5秒后自动消失
    setTimeout(function() {
        var alert = document.getElementById('统一消息提示');
        if (alert) {
            alert.remove();
        }
    }, 5000);
}

// 页面加载完成后检查消息
document.addEventListener('DOMContentLoaded', function() {
    // 从session中读取消息（通过URL参数传递）
    var urlParams = new URLSearchParams(window.location.search);
    var success = urlParams.get('msg');
    var error = urlParams.get('error');

    if (success) {
        showMsg(decodeURIComponent(success), 'success');
        // 清理URL参数
        urlParams.delete('msg');
        var newUrl = window.location.pathname + (urlParams.toString() ? '?' + urlParams.toString() : '');
        history.replaceState(null, '', newUrl);
    }
    if (error) {
        showMsg(decodeURIComponent(error), 'error');
        // 清理URL参数
        urlParams.delete('error');
        var newUrl = window.location.pathname + (urlParams.toString() ? '?' + urlParams.toString() : '');
        history.replaceState(null, '', newUrl);
    }
});
</script>

<style>
/* 消息提示样式增强 */
#统一消息提示 {
    border-radius: 4px;
    padding: 12px 20px;
    margin-bottom: 20px;
    border: 1px solid transparent;
    animation: slideDown 0.3s ease-out;
}

#统一消息提示.alert-success {
    color: #3c763d;
    background-color: #dff0d8;
    border-color: #d6e9c6;
}

#统一消息提示.alert-danger {
    color: #a94442;
    background-color: #f2dede;
    border-color: #ebccd1;
}

#统一消息提示.alert-warning {
    color: #8a6d3b;
    background-color: #fcf8e3;
    border-color: #faebcc;
}

#统一消息提示.alert-info {
    color: #31708f;
    background-color: #d9edf7;
    border-color: #bce8f1;
}

#统一消息提示 button.close {
    margin-left: 15px;
}

@keyframes slideDown {
    from {
        opacity: 0;
        transform: translateY(-20px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}
</style>
