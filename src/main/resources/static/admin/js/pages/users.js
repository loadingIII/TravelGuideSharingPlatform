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
