<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>数据概览 - 运营商后台</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin.css?v=2">
    <script src="https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js"></script>
</head>
<body>
<div class="admin-layout">
    <aside class="admin-sidebar">
        <div class="brand">
            <div class="icon">🛡️</div>
            <div class="title">运营商后台</div>
            <div class="subtitle">Taobao Operator</div>
        </div>
        <nav>
            <a href="${pageContext.request.contextPath}/admin/stat/dashboard" class="active">
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
            <a href="${pageContext.request.contextPath}/admin/announcement/list">
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
            <a href="${pageContext.request.contextPath}/admin/logout" style="color:rgba(255,255,255,0.8);text-decoration:none;">
                <span class="nav-icon">🚪</span><span>退出登录</span>
            </a>
        </div>
    </aside>

    <main class="admin-main">
        <header class="admin-header">
            <div class="page-title">📊 数据概览</div>
            <div class="user-info">
                <div style="text-align:right;">
                    <strong>${user.nickname}</strong>
                </div>
                <div class="avatar">管</div>
            </div>
        </header>

        <section class="admin-content">
            <div class="kpi-grid">
                <div class="kpi-card">
                    <div class="kpi-icon">👥</div>
                    <div class="kpi-value">${stats.totalUsers}</div>
                    <div class="kpi-label">平台总用户数</div>
                </div>
                <div class="kpi-card">
                    <div class="kpi-icon">🏪</div>
                    <div class="kpi-value">${stats.activeShops}</div>
                    <div class="kpi-label">营业中店铺</div>
                </div>
                <div class="kpi-card">
                    <div class="kpi-icon">📦</div>
                    <div class="kpi-value">${stats.totalOrders}</div>
                    <div class="kpi-label">平台订单总量</div>
                </div>
                <div class="kpi-card">
                    <div class="kpi-icon">💰</div>
                    <div class="kpi-value">￥${stats.totalRevenue}</div>
                    <div class="kpi-label">平台总营收</div>
                </div>
                <div class="kpi-card">
                    <div class="kpi-icon">🛒</div>
                    <div class="kpi-value">${stats.activeProducts}</div>
                    <div class="kpi-label">上架商品数</div>
                </div>
            </div>

            <div class="chart-grid">
                <div class="chart-card">
                    <div class="chart-title">📈 商品销量排行 TOP10</div>
                    <div class="chart-container" id="chartSalesChart"></div>
                </div>
                <div class="chart-card">
                    <div class="chart-title">🏪 店铺营收统计</div>
                    <div class="chart-container" id="chartShopChart"></div>
                </div>
                <div class="chart-card">
                    <div class="chart-title">📅 月度订单总量</div>
                    <div class="chart-container" id="chartOrderChart"></div>
                </div>
                <div class="chart-card">
                    <div class="chart-title">📊 用户注册增长</div>
                    <div class="chart-container" id="chartUserChart"></div>
                </div>
            </div>
        </section>
    </main>
</div>

<script>
var ctx = "${pageContext.request.contextPath}";

// ============================================================
// 工具：统一XHR获取 JSON
// ============================================================
function xhr(url, cb){
    var x = new XMLHttpRequest();
    x.open('GET', ctx + url, true);
    x.onreadystatechange = function(){
        if(x.readyState === 4 && x.status === 200){
            try { cb(JSON.parse(x.responseText)); } catch(e) {}
        }
    };
    x.send();
}

// 销量排行（柱图）
xhr('/admin/stat/salesRank', function(d){
    var chart = echarts.init(document.getElementById('chartSalesChart'));
    chart.setOption({
        color:['#667eea'],
        tooltip:{trigger:'axis'},
        grid:{left:40,right:20,top:20,bottom:60},
        xAxis:{type:'category',data:d.names, axisLabel:{rotate:30,interval:0,fontSize:11}},
        yAxis:{type:'value'},
        series:[{type:'bar',data:d.values,itemStyle:{borderRadius:[4,4,0,0]}}]
    });
    window.addEventListener('resize', function(){ chart.resize(); });
});

// 店铺营收（饼图）
xhr('/admin/stat/shopRevenue', function(d){
    var chart = echarts.init(document.getElementById('chartShopChart'));
    var pie = [];
    for (var i = 0; i < (d.names || []).length; i++) pie.push({name: d.names[i], value: d.values[i]});
    chart.setOption({
        tooltip:{trigger:'item'},
        legend:{bottom:0, type:'scroll',fontSize:11},
        series:[{type:'pie',radius:['35%','65%'],data:pie,label:{fontSize:11}}]
    });
    window.addEventListener('resize', function(){ chart.resize(); });
});

// 月度订单（折线）
xhr('/admin/stat/monthlyOrders', function(d){
    var chart = echarts.init(document.getElementById('chartOrderChart'));
    chart.setOption({
        color:['#27ae60'],
        tooltip:{trigger:'axis'},
        grid:{left:40,right:20,top:20,bottom:40},
        xAxis:{type:'category',data:d.months,boundaryGap:false,axisLabel:{fontSize:11}},
        yAxis:{type:'value'},
        series:[{type:'line',smooth:true,areaStyle:{opacity:0.3},data:d.counts}]
    });
    window.addEventListener('resize', function(){ chart.resize(); });
});

// 用户增长（折线）
xhr('/admin/stat/userGrowth', function(d){
    var chart = echarts.init(document.getElementById('chartUserChart'));
    chart.setOption({
        color:['#e67e22'],
        tooltip:{trigger:'axis'},
        grid:{left:40,right:20,top:20,bottom:40},
        xAxis:{type:'category',data:d.months,boundaryGap:false,axisLabel:{fontSize:11}},
        yAxis:{type:'value'},
        series:[{type:'line',smooth:true,areaStyle:{opacity:0.3},data:d.counts}]
    });
    window.addEventListener('resize', function(){ chart.resize(); });
});
</script>
</body>
</html>
