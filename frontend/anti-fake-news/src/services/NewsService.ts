import apiClient from './AxiosClient';

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

};
