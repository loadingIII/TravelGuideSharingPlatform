const COMMENT_STATUS_MAP = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已下架' };
const COMMENT_STATUS_BADGE = { 0: 'badge-warning', 1: 'badge-success', 2: 'badge-danger', 3: 'badge-info' };

async function initComments(page = 1, status = null) {
  const content = document.getElementById('content');
  try {
    const statusParam = status !== null ? `&status=${status}` : '';
    const result = await API.get(`/admin/comments?page=${page}${statusParam}`);
    const rows = result.list.map(c => `
      <tr>
        <td>${c.id}</td>
        <td>${(c.content || '').substring(0, 60)}${(c.content || '').length > 60 ? '...' : ''}</td>
        <td>${c.authorName || '-'}</td>
        <td>${c.guideId || '-'}</td>
        <td><span class="badge ${COMMENT_STATUS_BADGE[c.status] || 'badge-info'}">${COMMENT_STATUS_MAP[c.status] || '未知'}</span></td>
        <td>${c.createdAt || '-'}</td>
        <td>
          ${c.status === 0 ? `
            <button class="btn-link" onclick="auditComment(${c.id}, 'approve')">通过</button>
            <button class="btn-link danger" onclick="auditComment(${c.id}, 'reject')">拒绝</button>
          ` : ''}
          ${c.status === 1 ? `
            <button class="btn-link danger" onclick="auditComment(${c.id}, 'down')">下架</button>
          ` : ''}
          ${c.status === 2 || c.status === 3 ? `
            <button class="btn-link" onclick="auditComment(${c.id}, 'approve')">通过</button>
          ` : ''}
          <button class="btn-link danger" onclick="deleteComment(${c.id})">删除</button>
        </td>
      </tr>`).join('');

    const filterButtons = `
      <div class="filter-bar">
        <button class="btn btn-sm ${status === null ? 'btn-primary' : 'btn-ghost'}" onclick="initComments(1, null)">全部</button>
        <button class="btn btn-sm ${status === 0 ? 'btn-primary' : 'btn-ghost'}" onclick="initComments(1, 0)">待审核</button>
        <button class="btn btn-sm ${status === 1 ? 'btn-primary' : 'btn-ghost'}" onclick="initComments(1, 1)">已通过</button>
        <button class="btn btn-sm ${status === 2 ? 'btn-primary' : 'btn-ghost'}" onclick="initComments(1, 2)">已拒绝</button>
        <button class="btn btn-sm ${status === 3 ? 'btn-primary' : 'btn-ghost'}" onclick="initComments(1, 3)">已下架</button>
      </div>`;

    content.innerHTML = `
      <div class="page-header"><div class="page-title">评论管理</div></div>
      ${filterButtons}
      <div class="card">
        <div class="table-wrapper">
          <table>
            <thead><tr><th>ID</th><th>内容</th><th>作者</th><th>攻略ID</th><th>状态</th><th>时间</th><th>操作</th></tr></thead>
            <tbody>${rows || '<tr><td colspan="7" class="empty">暂无数据</td></tr>'}</tbody>
          </table>
        </div>
        <div class="card-body">${renderPagination(result.page, result.totalPages)}</div>
      </div>`;
    content.querySelectorAll('.page-btn:not([disabled])').forEach(btn => {
      btn.addEventListener('click', () => initComments(parseInt(btn.dataset.page), status));
    });
  } catch (err) { content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`; }
}

async function auditComment(id, action) {
  const actionMap = { approve: '审核通过', reject: '审核拒绝', down: '下架' };
  if (!await confirmDialog(`确认${actionMap[action]}该评论？`)) return;
  try { await API.post(`/admin/comments/${id}/audit`, { action }); Toast.success('操作成功'); initComments(); }
  catch (err) { Toast.error(err.message); }
}

async function deleteComment(id) {
  if (!await confirmDialog('确认删除该评论？')) return;
  try { await API.delete(`/admin/comments/${id}`); Toast.success('删除成功'); initComments(); }
  catch (err) { Toast.error(err.message); }
}
