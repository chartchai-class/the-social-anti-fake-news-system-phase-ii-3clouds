// src/service/NewsService.ts

import axios from 'axios';

// IMPORTANT: Replace 'YOUR_PROJECT_ID' with the actual ID from MockAPI.io.
const YOUR_PROJECT_ID = '689ed0883fed484cf8780f9e';

// สร้าง Axios instance
const apiClient = axios.create({
  baseURL: `https://${YOUR_PROJECT_ID}.mockapi.io/api/v1`, // ใช้ URL ของ MockAPI.io
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
  timeout: 30000, // 30 วินาที
});

// กำหนดฟังก์ชันสำหรับเรียก API ข่าวสาร
export default {
  /**
   * ดึงข้อมูลข่าวสารทั้งหมดจาก MockAPI.io
   * MockAPI.io จะสร้าง endpoint 'news'
   */
  getNews() {
    return apiClient.get('/news'); // เรียก endpoint /news บน MockAPI.io
  },

};