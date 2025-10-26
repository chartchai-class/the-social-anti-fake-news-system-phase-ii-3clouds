import apiClient from './AxiosClient';
import type { UserAuthDTO } from '@/types';

export default {
  /**
   * (Admin) ดึงข้อมูล User ทั้งหมดจาก Backend
   */
  getAllUsers(): Promise<{ data: UserAuthDTO[] }> {
    // ⭐️ ใช้ apiClient ที่ import มา (Interceptor จะใส่ Token ให้เอง)
    return apiClient.get('/api/v1/users');
  },

  /**
   * (Admin) ส่งคำขอ Promote User ไปยัง Backend
   */
  promoteUserToMember(userId: number): Promise<{ data: UserAuthDTO }> {
    // ⭐️ ใช้ apiClient ที่ import มา (Interceptor จะใส่ Token ให้เอง)
    return apiClient.put(`/api/v1/users/${userId}/promote`);
  },
};