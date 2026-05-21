<template>
  <div class="destination-detail-page">
    <header class="page-header" :style="{ backgroundImage: `url(${destination.image})` }">
      <div class="header-overlay">
        <div class="header-content">
          <div class="back-btn" @click="goBack">
            <span class="arrow">&lt;</span>
            <span>返回目的地</span>
          </div>
          <h1>{{ destination.name }}</h1>
          <p class="subtitle">{{ destination.description }}</p>
          <div class="dest-stats" v-if="destination.guidesCount">
            <span>{{ destination.guidesCount }} 篇攻略</span>
            <span>{{ destination.travelersCount }} 人去过</span>
            <span v-if="destination.rating">★ {{ destination.rating }}</span>
          </div>
        </div>
      </div>
    </header>

    <section class="guides-section">
      <div class="container">
        <h2>相关攻略</h2>
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>正在加载攻略...</p>
        </div>
        <div v-else-if="guides.length === 0" class="empty-state">
          <p>暂无相关攻略</p>
        </div>
        <div v-else class="guides-grid">
          <div
            v-for="guide in guides"
            :key="guide.id"
            class="guide-card"
            @click="$emit('view-guide', guide.id)"
          >
            <div class="card-image">
              <img :src="guide.image" :alt="guide.title" loading="lazy" @error="e => e.target.src = '/img/富士山.jpg'">
            </div>
            <div class="card-content">
              <h3>{{ guide.title }}</h3>
              <p class="description">{{ guide.summary }}</p>
              <div class="card-meta">
                <span class="author">{{ guide.authorName }}</span>
                <div class="stats">
                  <span>❤ {{ guide.likesCount || 0 }}</span>
                  <span>👁 {{ guide.viewsCount || 0 }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import request from '../utils/request'

const props = defineProps({
  destinationId: { type: Number, required: true }
})

const emit = defineEmits(['back-to-destinations', 'view-guide'])

const destination = ref({
  name: '',
  description: '',
  image: '/img/富士山.jpg',
  guidesCount: 0,
  travelersCount: 0,
  rating: null
})
const guides = ref([])
const loading = ref(false)

const goBack = () => {
  emit('back-to-destinations')
}

const loadDestination = async () => {
  try {
    const result = await request.get(`/api/destinations/${props.destinationId}`)
    if (result.code === 'OK' && result.data) {
      const d = result.data
      destination.value = {
        name: d.name,
        description: d.description,
        image: d.coverImageUrl || '/img/富士山.jpg',
        guidesCount: d.guidesCount,
        travelersCount: d.travelersCount,
        rating: d.ratingAvg
      }
    }
  } catch (err) {
    console.error('加载目的地详情失败:', err)
  }
}

const loadGuides = async () => {
  loading.value = true
  try {
    const result = await request.get(`/api/destinations/${props.destinationId}/guides?page=1&pageSize=20`)
    if (result.code === 'OK' && result.data && result.data.list) {
      guides.value = result.data.list.map(g => ({
        id: g.id,
        title: g.title,
        summary: g.summary,
        image: g.coverImageUrl || '/img/富士山.jpg',
        authorName: g.authorName,
        likesCount: g.likesCount,
        viewsCount: g.viewsCount
      }))
    }
  } catch (err) {
    console.error('加载攻略列表失败:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDestination()
  loadGuides()
})

watch(() => props.destinationId, () => {
  loadDestination()
  loadGuides()
})
</script>

<style scoped>
.destination-detail-page {
  min-height: 100vh;
  background-color: var(--color-bg-primary);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  background-size: cover;
  background-position: center;
  padding: 60px 0 80px;
  position: relative;
  min-height: 400px;
}

.header-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, rgba(45,58,30,0.3) 0%, rgba(45,58,30,0.55) 60%, var(--color-bg-primary) 100%);
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
  justify-content: flex-end;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  width: fit-content;
  margin-bottom: 20px;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateX(-5px);
}

.header-content h1 {
  font-family: var(--font-display);
  font-size: 48px;
  color: white;
  margin-bottom: 12px;
  text-shadow: 2px 2px 4px rgba(45, 58, 30, 0.3);
}

.subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 16px;
  max-width: 500px;
  line-height: 1.6;
}

.dest-stats {
  display: flex;
  gap: 20px;
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
}

.guides-section {
  padding: 40px 0 80px;
}

.guides-section h2 {
  font-family: var(--font-display);
  font-size: 28px;
  margin-bottom: 30px;
  color: var(--color-text-primary);
  padding-left: 15px;
  border-left: 4px solid var(--color-primary);
}

.loading-state, .empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--color-text-muted);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--color-card-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin { to { transform: rotate(360deg); } }

.guides-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
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
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(45, 58, 30, 0.15);
  border-color: var(--color-primary);
}

.card-image {
  height: 180px;
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
  padding: 20px;
}

.card-content h3 {
  font-family: var(--font-display);
  font-size: 17px;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--color-text-primary);
  line-height: 1.4;
}

.description {
  font-size: 14px;
  color: var(--color-text-muted);
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid var(--color-card-border);
}

.author {
  font-size: 13px;
  color: var(--color-text-muted);
}

.stats {
  display: flex;
  gap: 12px;
  font-size: 13px;
  color: var(--color-text-muted);
}
</style>
