# 淘宝购物系统 - Git合并策略文档

## 一、分支结构

### 1.1 分支命名规范

| 分支类型 | 命名格式 | 说明 |
|----------|----------|------|
| 主分支 | `main` | 生产环境稳定版本 |
| 公共基础分支 | `feature/public-base` | 公共工具类、过滤器、数据库脚本 |
| 组员模块分支 | `feature/admin-operator-module` | 组员1：运营商后台模块 |
| 组员模块分支 | `feature/shop-merchant-module` | 组员2：商家店铺模块 |
| 组员模块分支 | `feature/customer-buyer-module` | 组员3：顾客前台模块 |

### 1.2 当前分支清单

| 分支名称 | 负责人 | 功能范围 |
|----------|--------|----------|
| main | 组员4 | 主分支，合并最终版本 |
| feature/public-base | 组员4 | 公共基础组件、数据库、工具类 |
| feature/admin-operator-module | 组员1 | 运营商后台管理模块 |
| feature/shop-merchant-module | 组员2 | 店铺商家模块 |
| feature/customer-buyer-module | 组员3 | 顾客前台买家模块 |

---

## 二、分支协作规则

### 2.1 开发流程

```
┌─────────────────────────────────────────────────────────────────┐
│                         main (主分支)                          │
│                              ▲                                  │
│                              │ 最终合并                         │
│                              │                                  │
├─────────────────────────────────────────────────────────────────┤
│              feature/public-base (公共基础分支)                  │
│                 ▲          ▲          ▲                         │
│                 │          │          │ 拉取公共代码             │
├─────────────────┴──────────┴──────────┴─────────────────────────┤
│   feature/admin-operator-module     (组员1开发)                 │
│   feature/shop-merchant-module      (组员2开发)                 │
│   feature/customer-buyer-module     (组员3开发)                 │
│                                                                  │
│   各自独立开发，互不干扰，仅拉取公共分支更新                       │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 规则说明

1. **独立开发原则**：每人只维护自身业务代码，禁止修改其他组员的模块代码
2. **公共代码统一管理**：公共工具类、过滤器、数据库脚本由组员4统一维护
3. **定时拉取更新**：每周至少从 `feature/public-base` 拉取一次更新，保持公共代码同步
4. **禁止直接推主分支**：所有开发必须在各自的 feature 分支上进行
5. **文档独立完成**：每人独立完成整套模块文档，无需集中汇总

---

## 三、合并流程

### 3.1 合并顺序

```
第1步：feature/public-base → main
第2步：feature/admin-operator-module → main
第3步：feature/shop-merchant-module → main
第4步：feature/customer-buyer-module → main
```

### 3.2 合并步骤详解

#### 步骤1：合并公共基础分支

```bash
# 切换到主分支
git checkout main

# 拉取远程最新代码
git pull origin main

# 合并公共基础分支
git merge --no-ff feature/public-base

# 推送到远程
git push origin main
```

#### 步骤2：合并组员1模块分支

```bash
# 切换到主分支
git checkout main

# 拉取远程最新代码
git pull origin main

# 合并运营商模块分支
git merge --no-ff feature/admin-operator-module

# 推送到远程
git push origin main
```

#### 步骤3：合并组员2模块分支

```bash
git checkout main
git pull origin main
git merge --no-ff feature/shop-merchant-module
git push origin main
```

#### 步骤4：合并组员3模块分支

```bash
git checkout main
git pull origin main
git merge --no-ff feature/customer-buyer-module
git push origin main
```

---

## 四、冲突处理

### 4.1 冲突类型与责任

| 冲突类型 | 冲突文件示例 | 责任方 |
|----------|--------------|--------|
| 公共代码冲突 | util/*.java, filter/*.java | 组员4 |
| 数据库脚本冲突 | db_init.sql | 组员4 |
| 页面公共部分冲突 | header.jsp, footer.jsp | 组员4 |
| 模块业务冲突 | admin/*.java, shop/*.java, customer/*.java | 对应组员 |
| web.xml 冲突 | web.xml | 组员4（统一协调） |

### 4.2 冲突处理流程

```
1. 检测冲突
   └── git merge 后出现 CONFLICT

2. 判断冲突类型
   ├── 公共代码冲突 → 通知组员4处理
   ├── 模块业务冲突 → 通知对应组员处理
   └── web.xml 冲突 → 组员4协调处理

3. 解决冲突
   ├── 编辑冲突文件，手动解决冲突
   └── git add 标记冲突已解决

4. 提交合并
   └── git commit

5. 验证
   ├── mvn clean package -DskipTests
   └── 启动 Tomcat 测试功能
```

### 4.3 web.xml 冲突预防

为避免 web.xml 冲突，各组员按以下规则添加配置：

| 组员 | 添加内容 | 位置 |
|------|----------|------|
| 组员1 | admin/* Servlet 映射 | 按字母顺序插入 |
| 组员2 | shop/* Servlet 映射 | 按字母顺序插入 |
| 组员3 | customer/*, cart/*, order/* 等 Servlet 映射 | 按字母顺序插入 |
| 组员4 | Filter 配置、全局参数 | 统一管理 |

---

## 五、代码提交规范

### 5.1 提交信息格式

```
<类型>: <描述>

<详细说明（可选）>
```

### 5.2 类型说明

| 类型 | 说明 | 示例 |
|------|------|------|
| feat | 新功能 | feat: 新增商品分类管理 |
| fix | 修复bug | fix: 修复订单状态流转错误 |
| docs | 文档更新 | docs: 更新数据库设计文档 |
| refactor | 代码重构 | refactor: 优化分页查询逻辑 |
| style | 代码格式 | style: 调整代码缩进 |
| test | 测试 | test: 添加登录功能测试用例 |
| chore | 构建/工具 | chore: 更新 Maven 依赖 |

### 5.3 示例

```
feat: 新增购物车功能

- 添加 CartServlet 处理购物车操作
- 实现商品加入购物车、修改数量、删除功能
- 添加购物车页面 cart.jsp
```

---

## 六、开发环境配置

### 6.1 本地开发环境要求

| 项目 | 要求 |
|------|------|
| JDK | 17 或更高版本 |
| Maven | 3.6+ |
| MySQL | 5.7+ 或 8.0+ |
| Tomcat | 9.0+ |

### 6.2 数据库连接配置

在 `src/main/resources/db.properties` 中配置：

```properties
driverClassName=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/taobao_shop?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8
username=root
password=your_password
initialSize=5
maxActive=20
minIdle=5
maxWait=60000
```

### 6.3 初始化数据库

```bash
mysql -u root -p < src/main/resources/db_init.sql
```

---

## 七、发布流程

### 7.1 发布前检查清单

- [ ] 所有分支已合并到 main
- [ ] Maven 构建成功（mvn clean package -DskipTests）
- [ ] 数据库脚本已执行
- [ ] 各模块功能测试通过
- [ ] 权限过滤器配置正确
- [ ] web.xml 配置完整

### 7.2 打包部署

```bash
# 进入项目目录
cd d:\webzhku-system

# Maven 打包
mvn clean package -DskipTests

# 将 war 包复制到 Tomcat
copy target/taobao-shop.war D:\apache-tomcat-9.0.x\webapps\

# 启动 Tomcat
D:\apache-tomcat-9.0.x\bin\startup.bat
```

---

## 八、常见问题处理

### 8.1 合并冲突

**问题**：合并时出现文件冲突

**解决**：
```bash
# 查看冲突文件
git status

# 编辑冲突文件，手动解决冲突
# 冲突标记：<<<<<<< HEAD, =======, >>>>>>> branch-name

# 标记冲突已解决
git add <冲突文件名>

# 提交合并
git commit
```

### 8.2 推送被拒绝

**问题**：git push 时提示 "rejected"

**解决**：
```bash
# 先拉取远程最新代码
git pull origin <分支名>

# 解决冲突后再推送
git push origin <分支名>
```

### 8.3 忘记拉取更新

**问题**：本地分支落后于远程

**解决**：
```bash
# 从公共基础分支拉取更新
git checkout feature/admin-operator-module
git pull origin feature/public-base

# 处理冲突后提交
git add .
git commit -m "merge: 同步公共基础代码"
git push origin feature/admin-operator-module
```

---

## 九、答辩准备

### 9.1 现场演示顺序

1. **组员4**：部署完整项目，启动 Tomcat
2. **组员1**：演示运营商后台功能
3. **组员2**：演示商家后台功能
4. **组员3**：演示顾客前台购物流程

### 9.2 高频答辩问题准备

| 问题类型 | 负责人 |
|----------|--------|
| 数据库设计、表结构、外键关联 | 组员4 |
| 权限过滤器原理、角色划分 | 组员4 |
| 工具类设计思路、复用性 | 组员4 |
| Git 分支策略、合并冲突处理 | 组员4 |
| 运营商后台功能、数据统计 | 组员1 |
| 商家店铺管理、订单处理 | 组员2 |
| 购物车逻辑、支付流程 | 组员3 |

---

**文档版本**: v1.0  
**创建日期**: 2026-07-12  
**维护人**: 组员4  
**关联文档**: [数据库设计文档](数据库设计文档.md), [公共工具类文档](公共工具类文档.md)