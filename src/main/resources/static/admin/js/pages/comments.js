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
