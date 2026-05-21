<template>
  <Teleport to="body">
    <div class="confirm-overlay" v-if="visible" @click.self="cancel">
      <div class="confirm-modal">
        <div class="confirm-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 9v4M12 17h.01"/>
            <path d="M10.29 3.86l-8.1 14c-.6 1.04.15 2.34 1.21 2.34h16.2c1.06 0 1.81-1.3 1.21-2.34l-8.1-14c-.6-1.04-2.12-1.04-2.72 0z"/>
          </svg>
        </div>
        <h3>{{ title }}</h3>
        <p>{{ message }}</p>
        <div class="confirm-actions">
          <button class="btn-cancel" @click="cancel">取消</button>
          <button class="btn-confirm" @click="confirm">确认删除</button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref } from 'vue'

const visible = ref(false)
const title = ref('')
const message = ref('')
let resolveCallback = null

const show = (msg) => {
  title.value = '确认删除'
  message.value = msg
  visible.value = true
  return new Promise((resolve) => {
    resolveCallback = resolve
  })
}

const confirm = () => {
  visible.value = false
  resolveCallback?.(true)
}

const cancel = () => {
  visible.value = false
  resolveCallback?.(false)
}

defineExpose({ show })
</script>

<style scoped>
.confirm-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  backdrop-filter: blur(4px);
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.confirm-modal {
  background: #FFFFFF;
  border: 1px solid rgba(200, 221, 184, 0.5);
  border-radius: 20px;
  padding: 32px;
  width: 90%;
  max-width: 380px;
  text-align: center;
  box-shadow: 0 24px 80px rgba(0,0,0,0.4);
  animation: modalSlide 0.25s ease;
}

@keyframes modalSlide {
  from { opacity: 0; transform: translateY(20px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.confirm-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  background: rgba(239,68,68,0.15);
  border-radius: 50%;
  color: #ef4444;
}

.confirm-icon svg {
  width: 24px;
  height: 24px;
}

.confirm-modal h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 8px;
  font-family: var(--font-display);
}

.confirm-modal p {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: 24px;
  line-height: 1.5;
}

.confirm-actions {
  display: flex;
  gap: 12px;
}

.btn-cancel, .btn-confirm {
  flex: 1;
  padding: 12px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.btn-cancel {
  background: rgba(45, 58, 30, 0.06);
  color: var(--color-text-secondary);
}

.btn-cancel:hover {
  background: rgba(45, 58, 30, 0.1);
  color: var(--color-text-primary);
}

.btn-confirm {
  background: rgba(239,68,68,0.2);
  color: #ef4444;
}

.btn-confirm:hover {
  background: rgba(239,68,68,0.3);
}
</style>
