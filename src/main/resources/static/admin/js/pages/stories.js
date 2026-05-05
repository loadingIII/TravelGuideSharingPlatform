const STORY_STATUS_MAP = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已下架' };
const STORY_STATUS_BADGE = { 0: 'badge-warning', 1: 'badge-success', 2: 'badge-danger', 3: 'badge-info' };

async function initStories(page = 1, status = null) {
  const content = document.getElementById('content');
  try {
    const statusParam = status !== null ? `&status=${status}` : '';
    const result = await API.get(`/admin/stories?page=${page}${statusParam}`);
    const rows = result.list.map(s => `
      <tr>
        <td>${s.id}</td>
        <td>${(s.content || '').substring(0, 50)}${(s.content || '').length > 50 ? '...' : ''}</td>
        <td>${s.authorName || '-'}</td>
        <td><span class="badge ${STORY_STATUS_BADGE[s.status] || 'badge-info'}">${STORY_STATUS_MAP[s.status] || '未知'}</span></td>
        <td>${s.createdAt || '-'}</td>
        <td>
          <button class="btn-link" onclick="Router.navigate('/stories/detail/${s.id}')">详情</button>
          ${s.status === 0 ? `
            <button class="btn-link" onclick="auditStory(${s.id}, 'approve')">通过</button>
            <button class="btn-link danger" onclick="auditStory(${s.id}, 'reject')">拒绝</button>
          ` : ''}
          ${s.status === 1 ? `
            <button class="btn-link danger" onclick="auditStory(${s.id}, 'down')">下架</button>
          ` : ''}
          ${s.status === 2 || s.status === 3 ? `
            <button class="btn-link" onclick="auditStory(${s.id}, 'approve')">通过</button>
          ` : ''}
          <button class="btn-link danger" onclick="deleteStory(${s.id})">删除</button>
        </td>
      </tr>`).join('');

    const filterButtons = `
      <div class="filter-bar">
        <button class="btn btn-sm ${status === null ? 'btn-primary' : 'btn-ghost'}" onclick="initStories(1, null)">全部</button>
        <button class="btn btn-sm ${status === 0 ? 'btn-primary' : 'btn-ghost'}" onclick="initStories(1, 0)">待审核</button>
        <button class="btn btn-sm ${status === 1 ? 'btn-primary' : 'btn-ghost'}" onclick="initStories(1, 1)">已通过</button>
        <button class="btn btn-sm ${status === 2 ? 'btn-primary' : 'btn-ghost'}" onclick="initStories(1, 2)">已拒绝</button>
        <button class="btn btn-sm ${status === 3 ? 'btn-primary' : 'btn-ghost'}" onclick="initStories(1, 3)">已下架</button>
      </div>`;

    content.innerHTML = `
      <div class="page-header"><div class="page-title">故事管理</div></div>
      ${filterButtons}
      <div class="card">
        <div class="table-wrapper">
          <table>
            <thead><tr><th>ID</th><th>内容</th><th>作者</th><th>状态</th><th>发布时间</th><th>操作</th></tr></thead>
            <tbody>${rows || '<tr><td colspan="6" class="empty">暂无数据</td></tr>'}</tbody>
          </table>
        </div>
        <div class="card-body">${renderPagination(result.page, result.totalPages)}</div>
      </div>`;
    content.querySelectorAll('.page-btn:not([disabled])').forEach(btn => {
      btn.addEventListener('click', () => initStories(parseInt(btn.dataset.page), status));
    });
  } catch (err) { content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`; }
}

async function auditStory(id, action) {
  const actionMap = { approve: '审核通过', reject: '审核拒绝', down: '下架' };
  if (!await confirmDialog(`确认${actionMap[action]}该故事？`)) return;
  try { await API.post(`/admin/stories/${id}/audit`, { action }); Toast.success('操作成功'); initStories(); }
  catch (err) { Toast.error(err.message); }
}

async function deleteStory(id) {
  if (!await confirmDialog('确认删除该故事？')) return;
  try { await API.delete(`/admin/stories/${id}`); Toast.success('删除成功'); initStories(); }
  catch (err) { Toast.error(err.message); }
}

async function initStoryDetail(id) {
  const content = document.getElementById('content');
  try {
    const s = await API.get(`/admin/stories/${id}`);
    const avatarHtml = s.authorAvatarUrl
      ? `<img src="${s.authorAvatarUrl}" class="detail-avatar-sm" alt="">`
      : `<div class="detail-avatar-sm detail-avatar-placeholder-sm">${(s.authorName || '?')[0]}</div>`;
    content.innerHTML = `
      <div class="card detail-card">
        <div class="detail-header">
          <div class="detail-header-left">
            ${avatarHtml}
            <div>
              <div class="detail-header-title">${s.authorName || '故事详情'}的故事</div>
              <div class="detail-header-id">ID: ${s.id} &middot; ${s.isVip ? 'VIP用户' : '普通用户'}</div>
            </div>
          </div>
          <button class="btn btn-ghost btn-sm" onclick="Router.navigate('/stories')">&larr; 返回列表</button>
        </div>
        <div class="detail-body">
          <div class="detail-row"><div class="detail-row-label">作者</div><div class="detail-row-value">${s.authorName || '-'}</div></div>
          <div class="detail-row"><div class="detail-row-label">VIP</div><div class="detail-row-value">${s.isVip ? '<span class="badge badge-warning">VIP</span>' : '<span class="badge badge-info">普通</span>'}</div></div>
          <div class="detail-row"><div class="detail-row-label">状态</div><div class="detail-row-value"><span class="badge ${STORY_STATUS_BADGE[s.status] || 'badge-info'}">${STORY_STATUS_MAP[s.status] || '未知'}</span></div></div>
          <div class="detail-row"><div class="detail-row-label">内容</div><div class="detail-row-value" style="white-space:pre-wrap;line-height:1.8">${s.content || '-'}</div></div>
          <div class="detail-row"><div class="detail-row-label">发布时间</div><div class="detail-row-value">${s.publishedAt || '-'}</div></div>
          <div class="detail-row"><div class="detail-row-label">创建时间</div><div class="detail-row-value">${s.createdAt || '-'}</div></div>
        </div>
        <div class="detail-stats">
          <div class="detail-stat"><div class="detail-stat-value">${s.likesCount || 0}</div><div class="detail-stat-label">点赞</div></div>
          <div class="detail-stat"><div class="detail-stat-value">${s.commentsCount || 0}</div><div class="detail-stat-label">评论</div></div>
          <div class="detail-stat"><div class="detail-stat-value">${s.sharesCount || 0}</div><div class="detail-stat-label">分享</div></div>
        </div>
      </div>`;
  } catch (err) { content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`; }
}
