<template>
  <section class="hero" aria-label="英雄区域" id="home">
    <div class="hero-content">
      <h1>开启冒险之旅</h1>
      <p>与去哪儿一起发现您的下一次冒险</p>
      <form class="search-form" @submit.prevent="handleSearch" aria-label="搜索表单">
        <div class="search-input-wrapper">
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="出发吧" 
            aria-label="搜索"
          >
          <button type="submit" class="search-btn" aria-label="搜索">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"></circle>
              <path d="m21 21-4.35-4.35"></path>
            </svg>
          </button>
        </div>
      </form>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'

const searchQuery = ref('')

const handleSearch = () => {
  if (!searchQuery.value.trim()) {
    alert('请输入目的地')
    return
  }
  alert(`搜索成功！正在为您查找 "${searchQuery.value}" 的相关旅行信息...`)
}
</script>

<style scoped>
.hero {
  background-image: url('/img/1.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: white;
  position: relative;
}

.hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 800px;
  padding: 0 20px;
}

.hero h1 {
  font-size: 48px;
  margin-bottom: 20px;
  font-weight: 700;
}

.hero p {
  font-size: 20px;
  margin-bottom: 30px;
}

.search-form {
  display: flex;
  justify-content: center;
}

.search-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 550px;
}

.search-form input {
  padding: 18px 60px 18px 25px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 50px;
  font-size: 16px;
  width: 100%;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  color: white;
  outline: none;
  transition: all 0.3s ease;
}

.search-form input::placeholder {
  color: rgba(255, 255, 255, 0.8);
}

.search-form input:focus {
  background: rgba(255, 255, 255, 0.35);
  border-color: rgba(255, 255, 255, 0.5);
}

.search-btn {
  position: absolute;
  right: 6px;
  top: 50%;
  transform: translateY(-50%);
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: transparent;
  border: none;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 0;
}

.search-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-50%) scale(1.05);
}

@media (max-width: 768px) {
  .hero h1 {
    font-size: 36px;
  }

  .hero p {
    font-size: 18px;
  }

  .search-input-wrapper {
    width: 100%;
    max-width: 350px;
  }
}

@media (max-width: 480px) {
  .hero h1 {
    font-size: 28px;
  }

  .hero p {
    font-size: 16px;
  }
}
</style>
