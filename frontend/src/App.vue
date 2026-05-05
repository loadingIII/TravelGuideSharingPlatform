<template>
  <div class="app">
    <Navbar
      v-if="currentPage === 'home' || currentPage === 'hot-guides' || currentPage === 'traveler-stories' || currentPage === 'destination-guides'"
      @switch-page="switchPage"
      :user-info="userInfo"
      :current-page="currentPage"
      @logout="handleLogout"
      @view-profile="switchPage('user-profile')"
    />
    <main v-if="currentPage === 'home'">
      <HeroSection />
      <RecommendedDestinations />
      <CustomerReviews @switch-page="switchPage" />
    </main>
    <Footer v-if="currentPage === 'home'" />

    <Login v-if="currentPage === 'login'" @switch-page="switchPage" @login-success="handleLoginSuccess" />
    <Register v-if="currentPage === 'register'" @switch-page="switchPage" />
    <GuideDetail
      v-if="currentPage === 'guide-detail'"
      @back-to-community="switchPage('hot-guides')"
      @view-guide="viewGuideDetail"
    />
    <HotGuides
      v-if="currentPage === 'hot-guides'"
      @back-to-home="switchPage('home')"
      @view-guide-detail="viewGuideDetail"
    />
    <TravelerStories
      v-if="currentPage === 'traveler-stories'"
      @back-to-home="switchPage('home')"
    />
    <DestinationGuides
      v-if="currentPage === 'destination-guides'"
      @back-to-home="switchPage('home')"
    />
    <UserProfile
      v-if="currentPage === 'user-profile'"
      :user-info="userInfo"
      @back="switchPage('home')"
      @update-user="handleUpdateUser"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCookie, removeCookie, setCookie } from './utils/cookie'
import Navbar from './components/Navbar.vue'
import HeroSection from './components/HeroSection.vue'
import RecommendedDestinations from './components/RecommendedDestinations.vue'
import CustomerReviews from './components/CustomerReviews.vue'

import Footer from './components/Footer.vue'
import Login from './components/Login.vue'
import Register from './components/Register.vue'
import GuideDetail from './components/GuideDetail.vue'
import HotGuides from './components/HotGuides.vue'
import TravelerStories from './components/TravelerStories.vue'
import DestinationGuides from './components/DestinationGuides.vue'
import UserProfile from './components/UserProfile.vue'

const currentPage = ref('home')
const currentGuideId = ref(null)
const userInfo = ref(null)

const switchPage = (page) => {
  currentPage.value = page
  // 滚动到页面顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const viewGuideDetail = (guideId) => {
  currentGuideId.value = guideId
  switchPage('guide-detail')
}

const handleLoginSuccess = (user) => {
  userInfo.value = user
}

const handleLogout = () => {
  userInfo.value = null
  removeCookie('token')
  removeCookie('userInfo')
}

const handleUpdateUser = (updatedUser) => {
  userInfo.value = { ...userInfo.value, ...updatedUser }
  // 更新Cookie中的用户信息
  setCookie('userInfo', JSON.stringify(userInfo.value), 7)
}

onMounted(() => {
  // 页面加载动画
  document.body.style.opacity = '0'
  document.body.style.transition = 'opacity 0.5s ease-in-out'
  
  setTimeout(() => {
    document.body.style.opacity = '1'
  }, 100)
  
  // 检查Cookie中的用户信息
  const storedUserInfo = getCookie('userInfo')
  if (storedUserInfo) {
    try {
      userInfo.value = JSON.parse(storedUserInfo)
    } catch (e) {
      console.error('解析用户信息失败:', e)
      // 解析失败时清除无效的Cookie
      removeCookie('userInfo')
      removeCookie('token')
    }
  }
})
</script>

<style>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Noto Sans SC', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  line-height: 1.6;
  color: #333;
  background-color: #f8f6f3;
}

/* 自定义滚动条样式 */
::-webkit-scrollbar {
  width: 10px;
  height: 10px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 5px;
}

::-webkit-scrollbar-thumb {
  background: #7aa1c5;
  border-radius: 5px;
}

::-webkit-scrollbar-thumb:hover {
  background: #5a8ab5;
}

/* Firefox 滚动条 */
html {
  scrollbar-width: thin;
  scrollbar-color: #7aa1c5 #f1f1f1;
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

section {
  animation: fadeIn 0.8s ease-out;
}
</style>
