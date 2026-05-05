async function initUsers(page = 1) {
  const content = document.getElementById('content');
  try {
    const result = await API.get(`/admin/users?page=${page}`);
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
    await API.delete(`/admin/users/${id}`);
    Toast.success('删除成功');
    initUsers();
  } catch (err) { Toast.error(err.message); }
}

async function initUserEdit(id) {
  const content = document.getElementById('content');
  try {
    const user = await API.get(`/admin/users/${id}`);
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
        await API.put(`/admin/users/${id}`, body);
        Toast.success('保存成功');
        Router.navigate('/users');
      } catch (err) { Toast.error(err.message); }
    });
  } catch (err) {
    content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`;
  }
}
