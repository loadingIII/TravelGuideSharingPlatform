# 管理员审核管理设计

## 概述

将管理员对用户、攻略、故事、评论的管理从"编辑内容"改为"审核（通过/拒绝）+ 删除"。管理员应该审核内容，而不是直接修改。

## 当前状态

- **用户(User)：** 有 status 字段（1=正常, 0=禁用）。管理员可编辑用户名/手机/邮箱/状态。
- **攻略/故事/评论：** 无 status 字段。管理员可编辑内容。
- **目的地(Destination)：** 保持不变 — 管理员保留编辑能力（目的地由站点管理，非用户生成）。

## 设计

### 1. 状态值定义

| 实体 | 新状态值 |
|------|----------|
| 用户 | 0=待审核, 1=正常, 2=禁用 |
| 攻略 | 0=待审核, 1=已通过, 2=已拒绝, 3=已下架 |
| 故事 | 0=待审核, 1=已通过, 2=已拒绝, 3=已下架 |
| 评论 | 0=待审核, 1=已通过, 2=已拒绝, 3=已下架 |

### 2. 管理员操作

#### 用户管理
- **审核通过：** 0→1
- **审核拒绝：** 0→2
- **禁用：** 1→2（禁用正常用户）
- **启用：** 2→1（重新启用被禁用用户）
- **删除：** 永久删除用户
- **移除：** 编辑用户名/手机/邮箱

#### 攻略/故事/评论管理
- **审核通过：** 0→1
- **审核拒绝：** 0→2
- **下架：** 1→3（下架已发布内容）
- **删除：** 永久删除
- **移除：** 编辑内容

### 3. 后端变更

#### 新增审核接口

```
POST /admin/{entity}/{id}/audit
Body: { "action": "approve" | "reject" | "disable" | "enable" | "down" }
```

- `approve`：设置状态为 1
- `reject`：设置状态为 2
- `disable`（仅用户）：设置状态为 2
- `enable`（仅用户）：设置状态为 1
- `down`（攻略/故事/评论）：设置状态为 3

#### 移除编辑接口

移除以下 PUT 接口：
- `PUT /admin/users/{id}`（编辑用户信息）
- `PUT /admin/guides/{id}`（编辑攻略标题/摘要）
- `PUT /admin/stories/{id}`（编辑故事内容）

保留：`PUT /admin/destinations/{id}`（目的地由站点管理）

#### 新增状态筛选

在列表接口添加 `?status=` 查询参数：
- `GET /admin/users?status=0` — 列出待审核用户
- `GET /admin/guides?status=1` — 列出已通过攻略
- `GET /admin/comments?status=0` — 列出待审核评论

### 4. 数据库变更

#### 用户表
```sql
ALTER TABLE users MODIFY COLUMN status INT DEFAULT 0 COMMENT '0=待审核, 1=正常, 2=禁用';
```

#### 攻略/故事/评论表
```sql
ALTER TABLE guides ADD COLUMN status INT DEFAULT 0 COMMENT '0=待审核, 1=已通过, 2=已拒绝, 3=已下架';
ALTER TABLE stories ADD COLUMN status INT DEFAULT 0 COMMENT '0=待审核, 1=已通过, 2=已拒绝, 3=已下架';
ALTER TABLE comments ADD COLUMN status INT DEFAULT 0 COMMENT '0=待审核, 1=已通过, 2=已拒绝, 3=已下架';
```

### 5. 前端变更

#### 列表页面
- 新增状态筛选标签/按钮（全部/待审核/已通过/已拒绝/已下架）
- 每行显示状态徽章
- 将"编辑"按钮替换为操作按钮：
  - 待审核：[通过] [拒绝]
  - 已通过：[下架]
  - 已拒绝：[通过]
  - 已下架：[通过]

#### 移除编辑路由
- 移除 `#/users/edit/:id`
- 移除 `#/guides/edit/:id`
- 移除 `#/stories/edit/:id`

#### 用户列表特殊处理
- 新增启用/禁用切换按钮
- 状态值映射为中文标签：0=待审核, 1=正常, 2=禁用

### 6. 实体模型变更

#### User.java
- 修改 status 注释：`0=待审核, 1=正常, 2=禁用`

#### GuideSummary.java / GuideDetail.java
- 新增 `status` (Integer) 字段

#### GuideStoryVO.java
- 新增 `status` (Integer) 字段

#### GuideComment.java
- 新增 `status` (Integer) 字段

### 7. Mapper 变更

#### GuideMapper.java
- 新增 `updateStatus(Long id, Integer status)` 方法
- 修改 `selectPage` 支持 `?status=` 筛选
- 修改 `countAll` 支持 `?status=` 筛选
- 修改 XML：SELECT 列添加 `g.status`，WHERE 子句添加 status 条件

#### GuideStoryMapper.java
- 新增 `updateStatus(Long id, Integer status)` 方法
- 修改 `selectAll` 支持 `?status=` 筛选
- 修改 `countAll` 支持 `?status=` 筛选
- 修改 XML：SELECT 列添加 `status`，WHERE 子句添加 status 条件

#### GuideCommentMapper.java
- 新增 `updateStatus(Long id, Integer status)` 方法
- 修改 `selectPage` 支持 `?status=` 筛选
- 修改 `countAll` 支持 `?status=` 筛选
- 修改 XML：SELECT 列添加 `c.status`，WHERE 子句添加 status 条件

#### UserMapper.java（如存在）
- 新增 `updateStatus(Long id, Integer status)` 方法
- 新增 `countByStatus(Integer status)` 方法

### 8. 管理员控制器变更

#### AdminGuideApiController.java
- 移除 `PUT /{id}`（updateGuide）
- 新增 `POST /{id}/audit` 接口
- 修改 `GET /` 支持 `?status=` 筛选

#### AdminStoryApiController.java
- 移除 `PUT /{id}`（updateContent）
- 新增 `POST /{id}/audit` 接口
- 修改 `GET /` 支持 `?status=` 筛选

#### AdminCommentApiController.java
- 新增 `POST /{id}/audit` 接口
- 修改 `GET /` 支持 `?status=` 筛选

#### AdminUserApiController.java
- 移除 `PUT /{id}`（编辑用户信息）
- 新增 `POST /{id}/audit` 接口
- 修改 `GET /` 支持 `?status=` 筛选

### 9. 管理员日志变更

审核操作使用 action 类型 "AUDIT" 而不是 "UPDATE"：
```
action: AUDIT
targetType: USER/GUIDE/STORY/COMMENT
targetId: {id}
detail: "审核通过" / "审核拒绝" / "禁用" / "下架"
```

## 需要修改的文件

### 后端 - 控制器
- `AdminUserApiController.java` — 移除 PUT，新增审核接口
- `AdminGuideApiController.java` — 移除 PUT，新增审核接口
- `AdminStoryApiController.java` — 移除 PUT，新增审核接口
- `AdminCommentApiController.java` — 新增审核接口

### 后端 - 模型
- `User.java` — 修改 status 注释
- `GuideSummary.java` — 新增 status 字段
- `GuideDetail.java` — 新增 status 字段
- `GuideStoryVO.java` — 新增 status 字段
- `GuideComment.java` — 新增 status 字段

### 后端 - Mapper
- `GuideMapper.java` — 新增 updateStatus，修改 selectPage/countAll 支持状态筛选
- `GuideMapper.xml` — SELECT 添加 status，WHERE 添加 status 条件
- `GuideStoryMapper.java` — 新增 updateStatus，修改 selectAll/countAll 支持状态筛选
- `GuideStoryMapper.xml` — SELECT 添加 status，WHERE 添加 status 条件
- `GuideCommentMapper.java` — 新增 updateStatus，修改 selectPage/countAll 支持状态筛选
- `GuideCommentMapper.xml` — SELECT 添加 status，WHERE 添加 status 条件

### 前端 (SPA)
- `js/pages/users.js` — 替换编辑为审核操作
- `js/pages/guides.js` — 替换编辑为审核操作
- `js/pages/stories.js` — 替换编辑为审核操作
- `js/pages/comments.js` — 新增审核操作
- `js/api.js` — 新增审核 API 方法
- `index.html` — 移除编辑路由

### 数据库
- SQL 迁移脚本：status 列变更

## 不在范围内

- 目的地管理（保持编辑+删除）
- 批量审核操作
- 管理后台审核历史查看
- 审核前内容预览
