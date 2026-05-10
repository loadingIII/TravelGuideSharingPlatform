async function initDestinations(page = 1) {
  const content = document.getElementById('content');
  try {
    const result = await API.get(`/admin/destinations?page=${page}`);
    const rows = result.list.map(d => `
      <tr>
        <td>${d.id}</td>
        <td>${d.name}</td>
        <td>${d.country || '-'}</td>
        <td>${d.city || '-'}</td>
        <td>${d.guidesCount || 0}</td>
        <td>
          <button class="btn-link" onclick="Router.navigate('/destinations/detail/${d.id}')">详情</button>
          <button class="btn-link" onclick="Router.navigate('/destinations/edit/${d.id}')">编辑</button>
          <button class="btn-link danger" onclick="deleteDestination(${d.id}, '${(d.name||'').replace(/'/g,"\\'")}')">删除</button>
        </td>
      </tr>`).join('');
    content.innerHTML = `
      <div class="page-header">
        <div class="page-title">目的地管理</div>
        <button class="btn btn-primary btn-sm" onclick="Router.navigate('/destinations/create')">新增目的地</button>
      </div>
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
  try { await API.delete(`/admin/destinations/${id}`); Toast.success('删除成功'); initDestinations(); }
  catch (err) { Toast.error(err.message); }
}

async function initDestinationDetail(id) {
  const content = document.getElementById('content');
  try {
    const dest = await API.get(`/admin/destinations/${id}`);
    const coverHtml = dest.coverImageUrl
      ? `<div class="detail-cover"><img src="${dest.coverImageUrl}" alt="封面"></div>`
      : '';
    content.innerHTML = `
      <div class="card detail-card">
        ${coverHtml}
        <div class="detail-header">
          <div>
            <div class="detail-header-title">${dest.name || '目的地详情'}</div>
            <div class="detail-header-id">ID: ${dest.id} &middot; ${dest.country || ''} ${dest.city || ''}</div>
          </div>
          <button class="btn btn-ghost btn-sm" onclick="Router.navigate('/destinations')">&larr; 返回列表</button>
        </div>
        <div class="detail-body">
          <div class="detail-row"><div class="detail-row-label">名称</div><div class="detail-row-value">${dest.name || '-'}</div></div>
          <div class="detail-row"><div class="detail-row-label">国家</div><div class="detail-row-value">${dest.country || '-'}</div></div>
          <div class="detail-row"><div class="detail-row-label">城市</div><div class="detail-row-value">${dest.city || '-'}</div></div>
          <div class="detail-row"><div class="detail-row-label">描述</div><div class="detail-row-value" style="white-space:pre-wrap;line-height:1.8">${dest.description || '-'}</div></div>
        </div>
        <div class="detail-stats">
          <div class="detail-stat"><div class="detail-stat-value">${dest.guidesCount || 0}</div><div class="detail-stat-label">攻略数</div></div>
        </div>
      </div>`;
  } catch (err) { content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`; }
}

async function initDestinationEdit(id) {
  const content = document.getElementById('content');
  try {
    const dest = await API.get(`/admin/destinations/${id}`);
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
      try { await API.put(`/admin/destinations/${id}`, body); Toast.success('保存成功'); Router.navigate('/destinations'); }
      catch (err) { Toast.error(err.message); }
    });
  } catch (err) { content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`; }
}

async function initDestinationCreate() {
  const content = document.getElementById('content');
  content.innerHTML = `
    <div class="page-header">
      <div class="page-title">新增目的地</div>
      <button class="btn btn-ghost" onclick="Router.navigate('/destinations')">返回列表</button>
    </div>
    <div class="card edit-card"><div class="card-body">
      <form id="createForm">
        <div class="form-group"><label class="form-label">名称</label><input type="text" name="name" class="form-input" required></div>
        <div class="form-group"><label class="form-label">国家</label><input type="text" name="country" class="form-input"></div>
        <div class="form-group"><label class="form-label">城市</label><input type="text" name="city" class="form-input"></div>
        <div class="form-group"><label class="form-label">描述</label><textarea name="description" class="form-textarea"></textarea></div>
        <div class="form-group"><label class="form-label">封面图URL</label><input type="text" name="coverImageUrl" class="form-input"></div>
        <button type="submit" class="btn btn-primary">创建</button>
      </form>
    </div></div>`;
  document.getElementById('createForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    const body = {};
    fd.forEach((v, k) => body[k] = v);
    try { await API.post('/admin/destinations', body); Toast.success('创建成功'); Router.navigate('/destinations'); }
    catch (err) { Toast.error(err.message); }
  });
}
