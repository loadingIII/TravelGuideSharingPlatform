# 旅游攻略平台 (TravelProject)

一个基于 Spring Boot + Vue 3 的旅游攻略平台，提供目的地浏览、旅游攻略查看、旅行者故事分享等功能。

## 技术栈

### 后端

- **Java 17** + **Spring Boot 4.0.6**
- **MyBatis** — ORM 框架
- **MySQL** — 数据库
- **JWT (jjwt 0.12.7)** — 用户认证与授权
- **Spring Security Crypto** — 密码加密
- **Lombok** — 简化代码
- **Jakarta Validation** — 参数校验

### 前端

- **Vue 3** + **Vite 5**
- 响应式 SPA 架构

## 功能特性

- **用户系统** — 注册、登录、JWT Token 刷新、个人资料管理
- **首页展示** — 推荐目的地、热门攻略、旅行者故事
- **目的地浏览** — 热门目的地、目的地详情、目的地下攻略列表
- **旅游攻略** — 多维度筛选（关键词/旅行范围/旅行方式/排序）、攻略详情、评论系统（树形结构）、热度排行
- **旅行者故事** — 故事列表、按用户查询
- **全局搜索** — 支持按关键词搜索攻略和目的地

## 项目结构

```
TravelProject/
├── src/main/java/com/travel/
│   ├── TravelProjectApplication.java    # 启动类
│   ├── config/                          # 配置类
│   ├── controller/                      # 控制器层
│   ├── service/                         # 业务逻辑层
│   ├── mapper/                          # 数据访问层
│   ├── pojo/                            # 实体类 (model/dto/vo)
│   ├── security/                        # JWT 认证相关
│   └── common/                          # 通用工具 (ApiResponse, 异常处理)
├── src/main/resources/
│   ├── application.yml                  # 应用配置
│   └── mapper/                          # MyBatis XML 映射文件
├── fontend/                             # 前端项目 (Vue 3)
│   ├── src/
│   │   ├── components/                  # Vue 组件
│   │   ├── utils/                       # 工具函数
│   │   └── App.vue                      # 根组件
│   └── public/img/                      # 静态图片资源
└── pom.xml                              # Maven 配置
```

## 快速启动

### 环境要求

- JDK 17+
- MySQL 8.0+
- Node.js 18+
- Maven 3.8+

### 1. 数据库初始化

创建数据库并导入表结构：

```sql
CREATE DATABASE travel_guide_platform;
USE travel_guide_platform;
-- 执行 SQL 建表脚本（如有）
```

### 2. 启动后端

```bash
# 修改数据库连接信息
# 编辑 src/main/resources/application.yml

# 构建并运行
mvn spring-boot:run
```

后端服务默认运行在 `http://localhost:8082`

### 3. 启动前端

```bash
cd fontend
npm install
npm run dev
```

前端开发服务器默认运行在 `http://localhost:5173`

## API 接口概览

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 认证 | POST | `/auth/register` | 用户注册 |
| 认证 | POST | `/auth/login` | 用户登录 |
| 认证 | POST | `/auth/refresh` | 刷新 Token |
| 首页 | GET | `/home` | 获取首页数据 |
| 搜索 | GET | `/search?q={keyword}` | 全局搜索 |
| 目的地 | GET | `/destinations` | 目的地列表 |
| 目的地 | GET | `/destinations/hot` | 热门目的地 |
| 目的地 | GET | `/destinations/{id}` | 目的地详情 |
| 目的地 | GET | `/destinations/{id}/guides` | 目的地下攻略 |
| 攻略 | GET | `/guides/page` | 攻略列表（分页筛选） |
| 攻略 | GET | `/guides/{id}` | 攻略详情 |
| 攻略 | GET | `/guides/comments/{id}` | 攻略评论 |
| 攻略 | GET | `/guides/ranking/likes` | 热门攻略排行 |
| 故事 | GET | `/stories/page` | 旅行者故事列表 |
| 故事 | GET | `/stories/{id}` | 故事详情 |
| 故事 | GET | `/stories/user/{userId}` | 用户故事列表 |
| 用户 | GET | `/users/me` | 获取当前用户信息 |
| 用户 | PUT | `/users/me/profile` | 更新个人资料 |

## License

MIT
