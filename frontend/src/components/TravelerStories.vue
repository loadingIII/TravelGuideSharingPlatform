<template>
  <div class="traveler-stories-page">
    <!-- 沉浸式页面头部 -->
    <header class="hero-header">
      <div class="hero-bg">
        <img src="/img/富士山.jpg" alt="富士山" class="hero-image">
        <div class="hero-overlay"></div>
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
          <span class="hero-tag">STORIES</span>
          <h1>旅行者故事</h1>
          <p>分享旅途中的精彩瞬间与难忘经历</p>
        </div>
      </div>
      <div class="hero-wave">
        <svg viewBox="0 0 1440 100" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M0 100L48 94C96 88 192 76 288 70C384 64 480 64 576 68C672 72 768 80 864 82C960 84 1056 80 1152 74C1248 68 1344 60 1392 56L1440 52V100H0Z" fill="#3D4F2F"/>
        </svg>
      </div>
    </header>

    <!-- 故事列表区 -->
    <section class="stories-section">
      <div class="container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <div class="loader">
            <div class="loader-dot"></div>
            <div class="loader-dot"></div>
            <div class="loader-dot"></div>
          </div>
          <p>正在加载旅行者故事...</p>
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
          <button @click="fetchStories" class="retry-btn">重新加载</button>
        </div>
        
        <!-- 空状态 -->
        <div v-else-if="travelerStories.length === 0" class="empty-state">
          <div class="empty-icon-wrap">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
            </svg>
          </div>
          <p>暂无旅行者故事</p>
        </div>
        
        <!-- 故事卡片网格 -->
        <div v-else class="stories-masonry">
          <article 
            v-for="story in travelerStories" 
            :key="story.id"
            class="story-card"
            @mouseenter="hoverStory = story.id"
            @mouseleave="hoverStory = null"
            :class="{ hovered: hoverStory === story.id }"
          >
            <!-- 作者头部 -->
            <div class="story-author">
              <div class="author-avatar">
                <img :src="story.avatar" :alt="story.author">
                <div class="vip-ring" v-if="story.isVip"></div>
              </div>
              <div class="author-meta">
                <div class="author-name">
                  <span>{{ story.author }}</span>
                  <svg v-if="story.isVip" class="vip-badge" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z"/>
                  </svg>
                </div>
                <span class="story-time">{{ story.time }}</span>
              </div>
              <button class="more-btn">
                <svg viewBox="0 0 24 24" fill="currentColor">
                  <circle cx="12" cy="5" r="2"/>
                  <circle cx="12" cy="12" r="2"/>
                  <circle cx="12" cy="19" r="2"/>
                </svg>
              </button>
            </div>
            
            <!-- 故事内容 -->
            <div class="story-body">
              <p class="story-text">{{ story.content }}</p>
            </div>
            
            <!-- 图片画廊 -->
            <div class="story-gallery" v-if="story.images && story.images.length > 0" :class="`gallery-${Math.min(story.images.length, 3)}`">
              <div 
                v-for="(img, index) in story.images.slice(0, 4)" 
                :key="index"
                class="gallery-item"
                :class="{ 'has-more': index === 3 && story.images.length > 4 }"
                @click="openImagePreview(story.images, index)"
              >
                <img :src="img" :alt="`图片${index + 1}`" loading="lazy">
                <div class="gallery-overlay">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M15 3h6v6M9 21H3v-6M21 3l-7 7M3 21l7-7"/>
                  </svg>
                </div>
                <div class="more-count" v-if="index === 3 && story.images.length > 4">
                  <span>+{{ story.images.length - 4 }}</span>
                </div>
              </div>
            </div>
            
            <!-- 操作栏 -->
            <div class="story-actions">
              <button 
                class="action-btn like-btn" 
                :class="{ active: story.isLiked, animating: animatingLike === story.id }" 
                @click.stop="toggleLike(story)"
              >
                <div class="action-icon">
                  <svg viewBox="0 0 24 24" :fill="story.isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                  </svg>
                  <div class="like-particles" v-if="story.isLiked">
                    <span v-for="n in 8" :key="n" class="particle"></span>
                  </div>
                </div>
                <span>{{ formatNumber(story.likes) }}</span>
              </button>
              
              <button class="action-btn comment-btn" @click.stop="showComments(story)">
                <div class="action-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
                  </svg>
                </div>
                <span>{{ formatNumber(story.comments) }}</span>
              </button>
              
              <button class="action-btn share-btn" @click.stop="shareStory(story)">
                <div class="action-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8M16 6l-4-4-4 4M12 2v13"/>
                  </svg>
                </div>
                <span>分享</span>
              </button>
            </div>
          </article>
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

    <!-- 发布按钮 -->
    <button class="fab-publish" @click="showPublishModal = true">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M12 5v14M5 12h14"/>
      </svg>
      <span class="fab-text">分享故事</span>
    </button>

    <!-- 发布模态框 -->
    <div class="modal-overlay" v-if="showPublishModal" @click.self="showPublishModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>分享旅行故事</h3>
          <button class="close-btn" @click="showPublishModal = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 6L6 18M6 6l12 12"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>分享你的故事</label>
            <textarea rows="6" placeholder="记录你的旅行经历、心情和感悟..." v-model="newStoryContent"></textarea>
          </div>
          <div class="form-group">
            <label>上传图片</label>
            <div class="upload-area" @click="triggerFileInput">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"/>
              </svg>
              <span>点击或拖拽上传图片</span>
              <input type="file" ref="fileInput" multiple accept="image/*" style="display: none" @change="handleFileChange">
            </div>
            <div class="preview-images" v-if="previewImages.length > 0">
              <div v-for="(img, index) in previewImages" :key="index" class="preview-item">
                <img :src="img" alt="预览">
                <button class="remove-btn" @click="removeImage(index)">&times;</button>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-ghost" @click="showPublishModal = false">取消</button>
          <button class="btn-accent" @click="publishStory">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z"/>
            </svg>
            发布
          </button>
        </div>
      </div>
    </div>

    <!-- 图片预览模态框 -->
    <div class="image-preview-modal" v-if="previewImageList.length > 0" @click.self="closeImagePreview">
      <button class="preview-close" @click="closeImagePreview">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M18 6L6 18M6 6l12 12"/>
        </svg>
      </button>
      <button class="preview-nav prev" @click.stop="prevImage" v-if="previewImageList.length > 1">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M15 18l-6-6 6-6"/>
        </svg>
      </button>
      <div class="preview-image-wrap">
        <img :src="previewImageList[currentPreviewIndex]" alt="预览">
      </div>
      <button class="preview-nav next" @click.stop="nextImage" v-if="previewImageList.length > 1">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M9 18l6-6-6-6"/>
        </svg>
      </button>
      <div class="preview-counter">{{ currentPreviewIndex + 1 }} / {{ previewImageList.length }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const emit = defineEmits(['back-to-home'])

const currentPage = ref(1)
const totalPages = ref(1)
const hoverStory = ref(null)
const animatingLike = ref(null)

const showPublishModal = ref(false)
const newStoryContent = ref('')
const previewImages = ref([])
const fileInput = ref(null)

const previewImageList = ref([])
const currentPreviewIndex = ref(0)

const travelerStories = ref([])
const loading = ref(false)
const error = ref(null)

const fetchStories = async () => {
  loading.value = true
  error.value = null
  
  try {
    const result = await request.get(`/api/stories/page?page=${currentPage.value}&pageSize=10`)
    if (result.code === 200 || result.code === 'OK') {
      if (result.data && result.data.list && result.data.list.length > 0) {
        travelerStories.value = result.data.list.map(item => ({
          id: item.id,
          author: item.authorName,
          avatar: item.authorAvatarUrl,
          isVip: item.isVip,
          time: formatTime(item.createdAt),
          content: item.content,
          images: item.images || [],
          likes: item.likesCount,
          comments: item.commentsCount,
          isLiked: false
        }))
        totalPages.value = result.data.totalPages || 1
      } else {
        travelerStories.value = getDefaultStories()
        totalPages.value = 1
      }
    } else {
      travelerStories.value = getDefaultStories()
      totalPages.value = 1
    }
  } catch (err) {
    travelerStories.value = getDefaultStories()
    totalPages.value = 1
  } finally {
    loading.value = false
  }
}

const getDefaultStories = () => [
  {
    id: 1,
    author: '旅行达人李',
    avatar: '/img/头像1.jpg',
    isVip: true,
    time: '2小时前',
    content: '刚从冰岛回来，极光真的太震撼了！分享一下我的追光攻略：最佳时间是9月到次年3月，建议租一辆车自驾，这样可以灵活选择观赏地点。记得下载极光预报APP，KP值大于3的时候看到的机会就很大。',
    images: ['/img/纽约城市.jpg', '/img/悉尼歌剧院.jpg', '/img/富士山.jpg', '/img/埃菲尔铁塔.jpg'],
    likes: 456,
    comments: 89,
    isLiked: false
  },
  {
    id: 2,
    author: '摄影师阿明',
    avatar: '/img/头像2.png',
    isVip: false,
    time: '5小时前',
    content: '在巴塞罗那拍到了圣家堂最美的光线！高迪的建筑真的让人叹为观止，每一个细节都充满了想象力。建议早上8点前去，人少光线好，拍照效果最佳。',
    images: ['/img/巴塞罗亚.png'],
    likes: 328,
    comments: 56,
    isLiked: true
  },
  {
    id: 3,
    author: '美食探索家',
    avatar: '/img/头像3.png',
    isVip: true,
    time: '昨天',
    content: '泰国街头美食真的太棒了！曼谷的夜市是吃货的天堂，从芒果糯米饭到冬阴功汤，每一口都是惊喜。推荐去拉差达火车夜市，不仅美食多，拍照也很出片。',
    images: ['/img/长尾船.png', '/img/比萨斜塔.png'],
    likes: 567,
    comments: 123,
    isLiked: false
  }
]

const formatTime = (timestamp) => {
  if (!timestamp) return '刚刚'
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

const goBack = () => emit('back-to-home')

const formatNumber = (num) => {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toString()
}

const toggleLike = async (story) => {
  try {
    const action = story.isLiked ? 'delete' : 'post'
    const result = await request[action](`/api/stories/${story.id}/like`)
    if (result.code === 'OK') {
      story.isLiked = result.data.liked
      story.likes = result.data.likesCount
      if (story.isLiked) {
        animatingLike.value = story.id
        setTimeout(() => { animatingLike.value = null }, 600)
      }
    }
  } catch (err) {
    alert('请先登录后再点赞')
  }
}

const showComments = (story) => alert(`查看 ${story.author} 的故事评论 (${story.comments}条)`)
const shareStory = (story) => alert(`分享 ${story.author} 的故事`)

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    fetchStories()
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

const triggerFileInput = () => fileInput.value?.click()

const handleFileChange = (event) => {
  const files = event.target.files
  if (files) {
    Array.from(files).forEach(file => {
      const reader = new FileReader()
      reader.onload = (e) => previewImages.value.push(e.target.result)
      reader.readAsDataURL(file)
    })
  }
}

const removeImage = (index) => previewImages.value.splice(index, 1)

const publishStory = async () => {
  if (!newStoryContent.value.trim()) { alert('请输入故事内容'); return }
  try {
    const result = await request.post('/api/stories', { content: newStoryContent.value, imageUrls: [] })
    if (result.code === 'OK') {
      showPublishModal.value = false
      newStoryContent.value = ''
      previewImages.value = []
      fetchStories()
      alert('故事发布成功！')
    }
  } catch (err) {
    alert('请先登录后再发布故事')
  }
}

const openImagePreview = (images, index) => {
  previewImageList.value = images
  currentPreviewIndex.value = index
}

const closeImagePreview = () => {
  previewImageList.value = []
  currentPreviewIndex.value = 0
}

const prevImage = () => {
  currentPreviewIndex.value = currentPreviewIndex.value > 0 
    ? currentPreviewIndex.value - 1 
    : previewImageList.value.length - 1
}

const nextImage = () => {
  currentPreviewIndex.value = currentPreviewIndex.value < previewImageList.value.length - 1 
    ? currentPreviewIndex.value + 1 
    : 0
}

onMounted(() => fetchStories())
</script>

<style scoped>
.traveler-stories-page {
  min-height: 100vh;
  background-color: #3D4F2F;
  font-family: var(--font-body);
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 24px;
}

/* Hero Header */
.hero-header {
  position: relative;
  height: 60vh;
  min-height: 400px;
  overflow: hidden;
  display: flex;
  align-items: flex-end;
}

.hero-bg {
  position: absolute;
  inset: 0;
}

.hero-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transform: scale(1.05);
  animation: heroZoom 20s ease-in-out infinite alternate;
}

@keyframes heroZoom {
  0% { transform: scale(1.05); }
  100% { transform: scale(1.15); }
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    180deg,
    rgba(0,0,0,0.1) 0%,
    rgba(0,0,0,0.3) 40%,
    rgba(0,0,0,0.7) 100%
  );
}

.hero-grain {
  position: absolute;
  inset: 0;
  opacity: 0.15;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)' opacity='0.5'/%3E%3C/svg%3E");
}

.hero-content {
  position: relative;
  z-index: 2;
  padding: 40px 60px 60px;
  max-width: 900px;
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
  margin-bottom: 0;
}

.hero-tag {
  display: inline-block;
  padding: 6px 16px;
  background: linear-gradient(135deg, #F5F0E8, #FAF8F5);
  color: #2F3D24;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 3px;
  border-radius: 20px;
  margin-bottom: 16px;
}

.hero-text h1 {
  font-family: var(--font-display);
  font-size: clamp(36px, 5vw, 60px);
  color: white;
  margin: 0 0 16px;
  line-height: 1.1;
  letter-spacing: -1px;
  text-shadow: 0 4px 20px rgba(0,0,0,0.3);
}

.hero-text p {
  font-size: 18px;
  color: rgba(255,255,255,0.85);
  max-width: 400px;
  line-height: 1.6;
  margin: 0;
}

.hero-wave {
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  z-index: 3;
}

.hero-wave svg {
  display: block;
  width: 100%;
}

/* Stories Section */
.stories-section {
  padding: 40px 0 80px;
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
  color: #D4CFC7;
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
  color: #2F3D24;
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
  background: rgba(255,255,255,0.06);
  border-radius: 24px;
  margin-bottom: 20px;
}

.empty-icon-wrap svg {
  width: 40px;
  height: 40px;
}

/* Stories Masonry */
.stories-masonry {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.story-card {
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 24px;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  animation: cardSlideIn 0.6s ease backwards;
}

.story-card:nth-child(1) { animation-delay: 0.1s; }
.story-card:nth-child(2) { animation-delay: 0.2s; }
.story-card:nth-child(3) { animation-delay: 0.3s; }

@keyframes cardSlideIn {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

.story-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
  border-color: rgba(245,240,232,0.2);
}

/* Story Author */
.story-author {
  display: flex;
  align-items: center;
  padding: 20px 24px;
}

.author-avatar {
  position: relative;
  width: 48px;
  height: 48px;
  flex-shrink: 0;
}

.author-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255,255,255,0.15);
}

.vip-ring {
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  background: linear-gradient(135deg, #F5F0E8, #FAF8F5, #ffd700);
  z-index: -1;
  animation: ringRotate 3s linear infinite;
}

@keyframes ringRotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.author-meta {
  flex: 1;
  margin-left: 14px;
}

.author-name {
  display: flex;
  align-items: center;
  gap: 6px;
}

.author-name span {
  font-size: 15px;
  font-weight: 600;
  color: #F5F2ED;
}

.vip-badge {
  width: 16px;
  height: 16px;
  color: #ffd700;
}

.story-time {
  font-size: 13px;
  color: #A8A29E;
}

.more-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  border-radius: 50%;
  color: #A8A29E;
  cursor: pointer;
  transition: all 0.3s ease;
}

.more-btn:hover {
  background: rgba(255,255,255,0.1);
  color: #F5F2ED;
}

.more-btn svg {
  width: 20px;
  height: 20px;
}

/* Story Body */
.story-body {
  padding: 0 24px 16px;
}

.story-text {
  font-size: 15px;
  color: #D4CFC7;
  line-height: 1.75;
  margin: 0;
  letter-spacing: 0.01em;
}

/* Story Gallery */
.story-gallery {
  display: grid;
  gap: 4px;
  padding: 0 4px;
  margin-bottom: 16px;
}

.gallery-1 { grid-template-columns: 1fr; }
.gallery-2 { grid-template-columns: 1fr 1fr; }
.gallery-3 { grid-template-columns: 1fr 1fr; grid-template-rows: 1fr 1fr; }
.gallery-3 .gallery-item:first-child { grid-row: span 2; }

.gallery-item {
  position: relative;
  aspect-ratio: 1;
  overflow: hidden;
  cursor: pointer;
}

.gallery-1 .gallery-item { aspect-ratio: 16/9; border-radius: 0 0 20px 20px; }
.gallery-3 .gallery-item:first-child { aspect-ratio: auto; border-radius: 0 0 0 20px; }

.gallery-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.gallery-item:hover img {
  transform: scale(1.08);
}

.gallery-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.gallery-item:hover .gallery-overlay {
  opacity: 1;
}

.gallery-overlay svg {
  width: 32px;
  height: 32px;
  color: white;
}

.more-count {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.6);
  display: flex;
  align-items: center;
  justify-content: center;
}

.more-count span {
  font-size: 28px;
  font-weight: 700;
  color: white;
}

/* Story Actions */
.story-actions {
  display: flex;
  gap: 8px;
  padding: 12px 24px 20px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: transparent;
  border: none;
  border-radius: 12px;
  color: #A8A29E;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.action-btn:hover {
  background: rgba(255,255,255,0.08);
  color: #F5F2ED;
}

.action-icon {
  position: relative;
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-icon svg {
  width: 22px;
  height: 22px;
  transition: all 0.3s ease;
}

.action-btn:hover .action-icon svg {
  transform: scale(1.1);
}

.like-btn:hover {
  background: rgba(239,68,68,0.1);
  color: #ef4444;
}

.like-btn.active {
  color: #ef4444;
  background: rgba(239,68,68,0.1);
}

.like-btn.animating .action-icon svg {
  animation: heartPop 0.5s ease;
}

@keyframes heartPop {
  0% { transform: scale(1); }
  30% { transform: scale(1.3); }
  60% { transform: scale(0.9); }
  100% { transform: scale(1); }
}

.like-particles {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.particle {
  position: absolute;
  width: 5px;
  height: 5px;
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
  border-radius: 50%;
  top: 50%;
  left: 50%;
  animation: particleExplode 0.6s ease forwards;
}

.particle:nth-child(1) { animation-delay: 0s; --angle: 0deg; }
.particle:nth-child(2) { animation-delay: 0.03s; --angle: 45deg; }
.particle:nth-child(3) { animation-delay: 0.06s; --angle: 90deg; }
.particle:nth-child(4) { animation-delay: 0.09s; --angle: 135deg; }
.particle:nth-child(5) { animation-delay: 0.12s; --angle: 180deg; }
.particle:nth-child(6) { animation-delay: 0.15s; --angle: 225deg; }
.particle:nth-child(7) { animation-delay: 0.18s; --angle: 270deg; }
.particle:nth-child(8) { animation-delay: 0.21s; --angle: 315deg; }

@keyframes particleExplode {
  0% {
    opacity: 1;
    transform: translate(-50%, -50%) rotate(var(--angle)) translateX(0);
  }
  100% {
    opacity: 0;
    transform: translate(-50%, -50%) rotate(var(--angle)) translateX(30px) scale(0);
  }
}

.comment-btn:hover {
  background: rgba(59,130,246,0.1);
  color: #3b82f6;
}

.share-btn:hover {
  background: rgba(34,197,94,0.1);
  color: #22c55e;
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
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 30px;
  font-size: 14px;
  color: #D4CFC7;
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
  color: #F5F2ED;
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
  color: #2F3D24;
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
  background: #3D4F2F;
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 24px;
  width: 90%;
  max-width: 560px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 24px 80px rgba(0,0,0,0.4);
  animation: modalSlideUp 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

@keyframes modalSlideUp {
  from { opacity: 0; transform: translateY(40px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.modal-header h3 {
  font-size: 24px;
  font-weight: 600;
  color: #F5F2ED;
  font-family: var(--font-display);
}

.close-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,0.08);
  border: none;
  border-radius: 50%;
  color: #A8A29E;
  cursor: pointer;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: rgba(255,255,255,0.15);
  color: #F5F2ED;
}

.close-btn svg {
  width: 20px;
  height: 20px;
}

.modal-body {
  padding: 28px;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #D4CFC7;
  margin-bottom: 10px;
}

.form-group textarea {
  width: 100%;
  padding: 14px 18px;
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 12px;
  font-size: 15px;
  background: rgba(255,255,255,0.06);
  color: #F5F2ED;
  transition: all 0.3s ease;
  outline: none;
  resize: vertical;
  min-height: 140px;
  font-family: var(--font-body);
}

.form-group textarea::placeholder {
  color: #A8A29E;
}

.form-group textarea:focus {
  border-color: #F5F0E8;
  box-shadow: 0 0 0 3px rgba(245,240,232,0.1);
}

.upload-area {
  border: 2px dashed rgba(255,255,255,0.2);
  border-radius: 16px;
  padding: 40px;
  text-align: center;
  color: #A8A29E;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.upload-area:hover {
  border-color: rgba(245,240,232,0.4);
  background: rgba(255,255,255,0.04);
  color: #D4CFC7;
}

.upload-area svg {
  width: 40px;
  height: 40px;
}

.preview-images {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
}

.preview-item {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 12px;
  overflow: hidden;
}

.preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.6);
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.2s ease;
}

.remove-btn:hover {
  background: #ef4444;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 28px;
  border-top: 1px solid rgba(255,255,255,0.1);
}

.btn-ghost {
  padding: 12px 24px;
  background: transparent;
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 30px;
  font-size: 15px;
  color: #D4CFC7;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-ghost:hover {
  background: rgba(255,255,255,0.08);
  border-color: rgba(255,255,255,0.3);
}

.btn-accent {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  background: linear-gradient(135deg, #F5F0E8 0%, #FAF8F5 100%);
  border: none;
  border-radius: 30px;
  font-size: 15px;
  font-weight: 600;
  color: #2F3D24;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-accent svg {
  width: 18px;
  height: 18px;
}

.btn-accent:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(245,240,232,0.4);
}

/* Image Preview Modal */
.image-preview-modal {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.95);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.preview-close {
  position: absolute;
  top: 24px;
  right: 24px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,0.1);
  border: none;
  border-radius: 50%;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 2001;
}

.preview-close:hover {
  background: rgba(255,255,255,0.2);
}

.preview-close svg {
  width: 24px;
  height: 24px;
}

.preview-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,0.1);
  border: none;
  border-radius: 50%;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.preview-nav:hover {
  background: rgba(255,255,255,0.25);
}

.preview-nav svg {
  width: 24px;
  height: 24px;
}

.preview-nav.prev { left: 24px; }
.preview-nav.next { right: 24px; }

.preview-image-wrap {
  max-width: 85vw;
  max-height: 85vh;
}

.preview-image-wrap img {
  max-width: 100%;
  max-height: 85vh;
  object-fit: contain;
  border-radius: 8px;
}

.preview-counter {
  position: absolute;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  padding: 8px 20px;
  background: rgba(0,0,0,0.6);
  border-radius: 20px;
  color: white;
  font-size: 14px;
}

/* Responsive */
@media (max-width: 768px) {
  .hero-content {
    padding: 24px;
  }
  
  .story-card {
    border-radius: 20px;
  }
  
  .story-author {
    padding: 16px 20px;
  }
  
  .story-body {
    padding: 0 20px 12px;
  }
  
  .story-gallery {
    gap: 2px;
    padding: 0 2px;
  }
  
  .gallery-3 .gallery-item:first-child {
    border-radius: 0;
  }
  
  .story-actions {
    padding: 8px 16px 16px;
  }
  
  .action-btn {
    padding: 8px 12px;
    font-size: 13px;
  }
  
  .action-icon svg {
    width: 20px;
    height: 20px;
  }
  
  .fab-text {
    display: none;
  }
  
  .fab-publish {
    padding: 16px;
    border-radius: 50%;
  }
  
  .page-btn span {
    display: none;
  }
  
  .preview-nav {
    width: 44px;
    height: 44px;
  }
  
  .preview-nav svg {
    width: 20px;
    height: 20px;
  }
}
</style>
