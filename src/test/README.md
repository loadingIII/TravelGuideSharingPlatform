# 旅游攻略分享平台（TravelProject）

基于 **Spring Boot + MyBatis + MySQL + Vue 3** 的旅游内容平台，包含：

- 用户端：登录注册、首页推荐、目的地浏览、攻略与故事查看、个人资料管理
- 管理端：内容审核、用户管理、统计看板与操作日志

---

## 1. 技术栈

| 分层 | 技术 |
|---|---|
| 后端 | Java 17, Spring Boot 4.0.6, MyBatis 4.0.1, MySQL 8, JWT (jjwt 0.12.7), Spring Security Crypto, Jakarta Validation |
| 前端 | Vue 3, Vite 5 |
| 管理端 | 后端静态页面（`src/main/resources/static/admin`）+ Session 鉴权 |

---

## 2. 核心能力

### 用户端

- 认证：注册 / 登录 / 刷新 Token
- 首页：推荐目的地、热门攻略、旅行者故事
- 目的地：列表、热门、详情、目的地攻略
- 攻略：分页筛选、详情、评论树、点赞榜
- 故事：分页列表、详情、按用户查询
- 用户中心：获取个人信息、更新资料

### 管理端

- 管理员登录（Session + Remember Cookie）
- 用户、攻略、故事、评论、目的地管理
- 审核接口（通过/拒绝/下架）
- 统计面板与管理员操作日志

---

## 3. 项目结构

```text
TravelProject
├─ src
│  ├─ main
│  │  ├─ java\com\travel
│  │  │  ├─ controller\user      # 用户端 API
│  │  │  ├─ controller\admin     # 管理端 API
│  │  │  ├─ service              # 业务层
│  │  │  ├─ mapper               # MyBatis Mapper 接口
│  │  │  ├─ security             # JWT 与登录态上下文
│  │  │  ├─ interceptor          # 鉴权拦截器
│  │  │  ├─ config               # Spring 配置
│  │  │  ├─ common               # 统一响应与异常
│  │  │  └─ pojo                 # model/dto/vo
│  │  ├─ resources
│  │  │  ├─ application.yml
│  │  │  ├─ mapper\*.xml
│  │  │  └─ static
│  │  │     ├─ SqlData           # 建表与初始化 SQL
│  │  │     └─ admin             # 管理端静态资源 
│  │  └─ webapp                  # 管理端静态资源(页面)
│  └─ test\java\com\travel
├─ frontend                        # 用户端前端（目录名保持现状）
│  ├─ src
│  ├─ public
│  └─ package.json
└─ pom.xml
```

---

## 4. 本地启动

## 4.1 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Node.js 18+

## 4.2 初始化数据库

数据库名默认为 `travel_guide_platform`。

按顺序执行：

```sql
-- 1) 基础业务表
SOURCE src/main/resources/static/SqlData/init.sql
```

## 4.3 配置后端

编辑 `src/main/resources/application.yml`：

- `spring.datasource.url/username/password`
- `app.jwt.secret`（请替换默认值）
- `server.port`（默认 `8082`）

## 4.4 启动后端

```bash
mvn spring-boot:run
```

后端地址：`http://localhost:8082`

## 4.5 启动前端（用户端）

```bash
cd frontend
npm install
npm run dev
```

前端地址：`http://localhost:5173`  
Vite 已配置 `/api -> http://localhost:8082` 代理。

## 4.6 访问管理端

- 管理入口：`http://localhost:8082/admin`
- 默认账号（首次启动自动创建）：`admin / admin123`

> 请在首次登录后立即修改管理员密码（当前项目默认账号用于本地开发演示）。

---

## 5. API 概览

### 用户端 API

| 模块 | 方法 | 路径 |
|---|---|---|
| 认证 | POST | `/auth/register` |
| 认证 | POST | `/auth/login` |
| 认证 | POST | `/auth/refresh` |
| 首页 | GET | `/home` |
| 搜索 | GET | `/search?q=关键词&type=all\|guide\|destination` |
| 目的地 | GET | `/destinations` |
| 目的地 | GET | `/destinations/hot` |
| 目的地 | GET | `/destinations/{id}` |
| 目的地 | GET | `/destinations/{id}/guides` |
| 攻略 | GET | `/guides/page` |
| 攻略 | GET | `/guides/{id}` |
| 攻略 | GET | `/guides/comments/{id}` |
| 攻略 | GET | `/guides/ranking/likes` |
| 故事 | GET | `/stories/page` |
| 故事 | GET | `/stories/{id}` |
| 故事 | GET | `/stories/user/{userId}` |
| 用户 | GET | `/users/me` |
| 用户 | PUT | `/users/me/profile` |

### 管理端 API（示例）

| 模块 | 方法 | 路径 |
|---|---|---|
| 登录态 | POST | `/admin/login` |
| 登录态 | POST | `/admin/logout` |
| 登录态 | GET | `/admin/me` |
| 用户管理 | GET | `/admin/users` |
| 攻略管理 | GET | `/admin/guides` |
| 故事管理 | GET | `/admin/stories` |
| 评论管理 | GET | `/admin/comments` |
| 统计 | GET | `/admin/stats` |

---

## 6. 鉴权与响应格式

- 用户端受保护接口通过 `@RequireLogin` 标记，需要请求头：
  `Authorization: Bearer <token>`
- 管理端使用 Session（`/admin/**`）
- 统一返回结构：

```json
{
  "code": "OK",
  "message": "success",
  "data": {}
}
```

---

## 7. 常用开发命令

```bash
# 后端
mvn test
mvn clean package

# 前端
cd fontend
npm run dev
npm run build
npm run preview
```

---

## 8. 说明

- 仓库中的前端目录名为 `fontend`（非 `frontend`），命令请按实际目录执行。
- 当前仓库包含基础 Spring Boot 启动测试：`src/test/java/com/travel/TravelProjectApplicationTests.java`。

---

## License

MIT
