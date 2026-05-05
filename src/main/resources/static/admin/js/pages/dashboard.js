async function initDashboard() {
  const content = document.getElementById('content');
  try {
    const stats = await API.get('/admin/stats');
    content.innerHTML = `
      <div class="grid grid-4">
        <div class="card stat-card">
          <div class="stat-card-header">
            <div class="stat-card-icon blue">${Icons.users}</div>
          </div>
          <div class="stat-card-value">${stats.userCount}</div>
          <div class="stat-card-label">用户总数</div>
        </div>
        <div class="card stat-card">
          <div class="stat-card-header">
            <div class="stat-card-icon green">${Icons.guide}</div>
          </div>
          <div class="stat-card-value">${stats.guideCount}</div>
          <div class="stat-card-label">攻略总数</div>
        </div>
        <div class="card stat-card">
          <div class="stat-card-header">
            <div class="stat-card-icon yellow">${Icons.story}</div>
          </div>
          <div class="stat-card-value">${stats.storyCount}</div>
          <div class="stat-card-label">故事总数</div>
        </div>
        <div class="card stat-card">
          <div class="stat-card-header">
            <div class="stat-card-icon red">${Icons.destination}</div>
          </div>
          <div class="stat-card-value">${stats.destinationCount}</div>
          <div class="stat-card-label">目的地总数</div>
        </div>
      </div>
      <div class="grid grid-2 dashboard-charts">
        <div class="card chart-card">
          <div class="card-title">内容分布</div>
          <canvas id="doughnutChart" height="200"></canvas>
        </div>
        <div class="card chart-card">
          <div class="card-title">数据概览</div>
          <canvas id="barChart" height="200"></canvas>
        </div>
      </div>`;
    renderDoughnutChart(stats);
    renderBarChart(stats);
  } catch (err) {
    content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`;
  }
}

function renderDoughnutChart(stats) {
  const ctx = document.getElementById('doughnutChart');
  if (!ctx) return;
  new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: ['攻略', '故事', '目的地', '评论'],
      datasets: [{
        data: [stats.guideCount, stats.storyCount, stats.destinationCount, stats.commentCount],
        backgroundColor: ['#3b82f6', '#22c55e', '#eab308', '#ef4444'],
        borderWidth: 0
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { position: 'bottom', labels: { padding: 16 } } },
      cutout: '65%'
    }
  });
}

function renderBarChart(stats) {
  const ctx = document.getElementById('barChart');
  if (!ctx) return;
  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: ['用户', '攻略', '故事', '目的地', '评论'],
      datasets: [{
        data: [stats.userCount, stats.guideCount, stats.storyCount, stats.destinationCount, stats.commentCount],
        backgroundColor: ['#3b82f6', '#22c55e', '#eab308', '#ef4444', '#8b5cf6'],
        borderRadius: 6,
        barThickness: 32
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { display: false } },
      scales: { y: { beginAtZero: true, grid: { color: '#f1f5f9' } }, x: { grid: { display: false } } }
    }
  });
}
