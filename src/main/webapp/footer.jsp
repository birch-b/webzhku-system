<!-- footer.jsp - 底部公共页面（被 @include 包含，不要声明可能和主文件冲突的变量） -->
<footer class="footer">
    <div class="container">
        <div class="row">
            <div class="col-md-4">
                <h4>淘宝购物系统</h4>
                <p>JSP+Servlet电商平台演示项目</p>
            </div>
            <div class="col-md-4">
                <h4>快速链接</h4>
                <ul class="list-unstyled">
                    <li><a href="<%=request.getContextPath()%>/">首页</a></li>
                    <li><a href="<%=request.getContextPath()%>/product/list">商品列表</a></li>
                    <li><a href="<%=request.getContextPath()%>/login">登录</a></li>
                </ul>
            </div>
            <div class="col-md-4">
                <h4>联系我们</h4>
                <p>客服邮箱: support@taobao-shop.com</p>
            </div>
        </div>
        <hr>
        <p class="text-center">&copy; 2026 淘宝购物系统 - 期末考查项目</p>
    </div>
</footer>
