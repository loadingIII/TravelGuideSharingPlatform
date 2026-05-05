<template>
  <div class="user-profile-page">
    <!-- 顶部导航 -->
    <header class="profile-header">
      <div class="container">
        <button class="back-btn" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
          <span>返回</span>
        </button>
        <h1 class="page-title">个人中心</h1>
        <div class="header-spacer"></div>
      </div>
    </header>

    <div class="profile-container">
      <!-- 左侧：用户信息卡片 -->
      <aside class="user-sidebar">
        <div class="user-card">
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <img v-if="userInfo.avatar" :src="userInfo.avatar" alt="用户头像" class="user-avatar-img">
              <div v-else class="avatar-placeholder">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
                </svg>
              </div>
              <button class="edit-avatar-btn" @click="triggerAvatarUpload" title="更换头像">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/>
                  <circle cx="12" cy="13" r="4"/>
                </svg>
              </button>
              <input ref="avatarInput" type="file" accept="image/*" style="display: none" @change="handleAvatarChange">
            </div>
            <h2 class="user-name">{{ userInfo.username }}</h2>
            <p class="user-role">{{ userInfo.nickname || '旅行爱好者' }}</p>
          </div>

          <div class="user-stats">
            <div class="stat-item">
              <span class="stat-value">{{ stats.guides }}</span>
              <span class="stat-label">发布攻略</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ stats.stories }}</span>
              <span class="stat-label">旅行故事</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ stats.likes }}</span>
              <span class="stat-label">获赞</span>
            </div>
          </div>

          <div class="user-menu">
            <button 
              v-for="item in menuItems" 
              :key="item.key"
              class="menu-item"
              :class="{ active: currentTab === item.key }"
              @click="currentTab = item.key"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" v-html="item.icon"></svg>
              <span>{{ item.label }}</span>
            </button>
          </div>
        </div>
      </aside>

      <!-- 右侧：内容区域 -->
      <main class="profile-content">
        <!-- 个人信息 -->
        <section v-if="currentTab === 'profile'" class="content-section">
          <div class="section-header">
            <h3>个人信息</h3>
            <button class="edit-btn" @click="isEditing = !isEditing">
              {{ isEditing ? '取消' : '编辑资料' }}
            </button>
          </div>
          
          <div class="profile-form">
            <div class="form-group">
              <label>用户名</label>
              <input v-model="formData.username" type="text" :disabled="!isEditing" placeholder="请输入用户名">
            </div>
            <div class="form-group">
              <label>昵称</label>
              <input v-model="formData.nickname" type="text" :disabled="!isEditing" placeholder="请输入昵称">
            </div>
            <div class="form-group">
              <label>手机号</label>
              <input v-model="formData.phone" type="tel" :disabled="!isEditing" placeholder="请输入手机号">
            </div>
            <div class="form-group">
              <label>邮箱</label>
              <input v-model="formData.email" type="email" :disabled="!isEditing" placeholder="请输入邮箱">
            </div>
            <div class="form-group">
              <label>个人简介</label>
              <textarea v-model="formData.bio" :disabled="!isEditing" rows="4" placeholder="介绍一下你自己..."></textarea>
            </div>
            <div v-if="isEditing" class="form-actions">
              <button class="btn btn-secondary" @click="isEditing = false">取消</button>
              <button class="btn btn-primary" @click="saveProfile">保存修改</button>
            </div>
          </div>
        </section>

        <!-- 我的发布 -->
        <section v-if="currentTab === 'posts'" class="content-section">
          <div class="section-header">
            <h3>我的发布</h3>
            <div class="filter-tabs">
              <button 
                v-for="tab in postTabs" 
                :key="tab.key"
                class="filter-tab"
                :class="{ active: currentPostTab === tab.key }"
                @click="currentPostTab = tab.key"
              >
                {{ tab.label }}
              </button>
            </div>
          </div>

          <!-- 攻略列表 -->
          <div v-if="currentPostTab === 'guides'" class="content-list">
            <div v-for="guide in myGuides" :key="guide.id" class="content-card">
              <div class="card-image">
                <img :src="guide.cover_image_url || '/img/富士山.jpg'" alt="攻略封面">
              </div>
              <div class="card-body">
                <h4 class="card-title">{{ guide.title }}</h4>
                <p class="card-desc">{{ guide.summary }}</p>
                <div class="card-meta">
                  <span class="meta-item">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                      <circle cx="12" cy="12" r="3"/>
                    </svg>
                    {{ guide.views_count || 0 }}
                  </span>
                  <span class="meta-item">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                    </svg>
                    {{ guide.likes_count || 0 }}
                  </span>
                  <span class="meta-item">{{ formatDate(guide.published_at) }}</span>
                </div>
              </div>
              <div class="card-actions">
                <button class="action-btn edit" @click="editGuide(guide.id)" title="编辑">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                  </svg>
                </button>
                <button class="action-btn delete" @click="deleteGuide(guide.id)" title="删除">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="3 6 5 6 21 6"/>
                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                  </svg>
                </button>
              </div>
            </div>
            <div v-if="myGuides.length === 0" class="empty-state">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                <polyline points="14 2 14 8 20 8"/>
                <line x1="16" y1="13" x2="8" y2="13"/>
                <line x1="16" y1="17" x2="8" y2="17"/>
                <polyline points="10 9 9 9 8 9"/>
              </svg>
              <p>还没有发布攻略</p>
              <button class="btn btn-primary" @click="createGuide">写攻略</button>
            </div>
          </div>

          <!-- 故事列表 -->
          <div v-if="currentPostTab === 'stories'" class="content-list">
            <div v-for="story in myStories" :key="story.id" class="content-card story-card">
              <div class="card-body">
                <p class="story-content">{{ story.content }}</p>
                <div v-if="story.images && story.images.length" class="story-images">
                  <img v-for="(img, idx) in story.images.slice(0, 3)" :key="idx" :src="img" alt="故事图片">
                  <div v-if="story.images.length > 3" class="more-images">+{{ story.images.length - 3 }}</div>
                </div>
                <div class="card-meta">
                  <span class="meta-item">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                    </svg>
                    {{ story.likes_count || 0 }}
                  </span>
                  <span class="meta-item">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>
                    </svg>
                    {{ story.comments_count || 0 }}
                  </span>
                  <span class="meta-item">{{ formatDate(story.created_at) }}</span>
                </div>
              </div>
              <div class="card-actions">
                <button class="action-btn edit" @click="editStory(story.id)" title="编辑">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                  </svg>
                </button>
                <button class="action-btn delete" @click="deleteStory(story.id)" title="删除">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="3 6 5 6 21 6"/>
                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                  </svg>
                </button>
              </div>
            </div>
            <div v-if="myStories.length === 0" class="empty-state">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 20h9"/>
                <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/>
              </svg>
              <p>还没有发布故事</p>
              <button class="btn btn-primary" @click="createStory">写故事</button>
            </div>
          </div>
        </section>

        <!-- 我的评论 -->
        <section v-if="currentTab === 'comments'" class="content-section">
          <div class="section-header">
            <h3>我的评论</h3>
          </div>
          <div class="content-list">
            <div v-for="comment in myComments" :key="comment.id" class="content-card comment-card">
              <div class="comment-header">
                <span class="comment-target">评论了攻略《{{ comment.guide_title }}》</span>
                <span class="comment-time">{{ formatDate(comment.created_at) }}</span>
              </div>
              <p class="comment-content">{{ comment.content }}</p>
              <div class="comment-footer">
                <div class="comment-stats">
                  <span class="stat-item">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                    </svg>
                    {{ comment.likes_count || 0 }}
                  </span>
                </div>
                <div class="card-actions">
                  <button class="action-btn delete" @click="deleteComment(comment.id)" title="删除">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="3 6 5 6 21 6"/>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                    </svg>
                  </button>
                </div>
              </div>
            </div>
            <div v-if="myComments.length === 0" class="empty-state">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>
              </svg>
              <p>还没有发表评论</p>
            </div>
          </div>
        </section>

        <!-- 点赞收藏 -->
        <section v-if="currentTab === 'favorites'" class="content-section">
          <div class="section-header">
            <h3>点赞收藏</h3>
            <div class="filter-tabs">
              <button 
                v-for="tab in favoriteTabs" 
                :key="tab.key"
                class="filter-tab"
                :class="{ active: currentFavoriteTab === tab.key }"
                @click="currentFavoriteTab = tab.key"
              >
                {{ tab.label }}
              </button>
            </div>
          </div>

          <!-- 点赞的攻略 -->
          <div v-if="currentFavoriteTab === 'likedGuides'" class="content-list">
            <div v-for="guide in likedGuides" :key="guide.id" class="content-card">
              <div class="card-image">
                <img :src="guide.cover_image_url || '/img/富士山.jpg'" alt="攻略封面">
              </div>
              <div class="card-body">
                <h4 class="card-title">{{ guide.title }}</h4>
                <p class="card-desc">{{ guide.summary }}</p>
                <div class="card-author">
                  <img :src="guide.author_avatar || '/img/头像1.jpg'" alt="作者">
                  <span>{{ guide.author_name }}</span>
                </div>
              </div>
              <div class="card-actions">
                <button class="action-btn unlike" @click="unlikeGuide(guide.id)" title="取消点赞">
                  <svg viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="2">
                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                  </svg>
                </button>
              </div>
            </div>
            <div v-if="likedGuides.length === 0" class="empty-state">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
              </svg>
              <p>还没有点赞任何攻略</p>
            </div>
          </div>

          <!-- 收藏的故事 -->
          <div v-if="currentFavoriteTab === 'savedStories'" class="content-list">
            <div v-for="story in savedStories" :key="story.id" class="content-card story-card">
              <div class="card-body">
                <div class="story-author">
                  <img :src="story.author_avatar || '/img/头像1.jpg'" alt="作者">
                  <span>{{ story.author_name }}</span>
                </div>
                <p class="story-content">{{ story.content }}</p>
                <div v-if="story.images && story.images.length" class="story-images">
                  <img v-for="(img, idx) in story.images.slice(0, 3)" :key="idx" :src="img" alt="故事图片">
                </div>
              </div>
              <div class="card-actions">
                <button class="action-btn unlike" @click="unsaveStory(story.id)" title="取消收藏">
                  <svg viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="2">
                    <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
                  </svg>
                </button>
              </div>
            </div>
            <div v-if="savedStories.length === 0" class="empty-state">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
              </svg>
              <p>还没有收藏任何故事</p>
            </div>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'

const props = defineProps({
  userInfo: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['back', 'update-user'])

// 当前选中的标签页
const currentTab = ref('profile')
const currentPostTab = ref('guides')
const currentFavoriteTab = ref('likedGuides')
const isEditing = ref(false)
const avatarInput = ref(null)

// 菜单项
const menuItems = [
  {
    key: 'profile',
    label: '个人信息',
    icon: '<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>'
  },
  {
    key: 'posts',
    label: '我的发布',
    icon: '<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/>'
  },
  {
    key: 'comments',
    label: '我的评论',
    icon: '<path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>'
  },
  {
    key: 'favorites',
    label: '点赞收藏',
    icon: '<path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>'
  }
]

// 发布标签
const postTabs = [
  { key: 'guides', label: '攻略' },
  { key: 'stories', label: '故事' }
]

// 收藏标签
const favoriteTabs = [
  { key: 'likedGuides', label: '点赞的攻略' },
  { key: 'savedStories', label: '收藏的故事' }
]

// 统计数据
const stats = reactive({
  guides: 0,
  stories: 0,
  likes: 0
})

// 表单数据
const formData = reactive({
  username: props.userInfo.username || '',
  nickname: props.userInfo.nickname || '',
  phone: props.userInfo.phone || '',
  email: props.userInfo.email || '',
  bio: props.userInfo.bio || ''
})

// 数据列表
const myGuides = ref([])
const myStories = ref([])
const myComments = ref([])
const likedGuides = ref([])
const savedStories = ref([])

// 返回上一页
const goBack = () => {
  emit('back')
}

// 触发头像上传
const triggerAvatarUpload = () => {
  avatarInput.value?.click()
}

// 处理头像更换
const handleAvatarChange = (e) => {
  const file = e.target.files[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = (event) => {
      props.userInfo.avatar = event.target.result
      // TODO: 上传到服务器
    }
    reader.readAsDataURL(file)
  }
}

// 保存个人资料
const saveProfile = () => {
  // TODO: 调用API保存
  emit('update-user', { ...props.userInfo, ...formData })
  isEditing.value = false
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

// 编辑攻略
const editGuide = (id) => {
  console.log('编辑攻略:', id)
}

// 删除攻略
const deleteGuide = (id) => {
  if (confirm('确定要删除这篇攻略吗？')) {
    myGuides.value = myGuides.value.filter(g => g.id !== id)
  }
}

// 创建攻略
const createGuide = () => {
  console.log('创建攻略')
}

// 编辑故事
const editStory = (id) => {
  console.log('编辑故事:', id)
}

// 删除故事
const deleteStory = (id) => {
  if (confirm('确定要删除这个故事吗？')) {
    myStories.value = myStories.value.filter(s => s.id !== id)
  }
}

// 创建故事
const createStory = () => {
  console.log('创建故事')
}

// 删除评论
const deleteComment = (id) => {
  if (confirm('确定要删除这条评论吗？')) {
    myComments.value = myComments.value.filter(c => c.id !== id)
  }
}

// 取消点赞
const unlikeGuide = (id) => {
  likedGuides.value = likedGuides.value.filter(g => g.id !== id)
}

// 取消收藏
const unsaveStory = (id) => {
  savedStories.value = savedStories.value.filter(s => s.id !== id)
}

// 加载模拟数据
onMounted(() => {
  // 模拟我的攻略数据
  myGuides.value = [
    {
      id: 1,
      title: '日本京都赏樱完全指南',
      summary: '3月下旬至4月上旬是最佳赏樱期，推荐清水寺、哲学之道，记得提前预订和服体验和民宿...',
      cover_image_url: '/img/富士山.jpg',
      views_count: 12580,
      likes_count: 2341,
      published_at: '2024-03-15'
    },
    {
      id: 2,
      title: '泰国清迈穷游7天攻略',
      summary: '古城内寺庙免费参观，周末夜市必逛，双条车是性价比最高的交通方式，人均3000玩一周...',
      cover_image_url: '/img/长尾船.png',
      views_count: 9870,
      likes_count: 1856,
      published_at: '2024-02-20'
    }
  ]

  // 模拟我的故事数据
  myStories.value = [
    {
      id: 1,
      content: '刚从冰岛回来，极光真的太震撼了！分享一下我的追光攻略：最佳时间是9月到次年3月，建议租一辆车自驾，这样可以灵活选择观赏地点。',
      images: ['/img/纽约城市.jpg', '/img/悉尼歌剧院.jpg', '/img/富士山.jpg'],
      likes_count: 456,
      comments_count: 89,
      created_at: '2024-03-10'
    }
  ]

  // 模拟评论数据
  myComments.value = [
    {
      id: 1,
      guide_title: '日本京都赏樱完全指南',
      content: '写得太详细了！正好计划明年去京都，收藏了！',
      likes_count: 23,
      created_at: '2024-03-16'
    },
    {
      id: 2,
      guide_title: '泰国清迈穷游7天攻略',
      content: '周末夜市真的超级棒，美食太多吃不过来了~',
      likes_count: 15,
      created_at: '2024-02-25'
    }
  ]

  // 模拟点赞的攻略
  likedGuides.value = [
    {
      id: 3,
      title: '云南大理环洱海骑行记',
      summary: '全程120公里，建议分两天完成，沿途喜洲古镇、双廊海景不容错过...',
      cover_image_url: '/img/希腊圣托尼尼.png',
      author_name: '骑行达人',
      author_avatar: '/img/头像3.png'
    },
    {
      id: 4,
      title: '希腊圣托里尼蜜月之旅',
      summary: '蓝白相间的地中海风情，伊亚小镇的日落是世界最美日落之一...',
      cover_image_url: '/img/希腊圣托尼尼.png',
      author_name: '蜜月旅行者',
      author_avatar: '/img/头像1.jpg'
    }
  ]

  // 模拟收藏的故事
  savedStories.value = [
    {
      id: 2,
      author_name: '摄影师阿明',
      author_avatar: '/img/头像2.png',
      content: '在巴塞罗那拍到了圣家堂最美的光线！高迪的建筑真的让人叹为观止...',
      images: ['/img/巴塞罗亚.png']
    }
  ]

  // 更新统计数据
  stats.guides = myGuides.value.length
  stats.stories = myStories.value.length
  stats.likes = myGuides.value.reduce((sum, g) => sum + (g.likes_count || 0), 0)
})
</script>

<style scoped>
.user-profile-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8f6f3 0%, #fff9f5 100%);
}

/* 顶部导航 */
.profile-header {
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.profile-header .container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 15px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.back-btn:hover {
  color: #f79545;
}

.back-btn svg {
  width: 20px;
  height: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  font-family: 'Noto Serif SC', serif;
}

.header-spacer {
  width: 60px;
}

/* 主容器 */
.profile-container {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 30px;
}

/* 侧边栏 */
.user-sidebar {
  position: sticky;
  top: 90px;
  height: fit-content;
}

.user-card {
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.avatar-section {
  text-align: center;
  margin-bottom: 25px;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 15px;
}

.user-avatar-img,
.avatar-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  background: linear-gradient(135deg, #ffc494 0%, #f79545 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-placeholder svg {
  width: 50px;
  height: 50px;
  color: white;
}

.edit-avatar-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #f79545;
  border: 3px solid white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.edit-avatar-btn:hover {
  transform: scale(1.1);
  background: #e88535;
}

.edit-avatar-btn svg {
  width: 16px;
  height: 16px;
  color: white;
}

.user-name {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.user-role {
  font-size: 14px;
  color: #999;
}

/* 统计 */
.user-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #f79545;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

/* 菜单 */
.user-menu {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 12px;
  border: none;
  background: transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #666;
  font-size: 14px;
}

.menu-item svg {
  width: 20px;
  height: 20px;
}

.menu-item:hover {
  background: #fff5ed;
  color: #f79545;
}

.menu-item.active {
  background: linear-gradient(135deg, #f79545 0%, #ffc494 100%);
  color: white;
}

/* 内容区域 */
.profile-content {
  min-height: 600px;
}

.content-section {
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.edit-btn {
  padding: 8px 20px;
  border-radius: 20px;
  border: 1px solid #f79545;
  background: transparent;
  color: #f79545;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.edit-btn:hover {
  background: #f79545;
  color: white;
}

/* 表单 */
.profile-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #666;
}

.form-group input,
.form-group textarea {
  padding: 12px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  font-size: 14px;
  transition: all 0.3s ease;
  font-family: inherit;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #f79545;
  box-shadow: 0 0 0 3px rgba(247, 149, 69, 0.1);
}

.form-group input:disabled,
.form-group textarea:disabled {
  background: #f8f8f8;
  cursor: not-allowed;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 10px;
}

.btn {
  padding: 10px 24px;
  border-radius: 20px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-primary {
  background: linear-gradient(135deg, #f79545 0%, #ffc494 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(247, 149, 69, 0.4);
}

.btn-secondary {
  background: #f5f5f5;
  color: #666;
}

.btn-secondary:hover {
  background: #e8e8e8;
}

/* 筛选标签 */
.filter-tabs {
  display: flex;
  gap: 10px;
}

.filter-tab {
  padding: 6px 16px;
  border-radius: 20px;
  border: none;
  background: #f5f5f5;
  color: #666;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s ease;
}

.filter-tab:hover {
  background: #ebebeb;
}

.filter-tab.active {
  background: #f79545;
  color: white;
}

/* 内容列表 */
.content-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.content-card {
  display: flex;
  gap: 20px;
  padding: 20px 24px;
  background: #fafafa;
  border-radius: 16px;
  transition: all 0.3s ease;
  position: relative;
  border: 1px solid transparent;
}

.content-card:hover {
  background: #fff;
  border-color: #f0f0f0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.card-image {
  width: 140px;
  height: 100px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.content-card:hover .card-image img {
  transform: scale(1.05);
}

.card-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  padding-right: 50px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.3s ease;
}

.content-card:hover .card-title {
  color: #f79545;
}

.card-desc {
  font-size: 13px;
  color: #666;
  line-height: 1.7;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 12px;
  flex: 1;
}

.card-meta {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #999;
  margin-top: auto;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  transition: color 0.3s ease;
}

.meta-item:hover {
  color: #f79545;
}

.meta-item svg {
  width: 14px;
  height: 14px;
}

.card-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: none;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.action-btn svg {
  width: 16px;
  height: 16px;
}

.action-btn.edit {
  color: #666;
}

.action-btn.edit:hover {
  background: #e3f2fd;
  color: #2196f3;
}

.action-btn.delete {
  color: #666;
}

.action-btn.delete:hover {
  background: #ffebee;
  color: #f44336;
}

.action-btn.unlike {
  color: #f44336;
}

.action-btn.unlike:hover {
  background: #ffebee;
}

/* 故事卡片 */
.story-card {
  flex-direction: column;
  position: relative;
  padding: 20px 24px;
}

.story-card .card-body {
  width: 100%;
}

.story-content {
  font-size: 14px;
  color: #333;
  line-height: 1.8;
  margin-bottom: 16px;
  padding-right: 50px;
}

.story-images {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.story-images img {
  width: 100px;
  height: 100px;
  border-radius: 10px;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.story-images img:hover {
  transform: scale(1.05);
}

.more-images {
  width: 100px;
  height: 100px;
  border-radius: 10px;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.6) 0%, rgba(0, 0, 0, 0.4) 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.more-images:hover {
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.7) 0%, rgba(0, 0, 0, 0.5) 100%);
}

.story-author,
.card-author {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.story-author img,
.card-author img {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.story-author span,
.card-author span {
  font-size: 13px;
  color: #666;
  font-weight: 500;
}

.story-card .card-meta {
  display: flex;
  gap: 20px;
  padding-top: 12px;
  border-top: 1px solid #eee;
}

.story-card .card-actions {
  position: absolute;
  right: 20px;
  top: 20px;
  flex-direction: row;
  gap: 8px;
}

/* 评论卡片 */
.comment-card {
  flex-direction: column;
  position: relative;
  padding: 20px 24px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.comment-target {
  font-size: 13px;
  color: #f79545;
  font-weight: 500;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-content {
  font-size: 14px;
  color: #333;
  line-height: 1.8;
  margin-bottom: 16px;
  padding-right: 40px;
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-stats {
  display: flex;
  gap: 20px;
}

.comment-stats .stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #999;
  transition: all 0.3s ease;
}

.comment-stats .stat-item svg {
  width: 16px;
  height: 16px;
  color: #f79545;
}

.comment-stats .stat-item:hover {
  color: #f79545;
}

.comment-card .card-actions {
  position: absolute;
  right: 20px;
  bottom: 20px;
  flex-direction: row;
  gap: 8px;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state svg {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state p {
  margin-bottom: 20px;
  font-size: 14px;
}

/* 响应式 */
@media (max-width: 768px) {
  .profile-container {
    grid-template-columns: 1fr;
  }

  .user-sidebar {
    position: static;
  }

  .user-card {
    padding: 20px;
  }

  .user-stats {
    flex-direction: row;
    justify-content: space-around;
  }

  .user-menu {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
  }

  .menu-item {
    flex: 1;
    min-width: 100px;
    justify-content: center;
  }

  .content-card {
    flex-direction: column;
  }

  .card-image {
    width: 100%;
    height: 160px;
  }

  .card-actions {
    flex-direction: row;
    justify-content: flex-end;
  }

  .section-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .filter-tabs {
    width: 100%;
    overflow-x: auto;
  }
}
</style>
