import axios from 'axios';

import { readSessionUser } from '../utils/session';

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 添加请求拦截器，在每个API请求中添加用户信息
api.interceptors.request.use(
  config => {
    const user = readSessionUser();
    if (user && user.id) {
      // 在请求头中添加用户ID
      config.headers['X-User-Id'] = user.id;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

export const heritageApi = {
  getList: () => api.get('/heritage'),
  getDetail: (id) => api.get(`/heritage/${id}`),
  getByCategory: (categoryId) => api.get(`/heritage/category/${categoryId}`),
  getByLevel: (level) => api.get(`/heritage/level/${level}`),
  create: (payload) => api.post('/heritage', payload),
  update: (id, payload) => api.put(`/heritage/${id}`, payload),
  delete: (id) => api.delete(`/heritage/${id}`),
  uploadVideo: (id, file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post(`/heritage/${id}/video`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  }
};

export const masterApi = {
  getList: () => api.get('/master'),
  getDetail: (id) => api.get(`/master/${id}`),
  create: (payload) => api.post('/master', payload),
  update: (id, payload) => api.put(`/master/${id}`, payload),
  delete: (id) => api.delete(`/master/${id}`)
};

export const categoryApi = {
  getList: () => api.get('/category')
};

export const postApi = {
  getList: () => api.get('/community/posts'),
  getDetail: (id) => api.get(`/community/posts/${id}`),
  create: (payload) => api.post('/community/posts', payload),
  uploadImage: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/community/posts/upload-image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },
  like: (id) => api.post(`/community/posts/${id}/like`),
  unlike: (id) => api.delete(`/community/posts/${id}/like`),
  delete: (id) => api.delete(`/community/posts/${id}`)
};

export const commentApi = {
  getByPost: (postId) => api.get(`/community/comments/post/${postId}`),
  getReplies: (commentId) => api.get(`/community/comments/${commentId}/replies`),
  create: (payload) => api.post('/community/comments', payload),
  like: (id) => api.post(`/community/comments/${id}/like`),
  unlike: (id) => api.delete(`/community/comments/${id}/like`)
};

export const activityApi = {
  getList: () => api.get('/activity'),
  getDetail: (id) => api.get(`/activity/${id}`),
  create: (payload) => api.post('/activity', payload),
  update: (id, payload) => api.put(`/activity/${id}`, payload),
  delete: (id) => api.delete(`/activity/${id}`),
  register: (id, payload) => api.post(`/activity/${id}/register`, payload),
  getUserRegistrations: () => api.get('/activity/user/registrations'),
  cancelRegistration: (registrationId) => api.delete(`/activity/registration/${registrationId}`)
};

export const searchApi = {
  query: (keyword, type = '') => api.get('/search', { params: { keyword, type } })
};

export const aiApi = {
  chat: (message) => api.post('/ai/chat', { message })
};

export const userApi = {
  register: (payload) => api.post('/user/register', payload),
  loginWithPassword: (payload) => api.post('/user/login/password', payload),
  loginWithCode: (payload) => api.post('/user/login/code', payload),
  sendCode: (payload) => api.post('/user/send-code', payload),
  logout: () => api.post('/user/logout'),
  check: () => api.get('/user/check'),
  getProfile: () => api.get('/user/profile'),
  updateProfile: (payload) => api.put('/user/profile', payload),
  uploadAvatar: (formData) => api.post('/user/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
};

export function excerpt(text, max = 96) {
  if (!text) return '';
  if (text.length <= max) return text;
  return `${text.slice(0, max)}...`;
}

export function formatDateTime(value) {
  if (!value) return '待定';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
}

export function formatDate(value) {
  if (!value) return '待定';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
}

export function placeholderImage(label, accent = '#a64b2a', bg = '#f4e6d6') {
  const safeLabel = encodeURIComponent(label || '非遗文化');
  const safeAccent = encodeURIComponent(accent);
  const safeBg = encodeURIComponent(bg);
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 600 360"><rect width="600" height="360" fill="${safeBg}"/><circle cx="480" cy="84" r="56" fill="#fff4ea"/><path d="M70 278C142 200 232 172 328 188c84 14 138 42 202 112H70Z" fill="${safeAccent}" opacity="0.88"/><path d="M82 250c70-56 142-84 224-80 78 4 142 29 210 84" fill="none" stroke="#ffffff" stroke-opacity="0.7" stroke-width="10" stroke-linecap="round"/><text x="56" y="86" font-size="34" fill="${safeAccent}" font-family="Microsoft YaHei,SimHei,sans-serif">${safeLabel}</text><text x="56" y="132" font-size="18" fill="#7d5a4e" font-family="Microsoft YaHei,SimHei,sans-serif">传统技艺 · 传承人 · 活动体验</text></svg>`;
  return `data:image/svg+xml;charset=UTF-8,${svg}`;
}

function isGeneratingImageUrl(url) {
  if (!url) return false;
  const lowered = String(url).toLowerCase();
  return lowered.includes('trae-api-cn.mchost.guru/api/ide/v1/text_to_image');
}

export function normalizeImageUrl(value) {
  if (!value) return '';
  const raw = String(value).split(',')[0].trim();
  if (!raw || raw.startsWith('data:')) return raw;
  if (/^https?:\/\//i.test(raw)) return raw;
  if (raw.startsWith('//')) return `${window.location.protocol}${raw}`;
  if (raw.startsWith('/')) return `${window.location.origin}${raw}`;
  return raw;
}

export function isPendingGeneratedImage(value) {
  const normalized = normalizeImageUrl(value);
  return !!normalized && isGeneratingImageUrl(normalized);
}

export function resolveImage(value, label, accent, bg) {
  const normalized = normalizeImageUrl(value);
  if (normalized && !isGeneratingImageUrl(normalized)) {
    return normalized;
  }
  return placeholderImage(label, accent, bg);
}

export function activityStatus(activity) {
  const now = Date.now();
  const start = activity?.startTime ? new Date(activity.startTime).getTime() : null;
  const end = activity?.endTime ? new Date(activity.endTime).getTime() : null;

  if (start && now < start) return '即将开始';
  if (end && now > end) return '已结束';
  return '报名进行中';
}

export default api;
