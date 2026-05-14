<template>
  <div class="app">
    <Navbar
      v-if="currentPage === 'home' || currentPage === 'hot-guides' || currentPage === 'traveler-stories' || currentPage === 'destination-guides' || currentPage === 'destination-detail' || currentPage === 'my-guides'"
      @switch-page="switchPage"
      :user-info="userInfo"
      :current-page="currentPage"
      @logout="handleLogout"
      @view-profile="switchPage('user-profile')"
    />
    <main v-if="currentPage === 'home'">
      <HeroSection @search="handleSearch" />
      <RecommendedDestinations @view-destination-detail="viewDestinationDetail" />
      <CustomerReviews @switch-page="switchPage" @view-guide-detail="viewGuideDetail" />
    </main>
    <Footer v-if="currentPage === 'home'" />

    <Login v-if="currentPage === 'login'" @switch-page="switchPage" @login-success="handleLoginSuccess" />
    <Register v-if="currentPage === 'register'" @switch-page="switchPage" />
    <GuideDetail
      v-if="currentPage === 'guide-detail'"
      :guide-id="currentGuideId"
      @back-to-community="switchPage('hot-guides')"
      @view-guide="viewGuideDetail"
    />
    <HotGuides
      v-if="currentPage === 'hot-guides'"
      :initial-search="searchQuery"
      @back-to-home="switchPage('home')"
      @view-guide-detail="viewGuideDetail"
      @switch-page="switchPage"
    />
    <TravelerStories
      v-if="currentPage === 'traveler-stories'"
      @back-to-home="switchPage('home')"
    />
    <DestinationGuides
      v-if="currentPage === 'destination-guides'"
      @back-to-home="switchPage('home')"
      @view-destination-detail="viewDestinationDetail"
    />
    <DestinationDetail
      v-if="currentPage === 'destination-detail'"
      :destination-id="currentDestinationId"
      @back-to-destinations="switchPage('destination-guides')"
      @view-guide="viewGuideDetail"
    />
    <UserProfile
      v-if="currentPage === 'user-profile'"
      :user-info="userInfo"
      @back="switchPage('home')"
      @update-user="handleUpdateUser"
      @view-my-guides="switchPage('my-guides')"
      @view-public-profile="viewPublicProfile"
    />
    <PublicProfile
      v-if="currentPage === 'public-profile'"
      :user-id="currentPublicUserId"
      @back="switchPage('user-profile')"
      @view-guide="viewGuideDetail"
    />
    <MyGuides
      v-if="currentPage === 'my-guides'"
      @back="switchPage('home')"
      @view-guide-detail="viewGuideDetail"
    />
    <Toast ref="toastRef" />
  </div>
</template>

<script setup>
import { ref, onMounted, provide } from 'vue'
import { getCookie, removeCookie, setCookie } from './utils/cookie'
import { setOnAuthError } from './utils/request'
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
import DestinationDetail from './components/DestinationDetail.vue'
import UserProfile from './components/UserProfile.vue'
import PublicProfile from './components/PublicProfile.vue'
import MyGuides from './components/MyGuides.vue'
import Toast from './components/Toast.vue'

const currentPage = ref('home')
const currentGuideId = ref(null)
const currentDestinationId = ref(null)
const currentPublicUserId = ref(null)
const searchQuery = ref('')
const userInfo = ref(null)
const toastRef = ref(null)

provide('toast', {
  success: (msg) => toastRef.value?.addToast(msg, 'success'),
  error: (msg) => toastRef.value?.addToast(msg, 'error'),
  warning: (msg) => toastRef.value?.addToast(msg, 'warning'),
  info: (msg) => toastRef.value?.addToast(msg, 'info')
})

const switchPage = (page) => {
  currentPage.value = page
  // 滚动到页面顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

setOnAuthError(() => {
  switchPage('login')
})

const viewGuideDetail = (guideId) => {
  currentGuideId.value = guideId
  switchPage('guide-detail')
}

const viewDestinationDetail = (dest) => {
  currentDestinationId.value = dest.id
  switchPage('destination-detail')
}

const viewPublicProfile = (userId) => {
  currentPublicUserId.value = userId
  switchPage('public-profile')
}

const handleSearch = (query) => {
  searchQuery.value = query
  switchPage('hot-guides')
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
@import './styles/variables.css';

/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: var(--font-body);
  line-height: 1.6;
  color: var(--color-text-primary);
  background-color: var(--color-bg-primary);
}

/* 自定义滚动条样式 */
::-webkit-scrollbar {
  width: 10px;
  height: 10px;
}

::-webkit-scrollbar-track {
  background: var(--color-bg-secondary);
  border-radius: 5px;
}

::-webkit-scrollbar-thumb {
  background: var(--color-primary-light);
  border-radius: 5px;
}

::-webkit-scrollbar-thumb:hover {
  background: var(--color-primary);
}

/* Firefox 滚动条 */
html {
  scrollbar-width: thin;
  scrollbar-color: var(--color-primary-light) var(--color-bg-secondary);
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
