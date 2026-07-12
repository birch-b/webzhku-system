<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<!-- 底部公共页面 -->
<footer class="app-footer">
  <div class="container">
    <div class="footer-row">
      <div>
        <h4>淘宝购物系统</h4>
        <p>JSP+Servlet电商平台演示项目</p>
      </div>
      <div>
        <h4>快速链接</h4>
        <ul>
          <li><a href="${pageContext.request.contextPath}/">首页</a></li>
          <li>
            <a href="${pageContext.request.contextPath}/product/list"
              >商品列表</a
            >
          </li>
          <li><a href="${pageContext.request.contextPath}/login">登录</a></li>
        </ul>
      </div>
      <div>
        <h4>联系我们</h4>
        <p>客服邮箱: support@taobao-shop.com</p>
      </div>
    </div>
    <hr />
    <p class="text-center">&copy; 2026 淘宝购物系统 - 期末考查项目</p>
  </div>
</footer>
