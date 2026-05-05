# Admin JSP Management Panel Design

## Overview

Add a server-side rendered admin management panel to the existing Spring Boot + Vue 3 travel platform. The admin panel uses JSP + JSTL + Bootstrap 5, with independent session-based authentication. All CRUD operations return full page views (no AJAX).

## Goals

- Provide admin interface for managing users, guides, stories, destinations, and comments
- Independent admin login (not shared with user-side JWT auth)
- Clean, simple Bootstrap 5 UI with sidebar navigation
- Reuse existing service/mapper layer

## Project Structure

```
src/main/
├── java/com/travel/
│   ├── controller/admin/
│   │   ├── AdminAuthController.java
│   │   ├── AdminDashboardController.java
│   │   ├── AdminUserController.java
│   │   ├── AdminGuideController.java
│   │   ├── AdminStoryController.java
│   │   ├── AdminDestinationController.java
│   │   └── AdminCommentController.java
│   ├── interceptor/
│   │   └── AdminInterceptor.java
│   └── service/
│       └── AdminAuthService.java
├── resources/
│   └── views/admin/
│       ├── login.jsp
│       ├── dashboard.jsp
│       ├── common/
│       │   ├── header.jsp
│       │   └── footer.jsp
│       ├── user/
│       │   ├── list.jsp
│       │   └── edit.jsp
│       ├── guide/
│       │   ├── list.jsp
│       │   └── edit.jsp
│       ├── story/
│       │   ├── list.jsp
│       │   └── edit.jsp
│       ├── destination/
│       │   ├── list.jsp
│       │   └── edit.jsp
│       └── comment/
│           └── list.jsp
```

## Authentication

- Default admin credentials: `admin` / `admin123` (hardcoded in `AdminAuthService`)
- Session-based: login sets ` HttpSession attribute `adminUser`
- `AdminInterceptor` intercepts all `/admin/**` paths except `/admin/login` and static resources
- On unauthorized access, redirect to `/admin/login`

## Page Layout

- Left sidebar (200px fixed width) + right content area
- Sidebar: title "旅游管理后台" at top, navigation menu below (仪表盘, 用户管理, 攻略管理, 故事管理, 目的地管理, 评论管理)
- Current page highlighted in sidebar
- Bootstrap 5 via CDN, clean white theme, `table-striped` for tables

## Module CRUD Operations

### Dashboard
- GET `/admin` - Display statistics (total users, guides, stories, destinations, comments)

### User Management
- GET `/admin/users` - Paginated list (username, nickname, phone, register time)
- GET `/admin/users/{id}/edit` - Edit form
- POST `/admin/users/{id}/edit` - Save changes
- POST `/admin/users/{id}/delete` - Delete user

### Guide Management
- GET `/admin/guides` - Paginated list (title, author, likes, publish time)
- GET `/admin/guides/{id}/edit` - Edit form
- POST `/admin/guides/{id}/edit` - Save changes
- POST `/admin/guides/{id}/delete` - Delete guide

### Story Management
- GET `/admin/stories` - Paginated list (title, author, publish time)
- GET `/admin/stories/{id}/edit` - Edit form
- POST `/admin/stories/{id}/edit` - Save changes
- POST `/admin/stories/{id}/delete` - Delete story

### Destination Management
- GET `/admin/destinations` - Paginated list (name, rating, favorites)
- GET `/admin/destinations/{id}/edit` - Edit form
- POST `/admin/destinations/{id}/edit` - Save changes
- POST `/admin/destinations/{id}/delete` - Delete destination

### Comment Management
- GET `/admin/comments` - Paginated list (content, commenter, related guide, time)
- POST `/admin/comments/{id}/delete` - Delete comment (read-only, no edit)

## Technical Dependencies

### pom.xml additions
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

### application.yml additions
```yaml
spring:
  mvc:
    view:
      prefix: /WEB-INF/views/
      suffix: .jsp
```

### WebMvcConfig changes
- Register `AdminInterceptor` for `/admin/**`
- Exclude `/admin/login`, `/admin/css/**`, `/admin/js/**`, `/admin/images/**`

## Implementation Order

1. Add JSP dependencies and configure view resolver
2. Create `AdminAuthService` and `AdminInterceptor`
3. Create `AdminAuthController` (login/logout)
4. Create login.jsp
5. Create common header.jsp and footer.jsp (sidebar layout)
6. Create `AdminDashboardController` + dashboard.jsp
7. Create `AdminUserController` + user list/edit JSPs
8. Create `AdminGuideController` + guide list/edit JSPs
9. Create `AdminStoryController` + story list/edit JSPs
10. Create `AdminDestinationController` + destination list/edit JSPs
11. Create `AdminCommentController` + comment list JSP
12. Test all pages and CRUD operations

## Reused Existing Components

- `UserService` / `UserMapper` - user CRUD
- `GuideService` / `GuideMapper` - guide queries
- `GuideStoryService` / `GuideStoryMapper` - story queries
- `DestinationService` / `DestinationMapper` - destination queries
- `GuideCommentService` / `GuideCommentMapper` - comment queries
- `User`, `Destination`, `GuideSummary`, `GuideDetail`, `GuideComment` models
- `PageResult` for pagination
