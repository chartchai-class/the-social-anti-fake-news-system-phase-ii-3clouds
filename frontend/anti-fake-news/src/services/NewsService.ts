
// src/service/NewsService.ts

import axios from 'axios';
import type { InternalAxiosRequestConfig } from 'axios';
import { useAuthStore } from '../stores/auth';


const apiClient = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_URL,
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
});

const normalizeToken = (value: string | null) => {
  if (!value) return null;
  const withoutSpaces = value.trim();
  if (!withoutSpaces) return null;
  // Handle tokens accidentally stored with surrounding quotes
  if (withoutSpaces.startsWith('"') && withoutSpaces.endsWith('"')) {
    return withoutSpaces.slice(1, -1);
  }
  if (withoutSpaces.startsWith("'") && withoutSpaces.endsWith("'")) {
    return withoutSpaces.slice(1, -1);
  }
  return withoutSpaces;
};

const getStorage = (): Storage | null => {
  try {
    if (typeof globalThis === 'undefined') {
      return null;
    }
    const maybeStorage = (globalThis as typeof globalThis & { localStorage?: Storage }).localStorage;
    return maybeStorage ?? null;
  } catch {
    return null;
  }
};

const getStoredToken = () => {
  const storage = getStorage();
  if (!storage) return null;
  const raw = storage.getItem('access_token');
  return normalizeToken(raw);
};

apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  let token: string | null = null;
  try {
    const authStore = useAuthStore();
    token = normalizeToken(authStore?.token ?? null);
  } catch {
    token = getStoredToken();
  }
  if (!token) {
    token = getStoredToken();
  }
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// กำหนดฟังก์ชันสำหรับเรียก API ข่าวสาร
export default {
  /**
   * ดึงข้อมูลข่าวสารทั้งหมดจาก backend
   */
  getNews() {
    return apiClient.get('/api/news');
  },

  getRemovedNews() {
    return apiClient.get('/api/news/removed');
  },

  getNewsById(id: number) {
    return apiClient.get(`/api/news/${id}`);
  },

  createNews(payload: {
    topic: string;
    shortDetail: string;
    fullDetail: string;
    image: string;
    reporter: string;
    dateTime?: string;
  }) {
    return apiClient.post('/api/news', payload);
  },
  /**
   * ดึง comments ของข่าวตาม newsId (รองรับ pagination)
   */
  getCommentsByNewsId(newsId: number, page: number = 0, size: number = 100) {
    return apiClient.get(`/api/comments/news/${newsId}`, {
      params: { page, size }
    });
  },

  /**
   * สร้าง comment/vote ใหม่
   */
  createComment(data: {
    username: string;
    text: string;
    image: string | null;
    vote: 'fake' | 'real';
    newsId: number;
  }) {
    return apiClient.post('/api/comments', data);
  },

  /**
   * ดึงสรุปคะแนนโหวตของข่าว
   */
  getVoteSummary(newsId: number) {
    return apiClient.get(`/api/comments/news/${newsId}/summary`);
  },

  /**
   * ลบ comment ตาม ID
   */
  deleteComment(id: number) {
    return apiClient.delete(`/api/comments/${id}`);
  }

};
