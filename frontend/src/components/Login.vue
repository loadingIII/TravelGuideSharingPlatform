<template>
  <div class="login-page">
    <!-- 返回首页按钮 -->
    <button class="back-btn" @click="goToHome">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
        <polyline points="9 22 9 12 15 12 15 22"/>
      </svg>
      <span>返回首页</span>
    </button>
    <div class="login-container">
      <div class="login-card">
        <div class="header">
          <div class="logo">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="1.5"/>
              <path d="M4 20c0-3.314 3.582-6 8-6s8 2.686 8 6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
          </div>
          <h1>欢迎回来</h1>
          <p>请登录您的账户</p>
        </div>

        <form id="loginForm" @submit.prevent="handleSubmit">
          <div class="form-group">
            <label class="form-label" for="phone">手机号码</label>
            <div class="input-wrapper">
              <input
                type="text"
                id="phone"
                v-model="phone"
                class="form-input"
                :class="{ error: phoneError }"
                placeholder="请输入手机号码或root"
                required
                @input="phoneError = false"
              >
              <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
              </svg>
            </div>
            <p class="error-message" v-if="phoneError">请输入有效的手机号码</p>
          </div>

          <div class="form-group">
            <label class="form-label" for="password">登录密码</label>
            <div class="input-wrapper">
              <input
                :type="showPassword ? 'text' : 'password'"
                id="password"
                v-model="password"
                class="form-input"
                :class="{ error: passwordError }"
                placeholder="请输入密码"
                required
                @input="passwordError = false"
              >
              <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
              </svg>
              <button type="button" class="password-toggle" @click="togglePassword" aria-label="切换密码显示">
                <svg v-if="!showPassword" class="eye-open" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                  <circle cx="12" cy="12" r="3"/>
                </svg>
                <svg v-else class="eye-closed" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                  <line x1="1" y1="1" x2="23" y2="23"/>
                </svg>
              </button>
            </div>
            <p class="error-message" v-if="passwordError">请输入密码</p>
          </div>

          <div class="form-options">
            <label class="remember-me">
              <input type="checkbox" v-model="remember">
              <span class="checkbox-custom">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
              </span>
              <span>记住我</span>
            </label>
            <a href="#" class="forgot-password">忘记密码?</a>
          </div>

          <button type="submit" class="login-btn" :disabled="isLoading">
            {{ isLoading ? '登录中...' : '登 录' }}
          </button>
        </form>

        <div class="divider">
          <span>其他方式</span>
        </div>

        <div class="social-login">
          <button type="button" class="social-btn" aria-label="微信登录">
            <svg viewBox="0 0 24 24">
              <path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c.276 0 .543-.027.811-.05-.857-2.578.157-4.972 1.932-6.446 1.703-1.415 3.882-1.98 5.853-1.838-.576-3.583-4.196-6.348-8.596-6.348zM5.785 5.991c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178A1.17 1.17 0 0 1 4.623 7.17c0-.651.52-1.18 1.162-1.18zm5.813 0c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178 1.17 1.17 0 0 1-1.162-1.178c0-.651.52-1.18 1.162-1.18zm5.34 2.867c-1.797-.052-3.746.512-5.28 1.786-1.72 1.428-2.687 3.72-1.78 6.22.942 2.453 3.666 4.229 6.884 4.229.826 0 1.622-.12 2.361-.336a.722.722 0 0 1 .598.082l1.584.926a.272.272 0 0 0 .14.047c.134 0 .24-.111.24-.247 0-.06-.023-.12-.038-.177l-.327-1.233a.582.582 0 0 1-.023-.156.49.49 0 0 1 .201-.398C23.024 18.48 24 16.82 24 14.98c0-3.21-2.931-5.837-6.656-6.088V8.89c-.135-.01-.27-.027-.407-.032zm-2.53 3.274c.535 0 .969.44.969.982a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.982.97-.982zm4.844 0c.535 0 .969.44.969.982a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.982.969-.982z"/>
            </svg>
          </button>
          <button type="button" class="social-btn" aria-label="QQ登录">
            <svg viewBox="0 0 24 24">
              <path d="M12.003 2c-2.265 0-6.29 1.364-6.29 7.325v1.195S3.55 14.96 3.55 17.474c0 .665.17 1.025.281 1.025.114 0 .902-.484 1.748-2.072 0 0-.18 2.197 1.904 3.967 0 0-1.77.495-1.77 1.182 0 .686 2.24 1.18 4.49 1.18 2.249 0 4.489-.494 4.489-1.18 0-.687-1.77-1.182-1.77-1.182 2.085-1.77 1.904-3.967 1.904-3.967.846 1.588 1.634 2.072 1.748 2.072.111 0 .281-.36.281-1.025 0-2.514-2.166-6.954-2.166-6.954V9.325C14.29 3.364 10.268 2 12.003 2z"/>
            </svg>
          </button>
          <button type="button" class="social-btn" aria-label="邮箱登录">
            <svg viewBox="0 0 24 24">
              <path d="M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/>
            </svg>
          </button>
        </div>

        <p class="register-hint">
          还没有账户?<a href="#" @click.prevent="goToRegister">立即注册</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { setCookie } from '../utils/cookie'
import request from '../utils/request'

const emit = defineEmits(['switch-page', 'login-success'])

const phone = ref('')
const password = ref('')
const remember = ref(false)
const showPassword = ref(false)
const phoneError = ref(false)
const passwordError = ref(false)
const isLoading = ref(false)

const togglePassword = () => {
  showPassword.value = !showPassword.value
}

const handleSubmit = async () => {
  phoneError.value = false
  passwordError.value = false
  let isValid = true

  if (phone.value !== 'root' && !/^1[3-9]\d{9}$/.test(phone.value)) {
    phoneError.value = true
    isValid = false
  }

  if (password.value.length === 0) {
    passwordError.value = true
    isValid = false
  }

  if (!isValid) return

  isLoading.value = true

  try {
    const data = await request.post('/api/auth/login', {
      phone: phone.value,
      password: password.value
    })

    const isSuccess = data.code === 'OK' || data.code === 200

    if (isSuccess) {
      if (data.data?.accessToken) {
        setCookie('token', data.data.accessToken, 7)
      }

      const user = data.data?.user
      const nickname = user?.nickname || user?.username || phone.value
      let avatar = user?.avatarUrl ? user.avatarUrl.trim() : null

      if (avatar && avatar.includes('public/')) {
        avatar = avatar.substring(avatar.indexOf('public/') + 7)
        if (avatar.startsWith('img') && !avatar.startsWith('img/')) {
          avatar = avatar.replace('img', 'img/')
        }
        avatar = '/' + avatar
      }

      const userInfo = {
        username: nickname,
        avatar: avatar,
        phone: phone.value
      }
      setCookie('userInfo', JSON.stringify(userInfo), 7)

      emit('login-success', userInfo)
      emit('switch-page', 'home')
    } else {
      alert(data.message || '登录失败，请检查账号密码')
    }
  } catch (error) {
    alert(error.response?.data?.message || '网络错误，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

const goToRegister = () => {
  emit('switch-page', 'register')
}

const goToHome = () => {
  emit('switch-page', 'home')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: linear-gradient(135deg, #E8F2E0 0%, #F5FAF0 50%, #E8F2E0 100%);
  position: relative;
  overflow: hidden;
}

.login-page::before {
  content: '';
  position: fixed;
  top: -20%;
  right: -10%;
  width: 60%;
  height: 80%;
  background: radial-gradient(ellipse at 30% 50%, rgba(245, 240, 232, 0.1) 0%, transparent 60%);
  opacity: 0.8;
  z-index: 0;
  filter: blur(60px);
}

.login-page::after {
  content: '';
  position: fixed;
  bottom: -20%;
  left: -10%;
  width: 50%;
  height: 70%;
  background: radial-gradient(ellipse at 70% 30%, rgba(107, 142, 78, 0.15) 0%, transparent 55%);
  opacity: 0.6;
  z-index: 0;
  filter: blur(50px);
}

.login-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
  animation: fadeInUp 0.8s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-card {
  background: rgba(255, 255, 255, 0.85);
  border-radius: 24px;
  padding: 48px 40px;
  box-shadow:
    0 4px 20px rgba(45, 58, 30, 0.08),
    0 8px 40px rgba(45, 58, 30, 0.06),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(200, 221, 184, 0.5);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg,
    transparent 0%,
    var(--color-primary) 20%,
    var(--color-primary-light) 50%,
    var(--color-primary) 80%,
    transparent 100%);
  opacity: 0.8;
}

.header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;
  z-index: 1;
}

.logo {
  width: 64px;
  height: 64px;
  margin: 0 auto 20px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  backdrop-filter: blur(8px);
  transition: all 0.3s ease;
}

.logo:hover {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.4);
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(255, 255, 255, 0.1);
}

.logo svg {
  width: 32px;
  height: 32px;
  color: var(--color-primary);
}

.header h1 {
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
  letter-spacing: 3px;
  margin-bottom: 10px;
}

.header p {
  font-size: 15px;
  color: var(--color-text-secondary);
  font-weight: 400;
  letter-spacing: 1px;
}

.form-group {
  margin-bottom: 24px;
  animation: fadeIn 0.6s ease-out backwards;
  position: relative;
  z-index: 1;
}

.form-group:nth-child(1) { animation-delay: 0.1s; }
.form-group:nth-child(2) { animation-delay: 0.2s; }

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.form-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text-primary);
  margin-bottom: 10px;
  letter-spacing: 1px;
}

.input-wrapper {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 18px;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: var(--color-text-muted);
  transition: all 0.3s ease;
  pointer-events: none;
}

.form-input {
  width: 100%;
  padding: 15px 16px 15px 50px;
  font-family: var(--font-body);
  font-size: 15px;
  font-weight: 400;
  color: var(--color-text-primary);
  background: rgba(255, 255, 255, 0.08);
  border: 1.5px solid rgba(255, 255, 255, 0.15);
  border-radius: 14px;
  outline: none;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.05);
}

.form-input::placeholder {
  color: var(--color-text-muted);
  font-weight: 400;
}

.form-input:hover {
  border-color: rgba(255, 255, 255, 0.25);
  background: rgba(255, 255, 255, 0.12);
}

.form-input:focus {
  border-color: var(--color-primary);
  background: rgba(255, 255, 255, 0.15);
  box-shadow:
    0 0 0 4px rgba(245, 240, 232, 0.15),
    inset 0 1px 2px rgba(0, 0, 0, 0.05);
}

.form-input:focus + .input-icon {
  color: var(--color-primary);
  transform: translateY(-50%) scale(1.05);
}

.input-wrapper:hover .input-icon {
  color: var(--color-primary);
}

.password-toggle {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: var(--color-text-muted);
  cursor: pointer;
  padding: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  border-radius: 8px;
}

.password-toggle:hover {
  color: var(--color-primary);
  background: rgba(245, 240, 232, 0.1);
}

.password-toggle svg {
  width: 18px;
  height: 18px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  animation: fadeIn 0.6s ease-out 0.3s backwards;
  position: relative;
  z-index: 1;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.remember-me input[type="checkbox"] {
  display: none;
}

.checkbox-custom {
  width: 18px;
  height: 18px;
  border: 1.5px solid rgba(255, 255, 255, 0.25);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.08);
}

.remember-me input:checked + .checkbox-custom {
  background: var(--gradient-primary);
  border-color: var(--color-primary);
  box-shadow: 0 2px 8px rgba(245, 240, 232, 0.3);
}

.checkbox-custom svg {
  width: 11px;
  height: 11px;
  stroke: white;
  stroke-width: 3;
  opacity: 0;
  transform: scale(0);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.remember-me input:checked + .checkbox-custom svg {
  opacity: 1;
  transform: scale(1);
}

.remember-me span:last-child {
  font-size: 13px;
  color: var(--color-text-secondary);
  font-weight: 400;
}

.forgot-password {
  font-size: 13px;
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
}

.forgot-password::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 1.5px;
  background: linear-gradient(90deg, var(--color-primary), var(--color-primary-light));
  transition: width 0.3s ease;
}

.forgot-password:hover {
  color: var(--color-primary-light);
}

.forgot-password:hover::after {
  width: 100%;
}

.login-btn {
  width: 100%;
  padding: 17px;
  font-family: var(--font-body);
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 3px;
  color: #FFFFFF;
  background: var(--color-primary);
  border: 1px solid var(--color-primary);
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  animation: fadeIn 0.6s ease-out 0.4s backwards;
}

.login-btn:hover {
  transform: translateY(-2px);
  background: var(--color-primary-dark);
  border-color: var(--color-primary-dark);
  box-shadow: 0 4px 12px rgba(91, 140, 62, 0.3);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.divider {
  display: flex;
  align-items: center;
  margin: 36px 0;
  animation: fadeIn 0.6s ease-out 0.5s backwards;
  position: relative;
  z-index: 1;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent);
}

.divider span {
  padding: 0 18px;
  font-size: 12px;
  color: var(--color-text-muted);
  font-weight: 400;
  letter-spacing: 2px;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 18px;
  animation: fadeIn 0.6s ease-out 0.6s backwards;
  position: relative;
  z-index: 1;
}

.social-btn {
  width: 50px;
  height: 50px;
  border: 1.5px solid rgba(255, 255, 255, 0.15);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.social-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: rgba(245, 240, 232, 0.1);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.social-btn:hover {
  border-color: var(--color-primary);
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(245, 240, 232, 0.2);
}

.social-btn:hover::before {
  opacity: 1;
}

.social-btn svg {
  width: 22px;
  height: 22px;
  fill: var(--color-text-muted);
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}

.social-btn:hover svg {
  fill: var(--color-primary);
  transform: scale(1.1);
}

.register-hint {
  text-align: center;
  margin-top: 36px;
  font-size: 14px;
  color: var(--color-text-secondary);
  animation: fadeIn 0.6s ease-out 0.7s backwards;
  position: relative;
  z-index: 1;
}

.register-hint a {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
  margin-left: 4px;
  transition: all 0.3s ease;
  position: relative;
}

.register-hint a::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 1px;
  background: var(--color-primary);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.register-hint a:hover {
  color: var(--color-primary-light);
}

.register-hint a:hover::after {
  transform: scaleX(1);
}

.error-message {
  font-size: 12px;
  color: var(--color-error);
  margin-top: 8px;
  padding-left: 2px;
}

.form-input.error {
  border-color: var(--color-error);
  background: rgba(224, 112, 112, 0.1);
  animation: shake 0.4s ease;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-4px); }
  75% { transform: translateX(4px); }
}

/* 返回首页按钮 */
.back-btn {
  position: fixed;
  top: 24px;
  left: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50px;
  color: var(--color-primary-light);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  z-index: 100;
}

.back-btn svg {
  width: 18px;
  height: 18px;
  transition: transform 0.3s ease;
}

.back-btn:hover {
  background: rgba(245, 240, 232, 0.15);
  border-color: var(--color-primary);
  transform: translateX(-3px);
  box-shadow: 0 4px 16px rgba(245, 240, 232, 0.2);
}

.back-btn:hover svg {
  transform: translateX(-2px);
}

@media (max-width: 480px) {
  .login-card {
    padding: 36px 26px;
    border-radius: 20px;
  }

  .header h1 {
    font-size: 24px;
  }

  .form-input {
    padding: 13px 14px 13px 46px;
  }

  .form-options {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .login-btn {
    letter-spacing: 2px;
  }

  .back-btn {
    top: 16px;
    left: 16px;
    padding: 8px 14px;
    font-size: 13px;
  }

  .back-btn span {
    display: none;
  }

  .back-btn svg {
    width: 20px;
    height: 20px;
  }
}
</style>
