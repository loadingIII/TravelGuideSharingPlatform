<template>
  <Teleport to="body">
    <div class="toast-container">
      <TransitionGroup name="toast">
        <div 
          v-for="toast in toasts" 
          :key="toast.id"
          class="toast"
          :class="toast.type"
        >
          <div class="toast-icon">
            <svg v-if="toast.type === 'success'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <path d="M20 6L9 17l-5-5"/>
            </svg>
            <svg v-else-if="toast.type === 'error'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <path d="M18 6L6 18M6 6l12 12"/>
            </svg>
            <svg v-else-if="toast.type === 'warning'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <path d="M12 9v4M12 17h.01"/>
              <path d="M10.29 3.86l-8.1 14c-.6 1.04.15 2.34 1.21 2.34h16.2c1.06 0 1.81-1.3 1.21-2.34l-8.1-14c-.6-1.04-2.12-1.04-2.72 0z"/>
            </svg>
            <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <circle cx="12" cy="12" r="10"/>
              <path d="M12 16v-4M12 8h.01"/>
            </svg>
          </div>
          <span class="toast-text">{{ toast.message }}</span>
          <button class="toast-close" @click="removeToast(toast.id)">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 6L6 18M6 6l12 12"/>
            </svg>
          </button>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script setup>
import { ref } from 'vue'

const toasts = ref([])
let nextId = 0

const addToast = (message, type = 'success', duration = 3000) => {
  const id = ++nextId
  toasts.value.push({ id, message, type })
  setTimeout(() => removeToast(id), duration)
}

const removeToast = (id) => {
  const idx = toasts.value.findIndex(t => t.id === id)
  if (idx !== -1) toasts.value.splice(idx, 1)
}

defineExpose({ addToast })
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 10000;
  display: flex;
  flex-direction: column;
  gap: 10px;
  pointer-events: none;
}

.toast {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 20px;
  background: #FFFFFF;
  border: 1px solid rgba(200, 221, 184, 0.5);
  border-radius: 14px;
  box-shadow: 0 12px 40px rgba(45, 58, 30, 0.12);
  pointer-events: auto;
  min-width: 280px;
  max-width: 420px;
  backdrop-filter: blur(12px);
}

.toast.success {
  border-color: rgba(52,199,89,0.3);
}
.toast.success .toast-icon { color: #34c759; }

.toast.error {
  border-color: rgba(239,68,68,0.3);
}
.toast.error .toast-icon { color: #ef4444; }

.toast.warning {
  border-color: rgba(255,204,0,0.3);
}
.toast.warning .toast-icon { color: #ffcc00; }

.toast.info {
  border-color: rgba(90,200,250,0.3);
}
.toast.info .toast-icon { color: #5ac8fa; }

.toast-icon {
  width: 24px;
  height: 24px;
  flex-shrink: 0;
}

.toast-icon svg {
  width: 100%;
  height: 100%;
}

.toast-text {
  flex: 1;
  font-size: 14px;
  color: var(--color-text-primary);
  line-height: 1.4;
}

.toast-close {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(45, 58, 30, 0.06);
  border: none;
  border-radius: 50%;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.toast-close:hover {
  background: rgba(45, 58, 30, 0.1);
  color: var(--color-text-primary);
}

.toast-close svg {
  width: 12px;
  height: 12px;
}

.toast-enter-active {
  animation: toastSlideIn 0.3s ease;
}

.toast-leave-active {
  animation: toastSlideOut 0.25s ease forwards;
}

@keyframes toastSlideIn {
  from { opacity: 0; transform: translateX(100px) scale(0.9); }
  to { opacity: 1; transform: translateX(0) scale(1); }
}

@keyframes toastSlideOut {
  from { opacity: 1; transform: translateX(0) scale(1); }
  to { opacity: 0; transform: translateX(100px) scale(0.9); }
}
</style>
