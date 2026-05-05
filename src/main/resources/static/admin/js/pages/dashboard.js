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
      <div class="dashboard-charts">
        <div class="card chart-card" style="margin-bottom:var(--space-5)">
          <div class="card-title">数据概览</div>
          <div style="height:300px"><canvas id="barChart"></canvas></div>
        </div>
        <div class="grid grid-3">
          <div class="card chart-card">
            <div class="card-title">内容分布</div>
            <div style="height:240px;display:flex;align-items:center;justify-content:center"><canvas id="doughnutChart"></canvas></div>
          </div>
          <div class="card chart-card" style="grid-column:span 2">
            <div class="card-title">数据明细</div>
            <div id="statsDetail"></div>
          </div>
        </div>
      </div>`;
    renderBarChart(stats);
    renderDoughnutChart(stats);
    renderStatsDetail(stats);
  } catch (err) {
    content.innerHTML = `<div class="alert alert-danger">加载失败: ${err.message}</div>`;
  }
}

const chartColors = {
  primary: '#4f6d7a',
  accent: '#c4956a',
  success: '#5a8f6a',
  danger: '#b85450',
  muted: '#8494a7',
  palette: ['#4f6d7a', '#c4956a', '#5a8f6a', '#b85450', '#8494a7']
};

function renderBarChart(stats) {
  const ctx = document.getElementById('barChart');
  if (!ctx) return;
  const values = [stats.userCount, stats.guideCount, stats.storyCount, stats.destinationCount, stats.commentCount];
  const labels = ['用户', '攻略', '故事', '目的地', '评论'];
  const barColors = chartColors.palette;

  new Chart(ctx, {
    type: 'bar',
    data: {
      labels,
      datasets: [{
        data: values,
        backgroundColor: barColors,
        borderColor: barColors.map(c => c + 'cc'),
        borderWidth: 0,
        borderRadius: 6,
        barThickness: 40,
        hoverBackgroundColor: barColors
      }]
    },
    options: {
      indexAxis: 'y',
      responsive: true,
      maintainAspectRatio: false,
      layout: { padding: { right: 20 } },
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: 'rgba(26,35,50,0.92)',
          titleFont: { family: "'DM Sans'", size: 13, weight: '600' },
          bodyFont: { family: "'DM Sans'", size: 12 },
          padding: { top: 10, bottom: 10, left: 14, right: 14 },
          cornerRadius: 8,
          displayColors: false,
          borderColor: 'rgba(255,255,255,0.08)',
          borderWidth: 1,
          callbacks: {
            label: ctx => `${ctx.label}: ${ctx.parsed.x} 条`
          }
        }
      },
      scales: {
        x: {
          beginAtZero: true,
          grid: { color: 'rgba(226,232,240,0.6)', drawBorder: false },
          ticks: { font: { family: "'DM Sans'", size: 11 }, color: '#8494a7', padding: 4 },
          border: { display: false }
        },
        y: {
          grid: { display: false },
          ticks: {
            font: { family: "'DM Sans'", size: 13, weight: '600' },
            color: '#4a5568',
            padding: 8
          },
          border: { display: false }
        }
      },
      animation: {
        duration: 800,
        easing: 'easeOutQuart'
      }
    },
    plugins: [{
      id: 'barValueLabels',
      afterDatasetsDraw(chart) {
        const { ctx: c, data, chartArea } = chart;
        const meta = chart.getDatasetMeta(0);
        c.save();
        c.font = "600 12px 'DM Sans'";
        c.fillStyle = '#4a5568';
        c.textBaseline = 'middle';
        meta.data.forEach((bar, i) => {
          const val = data.datasets[0].data[i];
          c.fillText(val, bar.x + 8, bar.y);
        });
        c.restore();
      }
    }]
  });
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
        backgroundColor: [chartColors.primary, chartColors.success, chartColors.accent, chartColors.danger],
        borderWidth: 3,
        borderColor: '#fff',
        hoverBorderWidth: 0,
        hoverOffset: 6
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      cutout: '68%',
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            padding: 16,
            usePointStyle: true,
            pointStyle: 'circle',
            font: { family: "'DM Sans'", size: 12, weight: '500' },
            color: '#4a5568'
          }
        },
        tooltip: {
          backgroundColor: '#1a2332',
          titleFont: { family: "'DM Sans'", size: 13, weight: '600' },
          bodyFont: { family: "'DM Sans'", size: 12 },
          padding: 12,
          cornerRadius: 8,
          callbacks: {
            label: ctx => {
              const total = ctx.dataset.data.reduce((a, b) => a + b, 0);
              const pct = total > 0 ? Math.round(ctx.parsed / total * 100) : 0;
              return `${ctx.label}: ${ctx.parsed} (${pct}%)`;
            }
          }
        }
      }
    }
  });
}

function renderStatsDetail(stats) {
  const el = document.getElementById('statsDetail');
  if (!el) return;
  const total = stats.userCount + stats.guideCount + stats.storyCount + stats.destinationCount + stats.commentCount;
  const items = [
    { label: '用户', value: stats.userCount, color: chartColors.primary },
    { label: '攻略', value: stats.guideCount, color: chartColors.success },
    { label: '故事', value: stats.storyCount, color: chartColors.accent },
    { label: '目的地', value: stats.destinationCount, color: chartColors.danger },
    { label: '评论', value: stats.commentCount, color: chartColors.muted }
  ];
  el.innerHTML = items.map(item => {
    const pct = total > 0 ? (item.value / total * 100).toFixed(1) : 0;
    return `
      <div style="display:flex;align-items:center;padding:12px 0;border-bottom:1px solid var(--color-border-subtle)">
        <div style="width:10px;height:10px;border-radius:3px;background:${item.color};margin-right:12px;flex-shrink:0"></div>
        <div style="flex:1;font-size:13px;font-weight:500;color:var(--color-text)">${item.label}</div>
        <div style="font-size:14px;font-weight:700;color:var(--color-text);margin-right:12px;font-variant-numeric:tabular-nums">${item.value}</div>
        <div style="width:100px;height:6px;background:var(--color-border-subtle);border-radius:3px;overflow:hidden">
          <div style="width:${pct}%;height:100%;background:${item.color};border-radius:3px;transition:width 0.6s var(--ease-out)"></div>
        </div>
        <div style="width:48px;text-align:right;font-size:12px;color:var(--color-text-muted);margin-left:8px">${pct}%</div>
      </div>`;
  }).join('');
}
