# Admin JSP 管理端实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在 Spring Boot 项目中集成 JSP，实现 Admin 管理端，包含用户、攻略、故事、目的地、评论的 CRUD 管理。

**Architecture:** 使用 JSP + JSTL + Bootstrap 5 服务端渲染，Session-based 独立认证，AdminInterceptor 拦截 `/admin/**` 路径。复用现有 Service/Mapper 层，新增 Admin 专用的增删改查方法。

**Tech Stack:** Spring Boot 4.0.6, JSP, JSTL, Bootstrap 5, MyBatis, Lombok

---

## File Structure

### 新建文件
| 文件 | 职责 |
|------|------|
| `src/main/java/com/travel/service/AdminAuthService.java` | Admin 认证服务 |
| `src/main/java/com/travel/interceptor/AdminInterceptor.java` | Admin 登录拦截器 |
| `src/main/java/com/travel/controller/admin/AdminAuthController.java` | 登录/登出 |
| `src/main/java/com/travel/controller/admin/AdminDashboardController.java` | 仪表盘 |
| `src/main/java/com/travel/controller/admin/AdminUserController.java` | 用户管理 |
| `src/main/java/com/travel/controller/admin/AdminGuideController.java` | 攻略管理 |
| `src/main/java/com/travel/controller/admin/AdminStoryController.java` | 故事管理 |
| `src/main/java/com/travel/controller/admin/AdminDestinationController.java` | 目的地管理 |
| `src/main/java/com/travel/controller/admin/AdminCommentController.java` | 评论管理 |
| `src/main/resources/views/admin/login.jsp` | 登录页 |
| `src/main/resources/views/admin/dashboard.jsp` | 仪表盘 |
| `src/main/resources/views/admin/common/header.jsp` | 公共头部+侧边栏 |
| `src/main/resources/views/admin/common/footer.jsp` | 公共尾部 |
| `src/main/resources/views/admin/user/list.jsp` | 用户列表 |
| `src/main/resources/views/admin/user/edit.jsp` | 用户编辑 |
| `src/main/resources/views/admin/guide/list.jsp` | 攻略列表 |
| `src/main/resources/views/admin/guide/edit.jsp` | 攻略编辑 |
| `src/main/resources/views/admin/story/list.jsp` | 故事列表 |
| `src/main/resources/views/admin/story/edit.jsp` | 故事编辑 |
| `src/main/resources/views/admin/destination/list.jsp` | 目的地列表 |
| `src/main/resources/views/admin/destination/edit.jsp` | 目的地编辑 |
| `src/main/resources/views/admin/comment/list.jsp` | 评论列表 |

### 修改文件
| 文件 | 修改内容 |
|------|----------|
| `pom.xml` | 添加 JSP/JSTL 依赖 |
| `src/main/resources/application.yml` | 添加 JSP 视图解析器配置 |
| `src/main/java/com/travel/config/WebMvcConfig.java` | 注册 AdminInterceptor |
| `src/main/java/com/travel/mapper/UserMapper.java` | 添加分页查询、更新、删除方法 |
| `src/main/java/com/travel/mapper/DestinationMapper.java` | 添加更新、删除方法 |
| `src/main/java/com/travel/mapper/GuideMapper.java` | 添加更新、删除方法 |
| `src/main/java/com/travel/mapper/GuideStoryMapper.java` | 添加删除方法 |
| `src/main/java/com/travel/mapper/GuideCommentMapper.java` | 添加分页查询、删除方法 |

---

### Task 1: 添加 JSP 依赖并配置视图解析器

**Files:**
- Modify: `pom.xml`
- Modify: `src/main/resources/application.yml`

- [ ] **Step 1: 在 pom.xml 的 dependencies 中添加 JSP 依赖**

在 `</dependencies>` 标签前添加：

```xml
<!-- JSP 支持 -->
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
</dependency>
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
</dependency>
```

- [ ] **Step 2: 在 application.yml 中添加 JSP 视图解析器配置**

在 `spring:` 下添加：

```yaml
spring:
  mvc:
    view:
      prefix: /WEB-INF/views/
      suffix: .jsp
```

- [ ] **Step 3: 创建 JSP 目录结构**

```bash
mkdir -p src/main/resources/views/admin/common
mkdir -p src/main/resources/views/admin/user
mkdir -p src/main/resources/views/admin/guide
mkdir -p src/main/resources/views/admin/story
mkdir -p src/main/resources/views/admin/destination
mkdir -p src/main/resources/views/admin/comment
```

- [ ] **Step 4: 验证编译通过**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: 提交**

```bash
git add pom.xml src/main/resources/application.yml
git commit -m "feat: 添加JSP依赖和视图解析器配置"
```

---

### Task 2: 扩展 Mapper 层 — 添加 Admin 所需的增删改查方法

**Files:**
- Modify: `src/main/java/com/travel/mapper/UserMapper.java`
- Modify: `src/main/java/com/travel/mapper/DestinationMapper.java`
- Modify: `src/main/java/com/travel/mapper/GuideMapper.java`
- Modify: `src/main/java/com/travel/mapper/GuideStoryMapper.java`
- Modify: `src/main/java/com/travel/mapper/GuideCommentMapper.java`

- [ ] **Step 1: 在 UserMapper 中添加 admin 方法**

在 `UserMapper.java` 的 `insert` 方法后添加：

```java
@Select("SELECT id, username, phone, email, password_hash, status, last_login_at, created_at, updated_at " +
        "FROM users ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
List<User> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

@Select("SELECT COUNT(*) FROM users")
long countAll();

@Update("UPDATE users SET username=#{username}, phone=#{phone}, email=#{email}, status=#{status} WHERE id=#{id}")
int updateUser(@Param("id") Long id, @Param("username") String username,
               @Param("phone") String phone, @Param("email") String email,
               @Param("status") Integer status);

@Select("SELECT id, username, phone, email, password_hash, status, last_login_at, created_at, updated_at " +
        "FROM users WHERE id = #{id}")
User selectById(@Param("id") Long id);
```

注意：`selectById` 已存在，不要重复添加。只需添加 `selectPage`、`countAll`、`updateUser` 三个方法。

- [ ] **Step 2: 在 DestinationMapper 中添加 admin 方法**

在 `DestinationMapper.java` 的 `listHotDestinations` 方法后添加：

```java
@Select("SELECT * FROM destinations ORDER BY popularity_score DESC LIMIT #{offset}, #{pageSize}")
List<Destination> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

@Select("SELECT COUNT(*) FROM destinations")
long countAll();

@Update("UPDATE destinations SET name=#{name}, country=#{country}, city=#{city}, description=#{description}, cover_image_url=#{coverImageUrl} WHERE id=#{id}")
int updateDestination(@Param("id") Long id, @Param("name") String name,
                      @Param("country") String country, @Param("city") String city,
                      @Param("description") String description,
                      @Param("coverImageUrl") String coverImageUrl);

@Delete("DELETE FROM destinations WHERE id = #{id}")
int deleteById(@Param("id") Long id);
```

需要在文件顶部添加 `import org.apache.ibatis.annotations.Delete;` 和 `import org.apache.ibatis.annotations.Update;`。

- [ ] **Step 3: 在 GuideMapper 中添加 admin 方法**

在 `GuideMapper.java` 的 `listTopGuidesByLikes` 方法后添加：

```java
@Select("SELECT g.id, g.destination_id, g.author_id, g.title, g.summary, g.cover_image_url, " +
        "g.location_text, g.scope, g.travel_mode, g.published_at, g.days, g.budget_total, " +
        "g.views_count, g.likes_count, g.comments_count, g.favorites_count, " +
        "d.name AS destination_name, u.username AS author_name " +
        "FROM guides g LEFT JOIN destinations d ON g.destination_id = d.id " +
        "LEFT JOIN users u ON g.author_id = u.id " +
        "ORDER BY g.published_at DESC LIMIT #{offset}, #{pageSize}")
List<GuideSummary> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

@Select("SELECT COUNT(*) FROM guides")
long countAll();

@Update("UPDATE guides SET title=#{title}, summary=#{summary} WHERE id=#{id}")
int updateGuide(@Param("id") Long id, @Param("title") String title, @Param("summary") String summary);

@Delete("DELETE FROM guides WHERE id = #{id}")
int deleteById(@Param("id") Long id);
```

需要在文件顶部添加 `import org.apache.ibatis.annotations.Delete;` 和 `import org.apache.ibatis.annotations.Update;`。

- [ ] **Step 4: 在 GuideStoryMapper 中添加 admin 方法**

在 `GuideStoryMapper.java` 的 `countAll` 方法后添加：

```java
@Delete("DELETE FROM traveler_stories WHERE id = #{id}")
int deleteById(@Param("id") Long id);

@Update("UPDATE traveler_stories SET content=#{content} WHERE id=#{id}")
int updateContent(@Param("id") Long id, @Param("content") String content);
```

需要在文件顶部添加 `import org.apache.ibatis.annotations.Delete;` 和 `import org.apache.ibatis.annotations.Update;`。

- [ ] **Step 5: 在 GuideCommentMapper 中添加 admin 方法**

在 `GuideCommentMapper.java` 的 `countByGuideId` 方法后添加：

```java
@Select("SELECT c.id, c.guide_id, c.user_id, c.content, c.parent_comment_id, " +
        "c.likes_count, c.created_at, u.username AS author_name " +
        "FROM guide_comments c LEFT JOIN users u ON c.user_id = u.id " +
        "ORDER BY c.created_at DESC LIMIT #{offset}, #{pageSize}")
List<GuideComment> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

@Select("SELECT COUNT(*) FROM guide_comments")
long countAll();

@Delete("DELETE FROM guide_comments WHERE id = #{id}")
int deleteById(@Param("id") Long id);
```

需要在文件顶部添加 `import org.apache.ibatis.annotations.Delete;`、`import org.apache.ibatis.annotations.Select;`。

- [ ] **Step 6: 验证编译通过**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 7: 提交**

```bash
git add src/main/java/com/travel/mapper/
git commit -m "feat: 扩展Mapper层，添加Admin所需的增删改查方法"
```

---

### Task 3: 创建 AdminAuthService 和 AdminInterceptor

**Files:**
- Create: `src/main/java/com/travel/service/AdminAuthService.java`
- Create: `src/main/java/com/travel/interceptor/AdminInterceptor.java`
- Modify: `src/main/java/com/travel/config/WebMvcConfig.java`

- [ ] **Step 1: 创建 AdminAuthService**

创建 `src/main/java/com/travel/service/AdminAuthService.java`：

```java
package com.travel.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String SESSION_KEY = "adminUser";

    public boolean login(String username, String password, HttpSession session) {
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            session.setAttribute(SESSION_KEY, Map.of("username", username));
            return true;
        }
        return false;
    }

    public void logout(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
    }

    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute(SESSION_KEY) != null;
    }
}
```

- [ ] **Step 2: 创建 AdminInterceptor**

创建 `src/main/java/com/travel/interceptor/AdminInterceptor.java`：

```java
package com.travel.interceptor;

import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

    private final AdminAuthService adminAuthService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!adminAuthService.isLoggedIn(request.getSession())) {
            response.sendRedirect("/admin/login");
            return false;
        }
        return true;
    }
}
```

- [ ] **Step 3: 修改 WebMvcConfig，注册 AdminInterceptor**

将 `WebMvcConfig.java` 修改为：

```java
package com.travel.config;

import com.travel.interceptor.AdminInterceptor;
import com.travel.security.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;
    private final AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/favicon.ico", "/admin/**");

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login", "/admin/css/**", "/admin/js/**", "/admin/images/**");
    }
}
```

- [ ] **Step 4: 验证编译通过**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/travel/service/AdminAuthService.java \
        src/main/java/com/travel/interceptor/AdminInterceptor.java \
        src/main/java/com/travel/config/WebMvcConfig.java
git commit -m "feat: 添加Admin认证服务和登录拦截器"
```

---

### Task 4: 创建 AdminAuthController 和登录页

**Files:**
- Create: `src/main/java/com/travel/controller/admin/AdminAuthController.java`
- Create: `src/main/resources/views/admin/login.jsp`

- [ ] **Step 1: 创建 AdminAuthController**

创建 `src/main/java/com/travel/controller/admin/AdminAuthController.java`：

```java
package com.travel.controller.admin;

import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        if (adminAuthService.login(username, password, session)) {
            return "redirect:/admin";
        }
        model.addAttribute("error", "用户名或密码错误");
        return "admin/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        adminAuthService.logout(session);
        return "redirect:/admin/login";
    }
}
```

- [ ] **Step 2: 创建 login.jsp**

创建 `src/main/resources/views/admin/login.jsp`：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>管理后台 - 登录</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f5f5f5; }
        .login-card { max-width: 400px; margin: 100px auto; }
    </style>
</head>
<body>
<div class="container">
    <div class="login-card">
        <div class="card shadow">
            <div class="card-body p-5">
                <h3 class="text-center mb-4">旅游管理后台</h3>
                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger">${error}</div>
                <% } %>
                <form method="post" action="${pageContext.request.contextPath}/admin/login">
                    <div class="mb-3">
                        <label class="form-label">用户名</label>
                        <input type="text" name="username" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">密码</label>
                        <input type="password" name="password" class="form-control" required>
                    </div>
                    <button type="submit" class="btn btn-primary w-100">登录</button>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
```

- [ ] **Step 3: 验证编译通过**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/travel/controller/admin/AdminAuthController.java \
        src/main/resources/views/admin/login.jsp
git commit -m "feat: 添加Admin登录控制器和登录页面"
```

---

### Task 5: 创建公共 header.jsp 和 footer.jsp

**Files:**
- Create: `src/main/resources/views/admin/common/header.jsp`
- Create: `src/main/resources/views/admin/common/footer.jsp`

- [ ] **Step 1: 创建 header.jsp（侧边栏布局）**

创建 `src/main/resources/views/admin/common/header.jsp`：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>旅游管理后台</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { margin: 0; padding: 0; background-color: #f8f9fa; }
        .sidebar {
            position: fixed; top: 0; left: 0; bottom: 0;
            width: 220px; background-color: #343a40; color: #fff;
            padding-top: 20px; z-index: 100;
        }
        .sidebar h5 { padding: 0 20px 20px; margin: 0; font-size: 18px; border-bottom: 1px solid #495057; }
        .sidebar a {
            display: block; padding: 10px 20px; color: #adb5bd;
            text-decoration: none; font-size: 14px;
        }
        .sidebar a:hover, .sidebar a.active { color: #fff; background-color: #495057; }
        .content { margin-left: 220px; padding: 20px 30px; }
        .page-title { margin-bottom: 20px; padding-bottom: 10px; border-bottom: 1px solid #dee2e6; }
    </style>
</head>
<body>
<div class="sidebar">
    <h5>旅游管理后台</h5>
    <a href="${pageContext.request.contextPath}/admin" class="${empty param.active ? 'active' : ''}">仪表盘</a>
    <a href="${pageContext.request.contextPath}/admin/users" ${param.active == 'users' ? 'class="active"' : ''}>用户管理</a>
    <a href="${pageContext.request.contextPath}/admin/guides" ${param.active == 'guides' ? 'class="active"' : ''}>攻略管理</a>
    <a href="${pageContext.request.contextPath}/admin/stories" ${param.active == 'stories' ? 'class="active"' : ''}>故事管理</a>
    <a href="${pageContext.request.contextPath}/admin/destinations" ${param.active == 'destinations' ? 'class="active"' : ''}>目的地管理</a>
    <a href="${pageContext.request.contextPath}/admin/comments" ${param.active == 'comments' ? 'class="active"' : ''}>评论管理</a>
    <a href="${pageContext.request.contextPath}/admin/logout" style="position:absolute;bottom:20px;left:0;right:0;">退出登录</a>
</div>
<div class="content">
```

- [ ] **Step 2: 创建 footer.jsp**

创建 `src/main/resources/views/admin/common/footer.jsp`：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
```

- [ ] **Step 3: 提交**

```bash
git add src/main/resources/views/admin/common/
git commit -m "feat: 添加Admin公共头部和尾部JSP（侧边栏布局）"
```

---

### Task 6: 创建 AdminDashboardController 和仪表盘页面

**Files:**
- Create: `src/main/java/com/travel/controller/admin/AdminDashboardController.java`
- Create: `src/main/resources/views/admin/dashboard.jsp`

- [ ] **Step 1: 创建 AdminDashboardController**

创建 `src/main/java/com/travel/controller/admin/AdminDashboardController.java`：

```java
package com.travel.controller.admin;

import com.travel.mapper.DestinationMapper;
import com.travel.mapper.GuideCommentMapper;
import com.travel.mapper.GuideMapper;
import com.travel.mapper.GuideStoryMapper;
import com.travel.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserMapper userMapper;
    private final GuideMapper guideMapper;
    private final GuideStoryMapper storyMapper;
    private final DestinationMapper destinationMapper;
    private final GuideCommentMapper commentMapper;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("userCount", userMapper.countAll());
        model.addAttribute("guideCount", guideMapper.countAll());
        model.addAttribute("storyCount", storyMapper.countAll());
        model.addAttribute("destinationCount", destinationMapper.countAll());
        model.addAttribute("commentCount", commentMapper.countAll());
        return "admin/dashboard";
    }
}
```

- [ ] **Step 2: 创建 dashboard.jsp**

创建 `src/main/resources/views/admin/dashboard.jsp`：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="active" value=""/>
</jsp:include>

<h3 class="page-title">仪表盘</h3>
<div class="row">
    <div class="col-md-3 mb-3">
        <div class="card text-bg-primary">
            <div class="card-body text-center">
                <h2>${userCount}</h2>
                <p class="mb-0">用户总数</p>
            </div>
        </div>
    </div>
    <div class="col-md-3 mb-3">
        <div class="card text-bg-success">
            <div class="card-body text-center">
                <h2>${guideCount}</h2>
                <p class="mb-0">攻略总数</p>
            </div>
        </div>
    </div>
    <div class="col-md-3 mb-3">
        <div class="card text-bg-info">
            <div class="card-body text-center">
                <h2>${storyCount}</h2>
                <p class="mb-0">故事总数</p>
            </div>
        </div>
    </div>
    <div class="col-md-3 mb-3">
        <div class="card text-bg-warning">
            <div class="card-body text-center">
                <h2>${destinationCount}</h2>
                <p class="mb-0">目的地总数</p>
            </div>
        </div>
    </div>
</div>
<div class="row">
    <div class="col-md-4">
        <div class="card">
            <div class="card-body text-center">
                <h2>${commentCount}</h2>
                <p class="mb-0">评论总数</p>
            </div>
        </div>
    </div>
</div>

<jsp:include page="common/footer.jsp"/>
```

- [ ] **Step 3: 验证编译通过**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/travel/controller/admin/AdminDashboardController.java \
        src/main/resources/views/admin/dashboard.jsp
git commit -m "feat: 添加Admin仪表盘控制器和页面"
```

---

### Task 7: 创建 AdminUserController + 用户列表/编辑 JSP

**Files:**
- Create: `src/main/java/com/travel/controller/admin/AdminUserController.java`
- Create: `src/main/resources/views/admin/user/list.jsp`
- Create: `src/main/resources/views/admin/user/edit.jsp`

- [ ] **Step 1: 创建 AdminUserController**

创建 `src/main/java/com/travel/controller/admin/AdminUserController.java`：

```java
package com.travel.controller.admin;

import com.travel.common.PageResult;
import com.travel.mapper.UserMapper;
import com.travel.pojo.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;

    private static final int PAGE_SIZE = 10;

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = userMapper.countAll();
        PageResult<User> pageResult = PageResult.of(userMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total);
        model.addAttribute("pageResult", pageResult);
        return "admin/user/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userMapper.selectById(id));
        return "admin/user/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String username,
                         @RequestParam String phone,
                         @RequestParam String email,
                         @RequestParam Integer status,
                         RedirectAttributes ra) {
        userMapper.updateUser(id, username, phone, email, status);
        ra.addFlashAttribute("msg", "修改成功");
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        userMapper.deleteById(id);
        ra.addFlashAttribute("msg", "删除成功");
        return "redirect:/admin/users";
    }
}
```

注意：`UserMapper` 中还需要添加 `deleteById` 方法。在 Task 2 的 UserMapper 部分补充：

```java
@Delete("DELETE FROM users WHERE id = #{id}")
int deleteById(@Param("id") Long id);
```

需要在 `UserMapper.java` 顶部添加 `import org.apache.ibatis.annotations.Delete;`。

- [ ] **Step 2: 创建 user/list.jsp**

创建 `src/main/resources/views/admin/user/list.jsp`：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="users"/>
</jsp:include>

<h3 class="page-title">用户管理</h3>
<c:if test="${not empty msg}">
    <div class="alert alert-success">${msg}</div>
</c:if>
<table class="table table-striped">
    <thead>
        <tr><th>ID</th><th>用户名</th><th>手机号</th><th>邮箱</th><th>状态</th><th>注册时间</th><th>操作</th></tr>
    </thead>
    <tbody>
        <c:forEach var="u" items="${pageResult.list}">
            <tr>
                <td>${u.id}</td>
                <td>${u.username}</td>
                <td>${u.phone}</td>
                <td>${u.email}</td>
                <td>${u.status == 1 ? '正常' : '禁用'}</td>
                <td>${u.createdAt}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/users/edit/${u.id}" class="btn btn-sm btn-primary">编辑</a>
                    <form method="post" action="${pageContext.request.contextPath}/admin/users/delete/${u.id}" style="display:inline" onsubmit="return confirm('确认删除？')">
                        <button type="submit" class="btn btn-sm btn-danger">删除</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>
<nav>
    <ul class="pagination">
        <c:forEach begin="1" end="${pageResult.totalPages}" var="i">
            <li class="page-item ${i == pageResult.page ? 'active' : ''}">
                <a class="page-link" href="${pageContext.request.contextPath}/admin/users?page=${i}">${i}</a>
            </li>
        </c:forEach>
    </ul>
</nav>

<jsp:include page="../common/footer.jsp"/>
```

- [ ] **Step 3: 创建 user/edit.jsp**

创建 `src/main/resources/views/admin/user/edit.jsp`：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="users"/>
</jsp:include>

<h3 class="page-title">编辑用户</h3>
<a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary mb-3">返回列表</a>
<form method="post" action="${pageContext.request.contextPath}/admin/users/edit/${user.id}">
    <div class="mb-3">
        <label class="form-label">用户名</label>
        <input type="text" name="username" class="form-control" value="${user.username}" required>
    </div>
    <div class="mb-3">
        <label class="form-label">手机号</label>
        <input type="text" name="phone" class="form-control" value="${user.phone}">
    </div>
    <div class="mb-3">
        <label class="form-label">邮箱</label>
        <input type="email" name="email" class="form-control" value="${user.email}">
    </div>
    <div class="mb-3">
        <label class="form-label">状态</label>
        <select name="status" class="form-select">
            <option value="1" ${user.status == 1 ? 'selected' : ''}>正常</option>
            <option value="0" ${user.status == 0 ? 'selected' : ''}>禁用</option>
        </select>
    </div>
    <button type="submit" class="btn btn-primary">保存</button>
</form>

<jsp:include page="../common/footer.jsp"/>
```

- [ ] **Step 4: 验证编译通过**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/travel/controller/admin/AdminUserController.java \
        src/main/resources/views/admin/user/
git commit -m "feat: 添加Admin用户管理控制器和页面"
```

---

### Task 8: 创建 AdminGuideController + 攻略列表/编辑 JSP

**Files:**
- Create: `src/main/java/com/travel/controller/admin/AdminGuideController.java`
- Create: `src/main/resources/views/admin/guide/list.jsp`
- Create: `src/main/resources/views/admin/guide/edit.jsp`

- [ ] **Step 1: 创建 AdminGuideController**

创建 `src/main/java/com/travel/controller/admin/AdminGuideController.java`：

```java
package com.travel.controller.admin;

import com.travel.common.PageResult;
import com.travel.mapper.GuideMapper;
import com.travel.pojo.model.GuideSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/guides")
@RequiredArgsConstructor
public class AdminGuideController {

    private final GuideMapper guideMapper;

    private static final int PAGE_SIZE = 10;

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = guideMapper.countAll();
        PageResult<GuideSummary> pageResult = PageResult.of(guideMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total);
        model.addAttribute("pageResult", pageResult);
        return "admin/guide/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("guide", guideMapper.selectPage(0, 1000).stream()
                .filter(g -> g.getId().equals(id)).findFirst().orElse(null));
        return "admin/guide/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam String summary,
                         RedirectAttributes ra) {
        guideMapper.updateGuide(id, title, summary);
        ra.addFlashAttribute("msg", "修改成功");
        return "redirect:/admin/guides";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        guideMapper.deleteById(id);
        ra.addFlashAttribute("msg", "删除成功");
        return "redirect:/admin/guides";
    }
}
```

注意：`editForm` 中使用 `selectPage` 获取攻略详情是一种简化方式。更好的做法是在 GuideMapper 中添加 `selectById` 方法。请在 GuideMapper 中添加：

```java
@Select("SELECT g.id, g.destination_id, g.author_id, g.title, g.summary, g.cover_image_url, " +
        "g.location_text, g.scope, g.travel_mode, g.published_at, g.days, g.budget_total, " +
        "g.views_count, g.likes_count, g.comments_count, g.favorites_count, " +
        "d.name AS destination_name, u.username AS author_name " +
        "FROM guides g LEFT JOIN destinations d ON g.destination_id = d.id " +
        "LEFT JOIN users u ON g.author_id = u.id WHERE g.id = #{id}")
GuideSummary selectById(@Param("id") Long id);
```

然后将 `editForm` 方法改为：

```java
@GetMapping("/edit/{id}")
public String editForm(@PathVariable Long id, Model model) {
    model.addAttribute("guide", guideMapper.selectById(id));
    return "admin/guide/edit";
}
```

- [ ] **Step 2: 创建 guide/list.jsp**

创建 `src/main/resources/views/admin/guide/list.jsp`：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="guides"/>
</jsp:include>

<h3 class="page-title">攻略管理</h3>
<c:if test="${not empty msg}">
    <div class="alert alert-success">${msg}</div>
</c:if>
<table class="table table-striped">
    <thead>
        <tr><th>ID</th><th>标题</th><th>作者</th><th>目的地</th><th>点赞数</th><th>发布时间</th><th>操作</th></tr>
    </thead>
    <tbody>
        <c:forEach var="g" items="${pageResult.list}">
            <tr>
                <td>${g.id}</td>
                <td>${g.title}</td>
                <td>${g.authorName}</td>
                <td>${g.destinationName}</td>
                <td>${g.likesCount}</td>
                <td>${g.publishedAt}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/guides/edit/${g.id}" class="btn btn-sm btn-primary">编辑</a>
                    <form method="post" action="${pageContext.request.contextPath}/admin/guides/delete/${g.id}" style="display:inline" onsubmit="return confirm('确认删除？')">
                        <button type="submit" class="btn btn-sm btn-danger">删除</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>
<nav>
    <ul class="pagination">
        <c:forEach begin="1" end="${pageResult.totalPages}" var="i">
            <li class="page-item ${i == pageResult.page ? 'active' : ''}">
                <a class="page-link" href="${pageContext.request.contextPath}/admin/guides?page=${i}">${i}</a>
            </li>
        </c:forEach>
    </ul>
</nav>

<jsp:include page="../common/footer.jsp"/>
```

- [ ] **Step 3: 创建 guide/edit.jsp**

创建 `src/main/resources/views/admin/guide/edit.jsp`：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="guides"/>
</jsp:include>

<h3 class="page-title">编辑攻略</h3>
<a href="${pageContext.request.contextPath}/admin/guides" class="btn btn-secondary mb-3">返回列表</a>
<form method="post" action="${pageContext.request.contextPath}/admin/guides/edit/${guide.id}">
    <div class="mb-3">
        <label class="form-label">标题</label>
        <input type="text" name="title" class="form-control" value="${guide.title}" required>
    </div>
    <div class="mb-3">
        <label class="form-label">摘要</label>
        <textarea name="summary" class="form-control" rows="5">${guide.summary}</textarea>
    </div>
    <button type="submit" class="btn btn-primary">保存</button>
</form>

<jsp:include page="../common/footer.jsp"/>
```

- [ ] **Step 4: 验证编译通过**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/travel/controller/admin/AdminGuideController.java \
        src/main/resources/views/admin/guide/
git commit -m "feat: 添加Admin攻略管理控制器和页面"
```

---

### Task 9: 创建 AdminStoryController + 故事列表/编辑 JSP

**Files:**
- Create: `src/main/java/com/travel/controller/admin/AdminStoryController.java`
- Create: `src/main/resources/views/admin/story/list.jsp`
- Create: `src/main/resources/views/admin/story/edit.jsp`

- [ ] **Step 1: 创建 AdminStoryController**

创建 `src/main/java/com/travel/controller/admin/AdminStoryController.java`：

```java
package com.travel.controller.admin;

import com.travel.common.PageResult;
import com.travel.mapper.GuideStoryMapper;
import com.travel.pojo.vo.GuideStoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/stories")
@RequiredArgsConstructor
public class AdminStoryController {

    private final GuideStoryMapper storyMapper;

    private static final int PAGE_SIZE = 10;

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = storyMapper.countAll();
        PageResult<GuideStoryVO> pageResult = PageResult.of(storyMapper.selectAll(offset, PAGE_SIZE), page, PAGE_SIZE, total);
        model.addAttribute("pageResult", pageResult);
        return "admin/story/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("story", storyMapper.selectById(id));
        return "admin/story/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String content,
                         RedirectAttributes ra) {
        storyMapper.updateContent(id, content);
        ra.addFlashAttribute("msg", "修改成功");
        return "redirect:/admin/stories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        storyMapper.deleteById(id);
        ra.addFlashAttribute("msg", "删除成功");
        return "redirect:/admin/stories";
    }
}
```

- [ ] **Step 2: 创建 story/list.jsp**

创建 `src/main/resources/views/admin/story/list.jsp`：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="stories"/>
</jsp:include>

<h3 class="page-title">故事管理</h3>
<c:if test="${not empty msg}">
    <div class="alert alert-success">${msg}</div>
</c:if>
<table class="table table-striped">
    <thead>
        <tr><th>ID</th><th>作者</th><th>内容摘要</th><th>点赞数</th><th>发布时间</th><th>操作</th></tr>
    </thead>
    <tbody>
        <c:forEach var="s" items="${pageResult.list}">
            <tr>
                <td>${s.id}</td>
                <td>${s.authorName}</td>
                <td>${s.content.length() > 50 ? s.content.substring(0, 50).concat('...') : s.content}</td>
                <td>${s.likesCount}</td>
                <td>${s.publishedAt}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/stories/edit/${s.id}" class="btn btn-sm btn-primary">编辑</a>
                    <form method="post" action="${pageContext.request.contextPath}/admin/stories/delete/${s.id}" style="display:inline" onsubmit="return confirm('确认删除？')">
                        <button type="submit" class="btn btn-sm btn-danger">删除</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>
<nav>
    <ul class="pagination">
        <c:forEach begin="1" end="${pageResult.totalPages}" var="i">
            <li class="page-item ${i == pageResult.page ? 'active' : ''}">
                <a class="page-link" href="${pageContext.request.contextPath}/admin/stories?page=${i}">${i}</a>
            </li>
        </c:forEach>
    </ul>
</nav>

<jsp:include page="../common/footer.jsp"/>
```

- [ ] **Step 3: 创建 story/edit.jsp**

创建 `src/main/resources/views/admin/story/edit.jsp`：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="stories"/>
</jsp:include>

<h3 class="page-title">编辑故事</h3>
<a href="${pageContext.request.contextPath}/admin/stories" class="btn btn-secondary mb-3">返回列表</a>
<form method="post" action="${pageContext.request.contextPath}/admin/stories/edit/${story.id}">
    <div class="mb-3">
        <label class="form-label">作者</label>
        <input type="text" class="form-control" value="${story.authorName}" disabled>
    </div>
    <div class="mb-3">
        <label class="form-label">内容</label>
        <textarea name="content" class="form-control" rows="10">${story.content}</textarea>
    </div>
    <button type="submit" class="btn btn-primary">保存</button>
</form>

<jsp:include page="../common/footer.jsp"/>
```

- [ ] **Step 4: 验证编译通过**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/travel/controller/admin/AdminStoryController.java \
        src/main/resources/views/admin/story/
git commit -m "feat: 添加Admin故事管理控制器和页面"
```

---

### Task 10: 创建 AdminDestinationController + 目的地列表/编辑 JSP

**Files:**
- Create: `src/main/java/com/travel/controller/admin/AdminDestinationController.java`
- Create: `src/main/resources/views/admin/destination/list.jsp`
- Create: `src/main/resources/views/admin/destination/edit.jsp`

- [ ] **Step 1: 创建 AdminDestinationController**

创建 `src/main/java/com/travel/controller/admin/AdminDestinationController.java`：

```java
package com.travel.controller.admin;

import com.travel.common.PageResult;
import com.travel.mapper.DestinationMapper;
import com.travel.pojo.model.Destination;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/destinations")
@RequiredArgsConstructor
public class AdminDestinationController {

    private final DestinationMapper destinationMapper;

    private static final int PAGE_SIZE = 10;

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = destinationMapper.countAll();
        PageResult<Destination> pageResult = PageResult.of(destinationMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total);
        model.addAttribute("pageResult", pageResult);
        return "admin/destination/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("destination", destinationMapper.selectById(id));
        return "admin/destination/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam String country,
                         @RequestParam String city,
                         @RequestParam String description,
                         @RequestParam String coverImageUrl,
                         RedirectAttributes ra) {
        destinationMapper.updateDestination(id, name, country, city, description, coverImageUrl);
        ra.addFlashAttribute("msg", "修改成功");
        return "redirect:/admin/destinations";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        destinationMapper.deleteById(id);
        ra.addFlashAttribute("msg", "删除成功");
        return "redirect:/admin/destinations";
    }
}
```

- [ ] **Step 2: 创建 destination/list.jsp**

创建 `src/main/resources/views/admin/destination/list.jsp`：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="destinations"/>
</jsp:include>

<h3 class="page-title">目的地管理</h3>
<c:if test="${not empty msg}">
    <div class="alert alert-success">${msg}</div>
</c:if>
<table class="table table-striped">
    <thead>
        <tr><th>ID</th><th>名称</th><th>国家</th><th>城市</th><th>攻略数</th><th>操作</th></tr>
    </thead>
    <tbody>
        <c:forEach var="d" items="${pageResult.list}">
            <tr>
                <td>${d.id}</td>
                <td>${d.name}</td>
                <td>${d.country}</td>
                <td>${d.city}</td>
                <td>${d.guidesCount}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/destinations/edit/${d.id}" class="btn btn-sm btn-primary">编辑</a>
                    <form method="post" action="${pageContext.request.contextPath}/admin/destinations/delete/${d.id}" style="display:inline" onsubmit="return confirm('确认删除？')">
                        <button type="submit" class="btn btn-sm btn-danger">删除</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>
<nav>
    <ul class="pagination">
        <c:forEach begin="1" end="${pageResult.totalPages}" var="i">
            <li class="page-item ${i == pageResult.page ? 'active' : ''}">
                <a class="page-link" href="${pageContext.request.contextPath}/admin/destinations?page=${i}">${i}</a>
            </li>
        </c:forEach>
    </ul>
</nav>

<jsp:include page="../common/footer.jsp"/>
```

- [ ] **Step 3: 创建 destination/edit.jsp**

创建 `src/main/resources/views/admin/destination/edit.jsp`：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="destinations"/>
</jsp:include>

<h3 class="page-title">编辑目的地</h3>
<a href="${pageContext.request.contextPath}/admin/destinations" class="btn btn-secondary mb-3">返回列表</a>
<form method="post" action="${pageContext.request.contextPath}/admin/destinations/edit/${destination.id}">
    <div class="mb-3">
        <label class="form-label">名称</label>
        <input type="text" name="name" class="form-control" value="${destination.name}" required>
    </div>
    <div class="mb-3">
        <label class="form-label">国家</label>
        <input type="text" name="country" class="form-control" value="${destination.country}">
    </div>
    <div class="mb-3">
        <label class="form-label">城市</label>
        <input type="text" name="city" class="form-control" value="${destination.city}">
    </div>
    <div class="mb-3">
        <label class="form-label">描述</label>
        <textarea name="description" class="form-control" rows="5">${destination.description}</textarea>
    </div>
    <div class="mb-3">
        <label class="form-label">封面图片URL</label>
        <input type="text" name="coverImageUrl" class="form-control" value="${destination.coverImageUrl}">
    </div>
    <button type="submit" class="btn btn-primary">保存</button>
</form>

<jsp:include page="../common/footer.jsp"/>
```

- [ ] **Step 4: 验证编译通过**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/travel/controller/admin/AdminDestinationController.java \
        src/main/resources/views/admin/destination/
git commit -m "feat: 添加Admin目的地管理控制器和页面"
```

---

### Task 11: 创建 AdminCommentController + 评论列表 JSP

**Files:**
- Create: `src/main/java/com/travel/controller/admin/AdminCommentController.java`
- Create: `src/main/resources/views/admin/comment/list.jsp`

- [ ] **Step 1: 创建 AdminCommentController**

创建 `src/main/java/com/travel/controller/admin/AdminCommentController.java`：

```java
package com.travel.controller.admin;

import com.travel.common.PageResult;
import com.travel.mapper.GuideCommentMapper;
import com.travel.pojo.model.GuideComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final GuideCommentMapper commentMapper;

    private static final int PAGE_SIZE = 10;

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = commentMapper.countAll();
        PageResult<GuideComment> pageResult = PageResult.of(commentMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total);
        model.addAttribute("pageResult", pageResult);
        return "admin/comment/list";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        commentMapper.deleteById(id);
        ra.addFlashAttribute("msg", "删除成功");
        return "redirect:/admin/comments";
    }
}
```

- [ ] **Step 2: 创建 comment/list.jsp**

创建 `src/main/resources/views/admin/comment/list.jsp`：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="comments"/>
</jsp:include>

<h3 class="page-title">评论管理</h3>
<c:if test="${not empty msg}">
    <div class="alert alert-success">${msg}</div>
</c:if>
<table class="table table-striped">
    <thead>
        <tr><th>ID</th><th>攻略ID</th><th>评论者</th><th>内容</th><th>时间</th><th>操作</th></tr>
    </thead>
    <tbody>
        <c:forEach var="c" items="${pageResult.list}">
            <tr>
                <td>${c.id}</td>
                <td>${c.guideId}</td>
                <td>${c.authorName}</td>
                <td>${c.content.length() > 50 ? c.content.substring(0, 50).concat('...') : c.content}</td>
                <td>${c.createdAt}</td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/admin/comments/delete/${c.id}" style="display:inline" onsubmit="return confirm('确认删除？')">
                        <button type="submit" class="btn btn-sm btn-danger">删除</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>
<nav>
    <ul class="pagination">
        <c:forEach begin="1" end="${pageResult.totalPages}" var="i">
            <li class="page-item ${i == pageResult.page ? 'active' : ''}">
                <a class="page-link" href="${pageContext.request.contextPath}/admin/comments?page=${i}">${i}</a>
            </li>
        </c:forEach>
    </ul>
</nav>

<jsp:include page="../common/footer.jsp"/>
```

- [ ] **Step 3: 验证编译通过**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/travel/controller/admin/AdminCommentController.java \
        src/main/resources/views/admin/comment/
git commit -m "feat: 添加Admin评论管理控制器和页面"
```

---

### Task 12: 最终验证

- [ ] **Step 1: 完整编译验证**

Run: `mvn clean compile`
Expected: BUILD SUCCESS

- [ ] **Step 2: 检查所有文件是否就位**

确认以下文件全部存在：
- 7 个 Controller 在 `controller/admin/`
- 1 个 Service 在 `service/`
- 1 个 Interceptor 在 `interceptor/`
- 15 个 JSP 文件在 `views/admin/`

- [ ] **Step 3: 启动应用验证**

Run: `mvn spring-boot:run`
Expected: 应用在 8082 端口启动，访问 `http://localhost:8082/admin` 应重定向到登录页

- [ ] **Step 4: 最终提交**

```bash
git add -A
git commit -m "feat: 完成Admin JSP管理端全部功能"
```
