<template>
  <div class="destination-guides-page">
    <!-- 沉浸式页面头部 -->
    <header class="hero-header">
      <div class="hero-bg">
        <div class="hero-gradient"></div>
        <div class="hero-grain"></div>
      </div>
      <div class="hero-content">
        <button class="back-pill" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
          <span>返回首页</span>
        </button>
        <div class="hero-text">
          <span class="hero-tag">DESTINATIONS</span>
          <h1>目的地指南</h1>
          <p>探索世界各地的精彩目的地</p>
        </div>
        <div class="hero-search">
          <div class="search-box">
            <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8"/>
              <path d="M21 21l-4.35-4.35"/>
            </svg>
            <input
              type="text"
              v-model="searchQuery"
              placeholder="搜索目的地..."
              @keyup.enter="handleSearch"
            >
            <button class="search-btn" @click="handleSearch">探索</button>
          </div>
        </div>
      </div>
    </header>

    <!-- 地区筛选 -->
    <section class="region-section">
      <div class="container">
        <div class="region-scroll">
          <button 
            v-for="tag in regionTags" 
            :key="tag.value"
            class="region-chip"
            :class="{ active: currentRegion === tag.value }"
            @click="setRegion(tag.value)"
          >
            <span>{{ tag.label }}</span>
          </button>
        </div>
      </div>
    </section>

    <!-- 目的地列表区 -->
    <section class="destinations-section">
      <div class="container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <div class="loader">
            <div class="loader-dot"></div>
            <div class="loader-dot"></div>
            <div class="loader-dot"></div>
          </div>
          <p>正在加载目的地...</p>
        </div>
        
        <!-- 错误状态 -->
        <div v-else-if="error" class="error-state">
          <div class="error-icon-wrap">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <path d="M12 8v4M12 16h.01"/>
            </svg>
          </div>
          <p>{{ error }}</p>
          <button @click="fetchDestinations" class="retry-btn">重新加载</button>
        </div>
        
        <!-- 空状态 -->
        <div v-else-if="filteredDestinations.length === 0" class="empty-state">
          <div class="empty-icon-wrap">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <p>暂无目的地数据</p>
        </div>
        
        <!-- 目的地网格 -->
        <div v-else class="destinations-showcase">
          <div 
            v-for="(dest, index) in filteredDestinations" 
            :key="dest.name"
            class="destination-card"
            :class="[`card-${(index % 4) + 1}`]"
            @click="viewDestinationDetail(dest)"
          >
            <div class="card-visual">
              <img :src="dest.image" :alt="dest.name" loading="lazy">
              <div class="card-overlay"></div>
              <div class="card-shine"></div>
            </div>
            
            <div class="card-content">
              <div class="card-rating" v-if="dest.rating">
                <svg viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                </svg>
                <span>{{ dest.rating }}</span>
              </div>
              
              <div class="card-info">
                <div class="card-location">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"/>
                    <circle cx="12" cy="10" r="3"/>
                  </svg>
                  <span>{{ dest.country }} · {{ dest.city }}</span>
                </div>
                <h3>{{ dest.name }}</h3>
                <p class="dest-desc">{{ dest.description }}</p>
                
                <div class="card-stats">
                  <div class="stat">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/>
                      <path d="M14 2v6h6M16 13H8M16 17H8M10 9H8"/>
                    </svg>
                    <span>{{ formatNumber(dest.guidesCount) }} 篇攻略</span>
                  </div>
                  <div class="stat">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/>
                      <circle cx="9" cy="7" r="4"/>
                      <path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"/>
                    </svg>
                    <span>{{ formatNumber(dest.travelersCount) }} 人去过</span>
                  </div>
                </div>
              </div>
              
              <div class="card-cta">
                <span>探索目的地</span>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M5 12h14M12 5l7 7-7 7"/>
                </svg>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="totalPages > 1">
          <button 
            class="page-btn" 
            :disabled="currentPage === 1"
            @click="changePage(currentPage - 1)"
          >
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M15 18l-6-6 6-6"/>
            </svg>
            上一页
          </button>
          <div class="page-dots">
            <span v-for="page in totalPages" :key="page" class="page-dot" :class="{ active: currentPage === page }"></span>
          </div>
          <button 
            class="page-btn" 
            :disabled="currentPage === totalPages"
            @click="changePage(currentPage + 1)"
          >
            下一页
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 18l6-6-6-6"/>
            </svg>
          </button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '../utils/request'

const emit = defineEmits(['back-to-home', 'view-destination-detail'])

const searchQuery = ref('')
const currentRegion = ref('all')
const currentPage = ref(1)
const totalPages = ref(1)

const regionTags = [
  { label: '全部', value: 'all' },
  { label: '亚洲', value: 'asia' },
  { label: '欧洲', value: 'europe' },
  { label: '北美', value: 'north-america' },
  { label: '南美', value: 'south-america' },
  { label: '大洋洲', value: 'oceania' },
  { label: '非洲', value: 'africa' }
]

const destinations = ref([])
const loading = ref(false)
const error = ref(null)

const fetchDestinations = async () => {
  loading.value = true
  error.value = null
  
  try {
    const result = await request.get(`/api/destinations/page?page=${currentPage.value}&pageSize=12`)
    if (result.code === 200 || result.code === 'OK') {
      if (result.data && result.data.list && result.data.list.length > 0) {
        destinations.value = result.data.list.map(item => ({
          id: item.id,
          name: item.name,
          country: item.country,
          city: item.city,
          description: item.description,
          image: item.coverImageUrl,
          guidesCount: item.guidesCount,
          travelersCount: item.travelersCount,
          rating: item.ratingAvg,
          region: item.region || 'asia'
        }))
        totalPages.value = result.data.totalPages || 1
      } else {
        destinations.value = getDefaultDestinations()
        totalPages.value = 1
      }
    } else {
      destinations.value = getDefaultDestinations()
      totalPages.value = 1
    }
  } catch (err) {
    destinations.value = getDefaultDestinations()
    totalPages.value = 1
  } finally {
    loading.value = false
  }
}

const getDefaultDestinations = () => [
  { id: 1, name: '日本', country: '日本', city: '东京', description: '樱花、温泉、美食，四季皆有不同风情', image: '/img/富士山.jpg', guidesCount: 1250, travelersCount: 56800, rating: 4.9, region: 'asia' },
  { id: 2, name: '泰国', country: '泰国', city: '清迈', description: '微笑之国，热带风情与佛教文化的完美融合', image: '/img/长尾船.png', guidesCount: 980, travelersCount: 45200, rating: 4.7, region: 'asia' },
  { id: 3, name: '法国', country: '法国', city: '巴黎', description: '浪漫之都，艺术与时尚的殿堂', image: '/img/埃菲尔铁塔.jpg', guidesCount: 856, travelersCount: 38900, rating: 4.7, region: 'europe' },
  { id: 4, name: '意大利', country: '意大利', city: '罗马', description: '文艺复兴发源地，美食与历史的交响曲', image: '/img/比萨斜塔.png', guidesCount: 723, travelersCount: 32100, rating: 4.7, region: 'europe' },
  { id: 5, name: '希腊', country: '希腊', city: '圣托里尼', description: '蓝白相间的地中海风情，世界最美日落所在地', image: '/img/希腊圣托尼尼.png', guidesCount: 450, travelersCount: 25000, rating: 4.9, region: 'europe' },
  { id: 6, name: '澳大利亚', country: '澳大利亚', city: '悉尼', description: '海港城市，阳光海滩与现代建筑的完美结合', image: '/img/悉尼歌剧院.jpg', guidesCount: 650, travelersCount: 32000, rating: 4.8, region: 'oceania' },
  { id: 7, name: '中国', country: '中国', city: '大理', description: '风花雪月，云南慢生活的代表', image: '/img/希腊圣托尼尼.png', guidesCount: 560, travelersCount: 28000, rating: 4.6, region: 'asia' },
  { id: 8, name: '西班牙', country: '西班牙', city: '巴塞罗那', description: '高迪的建筑之城，地中海风情的艺术之都', image: '/img/巴塞罗亚.png', guidesCount: 690, travelersCount: 31000, rating: 4.8, region: 'europe' }
]

const filteredDestinations = computed(() => {
  let result = destinations.value
  if (currentRegion.value !== 'all') {
    result = result.filter(dest => dest.region === currentRegion.value)
  }
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(dest => 
      dest.name.toLowerCase().includes(query) ||
      dest.country.toLowerCase().includes(query) ||
      dest.city.toLowerCase().includes(query) ||
      dest.description.toLowerCase().includes(query)
    )
  }
  return result
})

const formatNumber = (num) => {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toString()
}

const goBack = () => emit('back-to-home')
const viewDestinationDetail = (dest) => emit('view-destination-detail', dest)

const handleSearch = () => { currentPage.value = 1 }
const setRegion = (value) => { currentRegion.value = value; currentPage.value = 1 }

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    fetchDestinations()
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

onMounted(() => fetchDestinations())
</script>

<style scoped>
.destination-guides-page {
  min-height: 100vh;
  background-color: var(--color-bg-primary);
  font-family: var(--font-body);
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

/* Hero Header */
.hero-header {
  position: relative;
  height: 45vh;
  min-height: 320px;
  overflow: hidden;
  display: flex;
  align-items: flex-end;
}

.hero-bg {
  position: absolute;
  inset: 0;
  background-image: url('/img/back3.png');
  background-size: cover;
  background-position: center;
}

.hero-gradient {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(to bottom, rgba(45, 58, 30, 0.25) 0%, rgba(45, 58, 30, 0.45) 100%);
}

.hero-grain {
  position: absolute;
  inset: 0;
  opacity: 0.12;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)' opacity='0.5'/%3E%3C/svg%3E");
}

.hero-content {
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

.hero-text {
  margin-bottom: 24px;
}

.hero-tag {
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

.hero-text h1 {
  font-family: var(--font-display);
  font-size: clamp(40px, 6vw, 72px);
  color: white;
  margin: 0 0 16px;
  line-height: 1.1;
  letter-spacing: -1px;
  text-shadow: 0 4px 20px rgba(45, 58, 30, 0.3);
}

.hero-text p {
  font-size: 18px;
  color: rgba(255,255,255,0.85);
  max-width: 450px;
  line-height: 1.6;
  margin: 0;
}

.hero-search {
  max-width: 560px;
}

.search-box {
  display: flex;
  align-items: center;
  padding: 8px;
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255,255,255,0.25);
  border-radius: 50px;
  transition: all 0.3s ease;
}

.search-box:focus-within {
  background: rgba(255,255,255,0.2);
  border-color: rgba(245,240,232,0.5);
  box-shadow: 0 0 0 4px rgba(245,240,232,0.15);
}

.search-icon {
  width: 20px;
  height: 20px;
  margin-left: 16px;
  color: rgba(255,255,255,0.7);
}

.search-box input {
  flex: 1;
  padding: 12px 16px;
  background: transparent;
  border: none;
  font-size: 16px;
  color: white;
  outline: none;
}

.search-box input::placeholder {
  color: rgba(255,255,255,0.6);
}

.search-btn {
  padding: 12px 28px;
  background: linear-gradient(135deg, #F5F0E8 0%, #FAF8F5 100%);
  color: var(--color-text-primary);
  border: none;
  border-radius: 30px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 15px rgba(245,240,232,0.3);
}



/* Region Section */
.region-section {
  padding: 32px 0;
  position: relative;
  z-index: 4;
}

.region-scroll {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding: 4px 0;
  scrollbar-width: none;
  -ms-overflow-style: none;
  justify-content: center;
}

.region-scroll::-webkit-scrollbar {
  display: none;
}

.region-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: rgba(255,255,255,0.6);
  border: 1px solid var(--color-card-border);
  border-radius: 30px;
  color: var(--color-text-secondary);
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  flex-shrink: 0;
}

.region-chip:hover {
  background: rgba(255,255,255,0.9);
  border-color: var(--color-primary);
  color: var(--color-primary-dark);
}

.region-chip.active {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: #FFFFFF;
}

/* Destinations Section */
.destinations-section {
  padding: 20px 0 80px;
}

/* Loading State */
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
  background: #F5F0E8;
  border-radius: 50%;
  animation: loaderBounce 1.4s ease-in-out infinite;
}

.loader-dot:nth-child(2) { animation-delay: 0.2s; }
.loader-dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes loaderBounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}

/* Error State */
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

/* Empty State */
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

/* Destinations Showcase */
.destinations-showcase {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-auto-rows: 380px;
  gap: 20px;
}

.destination-card {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.5s cubic-bezier(0.23, 1, 0.32, 1);
  animation: cardFadeIn 0.6s ease backwards;
}

.destination-card:nth-child(1) { animation-delay: 0.1s; }
.destination-card:nth-child(2) { animation-delay: 0.15s; }
.destination-card:nth-child(3) { animation-delay: 0.2s; }
.destination-card:nth-child(4) { animation-delay: 0.25s; }
.destination-card:nth-child(5) { animation-delay: 0.3s; }
.destination-card:nth-child(6) { animation-delay: 0.35s; }
.destination-card:nth-child(7) { animation-delay: 0.4s; }
.destination-card:nth-child(8) { animation-delay: 0.45s; }

@keyframes cardFadeIn {
  from { opacity: 0; transform: translateY(30px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.destination-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 24px 60px rgba(45, 58, 30, 0.4);
}

/* Card Variations */
.card-1, .card-5 { grid-column: span 2; }

.card-visual {
  position: absolute;
  inset: 0;
}

.card-visual img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.7s cubic-bezier(0.23, 1, 0.32, 1);
}

.destination-card:hover .card-visual img {
  transform: scale(1.1);
}

.card-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    180deg,
    transparent 0%,
    transparent 40%,
    rgba(45, 58, 30, 0.85) 100%
  );
}

.card-shine {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    rgba(255,255,255,0.1) 0%,
    transparent 50%
  );
  opacity: 0;
  transition: opacity 0.4s ease;
}

.destination-card:hover .card-shine {
  opacity: 1;
}

.card-content {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 24px;
}

.card-rating {
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: rgba(45, 58, 30, 0.5);
  backdrop-filter: blur(8px);
  border-radius: 20px;
  color: #ffd700;
  font-size: 14px;
  font-weight: 600;
}

.card-rating svg {
  width: 16px;
  height: 16px;
}

.card-info {
  transform: translateY(20px);
  opacity: 0;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

.destination-card:hover .card-info {
  transform: translateY(0);
  opacity: 1;
}

.card-location {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
}

.card-location svg {
  width: 14px;
  height: 14px;
  color: rgba(255,255,255,0.7);
}

.card-location span {
  font-size: 13px;
  color: rgba(255,255,255,0.7);
}

.card-info h3 {
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 700;
  color: white;
  margin: 0 0 8px;
}

.dest-desc {
  font-size: 14px;
  color: rgba(255,255,255,0.8);
  line-height: 1.5;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-stats {
  display: flex;
  gap: 20px;
}

.stat {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: rgba(255,255,255,0.7);
}

.stat svg {
  width: 14px;
  height: 14px;
}

.card-cta {
  position: absolute;
  bottom: 24px;
  right: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #F5F0E8 0%, #FAF8F5 100%);
  color: var(--color-text-primary);
  border-radius: 30px;
  font-size: 14px;
  font-weight: 600;
  opacity: 0;
  transform: translateX(20px);
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

.destination-card:hover .card-cta {
  opacity: 1;
  transform: translateX(0);
}

.card-cta svg {
  width: 16px;
  height: 16px;
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 48px;
}

.page-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: rgba(255,255,255,0.5);
  border: 1px solid var(--color-card-border);
  border-radius: 30px;
  font-size: 14px;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn svg {
  width: 16px;
  height: 16px;
}

.page-btn:hover:not(:disabled) {
  background: rgba(255,255,255,0.1);
  border-color: rgba(245,240,232,0.3);
  color: var(--color-text-primary);
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.page-dots {
  display: flex;
  gap: 8px;
}

.page-dot {
  width: 8px;
  height: 8px;
  background: rgba(255,255,255,0.2);
  border-radius: 50%;
  transition: all 0.3s ease;
}

.page-dot.active {
  width: 24px;
  border-radius: 4px;
  background: #F5F0E8;
}

/* Responsive */
@media (max-width: 1200px) {
  .destinations-showcase {
    grid-template-columns: repeat(3, 1fr);
  }
  .card-1, .card-5 { grid-column: span 2; }
  .card-2, .card-3, .card-4 { grid-column: span 1; }
}

@media (max-width: 900px) {
  .destinations-showcase {
    grid-template-columns: repeat(2, 1fr);
    grid-auto-rows: 320px;
  }
  .card-1, .card-2, .card-3, .card-4, .card-5 {
    grid-column: span 1;
  }
  .card-1 { grid-column: span 2; }
  
  .hero-content {
    padding: 32px 40px 40px;
  }
  
  .hero-search {
    max-width: 100%;
  }
}

@media (max-width: 600px) {
  .destinations-showcase {
    grid-template-columns: 1fr;
    grid-auto-rows: 360px;
  }
  .card-1, .card-2, .card-3, .card-4, .card-5 {
    grid-column: span 1;
  }
  
  .hero-content {
    padding: 24px 24px 32px;
  }
  
  .region-scroll {
    justify-content: flex-start;
    padding: 0 20px;
    margin: 0 -20px;
  }
  
  .card-info {
    transform: translateY(0);
    opacity: 1;
  }
  
  .card-cta {
    opacity: 1;
    transform: translateX(0);
  }
  
  .page-btn span {
    display: none;
  }
}
</style>
