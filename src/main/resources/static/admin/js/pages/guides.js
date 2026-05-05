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
