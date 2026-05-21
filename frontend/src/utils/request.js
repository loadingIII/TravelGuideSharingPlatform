import axios from 'axios'
import { getCookie } from './cookie.js'

let onAuthErrorCallback = null

export function setOnAuthError(fn) {
  onAuthErrorCallback = fn
}

export function triggerAuthError() {
  if (onAuthErrorCallback) {
    onAuthErrorCallback()
  }
}

const request = axios.create({
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

request.interceptors.request.use(
  (config) => {
    const token = getCookie('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    const data = response.data
    if (data && (data.code === 'UNAUTHORIZED' || data.code === 'NOT_LOGIN')) {
      if (onAuthErrorCallback) {
        onAuthErrorCallback()
      }
    }
    return data
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      if (onAuthErrorCallback) {
        onAuthErrorCallback()
      }
    }
    return Promise.reject(error)
  }
)

export default request
