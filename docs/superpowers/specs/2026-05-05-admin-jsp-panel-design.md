# Admin JSP 管理端设计文档

## 概述

在现有的 Spring Boot + Vue 3 旅游平台基础上，新增一个服务端渲染的 Admin 管理端。使用 JSP + JSTL + Bootstrap 5 技术栈，独立的 Session 认证机制。所有 CRUD 操作返回完整页面视图（无 AJAX）。

## 目标

- 提供管理用户、攻略、故事、目的地、评论的后台界面
- Admin 独立登录（不复用用户端的 JWT 认证）
- 简洁的 Bootstrap 5 UI，带侧边栏导航
- 复用现有的 service/mapper 层

## 项目结构

```
src/main/
├── java/com/travel/
│   ├── controller/admin/
│   │   ├── AdminAuthController.java          # 登录/登出
│   │   ├── AdminDashboardController.java     # 首页仪表盘
│   │   ├── AdminUserController.java          # 用户管理
│   │   ├── AdminGuideController.java         # 攻略管理
│   │   ├── AdminStoryController.java         # 故事管理
│   │   ├── AdminDestinationController.java   # 目的地管理
│   │   └── AdminCommentController.java       # 评论管理
│   ├── interceptor/
│   │   └── AdminInterceptor.java             # Admin 登录拦截器
│   └── service/
│       └── AdminAuthService.java             # Admin 认证服务
├── resources/
│   └── views/admin/
│       ├── login.jsp                         # 登录页
│       ├── dashboard.jsp                     # 仪表盘
│       ├── common/
│       │   ├── header.jsp                    # 公共头部+侧边栏
│       │   └── footer.jsp                    # 公共尾部
│       ├── user/
│       │   ├── list.jsp                      # 用户列表
│       │   └── edit.jsp                      # 用户编辑
│       ├── guide/
│       │   ├── list.jsp                      # 攻略列表
│       │   └── edit.jsp                      # 攻略编辑
│       ├── story/
│       │   ├── list.jsp                      # 故事列表
│       │   └── edit.jsp                      # 故事编辑
│       ├── destination/
│       │   ├── list.jsp                      # 目的地列表
│       │   └── edit.jsp                      # 目的地编辑
│       └── comment/
│           └── list.jsp                      # 评论列表（只读）
```

## 认证机制

- 默认管理员账号：`admin` / `admin123`（硬编码在 `AdminAuthService` 中）
- 基于 Session：登录成功后将管理员信息存入 `HttpSession`，属性名 `adminUser`
- `AdminInterceptor` 拦截所有 `/admin/**` 路径，排除 `/admin/login` 和静态资源
- 未登录访问时重定向到 `/admin/login`

## 页面布局

- 左侧固定侧边栏（200px 宽）+ 右侧内容区
- 侧边栏：顶部显示"旅游管理后台"标题，下方导航菜单（仪表盘、用户管理、攻略管理、故事管理、目的地管理、评论管理）
- 当前页面在侧边栏中高亮显示
- Bootstrap 5 CDN 引入，简洁白色主题，表格使用 `table-striped`

## 各模块 CRUD 功能

### 仪表盘
- GET `/admin` - 显示统计数据（总用户数、总攻略数、总故事数、总目的地数、总评论数）

### 用户管理
- GET `/admin/users` - 分页列表（用户名、昵称、手机号、注册时间）
- GET `/admin/users/{id}/edit` - 编辑表单
- POST `/admin/users/{id}/edit` - 保存修改
- POST `/admin/users/{id}/delete` - 删除用户

### 攻略管理
- GET `/admin/guides` - 分页列表（标题、作者、点赞数、发布时间）
- GET `/admin/guides/{id}/edit` - 编辑表单
- POST `/admin/guides/{id}/edit` - 保存修改
- POST `/admin/guides/{id}/delete` - 删除攻略

### 故事管理
- GET `/admin/stories` - 分页列表（标题、作者、发布时间）
- GET `/admin/stories/{id}/edit` - 编辑表单
- POST `/admin/stories/{id}/edit` - 保存修改
- POST `/admin/stories/{id}/delete` - 删除故事

### 目的地管理
- GET `/admin/destinations` - 分页列表（名称、评分、收藏数）
- GET `/admin/destinations/{id}/edit` - 编辑表单
- POST `/admin/destinations/{id}/edit` - 保存修改
- POST `/admin/destinations/{id}/delete` - 删除目的地

### 评论管理
- GET `/admin/comments` - 分页列表（评论内容、评论者、关联攻略、时间）
- POST `/admin/comments/{id}/delete` - 删除评论（只读，无编辑页）

## 技术依赖

### pom.xml 新增依赖
```xml
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
</dependency>
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>javax.servlet.jsp</groupId>
    <artifactId>javax.servlet.jsp-api</artifactId>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
</dependency>
```

### application.yml 新增配置
```yaml
spring:
  mvc:
    view:
      prefix: /WEB-INF/views/
      suffix: .jsp
```

### WebMvcConfig 修改
- 注册 `AdminInterceptor` 拦截 `/admin/**`
- 排除 `/admin/login`、`/admin/css/**`、`/admin/js/**`、`/admin/images/**`

## 实现顺序

1. 添加 JSP 依赖并配置视图解析器
2. 创建 `AdminAuthService` 和 `AdminInterceptor`
3. 创建 `AdminAuthController`（登录/登出）
4. 创建 login.jsp
5. 创建公共 header.jsp 和 footer.jsp（侧边栏布局）
6. 创建 `AdminDashboardController` + dashboard.jsp
7. 创建 `AdminUserController` + 用户列表/编辑 JSP
8. 创建 `AdminGuideController` + 攻略列表/编辑 JSP
9. 创建 `AdminStoryController` + 故事列表/编辑 JSP
10. 创建 `AdminDestinationController` + 目的地列表/编辑 JSP
11. 创建 `AdminCommentController` + 评论列表 JSP
12. 测试所有页面和 CRUD 操作

## 复用现有组件

- `UserService` / `UserMapper` - 用户增删改查
- `GuideService` / `GuideMapper` - 攻略查询
- `GuideStoryService` / `GuideStoryMapper` - 故事查询
- `DestinationService` / `DestinationMapper` - 目的地查询
- `GuideCommentService` / `GuideCommentMapper` - 评论查询
- `User`、`Destination`、`GuideSummary`、`GuideDetail`、`GuideComment` 模型
- `PageResult` 分页工具
