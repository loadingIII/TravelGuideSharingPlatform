const API = {
  async request(url, options = {}) {
    const config = { headers: { 'Content-Type': 'application/json' }, ...options };
    if (config.body && typeof config.body === 'object') {
      config.body = JSON.stringify(config.body);
    }
    const res = await fetch(url, config);
    if (res.status === 401) {
      window.location.href = '/admin/login.html';
      throw new Error('未登录');
    }
    const data = await res.json();
    if (data.code !== 'OK') {
      throw new Error(data.message || '请求失败');
    }
    return data.data;
  },
  get(url) { return this.request(url); },
  post(url, body) { return this.request(url, { method: 'POST', body }); },
  put(url, body) { return this.request(url, { method: 'PUT', body }); },
  delete(url) { return this.request(url, { method: 'DELETE' }); }
};
