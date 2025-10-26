import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserAuthDTO } from '@/types'
import userService from '../services/UserService'
// (Import messageStore หรือ notificationStore ถ้าต้องการใช้แจ้งเตือน)
import { useMessageStore } from './message'

export const useUserStore = defineStore('userManagement', () => {
  const users = ref<UserAuthDTO[]>([])
  const isLoading = ref(false)

  /**
   * (Admin) ดึงข้อมูล User ทั้งหมดจาก Backend
   */
  async function fetchAllUsers() {
    isLoading.value = true
    const messageStore = useMessageStore()
    try {
      const response = await userService.getAllUsers()
      users.value = response.data
    } catch (error) {
      console.error('Failed to fetch users:', error)
      messageStore.updateMessage('Failed to load user list.')
    } finally {
      isLoading.value = false
    }
    setTimeout(() => {
      messageStore.resetMessage()
    }, 3000)
  }

  /**
   * (Admin) ส่งคำขอ Promote User ไปยัง Backend
   * @param userId - ID ของ User ที่จะ Promote
   */
  async function promoteUser(userId: number) {
    const messageStore = useMessageStore()
    try {
      // ยิง API (PUT /api/v1/users/{id}/promote)
      const response = await userService.promoteUserToMember(userId)
      const updatedUser = response.data

      // อัปเดตข้อมูลใน State (users) ทันที
      const index = users.value.findIndex((u) => u.id === userId)
      if (index !== -1) {
        users.value.splice(index, 1, updatedUser)
        messageStore.updateMessage(`User ${updatedUser.username} promoted to MEMBER.`)
      }
    } catch (error) {
      console.error('Failed to promote user:', error)
      messageStore.updateMessage('Failed to promote user.')
      throw error // โยน error ต่อเพื่อให้ Component รู้ว่าไม่สำเร็จ
    }
    setTimeout(() => {
      messageStore.resetMessage()
    }, 3000)
  }

  // --- Return ---
  return {
    users,
    isLoading,
    fetchAllUsers,
    promoteUser,
  }
})
