# 管理员审核管理实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将管理员对用户、攻略、故事、评论的管理从"编辑内容"改为"审核（通过/拒绝/下架）+ 删除"

**Architecture:** 后端新增审核接口 `POST /admin/{entity}/{id}/audit`，移除编辑接口，列表接口支持 `?status=` 筛选。前端替换编辑按钮为审核操作按钮，新增状态筛选标签。

**Tech Stack:** Spring Boot, MyBatis (annotations + XML), Vanilla JS SPA

---

## 文件结构

### 后端文件

| 文件 | 操作 | 职责 |
|------|------|------|
| `src/main/java/com/travel/pojo/model/User.java` | 修改 | 更新 status 注释 |
| `src/main/java/com/travel/pojo/model/GuideSummary.java` | 修改 | 新增 status 字段 |
| `src/main/java/com/travel/pojo/model/GuideDetail.java` | 修改 | 新增 status 字段 |
| `src/main/java/com/travel/pojo/vo/GuideStoryVO.java` | 修改 | 新增 status 字段 |
| `src/main/java/com/travel/pojo/model/GuideComment.java` | 修改 | 新增 status 字段 |
| `src/main/java/com/travel/mapper/UserMapper.java` | 修改 | 新增 updateStatus, 修改 selectPage/countAll |
| `src/main/java/com/travel/mapper/GuideMapper.java` | 修改 | 新增 updateStatus, 修改 selectPage/countAll |
| `src/main/java/com/travel/mapper/GuideMapper.xml` | 修改 | SELECT 添加 status, WHERE 添加 status |
| `src/main/java/com/travel/mapper/GuideStoryMapper.java` | 修改 | 新增 updateStatus, 修改 selectAll/countAll |
| `src/main/java/com/travel/mapper/GuideStoryMapper.xml` | 修改 | SELECT 添加 status, WHERE 添加 status |
| `src/main/java/com/travel/mapper/GuideCommentMapper.java` | 修改 | 新增 updateStatus, 修改 selectPage/countAll |
| `src/main/java/com/travel/mapper/GuideCommentMapper.xml` | 修改 | SELECT 添加 status, WHERE 添加 status |
| `src/main/java/com/travel/controller/admin/AdminUserApiController.java` | 修改 | 移除 PUT, 新增 audit |
| `src/main/java/com/travel/controller/admin/AdminGuideApiController.java` | 修改 | 移除 PUT, 新增 audit |
| `src/main/java/com/travel/controller/admin/AdminStoryApiController.java` | 修改 | 移除 PUT, 新增 audit |
| `src/main/java/com/travel/controller/admin/AdminCommentApiController.java` | 修改 | 新增 audit |

### 前端文件

| 文件 | 操作 | 职责 |
|------|------|------|
| `src/main/resources/static/admin/js/api.js` | 修改 | 新增 post 方法（已有） |
| `src/main/resources/static/admin/js/pages/users.js` | 修改 | 替换编辑为审核操作 |
| `src/main/resources/static/admin/js/pages/guides.js` | 修改 | 替换编辑为审核操作 |
| `src/main/resources/static/admin/js/pages/stories.js` | 修改 | 替换编辑为审核操作 |
| `src/main/resources/static/admin/js/pages/comments.js` | 修改 | 新增审核操作 |
| `src/main/resources/static/admin/index.html` | 修改 | 移除编辑路由 |
| `src/main/resources/static/admin/css/components.css` | 修改 | 新增状态徽章样式 |

### 数据库

| 文件 | 操作 | 职责 |
|------|------|------|
| `src/main/resources/static/SqlData/admin_audit_migration.sql` | 新增 | SQL 迁移脚本 |

---

## Task 1: 数据库迁移脚本

**Files:**
- Create: `src/main/resources/static/SqlData/admin_audit_migration.sql`

- [ ] **Step 1: 创建 SQL 迁移脚本**

```sql
-- 管理员审核管理：添加 status 字段
-- 执行前请备份数据库

-- 用户表：修改 status 字段注释（值 0=待审核, 1=正常, 2=禁用）
ALTER TABLE users MODIFY COLUMN status INT DEFAULT 0 COMMENT '0=待审核, 1=正常, 2=禁用';

-- 攻略表：新增 status 字段
ALTER TABLE guides ADD COLUMN status INT DEFAULT 0 COMMENT '0=待审核, 1=已通过, 2=已拒绝, 3=已下架' AFTER favorites_count;

-- 故事表：新增 status 字段
ALTER TABLE traveler_stories ADD COLUMN status INT DEFAULT 0 COMMENT '0=待审核, 1=已通过, 2=已拒绝, 3=已下架' AFTER shares_count;

-- 评论表：新增 status 字段
ALTER TABLE guide_comments ADD COLUMN status INT DEFAULT 0 COMMENT '0=待审核, 1=已通过, 2=已拒绝, 3=已下架' AFTER likes_count;
```

- [ ] **Step 2: 验证 SQL 语法**

检查 SQL 语法正确，确认表名和字段名与现有数据库一致。

- [ ] **Step 3: 提交**

```bash
git add src/main/resources/static/SqlData/admin_audit_migration.sql
git commit -m "feat(db): add status column migration for audit management"
```

---

## Task 2: 实体模型添加 status 字段

**Files:**
- Modify: `src/main/java/com/travel/pojo/model/GuideSummary.java:9-29`
- Modify: `src/main/java/com/travel/pojo/model/GuideDetail.java:9-30`
- Modify: `src/main/java/com/travel/pojo/vo/GuideStoryVO.java:13-25`
- Modify: `src/main/java/com/travel/pojo/model/GuideComment.java:8-21`

- [ ] **Step 1: GuideSummary.java 添加 status 字段**

在 `GuideSummary.java` 第 28 行（`private String authorAvatarUrl;` 之后）添加：

```java
    private Integer status;
```

完整文件变为：

```java
package com.travel.pojo.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GuideSummary {
    private Long id;
    private Long destinationId;
    private Long authorId;
    private String title;
    private String summary;
    private String coverImageUrl;
    private String locationText;
    private String scope;
    private String travelMode;
    private LocalDateTime publishedAt;
    private Integer days;
    private BigDecimal budgetTotal;
    private Integer viewsCount;
    private Integer likesCount;
    private Integer commentsCount;
    private Integer favoritesCount;
    private String destinationName;
    private String authorName;
    private String authorAvatarUrl;
    private Integer status;
}
```

- [ ] **Step 2: GuideDetail.java 添加 status 字段**

在 `GuideDetail.java` 第 29 行（`private String authorAvatarUrl;` 之后）添加：

```java
    private Integer status;
```

- [ ] **Step 3: GuideStoryVO.java 添加 status 字段**

在 `GuideStoryVO.java` 第 24 行（`private LocalDateTime createdAt;` 之后）添加：

```java
    private Integer status;
```

- [ ] **Step 4: GuideComment.java 添加 status 字段**

在 `GuideComment.java` 第 20 行（`private String authorAvatarUrl;` 之前）添加：

```java
    private Integer status;
```

完整文件变为：

```java
package com.travel.pojo.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GuideComment {
    private Long id;
    private Long guideId;
    private Long userId;
    private String content;
    private Long parentCommentId;
    private Integer likesCount;
    private Integer status;
    private LocalDateTime createdAt;

    /** 评论者昵称（关联查询） */
    private String authorName;
    /** 评论者头像（关联查询） */
    private String authorAvatarUrl;
}
```

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/travel/pojo/model/GuideSummary.java src/main/java/com/travel/pojo/model/GuideDetail.java src/main/java/com/travel/pojo/vo/GuideStoryVO.java src/main/java/com/travel/pojo/model/GuideComment.java
git commit -m "feat(model): add status field to Guide, Story, Comment entities"
```

---

## Task 3: Mapper 接口添加 updateStatus 方法

**Files:**
- Modify: `src/main/java/com/travel/mapper/UserMapper.java:54-57`
- Modify: `src/main/java/com/travel/mapper/GuideMapper.java:124-128`
- Modify: `src/main/java/com/travel/mapper/GuideStoryMapper.java:62-66`
- Modify: `src/main/java/com/travel/mapper/GuideCommentMapper.java:34-38`

- [ ] **Step 1: UserMapper.java 添加 updateStatus 方法**

在 `UserMapper.java` 第 57 行（`updateUser` 方法之后）添加：

```java
    @Update("UPDATE users SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
```

- [ ] **Step 2: GuideMapper.java 添加 updateStatus 方法**

在 `GuideMapper.java` 第 128 行（`deleteById` 方法之后）添加：

```java
    @Update("UPDATE guides SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
```

- [ ] **Step 3: GuideStoryMapper.java 添加 updateStatus 方法**

在 `GuideStoryMapper.java` 第 66 行（`updateContent` 方法之后）添加：

```java
    @Update("UPDATE traveler_stories SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
```

- [ ] **Step 4: GuideCommentMapper.java 添加 updateStatus 方法**

在 `GuideCommentMapper.java` 第 38 行（`deleteById` 方法之后）添加：

```java
    @Update("UPDATE guide_comments SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
```

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/travel/mapper/UserMapper.java src/main/java/com/travel/mapper/GuideMapper.java src/main/java/com/travel/mapper/GuideStoryMapper.java src/main/java/com/travel/mapper/GuideCommentMapper.java
git commit -m "feat(mapper): add updateStatus method to all entity mappers"
```

---

## Task 4: Mapper 查询添加 status 筛选

**Files:**
- Modify: `src/main/java/com/travel/mapper/UserMapper.java:47-52`
- Modify: `src/main/java/com/travel/mapper/GuideMapper.java:104-114`
- Modify: `src/main/java/com/travel/mapper/GuideMapper.xml:50-56,64-79,81-100`
- Modify: `src/main/java/com/travel/mapper/GuideStoryMapper.java:44-60`
- Modify: `src/main/java/com/travel/mapper/GuideStoryMapper.xml:17-57`
- Modify: `src/main/java/com/travel/mapper/GuideCommentMapper.java:28-35`
- Modify: `src/main/java/com/travel/mapper/GuideCommentMapper.xml:5-22`

- [ ] **Step 1: UserMapper.java 修改 selectPage 和 countAll**

将 `UserMapper.java` 第 47-52 行的 `selectPage` 和 `countAll` 方法替换为：

```java
    @Select("SELECT id, username, phone, email, password_hash, status, last_login_at, created_at, updated_at " +
            "FROM users " +
            "<where>" +
            "<if test='status != null'>AND status = #{status}</if>" +
            "</where>" +
            "ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<User> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM users " +
            "<where>" +
            "<if test='status != null'>AND status = #{status}</if>" +
            "</where>")
    long countAll(@Param("status") Integer status);
```

注意：需要在文件顶部添加 `import org.apache.ibatis.annotations.Param;`（已有）。

- [ ] **Step 2: GuideMapper.java 修改 selectPage 和 countAll**

将 `GuideMapper.java` 第 104-114 行的 `selectPage` 和 `countAll` 方法替换为：

```java
    @Select("SELECT g.id, g.destination_id, g.author_id, g.title, g.summary, g.cover_image_url, " +
            "g.location_text, g.scope, g.travel_mode, g.published_at, g.days, g.budget_total, " +
            "g.views_count, g.likes_count, g.comments_count, g.favorites_count, g.status, " +
            "d.name AS destination_name, u.username AS author_name " +
            "FROM guides g LEFT JOIN destinations d ON g.destination_id = d.id " +
            "LEFT JOIN users u ON g.author_id = u.id " +
            "<where>" +
            "<if test='status != null'>AND g.status = #{status}</if>" +
            "</where>" +
            "ORDER BY g.published_at DESC LIMIT #{offset}, #{pageSize}")
    List<GuideSummary> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM guides g " +
            "<where>" +
            "<if test='status != null'>AND g.status = #{status}</if>" +
            "</where>")
    long countAll(@Param("status") Integer status);
```

- [ ] **Step 3: GuideMapper.xml 修改 GuideSummaryResultMap 添加 status**

在 `GuideMapper.xml` 第 24 行（`<result column="author_avatar_url" property="authorAvatarUrl"/>` 之后）添加：

```xml
        <result column="status" property="status"/>
```

- [ ] **Step 4: GuideMapper.xml 修改 GuideDetailResultMap 添加 status**

在 `GuideMapper.xml` 第 47 行（`<result column="author_avatar_url" property="authorAvatarUrl"/>` 之后）添加：

```xml
        <result column="status" property="status"/>
```

- [ ] **Step 5: GuideMapper.xml 修改 GuideSummaryColumns 添加 g.status**

将 `GuideMapper.xml` 第 50-56 行的 `GuideSummaryColumns` 修改为：

```xml
    <sql id="GuideSummaryColumns">
        SELECT g.id, g.destination_id, g.author_id, g.title, g.summary, g.cover_image_url, g.location_text, g.scope, g.travel_mode,
        g.published_at, g.days, g.budget_total, g.views_count, g.likes_count, g.comments_count, g.favorites_count, g.status,
        d.name AS destination_name,
        up.nickname AS author_name,
        up.avatar_url AS author_avatar_url
    </sql>
```

- [ ] **Step 6: GuideMapper.xml 修改 GuideSummaryWhere 添加 status 筛选**

将 `GuideMapper.xml` 第 64-79 行的 `GuideSummaryWhere` 修改为：

```xml
    <sql id="GuideSummaryWhere">
        <where>
            g.publish_status = 'published'
            <if test="status != null">
                AND g.status = #{status}
            </if>
            <if test="keyword != null and keyword != ''">
                AND (g.title LIKE CONCAT('%', #{keyword}, '%')
                     OR g.summary LIKE CONCAT('%', #{keyword}, '%')
                     OR g.content_html LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="scope != null and scope != ''">
                AND g.scope = #{scope}
            </if>
            <if test="travelMode != null and travelMode != ''">
                AND g.travel_mode = #{travelMode}
            </if>
        </where>
    </sql>
```

- [ ] **Step 7: GuideStoryMapper.java 修改 selectAll 和 countAll**

将 `GuideStoryMapper.java` 第 44-60 行的 `selectAll` 和 `countAll` 方法替换为：

```java
    List<GuideStoryVO> selectAll(@Param("offset") int offset,
                                  @Param("pageSize") int pageSize,
                                  @Param("status") Integer status);

    long countAll(@Param("status") Integer status);
```

- [ ] **Step 8: GuideStoryMapper.xml 修改 GuideStoryResultMap 添加 status**

在 `GuideStoryMapper.xml` 第 16 行（`<result column="created_at" property="createdAt"/>` 之后）添加：

```xml
        <result column="status" property="status"/>
```

- [ ] **Step 9: GuideStoryMapper.xml 修改 selectById 添加 status**

将 `GuideStoryMapper.xml` 第 19-25 行的 `selectById` 修改为：

```xml
    <select id="selectById" resultMap="GuideStoryResultMap">
        SELECT id, author_user_id, author_name, author_avatar_url, is_vip, content,
               published_at, likes_count, comments_count, shares_count, status, created_at
        FROM traveler_stories
        WHERE id = #{id}
          AND is_deleted = 0
    </select>
```

- [ ] **Step 10: GuideStoryMapper.xml 修改 selectAll 添加 status 和筛选**

将 `GuideStoryMapper.xml` 第 44-51 行的 `selectAll` 修改为：

```xml
    <select id="selectAll" resultMap="GuideStoryResultMap">
        SELECT id, author_user_id, author_name, author_avatar_url, is_vip, content,
               published_at, likes_count, comments_count, shares_count, status, created_at
        FROM traveler_stories
        WHERE is_deleted = 0
        <if test="status != null">
            AND status = #{status}
        </if>
        ORDER BY published_at DESC
        LIMIT #{offset}, #{pageSize}
    </select>
```

- [ ] **Step 11: GuideStoryMapper.xml 修改 countAll 添加 status 筛选**

将 `GuideStoryMapper.xml` 第 53-57 行的 `countAll` 修改为：

```xml
    <select id="countAll" resultType="long">
        SELECT COUNT(1)
        FROM traveler_stories
        WHERE is_deleted = 0
        <if test="status != null">
            AND status = #{status}
        </if>
    </select>
```

- [ ] **Step 12: GuideCommentMapper.java 修改 selectPage 和 countAll**

将 `GuideCommentMapper.java` 第 28-35 行的 `selectPage` 和 `countAll` 方法替换为：

```java
    @Select("SELECT c.id, c.guide_id, c.user_id, c.content, c.parent_comment_id, " +
            "c.likes_count, c.status, c.created_at, u.username AS author_name " +
            "FROM guide_comments c LEFT JOIN users u ON c.user_id = u.id " +
            "<where>" +
            "<if test='status != null'>AND c.status = #{status}</if>" +
            "</where>" +
            "ORDER BY c.created_at DESC LIMIT #{offset}, #{pageSize}")
    List<GuideComment> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM guide_comments c " +
            "<where>" +
            "<if test='status != null'>AND c.status = #{status}</if>" +
            "</where>")
    long countAll(@Param("status") Integer status);
```

- [ ] **Step 13: GuideCommentMapper.xml 修改 selectPage 添加 status**

将 `GuideCommentMapper.xml` 第 5-14 行的 `listByGuideId` 修改为：

```xml
    <select id="listByGuideId" resultType="com.travel.pojo.model.GuideComment">
        SELECT gc.id, gc.guide_id, gc.user_id, gc.content, gc.parent_comment_id,
               gc.likes_count, gc.status, gc.created_at,
               up.nickname AS author_name,
               up.avatar_url AS author_avatar_url
        FROM guide_comments gc
        LEFT JOIN user_profiles up ON up.user_id = gc.user_id
        WHERE gc.guide_id = #{guideId}
        ORDER BY gc.created_at ASC
    </select>
```

- [ ] **Step 14: 提交**

```bash
git add src/main/java/com/travel/mapper/UserMapper.java src/main/java/com/travel/mapper/GuideMapper.java src/main/java/com/travel/mapper/GuideMapper.xml src/main/java/com/travel/mapper/GuideStoryMapper.java src/main/java/com/travel/mapper/GuideStoryMapper.xml src/main/java/com/travel/mapper/GuideCommentMapper.java src/main/java/com/travel/mapper/GuideCommentMapper.xml
git commit -m "feat(mapper): add status filter to list/count queries"
```

---

## Task 5: 控制器添加审核接口

**Files:**
- Modify: `src/main/java/com/travel/controller/admin/AdminUserApiController.java:27-53`
- Modify: `src/main/java/com/travel/controller/admin/AdminGuideApiController.java:27-49`
- Modify: `src/main/java/com/travel/controller/admin/AdminStoryApiController.java:27-49`
- Modify: `src/main/java/com/travel/controller/admin/AdminCommentApiController.java:27-41`

- [ ] **Step 1: AdminUserApiController.java 修改 list 方法支持 status 筛选**

将 `AdminUserApiController.java` 第 27-32 行的 `list` 方法替换为：

```java
    @GetMapping
    public ApiResponse<PageResult<User>> list(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(required = false) Integer status) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = userMapper.countAll(status);
        return ApiResponse.success(PageResult.of(userMapper.selectPage(offset, PAGE_SIZE, status), page, PAGE_SIZE, total));
    }
```

- [ ] **Step 2: AdminUserApiController.java 移除 update 方法，添加 audit 方法**

将 `AdminUserApiController.java` 第 41-53 行的 `update` 方法替换为：

```java
    @PostMapping("/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id,
                                   @RequestBody Map<String, String> body,
                                   HttpSession session,
                                   HttpServletRequest request) {
        String action = body.get("action");
        User user = userMapper.selectById(id);
        if (user == null) return ApiResponse.fail("NOT_FOUND", "用户不存在");

        Integer newStatus;
        String detail;
        switch (action) {
            case "approve" -> { newStatus = 1; detail = "审核通过"; }
            case "reject" -> { newStatus = 2; detail = "审核拒绝"; }
            case "disable" -> { newStatus = 2; detail = "禁用"; }
            case "enable" -> { newStatus = 1; detail = "启用"; }
            default -> { newStatus = null; detail = null; }
        }

        if (newStatus == null) return ApiResponse.fail("INVALID_ACTION", "无效的操作");

        userMapper.updateStatus(id, newStatus);
        logOperation(session, request, "AUDIT", "USER", id, detail + ": " + user.getUsername());
        return ApiResponse.success();
    }
```

- [ ] **Step 3: AdminGuideApiController.java 修改 list 方法支持 status 筛选**

将 `AdminGuideApiController.java` 第 27-32 行的 `list` 方法替换为：

```java
    @GetMapping
    public ApiResponse<PageResult<GuideSummary>> list(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(required = false) Integer status) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = guideMapper.countAll(status);
        return ApiResponse.success(PageResult.of(guideMapper.selectPage(offset, PAGE_SIZE, status), page, PAGE_SIZE, total));
    }
```

- [ ] **Step 4: AdminGuideApiController.java 移除 update 方法，添加 audit 方法**

将 `AdminGuideApiController.java` 第 41-49 行的 `update` 方法替换为：

```java
    @PostMapping("/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id,
                                   @RequestBody Map<String, String> body,
                                   HttpSession session,
                                   HttpServletRequest request) {
        String action = body.get("action");
        GuideSummary guide = guideMapper.selectById(id);
        if (guide == null) return ApiResponse.fail("NOT_FOUND", "攻略不存在");

        Integer newStatus;
        String detail;
        switch (action) {
            case "approve" -> { newStatus = 1; detail = "审核通过"; }
            case "reject" -> { newStatus = 2; detail = "审核拒绝"; }
            case "down" -> { newStatus = 3; detail = "下架"; }
            default -> { newStatus = null; detail = null; }
        }

        if (newStatus == null) return ApiResponse.fail("INVALID_ACTION", "无效的操作");

        guideMapper.updateStatus(id, newStatus);
        logOperation(session, request, "AUDIT", "GUIDE", id, detail + ": " + guide.getTitle());
        return ApiResponse.success();
    }
```

- [ ] **Step 5: AdminStoryApiController.java 修改 list 方法支持 status 筛选**

将 `AdminStoryApiController.java` 第 27-32 行的 `list` 方法替换为：

```java
    @GetMapping
    public ApiResponse<PageResult<GuideStoryVO>> list(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(required = false) Integer status) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = storyMapper.countAll(status);
        return ApiResponse.success(PageResult.of(storyMapper.selectAll(offset, PAGE_SIZE, status), page, PAGE_SIZE, total));
    }
```

- [ ] **Step 6: AdminStoryApiController.java 移除 update 方法，添加 audit 方法**

将 `AdminStoryApiController.java` 第 41-49 行的 `update` 方法替换为：

```java
    @PostMapping("/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id,
                                   @RequestBody Map<String, String> body,
                                   HttpSession session,
                                   HttpServletRequest request) {
        String action = body.get("action");
        GuideStoryVO story = storyMapper.selectById(id);
        if (story == null) return ApiResponse.fail("NOT_FOUND", "故事不存在");

        Integer newStatus;
        String detail;
        switch (action) {
            case "approve" -> { newStatus = 1; detail = "审核通过"; }
            case "reject" -> { newStatus = 2; detail = "审核拒绝"; }
            case "down" -> { newStatus = 3; detail = "下架"; }
            default -> { newStatus = null; detail = null; }
        }

        if (newStatus == null) return ApiResponse.fail("INVALID_ACTION", "无效的操作");

        storyMapper.updateStatus(id, newStatus);
        logOperation(session, request, "AUDIT", "STORY", id, detail);
        return ApiResponse.success();
    }
```

- [ ] **Step 7: AdminCommentApiController.java 修改 list 方法支持 status 筛选**

将 `AdminCommentApiController.java` 第 27-32 行的 `list` 方法替换为：

```java
    @GetMapping
    public ApiResponse<PageResult<GuideComment>> list(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(required = false) Integer status) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = commentMapper.countAll(status);
        return ApiResponse.success(PageResult.of(commentMapper.selectPage(offset, PAGE_SIZE, status), page, PAGE_SIZE, total));
    }
```

- [ ] **Step 8: AdminCommentApiController.java 添加 audit 方法**

在 `AdminCommentApiController.java` 第 41 行（`delete` 方法之后）添加：

```java
    @PostMapping("/{id}/audit")
    public ApiResponse<Void> audit(@PathVariable Long id,
                                   @RequestBody Map<String, String> body,
                                   HttpSession session,
                                   HttpServletRequest request) {
        String action = body.get("action");
        Integer newStatus;
        String detail;
        switch (action) {
            case "approve" -> { newStatus = 1; detail = "审核通过"; }
            case "reject" -> { newStatus = 2; detail = "审核拒绝"; }
            case "down" -> { newStatus = 3; detail = "下架"; }
            default -> { newStatus = null; detail = null; }
        }

        if (newStatus == null) return ApiResponse.fail("INVALID_ACTION", "无效的操作");

        commentMapper.updateStatus(id, newStatus);
        logOperation(session, request, "AUDIT", "COMMENT", id, detail);
        return ApiResponse.success();
    }
```

- [ ] **Step 9: 提交**

```bash
git add src/main/java/com/travel/controller/admin/AdminUserApiController.java src/main/java/com/travel/controller/admin/AdminGuideApiController.java src/main/java/com/travel/controller/admin/AdminStoryApiController.java src/main/java/com/travel/controller/admin/AdminCommentApiController.java
git commit -m "feat(admin): replace edit endpoints with audit endpoints"
```

---

## Task 6: 前端添加状态徽章样式

**Files:**
- Modify: `src/main/resources/static/admin/css/components.css:159-172`

- [ ] **Step 1: components.css 添加新状态徽章样式**

在 `components.css` 第 172 行（`.badge-danger::before` 之后）添加：

```css
.badge-warning { color: var(--color-accent); background: var(--color-warning-bg); }
.badge-warning::before { background: var(--color-accent); }
.badge-info { color: var(--color-primary); background: var(--color-primary-bg); }
.badge-info::before { background: var(--color-primary); }
```

- [ ] **Step 2: 提交**

```bash
git add src/main/resources/static/admin/css/components.css
git commit -m "style(admin): add warning and info badge styles"
```

---

## Task 7: 前端 users.js 替换编辑为审核操作

**Files:**
- Modify: `src/main/resources/static/admin/js/pages/users.js:1-96`

- [ ] **Step 1: 重写 users.js**

将 `users.js` 全部内容替换为：

```javascript
const USER_STATUS_MAP = { 0: '待审核', 1: '正常', 2: '禁用' };
const USER_STATUS_BADGE = { 0: 'badge-warning', 1: 'badge-success', 2: 'badge-danger' };

async function initUsers(page = 1, status = null) {
  const content = document.getElementById('content');
  try {
    const statusParam = status !== null ? `&status=${status}` : '';
    const result = await API.get(`/admin/users?page=${page}${statusParam}`);
    const rows = result.list.map(u => `
      <tr>
        <td>${u.id}</td>
        <td>${u.username}</td>
        <td>${u.phone || '-'}</td>
        <td>${u.email || '-'}</td>
        <td><span class="badge ${USER_STATUS_BADGE[u.status] || 'badge-info'}">${USER_STATUS_MAP[u.status] || '未知'}</span></td>
        <td>${u.createdAt || '-'}</td>
        <td>
          ${u.status === 0 ? `
            <button class="btn-link" onclick="auditUser(${u.id}, 'approve')">通过</button>
            <button class="btn-link danger" onclick="auditUser(${u.id}, 'reject')">拒绝</button>
          ` : ''}
          ${u.status === 1 ? `
            <button class="btn-link danger" onclick="auditUser(${u.id}, 'disable')">禁用</button>
          ` : ''}
          ${u.status === 2 ? `
            <button class="btn-link" onclick="auditUser(${u.id}, 'enable')">启用</button>
          ` : ''}
          <button class="btn-link danger" onclick="deleteUser(${u.id}, '${u.username}')">删除</button>
        </td>
      </tr>`).join('');

    const filterButtons = `
      <div class="filter-bar">
        <button class="btn btn-sm ${status === null ? 'btn-primary' : 'btn-ghost'}" onclick="initUsers(1, null)">全部</button>
        <button class="btn btn-sm ${status === 0 ? 'btn-primary' : 'btn-ghost'}" onclick="initUsers(1, 0)">待审核</button>
        <button class="btn btn-sm ${status === 1 ? 'btn-primary' : 'btn-ghost'}" onclick="initUsers(1, 1)">正常</button>
        <button class="btn btn-sm ${status === 2 ? 'btn-primary' : 'btn-ghost'}" onclick="initUsers(1, 2)">禁用</button>
      </div>`;

    content.innerHTML = `
      <div class="page-header">
        <div class="page-title">用户管理</div>
      </div>
      ${filterButtons}
      <div class="card">
        <div class="table-wrapper">
          <table>
            <thead><tr><th>ID</th><th>用户名</th><th>手机号</th><th>邮箱</th><th>状态</th><th>注册时间</th><th>操作</th></tr></thead>
            <tbody>${rows || '<tr><td colspan="7" class="empty">暂无数据</td></tr>'}</tbody>
          </table>
        </div>
        <div class="card-body">${renderPagination(result.page, result.totalPages)}</div>
      </div>`;
    content.querySelectorAll('.page-btn:not([disabled])').forEach(btn => {
      btn.addEventListener('click', () => initUsers(parseInt(btn.dataset.page), status));
    });
  } catch (err) {
    content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`;
  }
}

async function auditUser(id, action) {
  const actionMap = { approve: '审核通过', reject: '审核拒绝', disable: '禁用', enable: '启用' };
  if (!await confirmDialog(`确认${actionMap[action]}该用户？`)) return;
  try {
    await API.post(`/admin/users/${id}/audit`, { action });
    Toast.success('操作成功');
    initUsers();
  } catch (err) { Toast.error(err.message); }
}

async function deleteUser(id, name) {
  if (!await confirmDialog(`确认删除用户「${name}」？`)) return;
  try {
    await API.delete(`/admin/users/${id}`);
    Toast.success('删除成功');
    initUsers();
  } catch (err) { Toast.error(err.message); }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/resources/static/admin/js/pages/users.js
git commit -m "feat(admin): replace user edit with audit actions"
```

---

## Task 8: 前端 guides.js 替换编辑为审核操作

**Files:**
- Modify: `src/main/resources/static/admin/js/pages/guides.js:1-66`

- [ ] **Step 1: 重写 guides.js**

将 `guides.js` 全部内容替换为：

```javascript
const GUIDE_STATUS_MAP = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已下架' };
const GUIDE_STATUS_BADGE = { 0: 'badge-warning', 1: 'badge-success', 2: 'badge-danger', 3: 'badge-info' };

async function initGuides(page = 1, status = null) {
  const content = document.getElementById('content');
  try {
    const statusParam = status !== null ? `&status=${status}` : '';
    const result = await API.get(`/admin/guides?page=${page}${statusParam}`);
    const rows = result.list.map(g => `
      <tr>
        <td>${g.id}</td>
        <td>${g.title}</td>
        <td>${g.authorName || '-'}</td>
        <td>${g.destinationName || '-'}</td>
        <td><span class="badge ${GUIDE_STATUS_BADGE[g.status] || 'badge-info'}">${GUIDE_STATUS_MAP[g.status] || '未知'}</span></td>
        <td>${g.publishedAt || '-'}</td>
        <td>
          ${g.status === 0 ? `
            <button class="btn-link" onclick="auditGuide(${g.id}, 'approve')">通过</button>
            <button class="btn-link danger" onclick="auditGuide(${g.id}, 'reject')">拒绝</button>
          ` : ''}
          ${g.status === 1 ? `
            <button class="btn-link danger" onclick="auditGuide(${g.id}, 'down')">下架</button>
          ` : ''}
          ${g.status === 2 || g.status === 3 ? `
            <button class="btn-link" onclick="auditGuide(${g.id}, 'approve')">通过</button>
          ` : ''}
          <button class="btn-link danger" onclick="deleteGuide(${g.id}, '${(g.title||'').replace(/'/g,"\\'")}')">删除</button>
        </td>
      </tr>`).join('');

    const filterButtons = `
      <div class="filter-bar">
        <button class="btn btn-sm ${status === null ? 'btn-primary' : 'btn-ghost'}" onclick="initGuides(1, null)">全部</button>
        <button class="btn btn-sm ${status === 0 ? 'btn-primary' : 'btn-ghost'}" onclick="initGuides(1, 0)">待审核</button>
        <button class="btn btn-sm ${status === 1 ? 'btn-primary' : 'btn-ghost'}" onclick="initGuides(1, 1)">已通过</button>
        <button class="btn btn-sm ${status === 2 ? 'btn-primary' : 'btn-ghost'}" onclick="initGuides(1, 2)">已拒绝</button>
        <button class="btn btn-sm ${status === 3 ? 'btn-primary' : 'btn-ghost'}" onclick="initGuides(1, 3)">已下架</button>
      </div>`;

    content.innerHTML = `
      <div class="page-header"><div class="page-title">攻略管理</div></div>
      ${filterButtons}
      <div class="card">
        <div class="table-wrapper">
          <table>
            <thead><tr><th>ID</th><th>标题</th><th>作者</th><th>目的地</th><th>状态</th><th>发布时间</th><th>操作</th></tr></thead>
            <tbody>${rows || '<tr><td colspan="7" class="empty">暂无数据</td></tr>'}</tbody>
          </table>
        </div>
        <div class="card-body">${renderPagination(result.page, result.totalPages)}</div>
      </div>`;
    content.querySelectorAll('.page-btn:not([disabled])').forEach(btn => {
      btn.addEventListener('click', () => initGuides(parseInt(btn.dataset.page), status));
    });
  } catch (err) {
    content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`;
  }
}

async function auditGuide(id, action) {
  const actionMap = { approve: '审核通过', reject: '审核拒绝', down: '下架' };
  if (!await confirmDialog(`确认${actionMap[action]}该攻略？`)) return;
  try { await API.post(`/admin/guides/${id}/audit`, { action }); Toast.success('操作成功'); initGuides(); }
  catch (err) { Toast.error(err.message); }
}

async function deleteGuide(id, title) {
  if (!await confirmDialog(`确认删除攻略「${title}」？`)) return;
  try { await API.delete(`/admin/guides/${id}`); Toast.success('删除成功'); initGuides(); }
  catch (err) { Toast.error(err.message); }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/resources/static/admin/js/pages/guides.js
git commit -m "feat(admin): replace guide edit with audit actions"
```

---

## Task 9: 前端 stories.js 替换编辑为审核操作

**Files:**
- Modify: `src/main/resources/static/admin/js/pages/stories.js:1-61`

- [ ] **Step 1: 重写 stories.js**

将 `stories.js` 全部内容替换为：

```javascript
const STORY_STATUS_MAP = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已下架' };
const STORY_STATUS_BADGE = { 0: 'badge-warning', 1: 'badge-success', 2: 'badge-danger', 3: 'badge-info' };

async function initStories(page = 1, status = null) {
  const content = document.getElementById('content');
  try {
    const statusParam = status !== null ? `&status=${status}` : '';
    const result = await API.get(`/admin/stories?page=${page}${statusParam}`);
    const rows = result.list.map(s => `
      <tr>
        <td>${s.id}</td>
        <td>${(s.content || '').substring(0, 50)}${(s.content || '').length > 50 ? '...' : ''}</td>
        <td>${s.authorName || '-'}</td>
        <td><span class="badge ${STORY_STATUS_BADGE[s.status] || 'badge-info'}">${STORY_STATUS_MAP[s.status] || '未知'}</span></td>
        <td>${s.createdAt || '-'}</td>
        <td>
          ${s.status === 0 ? `
            <button class="btn-link" onclick="auditStory(${s.id}, 'approve')">通过</button>
            <button class="btn-link danger" onclick="auditStory(${s.id}, 'reject')">拒绝</button>
          ` : ''}
          ${s.status === 1 ? `
            <button class="btn-link danger" onclick="auditStory(${s.id}, 'down')">下架</button>
          ` : ''}
          ${s.status === 2 || s.status === 3 ? `
            <button class="btn-link" onclick="auditStory(${s.id}, 'approve')">通过</button>
          ` : ''}
          <button class="btn-link danger" onclick="deleteStory(${s.id})">删除</button>
        </td>
      </tr>`).join('');

    const filterButtons = `
      <div class="filter-bar">
        <button class="btn btn-sm ${status === null ? 'btn-primary' : 'btn-ghost'}" onclick="initStories(1, null)">全部</button>
        <button class="btn btn-sm ${status === 0 ? 'btn-primary' : 'btn-ghost'}" onclick="initStories(1, 0)">待审核</button>
        <button class="btn btn-sm ${status === 1 ? 'btn-primary' : 'btn-ghost'}" onclick="initStories(1, 1)">已通过</button>
        <button class="btn btn-sm ${status === 2 ? 'btn-primary' : 'btn-ghost'}" onclick="initStories(1, 2)">已拒绝</button>
        <button class="btn btn-sm ${status === 3 ? 'btn-primary' : 'btn-ghost'}" onclick="initStories(1, 3)">已下架</button>
      </div>`;

    content.innerHTML = `
      <div class="page-header"><div class="page-title">故事管理</div></div>
      ${filterButtons}
      <div class="card">
        <div class="table-wrapper">
          <table>
            <thead><tr><th>ID</th><th>内容</th><th>作者</th><th>状态</th><th>发布时间</th><th>操作</th></tr></thead>
            <tbody>${rows || '<tr><td colspan="6" class="empty">暂无数据</td></tr>'}</tbody>
          </table>
        </div>
        <div class="card-body">${renderPagination(result.page, result.totalPages)}</div>
      </div>`;
    content.querySelectorAll('.page-btn:not([disabled])').forEach(btn => {
      btn.addEventListener('click', () => initStories(parseInt(btn.dataset.page), status));
    });
  } catch (err) { content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`; }
}

async function auditStory(id, action) {
  const actionMap = { approve: '审核通过', reject: '审核拒绝', down: '下架' };
  if (!await confirmDialog(`确认${actionMap[action]}该故事？`)) return;
  try { await API.post(`/admin/stories/${id}/audit`, { action }); Toast.success('操作成功'); initStories(); }
  catch (err) { Toast.error(err.message); }
}

async function deleteStory(id) {
  if (!await confirmDialog('确认删除该故事？')) return;
  try { await API.delete(`/admin/stories/${id}`); Toast.success('删除成功'); initStories(); }
  catch (err) { Toast.error(err.message); }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/resources/static/admin/js/pages/stories.js
git commit -m "feat(admin): replace story edit with audit actions"
```

---

## Task 10: 前端 comments.js 添加审核操作

**Files:**
- Modify: `src/main/resources/static/admin/js/pages/comments.js:1-37`

- [ ] **Step 1: 重写 comments.js**

将 `comments.js` 全部内容替换为：

```javascript
const COMMENT_STATUS_MAP = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已下架' };
const COMMENT_STATUS_BADGE = { 0: 'badge-warning', 1: 'badge-success', 2: 'badge-danger', 3: 'badge-info' };

async function initComments(page = 1, status = null) {
  const content = document.getElementById('content');
  try {
    const statusParam = status !== null ? `&status=${status}` : '';
    const result = await API.get(`/admin/comments?page=${page}${statusParam}`);
    const rows = result.list.map(c => `
      <tr>
        <td>${c.id}</td>
        <td>${(c.content || '').substring(0, 60)}${(c.content || '').length > 60 ? '...' : ''}</td>
        <td>${c.authorName || '-'}</td>
        <td>${c.guideId || '-'}</td>
        <td><span class="badge ${COMMENT_STATUS_BADGE[c.status] || 'badge-info'}">${COMMENT_STATUS_MAP[c.status] || '未知'}</span></td>
        <td>${c.createdAt || '-'}</td>
        <td>
          ${c.status === 0 ? `
            <button class="btn-link" onclick="auditComment(${c.id}, 'approve')">通过</button>
            <button class="btn-link danger" onclick="auditComment(${c.id}, 'reject')">拒绝</button>
          ` : ''}
          ${c.status === 1 ? `
            <button class="btn-link danger" onclick="auditComment(${c.id}, 'down')">下架</button>
          ` : ''}
          ${c.status === 2 || c.status === 3 ? `
            <button class="btn-link" onclick="auditComment(${c.id}, 'approve')">通过</button>
          ` : ''}
          <button class="btn-link danger" onclick="deleteComment(${c.id})">删除</button>
        </td>
      </tr>`).join('');

    const filterButtons = `
      <div class="filter-bar">
        <button class="btn btn-sm ${status === null ? 'btn-primary' : 'btn-ghost'}" onclick="initComments(1, null)">全部</button>
        <button class="btn btn-sm ${status === 0 ? 'btn-primary' : 'btn-ghost'}" onclick="initComments(1, 0)">待审核</button>
        <button class="btn btn-sm ${status === 1 ? 'btn-primary' : 'btn-ghost'}" onclick="initComments(1, 1)">已通过</button>
        <button class="btn btn-sm ${status === 2 ? 'btn-primary' : 'btn-ghost'}" onclick="initComments(1, 2)">已拒绝</button>
        <button class="btn btn-sm ${status === 3 ? 'btn-primary' : 'btn-ghost'}" onclick="initComments(1, 3)">已下架</button>
      </div>`;

    content.innerHTML = `
      <div class="page-header"><div class="page-title">评论管理</div></div>
      ${filterButtons}
      <div class="card">
        <div class="table-wrapper">
          <table>
            <thead><tr><th>ID</th><th>内容</th><th>作者</th><th>攻略ID</th><th>状态</th><th>时间</th><th>操作</th></tr></thead>
            <tbody>${rows || '<tr><td colspan="7" class="empty">暂无数据</td></tr>'}</tbody>
          </table>
        </div>
        <div class="card-body">${renderPagination(result.page, result.totalPages)}</div>
      </div>`;
    content.querySelectorAll('.page-btn:not([disabled])').forEach(btn => {
      btn.addEventListener('click', () => initComments(parseInt(btn.dataset.page), status));
    });
  } catch (err) { content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`; }
}

async function auditComment(id, action) {
  const actionMap = { approve: '审核通过', reject: '审核拒绝', down: '下架' };
  if (!await confirmDialog(`确认${actionMap[action]}该评论？`)) return;
  try { await API.post(`/admin/comments/${id}/audit`, { action }); Toast.success('操作成功'); initComments(); }
  catch (err) { Toast.error(err.message); }
}

async function deleteComment(id) {
  if (!await confirmDialog('确认删除该评论？')) return;
  try { await API.delete(`/admin/comments/${id}`); Toast.success('删除成功'); initComments(); }
  catch (err) { Toast.error(err.message); }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/resources/static/admin/js/pages/comments.js
git commit -m "feat(admin): add audit actions to comment management"
```

---

## Task 11: 前端 index.html 移除编辑路由

**Files:**
- Modify: `src/main/resources/static/admin/index.html:27-38`

- [ ] **Step 1: index.html 移除编辑路由**

将 `index.html` 第 27-38 行的路由注册替换为：

```javascript
    Router.register('/', params => { setupLayout('仪表盘', ''); initDashboard(); });
    Router.register('/users', params => { setupLayout('用户管理', 'users'); initUsers(); });
    Router.register('/guides', params => { setupLayout('攻略管理', 'guides'); initGuides(); });
    Router.register('/stories', params => { setupLayout('故事管理', 'stories'); initStories(); });
    Router.register('/destinations', params => { setupLayout('目的地管理', 'destinations'); initDestinations(); });
    Router.register('/destinations/edit/:id', params => { setupLayout('目的地管理', 'destinations'); initDestinationEdit(params.id); });
    Router.register('/comments', params => { setupLayout('评论管理', 'comments'); initComments(); });
    Router.start();
```

- [ ] **Step 2: 提交**

```bash
git add src/main/resources/static/admin/index.html
git commit -m "refactor(admin): remove edit routes for users, guides, stories"
```

---

## Task 12: 添加筛选栏样式

**Files:**
- Modify: `src/main/resources/static/admin/css/components.css:159-172`

- [ ] **Step 1: components.css 添加筛选栏样式**

在 `components.css` 第 172 行（`.badge-info::before` 之后）添加：

```css
/* ===================== Filter bar ===================== */
.filter-bar {
  display: flex; gap: var(--space-2); margin-bottom: var(--space-5);
  flex-wrap: wrap;
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/resources/static/admin/css/components.css
git commit -m "style(admin): add filter bar component style"
```

---

## Task 13: 验证编译和功能

- [ ] **Step 1: 编译验证**

```bash
cd E:/JavaFile/project/TravelProject && mvn compile -q
```

预期：编译成功，无错误

- [ ] **Step 2: 启动应用并测试**

启动 Spring Boot 应用，访问 `/admin/index.html`，验证：
1. 用户管理页面显示状态筛选按钮
2. 攻略管理页面显示状态筛选按钮
3. 故事管理页面显示状态筛选按钮
4. 评论管理页面显示状态筛选按钮
5. 点击"通过"/"拒绝"/"下架"按钮能正常工作
6. 编辑路由（`#/users/edit/:id` 等）不再存在

- [ ] **Step 3: 最终提交**

```bash
git add -A
git commit -m "feat(admin): complete audit management implementation"
```
