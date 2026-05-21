<template>
  <div class="my-guides-page">
    <header class="page-header">
      <div class="header-content">
        <button class="back-pill" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
          <span>返回</span>
        </button>
        <div class="header-text">
          <span class="header-tag">MY GUIDES</span>
          <h1>我的攻略</h1>
          <p>管理你发布的旅行攻略</p>
        </div>
      </div>
    </header>

    <section class="guides-section">
      <div class="container">
        <div v-if="loading" class="loading-state">
          <div class="loader">
            <div class="loader-dot"></div>
            <div class="loader-dot"></div>
            <div class="loader-dot"></div>
          </div>
          <p>正在加载我的攻略...</p>
        </div>

        <div v-else-if="error" class="error-state">
          <div class="error-icon-wrap">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <path d="M12 8v4M12 16h.01"/>
            </svg>
          </div>
          <p>{{ error }}</p>
          <button @click="fetchGuides" class="retry-btn">重新加载</button>
        </div>

        <div v-else-if="guides.length === 0" class="empty-state">
          <div class="empty-icon-wrap">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M9 12h6M12 9v6M3 12a9 9 0 1118 0 9 9 0 01-18 0z"/>
            </svg>
          </div>
          <p>暂无攻略，去发布一篇吧</p>
        </div>

        <div v-else class="guide-grid">
          <div
            v-for="guide in guides"
            :key="guide.id"
            class="guide-card"
          >
            <div class="card-visual" @click="viewGuideDetail(guide.id)">
              <img :src="guide.coverImageUrl" :alt="guide.title" loading="lazy">
              <div class="card-shine"></div>
            </div>
            <div class="card-body" @click="viewGuideDetail(guide.id)">
              <h3>{{ guide.title }}</h3>
              <p>{{ guide.summary }}</p>
              <div class="card-stats">
                <span class="stat-item">
                  <svg viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                  </svg>
                  {{ guide.likesCount }}
                </span>
                <span class="stat-item">
                  <svg viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                  </svg>
                  {{ guide.viewsCount }}
                </span>
                <span class="stat-item">
                  <svg viewBox="0 0 24 24" fill="currentColor">
                    <path d="M20 2H4C2.9 2 2 2.9 2 4V22L6 18H20C21.1 18 22 17.1 22 16V4C22 2.9 21.1 2 20 2z"/>
                  </svg>
                  {{ guide.commentsCount }}
                </span>
              </div>
            </div>
            <div class="card-actions">
              <button class="action-btn edit-btn" @click="editGuide(guide.id)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/>
                  <path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/>
                </svg>
                编辑
              </button>
              <button class="action-btn delete-btn" @click="confirmDelete(guide)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 6h18M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/>
                  <path d="M10 11v6M14 11v6"/>
                </svg>
                删除
              </button>
            </div>
          </div>
        </div>

        <div v-if="currentPage < totalPages && !loading" class="load-more-wrap">
          <button class="load-more-btn" @click="loadMore">加载更多</button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import request from '../utils/request'

const emit = defineEmits(['view-guide-detail', 'back'])
const toast = inject('toast')

const guides = ref([])
const loading = ref(false)
const error = ref(null)
const currentPage = ref(1)
const totalPages = ref(1)
const pageSize = 20

const fetchGuides = async () => {
  loading.value = true
  error.value = null
  try {
    const result = await request.get(`/api/users/me/guides?page=${currentPage.value}&pageSize=${pageSize}`)
    if (result.code === 200 || result.code === 'OK') {
      const list = result.data?.list || []
      if (currentPage.value === 1) {
        guides.value = list
      } else {
        guides.value = [...guides.value, ...list]
      }
      totalPages.value = result.data?.totalPages || 1
    } else {
      error.value = result.message || '获取攻略失败'
    }
  } catch (err) {
    error.value = '网络请求失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (currentPage.value >= totalPages.value) return
  currentPage.value++
  fetchGuides()
}

const goBack = () => emit('back')
const viewGuideDetail = (id) => emit('view-guide-detail', id)

const editGuide = (id) => {
  emit('view-guide-detail', id)
}

const confirmDelete = async (guide) => {
  if (!confirm(`确定删除《${guide.title}》吗？此操作不可撤销。`)) return
  try {
    const result = await request.delete(`/api/guides/${guide.id}`)
    if (result.code === 200 || result.code === 'OK') {
      guides.value = guides.value.filter(g => g.id !== guide.id)
      if (toast) toast.success('攻略已删除')
    } else {
      if (toast) toast.error(result.message || '删除失败')
    }
  } catch (err) {
    if (toast) toast.error('删除失败：' + (err.response?.data?.message || err.message))
  }
}

onMounted(() => {
  fetchGuides()
})
</script>

<style scoped>
.my-guides-page {
  min-height: 100vh;
  background-color: var(--color-bg-primary);
  font-family: var(--font-body);
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

.page-header {
  position: relative;
  background: linear-gradient(135deg, var(--color-bg-dark) 0%, var(--color-primary) 100%);
  overflow: hidden;
}

.header-content {
  position: relative;
  z-index: 2;
  padding: 40px 60px 48px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.back-pill {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 30px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 24px;
}

.back-pill:hover {
  background: rgba(255,255,255,0.25);
  transform: translateX(-4px);
}

.back-pill svg {
  width: 18px;
  height: 18px;
}

.header-text {
  margin-bottom: 8px;
}

.header-tag {
  display: inline-block;
  padding: 6px 16px;
  background: linear-gradient(135deg, #F5F0E8, #FAF8F5);
  color: var(--color-text-primary);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 3px;
  border-radius: 20px;
  margin-bottom: 16px;
}

.header-text h1 {
  font-family: var(--font-display);
  font-size: clamp(36px, 5vw, 56px);
  color: white;
  margin: 0 0 12px;
  line-height: 1.1;
  letter-spacing: -1px;
  text-shadow: 0 4px 20px rgba(45,58,30,0.3);
}

.header-text p {
  font-size: 16px;
  color: rgba(255,255,255,0.8);
  line-height: 1.6;
}

.guides-section {
  padding: 40px 0 80px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  color: #A8A29E;
}

.loader {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.loader-dot {
  width: 12px;
  height: 12px;
  background: var(--color-primary);
  border-radius: 50%;
  animation: loaderBounce 1.4s ease-in-out infinite;
}

.loader-dot:nth-child(2) { animation-delay: 0.2s; }
.loader-dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes loaderBounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}

.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  color: var(--color-text-muted);
}

.error-icon-wrap {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(224,112,112,0.15);
  border-radius: 50%;
  margin-bottom: 20px;
}

.error-icon-wrap svg {
  width: 32px;
  height: 32px;
  color: #E07070;
}

.retry-btn {
  margin-top: 20px;
  padding: 12px 32px;
  background: linear-gradient(135deg, #F5F0E8 0%, #FAF8F5 100%);
  color: var(--color-text-primary);
  border: none;
  border-radius: 30px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.retry-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(245,240,232,0.3);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  color: #A8A29E;
}

.empty-icon-wrap {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,0.5);
  border-radius: 24px;
  margin-bottom: 20px;
}

.empty-icon-wrap svg {
  width: 40px;
  height: 40px;
}

.guide-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.guide-card {
  background: var(--color-card-bg);
  border: 1px solid var(--color-card-border);
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  animation: cardFadeIn 0.5s ease backwards;
  display: flex;
  flex-direction: column;
}

.guide-card:nth-child(1) { animation-delay: 0.05s; }
.guide-card:nth-child(2) { animation-delay: 0.1s; }
.guide-card:nth-child(3) { animation-delay: 0.15s; }
.guide-card:nth-child(4) { animation-delay: 0.2s; }
.guide-card:nth-child(5) { animation-delay: 0.25s; }
.guide-card:nth-child(6) { animation-delay: 0.3s; }

@keyframes cardFadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.guide-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 40px rgba(45,58,30,0.15);
}

.card-visual {
  position: relative;
  height: 200px;
  overflow: hidden;
  cursor: pointer;
}

.card-visual img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.guide-card:hover .card-visual img {
  transform: scale(1.08);
}

.card-shine {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0.08) 0%, transparent 60%);
}

.card-body {
  padding: 20px;
  cursor: pointer;
  flex: 1;
}

.card-body h3 {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-body p {
  font-size: 14px;
  color: var(--color-text-muted);
  line-height: 1.6;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--color-text-muted);
}

.stat-item svg {
  width: 14px;
  height: 14px;
}

.card-actions {
  display: flex;
  border-top: 1px solid var(--color-card-border);
  padding: 0;
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 12px;
  background: transparent;
  border: none;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--color-text-secondary);
}

.action-btn svg {
  width: 16px;
  height: 16px;
}

.edit-btn {
  border-right: 1px solid var(--color-card-border);
}

.edit-btn:hover {
  background: var(--color-primary-10);
  color: var(--color-primary);
}

.delete-btn:hover {
  background: rgba(224,112,112,0.1);
  color: #E07070;
}

.load-more-wrap {
  display: flex;
  justify-content: center;
  padding: 40px 20px;
}

.load-more-btn {
  padding: 12px 40px;
  background: var(--color-card-bg);
  border: 1px solid var(--color-card-border);
  border-radius: 30px;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.3s ease;
}

.load-more-btn:hover {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: #fff;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(91,140,62,0.3);
}

@media (max-width: 1024px) {
  .guide-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .guide-grid {
    grid-template-columns: 1fr;
  }

  .header-content {
    padding: 32px 24px 40px;
  }

  .container {
    padding: 0 16px;
  }

  .card-visual {
    height: 180px;
  }
}
</style>
