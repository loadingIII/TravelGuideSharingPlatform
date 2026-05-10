<template>
  <header class="navbar" :style="navbarStyle">
    <div class="container">
      <div class="navbar-brand">
        <a href="#">旅游攻略平台</a>
      </div>
      <button class="navbar-toggle" @click="toggleMenu" :class="{ 'active': isMenuOpen }">
        <span class="toggle-icon" :style="iconStyles[0]"></span>
        <span class="toggle-icon" :style="iconStyles[1]"></span>
        <span class="toggle-icon" :style="iconStyles[2]"></span>
      </button>
      <nav class="navbar-menu" :class="{ 'active': isMenuOpen }">
        <ul>
          <li v-for="item in menuItems" :key="item.name" :class="{ 'active': isActive(item.href) }">
            <a :href="item.href" @click.prevent="handleNavClick(item.href)">{{ item.name }}</a>
          </li>
        </ul>
      </nav>
      <div class="navbar-actions">
        <!-- 未登录状态：显示登录和注册按钮 -->
        <template v-if="!userInfo">
          <a href="#" class="btn btn-primary" @click.prevent="handleLogin">登录</a>
          <a href="#" class="btn btn-secondary" @click.prevent="handleRegister">注册</a>
        </template>
        <!-- 已登录状态：显示用户信息 -->
        <template v-else>
          <div class="user-info" @click="handleViewProfile" title="进入个人中心">
            <div class="user-avatar">
              <img 
                v-if="userInfo.avatar" 
                :src="userInfo.avatar" 
                alt="用户头像"
                @error="handleAvatarError"
              >
              <svg v-show="!userInfo.avatar || avatarError" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
            </div>
            <span class="user-name">{{ userInfo.username }}</span>
            <button class="logout-btn" @click.stop="handleLogout" title="退出登录">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                <polyline points="16 17 21 12 16 7"/>
                <line x1="21" y1="12" x2="9" y2="12"/>
              </svg>
            </button>
          </div>
        </template>
      </div>
    </div>
  </header>

  <!-- 关于我们弹窗 -->
  <div class="about-modal-overlay" v-if="showAboutModal" @click.self="closeAboutModal">
    <div class="about-modal">
      <button class="about-modal-close" @click="closeAboutModal">&times;</button>
      <div class="about-modal-content">
        <h2>关于我们</h2>
        <div class="about-section">
          <h3>平台简介</h3>
          <p>旅游攻略平台是一个专注于为全球旅行者提供优质旅游攻略和目的地指南的综合性服务平台。我们致力于帮助每一位旅行者发现世界的美好，规划完美的旅程。</p>
        </div>
        <div class="about-section">
          <h3>我们的使命</h3>
          <p>让每一次旅行都成为难忘的回忆。我们相信，旅行不仅是到达目的地，更是沿途的风景和故事。通过汇聚全球旅行者的智慧，我们为您提供最真实、最实用的旅行建议。</p>
        </div>
        <div class="about-section">
          <h3>核心功能</h3>
          <ul>
            <li>海量攻略：覆盖全球热门目的地，从国内游到出境游应有尽有</li>
            <li>真实体验：来自真实旅行者的分享，拒绝虚假宣传</li>
            <li>智能推荐：根据您的偏好，推荐最适合的旅行方案</li>
            <li>社区互动：与志同道合的旅行者交流心得，结识新朋友</li>
          </ul>
        </div>
        <div class="about-section">
          <h3>联系我们</h3>
          <p>如果您有任何建议或合作意向，欢迎通过以下方式联系我们：</p>
          <p class="contact-info">邮箱：contact@travelguide.com</p>
          <p class="contact-info">电话：400-888-8888</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  userInfo: {
    type: Object,
    default: null
  },
  currentPage: {
    type: String,
    default: 'home'
  }
})

const emit = defineEmits(['switch-page', 'logout', 'view-profile'])

const isMenuOpen = ref(false)
const scrollY = ref(0)
const avatarError = ref(false)

const menuItems = [
  { name: '首页', href: '#home' },
  { name: '热门攻略', href: '#hot-guides' },
  { name: '旅行者故事', href: '#traveler-stories' },
  { name: '目的地指南', href: '#destination-guides' },
  { name: '关于我们', href: '#about' }
]

const navbarStyle = computed(() => ({
  backgroundColor: scrollY.value > 100 ? 'rgba(61, 79, 47, 0.95)' : 'rgba(61, 79, 47, 0.9)',
  boxShadow: scrollY.value > 100 ? '0 2px 10px rgba(0, 0, 0, 0.2)' : '0 2px 4px rgba(0, 0, 0, 0.15)'
}))

const iconStyles = computed(() => {
  if (!isMenuOpen.value) {
    return [{}, {}, {}]
  }
  return [
    { transform: 'rotate(45deg) translate(5px, 5px)' },
    { opacity: '0' },
    { transform: 'rotate(-45deg) translate(7px, -6px)' }
  ]
})

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}

// 判断当前导航项是否激活
const isActive = (href) => {
  const pageMap = {
    '#home': 'home',
    '#hot-guides': 'hot-guides',
    '#traveler-stories': 'traveler-stories',
    '#destination-guides': 'destination-guides',
    '#about': 'about'
  }
  return pageMap[href] === props.currentPage
}

// 关于我们弹窗显示状态
const showAboutModal = ref(false)

const handleNavClick = (href) => {
  isMenuOpen.value = false
  if (href === '#') return
  
  // 点击首页菜单，返回首页
  if (href === '#home') {
    emit('switch-page', 'home')
    return
  }
  
  // 点击热门攻略菜单，跳转到热门攻略页面
  if (href === '#hot-guides') {
    emit('switch-page', 'hot-guides')
    return
  }
  
  // 点击旅行者故事菜单，跳转到旅行者故事页面
  if (href === '#traveler-stories') {
    emit('switch-page', 'traveler-stories')
    return
  }
  
  // 点击目的地指南菜单，跳转到目的地指南页面
  if (href === '#destination-guides') {
    emit('switch-page', 'destination-guides')
    return
  }
  
  // 点击关于我们，显示弹窗
  if (href === '#about') {
    showAboutModal.value = true
    return
  }
  
  const targetElement = document.querySelector(href)
  if (targetElement) {
    window.scrollTo({
      top: targetElement.offsetTop - 70,
      behavior: 'smooth'
    })
  }
}

// 关闭关于我们弹窗
const closeAboutModal = () => {
  showAboutModal.value = false
}

const handleLogin = () => {
  emit('switch-page', 'login')
}

const handleRegister = () => {
  emit('switch-page', 'register')
}

const handleLogout = () => {
  emit('logout')
}

const handleAvatarError = () => {
  avatarError.value = true
}

const handleViewProfile = () => {
  emit('view-profile')
}

const handleScroll = () => {
  scrollY.value = window.scrollY
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.navbar {
  background: rgba(61, 79, 47, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 1000;
  transition: all var(--transition-normal);
}

.navbar .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.navbar-brand a {
  font-size: 26px;
  font-weight: 700;
  color: var(--color-primary);
  text-decoration: none;
  font-family: var(--font-display);
  letter-spacing: 3px;
  position: relative;
  padding: 5px 0;
  text-shadow: 1px 1px 2px rgba(212, 118, 58, 0.3);
  transition: all var(--transition-normal);
}

.navbar-brand a::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--gradient-primary);
  transition: width var(--transition-normal);
  border-radius: 1px;
}

.navbar-brand a:hover::after {
  width: 100%;
}

.navbar-brand a:hover {
  color: var(--color-primary-dark);
  text-shadow: 2px 2px 4px rgba(212, 118, 58, 0.4);
}

.navbar-toggle {
  display: none;
  flex-direction: column;
  justify-content: space-between;
  width: 30px;
  height: 21px;
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0;
}

.toggle-icon {
  display: block;
  width: 100%;
  height: 3px;
  background-color: var(--color-text-primary);
  border-radius: 1.5px;
  transition: all var(--transition-normal);
}

.navbar-menu ul {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
}

.navbar-menu li {
  margin-left: 30px;
  position: relative;
}

.navbar-menu li.active::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--gradient-primary);
  border-radius: 2px;
}

.navbar-menu a {
  color: var(--color-text-primary);
  text-decoration: none;
  font-weight: 500;
  transition: all var(--transition-normal);
  padding: 5px 0;
}

.navbar-menu li.active a {
  color: var(--color-primary);
  font-weight: 600;
}

.navbar-menu a:hover {
  color: var(--color-primary);
}

.navbar-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

/* 用户信息样式 - 优化版 */
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.15) 0%, rgba(255, 255, 255, 0.1) 100%);
  border-radius: 50px;
  border: 1px solid var(--color-primary-20);
  box-shadow: 
    0 4px 15px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: default;
  position: relative;
  overflow: hidden;
}

.user-info::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    var(--color-primary-10),
    transparent
  );
  transition: left 0.5s ease;
}

.user-info:hover::before {
  left: 100%;
}

.user-info:hover {
  transform: translateY(-2px);
  box-shadow: 
    0 8px 25px var(--color-primary-20),
    0 4px 10px rgba(0, 0, 0, 0.06),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  border-color: var(--color-primary);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  background: linear-gradient(135deg, var(--color-primary-light) 0%, var(--color-primary) 50%, var(--color-primary-dark) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 
    0 3px 8px var(--color-primary-20),
    0 0 0 3px var(--color-primary-10);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.user-avatar::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.5);
  pointer-events: none;
}

.user-info:hover .user-avatar {
  transform: scale(1.08) rotate(3deg);
  box-shadow: 
    0 5px 15px var(--color-primary-20),
    0 0 0 4px var(--color-primary-10);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.user-info:hover .user-avatar img {
  transform: scale(1.1);
}

.user-avatar svg {
  width: 22px;
  height: 22px;
  color: white;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.15));
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-primary);
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  letter-spacing: 0.3px;
  transition: color var(--transition-normal);
}

.user-info:hover .user-name {
  color: var(--color-primary);
}

.logout-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: linear-gradient(135deg, var(--color-primary-10) 0%, var(--color-primary-10) 100%);
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 0;
  position: relative;
  overflow: hidden;
}

.logout-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, var(--color-error) 0%, #ff5252 100%);
  opacity: 0;
  transition: opacity var(--transition-normal);
}

.logout-btn svg {
  width: 16px;
  height: 16px;
  color: var(--color-primary);
  transition: all var(--transition-normal);
  position: relative;
  z-index: 1;
}

.logout-btn:hover {
  transform: scale(1.12) rotate(-5deg);
  box-shadow: 0 4px 12px rgba(199, 91, 91, 0.35);
}

.logout-btn:hover::before {
  opacity: 1;
}

.logout-btn:hover svg {
  color: white;
  transform: translateX(2px);
}

.logout-btn:active {
  transform: scale(1.05) rotate(-5deg);
}

.btn {
  display: inline-block;
  padding: 8px 20px;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  transition: all var(--transition-normal);
}

.btn-primary {
  background: var(--gradient-primary);
  color: white;
  box-shadow: 0 2px 8px var(--color-primary-20);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px var(--color-primary-20);
}

.btn-secondary {
  background-color: transparent;
  color: var(--color-primary-light);
  border: 1px solid var(--color-primary-light);
}

.btn-secondary:hover {
  background-color: var(--color-primary);
  color: white;
  border-color: var(--color-primary);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px var(--color-primary-20);
}

/* 关于我们弹窗样式 */
.about-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  backdrop-filter: blur(5px);
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.about-modal {
  background: rgba(61, 79, 47, 0.95);
  border-radius: 20px;
  width: 50%;
  max-width: 600px;
  max-height: 80vh;
  position: relative;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideIn 0.3s ease;
  overflow: hidden;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.about-modal-close {
  position: absolute;
  top: 15px;
  right: 20px;
  background: none;
  border: none;
  font-size: 32px;
  color: #D4CFC7;
  cursor: pointer;
  transition: all var(--transition-normal);
  z-index: 10;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.about-modal-close:hover {
  color: var(--color-primary);
  background: var(--color-primary-10);
  transform: rotate(90deg);
}

.about-modal-content {
  padding: 40px;
  overflow-y: auto;
  max-height: 80vh;
}

.about-modal-content h2 {
  font-family: var(--font-display);
  font-size: 32px;
  color: #F5F0E8;
  text-align: center;
  margin-bottom: 30px;
  position: relative;
  padding-bottom: 15px;
}

.about-modal-content h2::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: var(--gradient-primary);
  border-radius: 2px;
}

.about-section {
  margin-bottom: 25px;
}

.about-section:last-child {
  margin-bottom: 0;
}

.about-section h3 {
  font-size: 18px;
  color: #F5F0E8;
  margin-bottom: 10px;
  font-weight: 600;
}

.about-section p {
  font-size: 15px;
  color: #D4CFC7;
  line-height: 1.8;
}

.about-section ul {
  list-style: none;
  padding: 0;
}

.about-section li {
  font-size: 15px;
  color: #D4CFC7;
  line-height: 1.8;
  padding-left: 20px;
  position: relative;
  margin-bottom: 8px;
}

.about-section li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 10px;
  width: 8px;
  height: 8px;
  background: var(--gradient-primary);
  border-radius: 50%;
}

.contact-info {
  color: #F5F0E8 !important;
  font-weight: 500;
}

@media (max-width: 768px) {
  .navbar .container {
    flex-direction: row;
    gap: 10px;
    padding: 10px 20px;
  }

  .navbar-toggle {
    display: flex;
  }

  .navbar-menu {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background-color: #f8f6f3;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    padding: 20px;
    display: none;
    z-index: 999;
  }

  .navbar-menu.active {
    display: block;
  }

  .navbar-menu ul {
    flex-direction: column;
    gap: 15px;
  }

  .navbar-menu li {
    margin: 0;
  }

  .navbar-actions {
    display: none;
  }

  .about-modal {
    width: 90%;
    max-height: 85vh;
  }

  .about-modal-content {
    padding: 30px 20px;
  }

  .about-modal-content h2 {
    font-size: 24px;
  }
}
</style>
