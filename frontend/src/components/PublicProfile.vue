<template>
  <div class="public-profile-page">
    <header class="profile-header">
      <div class="container">
        <button class="back-btn" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
          <span>返回</span>
        </button>
        <h1 class="page-title">{{ user.nickname || '用户主页' }}</h1>
        <div class="header-spacer"></div>
      </div>
    </header>

    <div class="profile-container">
      <aside class="user-sidebar">
        <div class="user-card">
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <img
                v-if="user.avatarUrl"
                :src="user.avatarUrl"
                alt="用户头像"
                class="user-avatar-img"
              >
              <div v-else class="avatar-placeholder">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
                </svg>
              </div>
            </div>
            <h2 class="user-name">{{ user.nickname || '用户' }}</h2>
            <p class="user-bio" v-if="user.bio">{{ user.bio }}</p>
          </div>

          <div class="user-stats">
            <div class="stat-item">
              <span class="stat-value">{{ user.guidesCount || 0 }}</span>
              <span class="stat-label">攻略</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ user.followersCount || 0 }}</span>
              <span class="stat-label">粉丝</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ user.likesReceivedCount || 0 }}</span>
              <span class="stat-label">获赞</span>
            </div>
          </div>

          <button
            v-if="currentUserId && currentUserId !== userId"
            class="follow-btn"
            :class="{ following: isFollowing }"
            @click="toggleFollow"
          >
            {{ isFollowing ? '已关注' : '+ 关注' }}
          </button>
        </div>
      </aside>

      <main class="profile-content">
        <div class="content-tabs">
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'guides' }"
            @click="activeTab = 'guides'"
          >攻略</button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'stories' }"
            @click="activeTab = 'stories'"
          >故事</button>
        </div>

        <!-- 攻略列表 -->
        <div v-if="activeTab === 'guides'" class="content-list">
          <div v-if="guidesLoading" class="loading-state">
            <div class="loading-spinner"></div>
            <p>加载中...</p>
          </div>
          <div v-else-if="guides.length === 0" class="empty-state">
            <p>暂无攻略</p>
          </div>
          <div v-else class="guides-grid">
            <div
              v-for="guide in guides"
              :key="guide.id"
              class="guide-card"
              @click="$emit('view-guide', guide.id)"
            >
              <div class="card-image">
                <img :src="guide.coverImageUrl || '/img/富士山.jpg'" :alt="guide.title" loading="lazy">
              </div>
              <div class="card-content">
                <h3>{{ guide.title }}</h3>
                <p class="card-desc">{{ guide.summary }}</p>
                <div class="card-meta">
                  <span>{{ guide.likesCount || 0 }} 赞</span>
                  <span>{{ guide.viewsCount || 0 }} 浏览</span>
                </div>
              </div>
            </div>
          </div>
          <div v-if="guidesTotal > 10" class="load-more">
            <button @click="loadMoreGuides" :disabled="guidesLoading">加载更多</button>
          </div>
        </div>

        <!-- 故事列表 -->
        <div v-if="activeTab === 'stories'" class="content-list">
          <div v-if="storiesLoading" class="loading-state">
            <div class="loading-spinner"></div>
            <p>加载中...</p>
          </div>
          <div v-else-if="stories.length === 0" class="empty-state">
            <p>暂无故事</p>
          </div>
          <div v-else class="stories-list">
            <div v-for="story in stories" :key="story.id" class="story-card">
              <div class="story-content">{{ story.content }}</div>
              <div class="story-meta">
                <span>{{ story.likesCount || 0 }} 赞</span>
                <span>{{ story.commentsCount || 0 }} 评论</span>
                <span>{{ formatDate(story.publishedAt) }}</span>
              </div>
            </div>
          </div>
          <div v-if="storiesTotal > 10" class="load-more">
            <button @click="loadMoreStories" :disabled="storiesLoading">加载更多</button>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import request, { triggerAuthError } from '../utils/request'
import { getCookie } from '../utils/cookie'

const props = defineProps({
  userId: { type: Number, required: true }
})

const emit = defineEmits(['back', 'view-guide'])

const user = ref({})
const currentUserId = ref(null)
const isFollowing = ref(false)
const activeTab = ref('guides')

const guides = ref([])
const guidesLoading = ref(false)
const guidesPage = ref(1)
const guidesTotal = ref(0)

const stories = ref([])
const storiesLoading = ref(false)
const storiesPage = ref(1)
const storiesTotal = ref(0)

const goBack = () => emit('back')

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const fetchCurrentUser = async () => {
  const token = getCookie('token')
  if (!token) return
  try {
    const res = await request.get('/api/users/me')
    if (res.code === 'OK' && res.data) {
      currentUserId.value = res.data.id
    }
  } catch (err) {
    // ignore
  }
}

const fetchUserProfile = async () => {
  try {
    const res = await request.get(`/api/users/${props.userId}`)
    if (res.code === 'OK' && res.data) {
      user.value = res.data
    }
  } catch (err) {
    console.error('加载用户信息失败:', err)
  }
}

const fetchFollowStatus = async () => {
  const token = getCookie('token')
  if (!token) return
  try {
    const res = await request.get(`/api/users/${props.userId}/follow`)
    if (res.code === 'OK') {
      isFollowing.value = res.data.following
    }
  } catch (err) {
    // ignore
  }
}

const toggleFollow = async () => {
  const token = getCookie('token')
  if (!token) {
    triggerAuthError()
    return
  }
  try {
    const result = isFollowing.value
      ? await request.delete(`/api/users/${props.userId}/follow`)
      : await request.post(`/api/users/${props.userId}/follow`)
    if (result.code === 'OK') {
      isFollowing.value = result.data.following
      user.value.followersCount = result.data.followersCount
    }
  } catch (err) {
    console.error('关注操作失败:', err)
  }
}

const fetchGuides = async (page = 1) => {
  guidesLoading.value = true
  try {
    const res = await request.get(`/api/users/${props.userId}/guides?page=${page}&pageSize=10`)
    if (res.code === 'OK' && res.data) {
      if (page === 1) {
        guides.value = res.data.list || []
      } else {
        guides.value.push(...(res.data.list || []))
      }
      guidesTotal.value = res.data.total || 0
      guidesPage.value = page
    }
  } catch (err) {
    console.error('加载攻略失败:', err)
  } finally {
    guidesLoading.value = false
  }
}

const loadMoreGuides = () => fetchGuides(guidesPage.value + 1)

const fetchStories = async (page = 1) => {
  storiesLoading.value = true
  try {
    const res = await request.get(`/api/users/${props.userId}/stories?page=${page}&pageSize=10`)
    if (res.code === 'OK' && res.data) {
      if (page === 1) {
        stories.value = res.data.list || []
      } else {
        stories.value.push(...(res.data.list || []))
      }
      storiesTotal.value = res.data.total || 0
      storiesPage.value = page
    }
  } catch (err) {
    console.error('加载故事失败:', err)
  } finally {
    storiesLoading.value = false
  }
}

const loadMoreStories = () => fetchStories(storiesPage.value + 1)

onMounted(() => {
  fetchCurrentUser()
  fetchUserProfile()
  fetchFollowStatus()
  fetchGuides()
  fetchStories()
})

watch(() => props.userId, () => {
  fetchUserProfile()
  fetchFollowStatus()
  guidesPage.value = 1
  storiesPage.value = 1
  fetchGuides()
  fetchStories()
})
</script>

<style scoped>
.public-profile-page {
  min-height: 100vh;
  background-color: var(--color-bg-primary);
  font-family: var(--font-body);
}

.profile-header {
  background: var(--color-card-bg);
  border-bottom: 1px solid var(--color-card-border);
  padding: 16px 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.profile-header .container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-card-border);
  border-radius: 20px;
  color: var(--color-text-secondary);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: var(--color-bg-tertiary);
  color: var(--color-text-primary);
}

.back-btn svg {
  width: 16px;
  height: 16px;
}

.page-title {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0;
}

.header-spacer {
  width: 80px;
}

.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px;
  display: flex;
  gap: 32px;
}

.user-sidebar {
  width: 300px;
  flex-shrink: 0;
}

.user-card {
  background: var(--color-card-bg);
  border: 1px solid var(--color-card-border);
  border-radius: 20px;
  padding: 32px 24px;
  text-align: center;
  position: sticky;
  top: 100px;
}

.avatar-section {
  margin-bottom: 20px;
}

.avatar-wrapper {
  width: 88px;
  height: 88px;
  margin: 0 auto 16px;
  position: relative;
}

.user-avatar-img {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--color-card-border);
}

.avatar-placeholder {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  background: var(--color-bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 3px solid var(--color-card-border);
}

.avatar-placeholder svg {
  width: 40px;
  height: 40px;
  color: var(--color-text-muted);
}

.user-name {
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 6px;
}

.user-bio {
  font-size: 14px;
  color: var(--color-text-muted);
  margin: 0;
  line-height: 1.5;
}

.user-stats {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 20px 0;
  border-top: 1px solid var(--color-card-border);
  border-bottom: 1px solid var(--color-card-border);
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 20px;
  font-weight: 700;
  color: var(--color-text-primary);
  font-family: var(--font-display);
}

.stat-label {
  display: block;
  font-size: 12px;
  color: var(--color-text-muted);
  margin-top: 2px;
}

.follow-btn {
  width: 100%;
  padding: 12px;
  background: var(--gradient-primary);
  color: #FFFFFF;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.follow-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(45,58,30,0.25);
}

.follow-btn.following {
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
  border: 1px solid var(--color-card-border);
}

.follow-btn.following:hover {
  box-shadow: none;
  transform: none;
}

.profile-content {
  flex: 1;
  min-width: 0;
}

.content-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
}

.tab-btn {
  padding: 10px 24px;
  background: var(--color-card-bg);
  border: 1px solid var(--color-card-border);
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab-btn.active {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: #FFFFFF;
}

.tab-btn:hover:not(.active) {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.loading-state, .empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--color-text-muted);
}

.loading-spinner {
  width: 36px;
  height: 36px;
  border: 3px solid var(--color-card-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin { to { transform: rotate(360deg); } }

.guides-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.guide-card {
  background: var(--color-card-bg);
  border: 1px solid var(--color-card-border);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.guide-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(45, 58, 30, 0.12);
  border-color: var(--color-primary);
}

.card-image {
  height: 160px;
  overflow: hidden;
  background: var(--color-bg-secondary);
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.guide-card:hover .card-image img {
  transform: scale(1.05);
}

.card-content {
  padding: 16px;
}

.card-content h3 {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 6px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-desc {
  font-size: 13px;
  color: var(--color-text-muted);
  margin: 0 0 10px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--color-text-muted);
}

.stories-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.story-card {
  background: var(--color-card-bg);
  border: 1px solid var(--color-card-border);
  border-radius: 16px;
  padding: 20px;
}

.story-content {
  font-size: 14px;
  color: var(--color-text-primary);
  line-height: 1.7;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.story-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--color-text-muted);
}

.load-more {
  text-align: center;
  padding: 24px 0;
}

.load-more button {
  padding: 10px 28px;
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-card-border);
  border-radius: 10px;
  font-size: 14px;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.load-more button:hover:not(:disabled) {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.load-more button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .profile-container {
    flex-direction: column;
    padding: 16px;
  }

  .user-sidebar {
    width: 100%;
  }

  .user-card {
    position: static;
  }

  .guides-grid {
    grid-template-columns: 1fr;
  }
}
</style>
