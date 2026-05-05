# 管理端前端重设计 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将管理后台从 JSP 内联样式重构为前后端分离的 SPA，HTML/CSS/JS 独立文件，Chart.js 图表，纯 CSS 专业 UI。

**Architecture:** 单页应用（SPA）使用 hash 路由，JSP 仅作为登录入口壳。后端新增 `@RestController` 提供 JSON API，前端通过 fetch 调用。Session 保持不变，登录页改为纯 HTML + fetch 提交。

**Tech Stack:** 纯 CSS、原生 JS（fetch/hash router）、Chart.js CDN、Spring Boot `@RestController`

---

## 文件结构

### 新建文件
```
src/main/resources/static/admin/
├── index.html                 # SPA 入口
├── login.html                 # 登录页（纯 HTML）
├── css/
│   ├── variables.css          # 设计变量
│   ├── layout.css             # 布局（侧边栏、顶栏、内容区）
│   ├── components.css         # 组件（按钮、卡片、表格、表单、分页、弹窗）
│   └── pages.css              # 页面特定样式
├── js/
│   ├── api.js                 # fetch 封装
│   ├── router.js              # hash 路由
│   ├── components.js          # 复用组件（侧边栏、表格、分页、toast）
│   └── pages/
│       ├── dashboard.js       # 仪表盘
│       ├── users.js           # 用户管理
│       ├── guides.js          # 攻略管理
│       ├── stories.js         # 故事管理
│       ├── destinations.js    # 目的地管理
│       └── comments.js        # 评论管理

src/main/java/com/travel/controller/admin/api/
├── AdminAuthApiController.java    # 登录/登出/当前用户 JSON 接口
├── AdminStatsApiController.java   # 仪表盘统计 JSON 接口
├── AdminUserApiController.java    # 用户 CRUD JSON 接口
├── AdminGuideApiController.java   # 攻略 CRUD JSON 接口
├── AdminStoryApiController.java   # 故事 CRUD JSON 接口
├── AdminDestinationApiController.java  # 目的地 CRUD JSON 接口
└── AdminCommentApiController.java # 评论 CRUD JSON 接口
```

### 修改文件
- `src/main/java/com/travel/config/WebMvcConfig.java` — API 路径排除
- `src/main/java/com/travel/interceptor/AdminInterceptor.java` — API 请求返回 401 JSON

---

## Task 1: CSS 设计系统

**Files:**
- Create: `src/main/resources/static/admin/css/variables.css`
- Create: `src/main/resources/static/admin/css/layout.css`
- Create: `src/main/resources/static/admin/css/components.css`
- Create: `src/main/resources/static/admin/css/pages.css`

- [ ] **Step 1: 创建 variables.css**

```css
:root {
  --color-primary: #3b82f6;
  --color-primary-light: #60a5fa;
  --color-primary-bg: #eff6ff;
  --color-success: #22c55e;
  --color-success-bg: #f0fdf4;
  --color-warning: #eab308;
  --color-warning-bg: #fefce8;
  --color-danger: #ef4444;
  --color-danger-bg: #fef2f2;
  --color-text: #1e293b;
  --color-text-secondary: #475569;
  --color-text-muted: #64748b;
  --color-text-light: #94a3b8;
  --color-border: #e2e8f0;
  --color-bg: #f1f5f9;
  --color-bg-card: #ffffff;
  --color-sidebar: #1e293b;
  --color-sidebar-dark: #0f172a;
  --radius-sm: 6px;
  --radius: 8px;
  --radius-lg: 12px;
  --radius-full: 9999px;
  --shadow-sm: 0 1px 2px rgba(0,0,0,0.05);
  --shadow: 0 1px 3px rgba(0,0,0,0.06);
  --shadow-md: 0 4px 6px -1px rgba(0,0,0,0.1);
  --space-1: 4px;
  --space-2: 8px;
  --space-3: 12px;
  --space-4: 16px;
  --space-5: 20px;
  --space-6: 24px;
  --space-8: 32px;
  --sidebar-width: 240px;
  --topbar-height: 56px;
}

*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; color: var(--color-text); background: var(--color-bg); font-size: 14px; line-height: 1.5; }
a { color: var(--color-primary); text-decoration: none; }
a:hover { text-decoration: underline; }
```

- [ ] **Step 2: 创建 layout.css**

```css
.app { display: flex; min-height: 100vh; }

/* Sidebar */
.sidebar {
  position: fixed; top: 0; left: 0; bottom: 0; width: var(--sidebar-width);
  background: linear-gradient(180deg, var(--color-sidebar) 0%, var(--color-sidebar-dark) 100%);
  color: #fff; z-index: 100; display: flex; flex-direction: column;
  transition: transform 0.2s;
}
.sidebar-brand {
  padding: var(--space-5) var(--space-5); display: flex; align-items: center; gap: 10px;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
.sidebar-brand-icon {
  width: 32px; height: 32px; border-radius: var(--radius);
  background: linear-gradient(135deg, var(--color-primary), #8b5cf6);
  display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 14px;
}
.sidebar-brand-text { font-size: 15px; font-weight: 600; }
.sidebar-section { padding: var(--space-5) var(--space-3) var(--space-2); }
.sidebar-section-title {
  font-size: 11px; color: #64748b; text-transform: uppercase;
  letter-spacing: 1px; padding: 0 var(--space-2);
}
.sidebar-nav { flex: 1; padding: var(--space-2) 0; }
.sidebar-link {
  display: flex; align-items: center; gap: 10px; padding: 10px 16px;
  margin: 2px 8px; border-radius: var(--radius); color: #94a3b8;
  font-size: 13px; transition: all 0.15s; cursor: pointer;
}
.sidebar-link:hover { color: #fff; background: rgba(255,255,255,0.06); text-decoration: none; }
.sidebar-link.active {
  color: var(--color-primary-light); background: rgba(59,130,246,0.15);
}
.sidebar-link svg { width: 18px; height: 18px; flex-shrink: 0; }

/* Main content */
.main { margin-left: var(--sidebar-width); flex: 1; display: flex; flex-direction: column; min-height: 100vh; }
.topbar {
  height: var(--topbar-height); background: var(--color-bg-card);
  border-bottom: 1px solid var(--color-border); display: flex;
  align-items: center; justify-content: space-between; padding: 0 var(--space-6);
  position: sticky; top: 0; z-index: 50;
}
.topbar-title { font-size: 18px; font-weight: 600; }
.topbar-right { display: flex; align-items: center; gap: var(--space-3); }
.topbar-avatar {
  width: 32px; height: 32px; border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), #8b5cf6);
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 12px; font-weight: 600;
}
.content { padding: var(--space-6); flex: 1; }

/* Loading */
.loading { display: flex; align-items: center; justify-content: center; padding: 60px; color: var(--color-text-muted); }
```

- [ ] **Step 3: 创建 components.css**

```css
/* Cards */
.card { background: var(--color-bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow); border: 1px solid var(--color-border); }
.card-body { padding: var(--space-5); }
.card-title { font-size: 15px; font-weight: 600; margin-bottom: var(--space-4); }

/* Stat cards */
.stat-card { padding: var(--space-5); }
.stat-card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-3); }
.stat-card-icon {
  width: 40px; height: 40px; border-radius: var(--radius); display: flex;
  align-items: center; justify-content: center;
}
.stat-card-icon.blue { background: var(--color-primary-bg); color: var(--color-primary); }
.stat-card-icon.green { background: var(--color-success-bg); color: var(--color-success); }
.stat-card-icon.yellow { background: var(--color-warning-bg); color: var(--color-warning); }
.stat-card-icon.red { background: var(--color-danger-bg); color: var(--color-danger); }
.stat-card-trend { font-size: 12px; padding: 2px 8px; border-radius: var(--radius-full); }
.stat-card-trend.up { color: var(--color-success); background: var(--color-success-bg); }
.stat-card-trend.down { color: var(--color-danger); background: var(--color-danger-bg); }
.stat-card-value { font-size: 28px; font-weight: 700; color: var(--color-text); }
.stat-card-label { font-size: 13px; color: var(--color-text-muted); margin-top: var(--space-1); }

/* Grid */
.grid { display: grid; gap: var(--space-4); }
.grid-4 { grid-template-columns: repeat(4, 1fr); }
.grid-3 { grid-template-columns: repeat(3, 1fr); }
.grid-2 { grid-template-columns: repeat(2, 1fr); }

/* Tables */
.table-wrapper { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; font-size: 13px; }
th { padding: 10px 12px; text-align: left; color: var(--color-text-muted); font-weight: 500; border-bottom: 1px solid var(--color-border); }
td { padding: 10px 12px; border-bottom: 1px solid #f1f5f9; }
tr:hover td { background: #f8fafc; }

/* Buttons */
.btn {
  display: inline-flex; align-items: center; justify-content: center; gap: 6px;
  padding: 8px 16px; border-radius: var(--radius); font-size: 13px;
  font-weight: 500; border: 1px solid transparent; cursor: pointer;
  transition: all 0.15s;
}
.btn-primary { background: var(--color-primary); color: #fff; }
.btn-primary:hover { background: #2563eb; }
.btn-danger { background: var(--color-danger); color: #fff; }
.btn-danger:hover { background: #dc2626; }
.btn-ghost { background: transparent; color: var(--color-text-secondary); border-color: var(--color-border); }
.btn-ghost:hover { background: #f8fafc; }
.btn-sm { padding: 4px 10px; font-size: 12px; }
.btn-link { background: none; border: none; color: var(--color-primary); padding: 0; cursor: pointer; }
.btn-link:hover { text-decoration: underline; }
.btn-link.danger { color: var(--color-danger); }

/* Forms */
.form-group { margin-bottom: var(--space-4); }
.form-label { display: block; font-size: 13px; font-weight: 500; color: var(--color-text-secondary); margin-bottom: var(--space-1); }
.form-input, .form-select, .form-textarea {
  width: 100%; padding: 8px 12px; border: 1px solid var(--color-border);
  border-radius: var(--radius); font-size: 13px; color: var(--color-text);
  background: #fff; transition: border-color 0.15s;
}
.form-input:focus, .form-select:focus, .form-textarea:focus {
  outline: none; border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(59,130,246,0.1);
}
.form-textarea { resize: vertical; min-height: 80px; }

/* Badge / Status */
.badge { display: inline-block; padding: 2px 10px; border-radius: var(--radius-full); font-size: 12px; font-weight: 500; }
.badge-success { color: #16a34a; background: var(--color-success-bg); }
.badge-danger { color: #dc2626; background: var(--color-danger-bg); }

/* Pagination */
.pagination { display: flex; gap: var(--space-1); margin-top: var(--space-4); }
.page-btn {
  padding: 6px 12px; border: 1px solid var(--color-border); border-radius: var(--radius);
  background: #fff; cursor: pointer; font-size: 13px; color: var(--color-text-secondary);
}
.page-btn:hover { background: #f8fafc; }
.page-btn.active { background: var(--color-primary); color: #fff; border-color: var(--color-primary); }
.page-btn:disabled { opacity: 0.5; cursor: not-allowed; }

/* Toast */
.toast-container { position: fixed; top: var(--space-4); right: var(--space-4); z-index: 9999; display: flex; flex-direction: column; gap: var(--space-2); }
.toast {
  padding: 12px 20px; border-radius: var(--radius); color: #fff;
  font-size: 13px; box-shadow: var(--shadow-md); animation: slideIn 0.2s;
}
.toast.success { background: #16a34a; }
.toast.error { background: #dc2626; }
.toast.info { background: var(--color-primary); }
@keyframes slideIn { from { transform: translateX(100%); opacity: 0; } to { transform: translateX(0); opacity: 1; } }

/* Modal / Confirm */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 1000;
  display: flex; align-items: center; justify-content: center;
}
.modal { background: #fff; border-radius: var(--radius-lg); padding: var(--space-6); max-width: 400px; width: 90%; box-shadow: var(--shadow-md); }
.modal-title { font-size: 16px; font-weight: 600; margin-bottom: var(--space-3); }
.modal-body { color: var(--color-text-secondary); font-size: 14px; margin-bottom: var(--space-5); }
.modal-actions { display: flex; gap: var(--space-2); justify-content: flex-end; }

/* Page header */
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-5); }
.page-title { font-size: 20px; font-weight: 600; }

/* Alert */
.alert { padding: 12px 16px; border-radius: var(--radius); font-size: 13px; margin-bottom: var(--space-4); }
.alert-success { background: var(--color-success-bg); color: #16a34a; border: 1px solid #bbf7d0; }
.alert-danger { background: var(--color-danger-bg); color: #dc2626; border: 1px solid #fecaca; }

/* Empty state */
.empty { text-align: center; padding: 40px; color: var(--color-text-muted); }

/* Responsive */
@media (max-width: 768px) {
  .sidebar { transform: translateX(-100%); }
  .sidebar.open { transform: translateX(0); }
  .main { margin-left: 0; }
  .grid-4 { grid-template-columns: repeat(2, 1fr); }
}
```

- [ ] **Step 4: 创建 pages.css**

```css
/* Dashboard */
.dashboard-charts { margin-top: var(--space-4); }
.chart-card { padding: var(--space-5); }
.chart-card canvas { width: 100% !important; }

/* Login */
.login-page {
  min-height: 100vh; display: flex; align-items: center; justify-content: center;
  background: var(--color-bg);
}
.login-card { width: 100%; max-width: 400px; padding: var(--space-8); }
.login-card h2 { text-align: center; margin-bottom: var(--space-6); font-size: 22px; }
.login-card .btn { width: 100%; padding: 10px; }

/* Edit form */
.edit-card { max-width: 640px; }
```

- [ ] **Step 5: Commit**

```bash
git add src/main/resources/static/admin/css/
git commit -m "feat(admin): add CSS design system for admin SPA"
```

---

## Task 2: JavaScript 基础设施

**Files:**
- Create: `src/main/resources/static/admin/js/api.js`
- Create: `src/main/resources/static/admin/js/router.js`
- Create: `src/main/resources/static/admin/js/components.js`

- [ ] **Step 1: 创建 api.js**

```javascript
const API = {
  async request(url, options = {}) {
    const config = { headers: { 'Content-Type': 'application/json' }, ...options };
    if (config.body && typeof config.body === 'object') {
      config.body = JSON.stringify(config.body);
    }
    const res = await fetch(url, config);
    if (res.status === 401) {
      window.location.hash = '#/login';
      throw new Error('未登录');
    }
    const data = await res.json();
    if (data.code !== 'OK') {
      throw new Error(data.message || '请求失败');
    }
    return data.data;
  },
  get(url) { return this.request(url); },
  post(url, body) { return this.request(url, { method: 'POST', body }); },
  put(url, body) { return this.request(url, { method: 'PUT', body }); },
  delete(url) { return this.request(url, { method: 'DELETE' }); }
};
```

- [ ] **Step 2: 创建 router.js**

```javascript
const Router = {
  routes: {},
  current: null,
  register(path, handler) { this.routes[path] = handler; },
  start() {
    window.addEventListener('hashchange', () => this.resolve());
    this.resolve();
  },
  resolve() {
    const hash = window.location.hash.slice(1) || '/';
    for (const [pattern, handler] of Object.entries(this.routes)) {
      const match = this.matchRoute(pattern, hash);
      if (match) {
        this.current = { pattern, params: match };
        handler(match);
        return;
      }
    }
    window.location.hash = '#/';
  },
  matchRoute(pattern, hash) {
    const patternParts = pattern.split('/');
    const hashParts = hash.split('/');
    if (patternParts.length !== hashParts.length) return null;
    const params = {};
    for (let i = 0; i < patternParts.length; i++) {
      if (patternParts[i].startsWith(':')) {
        params[patternParts[i].slice(1)] = hashParts[i];
      } else if (patternParts[i] !== hashParts[i]) {
        return null;
      }
    }
    return params;
  },
  navigate(path) { window.location.hash = '#' + path; }
};
```

- [ ] **Step 3: 创建 components.js**

```javascript
/* SVG Icons (no emoji) */
const Icons = {
  dashboard: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>',
  users: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4-4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87"/><path d="M16 3.13a4 4 0 010 7.75"/></svg>',
  guide: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14.5 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/></svg>',
  story: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 013 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>',
  destination: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"/><circle cx="12" cy="10" r="3"/></svg>',
  comment: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/></svg>',
  bell: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 01-3.46 0"/></svg>'
};

/* Toast */
const Toast = {
  container: null,
  init() {
    this.container = document.createElement('div');
    this.container.className = 'toast-container';
    document.body.appendChild(this.container);
  },
  show(message, type = 'info', duration = 3000) {
    if (!this.container) this.init();
    const el = document.createElement('div');
    el.className = `toast ${type}`;
    el.textContent = message;
    this.container.appendChild(el);
    setTimeout(() => el.remove(), duration);
  },
  success(msg) { this.show(msg, 'success'); },
  error(msg) { this.show(msg, 'error'); }
};

/* Confirm dialog */
function confirmDialog(message) {
  return new Promise(resolve => {
    const overlay = document.createElement('div');
    overlay.className = 'modal-overlay';
    overlay.innerHTML = `
      <div class="modal">
        <div class="modal-title">确认操作</div>
        <div class="modal-body">${message}</div>
        <div class="modal-actions">
          <button class="btn btn-ghost" data-action="cancel">取消</button>
          <button class="btn btn-danger" data-action="confirm">确认</button>
        </div>
      </div>`;
    document.body.appendChild(overlay);
    overlay.addEventListener('click', e => {
      const action = e.target.dataset.action;
      if (action === 'confirm') { overlay.remove(); resolve(true); }
      if (action === 'cancel' || e.target === overlay) { overlay.remove(); resolve(false); }
    });
  });
}

/* Sidebar renderer */
function renderSidebar(activeKey) {
  const links = [
    { key: '', label: '仪表盘', icon: Icons.dashboard },
    { key: 'users', label: '用户管理', icon: Icons.users },
    { key: 'guides', label: '攻略管理', icon: Icons.guide },
    { key: 'stories', label: '故事管理', icon: Icons.story },
    { key: 'destinations', label: '目的地管理', icon: Icons.destination },
    { key: 'comments', label: '评论管理', icon: Icons.comment }
  ];
  const navHtml = links.map(l =>
    `<a class="sidebar-link ${l.key === activeKey ? 'active' : ''}" href="#/${l.key}">
      <span>${l.icon}</span> ${l.label}
    </a>`
  ).join('');
  return `
    <div class="sidebar-brand">
      <div class="sidebar-brand-icon">T</div>
      <div class="sidebar-brand-text">Travel Admin</div>
    </div>
    <div class="sidebar-section">
      <div class="sidebar-section-title">导航</div>
    </div>
    <nav class="sidebar-nav">${navHtml}</nav>`;
}

/* Pagination renderer */
function renderPagination(page, totalPages, onPageClick) {
  if (totalPages <= 1) return '';
  let html = '<div class="pagination">';
  html += `<button class="page-btn" ${page <= 1 ? 'disabled' : ''} data-page="${page - 1}">&laquo;</button>`;
  for (let i = 1; i <= totalPages; i++) {
    if (i === 1 || i === totalPages || Math.abs(i - page) <= 2) {
      html += `<button class="page-btn ${i === page ? 'active' : ''}" data-page="${i}">${i}</button>`;
    } else if (Math.abs(i - page) === 3) {
      html += '<button class="page-btn" disabled>...</button>';
    }
  }
  html += `<button class="page-btn" ${page >= totalPages ? 'disabled' : ''} data-page="${page + 1}">&raquo;</button>`;
  html += '</div>';
  return html;
}

/* Setup layout (sidebar + topbar + content) */
function setupLayout(title, activeKey) {
  const app = document.getElementById('app');
  app.innerHTML = `
    <div class="sidebar" id="sidebar">${renderSidebar(activeKey)}</div>
    <div class="main">
      <div class="topbar">
        <div class="topbar-title">${title}</div>
        <div class="topbar-right">
          <div class="topbar-avatar">A</div>
          <span style="font-size:13px;color:var(--color-text-secondary)">Admin</span>
        </div>
      </div>
      <div class="content" id="content"><div class="loading">加载中...</div></div>
    </div>`;
}
```

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/static/admin/js/api.js src/main/resources/static/admin/js/router.js src/main/resources/static/admin/js/components.js
git commit -m "feat(admin): add JS infrastructure (api, router, components)"
```

---

## Task 3: SPA 入口 + 登录页

**Files:**
- Create: `src/main/resources/static/admin/index.html`
- Create: `src/main/resources/static/admin/login.html`

- [ ] **Step 1: 创建 index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>旅游管理后台</title>
  <link rel="stylesheet" href="css/variables.css">
  <link rel="stylesheet" href="css/layout.css">
  <link rel="stylesheet" href="css/components.css">
  <link rel="stylesheet" href="css/pages.css">
  <script src="https://cdn.jsdelivr.net/npm/chart.js@4"></script>
</head>
<body>
  <div class="app" id="app"><div class="loading">加载中...</div></div>
  <script src="js/api.js"></script>
  <script src="js/router.js"></script>
  <script src="js/components.js"></script>
  <script src="js/pages/dashboard.js"></script>
  <script src="js/pages/users.js"></script>
  <script src="js/pages/guides.js"></script>
  <script src="js/pages/stories.js"></script>
  <script src="js/pages/destinations.js"></script>
  <script src="js/pages/comments.js"></script>
  <script>
    Router.register('/', params => { setupLayout('仪表盘', ''); initDashboard(); });
    Router.register('/users', params => { setupLayout('用户管理', 'users'); initUsers(); });
    Router.register('/users/edit/:id', params => { setupLayout('用户管理', 'users'); initUserEdit(params.id); });
    Router.register('/guides', params => { setupLayout('攻略管理', 'guides'); initGuides(); });
    Router.register('/guides/edit/:id', params => { setupLayout('攻略管理', 'guides'); initGuideEdit(params.id); });
    Router.register('/stories', params => { setupLayout('故事管理', 'stories'); initStories(); });
    Router.register('/stories/edit/:id', params => { setupLayout('故事管理', 'stories'); initStoryEdit(params.id); });
    Router.register('/destinations', params => { setupLayout('目的地管理', 'destinations'); initDestinations(); });
    Router.register('/destinations/edit/:id', params => { setupLayout('目的地管理', 'destinations'); initDestinationEdit(params.id); });
    Router.register('/comments', params => { setupLayout('评论管理', 'comments'); initComments(); });
    Router.start();
  </script>
</body>
</html>
```

- [ ] **Step 2: 创建 login.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>管理后台 - 登录</title>
  <link rel="stylesheet" href="css/variables.css">
  <link rel="stylesheet" href="css/components.css">
  <link rel="stylesheet" href="css/pages.css">
</head>
<body class="login-page">
  <div class="card login-card">
    <h2>旅游管理后台</h2>
    <div id="error" class="alert alert-danger" style="display:none"></div>
    <form id="loginForm">
      <div class="form-group">
        <label class="form-label">用户名</label>
        <input type="text" name="username" class="form-input" required autofocus>
      </div>
      <div class="form-group">
        <label class="form-label">密码</label>
        <input type="password" name="password" class="form-input" required>
      </div>
      <button type="submit" class="btn btn-primary">登录</button>
    </form>
  </div>
  <script>
    document.getElementById('loginForm').addEventListener('submit', async (e) => {
      e.preventDefault();
      const form = e.target;
      const body = new URLSearchParams(new FormData(form));
      try {
        const res = await fetch('/admin/api/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body
        });
        const data = await res.json();
        if (data.code === 'OK') {
          window.location.href = '/admin/index.html';
        } else {
          const err = document.getElementById('error');
          err.textContent = data.message || '登录失败';
          err.style.display = 'block';
        }
      } catch (err) {
        const el = document.getElementById('error');
        el.textContent = '网络错误';
        el.style.display = 'block';
      }
    });
  </script>
</body>
</html>
```

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/static/admin/index.html src/main/resources/static/admin/login.html
git commit -m "feat(admin): add SPA entry and login page"
```

---

## Task 4: 后端 Auth API + 拦截器改造

**Files:**
- Create: `src/main/java/com/travel/controller/admin/api/AdminAuthApiController.java`
- Modify: `src/main/java/com/travel/interceptor/AdminInterceptor.java`
- Modify: `src/main/java/com/travel/config/WebMvcConfig.java`

- [ ] **Step 1: 创建 AdminAuthApiController.java**

```java
package com.travel.controller.admin.api;

import com.travel.common.ApiResponse;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/api")
@RequiredArgsConstructor
public class AdminAuthApiController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ApiResponse<Void> login(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpSession session,
                                   HttpServletRequest request) {
        boolean ok = adminAuthService.login(session, username, password);
        if (!ok) {
            return ApiResponse.fail("AUTH_FAILED", "用户名或密码错误");
        }
        adminAuthService.logLogin(username, request.getRemoteAddr());
        return ApiResponse.success();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session, HttpServletRequest request) {
        adminAuthService.logLogout(session, request.getRemoteAddr());
        session.invalidate();
        return ApiResponse.success();
    }

    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> me(HttpSession session) {
        Map<String, Object> admin = adminAuthService.getCurrentAdmin(session);
        if (admin == null) {
            return ApiResponse.fail("NOT_LOGIN", "未登录");
        }
        return ApiResponse.success(admin);
    }
}
```

- [ ] **Step 2: 修改 AdminInterceptor.java — API 请求返回 401 JSON**

将 `AdminInterceptor.java` 修改为：

```java
package com.travel.interceptor;

import com.travel.service.AdminAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

    private final AdminAuthService adminAuthService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!adminAuthService.isLoggedIn(request.getSession())) {
            String uri = request.getRequestURI();
            if (uri.startsWith("/admin/api/")) {
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(
                    ApiResponse.fail("NOT_LOGIN", "未登录")));
                return false;
            }
            response.sendRedirect("/admin/login.html");
            return false;
        }
        return true;
    }
}
```

- [ ] **Step 3: 检查 AdminAuthService 是否有 login/logLogin/logLogout 方法**

读取 `AdminAuthService.java`，确认有 `login(HttpSession, String, String)` 方法。如果没有，需要添加。如果没有 `logLogin`/`logLogout` 方法，需要简化为只保留 `login` 和 `getCurrentAdmin`。

- [ ] **Step 4: 修改 WebMvcConfig — 排除静态资源和 API**

```java
registry.addInterceptor(adminInterceptor)
    .addPathPatterns("/admin/**")
    .excludePathPatterns("/admin/login.html", "/admin/index.html",
        "/admin/css/**", "/admin/js/**", "/admin/images/**");
```

注意：API 路径 (`/admin/api/**`) **不排除**，让拦截器保护但返回 JSON。

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/travel/controller/admin/api/AdminAuthApiController.java
git add src/main/java/com/travel/interceptor/AdminInterceptor.java
git add src/main/java/com/travel/config/WebMvcConfig.java
git commit -m "feat(admin): add auth API and update interceptor for JSON 401"
```

---

## Task 5: 仪表盘统计 API

**Files:**
- Create: `src/main/java/com/travel/controller/admin/api/AdminStatsApiController.java`

- [ ] **Step 1: 创建 AdminStatsApiController.java**

```java
package com.travel.controller.admin.api;

import com.travel.common.ApiResponse;
import com.travel.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/stats")
@RequiredArgsConstructor
public class AdminStatsApiController {

    private final UserMapper userMapper;
    private final GuideMapper guideMapper;
    private final GuideStoryMapper storyMapper;
    private final DestinationMapper destinationMapper;
    private final GuideCommentMapper commentMapper;

    @GetMapping
    public ApiResponse<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userMapper.countAll());
        data.put("guideCount", guideMapper.countAll());
        data.put("storyCount", storyMapper.countAll());
        data.put("destinationCount", destinationMapper.countAll());
        data.put("commentCount", commentMapper.countAll());
        return ApiResponse.success(data);
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/travel/controller/admin/api/AdminStatsApiController.java
git commit -m "feat(admin): add dashboard stats API endpoint"
```

---

## Task 6: 用户管理 API

**Files:**
- Create: `src/main/java/com/travel/controller/admin/api/AdminUserApiController.java`

- [ ] **Step 1: 创建 AdminUserApiController.java**

```java
package com.travel.controller.admin.api;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.mapper.AdminLogMapper;
import com.travel.mapper.UserMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.pojo.model.User;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/api/users")
@RequiredArgsConstructor
public class AdminUserApiController {

    private final UserMapper userMapper;
    private final AdminLogMapper adminLogMapper;
    private final AdminAuthService adminAuthService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public ApiResponse<PageResult<User>> list(@RequestParam(defaultValue = "1") int page) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = userMapper.countAll();
        return ApiResponse.success(PageResult.of(userMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total));
    }

    @GetMapping("/{id}")
    public ApiResponse<User> detail(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) return ApiResponse.fail("NOT_FOUND", "用户不存在");
        return ApiResponse.success(user);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        userMapper.updateUser(id,
            (String) body.get("username"),
            (String) body.get("phone"),
            (String) body.get("email"),
            (Integer) body.get("status"));
        logOperation(session, request, "UPDATE", "USER", id, "修改用户: " + body.get("username"));
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        User user = userMapper.selectById(id);
        userMapper.deleteById(id);
        logOperation(session, request, "DELETE", "USER", id, "删除用户: " + (user != null ? user.getUsername() : id));
        return ApiResponse.success();
    }

    private void logOperation(HttpSession session, HttpServletRequest request,
                              String action, String targetType, Long targetId, String detail) {
        Map<String, Object> admin = adminAuthService.getCurrentAdmin(session);
        if (admin == null) return;
        AdminLog log = new AdminLog();
        log.setAdminId((Long) admin.get("id"));
        log.setAdminUsername((String) admin.get("username"));
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        log.setIpAddress(request.getRemoteAddr());
        adminLogMapper.insert(log);
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/travel/controller/admin/api/AdminUserApiController.java
git commit -m "feat(admin): add user CRUD API endpoints"
```

---

## Task 7: 其余管理 API（攻略/故事/目的地/评论）

**Files:**
- Create: `src/main/java/com/travel/controller/admin/api/AdminGuideApiController.java`
- Create: `src/main/java/com/travel/controller/admin/api/AdminStoryApiController.java`
- Create: `src/main/java/com/travel/controller/admin/api/AdminDestinationApiController.java`
- Create: `src/main/java/com/travel/controller/admin/api/AdminCommentApiController.java`

- [ ] **Step 1: 创建 AdminGuideApiController.java**

```java
package com.travel.controller.admin.api;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.mapper.AdminLogMapper;
import com.travel.mapper.GuideMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.pojo.model.GuideSummary;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/api/guides")
@RequiredArgsConstructor
public class AdminGuideApiController {

    private final GuideMapper guideMapper;
    private final AdminLogMapper adminLogMapper;
    private final AdminAuthService adminAuthService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public ApiResponse<PageResult<GuideSummary>> list(@RequestParam(defaultValue = "1") int page) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = guideMapper.countAll();
        return ApiResponse.success(PageResult.of(guideMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total));
    }

    @GetMapping("/{id}")
    public ApiResponse<GuideSummary> detail(@PathVariable Long id) {
        GuideSummary guide = guideMapper.selectById(id);
        if (guide == null) return ApiResponse.fail("NOT_FOUND", "攻略不存在");
        return ApiResponse.success(guide);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        guideMapper.updateGuide(id, (String) body.get("title"), (String) body.get("summary"));
        logOperation(session, request, "UPDATE", "GUIDE", id, "修改攻略");
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        guideMapper.deleteById(id);
        logOperation(session, request, "DELETE", "GUIDE", id, "删除攻略");
        return ApiResponse.success();
    }

    private void logOperation(HttpSession session, HttpServletRequest request,
                              String action, String targetType, Long targetId, String detail) {
        Map<String, Object> admin = adminAuthService.getCurrentAdmin(session);
        if (admin == null) return;
        AdminLog log = new AdminLog();
        log.setAdminId((Long) admin.get("id"));
        log.setAdminUsername((String) admin.get("username"));
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        log.setIpAddress(request.getRemoteAddr());
        adminLogMapper.insert(log);
    }
}
```

- [ ] **Step 2: 创建 AdminStoryApiController.java**

```java
package com.travel.controller.admin.api;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.mapper.AdminLogMapper;
import com.travel.mapper.GuideStoryMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.pojo.vo.GuideStoryVO;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/api/stories")
@RequiredArgsConstructor
public class AdminStoryApiController {

    private final GuideStoryMapper storyMapper;
    private final AdminLogMapper adminLogMapper;
    private final AdminAuthService adminAuthService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public ApiResponse<PageResult<GuideStoryVO>> list(@RequestParam(defaultValue = "1") int page) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = storyMapper.countAll();
        return ApiResponse.success(PageResult.of(storyMapper.selectAll(offset, PAGE_SIZE), page, PAGE_SIZE, total));
    }

    @GetMapping("/{id}")
    public ApiResponse<GuideStoryVO> detail(@PathVariable Long id) {
        GuideStoryVO story = storyMapper.selectById(id);
        if (story == null) return ApiResponse.fail("NOT_FOUND", "故事不存在");
        return ApiResponse.success(story);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        storyMapper.updateContent(id, (String) body.get("content"));
        logOperation(session, request, "UPDATE", "STORY", id, "修改故事");
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        storyMapper.deleteById(id);
        logOperation(session, request, "DELETE", "STORY", id, "删除故事");
        return ApiResponse.success();
    }

    private void logOperation(HttpSession session, HttpServletRequest request,
                              String action, String targetType, Long targetId, String detail) {
        Map<String, Object> admin = adminAuthService.getCurrentAdmin(session);
        if (admin == null) return;
        AdminLog log = new AdminLog();
        log.setAdminId((Long) admin.get("id"));
        log.setAdminUsername((String) admin.get("username"));
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        log.setIpAddress(request.getRemoteAddr());
        adminLogMapper.insert(log);
    }
}
```

- [ ] **Step 3: 创建 AdminDestinationApiController.java**

```java
package com.travel.controller.admin.api;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.mapper.AdminLogMapper;
import com.travel.mapper.DestinationMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.pojo.model.Destination;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/api/destinations")
@RequiredArgsConstructor
public class AdminDestinationApiController {

    private final DestinationMapper destinationMapper;
    private final AdminLogMapper adminLogMapper;
    private final AdminAuthService adminAuthService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public ApiResponse<PageResult<Destination>> list(@RequestParam(defaultValue = "1") int page) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = destinationMapper.countAll();
        return ApiResponse.success(PageResult.of(destinationMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total));
    }

    @GetMapping("/{id}")
    public ApiResponse<Destination> detail(@PathVariable Long id) {
        Destination dest = destinationMapper.selectById(id);
        if (dest == null) return ApiResponse.fail("NOT_FOUND", "目的地不存在");
        return ApiResponse.success(dest);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    HttpSession session,
                                    HttpServletRequest request) {
        destinationMapper.updateDestination(id,
            (String) body.get("name"), (String) body.get("country"),
            (String) body.get("city"), (String) body.get("description"),
            (String) body.get("coverImageUrl"));
        logOperation(session, request, "UPDATE", "DESTINATION", id, "修改目的地");
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        destinationMapper.deleteById(id);
        logOperation(session, request, "DELETE", "DESTINATION", id, "删除目的地");
        return ApiResponse.success();
    }

    private void logOperation(HttpSession session, HttpServletRequest request,
                              String action, String targetType, Long targetId, String detail) {
        Map<String, Object> admin = adminAuthService.getCurrentAdmin(session);
        if (admin == null) return;
        AdminLog log = new AdminLog();
        log.setAdminId((Long) admin.get("id"));
        log.setAdminUsername((String) admin.get("username"));
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        log.setIpAddress(request.getRemoteAddr());
        adminLogMapper.insert(log);
    }
}
```

- [ ] **Step 4: 创建 AdminCommentApiController.java**

```java
package com.travel.controller.admin.api;

import com.travel.common.ApiResponse;
import com.travel.common.PageResult;
import com.travel.mapper.AdminLogMapper;
import com.travel.mapper.GuideCommentMapper;
import com.travel.pojo.model.AdminLog;
import com.travel.pojo.model.GuideComment;
import com.travel.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/api/comments")
@RequiredArgsConstructor
public class AdminCommentApiController {

    private final GuideCommentMapper commentMapper;
    private final AdminLogMapper adminLogMapper;
    private final AdminAuthService adminAuthService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public ApiResponse<PageResult<GuideComment>> list(@RequestParam(defaultValue = "1") int page) {
        int offset = (page - 1) * PAGE_SIZE;
        long total = commentMapper.countAll();
        return ApiResponse.success(PageResult.of(commentMapper.selectPage(offset, PAGE_SIZE), page, PAGE_SIZE, total));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    HttpSession session,
                                    HttpServletRequest request) {
        commentMapper.deleteById(id);
        logOperation(session, request, "DELETE", "COMMENT", id, "删除评论");
        return ApiResponse.success();
    }

    private void logOperation(HttpSession session, HttpServletRequest request,
                              String action, String targetType, Long targetId, String detail) {
        Map<String, Object> admin = adminAuthService.getCurrentAdmin(session);
        if (admin == null) return;
        AdminLog log = new AdminLog();
        log.setAdminId((Long) admin.get("id"));
        log.setAdminUsername((String) admin.get("username"));
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        log.setIpAddress(request.getRemoteAddr());
        adminLogMapper.insert(log);
    }
}
```

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/travel/controller/admin/api/
git commit -m "feat(admin): add guide, story, destination, comment API endpoints"
```

---

## Task 8: 前端页面 — 仪表盘

**Files:**
- Create: `src/main/resources/static/admin/js/pages/dashboard.js`

- [ ] **Step 1: 创建 dashboard.js**

```javascript
async function initDashboard() {
  const content = document.getElementById('content');
  try {
    const stats = await API.get('/admin/api/stats');
    content.innerHTML = `
      <div class="grid grid-4">
        <div class="card stat-card">
          <div class="stat-card-header">
            <div class="stat-card-icon blue">${Icons.users}</div>
          </div>
          <div class="stat-card-value">${stats.userCount}</div>
          <div class="stat-card-label">用户总数</div>
        </div>
        <div class="card stat-card">
          <div class="stat-card-header">
            <div class="stat-card-icon green">${Icons.guide}</div>
          </div>
          <div class="stat-card-value">${stats.guideCount}</div>
          <div class="stat-card-label">攻略总数</div>
        </div>
        <div class="card stat-card">
          <div class="stat-card-header">
            <div class="stat-card-icon yellow">${Icons.story}</div>
          </div>
          <div class="stat-card-value">${stats.storyCount}</div>
          <div class="stat-card-label">故事总数</div>
        </div>
        <div class="card stat-card">
          <div class="stat-card-header">
            <div class="stat-card-icon red">${Icons.destination}</div>
          </div>
          <div class="stat-card-value">${stats.destinationCount}</div>
          <div class="stat-card-label">目的地总数</div>
        </div>
      </div>
      <div class="grid grid-2 dashboard-charts">
        <div class="card chart-card">
          <div class="card-title">内容分布</div>
          <canvas id="doughnutChart" height="200"></canvas>
        </div>
        <div class="card chart-card">
          <div class="card-title">数据概览</div>
          <canvas id="barChart" height="200"></canvas>
        </div>
      </div>`;
    renderDoughnutChart(stats);
    renderBarChart(stats);
  } catch (err) {
    content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`;
  }
}

function renderDoughnutChart(stats) {
  const ctx = document.getElementById('doughnutChart');
  if (!ctx) return;
  new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: ['攻略', '故事', '目的地', '评论'],
      datasets: [{
        data: [stats.guideCount, stats.storyCount, stats.destinationCount, stats.commentCount],
        backgroundColor: ['#3b82f6', '#22c55e', '#eab308', '#ef4444'],
        borderWidth: 0
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { position: 'bottom', labels: { padding: 16 } } },
      cutout: '65%'
    }
  });
}

function renderBarChart(stats) {
  const ctx = document.getElementById('barChart');
  if (!ctx) return;
  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: ['用户', '攻略', '故事', '目的地', '评论'],
      datasets: [{
        data: [stats.userCount, stats.guideCount, stats.storyCount, stats.destinationCount, stats.commentCount],
        backgroundColor: ['#3b82f6', '#22c55e', '#eab308', '#ef4444', '#8b5cf6'],
        borderRadius: 6,
        barThickness: 32
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { display: false } },
      scales: { y: { beginAtZero: true, grid: { color: '#f1f5f9' } }, x: { grid: { display: false } } }
    }
  });
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/static/admin/js/pages/dashboard.js
git commit -m "feat(admin): add dashboard page with Chart.js"
```

---

## Task 9: 前端页面 — 用户管理

**Files:**
- Create: `src/main/resources/static/admin/js/pages/users.js`

- [ ] **Step 1: 创建 users.js**

```javascript
async function initUsers(page = 1) {
  const content = document.getElementById('content');
  try {
    const result = await API.get(`/admin/api/users?page=${page}`);
    const rows = result.list.map(u => `
      <tr>
        <td>${u.id}</td>
        <td>${u.username}</td>
        <td>${u.phone || '-'}</td>
        <td>${u.email || '-'}</td>
        <td><span class="badge ${u.status === 1 ? 'badge-success' : 'badge-danger'}">${u.status === 1 ? '正常' : '禁用'}</span></td>
        <td>${u.createdAt || '-'}</td>
        <td>
          <button class="btn-link" onclick="Router.navigate('/users/edit/${u.id}')">编辑</button>
          <button class="btn-link danger" onclick="deleteUser(${u.id}, '${u.username}')">删除</button>
        </td>
      </tr>`).join('');
    content.innerHTML = `
      <div class="page-header">
        <div class="page-title">用户管理</div>
      </div>
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
      btn.addEventListener('click', () => initUsers(parseInt(btn.dataset.page)));
    });
  } catch (err) {
    content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`;
  }
}

async function deleteUser(id, name) {
  if (!await confirmDialog(`确认删除用户「${name}」？`)) return;
  try {
    await API.delete(`/admin/api/users/${id}`);
    Toast.success('删除成功');
    initUsers();
  } catch (err) { Toast.error(err.message); }
}

async function initUserEdit(id) {
  const content = document.getElementById('content');
  try {
    const user = await API.get(`/admin/api/users/${id}`);
    content.innerHTML = `
      <div class="page-header">
        <div class="page-title">编辑用户</div>
        <button class="btn btn-ghost" onclick="Router.navigate('/users')">返回列表</button>
      </div>
      <div class="card edit-card">
        <div class="card-body">
          <form id="editForm">
            <div class="form-group">
              <label class="form-label">用户名</label>
              <input type="text" name="username" class="form-input" value="${user.username || ''}" required>
            </div>
            <div class="form-group">
              <label class="form-label">手机号</label>
              <input type="text" name="phone" class="form-input" value="${user.phone || ''}">
            </div>
            <div class="form-group">
              <label class="form-label">邮箱</label>
              <input type="email" name="email" class="form-input" value="${user.email || ''}">
            </div>
            <div class="form-group">
              <label class="form-label">状态</label>
              <select name="status" class="form-select">
                <option value="1" ${user.status === 1 ? 'selected' : ''}>正常</option>
                <option value="0" ${user.status === 0 ? 'selected' : ''}>禁用</option>
              </select>
            </div>
            <button type="submit" class="btn btn-primary">保存</button>
          </form>
        </div>
      </div>`;
    document.getElementById('editForm').addEventListener('submit', async (e) => {
      e.preventDefault();
      const fd = new FormData(e.target);
      const body = { username: fd.get('username'), phone: fd.get('phone'), email: fd.get('email'), status: parseInt(fd.get('status')) };
      try {
        await API.put(`/admin/api/users/${id}`, body);
        Toast.success('保存成功');
        Router.navigate('/users');
      } catch (err) { Toast.error(err.message); }
    });
  } catch (err) {
    content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`;
  }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/static/admin/js/pages/users.js
git commit -m "feat(admin): add users list and edit page"
```

---

## Task 10: 前端页面 — 攻略/故事/目的地/评论管理

**Files:**
- Create: `src/main/resources/static/admin/js/pages/guides.js`
- Create: `src/main/resources/static/admin/js/pages/stories.js`
- Create: `src/main/resources/static/admin/js/pages/destinations.js`
- Create: `src/main/resources/static/admin/js/pages/comments.js`

- [ ] **Step 1: 创建 guides.js**

```javascript
async function initGuides(page = 1) {
  const content = document.getElementById('content');
  try {
    const result = await API.get(`/admin/api/guides?page=${page}`);
    const rows = result.list.map(g => `
      <tr>
        <td>${g.id}</td>
        <td>${g.title}</td>
        <td>${g.authorName || '-'}</td>
        <td>${g.destinationName || '-'}</td>
        <td>${g.likesCount || 0}</td>
        <td>${g.publishedAt || '-'}</td>
        <td>
          <button class="btn-link" onclick="Router.navigate('/guides/edit/${g.id}')">编辑</button>
          <button class="btn-link danger" onclick="deleteGuide(${g.id}, '${(g.title||'').replace(/'/g,"\\'")}')">删除</button>
        </td>
      </tr>`).join('');
    content.innerHTML = `
      <div class="page-header"><div class="page-title">攻略管理</div></div>
      <div class="card">
        <div class="table-wrapper">
          <table>
            <thead><tr><th>ID</th><th>标题</th><th>作者</th><th>目的地</th><th>点赞</th><th>发布时间</th><th>操作</th></tr></thead>
            <tbody>${rows || '<tr><td colspan="7" class="empty">暂无数据</td></tr>'}</tbody>
          </table>
        </div>
        <div class="card-body">${renderPagination(result.page, result.totalPages)}</div>
      </div>`;
    content.querySelectorAll('.page-btn:not([disabled])').forEach(btn => {
      btn.addEventListener('click', () => initGuides(parseInt(btn.dataset.page)));
    });
  } catch (err) {
    content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`;
  }
}

async function deleteGuide(id, title) {
  if (!await confirmDialog(`确认删除攻略「${title}」？`)) return;
  try { await API.delete(`/admin/api/guides/${id}`); Toast.success('删除成功'); initGuides(); }
  catch (err) { Toast.error(err.message); }
}

async function initGuideEdit(id) {
  const content = document.getElementById('content');
  try {
    const guide = await API.get(`/admin/api/guides/${id}`);
    content.innerHTML = `
      <div class="page-header">
        <div class="page-title">编辑攻略</div>
        <button class="btn btn-ghost" onclick="Router.navigate('/guides')">返回列表</button>
      </div>
      <div class="card edit-card"><div class="card-body">
        <form id="editForm">
          <div class="form-group"><label class="form-label">标题</label><input type="text" name="title" class="form-input" value="${guide.title || ''}" required></div>
          <div class="form-group"><label class="form-label">摘要</label><textarea name="summary" class="form-textarea">${guide.summary || ''}</textarea></div>
          <button type="submit" class="btn btn-primary">保存</button>
        </form>
      </div></div>`;
    document.getElementById('editForm').addEventListener('submit', async (e) => {
      e.preventDefault();
      const fd = new FormData(e.target);
      try { await API.put(`/admin/api/guides/${id}`, { title: fd.get('title'), summary: fd.get('summary') }); Toast.success('保存成功'); Router.navigate('/guides'); }
      catch (err) { Toast.error(err.message); }
    });
  } catch (err) { content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`; }
}
```

- [ ] **Step 2: 创建 stories.js**

```javascript
async function initStories(page = 1) {
  const content = document.getElementById('content');
  try {
    const result = await API.get(`/admin/api/stories?page=${page}`);
    const rows = result.list.map(s => `
      <tr>
        <td>${s.id}</td>
        <td>${(s.content || '').substring(0, 50)}${(s.content || '').length > 50 ? '...' : ''}</td>
        <td>${s.authorName || '-'}</td>
        <td>${s.createdAt || '-'}</td>
        <td>
          <button class="btn-link" onclick="Router.navigate('/stories/edit/${s.id}')">编辑</button>
          <button class="btn-link danger" onclick="deleteStory(${s.id})">删除</button>
        </td>
      </tr>`).join('');
    content.innerHTML = `
      <div class="page-header"><div class="page-title">故事管理</div></div>
      <div class="card">
        <div class="table-wrapper">
          <table>
            <thead><tr><th>ID</th><th>内容</th><th>作者</th><th>发布时间</th><th>操作</th></tr></thead>
            <tbody>${rows || '<tr><td colspan="5" class="empty">暂无数据</td></tr>'}</tbody>
          </table>
        </div>
        <div class="card-body">${renderPagination(result.page, result.totalPages)}</div>
      </div>`;
    content.querySelectorAll('.page-btn:not([disabled])').forEach(btn => {
      btn.addEventListener('click', () => initStories(parseInt(btn.dataset.page)));
    });
  } catch (err) { content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`; }
}

async function deleteStory(id) {
  if (!await confirmDialog('确认删除该故事？')) return;
  try { await API.delete(`/admin/api/stories/${id}`); Toast.success('删除成功'); initStories(); }
  catch (err) { Toast.error(err.message); }
}

async function initStoryEdit(id) {
  const content = document.getElementById('content');
  try {
    const story = await API.get(`/admin/api/stories/${id}`);
    content.innerHTML = `
      <div class="page-header">
        <div class="page-title">编辑故事</div>
        <button class="btn btn-ghost" onclick="Router.navigate('/stories')">返回列表</button>
      </div>
      <div class="card edit-card"><div class="card-body">
        <form id="editForm">
          <div class="form-group"><label class="form-label">内容</label><textarea name="content" class="form-textarea" rows="10">${story.content || ''}</textarea></div>
          <button type="submit" class="btn btn-primary">保存</button>
        </form>
      </div></div>`;
    document.getElementById('editForm').addEventListener('submit', async (e) => {
      e.preventDefault();
      const fd = new FormData(e.target);
      try { await API.put(`/admin/api/stories/${id}`, { content: fd.get('content') }); Toast.success('保存成功'); Router.navigate('/stories'); }
      catch (err) { Toast.error(err.message); }
    });
  } catch (err) { content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`; }
}
```

- [ ] **Step 3: 创建 destinations.js**

```javascript
async function initDestinations(page = 1) {
  const content = document.getElementById('content');
  try {
    const result = await API.get(`/admin/api/destinations?page=${page}`);
    const rows = result.list.map(d => `
      <tr>
        <td>${d.id}</td>
        <td>${d.name}</td>
        <td>${d.country || '-'}</td>
        <td>${d.city || '-'}</td>
        <td>${d.guidesCount || 0}</td>
        <td>
          <button class="btn-link" onclick="Router.navigate('/destinations/edit/${d.id}')">编辑</button>
          <button class="btn-link danger" onclick="deleteDestination(${d.id}, '${(d.name||'').replace(/'/g,"\\'")}')">删除</button>
        </td>
      </tr>`).join('');
    content.innerHTML = `
      <div class="page-header"><div class="page-title">目的地管理</div></div>
      <div class="card">
        <div class="table-wrapper">
          <table>
            <thead><tr><th>ID</th><th>名称</th><th>国家</th><th>城市</th><th>攻略数</th><th>操作</th></tr></thead>
            <tbody>${rows || '<tr><td colspan="6" class="empty">暂无数据</td></tr>'}</tbody>
          </table>
        </div>
        <div class="card-body">${renderPagination(result.page, result.totalPages)}</div>
      </div>`;
    content.querySelectorAll('.page-btn:not([disabled])').forEach(btn => {
      btn.addEventListener('click', () => initDestinations(parseInt(btn.dataset.page)));
    });
  } catch (err) { content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`; }
}

async function deleteDestination(id, name) {
  if (!await confirmDialog(`确认删除目的地「${name}」？`)) return;
  try { await API.delete(`/admin/api/destinations/${id}`); Toast.success('删除成功'); initDestinations(); }
  catch (err) { Toast.error(err.message); }
}

async function initDestinationEdit(id) {
  const content = document.getElementById('content');
  try {
    const dest = await API.get(`/admin/api/destinations/${id}`);
    content.innerHTML = `
      <div class="page-header">
        <div class="page-title">编辑目的地</div>
        <button class="btn btn-ghost" onclick="Router.navigate('/destinations')">返回列表</button>
      </div>
      <div class="card edit-card"><div class="card-body">
        <form id="editForm">
          <div class="form-group"><label class="form-label">名称</label><input type="text" name="name" class="form-input" value="${dest.name || ''}" required></div>
          <div class="form-group"><label class="form-label">国家</label><input type="text" name="country" class="form-input" value="${dest.country || ''}"></div>
          <div class="form-group"><label class="form-label">城市</label><input type="text" name="city" class="form-input" value="${dest.city || ''}"></div>
          <div class="form-group"><label class="form-label">描述</label><textarea name="description" class="form-textarea">${dest.description || ''}</textarea></div>
          <div class="form-group"><label class="form-label">封面图URL</label><input type="text" name="coverImageUrl" class="form-input" value="${dest.coverImageUrl || ''}"></div>
          <button type="submit" class="btn btn-primary">保存</button>
        </form>
      </div></div>`;
    document.getElementById('editForm').addEventListener('submit', async (e) => {
      e.preventDefault();
      const fd = new FormData(e.target);
      const body = {};
      fd.forEach((v, k) => body[k] = v);
      try { await API.put(`/admin/api/destinations/${id}`, body); Toast.success('保存成功'); Router.navigate('/destinations'); }
      catch (err) { Toast.error(err.message); }
    });
  } catch (err) { content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`; }
}
```

- [ ] **Step 4: 创建 comments.js**

```javascript
async function initComments(page = 1) {
  const content = document.getElementById('content');
  try {
    const result = await API.get(`/admin/api/comments?page=${page}`);
    const rows = result.list.map(c => `
      <tr>
        <td>${c.id}</td>
        <td>${(c.content || '').substring(0, 60)}${(c.content || '').length > 60 ? '...' : ''}</td>
        <td>${c.authorName || '-'}</td>
        <td>${c.guideId || '-'}</td>
        <td>${c.createdAt || '-'}</td>
        <td>
          <button class="btn-link danger" onclick="deleteComment(${c.id})">删除</button>
        </td>
      </tr>`).join('');
    content.innerHTML = `
      <div class="page-header"><div class="page-title">评论管理</div></div>
      <div class="card">
        <div class="table-wrapper">
          <table>
            <thead><tr><th>ID</th><th>内容</th><th>作者</th><th>攻略ID</th><th>时间</th><th>操作</th></tr></thead>
            <tbody>${rows || '<tr><td colspan="6" class="empty">暂无数据</td></tr>'}</tbody>
          </table>
        </div>
        <div class="card-body">${renderPagination(result.page, result.totalPages)}</div>
      </div>`;
    content.querySelectorAll('.page-btn:not([disabled])').forEach(btn => {
      btn.addEventListener('click', () => initComments(parseInt(btn.dataset.page)));
    });
  } catch (err) { content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`; }
}

async function deleteComment(id) {
  if (!await confirmDialog('确认删除该评论？')) return;
  try { await API.delete(`/admin/api/comments/${id}`); Toast.success('删除成功'); initComments(); }
  catch (err) { Toast.error(err.message); }
}
```

- [ ] **Step 5: Commit**

```bash
git add src/main/resources/static/admin/js/pages/
git commit -m "feat(admin): add guide, story, destination, comment pages"
```

---

## Task 11: 验证与清理

- [ ] **Step 1: 启动应用验证**

```bash
cd E:/JavaFile/project/TravelProject
./mvnw spring-boot:run
```

访问 `http://localhost:8082/admin/login.html` 验证登录页。
登录后验证仪表盘、各管理页面的加载和 CRUD 操作。

- [ ] **Step 2: 检查 AdminAuthService 方法**

确保 `AdminAuthService` 有 `login(HttpSession, String, String)` 方法。如果没有，读取该文件并添加。

- [ ] **Step 3: 最终 commit**

```bash
git add -A
git commit -m "feat(admin): complete SPA frontend redesign with professional UI"
```
