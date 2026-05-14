<template>
  <div class="hot-guides-page">
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
          <span class="hero-tag">EXPLORE</span>
          <h1>热门攻略</h1>
          <p>发现万千旅行者的精彩攻略与实用建议</p>
        </div>
        <div class="hero-stats">
          <div class="stat-item">
            <span class="stat-num">2.4k+</span>
            <span class="stat-label">优质攻略</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-num">128+</span>
            <span class="stat-label">热门目的地</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-num">50k+</span>
            <span class="stat-label">旅行者</span>
          </div>
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
              placeholder="搜索目的地、攻略..."
              @keyup.enter="handleSearch"
            >
            <button class="search-btn" @click="handleSearch">搜索</button>
          </div>
        </div>
      </div>
    </header>

    <!-- 搜索和筛选区 -->
    <section class="filter-section">
      <div class="container">
        <div class="filter-scroll">
          <button 
            v-for="tag in filterTags" 
            :key="tag.value"
            class="filter-chip"
            :class="{ active: currentFilter === tag.value }"
            @click="setFilter(tag.value)"
          >
            <span>{{ tag.label }}</span>
          </button>
          <div class="sort-group">
            <button
              class="sort-btn"
              :class="{ active: currentSort === 'latest' }"
              @click="currentSort = 'latest'; currentPage = 1; fetchHotGuides()"
            >最新</button>
            <button
              class="sort-btn"
              :class="{ active: currentSort === 'hot' }"
              @click="currentSort = 'hot'; currentPage = 1; fetchHotGuides()"
            >最热</button>
          </div>
        </div>
      </div>
    </section>

    <!-- 热门攻略区 -->
    <section class="guides-section">
      <div class="container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <div class="loader">
            <div class="loader-dot"></div>
            <div class="loader-dot"></div>
            <div class="loader-dot"></div>
          </div>
          <p>正在加载精彩攻略...</p>
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
          <button @click="fetchHotGuides" class="retry-btn">重新加载</button>
        </div>
        
        <!-- 空状态 -->
        <div v-else-if="hotGuides.length === 0" class="empty-state">
          <div class="empty-icon-wrap">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M9 12h6M12 9v6M3 12a9 9 0 1118 0 9 9 0 01-18 0z"/>
            </svg>
          </div>
          <p>暂无攻略数据</p>
        </div>
        
        <!-- 杂志风格攻略网格 -->
        <div v-else class="magazine-grid">
          <div 
            v-for="(guide, index) in hotGuides" 
            :key="guide.id"
            class="magazine-card"
            :class="[`card-${(index % 5) + 1}`]"
            @click="viewGuideDetail(guide.id)"
          >
            <div class="card-visual">
              <img :src="guide.image" :alt="guide.title" loading="lazy">
              <div class="card-shine"></div>
              <div class="card-badge">
                <svg viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z"/>
                </svg>
                {{ guide.location }}
              </div>
            </div>
            <div class="card-body">
              <h3>{{ guide.title }}</h3>
              <p>{{ guide.description }}</p>
              <div class="card-footer">
                <div class="author-chip">
                  <img :src="guide.authorAvatar" :alt="guide.author">
                  <span>{{ guide.author }}</span>
                </div>
                <div class="engagement">
                  <span class="eng-item like-eng-item" :class="{ liked: guide.isLiked }" @click.stop="toggleGuideLike(guide)">
                    <svg viewBox="0 0 24 24" :fill="guide.isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
                      <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                    </svg>
                    {{ guide.likes }}
                  </span>
                  <span class="eng-item">
                    <svg viewBox="0 0 24 24" fill="currentColor">
                      <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
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
          <div class="loader-small">
            <div class="loader-dot"></div>
            <div class="loader-dot"></div>
            <div class="loader-dot"></div>
          </div>
          <span>加载更多...</span>
        </div>

        <!-- 没有更多数据提示 -->
        <div v-else-if="hotGuides.length > 0 && currentPage >= totalPages" class="no-more">
          <div class="no-more-line"></div>
          <span>已经到底啦</span>
          <div class="no-more-line"></div>
        </div>

        <!-- 无限滚动触发器 -->
        <div ref="loadMoreTrigger" class="load-more-trigger"></div>
      </div>
    </section>

    <!-- 发布按钮 -->
    <button class="fab-publish" @click="checkLoginAndPublish">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M12 5v14M5 12h14"/>
      </svg>
      <span class="fab-text">发布攻略</span>
    </button>

    <!-- 发布模态框 -->
    <div class="modal-overlay" v-if="showPublishModal" @click.self="showPublishModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>发布攻略</h3>
          <button class="close-btn" @click="showPublishModal = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 6L6 18M6 6l12 12"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>标题</label>
            <input type="text" v-model="guideForm.title" placeholder="给你的攻略起个吸引人的标题">
          </div>
          <div class="form-group">
            <label>简介</label>
            <input type="text" v-model="guideForm.summary" placeholder="一句话描述你的攻略亮点">
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>目的地</label>
              <input type="text" v-model="guideForm.destinationName" placeholder="如：东京、巴黎、清迈">
            </div>
            <div class="form-group">
              <label>封面图片</label>
              <div class="upload-area" @click="triggerCoverUpload">
                <img v-if="coverPreview" :src="coverPreview" class="cover-preview">
                <div v-else class="upload-placeholder">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                  </svg>
                  <span>点击上传封面图片</span>
                </div>
                <input type="file" ref="coverFileInput" accept="image/*" style="display: none" @change="handleCoverUpload">
              </div>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>旅行范围</label>
              <select v-model="guideForm.scope">
                <option value="international">出境游</option>
                <option value="domestic">国内游</option>
              </select>
            </div>
            <div class="form-group">
              <label>旅行方式</label>
              <select v-model="guideForm.travelMode">
                <option value="free">自由行</option>
                <option value="group">跟团游</option>
                <option value="family">亲子游</option>
                <option value="honeymoon">蜜月游</option>
              </select>
            </div>
          </div>
          <div class="form-group">
            <label>标签（用逗号分隔）</label>
            <input type="text" v-model="guideForm.tags" placeholder="如：文化, 美食, 购物, 艺术">
          </div>
          <div class="form-group">
            <label>行程概览</label>
            <div class="itinerary-list">
              <div v-for="(day, index) in guideForm.itinerary" :key="index" class="itinerary-item">
                <div class="itinerary-header">
                  <span class="day-badge">Day {{ index + 1 }}</span>
                  <button class="remove-day-btn" @click="removeItineraryDay(index)" v-if="guideForm.itinerary.length > 1">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M18 6L6 18M6 6l12 12"/>
                    </svg>
                  </button>
                </div>
                <input type="text" v-model="day.title" placeholder="行程标题，如：抵达东京 & 浅草寺">
                <input type="text" v-model="day.description" placeholder="简短描述，如：感受传统日式文化">
                <!-- 景点列表 -->
                <div class="spots-list" v-if="day.spots && day.spots.length > 0">
                  <div v-for="(spot, spotIdx) in day.spots" :key="spotIdx" class="spot-input-item">
                    <div class="spot-top-row">
                      <div class="spot-image-upload" @click="triggerSpotUpload(index, spotIdx)">
                        <img v-if="spot.imageUrl" :src="spot.imageUrl" class="spot-image-preview">
                        <div v-else class="spot-image-placeholder">
                          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <path d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                          </svg>
                          <span>景点图片</span>
                        </div>
                      </div>
                      <div class="spot-fields">
                        <input type="text" v-model="spot.name" placeholder="景点名称（必填）" required>
                        <input type="text" v-model="spot.description" placeholder="景点描述">
                        <div class="spot-row">
                          <input type="text" v-model="spot.time" placeholder="时间 09:00">
                          <input type="text" v-model="spot.duration" placeholder="时长 2小时">
                        </div>
                      </div>
                    </div>
                    <button class="remove-spot-btn" @click="removeSpot(index, spotIdx)">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M18 6L6 18M6 6l12 12"/>
                      </svg>
                    </button>
                  </div>
                </div>
                <button class="add-spot-btn" @click="addSpot(index)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M12 5v14M5 12h14"/>
                  </svg>
                  添加景点
                </button>
              </div>
              <button class="add-day-btn" @click="addItineraryDay">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 5v14M5 12h14"/>
                </svg>
                添加一天
              </button>
            </div>
          </div>
          <div class="form-group">
            <label>详细攻略</label>
            <RichEditor v-model="guideForm.content" placeholder="分享你的详细旅行经历、攻略心得..." />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-ghost" @click="showPublishModal = false">取消</button>
          <button class="btn-accent" @click="publishGuide">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z"/>
            </svg>
            发布
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick, watch, inject } from 'vue'
import request from '../utils/request'
import { getCookie } from '../utils/cookie'
import RichEditor from './RichEditor.vue'

const toast = inject('toast')

const props = defineProps({
  initialSearch: { type: String, default: '' }
})

const emit = defineEmits(['back-to-home', 'view-guide-detail', 'switch-page'])

const searchQuery = ref('')
const currentFilter = ref('all')
const currentSort = ref('latest')
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

const showPublishModal = ref(false)
const coverFileInput = ref(null)
const coverPreview = ref('')
const guideForm = reactive({
  title: '',
  summary: '',
  content: '',
  destinationName: '',
  coverImage: null,
  scope: 'international',
  travelMode: 'free',
  tags: '',
  itinerary: [{ title: '', description: '', spots: [] }]
})

const hotGuides = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const error = ref(null)

const loadMoreTrigger = ref(null)
let observer = null

const fetchHotGuides = async (isLoadMore = false) => {
  if (isLoadMore) {
    loadingMore.value = true
  } else {
    loading.value = true
  }
  error.value = null
  
  try {
    const scopeValues = { domestic: 'domestic', international: 'international' }
    const travelModeValues = { free: 'free', group: 'group', family: 'family', honeymoon: 'honeymoon' }
    const scope = scopeValues[currentFilter.value] || ''
    const travelMode = travelModeValues[currentFilter.value] || ''
    let params = `page=${currentPage.value}&pageSize=12&sort=${currentSort.value}`
    if (searchQuery.value) params += `&keyword=${encodeURIComponent(searchQuery.value)}`
    if (scope) params += `&scope=${scope}`
    if (travelMode) params += `&travelMode=${travelMode}`
    const result = await request.get(`/api/guides/page?${params}`)
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
          views: item.viewsCount,
          isLiked: false
        }))
        if (isLoadMore) {
          hotGuides.value = [...hotGuides.value, ...newGuides]
        } else {
          hotGuides.value = newGuides
        }
        totalPages.value = result.data.totalPages || 1
      } else if (!isLoadMore) {
        hotGuides.value = []
      }
    } else {
      error.value = result.message || '获取攻略数据失败'
    }
  } catch (err) {
    error.value = '网络请求失败，请稍后重试'
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const loadNextPage = async () => {
  if (loadingMore.value || currentPage.value >= totalPages.value) return
  currentPage.value++
  await fetchHotGuides(true)
}

const setupInfiniteScroll = () => {
  if (observer) observer.disconnect()
  
  observer = new IntersectionObserver((entries) => {
    const entry = entries[0]
    if (entry.isIntersecting && !loading.value && !loadingMore.value && currentPage.value < totalPages.value) {
      loadNextPage()
    }
  }, { root: null, rootMargin: '100px', threshold: 0.1 })
  
  if (loadMoreTrigger.value) observer.observe(loadMoreTrigger.value)
}

const goBack = () => emit('back-to-home')
const viewGuideDetail = (guideId) => emit('view-guide-detail', guideId)

const toggleGuideLike = async (guide) => {
  const token = getCookie('token')
  if (!token) {
    if (confirm('请先登录后再点赞，是否前往登录？')) {
      emit('switch-page', 'login')
    }
    return
  }
  try {
    const result = guide.isLiked
      ? await request.delete(`/api/guides/${guide.id}/like`)
      : await request.post(`/api/guides/${guide.id}/like`)
    if (result.code === 'OK' || result.code === 200) {
      guide.isLiked = result.data.liked
      guide.likes = result.data.likesCount
    }
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchHotGuides()
}

const setFilter = (value) => {
  currentFilter.value = value
  currentPage.value = 1
  fetchHotGuides()
}

const resetObserver = () => nextTick(() => setupInfiniteScroll())

const checkLoginAndPublish = () => {
  const token = getCookie('token')
  if (!token) {
    if (confirm('请先登录后再发布攻略，是否前往登录？')) {
      emit('switch-page', 'login')
    }
    return
  }
  showPublishModal.value = true
}

const triggerCoverUpload = () => coverFileInput.value?.click()

const handleCoverUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    guideForm.coverImage = file
    const reader = new FileReader()
    reader.onload = (e) => { coverPreview.value = e.target.result }
    reader.readAsDataURL(file)
  }
}

const addItineraryDay = () => {
  guideForm.itinerary.push({ title: '', description: '', spots: [] })
}

const removeItineraryDay = (index) => {
  guideForm.itinerary.splice(index, 1)
}

const addSpot = (dayIndex) => {
  if (!guideForm.itinerary[dayIndex].spots) {
    guideForm.itinerary[dayIndex].spots = []
  }
  guideForm.itinerary[dayIndex].spots.push({ name: '', description: '', time: '', duration: '', imageUrl: '', imageFile: null })
}

const removeSpot = (dayIndex, spotIndex) => {
  guideForm.itinerary[dayIndex].spots.splice(spotIndex, 1)
}

const triggerSpotUpload = (dayIndex, spotIndex) => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = (e) => {
    const file = e.target.files[0]
    if (file) {
      guideForm.itinerary[dayIndex].spots[spotIndex].imageFile = file
      guideForm.itinerary[dayIndex].spots[spotIndex].imageUrl = URL.createObjectURL(file)
    }
  }
  input.click()
}

const publishGuide = async () => {
  if (!guideForm.title.trim()) { toast.warning('请输入攻略标题'); return }
  if (!guideForm.destinationName.trim()) { toast.warning('请输入目的地'); return }
  for (let i = 0; i < guideForm.itinerary.length; i++) {
    const day = guideForm.itinerary[i]
    for (const spot of (day.spots || [])) {
      if (!spot.name.trim()) {
        toast.warning(`第${i + 1}天有景点名称未填写，请补充`)
        return
      }
    }
  }
  try {
    // 先上传封面图片
    let coverImageUrl = null
    if (guideForm.coverImage) {
      const formData = new FormData()
      formData.append('file', guideForm.coverImage)
      const uploadResult = await request.post('/api/files/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
      if (uploadResult.code === 'OK' || uploadResult.code === 200) {
        coverImageUrl = uploadResult.data
      }
    }

    // 构建行程数据（上传景点图片）
    const itineraryDays = []
    for (let i = 0; i < guideForm.itinerary.length; i++) {
      const day = guideForm.itinerary[i]
      const spots = []
      for (const spot of (day.spots || [])) {
        let spotImageUrl = null
        if (spot.imageFile) {
          const fd = new FormData()
          fd.append('file', spot.imageFile)
          const res = await request.post('/api/files/upload', fd, {
            headers: { 'Content-Type': 'multipart/form-data' }
          })
          if (res.code === 'OK' || res.code === 200) {
            spotImageUrl = res.data
          }
        }
        spots.push({
          name: spot.name,
          description: spot.description,
          imageUrl: spotImageUrl,
          time: spot.time,
          duration: spot.duration
        })
      }
      itineraryDays.push({
        dayNo: i + 1,
        title: day.title || `第${i + 1}天`,
        summary: day.description,
        spots
      })
    }

    // 构建标签数据
    const tagNames = guideForm.tags 
      ? guideForm.tags.split(',').map(t => t.trim()).filter(t => t) 
      : []

    // 提交攻略
    const guideData = {
      title: guideForm.title,
      summary: guideForm.summary || guideForm.content.substring(0, 200),
      contentHtml: guideForm.content,
      destinationName: guideForm.destinationName.trim(),
      coverImageUrl: coverImageUrl,
      scope: guideForm.scope,
      travelMode: guideForm.travelMode,
      tagNames: tagNames,
      itineraryDays: itineraryDays
    }
    
    const result = await request.post('/api/guides', guideData)
    if (result.code === 'OK' || result.code === 200) {
      showPublishModal.value = false
      Object.assign(guideForm, {
        title: '', summary: '', content: '', destinationName: '', coverImage: null,
        scope: 'international', travelMode: 'free', tags: '',
        itinerary: [{ title: '', description: '', spots: [] }]
      })
      coverPreview.value = ''
      currentPage.value = 1
      fetchHotGuides()
      toast.success('攻略发布成功！')
    } else {
      toast.error('发布失败：' + (result.message || '未知错误'))
    }
  } catch (err) {
    console.error('发布攻略失败:', err)
    toast.error('发布失败：' + (err.response?.data?.message || err.message || '请先登录后再发布攻略'))
  }
}

onMounted(() => {
  if (props.initialSearch) searchQuery.value = props.initialSearch
  fetchHotGuides()
  resetObserver()
})

watch(() => props.initialSearch, (val) => {
  if (val) { searchQuery.value = val; currentPage.value = 1; fetchHotGuides() }
})

onUnmounted(() => { if (observer) observer.disconnect() })
</script>

<style scoped>
.hot-guides-page {
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
  background-image: url('/img/back1.png');
  background-size: cover;
  background-position: center;
}

.hero-gradient {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(to bottom, rgba(45, 58, 30, 0.3) 0%, rgba(45, 58, 30, 0.5) 100%);
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
  text-shadow: 0 4px 20px rgba(45,58,30,0.3);
}

.hero-text p {
  font-size: 18px;
  color: rgba(255,255,255,0.85);
  max-width: 500px;
  line-height: 1.6;
}

.hero-stats {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 20px 28px;
  background: rgba(255,255,255,0.1);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  border: 1px solid rgba(255,255,255,0.15);
  width: fit-content;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-num {
  font-size: 24px;
  font-weight: 700;
  color: #F5F0E8;
  font-family: var(--font-display);
}

.stat-label {
  font-size: 13px;
  color: rgba(255,255,255,0.7);
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(255,255,255,0.2);
}

/* Filter Section */
.filter-section {
  padding: 32px 0;
  position: relative;
  z-index: 4;
}

.hero-search {
  max-width: 560px;
  margin-top: 8px;
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
  font-size: 15px;
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

.filter-scroll {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding: 4px 0;
  scrollbar-width: none;
  -ms-overflow-style: none;
  justify-content: center;
}

.filter-scroll::-webkit-scrollbar {
  display: none;
}

.filter-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: rgba(255,255,255,0.6);
  border: 1px solid var(--color-card-border);
  border-radius: 30px;
  color: var(--color-text-secondary);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  flex-shrink: 0;
}

.filter-chip:hover {
  background: rgba(255,255,255,0.9);
  border-color: var(--color-primary);
  color: var(--color-primary-dark);
}

.filter-chip.active {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: #FFFFFF;
}

.sort-group {
  display: flex;
  gap: 4px;
  margin-left: auto;
  flex-shrink: 0;
}

.sort-btn {
  padding: 8px 16px;
  background: rgba(255,255,255,0.4);
  border: 1px solid var(--color-card-border);
  color: var(--color-text-secondary);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sort-btn:first-child {
  border-radius: 20px 0 0 20px;
}

.sort-btn:last-child {
  border-radius: 0 20px 20px 0;
}

.sort-btn.active {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: #fff;
}

/* Guides Section */
.guides-section {
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

/* Magazine Grid */
.magazine-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-auto-rows: 280px;
  gap: 20px;
}

.magazine-card {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.5s cubic-bezier(0.23, 1, 0.32, 1);
  animation: cardFadeIn 0.6s ease backwards;
}

.magazine-card:nth-child(1) { animation-delay: 0.1s; }
.magazine-card:nth-child(2) { animation-delay: 0.2s; }
.magazine-card:nth-child(3) { animation-delay: 0.3s; }
.magazine-card:nth-child(4) { animation-delay: 0.4s; }
.magazine-card:nth-child(5) { animation-delay: 0.5s; }

@keyframes cardFadeIn {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

.magazine-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 60px rgba(45,58,30,0.4);
  z-index: 10;
}

/* Card Variations */
.card-1 { grid-column: span 2; grid-row: span 2; }
.card-2 { grid-column: span 1; grid-row: span 2; }
.card-3 { grid-column: span 1; grid-row: span 1; }
.card-4 { grid-column: span 1; grid-row: span 1; }
.card-5 { grid-column: span 2; grid-row: span 1; }

.card-visual {
  position: absolute;
  inset: 0;
}

.card-visual img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s cubic-bezier(0.23, 1, 0.32, 1);
}

.magazine-card:hover .card-visual img {
  transform: scale(1.1);
}

.card-shine {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    rgba(255,255,255,0.1) 0%,
    transparent 50%,
    rgba(0,0,0,0.3) 100%
  );
}

.card-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: rgba(0,0,0,0.5);
  backdrop-filter: blur(8px);
  border-radius: 20px;
  color: white;
  font-size: 13px;
  font-weight: 500;
}

.card-badge svg {
  width: 14px;
  height: 14px;
}

.card-body {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 24px;
  background: linear-gradient(transparent, rgba(0,0,0,0.85));
  transform: translateY(20px);
  opacity: 0;
  transition: all 0.4s ease;
}

.magazine-card:hover .card-body {
  transform: translateY(0);
  opacity: 1;
}

.card-body h3 {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 600;
  color: white;
  margin: 0 0 8px;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-body p {
  font-size: 14px;
  color: rgba(255,255,255,0.8);
  line-height: 1.5;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author-chip {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author-chip img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255,255,255,0.3);
}

.author-chip span {
  font-size: 13px;
  color: rgba(255,255,255,0.9);
  font-weight: 500;
}

.engagement {
  display: flex;
  gap: 12px;
}

.eng-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: rgba(255,255,255,0.7);
}

.eng-item.like-eng-item {
  cursor: pointer;
  transition: all 0.3s ease;
}

.eng-item.like-eng-item:hover {
  color: #e07070;
  transform: scale(1.1);
}

.eng-item.like-eng-item.liked {
  color: #e07070;
}

.eng-item svg {
  width: 14px;
  height: 14px;
}

/* Loading More */
.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 40px 20px;
  color: #A8A29E;
  font-size: 14px;
}

.loader-small {
  display: flex;
  gap: 4px;
}

.loader-small .loader-dot {
  width: 8px;
  height: 8px;
}

/* No More */
.no-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 40px 20px;
  color: #A8A29E;
  font-size: 14px;
}

.no-more-line {
  width: 60px;
  height: 1px;
  background: rgba(255,255,255,0.15);
}

.load-more-trigger {
  height: 20px;
}

/* FAB Publish */
.fab-publish {
  position: fixed;
  bottom: 32px;
  right: 32px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 28px;
  background: linear-gradient(135deg, #F5F0E8 0%, #FAF8F5 100%);
  color: var(--color-text-primary);
  border: none;
  border-radius: 50px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 8px 30px rgba(245,240,232,0.4);
  transition: all 0.3s ease;
  z-index: 100;
}

.fab-publish svg {
  width: 20px;
  height: 20px;
}

.fab-publish:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 40px rgba(245,240,232,0.5);
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(8px);
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  background: var(--color-card-bg);
  border: 1px solid var(--color-card-border);
  border-radius: 20px;
  width: 96%;
  max-width: 960px;
  height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 24px 80px rgba(45,58,30,0.2);
  animation: modalSlideUp 0.35s cubic-bezier(0.23, 1, 0.32, 1);
}

@keyframes modalSlideUp {
  from { opacity: 0; transform: translateY(40px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 32px;
  border-bottom: 1px solid var(--color-card-border);
  flex-shrink: 0;
}

.modal-header h3 {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-text-primary);
  font-family: var(--font-display);
}

.close-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-tertiary);
  border: none;
  border-radius: 50%;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: var(--color-card-border);
  color: var(--color-text-primary);
}

.close-btn svg {
  width: 20px;
  height: 20px;
}

.modal-body {
  padding: 24px 32px;
  flex: 1;
  overflow-y: auto;
}

/* 让详细攻略所在的form-group撑满 */
.modal-body .form-group:last-child {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.modal-body .form-group:last-child .rich-editor {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.modal-body .form-group:last-child .editor-content {
  flex: 1;
}

.modal-body .form-group:last-child .editor-content .ProseMirror {
  min-height: 300px;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-secondary);
  margin-bottom: 10px;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 14px 18px;
  border: 1px solid var(--color-card-border);
  border-radius: 12px;
  font-size: 15px;
  background: var(--color-bg-secondary);
  color: var(--color-text-primary);
  transition: all 0.3s ease;
  outline: none;
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%236B8A52' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 16px center;
  padding-right: 40px;
}

.form-group select option {
  background: var(--color-card-bg);
  color: var(--color-text-primary);
  padding: 12px;
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: var(--color-text-muted);
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(91,140,62,0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 140px;
}

.form-group select {
  cursor: pointer;
}

/* 上传区域 */
.upload-area {
  border: 2px dashed var(--color-card-border);
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
}

.upload-area:hover {
  border-color: var(--color-primary);
  background: var(--color-bg-secondary);
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: var(--color-text-muted);
}

.upload-placeholder svg {
  width: 36px;
  height: 36px;
}

.upload-placeholder span {
  font-size: 14px;
}

.cover-preview {
  width: 100%;
  max-height: 160px;
  object-fit: cover;
  border-radius: 8px;
}

/* 行程概览 */
.itinerary-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.itinerary-item {
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-card-border);
  border-radius: 12px;
  padding: 16px;
}

.itinerary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.day-badge {
  display: inline-block;
  padding: 4px 12px;
  background: var(--color-primary);
  color: #FFFFFF;
  font-size: 12px;
  font-weight: 600;
  border-radius: 6px;
  letter-spacing: 0.5px;
}

.remove-day-btn {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(224,112,112,0.15);
  border: none;
  border-radius: 6px;
  color: #E07070;
  cursor: pointer;
  transition: all 0.2s ease;
}

.remove-day-btn:hover {
  background: rgba(224,112,112,0.25);
}

.remove-day-btn svg {
  width: 14px;
  height: 14px;
}

.itinerary-item input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid var(--color-card-border);
  border-radius: 8px;
  font-size: 14px;
  background: var(--color-card-bg);
  color: var(--color-text-primary);
  transition: all 0.3s ease;
  outline: none;
  margin-bottom: 8px;
}

.itinerary-item input:last-child {
  margin-bottom: 0;
}

.itinerary-item input::placeholder {
  color: var(--color-text-muted);
}

.itinerary-item input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(91,140,62,0.1);
}

.add-day-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  background: transparent;
  border: 2px dashed var(--color-card-border);
  border-radius: 12px;
  color: var(--color-text-muted);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.add-day-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: var(--color-bg-secondary);
}

.add-day-btn svg {
  width: 18px;
  height: 18px;
}

/* 景点列表样式 */
.spots-list {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed var(--color-card-border);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.spot-input-item {
  background: var(--color-card-bg);
  border: 1px solid var(--color-card-border);
  border-radius: 8px;
  padding: 12px;
  position: relative;
}

.spot-input-item input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid var(--color-card-border);
  border-radius: 6px;
  font-size: 13px;
  background: var(--color-bg-secondary);
  color: var(--color-text-primary);
  margin-bottom: 8px;
}

.spot-input-item input:last-child {
  margin-bottom: 0;
}

.spot-input-item input::placeholder {
  color: var(--color-text-muted);
}

.spot-input-item input:focus {
  border-color: var(--color-primary);
  outline: none;
}

.spot-top-row {
  display: flex;
  gap: 12px;
}

.spot-image-upload {
  width: 90px;
  height: 90px;
  flex-shrink: 0;
  border: 2px dashed var(--color-card-border);
  border-radius: 8px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
}

.spot-image-upload:hover {
  border-color: var(--color-primary);
}

.spot-image-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.spot-image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  color: var(--color-text-muted);
}

.spot-image-placeholder svg {
  width: 24px;
  height: 24px;
}

.spot-image-placeholder span {
  font-size: 11px;
}

.spot-fields {
  flex: 1;
  min-width: 0;
}

.spot-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.spot-row input {
  margin-bottom: 0;
}

.remove-spot-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(224,112,112,0.15);
  border: none;
  border-radius: 4px;
  color: #E07070;
  cursor: pointer;
  transition: all 0.2s ease;
}

.remove-spot-btn:hover {
  background: rgba(224,112,112,0.25);
}

.remove-spot-btn svg {
  width: 12px;
  height: 12px;
}

.add-spot-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px;
  margin-top: 10px;
  background: transparent;
  border: 1px dashed var(--color-card-border);
  border-radius: 8px;
  color: var(--color-text-muted);
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.add-spot-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.add-spot-btn svg {
  width: 14px;
  height: 14px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 32px;
  border-top: 1px solid var(--color-card-border);
  flex-shrink: 0;
}

.btn-ghost {
  padding: 12px 24px;
  background: transparent;
  border: 1px solid var(--color-card-border);
  border-radius: 30px;
  font-size: 15px;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-ghost:hover {
  background: var(--color-bg-tertiary);
  border-color: var(--color-card-border);
}

.btn-accent {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  background: var(--gradient-secondary);
  border: none;
  border-radius: 30px;
  font-size: 15px;
  font-weight: 600;
  color: #FFFFFF;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-accent svg {
  width: 18px;
  height: 18px;
}

.btn-accent:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(45,58,30,0.2);
}

/* Responsive */
@media (max-width: 1200px) {
  .magazine-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .card-1 { grid-column: span 2; grid-row: span 2; }
  .card-2 { grid-column: span 1; grid-row: span 2; }
  .card-3, .card-4 { grid-column: span 1; grid-row: span 1; }
  .card-5 { grid-column: span 2; grid-row: span 1; }
}

@media (max-width: 900px) {
  .magazine-grid {
    grid-template-columns: repeat(2, 1fr);
    grid-auto-rows: 240px;
  }
  .card-1 { grid-column: span 2; grid-row: span 1; }
  .card-2 { grid-column: span 1; grid-row: span 1; }
  .card-3, .card-4, .card-5 { grid-column: span 1; grid-row: span 1; }
  
  .hero-content {
    padding: 32px 40px 40px;
  }
  
  .hero-stats {
    flex-wrap: wrap;
    gap: 16px;
  }
  
  .stat-divider {
    display: none;
  }
}

@media (max-width: 600px) {
  .magazine-grid {
    grid-template-columns: 1fr;
    grid-auto-rows: 280px;
  }
  .card-1, .card-2, .card-3, .card-4, .card-5 {
    grid-column: span 1;
    grid-row: span 1;
  }
  
  .hero-content {
    padding: 24px 24px 32px;
  }
  
  .hero-stats {
    width: 100%;
  }
  
  .stat-item {
    flex: 1;
    text-align: center;
  }
  
  .filter-scroll {
    justify-content: flex-start;
    padding: 0 20px;
    margin: 0 -20px;
  }
  
  .fab-text {
    display: none;
  }
  
  .fab-publish {
    padding: 16px;
    border-radius: 50%;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
