<template>
  <div class="register-page">
    <!-- 返回首页按钮 -->
    <button class="back-btn" @click="goToHome">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
        <polyline points="9 22 9 12 15 12 15 22"/>
      </svg>
      <span>返回首页</span>
    </button>

    <!-- 成功提示弹框 -->
    <Teleport to="body">
      <Transition name="modal">
        <div v-if="showSuccessModal" class="modal-overlay" @click="closeSuccessModal">
          <div class="modal-content" @click.stop>
            <div class="modal-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"/>
                <path d="M8 12l2.5 2.5L16 9"/>
              </svg>
            </div>
            <h3 class="modal-title">注册成功</h3>
            <p class="modal-message">恭喜您，账号已创建成功！<br>现在可以前往登录页面。</p>
            <button class="modal-btn" @click="closeSuccessModal">
              去登录
            </button>
          </div>
        </div>
      </Transition>
    </Teleport>

    <div class="register-container">
      <div class="register-card">
        <div class="header">
          <div class="logo">
            <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z"/>
            </svg>
          </div>
          <h1>注册新账户</h1>
          <p>创建你的专属旅行账号</p>
        </div>
        <form id="registerForm" @submit.prevent="handleSubmit" novalidate>
          <div class="form-group">
            <label class="form-label" for="username">用户名</label>
            <div class="input-wrapper">
              <input
                type="text"
                id="username"
                v-model="username"
                class="form-input"
                :class="{ error: usernameError }"
                placeholder="请输入用户名"
                maxlength="20"
                required
                @input="usernameError = false"
              >
              <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                <circle cx="8.5" cy="7" r="4"/>
                <path d="M20 8v6"/>
                <path d="M23 11h-6"/>
              </svg>
            </div>
            <p class="error-message" v-if="usernameError">用户名必须为3到20个字符，不能包含空格</p>
          </div>
          <div class="form-group">
            <label class="form-label" for="phone">手机号</label>
            <div class="input-wrapper">
              <input
                type="tel"
                id="phone"
                v-model="phone"
                class="form-input"
                :class="{ error: phoneError }"
                placeholder="请输入手机号"
                maxlength="11"
                required
                @input="phoneError = false"
              >
              <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
              </svg>
            </div>
            <p class="error-message" v-if="phoneError">请输入正确的11位手机号</p>
          </div>
          <div class="form-group">
            <label class="form-label" for="email">邮箱</label>
            <div class="input-wrapper">
              <input
                type="email"
                id="email"
                v-model="email"
                class="form-input"
                :class="{ error: emailError }"
                placeholder="请输入邮箱地址"
                maxlength="100"
                required
                @input="emailError = false"
              >
              <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                <polyline points="22,6 12,13 2,6"/>
              </svg>
            </div>
            <p class="error-message" v-if="emailError">请输入正确的邮箱地址</p>
          </div>
          <div class="form-group">
            <label class="form-label" for="password">密码</label>
            <div class="input-wrapper">
              <input
                type="password"
                id="password"
                v-model="password"
                class="form-input"
                :class="{ error: passwordError }"
                placeholder="请输入密码"
                minlength="6"
                required
                @input="passwordError = false"
              >
              <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
              </svg>
            </div>
            <p class="error-message" v-if="passwordError">密码长度至少6位</p>
          </div>
          <div class="form-group">
            <label class="form-label" for="confirmPassword">确认密码</label>
            <div class="input-wrapper">
              <input
                type="password"
                id="confirmPassword"
                v-model="confirmPassword"
                class="form-input"
                :class="{ error: confirmPasswordError }"
                placeholder="请再次输入密码"
                minlength="6"
                required
                @input="confirmPasswordError = false"
              >
              <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M2 12l5 5L20 4"/>
              </svg>
            </div>
            <p class="error-message" v-if="confirmPasswordError">两次输入密码不一致</p>
          </div>
          <button type="submit" class="submit-btn" :disabled="isLoading">
            {{ isLoading ? '注册中...' : '注 册' }}
          </button>
          <p class="info-text">已有账号？<a href="#" @click.prevent="goToLogin">立即登录</a></p>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['switch-page'])

const username = ref('')
const phone = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const usernameError = ref(false)
const phoneError = ref(false)
const emailError = ref(false)
const passwordError = ref(false)
const confirmPasswordError = ref(false)
const isLoading = ref(false)
const showSuccessModal = ref(false)

const validatePhone = (phone) => {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(phone)
}

const validateEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

const handleSubmit = async () => {
  usernameError.value = false
  phoneError.value = false
  emailError.value = false
  passwordError.value = false
  confirmPasswordError.value = false
  let valid = true

  const usernameVal = username.value.trim()
  const phoneVal = phone.value.trim()
  const emailVal = email.value.trim()
  const passwordVal = password.value
  const confirmPasswordVal = confirmPassword.value

  if (usernameVal.length < 3 || usernameVal.length > 20 || /\s/.test(usernameVal)) {
    usernameError.value = true
    valid = false
  }

  if (!validatePhone(phoneVal)) {
    phoneError.value = true
    valid = false
  }

  if (!validateEmail(emailVal)) {
    emailError.value = true
    valid = false
  }

  if (passwordVal.length < 6) {
    passwordError.value = true
    valid = false
  }

  if (confirmPasswordVal !== passwordVal || confirmPasswordVal.length < 6) {
    confirmPasswordError.value = true
    valid = false
  }

  if (!valid) {
    return
  }

  isLoading.value = true

  try {
    const response = await fetch('/api/auth/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: usernameVal,
        phone: phoneVal,
        email: emailVal,
        password: passwordVal
      })
    })

    const data = await response.json()

    // 支持多种成功响应格式
    const isSuccess = (response.ok && data.code === 200) ||
                      (response.ok && data.message?.toLowerCase() === 'success') ||
                      (response.ok && typeof data === 'string' && data.toLowerCase() === 'success')

    if (isSuccess) {
      showSuccessModal.value = true
    } else {
      // 注册失败
      alert(data.message || '注册失败，请检查输入信息')
    }
  } catch (error) {
    console.error('注册请求失败:', error)
    alert('网络错误，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

const goToLogin = () => {
  emit('switch-page', 'login')
}

const goToHome = () => {
  emit('switch-page', 'home')
}

const closeSuccessModal = () => {
  showSuccessModal.value = false
  emit('switch-page', 'login')
}
</script>

<style scoped>
/* 官网色系：
   - 主背景：#faf8f5 (米白色)
   - 强调色：#f79545 (橙色系)
   - 强调色浅：#ffc494 (浅橙色)
   - 文字主色：#333, #0b0a0a
   - 区块背景：#f4eed7 (暖黄色)
   - 按钮悬停：#e88535
*/

.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: linear-gradient(135deg, #f4eed7 0%, #faf8f5 50%, #f4eed7 100%);
  position: relative;
  overflow: hidden;
}

.register-page::before {
  content: '';
  position: fixed;
  top: -20%;
  right: -10%;
  width: 60%;
  height: 80%;
  background: radial-gradient(ellipse at 30% 50%, rgba(247, 149, 69, 0.08) 0%, transparent 60%);
  opacity: 0.8;
  z-index: 0;
  filter: blur(60px);
}

.register-page::after {
  content: '';
  position: fixed;
  bottom: -20%;
  left: -10%;
  width: 50%;
  height: 70%;
  background: radial-gradient(ellipse at 70% 30%, rgba(255, 196, 148, 0.15) 0%, transparent 55%);
  opacity: 0.6;
  z-index: 0;
  filter: blur(50px);
}

.register-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 440px;
  animation: fadeInUp 0.8s ease-out;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

.register-card {
  background: rgba(255, 255, 255, 0.85);
  border-radius: 24px;
  padding: 48px 40px;
  box-shadow:
    0 4px 20px rgba(247, 149, 69, 0.1),
    0 8px 40px rgba(0, 0, 0, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 196, 148, 0.3);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(20px);
}

.register-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, transparent 0%, #f79545 20%, #ffc494 50%, #f79545 80%, transparent 100%);
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
  background-color: #ffc494;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(255, 196, 148, 0.3);
  position: relative;
}

@keyframes logoFloat {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  25% { transform: translateY(-3px) rotate(1deg); }
  75% { transform: translateY(-2px) rotate(-1deg); }
}

.logo svg {
  width: 32px;
  height: 32px;
  fill: white;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1));
}

.header h1 {
  font-family: 'Noto Serif SC', serif;
  font-size: 28px;
  font-weight: 700;
  color: #0b0a0a;
  letter-spacing: 3px;
  margin-bottom: 10px;
}

.header p {
  font-size: 15px;
  color: #666;
  font-weight: 400;
  letter-spacing: 1px;
}

.form-group {
  margin-bottom: 20px;
  animation: fadeIn 0.6s ease-out backwards;
  position: relative;
  z-index: 1;
}

.form-group:nth-child(1) { animation-delay: 0.1s; }
.form-group:nth-child(2) { animation-delay: 0.15s; }
.form-group:nth-child(3) { animation-delay: 0.2s; }
.form-group:nth-child(4) { animation-delay: 0.25s; }
.form-group:nth-child(5) { animation-delay: 0.3s; }

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.form-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #333;
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
  color: #999;
  pointer-events: none;
  transition: all 0.3s ease;
}

.form-input {
  width: 100%;
  padding: 15px 16px 15px 50px;
  font-family: 'Noto Sans SC', sans-serif;
  font-size: 15px;
  font-weight: 400;
  color: #333;
  background: rgba(250, 248, 245, 0.9);
  border: 1.5px solid rgba(247, 149, 69, 0.2);
  border-radius: 14px;
  outline: none;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.02);
}

.form-input::placeholder {
  color: #aaa;
  font-weight: 400;
}

.form-input:hover {
  border-color: rgba(247, 149, 69, 0.4);
  background: rgba(255, 255, 255, 0.95);
}

.form-input:focus {
  border-color: #f79545;
  background: #fff;
  box-shadow: 0 0 0 4px rgba(247, 149, 69, 0.12), inset 0 1px 2px rgba(0, 0, 0, 0.02);
}

.form-input:focus + .input-icon {
  color: #f79545;
  transform: translateY(-50%) scale(1.05);
}

.input-wrapper:hover .input-icon {
  color: #f79545;
}

.form-input.error {
  border-color: #e74c3c;
  background: rgba(231, 76, 60, 0.05);
  animation: shake 0.4s ease;
}

.error-message {
  font-size: 12px;
  color: #e74c3c;
  margin-top: 8px;
  padding-left: 2px;
}

.submit-btn {
  width: 100%;
  padding: 17px;
  font-family: 'Noto Sans SC', sans-serif;
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 3px;
  color: #7b7474;
  background-color: #ffc494;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  animation: fadeIn 0.6s ease-out 0.4s backwards;
  box-shadow: 0 2px 8px rgba(255, 196, 148, 0.3);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.submit-btn:hover {
  background-color: #ffc494;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 196, 148, 0.4);
}

.info-text {
  font-size: 13px;
  color: #666;
  margin-top: 18px;
  text-align: center;
}

.info-text a {
  color: #f79545;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
}

.info-text a::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 1px;
  background: #f79545;
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.info-text a:hover {
  color: #e88535;
}

.info-text a:hover::after {
  transform: scaleX(1);
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
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(247, 149, 69, 0.3);
  border-radius: 50px;
  color: #f79545;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 12px rgba(247, 149, 69, 0.15);
  z-index: 100;
}

.back-btn svg {
  width: 18px;
  height: 18px;
  transition: transform 0.3s ease;
}

.back-btn:hover {
  background: rgba(247, 149, 69, 0.1);
  border-color: #f79545;
  transform: translateX(-3px);
  box-shadow: 0 4px 16px rgba(247, 149, 69, 0.25);
}

.back-btn:hover svg {
  transform: translateX(-2px);
}

/* 成功弹框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: linear-gradient(135deg, #fff 0%, #faf8f5 100%);
  border-radius: 24px;
  padding: 48px 40px;
  text-align: center;
  max-width: 360px;
  width: 100%;
  box-shadow:
    0 20px 60px rgba(0, 0, 0, 0.2),
    0 0 0 1px rgba(247, 149, 69, 0.1);
  border: 1px solid rgba(255, 196, 148, 0.3);
  animation: modalPop 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes modalPop {
  0% {
    opacity: 0;
    transform: scale(0.8) translateY(20px);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.modal-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto 24px;
  background: linear-gradient(135deg, #ffc494 0%, #f79545 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(247, 149, 69, 0.3);
  animation: iconPulse 0.6s ease 0.2s both;
}

@keyframes iconPulse {
  0% {
    transform: scale(0);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

.modal-icon svg {
  width: 36px;
  height: 36px;
  color: white;
  stroke-width: 3;
}

.modal-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 24px;
  font-weight: 700;
  color: #0b0a0a;
  margin-bottom: 12px;
  letter-spacing: 2px;
}

.modal-message {
  font-size: 15px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 32px;
  font-weight: 400;
}

.modal-btn {
  width: 100%;
  padding: 16px 32px;
  font-family: 'Noto Sans SC', sans-serif;
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 2px;
  color: white;
  background: linear-gradient(135deg, #ffc494 0%, #f79545 100%);
  border: none;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(247, 149, 69, 0.3);
}

.modal-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(247, 149, 69, 0.4);
}

.modal-btn:active {
  transform: translateY(0);
}

/* 弹框过渡动画 */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-content,
.modal-leave-to .modal-content {
  transform: scale(0.9) translateY(20px);
  opacity: 0;
}

@media (max-width: 480px) {
  .register-card {
    padding: 36px 26px;
    border-radius: 20px;
  }

  .header h1 {
    font-size: 24px;
  }

  .form-input {
    padding: 13px 14px 13px 46px;
  }

  .submit-btn {
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

  .modal-content {
    padding: 36px 28px;
    border-radius: 20px;
  }

  .modal-icon {
    width: 60px;
    height: 60px;
    margin-bottom: 20px;
  }

  .modal-icon svg {
    width: 30px;
    height: 30px;
  }

  .modal-title {
    font-size: 20px;
  }

  .modal-message {
    font-size: 14px;
    margin-bottom: 24px;
  }

  .modal-btn {
    padding: 14px 24px;
    font-size: 14px;
  }
}
</style>
