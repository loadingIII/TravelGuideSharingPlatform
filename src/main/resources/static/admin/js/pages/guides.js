const GUIDE_STATUS_MAP = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已下架' };
const GUIDE_STATUS_BADGE = { 0: 'badge-warning', 1: 'badge-success', 2: 'badge-danger', 3: 'badge-info' };

async function initGuides(page = 1, status = null) {
  const content = document.getElementById('content');
  try {
    const statusParam = status !== null ? `&status=${status}` : '';
    const result = await API.get(`/admin/guides?page=${page}${statusParam}`);
    const rows = result.list.map(g => `
      <tr>
        <td>${g.id}</td>
        <td>${g.title}</td>
        <td>${g.authorName || '-'}</td>
        <td>${g.destinationName || '-'}</td>
        <td><span class="badge ${GUIDE_STATUS_BADGE[g.status] || 'badge-info'}">${GUIDE_STATUS_MAP[g.status] || '未知'}</span></td>
        <td>${g.publishedAt || '-'}</td>
        <td>
          ${g.status === 0 ? `
            <button class="btn-link" onclick="auditGuide(${g.id}, 'approve')">通过</button>
            <button class="btn-link danger" onclick="auditGuide(${g.id}, 'reject')">拒绝</button>
          ` : ''}
          ${g.status === 1 ? `
            <button class="btn-link danger" onclick="auditGuide(${g.id}, 'down')">下架</button>
          ` : ''}
          ${g.status === 2 || g.status === 3 ? `
            <button class="btn-link" onclick="auditGuide(${g.id}, 'approve')">通过</button>
          ` : ''}
          <button class="btn-link danger" onclick="deleteGuide(${g.id}, '${(g.title||'').replace(/'/g,"\\'")}')">删除</button>
        </td>
      </tr>`).join('');

    const filterButtons = `
      <div class="filter-bar">
        <button class="btn btn-sm ${status === null ? 'btn-primary' : 'btn-ghost'}" onclick="initGuides(1, null)">全部</button>
        <button class="btn btn-sm ${status === 0 ? 'btn-primary' : 'btn-ghost'}" onclick="initGuides(1, 0)">待审核</button>
        <button class="btn btn-sm ${status === 1 ? 'btn-primary' : 'btn-ghost'}" onclick="initGuides(1, 1)">已通过</button>
        <button class="btn btn-sm ${status === 2 ? 'btn-primary' : 'btn-ghost'}" onclick="initGuides(1, 2)">已拒绝</button>
        <button class="btn btn-sm ${status === 3 ? 'btn-primary' : 'btn-ghost'}" onclick="initGuides(1, 3)">已下架</button>
      </div>`;

    content.innerHTML = `
      <div class="page-header"><div class="page-title">攻略管理</div></div>
      ${filterButtons}
      <div class="card">
        <div class="table-wrapper">
          <table>
            <thead><tr><th>ID</th><th>标题</th><th>作者</th><th>目的地</th><th>状态</th><th>发布时间</th><th>操作</th></tr></thead>
            <tbody>${rows || '<tr><td colspan="7" class="empty">暂无数据</td></tr>'}</tbody>
          </table>
        </div>
        <div class="card-body">${renderPagination(result.page, result.totalPages)}</div>
      </div>`;
    content.querySelectorAll('.page-btn:not([disabled])').forEach(btn => {
      btn.addEventListener('click', () => initGuides(parseInt(btn.dataset.page), status));
    });
  } catch (err) {
    content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`;
  }
}

async function auditGuide(id, action) {
  const actionMap = { approve: '审核通过', reject: '审核拒绝', down: '下架' };
  if (!await confirmDialog(`确认${actionMap[action]}该攻略？`)) return;
  try { await API.post(`/admin/guides/${id}/audit`, { action }); Toast.success('操作成功'); initGuides(); }
  catch (err) { Toast.error(err.message); }
}

async function deleteGuide(id, title) {
  if (!await confirmDialog(`确认删除攻略「${title}」？`)) return;
  try { await API.delete(`/admin/guides/${id}`); Toast.success('删除成功'); initGuides(); }
  catch (err) { Toast.error(err.message); }
}
