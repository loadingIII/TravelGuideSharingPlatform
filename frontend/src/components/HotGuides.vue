<template>
  <div class="hot-guides-page">
    <!-- 页面头部 -->
    <header class="page-header">
      <div class="header-content">
        <div class="back-btn" @click="goBack">
          <span class="arrow">&lt;</span>
          <span>返回首页</span>
        </div>
        <h1>热门攻略</h1>
        <p class="subtitle">发现万千旅行者的精彩攻略与实用建议</p>
      </div>
      <div class="header-wave">
        <svg viewBox="0 0 1440 120" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M0 120L60 110C120 100 240 80 360 70C480 60 600 60 720 65C840 70 960 80 1080 85C1200 90 1320 90 1380 90L1440 90V120H1380C1320 120 1200 120 1080 120C960 120 840 120 720 120C600 120 480 120 360 120C240 120 120 120 60 120H0Z" fill="#f8f6f3"/>
        </svg>
      </div>
    </header>

    <!-- 搜索和筛选区 -->
    <section class="search-section">
      <div class="container">
        <div class="search-box">
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="搜索目的地、攻略..."
            @keyup.enter="handleSearch"
          >
          <button class="search-btn" @click="handleSearch">
            <span>搜索</span>
          </button>
        </div>
        <div class="filter-tags">
          <button 
            v-for="tag in filterTags" 
            :key="tag.value"
            class="tag-btn"
            :class="{ active: currentFilter === tag.value }"
            @click="setFilter(tag.value)"
          >
            {{ tag.label }}
          </button>
        </div>
      </div>
    </section>

    <!-- 热门攻略区 -->
    <section class="hot-guides">
      <div class="container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>正在加载热门攻略...</p>
        </div>
        
        <!-- 错误状态 -->
        <div v-else-if="error" class="error-state">
          <span class="error-icon">⚠️</span>
          <p>{{ error }}</p>
          <button @click="fetchHotGuides" class="retry-btn">重新加载</button>
        </div>
        
        <!-- 空状态 -->
        <div v-else-if="hotGuides.length === 0" class="empty-state">
          <span class="empty-icon">📝</span>
          <p>暂无攻略数据</p>
        </div>
        
        <!-- 攻略列表 -->
        <div v-else class="guides-grid">
          <div 
            v-for="guide in hotGuides" 
            :key="guide.id"
            class="guide-card"
            @mouseenter="hoverGuide = guide.id"
            @mouseleave="hoverGuide = null"
            :class="{ hovered: hoverGuide === guide.id }"
            @click="viewGuideDetail(guide.id)"
          >
            <div class="card-image">
              <img :src="guide.image" :alt="guide.title" loading="lazy">
              <div class="card-overlay">
                <span class="location">{{ guide.location }}</span>
              </div>
            </div>
            <div class="card-content">
              <h3>{{ guide.title }}</h3>
              <p class="description">{{ guide.description }}</p>
              <div class="card-meta">
                <div class="author">
                  <img :src="guide.authorAvatar" :alt="guide.author">
                  <span>{{ guide.author }}</span>
                </div>
                <div class="stats">
                  <span class="likes">
                    <svg class="stat-icon likes-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5C22 12.27 18.6 15.36 13.45 20.03L12 21.35Z" fill="url(#heartGradientHot)" stroke="#e85a5a" stroke-width="1.5"/>
                      <defs>
                        <linearGradient id="heartGradientHot" x1="12" y1="3" x2="12" y2="21.35" gradientUnits="userSpaceOnUse">
                          <stop stop-color="#ff9a9e"/>
                          <stop offset="1" stop-color="#f79545"/>
                        </linearGradient>
                      </defs>
                    </svg>
                    {{ guide.likes }}
                  </span>
                  <span class="views">
                    <svg class="stat-icon views-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M12 5C7 5 2.73 8.11 1 12C2.73 15.89 7 19 12 19C17 19 21.27 15.89 23 12C21.27 8.11 17 5 12 5Z" fill="#e8e8e8" stroke="#999" stroke-width="1.5"/>
                      <circle cx="12" cy="12" r="4" fill="#fff" stroke="#999" stroke-width="1.5"/>
                      <circle cx="12" cy="12" r="2" fill="#f79545"/>
                    </svg>
                    {{ guide.views }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载更多状态 -->
        <div v-if="loadingMore" class="loading-more">
          <div class="loading-spinner-small"></div>
          <span>正在加载更多攻略...</span>
        </div>

        <!-- 没有更多数据提示 -->
        <div v-else-if="hotGuides.length > 0 && currentPage >= totalPages" class="no-more">
          <span>已经到底啦 ~</span>
        </div>

        <!-- 无限滚动触发器 -->
        <div ref="loadMoreTrigger" class="load-more-trigger"></div>
      </div>
    </section>

    <!-- 发布按钮 -->
    <button class="fab-publish" @click="showPublishModal = true">
      <span>+</span>
      <span class="fab-text">发布攻略</span>
    </button>

    <!-- 发布模态框 -->
    <div class="modal-overlay" v-if="showPublishModal" @click.self="showPublishModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>发布攻略</h3>
          <button class="close-btn" @click="showPublishModal = false">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>标题</label>
            <input type="text" placeholder="给你的攻略起个吸引人的标题">
          </div>
          <div class="form-group">
            <label>目的地</label>
            <input type="text" placeholder="去哪里旅行？">
          </div>
          <div class="form-group">
            <label>内容</label>
            <textarea rows="6" placeholder="分享你的旅行经历、攻略心得..."></textarea>
          </div>
          <div class="form-group">
            <label>上传图片</label>
            <div class="upload-area">
              <span>+ 点击或拖拽上传图片</span>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="showPublishModal = false">取消</button>
          <button class="btn-primary" @click="publishGuide">发布</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'

const emit = defineEmits(['back-to-home', 'view-guide-detail'])

// 搜索和筛选
const searchQuery = ref('')
const currentFilter = ref('all')
const currentPage = ref(1)
const totalPages = ref(1)

const filterTags = [
  { label: '全部', value: 'all' },
  { label: '国内游', value: 'domestic' },
  { label: '出境游', value: 'international' },
  { label: '自由行', value: 'free' },
  { label: '跟团游', value: 'group' },
  { label: '亲子游', value: 'family' },
  { label: '蜜月游', value: 'honeymoon' }
]

// 悬停状态
const hoverGuide = ref(null)

// 模态框
const showPublishModal = ref(false)

// 热门攻略数据
const hotGuides = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const error = ref(null)

// 无限滚动相关
const loadMoreTrigger = ref(null)
let observer = null

// 获取热门攻略数据
const fetchHotGuides = async (isLoadMore = false) => {
  if (isLoadMore) {
    loadingMore.value = true
  } else {
    loading.value = true
  }
  error.value = null
  
  try {
    const response = await fetch(`/api/guides/page?page=${currentPage.value}&pageSize=12`)
    const result = await response.json()
    
    console.log('API 返回结果:', result)
    
    if (result.code === 200 || result.code === 'OK') {
      if (result.data && result.data.list && result.data.list.length > 0) {
        const newGuides = result.data.list.map(item => ({
          id: item.id,
          title: item.title,
          description: item.summary,
          image: item.coverImageUrl,
          location: item.locationText,
          author: item.authorName,
          authorAvatar: item.authorAvatarUrl,
          likes: item.likesCount,
          views: item.viewsCount
        }))
        
        if (isLoadMore) {
          // 追加数据
          hotGuides.value = [...hotGuides.value, ...newGuides]
        } else {
          // 替换数据
          hotGuides.value = newGuides
        }
        
        totalPages.value = result.data.totalPages || 1
        console.log('攻略数据加载成功:', hotGuides.value)
      } else if (!isLoadMore) {
        hotGuides.value = []
        console.log('攻略数据为空')
      }
    } else {
      error.value = result.message || '获取攻略数据失败'
      console.error('接口返回错误:', result.message)
    }
  } catch (err) {
    console.error('请求攻略接口失败:', err)
    error.value = '网络请求失败，请稍后重试'
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

// 加载下一页
const loadNextPage = async () => {
  if (loadingMore.value || currentPage.value >= totalPages.value) {
    return
  }
  
  currentPage.value++
  await fetchHotGuides(true)
}

// 设置无限滚动观察器
const setupInfiniteScroll = () => {
  if (observer) {
    observer.disconnect()
  }
  
  observer = new IntersectionObserver((entries) => {
    const entry = entries[0]
    if (entry.isIntersecting && !loading.value && !loadingMore.value && currentPage.value < totalPages.value) {
      loadNextPage()
    }
  }, {
    root: null,
    rootMargin: '100px',
    threshold: 0.1
  })
  
  if (loadMoreTrigger.value) {
    observer.observe(loadMoreTrigger.value)
  }
}

// 返回首页
const goBack = () => {
  emit('back-to-home')
}

// 查看攻略详情
const viewGuideDetail = (guideId) => {
  emit('view-guide-detail', guideId)
}

// 搜索
const handleSearch = () => {
  console.log('搜索:', searchQuery.value)
  currentPage.value = 1
  fetchHotGuides()
}

// 设置筛选
const setFilter = (value) => {
  currentFilter.value = value
  currentPage.value = 1
  fetchHotGuides()
}

// 重置并重新设置观察器
const resetObserver = () => {
  nextTick(() => {
    setupInfiniteScroll()
  })
}

// 发布攻略
const publishGuide = () => {
  alert('攻略发布成功！')
  showPublishModal.value = false
}

// 页面加载时获取数据
onMounted(() => {
  fetchHotGuides()
  resetObserver()
})

// 组件卸载时清理观察器
onUnmounted(() => {
  if (observer) {
    observer.disconnect()
  }
})
</script>

<style scoped>
/* 页面整体样式 */
.hot-guides-page {
  min-height: 100vh;
  background-color: #f8f6f3;
  font-family: 'Noto Sans SC', sans-serif;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 页面头部 */
.page-header {
  background: url('/img/悉尼歌剧院.jpg') center/cover no-repeat;
  padding: 60px 0 0;
  position: relative;
  overflow: hidden;
}

.page-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.4) 0%, rgba(0, 0, 0, 0.2) 50%, rgba(0, 0, 0, 0.4) 100%);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 40px;
  position: relative;
  z-index: 1;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  margin-bottom: 20px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateX(-5px);
}

.back-btn .arrow {
  font-size: 16px;
}

.page-header h1 {
  font-family: 'Noto Serif SC', serif;
  font-size: 48px;
  color: white;
  margin-bottom: 15px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
  letter-spacing: 4px;
}

.subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 300;
}

.header-wave {
  position: relative;
  bottom: -1px;
}

.header-wave svg {
  display: block;
  width: 100%;
}

/* 搜索区域 */
.search-section {
  background-color: #f8f6f3;
  padding: 40px 0;
}

.search-box {
  display: flex;
  gap: 15px;
  margin-bottom: 25px;
  max-width: 700px;
  margin-left: auto;
  margin-right: auto;
}

.search-box input {
  flex: 1;
  padding: 15px 25px;
  border: 2px solid rgba(247, 149, 69, 0.3);
  border-radius: 30px;
  font-size: 16px;
  background: white;
  transition: all 0.3s ease;
  outline: none;
}

.search-box input:focus {
  border-color: #f79545;
  box-shadow: 0 0 0 4px rgba(247, 149, 69, 0.1);
}

.search-btn {
  padding: 15px 35px;
  background: linear-gradient(135deg, #f79545 0%, #ffc494 100%);
  color: white;
  border: none;
  border-radius: 30px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(247, 149, 69, 0.3);
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(247, 149, 69, 0.4);
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
}

.tag-btn {
  padding: 10px 24px;
  background: white;
  border: 2px solid rgba(247, 149, 69, 0.2);
  border-radius: 25px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tag-btn:hover {
  border-color: #f79545;
  color: #f79545;
}

.tag-btn.active {
  background: linear-gradient(135deg, #f79545 0%, #ffc494 100%);
  border-color: #f79545;
  color: white;
}

/* 热门攻略 */
.hot-guides {
  padding: 60px 0;
  background-color: #f8f6f3;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #888;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 3px solid #f0f0f0;
  border-top-color: #f79545;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 错误状态 */
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #666;
}

.error-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.retry-btn {
  margin-top: 20px;
  padding: 12px 32px;
  background: linear-gradient(135deg, #f79545 0%, #ffc494 100%);
  color: #fff;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.retry-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(247, 149, 69, 0.3);
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #888;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.guides-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 25px;
}

.guide-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.4s ease;
  cursor: pointer;
}

.guide-card.hovered {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.card-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.guide-card.hovered .card-image img {
  transform: scale(1.05);
}

.card-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 15px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
}

.location {
  display: flex;
  align-items: center;
  gap: 5px;
  color: white;
  font-size: 14px;
  font-weight: 500;
}

.location::before {
  content: '';
  display: inline-block;
  width: 14px;
  height: 14px;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none'%3E%3Cpath d='M12 2C8.13 2 5 5.13 5 9C5 14.25 12 22 12 22C12 22 19 14.25 19 9C19 5.13 15.87 2 12 2Z' fill='%23fff5f5' stroke='%23fff' stroke-width='1.5'/%3E%3Ccircle cx='12' cy='9' r='2.5' fill='%23fff'/%3E%3C/svg%3E");
  background-size: contain;
  background-repeat: no-repeat;
}

.card-content {
  padding: 20px;
}

.card-content h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
  line-height: 1.4;
}

.description {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.author {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.author span {
  font-size: 14px;
  color: #666;
}

.stats {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: #999;
}

.stats .likes,
.stats .views {
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s ease;
}

.stats .likes:hover,
.stats .views:hover {
  color: #666;
  transform: translateY(-2px);
}

.stat-icon {
  width: 16px;
  height: 16px;
  transition: all 0.3s ease;
  filter: drop-shadow(0 2px 3px rgba(0, 0, 0, 0.1));
}

.stats .likes:hover .stat-icon,
.stats .views:hover .stat-icon {
  transform: scale(1.15);
}

.stats .likes:hover .likes-icon {
  filter: drop-shadow(0 3px 5px rgba(232, 90, 90, 0.3));
}

.stats .views:hover .views-icon {
  filter: drop-shadow(0 3px 5px rgba(153, 153, 153, 0.3));
}

/* 加载更多 */
.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 30px 20px;
  color: #888;
  font-size: 14px;
}

.loading-spinner-small {
  width: 24px;
  height: 24px;
  border: 2px solid #f0f0f0;
  border-top-color: #f79545;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.no-more {
  text-align: center;
  padding: 30px 20px;
  color: #aaa;
  font-size: 14px;
}

.load-more-trigger {
  height: 20px;
  margin-top: 20px;
}

/* 发布按钮 */
.fab-publish {
  position: fixed;
  bottom: 30px;
  right: 30px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 28px;
  background: linear-gradient(135deg, #f79545 0%, #ffc494 100%);
  color: white;
  border: none;
  border-radius: 50px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 6px 25px rgba(247, 149, 69, 0.4);
  transition: all 0.3s ease;
  z-index: 100;
}

.fab-publish:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 10px 35px rgba(247, 149, 69, 0.5);
}

.fab-publish span:first-child {
  font-size: 24px;
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.modal-content {
  background: white;
  border-radius: 24px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: modalSlideIn 0.3s ease;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  font-family: 'Noto Serif SC', serif;
}

.close-btn {
  background: none;
  border: none;
  font-size: 28px;
  color: #999;
  cursor: pointer;
  transition: color 0.3s ease;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 25px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #555;
  margin-bottom: 8px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 18px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  font-size: 15px;
  transition: all 0.3s ease;
  outline: none;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #f79545;
  box-shadow: 0 0 0 4px rgba(247, 149, 69, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 120px;
}

.upload-area {
  border: 2px dashed #f79545;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  color: #f79545;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-area:hover {
  background: rgba(247, 149, 69, 0.05);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding: 20px 25px;
  border-top: 1px solid #f0f0f0;
}

.btn-secondary {
  padding: 12px 25px;
  background: #f5f5f5;
  border: none;
  border-radius: 25px;
  font-size: 15px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background: #e8e8e8;
}

.btn-primary {
  padding: 12px 35px;
  background: linear-gradient(135deg, #f79545 0%, #ffc494 100%);
  border: none;
  border-radius: 25px;
  font-size: 15px;
  color: white;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(247, 149, 69, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header h1 {
    font-size: 32px;
  }

  .guides-grid {
    grid-template-columns: 1fr;
  }

  .fab-text {
    display: none;
  }

  .fab-publish {
    padding: 16px;
    border-radius: 50%;
  }

  .pagination {
    flex-wrap: wrap;
  }
}
</style>
