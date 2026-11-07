# 汽车租赁管理系统

一个**前后端分离**的在线租车 Web 应用，面向「用户」与「管理员」两类角色，实现车辆查询、在线预订支付、订单台账、留言反馈、数据统计等核心业务，实现车辆出租状态的实时同步，避免重复出租。

> 课程设计项目 · 基于《汽车租赁管理系统需求分析文档》开发

## ✨ 功能特性

### 用户端

- **注册 / 登录**：JWT 无状态鉴权
- **租车查询**：按品牌、类型、日租金区间、座位数、出租状态筛选，支持分页
- **车辆详情**：图片、参数、维护状态、出租状态、租车须知
- **租车预订**：选择取还车时间与地址 → 自动计算租金 → 下单 → 在线支付
- **我的订单**：按状态（待支付/租用中/已完成/已取消）筛选，继续支付 / 取消
- **留言反馈**：提交留言并查看管理员回复
- **个人中心**：维护个人信息

### 管理端

- **数据看板**：车辆/客户/订单/营收统计 + 近 7 日订单趋势 / 品牌分布 / 热门车型图表
- **车辆信息管理**：增删改查（车牌号、品牌、类型、维护状态、购入日期、租金、出租状态、图片上传）
- **客户信息管理**：增删改查（出生日期、性别、身份证号、驾龄等）
- **订单管理与台账**：多条件检索、完成/取消订单、导出 CSV 台账
- **留言管理**：查看并回复客户留言

## 🛠️ 技术栈

| 层次 | 技术 |
| --- | --- |
| 后端 | Spring Boot 3.3 · MyBatis Plus 3.5 · MySQL 8+ · JWT · BCrypt |
| 前端 | Vue 3 · Vite · TypeScript · Pinia · Vue Router 4 · Naive UI · ECharts 5 · Axios |
| 架构 | 前后端分离，RESTful API，后端分层（Controller / Service / Mapper） |

## 📁 项目结构

```
Car Rent System/
├── backend/                 # 后端（Spring Boot，端口 8081）
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/carrent/
│       │   ├── controller/  # 控制层
│       │   ├── service/     # 服务接口 + impl 实现
│       │   ├── mapper/      # MyBatis Plus Mapper
│       │   ├── entity/      # 实体
│       │   ├── dto/ / vo/   # 请求/响应对象
│       │   ├── config/      # 配置（拦截器、CORS、分页、Jackson）
│       │   ├── interceptor/ # JWT 鉴权拦截器
│       │   └── util/        # JWT、上下文等工具
│       └── resources/application.yml
├── frontend/                # 前端（Vue 3 + Vite，端口 5173）
│   └── src/
│       ├── views/           # 页面（前台 + admin/ 后台）
│       ├── components/      # 通用组件
│       ├── api/             # Axios 封装与接口
│       ├── stores/          # Pinia 状态
│       ├── router/          # 路由与守卫
│       └── utils/           # 工具函数
├── sql/
│   ├── schema.sql           # 建库建表
│   └── data.sql             # 车辆种子数据
└── 汽车租赁管理系统需求分析文档.docx
```

## 🚀 快速开始

### 前置要求

- JDK 17
- Node.js 18+ / npm
- MySQL 8+（需先创建数据库）

### 1. 初始化数据库

```bash
mysql -h 127.0.0.1 -P 3306 -uroot -p < sql/schema.sql
mysql -h 127.0.0.1 -P 3306 -uroot -p < sql/data.sql
```

> 后端首次启动时会自动创建默认账号（见下方「默认账号」）。

### 2. 启动后端（端口 8081）

配置好数据库密码（见下方「🔐 数据库配置」），然后在 IDEA 中打开 `backend` 模块，设置 Project SDK 为 JDK 17，运行 `com.carrent.CarRentApplication`；或命令行：

```bash
cd backend
mvn spring-boot:run
```

### 3. 启动前端（端口 5173）

```bash
cd frontend
npm install
npm run dev
```

浏览器访问 http://localhost:5173 。前端已配置代理，将 `/api`、`/uploads` 转发到后端 8081。

> 如需内网其他电脑访问：前端已配置 `host: true`，访问 `http://<本机内网IP>:5173` 即可（`ipconfig getifaddr en0` 查 IP）。

## 👤 默认账号

| 角色 | 账号 | 密码 | 入口 |
| --- | --- | --- | --- |
| 管理员 | `admin` | `admin123` | http://localhost:5173/admin/login |
| 用户 | `user1` | `123456` | http://localhost:5173/login |

## 🔐 数据库配置（重要）

数据库连接配置在 `backend/src/main/resources/application.yml`，密码使用环境变量占位，**请勿把真实密码提交到仓库**：

```yaml
spring:
  datasource:
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:your_mysql_password}
```

每个组员按自己本地的 MySQL 密码配置，二选一：

1. **设置环境变量（推荐）**
   - IDEA：Run Configuration → Environment variables → 添加 `DB_PASSWORD=你的密码`
   - 命令行：`DB_PASSWORD=你的密码 mvn spring-boot:run`
2. **本地改配置文件**：直接把 `application.yml` 里的 `password` 改成你的密码（改完不要 `git add` 该文件）

> 若需更精细的本地配置，可自行创建 `backend/src/main/resources/application-local.yml`（已被 `.gitignore` 忽略），以 `--spring.profiles.active=local` 启动。

## 📄 主要接口约定

- 统一响应结构：`{ code, message, data }`，`code = 200` 表示成功。
- 鉴权方式：请求头 `Authorization: Bearer <token>`；管理端接口需角色为 `ADMIN`。
- 时间格式：`yyyy-MM-dd HH:mm:ss`（日期 `yyyy-MM-dd`）。

## 📌 设计说明

- 车辆「出租状态」与订单状态联动：下单即占用车辆，取消/完成订单后自动释放，避免重复出租。
- 车辆图片支持本地上传（保存到 `backend/uploads/`，通过 `/uploads/**` 访问）或填写网络图片 URL。
- 押金与租金为课程演示的简化实现，仅作金额展示与统计，不涉及真实支付通道。
