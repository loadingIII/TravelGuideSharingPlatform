const ROLE_MAP = { ROOT: '超级管理员', ADMIN: '管理员', EDITOR: '编辑', VIEWER: '查看者' };
const ROLE_BADGE = { ROOT: 'badge-danger', ADMIN: 'badge-warning', EDITOR: 'badge-info', VIEWER: 'badge-gray' };

async function initStaff(page = 1) {
  const content = document.getElementById('content');
  try {
    const result = await API.get(`/admin/admins?page=${page}`);
    const rows = result.list.map(a => `
      <tr>
        <td>${a.id}</td>
        <td>${a.username}</td>
        <td>${a.realName || '-'}</td>
        <td><span class="badge ${ROLE_BADGE[a.role] || 'badge-info'}">${ROLE_MAP[a.role] || a.role}</span></td>
        <td><span class="badge ${a.status === 1 ? 'badge-success' : 'badge-gray'}">${a.status === 1 ? '正常' : '禁用'}</span></td>
        <td>${a.lastLoginAt || '-'}</td>
        <td>
          <button class="btn-link" onclick="openStaffModal(${a.id})">编辑</button>
          <button class="btn-link danger" onclick="deleteStaff(${a.id}, '${a.username}')">删除</button>
        </td>
      </tr>`).join('');

    content.innerHTML = `
      <div class="page-header">
        <div class="page-title">员工管理</div>
        <button class="btn btn-primary" onclick="openStaffModal()">+ 新增员工</button>
      </div>
      <div class="card">
        <div class="table-wrapper">
          <table>
            <thead><tr><th>ID</th><th>用户名</th><th>姓名</th><th>角色</th><th>状态</th><th>最后登录</th><th>操作</th></tr></thead>
            <tbody>${rows || '<tr><td colspan="7" class="empty">暂无数据</td></tr>'}</tbody>
          </table>
        </div>
        <div class="card-body">${renderPagination(result.page, result.totalPages)}</div>
      </div>`;
    content.querySelectorAll('.page-btn:not([disabled])').forEach(btn => {
      btn.addEventListener('click', () => initStaff(parseInt(btn.dataset.page)));
    });
  } catch (err) {
    content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`;
  }
}

async function openStaffModal(id) {
  let staff = { username: '', realName: '', role: 'EDITOR' };
  let title = '新增员工';
  if (id) {
    staff = await API.get(`/admin/admins/${id}`);
    title = '编辑员工';
  }

  const overlay = document.createElement('div');
  overlay.className = 'modal-overlay';
  overlay.innerHTML = `
    <div class="modal" style="max-width:480px">
      <div class="modal-title">${title}</div>
      <form id="staffForm">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input type="text" name="username" class="form-input" value="${staff.username || ''}" ${id ? 'disabled' : 'required'}>
        </div>
        <div class="form-group">
          <label class="form-label">姓名</label>
          <input type="text" name="realName" class="form-input" value="${staff.realName || ''}">
        </div>
        <div class="form-group">
          <label class="form-label">角色</label>
          <select name="role" class="form-select">
            ${Object.entries(ROLE_MAP).map(([k, v]) =>
              `<option value="${k}" ${(staff.role === k) ? 'selected' : ''}>${v}</option>`
            ).join('')}
          </select>
        </div>
        <div class="form-group">
          <label class="form-label">状态</label>
          <select name="status" class="form-select">
            <option value="1" ${staff.status !== 0 ? 'selected' : ''}>正常</option>
            <option value="0" ${staff.status === 0 ? 'selected' : ''}>禁用</option>
          </select>
        </div>
        <div class="form-group">
          <label class="form-label">密码${id ? '（留空不修改）' : ''}</label>
          <input type="password" name="password" class="form-input" ${id ? '' : 'required'}>
        </div>
        <div class="modal-actions">
          <button type="button" class="btn btn-ghost" onclick="this.closest('.modal-overlay').remove()">取消</button>
          <button type="submit" class="btn btn-primary">保存</button>
        </div>
      </form>
    </div>`;
  document.body.appendChild(overlay);
  overlay.addEventListener('click', e => { if (e.target === overlay) overlay.remove(); });

  document.getElementById('staffForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    const body = {};
    fd.forEach((v, k) => body[k] = v);
    if (id) {
      delete body.username;
      if (!body.password || body.password.trim() === '') delete body.password;
    }
    try {
      if (id) {
        await API.put(`/admin/admins/${id}`, body);
      } else {
        await API.post('/admin/admins', body);
      }
      overlay.remove();
      Toast.success(id ? '保存成功' : '新增成功');
      initStaff();
    } catch (err) { Toast.error(err.message); }
  });
}

async function deleteStaff(id, name) {
  if (!await confirmDialog(`确认删除员工「${name}」？`)) return;
  try {
    await API.delete(`/admin/admins/${id}`);
    Toast.success('删除成功');
    initStaff();
  } catch (err) { Toast.error(err.message); }
}
