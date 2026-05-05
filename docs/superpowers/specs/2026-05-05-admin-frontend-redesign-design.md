# 管理端前端重设计方案

## 概述

将管理后台从 JSP 内联样式重构为前后端分离架构，HTML/CSS/JS 独立文件，使用 Chart.js 实现数据可视化，纯 CSS 实现专业 UI 设计。

## 技术选型

| 项目 | 选择 | 说明 |
|------|------|------|
| 前后端分离 | JS fetch API + JSON 接口 | JSP 仅作为入口壳加载 HTML/CSS/JS |
| CSS | 纯 CSS | 不依赖 Bootstrap，自定义设计系统 |
| 图表 | Chart.js | 折线图、环形图 |
| 路由 | Hash 模式 SPA | 单页面应用，无刷新切换 |
| 文件位置 | `static/admin/` | 浏览器可直接访问 |

## 目录结构

```
static/admin/
├── css/
│   ├── variables.css      # 颜色、间距、圆角等设计变量
│   ├── layout.css         # 侧边栏、顶栏、内容区布局
│   ├── components.css     # 按钮、卡片、表格、表单、分页
│   └── pages.css          # 页面特定样式
├── js/
│   ├── api.js             # fetch 封装，统一请求/错误处理
│   ├── router.js          # Hash 路由，页面切换
│   ├── components.js      # 侧边栏、表格、分页、弹窗等复用组件
│   └── pages/
│       ├── dashboard.js   # 仪表盘（图表）
│       ├── users.js       # 用户管理
│       ├── guides.js      # 攻略管理
│       ├── stories.js     # 故事管理
│       ├── destinations.js# 目的地管理
│       └── comments.js    # 评论管理
└── index.html             # SPA 入口页面
```

## 设计系统 (variables.css)

颜色方案：深色侧边栏 (#1e293b → #0f172a 渐变)，浅灰内容区 (#f1f5f9)，白色卡片 (#fff)
主色：#3b82f6 (蓝色)
成功：#22c55e  警告：#eab308  危险：#ef4444
文字色：#1e293b (主) / #475569 (次) / #64748b (辅助)
圆角：8px (小) / 12px (卡片) / 99px (标签)
间距：8px 倍数体系

## 架构设计

### 入口页面 (index.html)

单个 HTML 文件作为 SPA 入口，包含：
- 侧边栏导航（固定左侧 220px）
- 顶栏（标题 + 用户信息）
- 内容区域（动态渲染）
- 加载 Chart.js CDN

### 路由 (router.js)

Hash 模式路由：
- `#/` → 仪表盘
- `#/users` → 用户列表
- `#/users/edit/:id` → 用户编辑
- `#/guides` → 攻略列表
- `#/guides/edit/:id` → 攻略编辑
- `#/stories` → 故事列表
- `#/stories/edit/:id` → 故事编辑
- `#/destinations` → 目的地列表
- `#/destinations/edit/:id` → 目的地编辑
- `#/comments` → 评论列表

### API 层 (api.js)

封装 fetch，统一处理：
- 请求拦截（添加认证 token）
- 响应处理（JSON 解析、错误码判断）
- 错误提示（toast 通知）

### 后端改造

现有 Controller 需要新增 JSON 接口：
- `GET /api/admin/stats` → 仪表盘统计数据
- `GET /api/admin/users?page=&size=` → 用户分页列表
- `POST /api/admin/users` → 创建用户
- `PUT /api/admin/users/:id` → 更新用户
- `DELETE /api/admin/users/:id` → 删除用户
- 同理为 guides、stories、destinations、comments 添加 CRUD 接口

### JSP 壳页面

保留一个最小化 JSP 文件，仅负责：
- 引入 HTML 入口
- 注入认证信息（如有需要）

## 页面设计

### 仪表盘

- 4 个统计卡片（用户/攻略/故事/目的地），带 SVG 图标和趋势百分比
- 折线图：访问趋势（按月）
- 环形图：内容分布（攻略/故事/目的地/评论占比）

### 列表页（用户/攻略/故事/目的地/评论）

- 搜索/筛选栏
- 数据表格（带排序）
- 分页组件
- 操作按钮（编辑/删除）

### 编辑页

- 表单卡片
- 返回按钮
- 保存/取消按钮

## 实施步骤

1. 创建 CSS 设计系统（variables.css, layout.css, components.css, pages.css）
2. 创建 JS 基础设施（api.js, router.js, components.js）
3. 创建 index.html 入口页面
4. 实现仪表盘页面 + Chart.js 图表
5. 实现列表页（用户管理为例）
6. 实现编辑页
7. 后端新增 JSON API 接口
8. 其余页面实现（攻略/故事/目的地/评论）
9. 清理旧 JSP 文件

## 验收标准

- 所有页面可通过 Hash 路由访问
- 数据通过 fetch 从 JSON 接口获取
- 图表正常渲染（折线图 + 环形图）
- 响应式布局（侧边栏可折叠）
- 无 emoji，使用 SVG 图标
- 纯 CSS，无 Bootstrap 依赖
