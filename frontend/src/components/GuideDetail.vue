<template>
  <div class="guide-detail-page">
    <!-- 页面头部 -->
    <header class="detail-header" :style="{ backgroundImage: `url(${guide.coverImage})` }">
      <div class="header-overlay">
        <div class="header-content">
          <div class="back-btn" @click="goBack">
            <span class="arrow">&lt;</span>
            <span>返回社区</span>
          </div>
          <div class="header-info">
            <span class="location-tag">{{ guide.location }}</span>
            <h1>{{ guide.title }}</h1>
            <div class="header-meta">
              <div class="author">
                <img :src="guide.authorAvatar" :alt="guide.author">
                <span>{{ guide.author }}</span>
              </div>
              <div class="stats">
                <span class="stat-item">
                  <svg class="stat-icon likes-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5C22 12.27 18.6 15.36 13.45 20.03L12 21.35Z" fill="url(#detailHeartGradient)" stroke="#e07070" stroke-width="1.5"/>
                    <defs>
                      <linearGradient id="detailHeartGradient" x1="12" y1="3" x2="12" y2="21.35" gradientUnits="userSpaceOnUse">
                        <stop stop-color="#e07070"/>
                        <stop offset="1" stop-color="#e07070"/>
                      </linearGradient>
                    </defs>
                  </svg>
                  <span>{{ guide.likes }}</span>
                </span>
                <span class="stat-item">
                  <svg class="stat-icon views-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 5C7 5 2.73 8.11 1 12C2.73 15.89 7 19 12 19C17 19 21.27 15.89 23 12C21.27 8.11 17 5 12 5Z" fill="rgba(255,255,255,0.2)" stroke="rgba(255,255,255,0.5)" stroke-width="1.5"/>
                    <circle cx="12" cy="12" r="4" fill="rgba(255,255,255,0.8)" stroke="rgba(255,255,255,0.5)" stroke-width="1.5"/>
                    <circle cx="12" cy="12" r="2" fill="#F5F0E8"/>
                  </svg>
                  <span>{{ guide.views }}</span>
                </span>
                <span class="stat-item">
                  <svg class="stat-icon comments-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M20 2H4C2.9 2 2 2.9 2 4V22L6 18H20C21.1 18 22 17.1 22 16V4C22 2.9 21.1 2 20 2Z" fill="rgba(255,255,255,0.15)" stroke="rgba(255,255,255,0.4)" stroke-width="1.5"/>
                    <circle cx="8" cy="10" r="1.5" fill="rgba(255,255,255,0.6)"/>
                    <circle cx="12" cy="10" r="1.5" fill="rgba(255,255,255,0.6)"/>
                    <circle cx="16" cy="10" r="1.5" fill="rgba(255,255,255,0.6)"/>
                    <path d="M6 14H18" stroke="rgba(255,255,255,0.3)" stroke-width="1.5" stroke-linecap="round"/>
                  </svg>
                  <span>{{ guide.comments.length }}</span>
                </span>
                <span class="stat-item">
                  <svg class="stat-icon date-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <rect x="3" y="4" width="18" height="18" rx="2" fill="rgba(255,255,255,0.15)" stroke="rgba(255,255,255,0.4)" stroke-width="1.5"/>
                    <path d="M16 2V6" stroke="rgba(255,255,255,0.5)" stroke-width="1.5" stroke-linecap="round"/>
                    <path d="M8 2V6" stroke="rgba(255,255,255,0.5)" stroke-width="1.5" stroke-linecap="round"/>
                    <path d="M3 10H21" stroke="rgba(255,255,255,0.3)" stroke-width="1.5"/>
                    <circle cx="12" cy="15" r="2" fill="#F5F0E8"/>
                  </svg>
                  <span>{{ guide.publishDate }}</span>
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="header-wave">
        <svg viewBox="0 0 1440 120" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M0 120L60 110C120 100 240 80 360 70C480 60 600 60 720 65C840 70 960 80 1080 85C1200 90 1320 90 1380 90L1440 90V120H1380C1320 120 1200 120 1080 120C960 120 840 120 720 120C600 120 480 120 360 120C240 120 120 120 60 120H0Z" fill="#3D4F2F"/>
        </svg>
      </div>
    </header>

    <!-- 主要内容区 -->
    <main class="detail-content">
      <div class="container">
        <div class="content-layout">
          <!-- 左侧内容 -->
          <article class="article-main">
            <!-- 攻略简介 -->
            <section class="intro-section">
              <h2>攻略简介</h2>
              <p class="intro-text">{{ guide.description }}</p>
              <div class="tags">
                <span v-for="tag in guide.tags" :key="tag" class="tag">{{ tag }}</span>
              </div>
            </section>

            <!-- 行程概览 -->
            <section class="itinerary-section">
              <h2>行程概览</h2>
              <div class="timeline">
                <div 
                  v-for="(day, index) in guide.itinerary" 
                  :key="index"
                  class="timeline-item"
                  :class="{ active: activeDay === index }"
                  @click="activeDay = index"
                >
                  <div class="timeline-marker">
                    <span class="day-num">Day {{ day.day }}</span>
                  </div>
                  <div class="timeline-content">
                    <h3>{{ day.title }}</h3>
                    <p>{{ day.summary }}</p>
                    <div class="day-details" v-show="activeDay === index">
                      <div 
                        v-for="(spot, spotIndex) in day.spots" 
                        :key="spotIndex"
                        class="spot-item"
                      >
                        <img :src="spot.image" :alt="spot.name" loading="lazy">
                        <div class="spot-info">
                          <h4>{{ spot.name }}</h4>
                          <p>{{ spot.description }}</p>
                          <div class="spot-meta">
                            <span class="time">
                              <svg class="meta-icon time-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <circle cx="12" cy="12" r="9" fill="rgba(245,240,232,0.1)" stroke="#F5F0E8" stroke-width="1.5"/>
                                <path d="M12 7V12L15 15" stroke="#F5F0E8" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                                <circle cx="12" cy="12" r="1.5" fill="#F5F0E8"/>
                              </svg>
                              {{ spot.time }}
                            </span>
                            <span class="duration">
                              <svg class="meta-icon duration-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <circle cx="12" cy="12" r="9" fill="rgba(245,240,232,0.1)" stroke="#D4CFC7" stroke-width="1.5"/>
                                <path d="M12 7V12L14 14" stroke="#D4CFC7" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                                <path d="M7 12H9" stroke="#D4CFC7" stroke-width="1.5" stroke-linecap="round"/>
                                <path d="M15 12H17" stroke="#D4CFC7" stroke-width="1.5" stroke-linecap="round"/>
                              </svg>
                              {{ spot.duration }}
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </section>

            <!-- 详细内容 -->
            <section class="content-section">
              <h2>详细攻略</h2>
              <div class="rich-content" v-html="sanitizedContent"></div>
            </section>

            <!-- 实用信息 -->
            <section class="tips-section">
              <h2>实用信息</h2>
              <div class="tips-grid">
                <div class="tip-card" v-for="tip in guide.tips" :key="tip.category">
                  <h3>{{ tip.category }}</h3>
                  <ul>
                    <li v-for="(item, idx) in tip.items" :key="idx">{{ item }}</li>
                  </ul>
                </div>
              </div>
            </section>

            <!-- 互动区 -->
            <section class="interaction-section">
              <div class="action-buttons">
                <button
                  class="action-btn like-btn"
                  :class="{ active: isLiked }"
                  @click="toggleLike"
                >
                  <svg class="btn-icon like-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5C22 12.27 18.6 15.36 13.45 20.03L12 21.35Z" :fill="isLiked ? 'url(#btnHeartGradient)' : 'rgba(255,255,255,0.1)'" :stroke="isLiked ? '#e07070' : 'rgba(255,255,255,0.4)'" stroke-width="1.5"/>
                  </svg>
                  <span class="btn-text">{{ guide.likes + (isLiked ? 1 : 0) }}</span>
                </button>
                <button class="action-btn collect-btn" :class="{ active: isCollected }" @click="toggleCollect">
                  <svg class="btn-icon collect-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M5 5C5 3.89543 5.89543 3 7 3H17C18.1046 3 19 3.89543 19 5V21L12 17.5L5 21V5Z" :fill="isCollected ? 'rgba(245,240,232,0.2)' : 'rgba(255,255,255,0.1)'" :stroke="isCollected ? '#F5F0E8' : 'rgba(255,255,255,0.4)'" stroke-width="1.5"/>
                    <path d="M12 7V13" :stroke="isCollected ? '#F5F0E8' : 'rgba(255,255,255,0.4)'" stroke-width="1.5" stroke-linecap="round" v-if="!isCollected"/>
                    <path d="M9 10H15" :stroke="isCollected ? '#F5F0E8' : 'rgba(255,255,255,0.4)'" stroke-width="1.5" stroke-linecap="round" v-if="!isCollected"/>
                    <path d="M9 10L11 12L15 8" stroke="#F5F0E8" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" v-if="isCollected"/>
                  </svg>
                  <span class="btn-text">{{ isCollected ? '已收藏' : '收藏' }}</span>
                </button>
                <button class="action-btn share-btn" @click="showShareModal = true">
                  <svg class="btn-icon share-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="18" cy="5" r="3" fill="rgba(245,240,232,0.15)" stroke="#F5F0E8" stroke-width="1.5"/>
                    <circle cx="6" cy="12" r="3" fill="rgba(245,240,232,0.1)" stroke="#D4CFC7" stroke-width="1.5"/>
                    <circle cx="18" cy="19" r="3" fill="rgba(245,240,232,0.15)" stroke="#F5F0E8" stroke-width="1.5"/>
                    <path d="M8.59 13.51L15.42 17.49" stroke="#A8A29E" stroke-width="1.5" stroke-linecap="round"/>
                    <path d="M15.41 6.51L8.59 10.49" stroke="#A8A29E" stroke-width="1.5" stroke-linecap="round"/>
                  </svg>
                  <span class="btn-text">分享</span>
                </button>
              </div>
              <defs>
                <linearGradient id="btnHeartGradient" x1="12" y1="3" x2="12" y2="21.35" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#e07070"/>
                  <stop offset="1" stop-color="#e07070"/>
                </linearGradient>
              </defs>
            </section>

            <!-- 评论区 -->
            <section class="comments-section">
              <h2>评论 ({{ guide.comments.length }})</h2>
              <div class="comment-form">
                <img :src="currentUser.avatar" alt="我的头像" class="user-avatar">
                <div class="input-area">
                  <textarea 
                    v-model="newComment" 
                    :placeholder="replyingTo ? `回复 ${replyingTo.nickname}...` : '分享你的想法...'"
                    rows="3"
                  ></textarea>
                  <div class="form-actions">
                    <button v-if="replyingTo" class="cancel-btn" @click="cancelReply">取消回复</button>
                    <button class="submit-btn" @click="submitComment">发表评论</button>
                  </div>
                </div>
              </div>
              <div class="comments-list">
                <div
                  v-for="comment in guide.comments"
                  :key="comment.id"
                  class="comment-item"
                >
                  <img :src="comment.avatarUrl || '/img/avatar-default.png'" :alt="comment.nickname" class="commenter-avatar">
                  <div class="comment-content">
                    <div class="comment-header">
                      <span class="commenter-name">{{ comment.nickname || '匿名用户' }}</span>
                      <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
                    </div>
                    <p class="comment-text">{{ comment.content }}</p>
                    <div class="comment-actions">
                      <button 
                        class="comment-action like-action" 
                        :class="{ active: comment.isLiked }"
                        @click="toggleCommentLike(comment)"
                      >
                        <svg class="action-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                          <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5C22 12.27 18.6 15.36 13.45 20.03L12 21.35Z" :fill="comment.isLiked ? 'url(#commentHeartGradient)' : 'none'" stroke="rgba(255,255,255,0.3)" stroke-width="1.5"/>
                        </svg>
                        {{ comment.likesCount }}
                      </button>
                      <button class="comment-action reply-action" @click="replyTo(comment)">回复</button>
                    </div>
                    <!-- 回复列表 -->
                    <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
                      <div 
                        v-for="reply in comment.replies" 
                        :key="reply.id"
                        class="reply-item"
                      >
                        <img :src="reply.avatarUrl || '/img/avatar-default.png'" :alt="reply.nickname" class="reply-avatar">
                        <div class="reply-content">
                          <div class="reply-header">
                            <span class="reply-name">{{ reply.nickname || '匿名用户' }}</span>
                            <span class="reply-time">{{ formatTime(reply.createdAt) }}</span>
                          </div>
                          <p class="reply-text">{{ reply.content }}</p>
                          <div class="reply-actions">
                            <button 
                              class="comment-action like-action" 
                              :class="{ active: reply.isLiked }"
                              @click="toggleCommentLike(reply)"
                            >
                              <svg class="action-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5C22 12.27 18.6 15.36 13.45 20.03L12 21.35Z" :fill="reply.isLiked ? 'url(#replyHeartGradient)' : 'none'" stroke="rgba(255,255,255,0.3)" stroke-width="1.5"/>
                              </svg>
                              {{ reply.likesCount }}
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <defs>
                <linearGradient id="commentHeartGradient" x1="12" y1="3" x2="12" y2="21.35" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#e07070"/>
                  <stop offset="1" stop-color="#e07070"/>
                </linearGradient>
                <linearGradient id="replyHeartGradient" x1="12" y1="3" x2="12" y2="21.35" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#e07070"/>
                  <stop offset="1" stop-color="#e07070"/>
                </linearGradient>
              </defs>
            </section>
          </article>

          <!-- 右侧侧边栏 -->
          <aside class="article-sidebar">
            <!-- 作者信息 -->
            <div class="sidebar-card author-card">
              <div class="author-header">
                <img :src="guide.authorAvatar" :alt="guide.author" class="author-avatar-large">
                <div class="author-info">
                  <h3>{{ guide.author }}</h3>
                  <span class="author-level">{{ guide.authorLevel }}</span>
                </div>
              </div>
              <div class="author-stats">
                <div class="stat">
                  <span class="stat-num">{{ guide.authorGuides }}</span>
                  <span class="stat-label">攻略</span>
                </div>
                <div class="stat">
                  <span class="stat-num">{{ guide.authorFollowers }}</span>
                  <span class="stat-label">粉丝</span>
                </div>
                <div class="stat">
                  <span class="stat-num">{{ guide.authorLikes }}</span>
                  <span class="stat-label">获赞</span>
                </div>
              </div>
              <button class="follow-btn" :class="{ following: isFollowing }" @click="toggleFollow">
                {{ isFollowing ? '已关注' : '+ 关注' }}
              </button>
            </div>

            <!-- 相关攻略 -->
            <div class="sidebar-card related-guides">
              <h3>相关攻略</h3>
              <div class="related-list">
                <div 
                  v-for="related in guide.relatedGuides" 
                  :key="related.id"
                  class="related-item"
                  @click="goToGuide(related.id)"
                >
                  <img :src="related.image" :alt="related.title" loading="lazy">
                  <div class="related-info">
                    <h4>{{ related.title }}</h4>
                    <span>{{ related.views }} 次浏览</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 热门标签 -->
            <div class="sidebar-card hot-tags">
              <h3>热门标签</h3>
              <div class="tags-cloud">
                <span 
                  v-for="tag in hotTags" 
                  :key="tag"
                  class="tag-item"
                  @click="searchByTag(tag)"
                >
                  {{ tag }}
                </span>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </main>

    <!-- 分享模态框 -->
    <div class="modal-overlay" v-if="showShareModal" @click.self="showShareModal = false">
      <div class="share-modal">
        <div class="modal-header">
          <h3>分享攻略</h3>
          <button class="close-btn" @click="showShareModal = false">&times;</button>
        </div>
        <div class="share-options">
          <button class="share-option wechat">
            <span>微信</span>
          </button>
          <button class="share-option weibo">
            <span>微博</span>
          </button>
          <button class="share-option qq">
            <span>QQ</span>
          </button>
          <button class="share-option link" @click="copyLink">
            <span>复制链接</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import request from '../utils/request'
import DOMPurify from 'dompurify'

const props = defineProps({
  guideId: { type: [Number, String], default: null }
})

const emit = defineEmits(['back-to-community', 'view-guide'])

// 当前展开的行程天数
const activeDay = ref(0)

// 用户交互状态
const isLiked = ref(false)
const isCollected = ref(false)
const isFollowing = ref(false)
const newComment = ref('')
const showShareModal = ref(false)

// 评论相关
const replyingTo = ref(null)
const isLoadingComments = ref(false)

// 当前用户信息
const currentUser = reactive({
  avatar: '/img/头像1.jpg',
  id: 1,
  nickname: '我'
})

// 热门标签
const hotTags = ['日本', '京都', '赏樱', '自由行', '美食', '摄影', '穷游', '蜜月']

// 净化后的HTML内容（防止XSS攻击）
const sanitizedContent = computed(() => {
  return DOMPurify.sanitize(guide.content, {
    ALLOWED_TAGS: ['p', 'br', 'strong', 'em', 'u', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6',
                   'ul', 'ol', 'li', 'a', 'img', 'blockquote', 'pre', 'code', 'table',
                   'thead', 'tbody', 'tr', 'th', 'td', 'div', 'span'],
    ALLOWED_ATTR: ['href', 'src', 'alt', 'title', 'class', 'target', 'rel']
  })
})

// 返回社区
const goBack = () => {
  emit('back-to-community')
}

// 跳转到其他攻略
const goToGuide = (guideId) => {
  emit('view-guide', guideId)
}

// 点赞
const toggleLike = () => {
  isLiked.value = !isLiked.value
}

// 收藏
const toggleCollect = () => {
  isCollected.value = !isCollected.value
}

// 关注作者
const toggleFollow = () => {
  isFollowing.value = !isFollowing.value
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  // 小于1小时
  if (diff < 3600000) {
    const minutes = Math.floor(diff / 60000)
    return minutes < 1 ? '刚刚' : `${minutes}分钟前`
  }
  // 小于24小时
  if (diff < 86400000) {
    const hours = Math.floor(diff / 3600000)
    return `${hours}小时前`
  }
  // 小于7天
  if (diff < 604800000) {
    const days = Math.floor(diff / 86400000)
    return `${days}天前`
  }
  // 大于7天显示日期
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

// 加载评论
const loadComments = async () => {
  isLoadingComments.value = true
  try {
    const result = await request.get(`/api/guides/comments/${guide.id}`)
    if (result.code === 'OK') {
      // 为每条评论添加 isLiked 状态
      guide.comments = (result.data || []).map(comment => ({
        ...comment,
        isLiked: false,
        replies: (comment.replies || []).map(reply => ({
          ...reply,
          isLiked: false
        }))
      }))
    }
  } catch (error) {
    console.error('加载评论失败:', error)
    // 使用模拟数据
    guide.comments = [
      {
        id: 1,
        guideId: 101,
        userId: 1,
        nickname: '旅行达人',
        avatarUrl: '/img/头像2.png',
        content: '这篇攻略太详细了，收藏了！',
        parentCommentId: null,
        likesCount: 12,
        createdAt: '2025-05-01T10:30:00',
        isLiked: false,
        replies: [
          {
            id: 2,
            guideId: 101,
            userId: 2,
            nickname: '背包客小王',
            avatarUrl: '/img/头像1.jpg',
            content: '同意！行程安排得很合理',
            parentCommentId: 1,
            likesCount: 3,
            createdAt: '2025-05-01T11:00:00',
            isLiked: false
          }
        ]
      },
      {
        id: 4,
        guideId: 101,
        userId: 4,
        nickname: '摄影师老李',
        avatarUrl: '/img/头像3.png',
        content: '照片拍得真好看，用的什么相机？',
        parentCommentId: null,
        likesCount: 5,
        createdAt: '2025-05-01T14:00:00',
        isLiked: false,
        replies: []
      }
    ]
  } finally {
    isLoadingComments.value = false
  }
}

// 提交评论
const submitComment = async () => {
  if (!newComment.value.trim()) return
  
  // 这里应该调用API提交评论
  const newCommentData = {
    id: Date.now(),
    guideId: guide.id,
    userId: currentUser.id,
    nickname: currentUser.nickname,
    avatarUrl: currentUser.avatar,
    content: newComment.value,
    parentCommentId: replyingTo.value ? replyingTo.value.id : null,
    likesCount: 0,
    createdAt: new Date().toISOString(),
    isLiked: false,
    replies: []
  }
  
  if (replyingTo.value) {
    // 添加到回复列表
    const parentComment = guide.comments.find(c => c.id === replyingTo.value.id)
    if (parentComment) {
      if (!parentComment.replies) parentComment.replies = []
      parentComment.replies.push(newCommentData)
    }
    replyingTo.value = null
  } else {
    // 添加到评论列表顶部
    guide.comments.unshift(newCommentData)
  }
  
  newComment.value = ''
}

// 取消回复
const cancelReply = () => {
  replyingTo.value = null
  newComment.value = ''
}

// 评论点赞
const toggleCommentLike = async (comment) => {
  comment.isLiked = !comment.isLiked
  comment.likesCount += comment.isLiked ? 1 : -1
  // 这里应该调用API
}

// 回复评论
const replyTo = (comment) => {
  replyingTo.value = comment
  newComment.value = ''
}

// 复制链接
const copyLink = () => {
  alert('链接已复制到剪贴板！')
  showShareModal.value = false
}

// 按标签搜索
const searchByTag = (tag) => {
  console.log('搜索标签:', tag)
}

// 攻略数据
const guide = reactive({
  id: null,
  title: '',
  description: '',
  coverImage: '',
  location: '',
  author: '',
  authorAvatar: '',
  authorLevel: '',
  authorGuides: 0,
  authorFollowers: 0,
  authorLikes: 0,
  likes: 0,
  views: 0,
  publishDate: '',
  tags: [],
  itinerary: [],
  content: '',
  tips: [],
  budget: { total: 0, breakdown: {} },
  comments: [],
  relatedGuides: []
})

const isLoading = ref(false)

// 从API获取攻略详情
const fetchGuideData = async (id) => {
  if (!id) return
  isLoading.value = true
  try {
    const result = await request.get(`/api/guides/${id}`)
    if (result.code === 200 || result.code === 'OK') {
      const data = result.data
      guide.id = data.id
      guide.title = data.title
      guide.description = data.summary || ''
      guide.coverImage = data.coverImageUrl || '/img/富士山.jpg'
      guide.location = data.locationText || ''
      guide.author = data.authorName || ''
      guide.authorAvatar = data.authorAvatarUrl || '/img/头像1.jpg'
      guide.authorLevel = '旅行家'
      guide.authorGuides = 0
      guide.authorFollowers = 0
      guide.authorLikes = 0
      guide.likes = data.likesCount || 0
      guide.views = data.viewsCount || 0
      guide.publishDate = data.publishedAt ? new Date(data.publishedAt).toLocaleDateString('zh-CN') : ''
      guide.tags = []
      guide.content = data.contentHtml || ''
      guide.itinerary = (data.itinerary || []).map(day => ({
        day: day.dayNo,
        title: day.title,
        summary: day.summary,
        spots: (day.spots || []).map(spot => ({
          name: spot.name,
          description: spot.description,
          time: spot.time,
          duration: spot.duration,
          image: spot.imageUrl || '/img/富士山.jpg'
        }))
      }))
      guide.tips = [
        { icon: '最佳', category: '最佳时间', items: ['提前规划行程', '关注天气预报'] },
        { icon: '货币', category: '货币消费', items: ['提前兑换货币', '准备零钱'] },
        { icon: '天气', category: '天气穿着', items: ['查看目的地天气', '准备合适衣物'] },
        { icon: '网络', category: '网络通讯', items: ['租借随身WiFi', '下载离线地图'] }
      ]
      guide.budget = { total: 0, breakdown: {} }
      guide.relatedGuides = []
    }
  } catch (error) {
    console.error('获取攻略详情失败:', error)
  } finally {
    isLoading.value = false
  }
}

// 页面加载时
onMounted(() => {
  if (props.guideId) {
    fetchGuideData(props.guideId)
  }
  loadComments()
})

// 监听guideId变化
watch(() => props.guideId, (newId) => {
  if (newId) {
    fetchGuideData(newId)
  }
})
</script>

<style scoped>
/* 页面整体样式 */
.guide-detail-page {
  min-height: 100vh;
  background-color: #3D4F2F;
  font-family: 'Noto Sans SC', sans-serif;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 页面头部 */
.detail-header {
  background-size: cover;
  background-position: center;
  padding: 80px 0 0;
  position: relative;
  min-height: 500px;
}

.header-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0.5) 0%, rgba(0,0,0,0.3) 50%, rgba(0,0,0,0.6) 100%);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  position: relative;
  z-index: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 25px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  width: fit-content;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateX(-5px);
}

.header-info {
  margin-top: auto;
  padding-bottom: 40px;
}

.location-tag {
  display: inline-block;
  padding: 8px 20px;
  background: linear-gradient(135deg, #F5F0E8 0%, #FAF8F5 100%);
  color: #2F3D24;
  border-radius: 20px;
  font-size: 14px;
  margin-bottom: 20px;
  font-weight: 600;
}

.header-info h1 {
  font-family: 'Noto Serif SC', serif;
  font-size: 48px;
  color: white;
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  letter-spacing: 2px;
}

.header-meta {
  display: flex;
  align-items: center;
  gap: 30px;
  flex-wrap: wrap;
}

.author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author img {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  border: 3px solid white;
  object-fit: cover;
}

.author span {
  color: white;
  font-size: 16px;
  font-weight: 500;
}

.stats {
  display: flex;
  gap: 25px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  transition: all 0.3s ease;
}

.stat-item:hover {
  color: white;
  transform: translateY(-2px);
}

.stat-icon {
  width: 18px;
  height: 18px;
  transition: all 0.3s ease;
  filter: drop-shadow(0 2px 3px rgba(0, 0, 0, 0.2));
}

.stat-item:hover .stat-icon {
  transform: scale(1.15);
}

.stat-item:hover .likes-icon {
  filter: drop-shadow(0 3px 6px rgba(255, 154, 158, 0.5));
}

.stat-item:hover .views-icon {
  filter: drop-shadow(0 3px 6px rgba(200, 200, 200, 0.4));
}

.stat-item:hover .comments-icon {
  filter: drop-shadow(0 3px 6px rgba(122, 161, 197, 0.4));
}

.stat-item:hover .date-icon {
  filter: drop-shadow(0 3px 6px rgba(200, 200, 200, 0.4));
}

.header-wave {
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
}

.header-wave svg {
  display: block;
  width: 100%;
}

.header-wave {
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
}

/* 主要内容区 */
.detail-content {
  padding: 40px 0 80px;
}

.content-layout {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 40px;
}

/* 文章主体 */
.article-main section {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 20px;
  padding: 35px;
  margin-bottom: 30px;
}

.article-main h2 {
  font-family: 'Noto Serif SC', serif;
  font-size: 26px;
  color: #F5F0E8;
  margin-bottom: 25px;
  padding-left: 15px;
  border-left: 4px solid #F5F0E8;
}

/* 简介区 */
.intro-text {
  font-size: 16px;
  line-height: 1.8;
  color: #D4CFC7;
  margin-bottom: 20px;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag {
  padding: 8px 18px;
  background: rgba(245, 240, 232, 0.1);
  color: #F5F0E8;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tag:hover {
  background: #F5F0E8;
  color: #2F3D24;
}

/* 行程时间线 */
.timeline {
  position: relative;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 60px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(to bottom, #F5F0E8, rgba(245, 240, 232, 0.3));
}

.timeline-item {
  display: flex;
  gap: 30px;
  padding: 25px 0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.timeline-item:hover {
  background: rgba(245, 240, 232, 0.05);
}

.timeline-marker {
  flex-shrink: 0;
  width: 80px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}

.day-num {
  display: inline-block;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid rgba(245, 240, 232, 0.3);
  color: #F5F0E8;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.timeline-item.active .day-num,
.timeline-item:hover .day-num {
  background: #F5F0E8;
  color: #2F3D24;
  border-color: #F5F0E8;
}

.timeline-content {
  flex: 1;
  padding-left: 20px;
}

.timeline-content h3 {
  font-size: 20px;
  color: #F5F0E8;
  margin-bottom: 8px;
}

.timeline-content > p {
  color: #D4CFC7;
  font-size: 15px;
  margin-bottom: 15px;
}

.day-details {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px dashed rgba(255, 255, 255, 0.15);
  animation: slideDown 0.3s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.spot-item {
  display: flex;
  gap: 20px;
  padding: 20px;
  background: rgba(245, 240, 232, 0.05);
  border-radius: 15px;
  margin-bottom: 15px;
}

.spot-item img {
  width: 150px;
  height: 100px;
  object-fit: cover;
  border-radius: 10px;
  flex-shrink: 0;
}

.spot-info h4 {
  font-size: 17px;
  color: #F5F0E8;
  margin-bottom: 8px;
}

.spot-info p {
  font-size: 14px;
  color: #D4CFC7;
  line-height: 1.6;
  margin-bottom: 10px;
}

.spot-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #A8A29E;
}

.spot-meta .time,
.spot-meta .duration {
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s ease;
}

.spot-meta .time:hover,
.spot-meta .duration:hover {
  color: #F5F0E8;
  transform: translateY(-1px);
}

.spot-meta .meta-icon {
  width: 15px;
  height: 15px;
  transition: all 0.3s ease;
  filter: drop-shadow(0 2px 2px rgba(0, 0, 0, 0.08));
}

.spot-meta .time:hover .meta-icon,
.spot-meta .duration:hover .meta-icon {
  transform: scale(1.12);
}

.spot-meta .time:hover .time-icon {
  filter: drop-shadow(0 3px 4px rgba(245, 240, 232, 0.25));
}

.spot-meta .duration:hover .duration-icon {
  filter: drop-shadow(0 3px 4px rgba(212, 207, 199, 0.25));
}

/* 详细内容 */
.rich-content {
  font-size: 16px;
  line-height: 1.9;
  color: #D4CFC7;
}

.rich-content h3 {
  font-size: 22px;
  color: #F5F0E8;
  margin: 30px 0 15px;
  font-family: 'Noto Serif SC', serif;
}

.rich-content p {
  margin-bottom: 15px;
}

/* 实用信息 */
.tips-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.tip-card {
  padding: 25px;
  background: rgba(245, 240, 232, 0.05);
  border-radius: 15px;
  border: 1px solid rgba(245, 240, 232, 0.15);
}

.tip-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.tip-card h3 {
  font-size: 18px;
  color: #F5F0E8;
  margin-bottom: 15px;
  border: none;
  padding: 0;
}

.tip-card ul {
  list-style: none;
  padding: 0;
}

.tip-card li {
  font-size: 14px;
  color: #D4CFC7;
  padding: 6px 0;
  padding-left: 20px;
  position: relative;
}

.tip-card li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #F5F0E8;
  font-weight: bold;
}

/* 费用预算 */
.budget-summary {
  display: flex;
  gap: 40px;
  align-items: flex-start;
}

.total-budget {
  text-align: center;
  padding: 30px 40px;
  background: linear-gradient(135deg, #F5F0E8 0%, #FAF8F5 100%);
  border-radius: 20px;
  color: #2F3D24;
  flex-shrink: 0;
}

.total-budget .label {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 14px;
  margin-bottom: 10px;
  opacity: 0.9;
}

.total-budget .label .budget-icon {
  width: 16px;
  height: 16px;
}

.total-budget .label .budget-icon circle[fill="#fff8e7"] {
  fill: rgba(255, 255, 255, 0.3);
}

.total-budget .label .budget-icon circle[stroke="#f5a623"] {
  stroke: rgba(255, 255, 255, 0.8);
}

.total-budget .label .budget-icon text {
  fill: white;
}

.total-budget .amount {
  display: block;
  font-size: 42px;
  font-weight: 700;
}

.budget-breakdown {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.budget-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.budget-label {
  width: 60px;
  font-size: 14px;
  color: #D4CFC7;
}

.budget-bar {
  flex: 1;
  height: 10px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 5px;
  overflow: hidden;
}

.budget-fill {
  height: 100%;
  background: linear-gradient(90deg, #F5F0E8, #FAF8F5);
  border-radius: 5px;
  transition: width 0.5s ease;
}

.budget-value {
  width: 70px;
  text-align: right;
  font-size: 14px;
  color: #F5F0E8;
  font-weight: 500;
}

/* 互动按钮 */
.interaction-section {
  text-align: center;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  border: 2px solid rgba(255, 255, 255, 0.15);
  background: rgba(255, 255, 255, 0.08);
  border-radius: 25px;
  font-size: 15px;
  color: #D4CFC7;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-btn:hover {
  border-color: #F5F0E8;
  color: #F5F0E8;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 240, 232, 0.15);
}

.action-btn.active {
  background: linear-gradient(135deg, #F5F0E8 0%, #FAF8F5 100%);
  border-color: #F5F0E8;
  color: #2F3D24;
  box-shadow: 0 4px 15px rgba(245, 240, 232, 0.3);
}

.action-btn.active .btn-icon {
  filter: drop-shadow(0 2px 3px rgba(0, 0, 0, 0.1));
}

.btn-icon {
  width: 22px;
  height: 22px;
  transition: all 0.3s ease;
}

.action-btn:hover .btn-icon {
  transform: scale(1.1);
}

.like-icon {
  filter: drop-shadow(0 2px 3px rgba(224, 112, 112, 0.15));
}

.collect-icon {
  filter: drop-shadow(0 2px 3px rgba(245, 240, 232, 0.15));
}

.share-icon {
  filter: drop-shadow(0 2px 3px rgba(245, 240, 232, 0.15));
}

.action-btn.active .like-icon {
  filter: drop-shadow(0 2px 4px rgba(255, 255, 255, 0.3));
}

.btn-text {
  font-weight: 500;
}

/* 评论区 */
.comment-form {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.input-area {
  flex: 1;
}

.input-area textarea {
  width: 100%;
  padding: 15px;
  border: 2px solid rgba(255, 255, 255, 0.15);
  border-radius: 15px;
  font-size: 15px;
  resize: vertical;
  min-height: 100px;
  margin-bottom: 10px;
  transition: border-color 0.3s ease;
  background: rgba(255, 255, 255, 0.06);
  color: #F5F0E8;
}

.input-area textarea:focus {
  outline: none;
  border-color: #F5F0E8;
}

.input-area textarea::placeholder {
  color: #A8A29E;
}

.submit-btn {
  padding: 12px 30px;
  background: linear-gradient(135deg, #F5F0E8 0%, #FAF8F5 100%);
  color: #2F3D24;
  border: none;
  border-radius: 25px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 240, 232, 0.4);
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.comment-item {
  display: flex;
  gap: 15px;
}

.commenter-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  border: 2px solid rgba(255, 255, 255, 0.15);
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.commenter-name {
  font-weight: 600;
  color: #F5F0E8;
}

.comment-time {
  font-size: 13px;
  color: #A8A29E;
}

.comment-text {
  font-size: 15px;
  color: #D4CFC7;
  line-height: 1.7;
  margin-bottom: 10px;
}

.comment-actions {
  display: flex;
  gap: 20px;
}

.comment-action {
  background: none;
  border: none;
  color: #A8A29E;
  font-size: 13px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.comment-action:hover,
.comment-action.active {
  color: #F5F0E8;
}

.comment-action.like-action {
  display: flex;
  align-items: center;
  gap: 5px;
}

.comment-action.like-action .action-icon {
  width: 16px;
  height: 16px;
  transition: all 0.3s ease;
}

.comment-action.like-action:hover .action-icon,
.comment-action.like-action.active .action-icon {
  transform: scale(1.1);
}

.comment-action.like-action.active {
  color: #e07070;
}

.comment-action.like-action.active .action-icon path {
  stroke: #e07070;
}

/* 回复列表 */
.replies-list {
  margin-top: 15px;
  padding-left: 20px;
  border-left: 2px solid rgba(255, 255, 255, 0.1);
}

.reply-item {
  display: flex;
  gap: 12px;
  padding: 15px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  border: 2px solid rgba(255, 255, 255, 0.15);
}

.reply-content {
  flex: 1;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.reply-name {
  font-weight: 600;
  font-size: 14px;
  color: #F5F0E8;
}

.reply-time {
  font-size: 12px;
  color: #A8A29E;
}

.reply-text {
  font-size: 14px;
  color: #D4CFC7;
  line-height: 1.6;
  margin-bottom: 8px;
}

.reply-actions {
  display: flex;
  gap: 15px;
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  align-items: center;
}

.cancel-btn {
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.08);
  color: #D4CFC7;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  color: #F5F0E8;
}

/* 加载状态 */
.loading-comments {
  text-align: center;
  padding: 40px;
  color: #A8A29E;
}

/* 侧边栏 */
.article-sidebar {
  position: sticky;
  top: 100px;
  height: fit-content;
}

.sidebar-card {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 20px;
  padding: 25px;
  margin-bottom: 25px;
}

.sidebar-card h3 {
  font-family: 'Noto Serif SC', serif;
  font-size: 20px;
  color: #F5F0E8;
  margin-bottom: 20px;
}

/* 作者卡片 */
.author-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.author-avatar-large {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #F5F0E8;
}

.author-info h3 {
  font-size: 18px;
  margin-bottom: 5px;
  color: #F5F0E8;
}

.author-level {
  font-size: 13px;
  color: #F5F0E8;
  background: rgba(245, 240, 232, 0.15);
  padding: 4px 12px;
  border-radius: 12px;
}

.author-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  margin-bottom: 20px;
}

.stat {
  text-align: center;
}

.stat-num {
  display: block;
  font-size: 20px;
  font-weight: 700;
  color: #F5F0E8;
}

.stat-label {
  font-size: 13px;
  color: #A8A29E;
}

.follow-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #F5F0E8 0%, #FAF8F5 100%);
  color: #2F3D24;
  border: none;
  border-radius: 25px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.follow-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 240, 232, 0.4);
}

.follow-btn.following {
  background: rgba(255, 255, 255, 0.1);
  color: #A8A29E;
}

/* 相关攻略 */
.related-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.related-item {
  display: flex;
  gap: 12px;
  cursor: pointer;
  padding: 10px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.related-item:hover {
  background: rgba(245, 240, 232, 0.08);
}

.related-item img {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}

.related-info h4 {
  font-size: 15px;
  color: #F5F0E8;
  margin-bottom: 5px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.related-info span {
  font-size: 12px;
  color: #A8A29E;
}

/* 热门标签 */
.tags-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-item {
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  font-size: 13px;
  color: #D4CFC7;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tag-item:hover {
  background: #F5F0E8;
  color: #2F3D24;
}

/* 分享模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(8px);
}

.share-modal {
  background: #3D4F2F;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 24px;
  padding: 30px;
  width: 90%;
  max-width: 400px;
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
  margin-bottom: 25px;
}

.modal-header h3 {
  font-size: 22px;
  font-family: 'Noto Serif SC', serif;
  color: #F5F0E8;
}

.close-btn {
  background: none;
  border: none;
  font-size: 28px;
  color: #A8A29E;
  cursor: pointer;
}

.share-options {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.share-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 25px;
  background: rgba(255, 255, 255, 0.08);
  border: none;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.share-option:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-3px);
}

.share-option .icon {
  font-size: 32px;
}

.share-option span:last-child {
  font-size: 14px;
  color: #D4CFC7;
}

/* 响应式设计 */
@media (max-width: 968px) {
  .content-layout {
    grid-template-columns: 1fr;
  }

  .article-sidebar {
    position: static;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 20px;
  }

  .header-info h1 {
    font-size: 32px;
  }

  .budget-summary {
    flex-direction: column;
  }

  .tips-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .header-meta {
    flex-direction: column;
    gap: 15px;
  }

  .spot-item {
    flex-direction: column;
  }

  .spot-item img {
    width: 100%;
    height: 150px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
