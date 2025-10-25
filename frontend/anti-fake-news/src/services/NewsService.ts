// src/service/NewsService.ts

import axios from 'axios';


const apiClient = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_URL,
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
});

// กำหนดฟังก์ชันสำหรับเรียก API ข่าวสาร
export default {
  /**
   * ดึงข้อมูลข่าวสารทั้งหมดจาก backend
   */
  getNews() {
    return apiClient.get('/api/news');
  },

  getNewsById(id: number) {
    return apiClient.get(`/api/news/${id}`);
  },

};
