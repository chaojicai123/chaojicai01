# 养老服务系统

基于 **Spring Boot + Vue.js + MySQL** 的养老服务管理系统，前后端分离，支持老人信息管理、服务预约与分配、护理人员管理、健康数据监测、消息通知、统计分析等模块。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3、Spring Security、JWT、Spring Data JPA |
| 前端 | Vue 3、Vite、Vue Router、Pinia、Element Plus、Axios |
| 数据库 | MySQL 8.4 |

## 项目结构

```
elder_care_system/
├── elder_care_server/     # Spring Boot 后端
│   ├── src/main/java/com/eldercare/
│   │   ├── config/        # 安全、异常处理、数据初始化
│   │   ├── controller/    # REST 接口
│   │   ├── dto/           # 请求/响应 DTO
│   │   ├── entity/        # JPA 实体
│   │   ├── repository/    # 数据访问
│   │   ├── security/      # JWT 认证
│   │   └── service/       # 业务逻辑
│   └── pom.xml
│
└── elder_care_web/        # Vue 3 前端
    ├── src/
    │   ├── api/           # 接口封装
    │   ├── layouts/       # 布局
    │   ├── router/        # 路由
    │   ├── stores/        # Pinia 状态
    │   └── views/         # 页面
    └── package.json
```

## 功能模块

- **老人信息管理**：增删改查、状态、紧急联系人、健康状况
- **护理人员管理**：人员信息、职称技能、可预约状态
- **服务预约与分配**：创建预约、分配护理人员、状态流转（待确认→已确认→进行中→已完成）
- **健康数据监测**：按老人录入血压、心率、血糖、体温、体重等，支持来源（如手环、手动）
- **消息通知**：系统通知、按对象查询、已读未读
- **统计分析**：工作台统计老人数、护理人员数、预约数、健康记录数
- **权限**：登录 JWT、角色 ADMIN/STAFF/FAMILY，`/admin/**` 仅 ADMIN

## 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.4（确保已安装并启动）

## 后端运行

### 使用 MySQL（默认）

1. 创建数据库（可选，配置了 `createDatabaseIfNotExist=true`）：
   ```sql
   CREATE DATABASE elder_care CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. 修改 `elder_care_server/src/main/resources/application.yml` 中的 `spring.datasource.url/username/password`。
3. 启动（在 **elder_care_server** 目录下执行，无需安装 Maven，使用项目自带脚本）：
   ```bash
   cd elder_care_server
   .\mvnw.cmd spring-boot:run
   ```
   或在 IDE 中运行 `ElderCareApplication`。

### 使用开发环境配置（MySQL 8.4）

在 **elder_care_server** 目录下：

```bash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

- 使用独立的开发数据库 `elder_care_dev`
- 首次运行会初始化管理员：**用户名 admin，密码 admin123**。

- API 根地址：`http://localhost:8080/api`。

## 前端运行

```bash
cd elder_care_web
npm install
npm run dev
```

- 访问：http://localhost:5173
- 开发环境已配置代理，请求会转发到 `http://localhost:8080/api`。
- 默认登录：**admin / admin123**（需先按上文用 dev 配置启动后端并初始化数据）。

## 主要 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 登录 |
| GET  | /api/elders | 老人分页列表 |
| GET  | /api/elders/{id} | 老人详情 |
| POST | /api/elders | 新增老人 |
| PUT  | /api/elders/{id} | 更新老人 |
| DELETE | /api/elders/{id} | 删除老人 |
| GET  | /api/staff | 护理人员列表 |
| GET  | /api/staff/available | 可预约护理人员 |
| GET  | /api/appointments | 预约列表 |
| POST | /api/appointments | 新建预约 |
| PATCH | /api/appointments/{id}/assign?staffId= | 分配护理人员 |
| PATCH | /api/appointments/{id}/status?status= | 更新预约状态 |
| GET  | /api/health/elder/{elderId} | 健康记录分页 |
| POST | /api/health | 录入健康数据 |
| GET  | /api/notifications | 通知列表 |
| GET  | /api/admin/stats | 工作台统计（需 ADMIN） |

## 扩展方向

- **物联网设备**：健康数据 `source` 可扩展为设备编码，接入手环、智能床垫等上报接口。
- **第三方服务**：集成急救呼叫、医疗数据接口，在服务层封装调用。
- **智能化**：在健康记录与预约数据基础上做健康风险预测、服务需求推荐（单独模块或新服务）。
- **数据库**：可增加 MongoDB 存储非结构化健康/日志数据，与 MySQL 并存。