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
          <button class="btn-link" onclick="Router.navigate('/users/detail/${u.id}')">详情</button>
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

async function initUserDetail(id) {
  const content = document.getElementById('content');
  try {
    const user = await API.get(`/admin/users/${id}`);
    const avatarHtml = user.avatarUrl
      ? `<img src="${user.avatarUrl}" alt="头像" class="detail-avatar">`
      : `<div class="detail-avatar detail-avatar-placeholder">${(user.username || '?')[0].toUpperCase()}</div>`;
    content.innerHTML = `
      <div class="card detail-card">
        <div class="detail-header">
          <div class="detail-header-left">
            ${avatarHtml}
            <div>
              <div class="detail-header-title">${user.nickname || user.username || '用户详情'}</div>
              <div class="detail-header-id">ID: ${user.id} ${user.isVip ? '&middot; <span style="color:var(--color-accent)">VIP</span>' : ''}</div>
            </div>
          </div>
          <button class="btn btn-ghost btn-sm" onclick="Router.navigate('/users')">&larr; 返回列表</button>
        </div>
        <div class="detail-body">
          <div class="detail-row"><div class="detail-row-label">用户名</div><div class="detail-row-value">${user.username || '-'}</div></div>
          <div class="detail-row"><div class="detail-row-label">昵称</div><div class="detail-row-value ${user.nickname ? '' : 'muted'}">${user.nickname || '未设置'}</div></div>
          <div class="detail-row"><div class="detail-row-label">手机号</div><div class="detail-row-value">${user.phone || '-'}</div></div>
          <div class="detail-row"><div class="detail-row-label">邮箱</div><div class="detail-row-value">${user.email || '-'}</div></div>
          <div class="detail-row"><div class="detail-row-label">简介</div><div class="detail-row-value ${user.bio ? '' : 'muted'}" style="white-space:pre-wrap">${user.bio || '暂无简介'}</div></div>
          <div class="detail-row"><div class="detail-row-label">状态</div><div class="detail-row-value"><span class="badge ${USER_STATUS_BADGE[user.status] || 'badge-info'}">${USER_STATUS_MAP[user.status] || '未知'}</span></div></div>
          <div class="detail-row"><div class="detail-row-label">最后登录</div><div class="detail-row-value ${user.lastLoginAt ? '' : 'muted'}">${user.lastLoginAt || '暂无记录'}</div></div>
          <div class="detail-row"><div class="detail-row-label">注册时间</div><div class="detail-row-value">${user.createdAt || '-'}</div></div>
        </div>
      </div>`;
  } catch (err) { content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`; }
}


