<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html><html lang="zh-CN"><head><meta charset="UTF-8"><title>数据概览</title>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<script src="https://cdn.bootcdn.net/ajax/libs/echarts/5.4.3/echarts.min.js"></script></head>
<body><div class="container-fluid"><div class="row">
<div class="col-md-2 sidebar"><h4>🛡️ 运营商后台</h4>
<a href="${pageContext.request.contextPath}/admin/stat/dashboard" class="active">数据概览</a>
<a href="${pageContext.request.contextPath}/admin/user/list">用户管理</a>
<a href="${pageContext.request.contextPath}/admin/shop/auditList">商家审核</a>
<a href="${pageContext.request.contextPath}/admin/announcement/list">公告管理</a>
<a href="${pageContext.request.contextPath}/admin/order/list">订单监控</a></div>
<div class="col-md-10" style="padding:20px">
<div class="row">
<div class="col-md-3"><div class="panel panel-default"><div class="panel-body text-center"><h3>${stats.totalUsers}</h3><p>总用户</p></div></div></div>
<div class="col-md-3"><div class="panel panel-default"><div class="panel-body text-center"><h3>${stats.activeShops}</h3><p>营业店铺</p></div></div></div>
<div class="col-md-3"><div class="panel panel-default"><div class="panel-body text-center"><h3>${stats.activeProducts}</h3><p>上架商品</p></div></div></div>
<div class="col-md-3"><div class="panel panel-success"><div class="panel-body text-center"><h3>￥${stats.totalRevenue}</h3><p>总营收</p></div></div></div>
</div>
<div class="row"><div class="col-md-6"><div id="salesChart" style="height:350px"></div></div><div class="col-md-6"><div id="shopChart" style="height:350px"></div></div></div>
<div class="row" style="margin-top:20px"><div class="col-md-6"><div id="orderChart" style="height:350px"></div></div><div class="col-md-6"><div id="userChart" style="height:350px"></div></div></div>
</div></div></div>
<script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script>var ctx="${pageContext.request.contextPath}";
$.get(ctx+"/admin/stat/salesRank",function(d){var c=echarts.init(document.getElementById("salesChart"));c.setOption({title:{text:"销量排行"},tooltip:{},xAxis:{type:"category",data:d.names},yAxis:{type:"value"},series:[{type:"bar",data:d.values}]})});
$.get(ctx+"/admin/stat/shopRevenue",function(d){var c=echarts.init(document.getElementById("shopChart"));var p=[];for(var i=0;i<d.names.length;i++)p.push({name:d.names[i],value:d.values[i]});c.setOption({title:{text:"店铺营收"},tooltip:{},series:[{type:"pie",radius:"60%",data:p}]})});
$.get(ctx+"/admin/stat/monthlyOrders",function(d){var c=echarts.init(document.getElementById("orderChart"));c.setOption({title:{text:"月度订单"},tooltip:{},xAxis:{type:"category",data:d.months},yAxis:{type:"value"},series:[{type:"line",data:d.counts,smooth:true}]})});
$.get(ctx+"/admin/stat/userGrowth",function(d){var c=echarts.init(document.getElementById("userChart"));c.setOption({title:{text:"用户增长"},tooltip:{},xAxis:{type:"category",data:d.months},yAxis:{type:"value"},series:[{type:"line",data:d.counts,smooth:true,areaStyle:{}}]})});
</script></body></html>