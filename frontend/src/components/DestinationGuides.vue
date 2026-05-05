<template>
  <div class="destination-guides-page">
    <!-- 页面头部 -->
    <header class="page-header">
      <div class="header-content">
        <div class="back-btn" @click="goBack">
          <span class="arrow">&lt;</span>
          <span>返回首页</span>
        </div>
        <h1>目的地指南</h1>
        <p class="subtitle">探索世界各地的精彩目的地</p>
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
            placeholder="搜索目的地..."
            @keyup.enter="handleSearch"
          >
          <button class="search-btn" @click="handleSearch">
            <span>搜索</span>
          </button>
        </div>
        <div class="filter-tags">
          <button 
            v-for="tag in regionTags" 
            :key="tag.value"
            class="tag-btn"
            :class="{ active: currentRegion === tag.value }"
            @click="setRegion(tag.value)"
          >
            {{ tag.label }}
          </button>
        </div>
      </div>
    </section>

    <!-- 目的地列表区 -->
    <section class="destinations-section">
      <div class="container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>正在加载目的地...</p>
        </div>
        
        <!-- 错误状态 -->
        <div v-else-if="error" class="error-state">
          <span class="error-icon">⚠️</span>
          <p>{{ error }}</p>
          <button @click="fetchDestinations" class="retry-btn">重新加载</button>
        </div>
        
        <!-- 空状态 -->
        <div v-else-if="filteredDestinations.length === 0" class="empty-state">
          <span class="empty-icon">🌍</span>
          <p>暂无目的地数据</p>
        </div>
        
        <!-- 目的地列表 -->
        <div v-else class="destinations-grid">
          <div 
            v-for="dest in filteredDestinations" 
            :key="dest.name"
            class="destination-card"
            @mouseenter="hoverDest = dest.name"
            @mouseleave="hoverDest = null"
            :class="{ hovered: hoverDest === dest.name }"
            @click="viewDestinationDetail(dest)"
          >
            <img :src="dest.image" :alt="dest.name" loading="lazy">
            <div class="dest-info">
              <h3>{{ dest.name }}</h3>
              <p class="dest-desc">{{ dest.description }}</p>
              <div class="dest-stats">
                <span>{{ dest.guidesCount }} 篇攻略</span>
                <span>{{ dest.travelersCount }} 人去过</span>
              </div>
            </div>
            <div class="dest-rating" v-if="dest.rating">
              <span class="star">★</span>
              <span>{{ dest.rating }}</span>
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
            上一页
          </button>
          <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
          <button 
            class="page-btn" 
            :disabled="currentPage === totalPages"
            @click="changePage(currentPage + 1)"
          >
            下一页
          </button>
        </div>
      </div>
    </section>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const emit = defineEmits(['back-to-home', 'view-destination-detail'])

// 搜索和筛选
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

// 悬停状态
const hoverDest = ref(null)

// 数据状态
const destinations = ref([])
const loading = ref(false)
const error = ref(null)

// 获取目的地数据
const fetchDestinations = async () => {
  loading.value = true
  error.value = null
  
  try {
    const response = await fetch(`/api/destinations/page?page=${currentPage.value}&pageSize=12`)
    const result = await response.json()
    
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
        // 使用默认数据
        destinations.value = getDefaultDestinations()
        totalPages.value = 1
      }
    } else {
      // 使用默认数据
      destinations.value = getDefaultDestinations()
      totalPages.value = 1
    }
  } catch (err) {
    console.error('请求目的地接口失败:', err)
    // 使用默认数据
    destinations.value = getDefaultDestinations()
    totalPages.value = 1
  } finally {
    loading.value = false
  }
}

// 默认目的地数据
const getDefaultDestinations = () => [
  {
    id: 1,
    name: '日本',
    country: '日本',
    city: '东京',
    description: '樱花、温泉、美食，四季皆有不同风情',
    image: '/img/富士山.jpg',
    guidesCount: 1250,
    travelersCount: 56800,
    rating: 4.9,
    region: 'asia'
  },
  {
    id: 2,
    name: '泰国',
    country: '泰国',
    city: '清迈',
    description: '微笑之国，热带风情与佛教文化的完美融合',
    image: '/img/长尾船.png',
    guidesCount: 980,
    travelersCount: 45200,
    rating: 4.7,
    region: 'asia'
  },
  {
    id: 3,
    name: '法国',
    country: '法国',
    city: '巴黎',
    description: '浪漫之都，艺术与时尚的殿堂',
    image: '/img/埃菲尔铁塔.jpg',
    guidesCount: 856,
    travelersCount: 38900,
    rating: 4.7,
    region: 'europe'
  },
  {
    id: 4,
    name: '意大利',
    country: '意大利',
    city: '罗马',
    description: '文艺复兴发源地，美食与历史的交响曲',
    image: '/img/比萨斜塔.png',
    guidesCount: 723,
    travelersCount: 32100,
    rating: 4.7,
    region: 'europe'
  },
  {
    id: 5,
    name: '希腊',
    country: '希腊',
    city: '圣托里尼',
    description: '蓝白相间的地中海风情，世界最美日落所在地',
    image: '/img/希腊圣托尼尼.png',
    guidesCount: 450,
    travelersCount: 25000,
    rating: 4.9,
    region: 'europe'
  },
  {
    id: 6,
    name: '澳大利亚',
    country: '澳大利亚',
    city: '悉尼',
    description: '海港城市，阳光海滩与现代建筑的完美结合',
    image: '/img/悉尼歌剧院.jpg',
    guidesCount: 650,
    travelersCount: 32000,
    rating: 4.8,
    region: 'oceania'
  },
  {
    id: 7,
    name: '中国',
    country: '中国',
    city: '大理',
    description: '风花雪月，云南慢生活的代表',
    image: '/img/希腊圣托尼尼.png',
    guidesCount: 560,
    travelersCount: 28000,
    rating: 4.6,
    region: 'asia'
  },
  {
    id: 8,
    name: '西班牙',
    country: '西班牙',
    city: '巴塞罗那',
    description: '高迪的建筑之城，地中海风情的艺术之都',
    image: '/img/巴塞罗亚.png',
    guidesCount: 690,
    travelersCount: 31000,
    rating: 4.8,
    region: 'europe'
  }
]

// 过滤后的目的地
const filteredDestinations = computed(() => {
  let result = destinations.value
  
  // 按地区筛选
  if (currentRegion.value !== 'all') {
    result = result.filter(dest => dest.region === currentRegion.value)
  }
  
  // 按搜索词筛选
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

// 返回首页
const goBack = () => {
  emit('back-to-home')
}

// 查看目的地详情
const viewDestinationDetail = (dest) => {
  emit('view-destination-detail', dest)
}

// 搜索
const handleSearch = () => {
  console.log('搜索:', searchQuery.value)
  currentPage.value = 1
}

// 设置地区筛选
const setRegion = (value) => {
  currentRegion.value = value
  currentPage.value = 1
}

// 切换页面
const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    fetchDestinations()
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchDestinations()
})
</script>

<style scoped>
/* 页面整体样式 */
.destination-guides-page {
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
  background: url('/img/埃菲尔铁塔.jpg') center/cover no-repeat;
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

/* 目的地区域 */
.destinations-section {
  padding: 60px 0;
  background-color: #f8f6f3;
  min-height: 50vh;
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

/* 目的地网格 */
.destinations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
}

.destination-card {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.4s ease;
}

.destination-card.hovered {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.2);
}

.destination-card img {
  width: 100%;
  height: 320px;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.destination-card.hovered img {
  transform: scale(1.1);
}

.dest-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 25px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  color: white;
}

.dest-info h3 {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 8px;
  font-family: 'Noto Serif SC', serif;
}

.dest-desc {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 12px;
  line-height: 1.5;
}

.dest-stats {
  display: flex;
  gap: 20px;
  font-size: 13px;
  opacity: 0.8;
}

.dest-rating {
  position: absolute;
  top: 15px;
  right: 15px;
  background: rgba(247, 149, 69, 0.9);
  color: white;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
}

.dest-rating .star {
  color: #ffd700;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 40px;
}

.page-btn {
  padding: 10px 24px;
  background: white;
  border: 2px solid rgba(247, 149, 69, 0.3);
  border-radius: 25px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  border-color: #f79545;
  color: #f79545;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header h1 {
    font-size: 32px;
  }

  .destinations-grid {
    grid-template-columns: 1fr;
  }

  .destination-card img {
    height: 250px;
  }

  .pagination {
    flex-wrap: wrap;
  }
}
</style>
