<template>
  <section class="customer-reviews">
    <div class="container">
      <div class="section-header">
        <h2>热门旅游攻略</h2>
        <a href="#hot-guides" class="view-more" @click.prevent="viewMore">
          查看详情 <span class="arrow">&gt;</span>
        </a>
      </div>
      <div v-if="loading" class="loading">正在加载攻略数据...</div>
      <div v-else-if="error" class="error">{{ error }}</div>
      <div v-else-if="guides.length === 0" class="empty">暂无攻略数据</div>
      <div v-else class="reviews-grid">
        <div
          v-for="guide in guides"
          :key="guide.id"
          class="review-card"
          @mouseenter="hoverCard = guide.id"
          @mouseleave="hoverCard = null"
          :class="{ 'hovered': hoverCard === guide.id }"
        >
          <div class="reviewer-info">
            <img :src="guide.authorAvatarUrl || '/img/默认头像.png'" :alt="guide.authorName + '的头像'" loading="lazy">
            <div class="author-meta">
              <h4>{{ guide.authorName }}</h4>
              <span class="destination-tag">{{ guide.destinationName }}</span>
            </div>
          </div>
          <h3 class="guide-title">{{ guide.title }}</h3>
          <p>{{ guide.summary }}</p>
          <div class="guide-meta">
            <span class="meta-item">
              <svg class="meta-icon location-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2C8.13 2 5 5.13 5 9C5 14.25 12 22 12 22C12 22 19 14.25 19 9C19 5.13 15.87 2 12 2Z" fill="#fff5f5" stroke="#e85a5a" stroke-width="1.5"/>
                <circle cx="12" cy="9" r="2.5" fill="#e85a5a"/>
              </svg>
              {{ guide.locationText }}
            </span>
            <span class="meta-item">
              <svg class="meta-icon time-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="9" fill="#f0f7ff" stroke="#4a90d9" stroke-width="1.5"/>
                <path d="M12 7V12L15 15" stroke="#4a90d9" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="12" cy="12" r="1.5" fill="#4a90d9"/>
              </svg>
              {{ guide.days }}天
            </span>
            <span class="meta-item">
              <svg class="meta-icon budget-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="9" fill="#fff8e7" stroke="#f5a623" stroke-width="1.5"/>
                <text x="12" y="16" text-anchor="middle" fill="#f5a623" font-size="10" font-weight="bold">¥</text>
              </svg>
              ¥{{ guide.budgetTotal }}
            </span>
          </div>
          <div class="guide-stats">
            <span class="stat-item">
              <svg class="stat-icon views-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 5C7 5 2.73 8.11 1 12C2.73 15.89 7 19 12 19C17 19 21.27 15.89 23 12C21.27 8.11 17 5 12 5Z" fill="#e8e8e8" stroke="#999" stroke-width="1.5"/>
                <circle cx="12" cy="12" r="4" fill="#fff" stroke="#999" stroke-width="1.5"/>
                <circle cx="12" cy="12" r="2" fill="#f79545"/>
              </svg>
              {{ formatNumber(guide.viewsCount) }}
            </span>
            <span class="stat-item">
              <svg class="stat-icon likes-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5C22 12.27 18.6 15.36 13.45 20.03L12 21.35Z" fill="url(#heartGradient)" stroke="#e85a5a" stroke-width="1.5"/>
                <defs>
                  <linearGradient id="heartGradient" x1="12" y1="3" x2="12" y2="21.35" gradientUnits="userSpaceOnUse">
                    <stop stop-color="#ff9a9e"/>
                    <stop offset="1" stop-color="#f79545"/>
                  </linearGradient>
                </defs>
              </svg>
              {{ formatNumber(guide.likesCount) }}
            </span>
            <span class="stat-item">
              <svg class="stat-icon comments-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 2H4C2.9 2 2 2.9 2 4V22L6 18H20C21.1 18 22 17.1 22 16V4C22 2.9 21.1 2 20 2Z" fill="#f8f8f8" stroke="#7aa1c5" stroke-width="1.5"/>
                <circle cx="8" cy="10" r="1.5" fill="#7aa1c5"/>
                <circle cx="12" cy="10" r="1.5" fill="#7aa1c5"/>
                <circle cx="16" cy="10" r="1.5" fill="#7aa1c5"/>
                <path d="M6 14H18" stroke="#7aa1c5" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
              {{ formatNumber(guide.commentsCount) }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const hoverCard = ref(null)
const guides = ref([])
const loading = ref(true)
const error = ref(null)
const emit = defineEmits(['view-community'])

// 格式化数字（超过1000显示为1k）
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num
}

// 获取热门攻略数据
const fetchTopGuides = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await fetch('/api/guides/ranking/likes')
    const result = await response.json()
    console.log('攻略接口返回数据:', result)
    if (result.code === 'OK' && result.data && Array.isArray(result.data)) {
      guides.value = result.data
    } else {
      error.value = '获取攻略数据失败: ' + (result.message || '返回数据格式错误')
    }
  } catch (err) {
    console.error('请求攻略接口失败:', err)
    error.value = '网络请求失败: ' + err.message
  } finally {
    loading.value = false
  }
}

const viewMore = () => {
  emit('switch-page', 'hot-guides')
}

onMounted(() => {
  fetchTopGuides()
})
</script>

<style scoped>
.customer-reviews {
  padding: 80px 0;
  background-color: #f8f6f3;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  margin-bottom: 40px;
}

.customer-reviews h2 {
  font-size: 38px;
  color: #333;
  margin: 0;
  text-align: left;
  font-family: 'Noto Serif SC', serif;
  font-weight: 700;
  letter-spacing: 2px;
  position: relative;
  padding: 12px 30px;
  background: linear-gradient(135deg, rgba(247, 149, 69, 0.08) 0%, rgba(255, 196, 148, 0.08) 100%);
  border-radius: 16px;
  border: 2px dashed rgba(247, 149, 69, 0.4);
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.05);
  box-shadow: 0 4px 15px rgba(247, 149, 69, 0.1);
}

.customer-reviews h2::before {
  content: '❝';
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 24px;
  color: #f79545;
  opacity: 0.7;
  font-family: Georgia, serif;
}

.view-more {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #ff6b6b;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: color 0.3s ease;
  cursor: pointer;
}

.view-more:hover {
  color: #ff5252;
}

.view-more .arrow {
  font-size: 16px;
  transition: transform 0.3s ease;
}

.view-more:hover .arrow {
  transform: translateX(3px);
}

.loading, .error, .empty {
  text-align: center;
  padding: 60px 20px;
  color: #666;
  font-size: 16px;
}

.error {
  color: #e74c3c;
}

.reviews-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
}

.review-card {
  background: #ffffff;
  border: 1px solid #f0f0f0;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.review-card.hovered {
  transform: translateY(-5px);
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
}

.reviewer-info {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.reviewer-info img {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin-right: 15px;
  object-fit: cover;
}

.author-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.reviewer-info h4 {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
}

.destination-tag {
  font-size: 12px;
  color: #f79545;
  background: rgba(247, 149, 69, 0.15);
  padding: 2px 8px;
  border-radius: 12px;
  width: fit-content;
}

.guide-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 10px 0;
  color: #333;
  line-height: 1.4;
}

.review-card p {
  margin-bottom: 15px;
  color: #666;
  line-height: 1.6;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.guide-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #888;
  transition: all 0.3s ease;
}

.meta-item:hover {
  color: #666;
  transform: translateY(-1px);
}

.meta-icon {
  width: 16px;
  height: 16px;
  transition: all 0.3s ease;
  filter: drop-shadow(0 2px 2px rgba(0, 0, 0, 0.08));
}

.meta-item:hover .meta-icon {
  transform: scale(1.12);
}

.meta-item:hover .location-icon {
  filter: drop-shadow(0 3px 4px rgba(232, 90, 90, 0.25));
}

.meta-item:hover .time-icon {
  filter: drop-shadow(0 3px 4px rgba(74, 144, 217, 0.25));
}

.meta-item:hover .budget-icon {
  filter: drop-shadow(0 3px 4px rgba(245, 166, 35, 0.25));
}

.guide-stats {
  display: flex;
  gap: 20px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
  transition: all 0.3s ease;
}

.stat-item:hover {
  color: #333;
  transform: translateY(-2px);
}

.stat-icon {
  width: 20px;
  height: 20px;
  transition: all 0.3s ease;
  filter: drop-shadow(0 2px 3px rgba(0, 0, 0, 0.1));
}

.stat-item:hover .stat-icon {
  transform: scale(1.15);
  filter: drop-shadow(0 3px 5px rgba(0, 0, 0, 0.15));
}

.stat-item:hover .views-icon {
  filter: drop-shadow(0 3px 5px rgba(153, 153, 153, 0.3));
}

.stat-item:hover .likes-icon {
  filter: drop-shadow(0 3px 5px rgba(232, 90, 90, 0.3));
}

.stat-item:hover .comments-icon {
  filter: drop-shadow(0 3px 5px rgba(122, 161, 197, 0.3));
}

@media (max-width: 768px) {
  .reviews-grid {
    grid-template-columns: 1fr;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .guide-meta {
    gap: 8px;
  }

  .guide-stats {
    gap: 12px;
  }
}
</style>
