<template>
  <div class="traveler-stories-page">
    <!-- 页面头部 -->
    <header class="page-header">
      <div class="header-content">
        <div class="back-btn" @click="goBack">
          <span class="arrow">&lt;</span>
          <span>返回首页</span>
        </div>
        <h1>旅行者故事</h1>
        <p class="subtitle">分享旅途中的精彩瞬间与难忘经历</p>
      </div>
      <div class="header-wave">
        <svg viewBox="0 0 1440 120" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M0 120L60 110C120 100 240 80 360 70C480 60 600 60 720 65C840 70 960 80 1080 85C1200 90 1320 90 1380 90L1440 90V120H1380C1320 120 1200 120 1080 120C960 120 840 120 720 120C600 120 480 120 360 120C240 120 120 120 60 120H0Z" fill="#f8f6f3"/>
        </svg>
      </div>
    </header>

    <!-- 故事列表区 -->
    <section class="stories-section">
      <div class="container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>正在加载旅行者故事...</p>
        </div>
        
        <!-- 错误状态 -->
        <div v-else-if="error" class="error-state">
          <span class="error-icon">⚠️</span>
          <p>{{ error }}</p>
          <button @click="fetchStories" class="retry-btn">重新加载</button>
        </div>
        
        <!-- 空状态 -->
        <div v-else-if="travelerStories.length === 0" class="empty-state">
          <span class="empty-icon">📖</span>
          <p>暂无旅行者故事</p>
        </div>
        
        <!-- 故事列表 -->
        <div v-else class="stories-list">
          <div 
            v-for="story in travelerStories" 
            :key="story.id"
            class="story-item"
            @mouseenter="hoverStory = story.id"
            @mouseleave="hoverStory = null"
            :class="{ hovered: hoverStory === story.id }"
          >
            <div class="story-avatar">
              <div class="avatar-wrapper">
                <img :src="story.avatar" :alt="story.author">
                <div class="avatar-ring" v-if="story.isVip"></div>
              </div>
              <div class="author-badge" v-if="story.isVip">
                <svg viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z"/>
                </svg>
              </div>
            </div>
            <div class="story-content">
              <div class="story-header">
                <div class="author-info">
                  <h4>{{ story.author }}</h4>
                  <span class="story-time">{{ story.time }}</span>
                </div>
              </div>
              <p class="story-text">{{ story.content }}</p>
              <div class="story-images" v-if="story.images && story.images.length > 0">
                <div 
                  v-for="(img, index) in story.images.slice(0, 3)" 
                  :key="index"
                  class="image-wrapper"
                  @click="openImagePreview(story.images, index)"
                >
                  <img :src="img" :alt="`图片${index + 1}`" loading="lazy">
                  <div class="image-overlay">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <circle cx="11" cy="11" r="8"/>
                      <path d="M21 21l-4.35-4.35"/>
                      <path d="M11 8v6M8 11h6"/>
                    </svg>
                  </div>
                </div>
                <div class="more-images" v-if="story.images.length > 3">
                  <span>+{{ story.images.length - 3 }}</span>
                </div>
              </div>
              <div class="story-actions">
                <button 
                  class="action-btn like-btn" 
                  :class="{ active: story.isLiked, animating: animatingLike === story.id }" 
                  @click.stop="toggleLike(story)"
                >
                  <div class="icon-wrapper">
                    <svg class="action-icon like-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path class="heart-path" d="M12 21.35L10.55 20.03C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5C22 12.27 18.6 15.36 13.45 20.03L12 21.35Z" :fill="story.isLiked ? 'url(#storyHeartGradient)' : 'none'" :stroke="story.isLiked ? '#e85a5a' : '#9ca3af'" stroke-width="1.8"/>
                      <defs>
                        <linearGradient id="storyHeartGradient" x1="12" y1="3" x2="12" y2="21.35" gradientUnits="userSpaceOnUse">
                          <stop stop-color="#ff6b6b"/>
                          <stop offset="0.5" stop-color="#ff8e8e"/>
                          <stop offset="1" stop-color="#ffb4b4"/>
                        </linearGradient>
                      </defs>
                    </svg>
                    <div class="like-particles" v-if="story.isLiked">
                      <span v-for="n in 6" :key="n" class="particle"></span>
                    </div>
                  </div>
                  <span class="count">{{ formatNumber(story.likes) }}</span>
                </button>
                <button class="action-btn comment-btn" @click.stop="showComments(story)">
                  <div class="icon-wrapper">
                    <svg class="action-icon comment-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path class="chat-bubble" d="M20 2H4C2.9 2 2 2.9 2 4V22L6 18H20C21.1 18 22 17.1 22 16V4C22 2.9 21.1 2 20 2Z" fill="none" stroke="#9ca3af" stroke-width="1.8" stroke-linejoin="round"/>
                      <circle cx="8" cy="10" r="1.2" fill="#9ca3af"/>
                      <circle cx="12" cy="10" r="1.2" fill="#9ca3af"/>
                      <circle cx="16" cy="10" r="1.2" fill="#9ca3af"/>
                    </svg>
                  </div>
                  <span class="count">{{ formatNumber(story.comments) }}</span>
                </button>
                <button class="action-btn share-btn" @click.stop="shareStory(story)">
                  <div class="icon-wrapper">
                    <svg class="action-icon share-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <circle cx="18" cy="5" r="2.5" stroke="#9ca3af" stroke-width="1.8" fill="none"/>
                      <circle cx="6" cy="12" r="2.5" stroke="#9ca3af" stroke-width="1.8" fill="none"/>
                      <circle cx="18" cy="19" r="2.5" stroke="#9ca3af" stroke-width="1.8" fill="none"/>
                      <path d="M8.5 10.5L15.5 6.5" stroke="#9ca3af" stroke-width="1.8" stroke-linecap="round"/>
                      <path d="M8.5 13.5L15.5 17.5" stroke="#9ca3af" stroke-width="1.8" stroke-linecap="round"/>
                    </svg>
                  </div>
                  <span class="label">分享</span>
                </button>
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

    <!-- 发布按钮 -->
    <button class="fab-publish" @click="showPublishModal = true">
      <span>+</span>
      <span class="fab-text">分享故事</span>
    </button>

    <!-- 发布模态框 -->
    <div class="modal-overlay" v-if="showPublishModal" @click.self="showPublishModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>分享旅行故事</h3>
          <button class="close-btn" @click="showPublishModal = false">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>分享你的故事</label>
            <textarea rows="6" placeholder="记录你的旅行经历、心情和感悟..." v-model="newStoryContent"></textarea>
          </div>
          <div class="form-group">
            <label>上传图片</label>
            <div class="upload-area" @click="triggerFileInput">
              <span>+ 点击或拖拽上传图片</span>
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
          <button class="btn-secondary" @click="showPublishModal = false">取消</button>
          <button class="btn-primary" @click="publishStory">发布</button>
        </div>
      </div>
    </div>

    <!-- 图片预览模态框 -->
    <div class="image-preview-modal" v-if="previewImageList.length > 0" @click.self="closeImagePreview">
      <button class="preview-close" @click="closeImagePreview">&times;</button>
      <button class="preview-nav prev" @click.stop="prevImage" v-if="previewImageList.length > 1">&lt;</button>
      <img :src="previewImageList[currentPreviewIndex]" alt="预览">
      <button class="preview-nav next" @click.stop="nextImage" v-if="previewImageList.length > 1">&gt;</button>
      <div class="preview-counter">{{ currentPreviewIndex + 1 }} / {{ previewImageList.length }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const emit = defineEmits(['back-to-home'])

// 分页
const currentPage = ref(1)
const totalPages = ref(1)

// 悬停状态
const hoverStory = ref(null)

// 点赞动画状态
const animatingLike = ref(null)

// 模态框
const showPublishModal = ref(false)
const newStoryContent = ref('')
const previewImages = ref([])
const fileInput = ref(null)

// 图片预览
const previewImageList = ref([])
const currentPreviewIndex = ref(0)

// 数据状态
const travelerStories = ref([])
const loading = ref(false)
const error = ref(null)

// 获取旅行者故事数据
const fetchStories = async () => {
  loading.value = true
  error.value = null
  
  try {
    const response = await fetch(`/api/stories/page?page=${currentPage.value}&pageSize=10`)
    const result = await response.json()
    
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
        // 使用默认数据
        travelerStories.value = getDefaultStories()
        totalPages.value = 1
      }
    } else {
      // 使用默认数据
      travelerStories.value = getDefaultStories()
      totalPages.value = 1
    }
  } catch (err) {
    console.error('请求故事接口失败:', err)
    // 使用默认数据
    travelerStories.value = getDefaultStories()
    totalPages.value = 1
  } finally {
    loading.value = false
  }
}

// 默认故事数据
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

// 格式化时间
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

// 返回首页
const goBack = () => {
  emit('back-to-home')
}

// 格式化数字
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

// 点赞
const toggleLike = (story) => {
  story.isLiked = !story.isLiked
  story.likes += story.isLiked ? 1 : -1
  
  // 触发动画
  if (story.isLiked) {
    animatingLike.value = story.id
    setTimeout(() => {
      animatingLike.value = null
    }, 500)
  }
}

// 显示评论
const showComments = (story) => {
  alert(`查看 ${story.author} 的故事评论 (${story.comments}条)`)
}

// 分享故事
const shareStory = (story) => {
  alert(`分享 ${story.author} 的故事`)
}

// 切换页面
const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    fetchStories()
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value?.click()
}

// 处理文件选择
const handleFileChange = (event) => {
  const files = event.target.files
  if (files) {
    Array.from(files).forEach(file => {
      const reader = new FileReader()
      reader.onload = (e) => {
        previewImages.value.push(e.target.result)
      }
      reader.readAsDataURL(file)
    })
  }
}

// 移除预览图片
const removeImage = (index) => {
  previewImages.value.splice(index, 1)
}

// 发布故事
const publishStory = () => {
  if (!newStoryContent.value.trim()) {
    alert('请输入故事内容')
    return
  }
  
  const newStory = {
    id: Date.now(),
    author: '我',
    avatar: '/img/头像1.jpg',
    isVip: false,
    time: '刚刚',
    content: newStoryContent.value,
    images: [...previewImages.value],
    likes: 0,
    comments: 0,
    isLiked: false
  }
  
  travelerStories.value.unshift(newStory)
  
  // 重置表单
  newStoryContent.value = ''
  previewImages.value = []
  showPublishModal.value = false
  
  alert('故事发布成功！')
}

// 打开图片预览
const openImagePreview = (images, index) => {
  previewImageList.value = images
  currentPreviewIndex.value = index
}

// 关闭图片预览
const closeImagePreview = () => {
  previewImageList.value = []
  currentPreviewIndex.value = 0
}

// 上一张图片
const prevImage = () => {
  if (currentPreviewIndex.value > 0) {
    currentPreviewIndex.value--
  } else {
    currentPreviewIndex.value = previewImageList.value.length - 1
  }
}

// 下一张图片
const nextImage = () => {
  if (currentPreviewIndex.value < previewImageList.value.length - 1) {
    currentPreviewIndex.value++
  } else {
    currentPreviewIndex.value = 0
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchStories()
})
</script>

<style scoped>
/* 页面整体样式 */
.traveler-stories-page {
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
  background: url('/img/富士山.jpg') center/cover no-repeat;
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

/* 故事区域 */
.stories-section {
  padding: 60px 0;
  background-color: #f8f6f3;
  min-height: 60vh;
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

/* 故事列表 */
.stories-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.story-item {
  display: flex;
  gap: 18px;
  background: white;
  padding: 24px;
  border-radius: 24px;
  box-shadow: 
    0 2px 8px rgba(0, 0, 0, 0.04),
    0 8px 24px rgba(0, 0, 0, 0.04);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(0, 0, 0, 0.03);
}

.story-item.hovered {
  transform: translateY(-4px);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.06),
    0 16px 48px rgba(0, 0, 0, 0.08);
}

.story-avatar {
  position: relative;
  flex-shrink: 0;
}

.avatar-wrapper {
  position: relative;
  width: 56px;
  height: 56px;
}

.story-avatar img {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.story-item:hover .story-avatar img {
  transform: scale(1.05);
}

.avatar-ring {
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f59e0b 0%, #f97316 50%, #ef4444 100%);
  z-index: -1;
  animation: ringPulse 2s ease-in-out infinite;
}

@keyframes ringPulse {
  0%, 100% { opacity: 0.8; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.02); }
}

.author-badge {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 22px;
  height: 22px;
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 6px rgba(245, 158, 11, 0.4);
  border: 2px solid white;
}

.author-badge svg {
  width: 12px;
  height: 12px;
  fill: white;
}

.story-content {
  flex: 1;
  min-width: 0;
}

.story-header {
  margin-bottom: 12px;
}

.author-info {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.story-header h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  letter-spacing: -0.01em;
}

.story-time {
  font-size: 13px;
  color: #9ca3af;
  font-weight: 400;
}

.story-text {
  font-size: 15px;
  color: #4b5563;
  line-height: 1.75;
  margin-bottom: 16px;
  letter-spacing: 0.01em;
}

/* 图片展示 */
.story-images {
  display: flex;
  gap: 10px;
  margin-bottom: 18px;
  flex-wrap: wrap;
}

.image-wrapper {
  position: relative;
  width: 140px;
  height: 140px;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.image-wrapper:hover {
  transform: scale(1.03);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.image-wrapper:hover img {
  transform: scale(1.1);
}

.image-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.image-wrapper:hover .image-overlay {
  opacity: 1;
}

.image-overlay svg {
  width: 32px;
  height: 32px;
  color: white;
  stroke-width: 1.5;
}

.more-images {
  width: 140px;
  height: 140px;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.more-images:hover {
  transform: scale(1.03);
  box-shadow: 0 8px 24px rgba(251, 191, 36, 0.3);
}

.more-images span {
  font-size: 20px;
  font-weight: 600;
  color: #d97706;
}

/* 故事操作按钮 */
.story-actions {
  display: flex;
  gap: 8px;
  padding-top: 4px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: transparent;
  border: none;
  color: #6b7280;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 8px 14px;
  border-radius: 20px;
  position: relative;
}

.action-btn:hover {
  background: #f3f4f6;
  color: #374151;
}

.action-btn.like-btn:hover {
  background: #fef2f2;
  color: #ef4444;
}

.action-btn.like-btn.active {
  color: #ef4444;
  background: #fef2f2;
}

.action-btn.comment-btn:hover {
  background: #eff6ff;
  color: #3b82f6;
}

.action-btn.comment-btn:hover .chat-bubble {
  stroke: #3b82f6;
}

.action-btn.share-btn:hover {
  background: #f0fdf4;
  color: #22c55e;
}

.icon-wrapper {
  position: relative;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-icon {
  width: 20px;
  height: 20px;
  transition: all 0.3s ease;
}

.action-btn:hover .action-icon {
  transform: scale(1.1);
}

.action-btn.like-btn.active .like-icon {
  animation: heartBeat 0.4s ease;
}

.action-btn.like-btn.animating .like-icon {
  animation: heartBurst 0.5s ease;
}

@keyframes heartBeat {
  0%, 100% { transform: scale(1); }
  25% { transform: scale(1.2); }
  50% { transform: scale(0.95); }
  75% { transform: scale(1.1); }
}

@keyframes heartBurst {
  0% { transform: scale(1); }
  30% { transform: scale(1.4); }
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
  width: 6px;
  height: 6px;
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
  border-radius: 50%;
  top: 50%;
  left: 50%;
  animation: particleBurst 0.6s ease forwards;
}

.particle:nth-child(1) { animation-delay: 0s; transform: translate(-50%, -50%) rotate(0deg); }
.particle:nth-child(2) { animation-delay: 0.05s; transform: translate(-50%, -50%) rotate(60deg); }
.particle:nth-child(3) { animation-delay: 0.1s; transform: translate(-50%, -50%) rotate(120deg); }
.particle:nth-child(4) { animation-delay: 0.15s; transform: translate(-50%, -50%) rotate(180deg); }
.particle:nth-child(5) { animation-delay: 0.2s; transform: translate(-50%, -50%) rotate(240deg); }
.particle:nth-child(6) { animation-delay: 0.25s; transform: translate(-50%, -50%) rotate(300deg); }

@keyframes particleBurst {
  0% {
    opacity: 1;
    transform: translate(-50%, -50%) rotate(var(--rotation, 0deg)) translateX(0);
  }
  100% {
    opacity: 0;
    transform: translate(-50%, -50%) rotate(var(--rotation, 0deg)) translateX(25px) scale(0);
  }
}

.action-btn .count,
.action-btn .label {
  font-weight: 500;
  font-size: 13px;
}

.action-btn.like-btn.active .count {
  color: #ef4444;
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

.form-group textarea {
  width: 100%;
  padding: 12px 18px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  font-size: 15px;
  transition: all 0.3s ease;
  outline: none;
  resize: vertical;
  min-height: 120px;
}

.form-group textarea:focus {
  border-color: #f79545;
  box-shadow: 0 0 0 4px rgba(247, 149, 69, 0.1);
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

.preview-images {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 15px;
}

.preview-item {
  position: relative;
  width: 80px;
  height: 80px;
}

.preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.remove-btn {
  position: absolute;
  top: -5px;
  right: -5px;
  width: 20px;
  height: 20px;
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
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

/* 图片预览模态框 */
.image-preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.image-preview-modal img {
  max-width: 90%;
  max-height: 80vh;
  object-fit: contain;
  border-radius: 8px;
}

.preview-close {
  position: absolute;
  top: 20px;
  right: 20px;
  background: none;
  border: none;
  color: white;
  font-size: 36px;
  cursor: pointer;
  z-index: 2001;
}

.preview-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  font-size: 24px;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
}

.preview-nav:hover {
  background: rgba(255, 255, 255, 0.4);
}

.preview-nav.prev {
  left: 20px;
}

.preview-nav.next {
  right: 20px;
}

.preview-counter {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  color: white;
  font-size: 14px;
  background: rgba(0, 0, 0, 0.5);
  padding: 8px 16px;
  border-radius: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header h1 {
    font-size: 32px;
  }

  .story-item {
    flex-direction: column;
    gap: 16px;
    padding: 20px;
    border-radius: 20px;
  }

  .avatar-wrapper {
    width: 48px;
    height: 48px;
  }

  .story-avatar img {
    width: 48px;
    height: 48px;
  }

  .author-badge {
    width: 18px;
    height: 18px;
  }

  .author-badge svg {
    width: 10px;
    height: 10px;
  }

  .image-wrapper,
  .more-images {
    width: 100px;
    height: 100px;
  }

  .story-actions {
    gap: 4px;
  }

  .action-btn {
    padding: 6px 10px;
  }

  .action-btn .count,
  .action-btn .label {
    font-size: 12px;
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

  .preview-nav {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }
}
</style>
