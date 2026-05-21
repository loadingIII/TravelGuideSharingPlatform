<template>
  <div class="story-detail-page">
    <div class="detail-container">
      <button class="back-btn" @click="$emit('back')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
        返回
      </button>

      <div v-if="loading" class="loading-state">
        <div class="loader"><div class="loader-dot"></div><div class="loader-dot"></div><div class="loader-dot"></div></div>
        <p>加载中...</p>
      </div>

      <div v-else-if="error" class="error-state">
        <p>{{ error }}</p>
        <button @click="fetchStory" class="retry-btn">重试</button>
      </div>

      <template v-else-if="story">
        <div class="story-header">
          <div class="author-info">
            <div class="author-avatar">
              <img :src="story.avatar || defaultAvatar" :alt="story.author" @error="e => e.target.src = defaultAvatar">
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
          </div>
          <div class="story-actions-header" v-if="story.isOwner">
            <button class="action-link" @click="openEditModal">编辑</button>
            <button class="action-link danger" @click="deleteStory">删除</button>
          </div>
        </div>

        <div class="story-content">
          <p>{{ story.content }}</p>
        </div>

        <div class="story-gallery" v-if="story.images && story.images.length > 0">
          <div v-for="(img, index) in story.images" :key="index" class="gallery-item" @click="openPreview(index)">
            <img :src="img" :alt="`图片${index + 1}`" loading="lazy">
          </div>
        </div>

        <div class="story-actions-bar">
          <button class="action-btn" :class="{ liked: story.isLiked }" @click="toggleLike">
            <svg viewBox="0 0 24 24" :fill="story.isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
            </svg>
            <span>{{ story.likes }}</span>
          </button>
          <button class="action-btn" @click="shareStory">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8M16 6l-4-4-4 4M12 2v13"/>
            </svg>
            <span>分享</span>
          </button>
        </div>

        <div class="comments-section">
          <h3>评论 ({{ story.comments }})</h3>

          <div class="comment-input-area">
            <textarea v-model="commentText" :placeholder="replyingTo ? '回复 ' + replyingTo.nickname + '...' : '写下你的评论...'" rows="2" @keydown.ctrl.enter="submitComment"></textarea>
            <div class="comment-input-actions">
              <button v-if="replyingTo" class="cancel-reply-btn" @click="cancelReply">取消回复</button>
              <button class="submit-comment-btn" @click="submitComment" :disabled="!commentText.trim()">发表</button>
            </div>
          </div>

          <div v-if="commentsLoading" class="loading-state"><p>加载评论中...</p></div>
          <div v-else-if="comments.length === 0" class="empty-comments"><p>暂无评论</p></div>
          <div v-else class="comments-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <img :src="comment.avatarUrl || defaultAvatar" :alt="comment.nickname" class="commenter-avatar" @error="e => e.target.src = defaultAvatar">
              <div class="comment-body">
                <div class="comment-header">
                  <span class="commenter-name">{{ comment.nickname || '匿名用户' }}</span>
                  <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
                <div class="comment-actions">
                  <button class="comment-action like-action" :class="{ active: commentLiked[comment.id] }" @click="toggleCommentLike(comment)">
                    <svg class="action-icon" viewBox="0 0 24 24" fill="none" width="14" height="14">
                      <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5C22 12.27 18.6 15.36 13.45 20.03L12 21.35Z" :fill="commentLiked[comment.id] ? '#E07070' : 'none'" :stroke="commentLiked[comment.id] ? 'none' : '#E07070'" stroke-width="1.5"/>
                    </svg>
                    {{ comment.likesCount || 0 }}
                  </button>
                  <button class="comment-action reply-action" @click="replyTo(comment)">回复</button>
                  <button v-if="currentUserId && comment.userId === currentUserId" class="comment-action delete-action" @click="deleteComment(comment.id)">删除</button>
                </div>
                <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
                  <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                    <div class="comment-body">
                      <div class="comment-header">
                        <span class="commenter-name">{{ reply.nickname || '匿名用户' }}</span>
                        <span class="comment-time">{{ formatTime(reply.createdAt) }}</span>
                      </div>
                      <p class="comment-text">{{ reply.content }}</p>
                      <div class="comment-actions">
                        <button class="comment-action like-action" :class="{ active: commentLiked[reply.id] }" @click="toggleCommentLike(reply)">
                          <svg class="action-icon" viewBox="0 0 24 24" fill="none" width="14" height="14">
                            <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5C22 12.27 18.6 15.36 13.45 20.03L12 21.35Z" :fill="commentLiked[reply.id] ? '#E07070' : 'none'" :stroke="commentLiked[reply.id] ? 'none' : '#E07070'" stroke-width="1.5"/>
                          </svg>
                          {{ reply.likesCount || 0 }}
                        </button>
                        <button v-if="currentUserId && reply.userId === currentUserId" class="comment-action delete-action" @click="deleteComment(reply.id)">删除</button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- 编辑弹窗 -->
    <div class="modal-overlay" v-if="showEditModal" @click.self="showEditModal = false">
      <div class="modal-content" style="max-width:500px">
        <div class="modal-header">
          <h3>编辑故事</h3>
          <button class="close-btn" @click="showEditModal = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 6L6 18M6 6l12 12"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <textarea v-model="editContent" rows="8" style="width:100%;padding:12px;border:1px solid var(--color-border);border-radius:10px;font-size:14px;font-family:inherit;resize:vertical;box-sizing:border-box"></textarea>
        </div>
        <div class="modal-footer">
          <button class="btn-ghost" @click="showEditModal = false">取消</button>
          <button class="btn-accent" @click="submitEdit">保存</button>
        </div>
      </div>
    </div>

    <!-- 图片预览 -->
    <div class="image-preview-overlay" v-if="previewIndex !== null" @click.self="previewIndex = null">
      <button class="preview-close" @click="previewIndex = null">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M18 6L6 18M6 6l12 12"/>
        </svg>
      </button>
      <button class="preview-nav prev" @click="previewIndex = (previewIndex - 1 + story.images.length) % story.images.length" v-if="story.images.length > 1">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M15 18l-6-6 6-6"/></svg>
      </button>
      <img :src="story.images[previewIndex]" alt="预览" class="preview-image">
      <button class="preview-nav next" @click="previewIndex = (previewIndex + 1) % story.images.length" v-if="story.images.length > 1">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg>
      </button>
      <div class="preview-counter">{{ previewIndex + 1 }} / {{ story.images.length }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import request, { triggerAuthError } from '../utils/request'
import { getCookie } from '../utils/cookie'

const props = defineProps({
  storyId: { type: [Number, String], default: null }
})
const emit = defineEmits(['back'])

const story = ref(null)
const loading = ref(false)
const error = ref(null)

const comments = ref([])
const commentText = ref('')
const commentsLoading = ref(false)
const commentLiked = ref({})
const replyingTo = ref(null)
const currentUserId = ref(null)

const showEditModal = ref(false)
const editContent = ref('')

const defaultAvatar = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="80" height="80" viewBox="0 0 80 80"><rect width="80" height="80" fill="%23E8E4DE" rx="40"/><text x="40" y="48" text-anchor="middle" font-size="32" fill="%23A8A29E" font-family="sans-serif">?</text></svg>')

const previewIndex = ref(null)

const fetchStory = async () => {
  if (!props.storyId) return
  loading.value = true
  error.value = null
  try {
    const res = await request.get(`/api/stories/${props.storyId}`)
    if (res.code === 'OK' || res.code === 200) {
      const data = res.data
      const token = getCookie('token')
      let currentUserIdVal = null
      if (token) {
        try {
          const me = await request.get('/api/users/me')
          if (me.code === 'OK' && me.data) currentUserIdVal = me.data.id
        } catch (e) {}
      }
      currentUserId.value = currentUserIdVal
      story.value = {
        id: data.id,
        author: data.authorName,
        avatar: data.authorAvatarUrl,
        isVip: data.isVip,
        time: formatTime(data.createdAt),
        content: data.content,
        images: data.images || [],
        likes: data.likesCount || 0,
        comments: data.commentsCount || 0,
        isLiked: data.isLiked || false,
        isOwner: currentUserId && data.authorUserId === currentUserId
      }
      await fetchLikeStatus()
      await loadComments()
    }
  } catch (err) {
    error.value = '加载失败'
  } finally {
    loading.value = false
  }
}

const fetchLikeStatus = async () => {
  if (!story.value) return
  try {
    const res = await request.get(`/api/stories/${story.value.id}/like`)
    if (res.code === 'OK') {
      story.value.isLiked = res.data.liked
      story.value.likes = res.data.likesCount
    }
  } catch (e) {}
}

const toggleLike = async () => {
  if (!story.value) return
  try {
    const action = story.value.isLiked ? 'delete' : 'post'
    const res = await request[action](`/api/stories/${story.value.id}/like`)
    if (res.code === 'OK') {
      story.value.isLiked = res.data.liked
      story.value.likes = res.data.likesCount
    }
  } catch (err) {
    triggerAuthError()
  }
}

const shareStory = () => {
  alert('复制链接分享：' + window.location.origin + '/?story=' + story.value.id)
}

const loadComments = async () => {
  if (!story.value) return
  commentsLoading.value = true
  try {
    const res = await request.get(`/api/stories/${story.value.id}/comments`)
    if (res.code === 'OK') {
      const list = Array.isArray(res.data) ? res.data : []
      comments.value = list
      const initialLiked = {}
      const walk = (arr) => {
        arr.forEach(c => {
          initialLiked[c.id] = c.liked || false
          if (c.replies) walk(c.replies)
        })
      }
      walk(list)
      commentLiked.value = initialLiked
      story.value.comments = list.length
    }
  } catch (err) {
    console.error('加载评论失败:', err)
  } finally {
    commentsLoading.value = false
  }
}

const submitComment = async () => {
  if (!commentText.value.trim() || !story.value) return
  const token = getCookie('token')
  if (!token) { triggerAuthError(); return }
  try {
    const res = await request.post(`/api/stories/${story.value.id}/comments`, {
      content: commentText.value,
      parentCommentId: replyingTo.value ? replyingTo.value.id : null
    })
    if (res.code === 'OK') {
      commentText.value = ''
      replyingTo.value = null
      story.value.comments = (story.value.comments || 0) + 1
      await loadComments()
    }
  } catch (err) {
    console.error('发表评论失败:', err)
  }
}

const toggleCommentLike = async (comment) => {
  const token = getCookie('token')
  if (!token) { triggerAuthError(); return }
  try {
    const liked = commentLiked.value[comment.id]
    const res = liked
      ? await request.delete(`/api/stories/comments/${comment.id}/like`)
      : await request.post(`/api/stories/comments/${comment.id}/like`)
    if (res.code === 'OK') {
      commentLiked.value[comment.id] = res.data.liked
      comment.likesCount = res.data.likesCount
    }
  } catch (err) {
    console.error('评论点赞失败:', err)
  }
}

const replyTo = (comment) => {
  replyingTo.value = comment
  commentText.value = ''
}

const cancelReply = () => {
  replyingTo.value = null
  commentText.value = ''
}

const deleteComment = async (commentId) => {
  const token = getCookie('token')
  if (!token) { triggerAuthError(); return }
  try {
    const res = await request.delete(`/api/stories/comments/${commentId}`)
    if (res.code === 'OK') {
      await loadComments()
    }
  } catch (err) {
    console.error('删除评论失败:', err)
  }
}

const openEditModal = () => {
  editContent.value = story.value.content
  showEditModal.value = true
}

const submitEdit = async () => {
  if (!editContent.value.trim()) return
  try {
    const res = await request.put(`/api/stories/${story.value.id}`, { content: editContent.value })
    if (res.code === 'OK') {
      story.value.content = editContent.value
      showEditModal.value = false
    }
  } catch (err) {
    console.error('编辑失败:', err)
  }
}

const deleteStory = async () => {
  if (!confirm('确定删除此故事？')) return
  try {
    const res = await request.delete(`/api/stories/${story.value.id}`)
    if (res.code === 'OK') {
      emit('back')
    }
  } catch (err) {
    console.error('删除失败:', err)
  }
}

const openPreview = (index) => { previewIndex.value = index }

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

onMounted(() => { if (props.storyId) fetchStory() })
watch(() => props.storyId, (id) => { if (id) fetchStory() })
</script>

<style scoped>
.story-detail-page {
  min-height: 100vh;
  background: var(--color-bg-primary);
}
.detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px 20px;
}
.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: none;
  border: none;
  font-size: 14px;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: 8px 0;
  margin-bottom: 20px;
}
.back-btn:hover { color: var(--color-text-primary); }
.back-btn svg { width: 20px; height: 20px; }

.loading-state, .error-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--color-text-muted);
}
.loader { display: flex; gap: 8px; justify-content: center; margin-bottom: 12px; }
.loader-dot {
  width: 8px; height: 8px; border-radius: 50%;
  background: var(--color-primary);
  animation: bounce 0.6s infinite alternate;
}
.loader-dot:nth-child(2) { animation-delay: 0.2s; }
.loader-dot:nth-child(3) { animation-delay: 0.4s; }
@keyframes bounce { from { transform: translateY(0); } to { transform: translateY(-8px); } }
.retry-btn {
  margin-top: 12px;
  padding: 8px 20px;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.story-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.author-info { display: flex; align-items: center; gap: 12px; }
.author-avatar {
  width: 48px; height: 48px; border-radius: 50%;
  overflow: hidden; position: relative; flex-shrink: 0;
}
.author-avatar img { width: 100%; height: 100%; object-fit: cover; }
.vip-ring {
  position: absolute; inset: -2px;
  border-radius: 50%;
  border: 2px solid var(--color-primary);
}
.author-meta { display: flex; flex-direction: column; gap: 2px; }
.author-name {
  display: flex; align-items: center; gap: 6px;
  font-size: 16px; font-weight: 600; color: var(--color-text-primary);
}
.vip-badge { width: 16px; height: 16px; color: var(--color-primary); }
.story-time { font-size: 13px; color: var(--color-text-muted); }
.story-actions-header { display: flex; gap: 12px; }
.action-link {
  background: none; border: none;
  font-size: 14px; color: var(--color-primary); cursor: pointer;
}
.action-link.danger { color: #e07070; }

.story-content {
  font-size: 16px;
  line-height: 1.8;
  color: var(--color-text-primary);
  margin-bottom: 24px;
  white-space: pre-wrap;
}

.story-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 12px;
  margin-bottom: 24px;
}
.gallery-item {
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
}
.gallery-item img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  transition: transform 0.3s;
}
.gallery-item:hover img { transform: scale(1.03); }

.story-actions-bar {
  display: flex;
  gap: 16px;
  padding: 16px 0;
  border-top: 1px solid var(--color-border);
  border-bottom: 1px solid var(--color-border);
  margin-bottom: 24px;
}
.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: none;
  border: none;
  font-size: 14px;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.2s;
}
.action-btn:hover { background: var(--color-bg-secondary); }
.action-btn.liked { color: #e07070; }
.action-btn svg { width: 20px; height: 20px; }

.comments-section { margin-bottom: 40px; }
.comments-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 16px;
}
.comment-input-area {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}
.comment-input-area textarea {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid var(--color-border);
  border-radius: 10px;
  font-size: 14px;
  font-family: inherit;
  resize: none;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}
.comment-input-area textarea:focus { border-color: var(--color-primary); }
.comment-input-actions {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.cancel-reply-btn {
  padding: 10px 14px;
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  border-radius: 10px;
  font-size: 13px;
  color: var(--color-text-muted);
  cursor: pointer;
  white-space: nowrap;
}
.submit-comment-btn {
  padding: 10px 20px;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
}
.submit-comment-btn:disabled { opacity: 0.5; cursor: default; }

.empty-comments { text-align: center; padding: 30px; color: var(--color-text-muted); }
.comments-list { display: flex; flex-direction: column; gap: 16px; }
.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: var(--color-bg-secondary);
  border-radius: 12px;
}
.commenter-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}
.comment-body { flex: 1; min-width: 0; }
.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}
.commenter-name { font-size: 13px; font-weight: 600; color: var(--color-text-primary); }
.comment-time { font-size: 12px; color: var(--color-text-muted); }
.comment-text { font-size: 14px; line-height: 1.6; color: var(--color-text-primary); margin-bottom: 8px; }
.comment-actions { display: flex; gap: 16px; }
.comment-action {
  display: flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  font-size: 13px;
  color: var(--color-text-muted);
  cursor: pointer;
  padding: 2px 4px;
  transition: color 0.2s;
}
.comment-action:hover { color: var(--color-text-primary); }
.comment-action.active { color: #e07070; }
.reply-action { color: var(--color-primary); }
.delete-action { color: #e07070; }
.replies-list {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.reply-item {
  display: flex;
  gap: 12px;
  padding-left: 12px;
  border-left: 2px solid var(--color-border);
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(45,58,30,0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(8px);
}
.modal-content {
  background: var(--color-bg-card, #fff);
  border-radius: 20px;
  width: 90%;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0,0,0,0.2);
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px 16px;
  border-bottom: 1px solid var(--color-border);
}
.modal-header h3 { margin: 0; font-size: 18px; color: var(--color-text-primary); }
.close-btn {
  background: none; border: none; cursor: pointer;
  color: var(--color-text-muted);
}
.close-btn svg { width: 24px; height: 24px; }
.modal-body { padding: 20px 24px; }
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px 20px;
  border-top: 1px solid var(--color-border);
}
.btn-ghost {
  padding: 10px 20px;
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  border-radius: 10px;
  color: var(--color-text-secondary);
  font-size: 14px;
  cursor: pointer;
}
.btn-accent {
  padding: 10px 20px;
  background: var(--color-primary);
  border: none;
  border-radius: 10px;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.image-preview-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}
.preview-close {
  position: absolute;
  top: 20px;
  right: 20px;
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  z-index: 10;
}
.preview-close svg { width: 32px; height: 32px; }
.preview-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255,255,255,0.1);
  border: none;
  color: white;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
.preview-nav.prev { left: 20px; }
.preview-nav.next { right: 20px; }
.preview-nav svg { width: 24px; height: 24px; }
.preview-image {
  max-width: 90%;
  max-height: 85vh;
  object-fit: contain;
  border-radius: 8px;
}
.preview-counter {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  color: white;
  font-size: 14px;
  background: rgba(0,0,0,0.5);
  padding: 6px 16px;
  border-radius: 20px;
}

@media (max-width: 600px) {
  .story-gallery { grid-template-columns: 1fr; }
  .story-actions-header { flex-direction: column; gap: 4px; }
}
</style>
