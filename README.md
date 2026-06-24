# 淘宝购物系统 - 部署指南

## 📋 环境要求

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 1.8+ | Java Development Kit |
| MySQL | 5.7+ / 8.0+ | 数据库服务器 |
| Tomcat | 8.5+ / 9.0+ | Web应用服务器 |
| Maven | 3.6+ | 项目构建工具（可选） |

## 🚀 部署步骤

### 第一步：导入数据库

1. **打开MySQL客户端**（命令行或可视化工具如Navicat、DBeaver）

2. **创建数据库**：
```sql
CREATE DATABASE taobao_shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE taobao_shop;
```

3. **执行初始化脚本**：
```bash
# 方式一：命令行执行
mysql -u root -p taobao_shop < db_init.sql

# 方式二：可视化工具导入
# 在Navicat/DBeaver中打开 db_init.sql 文件并执行
```

**脚本位置**：`taobao-shop/db_init.sql` 或 `taobao-shop/src/main/resources/db_init.sql`

### 第二步：配置数据库连接

修改文件：`src/main/resources/db.properties`

```properties
# 修改为你的MySQL配置
url=jdbc:mysql://localhost:3306/taobao_shop?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
username=root          # 你的MySQL用户名
password=your_password # 你的MySQL密码
```

### 第三步：构建项目

#### 方式一：使用Maven（推荐）

```bash
# 进入项目目录
cd taobao-shop

# 编译打包（跳过测试）
mvn clean package -DskipTests

# 打包成功后，生成的war文件位置：
# target/taobao-shop.war
```

#### 方式二：使用IDE（IntelliJ IDEA / Eclipse）

1. 打开项目
2. 等待Maven依赖下载完成
3. 执行Maven目标：`clean package -DskipTests`

### 第四步：部署到Tomcat

#### 方式一：手动部署

```bash
# 1. 复制war文件到Tomcat的webapps目录
cp target/taobao-shop.war /path/to/tomcat/webapps/

# 2. 启动Tomcat
# Windows
cd /path/to/tomcat/bin
startup.bat

# Linux/Mac
cd /path/to/tomcat/bin
./startup.sh
```

#### 方式二：IDEA集成部署

1. 配置Tomcat服务器
2. 添加Artifact：选择 `taobao-shop:war exploded`
3. 启动Tomcat

### 第五步：访问项目

启动成功后，打开浏览器访问：

```
http://localhost:8080/taobao-shop/
```

## 👤 演示账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 运营商 | admin | admin123 | 平台管理员后台 |
| 商家1 | shop001 | 123456 | 数码小铺店主 |
| 商家2 | shop002 | 123456 | 服饰精品店店主 |
| 买家1 | buyer001 | 123456 | 购物用户 |
| 买家2 | buyer002 | 123456 | 购物用户 |

## 🎯 功能模块

### 平台运营商（admin）
- 账户权限管理：查看/封禁用户、商家入驻审核
- 数据可视化：ECharts统计图表（销量、营收、订单）
- 公告管理：发布/编辑全站公告
- 订单监控：查看全平台订单

### 店铺商家（shop001/shop002）
- 店铺信息：编辑店铺资料、上传头像、CKEditor编辑简介
- 商品管理：商品上架/下架、多图上传、库存调整
- 订单处理：待发货/已发货/已完成订单管理、物流录入
- 评价管理：查看评价、商家回复

### 顾客买家（buyer001/buyer002）
- 个人中心：修改信息、管理收货地址
- 商品浏览：首页展示、搜索筛选、商品详情
- 购物车：添加/修改/删除商品
- 交易流程：下单、模拟支付、确认收货、评价
- 售后服务：申请售后退款

### 浏览者（未登录）
- 浏览首页商品
- 搜索查看商品详情

## 🏗️ 项目架构

```
taobao-shop/
├── src/main/java/com/taobao/
│   ├── admin/servlet/     # 运营商后台Servlet
│   ├── customer/servlet/  # 买家前台Servlet
│   ├── shop/servlet/      # 商家后台Servlet
│   ├── servlet/           # 公共Servlet（登录/注册/首页）
│   ├── filter/            # 权限过滤器
│   ├── dao/               # 数据访问层
│   ├── entity/            # 实体类
│   └── util/              # 工具类
├── src/main/webapp/
│   ├── admin/             # 运营商页面
│   ├── shop/              # 商家页面
│   ├── customer/          # 买家页面
│   └── *.jsp              # 公共页面
├── db_init.sql            # 数据库脚本
└── pom.xml                # Maven配置
```

## ⚠️ 常见问题

### 1. 数据库连接失败
- 检查MySQL服务是否启动
- 确认db.properties中的用户名密码正确
- MySQL 8.x需要在URL中添加 `serverTimezone=Asia/Shanghai`

### 2. 页面显示404
- 确认Tomcat已启动且war包已解压
- 检查访问路径是否正确（应为 `/taobao-shop/`）

### 3. 中文乱码
- 项目已配置EncodingFilter统一UTF-8编码
- 确认数据库连接URL包含 `characterEncoding=UTF-8`

### 4. 文件上传失败
- 确保Tomcat目录有写入权限
- 检查上传目录是否存在

## 📝 开发说明

### 技术栈
- **前端**：JSP、JSTL、Bootstrap 3、CKEditor、ECharts
- **后端**：Servlet 3.1、JDBC、Druid连接池
- **数据库**：MySQL 5.7+
- **工具**：Maven、Git

### 权限控制
- EncodingFilter：统一UTF-8编码（最先执行）
- AuthFilter：拦截未登录访问（登录检查）
- ShopAuthFilter：商家后台权限（开店状态检查）

### 订单状态流转
```
待付款(0) → 待发货(1) → 已发货(2) → 已收货(3) → 已完成(4)
                ↓              ↓
            已取消(5)      申请售后 → 退款中(6) → 已退款(7)
```

### 文件上传目录
- 店铺头像：`/upload/shop/`
- 商品图片：`/upload/product/`

---

**项目名称**：淘宝购物系统  
**技术方案**：JSP + Servlet + MySQL  
**开发团队**：4人小组分工协作  
**课程**：Java Web开发期末考查项目
