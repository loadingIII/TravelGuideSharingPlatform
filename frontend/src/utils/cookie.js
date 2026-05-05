// Cookie操作工具函数

/**
 * 设置Cookie
 * @param {string} name Cookie名称
 * @param {string} value Cookie值
 * @param {number} days 过期时间（天），默认7天
 * @param {string} path Cookie路径，默认根路径
 * @param {string} domain Cookie域名，默认当前域名
 * @param {boolean} secure 是否仅HTTPS传输，默认false
 * @param {boolean} sameSite 是否禁止跨域发送，默认Lax
 */
export function setCookie(name, value, days = 7, path = '/', domain = '', secure = false, sameSite = 'Lax') {
  let cookie = `${encodeURIComponent(name)}=${encodeURIComponent(value)}`
  
  if (days) {
    const date = new Date()
    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000))
    cookie += `; expires=${date.toUTCString()}`
  }
  
  if (path) cookie += `; path=${path}`
  if (domain) cookie += `; domain=${domain}`
  if (secure) cookie += '; secure'
  if (sameSite) cookie += `; SameSite=${sameSite}`
  
  document.cookie = cookie
}

/**
 * 获取Cookie
 * @param {string} name Cookie名称
 * @returns {string|null} Cookie值
 */
export function getCookie(name) {
  const nameEQ = `${encodeURIComponent(name)}=`
  const cookies = document.cookie.split(';')
  
  for (let i = 0; i < cookies.length; i++) {
    let cookie = cookies[i].trim()
    if (cookie.indexOf(nameEQ) === 0) {
      return decodeURIComponent(cookie.substring(nameEQ.length))
    }
  }
  
  return null
}

/**
 * 删除Cookie
 * @param {string} name Cookie名称
 * @param {string} path Cookie路径
 * @param {string} domain Cookie域名
 */
export function removeCookie(name, path = '/', domain = '') {
  // 设置过期时间为过去的时间来删除Cookie
  setCookie(name, '', -1, path, domain)
}
