<template>
  <section class="recommended" id="destinations">
    <div class="container">
      <h2>发现目的地</h2>
      <div class="destination-grid">
        <div v-if="loading" class="loading">正在加载目的地数据...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <div v-else-if="destinations.length === 0" class="empty">暂无目的地数据</div>
        <div
          v-for="destination in destinations"
          :key="destination.id || destination.name"
          class="destination-card"
          :class="{ 'hovered': hoverCard === destination.name }"
          @mouseenter="hoverCard = destination.name"
          @mouseleave="hoverCard = null"
          @click="viewDestinationDetail(destination)"
        >
          <div class="card-image-wrapper">
            <img :src="destination.image" :alt="destination.alt" loading="lazy">
          </div>
          <h3>{{ destination.name }}</h3>
          <p>{{ destination.description }}</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const emit = defineEmits(['view-destination-detail'])
const hoverCard = ref(null)
const destinations = ref([])
const loading = ref(true)
const error = ref(null)

const fetchDestinations = async () => {
  loading.value = true
  error.value = null
  try {
    const result = await request.get('/api/destinations/hot')
    if (result.code === 'OK' && result.data && Array.isArray(result.data)) {
      destinations.value = result.data.map(item => ({
        id: item.id,
        name: item.name,
        image: item.coverImageUrl,
        alt: `${item.name}旅游景点`,
        description: item.description
      }))
    } else {
      error.value = '获取目的地数据失败: ' + (result.message || '返回数据格式错误')
    }
  } catch (err) {
    error.value = '网络请求失败: ' + err.message
  } finally {
    loading.value = false
  }
}

const viewDestinationDetail = (destination) => {
  emit('view-destination-detail', destination)
}

onMounted(() => {
  fetchDestinations()
})
</script>

<style scoped>
.recommended {
  padding: 80px 0;
  background-color: var(--color-bg-primary);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.recommended h2 {
  text-align: left;
  font-size: 38px;
  margin-bottom: 40px;
  color: var(--color-text-primary);
  font-family: var(--font-display);
  font-weight: 700;
  letter-spacing: 2px;
  position: relative;
  padding-bottom: 15px;
  display: inline-block;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.05);
}

.recommended h2::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 80px;
  height: 4px;
  background: linear-gradient(90deg, var(--color-primary) 0%, var(--color-primary-light) 50%, transparent 100%);
  border-radius: 2px;
}

.recommended h2::before {
  content: '✦';
  position: absolute;
  right: -35px;
  top: 5px;
  font-size: 20px;
  color: var(--color-primary);
  opacity: 0.7;
  animation: twinkle 2s ease-in-out infinite;
}

@keyframes twinkle {
  0%, 100% { opacity: 0.5; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.1); }
}

.destination-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.destination-card {
  background: var(--color-card-bg);
  border: 1px solid var(--color-card-border);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  transition: transform var(--transition-normal), box-shadow var(--transition-normal), background var(--transition-normal);
  cursor: pointer;
  animation: fadeInUp 0.4s ease forwards;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.destination-card.hovered {
  transform: translateY(-5px);
  box-shadow: var(--shadow-lg);
  background: var(--color-card-bg-hover);
}

.card-image-wrapper {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: linear-gradient(135deg, var(--color-bg-secondary) 0%, var(--color-bg-tertiary) 100%);
}

.destination-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.destination-card:hover img {
  transform: scale(1.05);
}

.destination-card h3 {
  padding: 15px;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.destination-card p {
  padding: 0 15px 15px;
  color: var(--color-text-secondary);
  font-size: 14px;
  margin: 0;
}

.loading, .error, .empty {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
  color: var(--color-text-secondary);
  font-size: 16px;
}

.error {
  color: var(--color-error);
}

@media (max-width: 768px) {
  .destination-grid {
    grid-template-columns: 1fr;
  }
}
</style>
