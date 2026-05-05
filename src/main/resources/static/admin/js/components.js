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
