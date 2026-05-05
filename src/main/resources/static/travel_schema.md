# 旅游攻略平台数据库设计（MySQL）

> 完整建表 SQL：`travel_schema.sql`  
> 数据库名：`travel_guide_platform`

## 文档说明

- 下文**每个表均独立用表格描述**，包含字段、类型、可空、键、默认值、说明。
- “键”列标记规则：`PK` 主键，`UK` 唯一键，`FK` 外键，`IDX` 普通索引。
- 组合键会标注为“联合”。

---

## 1) users（用户账号）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| id | BIGINT UNSIGNED | 否 | PK | AUTO_INCREMENT | 用户主键 |
| username | VARCHAR(50) | 否 | UK | - | 用户名（唯一） |
| phone | VARCHAR(20) | 是 | UK | NULL | 手机号（唯一，可空） |
| email | VARCHAR(255) | 是 | UK | NULL | 邮箱（唯一，可空） |
| password_hash | VARCHAR(255) | 否 | - | - | 密码哈希 |
| status | TINYINT UNSIGNED | 否 | IDX | 1 | 账号状态（1=正常） |
| last_login_at | DATETIME | 是 | - | NULL | 最后登录时间 |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 更新时间（ON UPDATE CURRENT_TIMESTAMP） |

---

## 2) user_profiles（用户扩展资料）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| user_id | BIGINT UNSIGNED | 否 | PK, FK | - | 对应 users.id（1:1） |
| nickname | VARCHAR(50) | 否 | - | - | 昵称 |
| avatar_url | VARCHAR(500) | 是 | - | NULL | 头像 URL |
| bio | VARCHAR(500) | 是 | - | NULL | 简介 |
| is_vip | TINYINT(1) | 否 | - | 0 | 是否 VIP |
| guides_count | INT UNSIGNED | 否 | - | 0 | 发布攻略数（冗余统计） |
| followers_count | INT UNSIGNED | 否 | - | 0 | 粉丝数（冗余统计） |
| likes_received_count | INT UNSIGNED | 否 | - | 0 | 获赞数（冗余统计） |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 更新时间（ON UPDATE CURRENT_TIMESTAMP） |

---

## 3) destinations（目的地主数据）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| id | BIGINT UNSIGNED | 否 | PK | AUTO_INCREMENT | 目的地主键 |
| name | VARCHAR(100) | 否 | UK(联合) | - | 名称 |
| country | VARCHAR(100) | 否 | UK(联合), IDX | - | 国家 |
| city | VARCHAR(100) | 否 | UK(联合) | '' | 城市 |
| description | VARCHAR(1000) | 是 | - | NULL | 描述 |
| cover_image_url | VARCHAR(500) | 是 | - | NULL | 封面图 |
| rating_avg | DECIMAL(3,2) | 是 | - | NULL | 平均评分 |
| guides_count | INT UNSIGNED | 否 | - | 0 | 攻略数量（冗余统计） |
| travelers_count | INT UNSIGNED | 否 | - | 0 | 去过人数（冗余统计） |
| popularity_score | DECIMAL(10,2) | 否 | IDX | 0 | 热度分 |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 更新时间（ON UPDATE CURRENT_TIMESTAMP） |

---

## 4) destination_showcases（首页展示位）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| id | BIGINT UNSIGNED | 否 | PK | AUTO_INCREMENT | 展示位主键 |
| destination_id | BIGINT UNSIGNED | 是 | FK, IDX | NULL | 关联目的地（可空，删除目的地后置空） |
| section | ENUM('recommended','popular','inspiration') | 否 | IDX(联合) | - | 展示分区：推荐/热门/灵感 |
| display_title | VARCHAR(100) | 否 | - | - | 展示标题 |
| display_subtitle | VARCHAR(255) | 是 | - | NULL | 展示副标题 |
| display_description | VARCHAR(1000) | 是 | - | NULL | 展示描述 |
| display_image_url | VARCHAR(500) | 是 | - | NULL | 展示图 |
| display_rating | DECIMAL(3,2) | 是 | - | NULL | 展示评分 |
| sort_order | INT UNSIGNED | 否 | IDX(联合) | 0 | 排序 |
| is_active | TINYINT(1) | 否 | - | 1 | 是否启用 |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 更新时间（ON UPDATE CURRENT_TIMESTAMP） |

---

## 5) guides（攻略主表）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| id | BIGINT UNSIGNED | 否 | PK | AUTO_INCREMENT | 攻略主键 |
| destination_id | BIGINT UNSIGNED | 否 | FK, IDX | - | 目的地 ID |
| author_id | BIGINT UNSIGNED | 否 | FK, IDX(联合) | - | 作者用户 ID |
| title | VARCHAR(200) | 否 | FULLTEXT | - | 标题 |
| summary | VARCHAR(1000) | 是 | FULLTEXT | NULL | 摘要 |
| content_html | MEDIUMTEXT | 是 | FULLTEXT | NULL | 正文（HTML） |
| cover_image_url | VARCHAR(500) | 是 | - | NULL | 封面图 |
| location_text | VARCHAR(200) | 是 | - | NULL | 位置展示文案 |
| scope | ENUM('domestic','international') | 否 | IDX(联合) | 'international' | 国内游/出境游 |
| travel_mode | ENUM('free','group','family','honeymoon','other') | 否 | IDX(联合) | 'free' | 出行模式 |
| publish_status | ENUM('draft','published','archived') | 否 | - | 'published' | 发布状态 |
| published_at | DATETIME | 是 | IDX(联合) | NULL | 发布时间 |
| days | SMALLINT UNSIGNED | 是 | - | NULL | 行程天数 |
| budget_total | DECIMAL(10,2) | 否 | - | 0 | 总预算 |
| views_count | INT UNSIGNED | 否 | - | 0 | 浏览量（冗余统计） |
| likes_count | INT UNSIGNED | 否 | - | 0 | 点赞数（冗余统计） |
| comments_count | INT UNSIGNED | 否 | - | 0 | 评论数（冗余统计） |
| favorites_count | INT UNSIGNED | 否 | - | 0 | 收藏数（冗余统计） |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 更新时间（ON UPDATE CURRENT_TIMESTAMP） |

---

## 6) tags（标签字典）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| id | BIGINT UNSIGNED | 否 | PK | AUTO_INCREMENT | 标签主键 |
| name | VARCHAR(50) | 否 | UK | - | 标签名（唯一） |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 创建时间 |

---

## 7) guide_tags（攻略-标签关联）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| guide_id | BIGINT UNSIGNED | 否 | PK(联合), FK | - | 攻略 ID |
| tag_id | BIGINT UNSIGNED | 否 | PK(联合), FK, IDX | - | 标签 ID |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 创建时间 |

---

## 8) guide_itinerary_days（行程天）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| id | BIGINT UNSIGNED | 否 | PK | AUTO_INCREMENT | 行程天主键 |
| guide_id | BIGINT UNSIGNED | 否 | FK, UK(联合), IDX | - | 攻略 ID |
| day_no | SMALLINT UNSIGNED | 否 | UK(联合) | - | 第几天（同攻略内唯一） |
| title | VARCHAR(200) | 否 | - | - | 当天标题 |
| summary | VARCHAR(1000) | 是 | - | NULL | 当天概要 |
| sort_order | SMALLINT UNSIGNED | 否 | - | 1 | 排序 |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 更新时间（ON UPDATE CURRENT_TIMESTAMP） |

---

## 9) guide_itinerary_spots（行程景点）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| id | BIGINT UNSIGNED | 否 | PK | AUTO_INCREMENT | 景点主键 |
| itinerary_day_id | BIGINT UNSIGNED | 否 | FK, IDX(联合) | - | 所属行程天 ID |
| name | VARCHAR(150) | 否 | - | - | 景点名 |
| description | VARCHAR(1000) | 是 | - | NULL | 景点说明 |
| visit_time | TIME | 是 | - | NULL | 计划到访时间 |
| duration_minutes | SMALLINT UNSIGNED | 是 | - | NULL | 停留时长（分钟） |
| image_url | VARCHAR(500) | 是 | - | NULL | 景点图 |
| sort_order | SMALLINT UNSIGNED | 否 | IDX(联合) | 1 | 排序 |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 更新时间（ON UPDATE CURRENT_TIMESTAMP） |

---

## 10) guide_tips（实用信息分类）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| id | BIGINT UNSIGNED | 否 | PK | AUTO_INCREMENT | 分类主键 |
| guide_id | BIGINT UNSIGNED | 否 | FK, UK(联合), IDX | - | 攻略 ID |
| category_name | VARCHAR(100) | 否 | UK(联合) | - | 分类名（同攻略内唯一） |
| sort_order | SMALLINT UNSIGNED | 否 | - | 1 | 排序 |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 更新时间（ON UPDATE CURRENT_TIMESTAMP） |

---

## 11) guide_tip_items（实用信息条目）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| id | BIGINT UNSIGNED | 否 | PK | AUTO_INCREMENT | 条目主键 |
| tip_id | BIGINT UNSIGNED | 否 | FK, IDX(联合) | - | 对应 guide_tips.id |
| item_text | VARCHAR(300) | 否 | - | - | 条目文本 |
| sort_order | SMALLINT UNSIGNED | 否 | IDX(联合) | 1 | 排序 |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 创建时间 |

---

## 12) guide_budget_items（预算拆分）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| id | BIGINT UNSIGNED | 否 | PK | AUTO_INCREMENT | 预算项主键 |
| guide_id | BIGINT UNSIGNED | 否 | FK, UK(联合), IDX | - | 攻略 ID |
| category_code | VARCHAR(50) | 否 | UK(联合) | - | 类别编码（如 transport） |
| category_name | VARCHAR(50) | 否 | - | - | 类别名称（如 交通） |
| amount | DECIMAL(10,2) | 否 | - | - | 金额 |
| percentage | DECIMAL(5,2) | 否 | - | - | 百分比 |
| sort_order | SMALLINT UNSIGNED | 否 | - | 1 | 排序 |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 更新时间（ON UPDATE CURRENT_TIMESTAMP） |

---

## 13) guide_related（相关攻略）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| guide_id | BIGINT UNSIGNED | 否 | PK(联合), FK | - | 主攻略 ID |
| related_guide_id | BIGINT UNSIGNED | 否 | PK(联合), FK, IDX | - | 相关攻略 ID |
| sort_order | SMALLINT UNSIGNED | 否 | - | 1 | 排序 |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 创建时间 |

---

## 14) guide_comments（攻略评论）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| id | BIGINT UNSIGNED | 否 | PK | AUTO_INCREMENT | 评论主键 |
| guide_id | BIGINT UNSIGNED | 否 | FK, IDX | - | 攻略 ID |
| user_id | BIGINT UNSIGNED | 是 | FK, IDX | NULL | 评论用户 ID（可空） |
| parent_comment_id | BIGINT UNSIGNED | 是 | FK, IDX | NULL | 父评论 ID（支持楼中楼） |
| author_name | VARCHAR(50) | 否 | - | - | 显示作者名 |
| author_avatar_url | VARCHAR(500) | 是 | - | NULL | 显示头像 |
| content | TEXT | 否 | - | - | 评论内容 |
| likes_count | INT UNSIGNED | 否 | - | 0 | 评论点赞数（冗余统计） |
| is_deleted | TINYINT(1) | 否 | - | 0 | 软删除标记 |
| created_at | DATETIME | 否 | IDX(联合) | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 更新时间（ON UPDATE CURRENT_TIMESTAMP） |

---

## 15) guide_likes（攻略点赞）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| guide_id | BIGINT UNSIGNED | 否 | PK(联合), FK | - | 攻略 ID |
| user_id | BIGINT UNSIGNED | 否 | PK(联合), FK, IDX | - | 用户 ID |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 点赞时间 |

---

## 16) guide_favorites（攻略收藏）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| guide_id | BIGINT UNSIGNED | 否 | PK(联合), FK | - | 攻略 ID |
| user_id | BIGINT UNSIGNED | 否 | PK(联合), FK, IDX | - | 用户 ID |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 收藏时间 |

---

## 17) guide_comment_likes（评论点赞）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| comment_id | BIGINT UNSIGNED | 否 | PK(联合), FK | - | 评论 ID |
| user_id | BIGINT UNSIGNED | 否 | PK(联合), FK, IDX | - | 用户 ID |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 点赞时间 |

---

## 18) user_follows（用户关注）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| follower_user_id | BIGINT UNSIGNED | 否 | PK(联合), FK | - | 关注者 ID |
| followed_user_id | BIGINT UNSIGNED | 否 | PK(联合), FK, IDX | - | 被关注者 ID |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 关注时间 |

---

## 19) traveler_stories（旅行者故事）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| id | BIGINT UNSIGNED | 否 | PK | AUTO_INCREMENT | 故事主键 |
| author_user_id | BIGINT UNSIGNED | 是 | FK, IDX | NULL | 作者用户 ID（可空） |
| author_name | VARCHAR(50) | 否 | - | - | 作者展示名 |
| author_avatar_url | VARCHAR(500) | 是 | - | NULL | 作者头像 |
| is_vip | TINYINT(1) | 否 | - | 0 | 是否 VIP |
| content | TEXT | 否 | - | - | 故事正文 |
| published_at | DATETIME | 否 | IDX | CURRENT_TIMESTAMP | 发布时间 |
| likes_count | INT UNSIGNED | 否 | - | 0 | 点赞数（冗余统计） |
| comments_count | INT UNSIGNED | 否 | - | 0 | 评论数（冗余统计） |
| shares_count | INT UNSIGNED | 否 | - | 0 | 分享数（冗余统计） |
| is_deleted | TINYINT(1) | 否 | - | 0 | 软删除标记 |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 更新时间（ON UPDATE CURRENT_TIMESTAMP） |

---

## 20) traveler_story_images（故事图片）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| id | BIGINT UNSIGNED | 否 | PK | AUTO_INCREMENT | 图片主键 |
| story_id | BIGINT UNSIGNED | 否 | FK, IDX(联合) | - | 故事 ID |
| image_url | VARCHAR(500) | 否 | - | - | 图片 URL |
| sort_order | SMALLINT UNSIGNED | 否 | IDX(联合) | 1 | 排序 |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 创建时间 |

---

## 21) traveler_story_likes（故事点赞）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| story_id | BIGINT UNSIGNED | 否 | PK(联合), FK | - | 故事 ID |
| user_id | BIGINT UNSIGNED | 否 | PK(联合), FK, IDX | - | 用户 ID |
| created_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 点赞时间 |

---

## 22) traveler_story_comments（故事评论）

| 字段 | 类型 | 可空 | 键 | 默认值 | 说明 |
|---|---|---|---|---|---|
| id | BIGINT UNSIGNED | 否 | PK | AUTO_INCREMENT | 评论主键 |
| story_id | BIGINT UNSIGNED | 否 | FK, IDX(联合) | - | 故事 ID |
| user_id | BIGINT UNSIGNED | 是 | FK, IDX | NULL | 评论用户 ID（可空） |
| parent_comment_id | BIGINT UNSIGNED | 是 | FK, IDX | NULL | 父评论 ID（支持楼中楼） |
| author_name | VARCHAR(50) | 否 | - | - | 显示作者名 |
| author_avatar_url | VARCHAR(500) | 是 | - | NULL | 显示头像 |
| content | TEXT | 否 | - | - | 评论内容 |
| likes_count | INT UNSIGNED | 否 | - | 0 | 评论点赞数（冗余统计） |
| is_deleted | TINYINT(1) | 否 | - | 0 | 软删除标记 |
| created_at | DATETIME | 否 | IDX(联合) | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | - | CURRENT_TIMESTAMP | 更新时间（ON UPDATE CURRENT_TIMESTAMP） |

---

## SQL 执行方式

```sql
SOURCE travel_schema.sql;
```

