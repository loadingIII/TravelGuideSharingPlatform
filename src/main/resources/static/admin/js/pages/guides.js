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
          <button class="btn-link" onclick="Router.navigate('/guides/detail/${g.id}')">详情</button>
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

async function initGuideDetail(id) {
  const content = document.getElementById('content');
  try {
    const g = await API.get(`/admin/guides/${id}`);
    const coverHtml = g.coverImageUrl
      ? `<div class="detail-cover"><img src="${g.coverImageUrl}" alt="封面"></div>`
      : '';
    const avatarHtml = g.authorAvatarUrl
      ? `<img src="${g.authorAvatarUrl}" class="detail-avatar-sm" alt="">`
      : `<div class="detail-avatar-sm detail-avatar-placeholder-sm">${(g.authorName || '?')[0]}</div>`;
    content.innerHTML = `
      <div class="card detail-card">
        ${coverHtml}
        <div class="detail-header">
          <div class="detail-header-left">
            ${avatarHtml}
            <div>
              <div class="detail-header-title">${g.title || '攻略详情'}</div>
              <div class="detail-header-id">ID: ${g.id} &middot; ${g.destinationName || ''}</div>
            </div>
          </div>
          <button class="btn btn-ghost btn-sm" onclick="Router.navigate('/guides')">&larr; 返回列表</button>
        </div>
        <div class="detail-body">
          <div class="detail-row"><div class="detail-row-label">作者</div><div class="detail-row-value">${g.authorName || '-'}</div></div>
          <div class="detail-row"><div class="detail-row-label">目的地</div><div class="detail-row-value">${g.destinationName || '-'}</div></div>
          <div class="detail-row"><div class="detail-row-label">摘要</div><div class="detail-row-value">${g.summary || '-'}</div></div>
          <div class="detail-row"><div class="detail-row-label">出发地</div><div class="detail-row-value ${g.locationText ? '' : 'muted'}">${g.locationText || '未设置'}</div></div>
          <div class="detail-row"><div class="detail-row-label">旅行范围</div><div class="detail-row-value">${g.scope || '-'}</div></div>
          <div class="detail-row"><div class="detail-row-label">旅行方式</div><div class="detail-row-value">${g.travelMode || '-'}</div></div>
          <div class="detail-row"><div class="detail-row-label">天数</div><div class="detail-row-value">${g.days || '-'}</div></div>
          <div class="detail-row"><div class="detail-row-label">预算</div><div class="detail-row-value">${g.budgetTotal || '-'}</div></div>
          <div class="detail-row"><div class="detail-row-label">状态</div><div class="detail-row-value"><span class="badge ${GUIDE_STATUS_BADGE[g.status] || 'badge-info'}">${GUIDE_STATUS_MAP[g.status] || '未知'}</span></div></div>
          <div class="detail-row"><div class="detail-row-label">发布时间</div><div class="detail-row-value">${g.publishedAt || '-'}</div></div>
        </div>
        <div class="detail-stats">
          <div class="detail-stat"><div class="detail-stat-value">${g.viewsCount || 0}</div><div class="detail-stat-label">浏览</div></div>
          <div class="detail-stat"><div class="detail-stat-value">${g.likesCount || 0}</div><div class="detail-stat-label">点赞</div></div>
          <div class="detail-stat"><div class="detail-stat-value">${g.commentsCount || 0}</div><div class="detail-stat-label">评论</div></div>
          <div class="detail-stat"><div class="detail-stat-value">${g.favoritesCount || 0}</div><div class="detail-stat-label">收藏</div></div>
        </div>
      </div>`;
  } catch (err) { content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`; }
}
