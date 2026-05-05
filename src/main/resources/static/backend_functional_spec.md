# 旅游攻略平台后端功能文档（Spring Boot + MySQL）

## 1. 文档目标

基于当前前端页面（首页、登录/注册、社区列表、攻略详情），定义后端可落地的**功能模块、核心接口与业务规则**，用于指导 Spring Boot + MySQL 的基础开发。

---

## 2. 前端功能到后端能力映射

| 前端页面/区域 | 需要的后端能力 |
|---|---|
| 首页（搜索、推荐目的地、热门目的地、社区预览、灵感） | 首页聚合接口、目的地数据、热门内容排序、关键词搜索 |
| 登录页 | 手机号+密码登录、JWT发放、记住我（长有效期 Refresh Token） |
| 注册页 | 用户注册、用户名唯一性校验、密码加密存储 |
| 社区页（搜索+筛选+分页） | 攻略列表查询（关键词/标签/类型/分页/排序） |
| 社区页（旅行者故事） | 故事流查询、点赞、评论基础能力 |
| 社区页（发布攻略） | 发布攻略（标题、目的地、正文、图片） |
| 攻略详情页 | 攻略详情聚合（基础信息、行程、tips、预算、标签、作者、相关攻略） |
| 攻略详情互动区 | 点赞、收藏、评论、评论点赞、关注作者、分享计数（可选） |

---

## 3. 后端模块划分

## 3.1 用户与认证模块（Auth/User）
- 注册（用户名+密码）
- 登录（手机号+密码）
- 当前用户信息获取
- 用户资料（昵称、头像、简介、统计信息）

**建议表**：`users`、`user_profiles`  
**建议鉴权**：JWT Access Token + Refresh Token（可存在 Redis 或 DB）

---

## 3.2 首页内容模块（Home）
- 首页搜索入口（关键词搜目的地/攻略）
- 推荐目的地（发现目的地）
- 热门目的地（按热度或评分）
- 社区精选（首页3条攻略/故事预览）
- 灵感目的地（运营配置）

**建议表**：`destinations`、`destination_showcases`、`guides`、`traveler_stories`

---

## 3.3 攻略社区模块（Guide Community）
- 攻略列表（热门、最新）
- 筛选：国内/出境、自由行/跟团/亲子/蜜月
- 关键词搜索（标题/摘要/正文）
- 分页查询
- 发布攻略（基础版）

**建议表**：`guides`、`tags`、`guide_tags`、`guide_itinerary_days`、`guide_itinerary_spots`、`guide_tips`、`guide_tip_items`、`guide_budget_items`

---

## 3.4 攻略详情模块（Guide Detail）
- 攻略基础信息（标题、封面、作者、发布时间、浏览/点赞/评论）
- 行程概览（Day1/Day2…+景点）
- 实用信息（tips）
- 预算拆分（总预算+分类占比）
- 相关攻略推荐

**建议表**：`guides` + 其关联子表（行程、tips、预算、related）

---

## 3.5 互动模块（Interaction）
- 攻略点赞/取消点赞
- 攻略收藏/取消收藏
- 攻略评论（支持回复）
- 评论点赞
- 作者关注/取消关注
- 故事点赞/评论（社区故事流）

**建议表**：`guide_likes`、`guide_favorites`、`guide_comments`、`guide_comment_likes`、`user_follows`、`traveler_story_likes`、`traveler_story_comments`

---

## 3.6 旅行者故事模块（Story）
- 故事列表（时间倒序）
- 发布故事（内容+图片）
- 故事点赞、评论

**建议表**：`traveler_stories`、`traveler_story_images`、`traveler_story_likes`、`traveler_story_comments`

---

## 4. 核心业务规则（基础版）

1. 用户密码必须加密（BCrypt），禁止明文存储。  
2. 未登录用户可浏览；点赞/收藏/评论/发布必须登录。  
3. 点赞、收藏、关注需幂等（重复操作不报错，不重复计数）。  
4. 评论支持二级结构（parent_comment_id）。  
5. 列表统一分页参数：`page`、`pageSize`（默认 1/10）。  
6. 发布内容需基础校验（标题、目的地、正文长度、图片数量上限）。  
7. 删除评论建议软删除（`is_deleted=1`），前端展示“该评论已删除”。  

---

## 5. API 设计（MVP）

> 前缀建议：`/api/v1`

## 5.1 认证与用户
- `POST /auth/register` 注册
- `POST /auth/login` 登录
- `POST /auth/refresh` 刷新 token
- `GET /users/me` 当前用户信息
- `PUT /users/me/profile` 更新用户资料

## 5.2 首页
- `GET /home` 首页聚合（推荐目的地、热门目的地、社区精选、灵感）
- `GET /search?q=xxx&type=all|destination|guide` 全局搜索

## 5.3 目的地
- `GET /destinations` 目的地列表（支持热门排序）
- `GET /destinations/{id}` 目的地详情
- `GET /destinations/{id}/guides` 目的地攻略列表

## 5.4 攻略
- `GET /guides` 攻略列表（关键词、筛选、分页、排序）
- `GET /guides/{id}` 攻略详情（聚合返回）
- `POST /guides` 发布攻略
- `PUT /guides/{id}` 编辑攻略（作者本人）
- `DELETE /guides/{id}` 删除攻略（作者本人/管理员）

## 5.5 攻略互动
- `POST /guides/{id}/like` 点赞
- `DELETE /guides/{id}/like` 取消点赞
- `POST /guides/{id}/favorite` 收藏
- `DELETE /guides/{id}/favorite` 取消收藏
- `GET /guides/{id}/comments` 评论列表
- `POST /guides/{id}/comments` 发表评论
- `POST /guide-comments/{id}/like` 评论点赞
- `DELETE /guide-comments/{id}/like` 取消评论点赞

## 5.6 用户关注
- `POST /users/{id}/follow` 关注用户
- `DELETE /users/{id}/follow` 取消关注

## 5.7 旅行者故事
- `GET /stories` 故事流列表
- `POST /stories` 发布故事
- `POST /stories/{id}/like` 点赞故事
- `DELETE /stories/{id}/like` 取消点赞故事
- `GET /stories/{id}/comments` 故事评论列表
- `POST /stories/{id}/comments` 发布故事评论

---

## 6. 关键返回结构建议

## 6.1 列表统一结构
```json
{
  "list": [],
  "page": 1,
  "pageSize": 10,
  "total": 0,
  "totalPages": 0
}
```

## 6.2 错误统一结构
```json
{
  "code": "VALIDATION_ERROR",
  "message": "参数校验失败",
  "details": []
}
```

---

## 7. 与现有数据库脚本对应关系

当前仓库已有 `travel_schema.sql`，已覆盖本项目主要业务表结构。后端开发时可直接复用，并优先保证以下关系：
- 用户域：`users` ↔ `user_profiles`
- 攻略域：`guides` ↔ `guide_tags`/`guide_itinerary_*`/`guide_tips*`/`guide_budget_items`
- 互动域：`guide_likes`/`guide_favorites`/`guide_comments`/`guide_comment_likes`
- 社区故事域：`traveler_stories` 及其图片/点赞/评论子表

---

## 8. 推荐开发顺序（后端基本逻辑）

1. 认证与用户（注册/登录/鉴权）  
2. 首页聚合与攻略列表查询  
3. 攻略详情聚合接口  
4. 攻略点赞/收藏/评论  
5. 发布攻略  
6. 故事流与关注功能  

---

## 9. 非功能建议（基础可用）

- 接口鉴权：Spring Security + JWT  
- 参数校验：Jakarta Validation（`@Valid`）  
- 全局异常：统一异常处理器返回标准错误结构  
- 数据一致性：点赞/收藏/关注使用唯一索引+事务  
- 性能：热门列表、详情聚合可加 Redis 缓存（后续）  
- 日志：关键操作（登录、发布、删除、互动）记录审计日志  

