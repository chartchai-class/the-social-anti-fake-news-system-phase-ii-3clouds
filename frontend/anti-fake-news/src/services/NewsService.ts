import apiClient from './AxiosClient';

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
