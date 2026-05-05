async function initStories(page = 1) {
  const content = document.getElementById('content');
  try {
    const result = await API.get(`/admin/stories?page=${page}`);
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
  try { await API.delete(`/admin/stories/${id}`); Toast.success('删除成功'); initStories(); }
  catch (err) { Toast.error(err.message); }
}

async function initStoryEdit(id) {
  const content = document.getElementById('content');
  try {
    const story = await API.get(`/admin/stories/${id}`);
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
      try { await API.put(`/admin/stories/${id}`, { content: fd.get('content') }); Toast.success('保存成功'); Router.navigate('/stories'); }
      catch (err) { Toast.error(err.message); }
    });
  } catch (err) { content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`; }
}
