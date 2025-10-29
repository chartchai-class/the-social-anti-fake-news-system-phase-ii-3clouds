import { defineStore } from 'pinia'
import apiClient from '../services/NewsService'

// ===== TypeScript Interfaces (กำหนดโครงสร้างข้อมูล) =====

/**
 * โครงสร้างข้อมูล Comment (ความคิดเห็น/โหวต)
 */
export interface Comment {
  id: number
  username: string
  text: string
  image: string | null
  time: string
  vote: 'real' | 'fake' // โหวตว่าข่าวจริงหรือปลอม
}

/**
 * โครงสร้างข้อมูล News (ข่าว)
 */
export interface News {
  id: number
  topic: string
  shortDetail: string
  fullDetail: string
  image: string
  reporter: string
  dateTime: string
  voteSummary: {
    real: number // จำนวนโหวต "จริง"
    fake: number // จำนวนโหวต "ปลอม"
  }
  totalVotes: number
  comments: Comment[]
  status?: 'fake' | 'not fake' | 'equal' | 'removed' // สถานะข่าว
  removed?: boolean // ถูกลบหรือไม่
}

export interface VoteSummary {
  real: number
  fake: number
}

export type Vote = 'real' | 'fake'

/**
 * State ของ Store (ข้อมูลที่เก็บใน Store)
 */
interface NewsState {
  allNews: News[] // ข่าวปกติทั้งหมด (ไม่รวมที่ถูกลบ)
  unsavedNews: News[] // ข่าวที่ยังไม่ได้บันทึกใน backend
  removedNews: News[] // ข่าวที่ถูกลบ (สำหรับ admin)
  currentNews: News | null // ข่าวที่กำลังดูอยู่
  loading: boolean // กำลังโหลดข้อมูลหรือไม่
  error: string | null // ข้อความ error (ถ้ามี)
}

/**
 * โครงสร้าง Comment จาก Backend (อาจแตกต่างจาก interface Comment)
 */
type CommentResponse = {
  id?: number
  username?: string
  user?: string // บาง API ใช้ชื่อ "user" แทน "username"
  text?: string
  image?: string | null
  time?: string
  vote?: string
}

/**
 * ข้อมูลสำหรับสร้างข่าวใหม่
 */
export interface CreateNewsPayload {
  topic: string
  shortDetail: string
  fullDetail: string
  image: string
  reporter: string
  dateTime?: string
}

// ===== Pinia Store Definition =====

/**
 * News Store - จัดการข้อมูลข่าวทั้งหมด
 */
export const useNewsStore = defineStore('news', {
  // ===== State (ข้อมูลที่เก็บ) =====
  state: (): NewsState => ({
    allNews: [],
    unsavedNews: [],
    removedNews: [],
    currentNews: null,
    loading: false,
    error: null,
  }),

  // ===== Getters (คำนวณข้อมูลจาก state) =====
  getters: {
    /**
     * รวมข่าวปกติกับข่าวที่ยังไม่ได้บันทึก
     */
    getCombinedNews: (state): News[] => {
      return [...state.allNews, ...state.unsavedNews]
    },

    /**
     * หาข่าวตาม ID และคำนวณ status
     */
    getNewsById: (state) => {
      return (id: number): News | undefined => {
        const combinedNews = [...state.allNews, ...state.unsavedNews]
        const newsItem = combinedNews.find((news) => news.id === id)
        if (!newsItem) return undefined

        // คำนวณ status จากจำนวนโหวต
        const realVotes = newsItem.voteSummary?.real || 0
        const fakeVotes = newsItem.voteSummary?.fake || 0

        let calculatedStatus: 'fake' | 'not fake' | 'equal'
        if (realVotes > fakeVotes) {
          calculatedStatus = 'not fake'
        } else if (realVotes < fakeVotes) {
          calculatedStatus = 'fake'
        } else {
          calculatedStatus = 'equal'
        }

        return {
          ...newsItem,
          status: calculatedStatus,
          comments: newsItem.comments || [],
        }
      }
    },

    /**
     * FILTER: กรองข่าวตาม status
     */
    getNewsWithStatus: (state) => {
      return (statusFilter: 'all' | 'fake' | 'not fake' | 'equal'): News[] => {
        // กรองเฉพาะข่าวที่ไม่ถูกลบ
        const removedIds = new Set(state.removedNews.map(n => n.id));
        const combinedNews = [...state.allNews, ...state.unsavedNews].filter(
          (n) => n.status !== 'removed' && !removedIds.has(n.id)
        )

        // คำนวณ status ให้ทุกข่าว
        const allNewsWithStatus = combinedNews.map((news) => {
          const realVotes = news.voteSummary?.real || 0
          const fakeVotes = news.voteSummary?.fake || 0

          let calculatedStatus: 'fake' | 'not fake' | 'equal'
          if (realVotes > fakeVotes) calculatedStatus = 'not fake'
          else if (realVotes < fakeVotes) calculatedStatus = 'fake'
          else calculatedStatus = 'equal'

          return { ...news, status: calculatedStatus }
        })

        // กรองตาม status ที่ต้องการ
        if (statusFilter === 'all') return allNewsWithStatus
        return allNewsWithStatus.filter((news) => news.status === statusFilter)
      }
    },
  },

  // ===== Actions (ฟังก์ชันที่เปลี่ยนแปลง state) =====
  actions: {
    /**
     * ดึงข่าวทั้งหมดจาก Backend
     */
    async fetchNews() {
      this.loading = true
      this.error = null
      try {
        const response = await apiClient.getNews()
        const allData = response.data || []
        
        // แยกข่าวปกติกับข่าวที่ถูกลบ
        const activeNews: News[] = []
        const removed: News[] = []
        
        allData.forEach((news: News) => {
          if (news.status === 'removed' || news.removed) {
            removed.push(news)
          } else {
            activeNews.push(news)
          }
        })
        
        // เก็บข่าวปกติใน allNews
        this.allNews = activeNews
        
        // เก็บข่าวที่ถูกลบใน removedNews (ไม่ให้ซ้ำ)
        removed.forEach((removedItem) => {
          if (!this.removedNews.some(n => n.id === removedItem.id)) {
            this.removedNews.push(removedItem)
          }
        })
      } catch (error) {
        console.error('Error fetching news:', error)
        this.error = 'Failed to fetch news'
        this.allNews = []
      } finally {
        this.loading = false
      }
    },

    /**
     * สร้างข่าวใหม่
     */
    async createNews(payload: CreateNewsPayload) {
      this.error = null
      try {
        const response = await apiClient.createNews(payload)
        const createdNews = response?.data

        if (createdNews) {
          const existingIndex = this.allNews.findIndex((newsItem) => newsItem.id === createdNews.id)

          if (existingIndex !== -1) {
            // ถ้ามีอยู่แล้ว = update
            this.allNews[existingIndex] = {
              ...this.allNews[existingIndex],
              ...createdNews,
            }
          } else {
            // ถ้ายังไม่มี = เพิ่มใหม่ (ไว้ด้านหน้าสุด)
            this.allNews.unshift({
              comments: [],
              totalVotes: 0,
              voteSummary: { real: 0, fake: 0 },
              ...createdNews,
            })
          }
        } else {
          // ถ้าไม่ได้ข้อมูลกลับมา = โหลดข่าวทั้งหมดใหม่
          await this.fetchNews()
        }

        return createdNews
      } catch (error) {
        console.error('Error creating news:', error)
        this.error = 'Failed to create news'
        throw error
      }
    },

    /**
     * ดึงข่าวตาม ID พร้อม comments
     */
    async fetchNewsById(id: number) {
      this.loading = true
      this.error = null
      this.currentNews = null

      try {
        // ดึงข้อมูลข่าว
        const newsResponse = await apiClient.getNewsById(id)

        // ดึง comments แยกต่างหาก
        const commentsResponse = await apiClient.getCommentsByNewsId(id)

        // ประมวลผล comments
        const commentsData = commentsResponse.data?.content || commentsResponse.data || []

        // แปลงข้อมูล comment ให้ตรงกับ interface
        const comments = Array.isArray(commentsData)
          ? commentsData.map((comment: CommentResponse) => ({
              id: comment.id ?? 0,
              username: comment.username ?? comment.user ?? 'Anonymous',
              text: comment.text ?? '',
              image: comment.image ?? null,
              time: comment.time ?? new Date().toISOString(),
              vote: comment.vote === 'real' || comment.vote === 'fake' ? comment.vote : 'fake',
            }))
          : []

        // รวมข้อมูล
        this.currentNews = {
          ...newsResponse.data,
          comments,
        }

        // อัปเดตใน allNews ด้วย (ถ้ามีอยู่แล้ว)
        const index = this.allNews.findIndex((n) => n.id === id)
        if (index !== -1 && this.currentNews) {
          this.allNews[index] = this.currentNews
        }

        return this.currentNews
      } catch (err) {
        console.error('Error fetching news by ID:', err)
        this.error = 'Failed to fetch news details'
        this.currentNews = null
        throw err
      } finally {
        this.loading = false
      }
    },

    /**
     * ส่ง comment/vote
     */
    async submitComment(
      newsId: number,
      data: {
        username: string
        text: string
        image: string
        vote: 'fake' | 'real'
      },
    ) {
      try {
        await apiClient.createComment({
          username: data.username,
          text: data.text,
          image: data.image || null,
          vote: data.vote,
          newsId: newsId,
        })

        // โหลดข้อมูลใหม่หลังส่งสำเร็จ
        await this.fetchNewsById(newsId)

        return { success: true }
      } catch (err) {
        console.error('Error submitting comment:', err)
        return { success: false, error: 'Failed to submit vote' }
      }
    },

    /**
     * เพิ่มข่าวที่ยังไม่ได้บันทึก (Local)
     */
    addUnsavedNews(newNews: Omit<News, 'id'>) {
      // สร้าง temporary ID (ติดลบเพื่อไม่ซ้ำกับ ID จริง)
      const tempId = Math.random() * -1000000 - Date.now()
      this.unsavedNews.push({ ...newNews, id: tempId })
    },

    /**
     * เพิ่ม comment ให้ข่าว (Local - ไม่ผ่าน Backend)
     */
    addCommentToNews(
      newsId: number,
      user: string,
      text: string,
      image: string | null,
      vote: 'real' | 'fake',
    ) {
      const newsItem = this.getNewsById(newsId)
      if (!newsItem) {
        console.error('News item not found!')
        return
      }

      // ตรวจสอบว่ามี comments array หรือไม่
      if (!newsItem.comments) {
        newsItem.comments = []
      }

      // สร้าง comment ID ใหม่
      const newCommentId =
        newsItem.comments.length > 0 ? Math.max(...newsItem.comments.map((c) => c.id)) + 1 : 1

      const newComment: Comment = {
        id: newCommentId,
        username: user || 'Anonymous',
        text: text || '',
        image: image || null,
        time: new Date().toISOString(),
        vote: vote,
      }

      newsItem.comments.push(newComment)

      // อัปเดต vote summary
      if (!newsItem.voteSummary) {
        newsItem.voteSummary = { real: 0, fake: 0 }
      }

      if (vote === 'real') {
        newsItem.voteSummary.real++
      } else {
        newsItem.voteSummary.fake++
      }
    },

    /**
     * ลบข่าว (Soft Delete)
     */
    async removeNews(newsId: number) {
      this.error = null
      try {
        // เรียก API ลบข่าว
        await apiClient.removeNews(newsId)

        // หาข่าวที่จะลบ
        const removedItem =
          this.allNews.find((n) => n.id === newsId) || this.unsavedNews.find((n) => n.id === newsId)

        // ลบออกจาก allNews และ unsavedNews
        this.allNews = this.allNews.filter((n) => n.id !== newsId)
        this.unsavedNews = this.unsavedNews.filter((n) => n.id !== newsId)

        // เพิ่มเข้า removedNews (ไม่ให้ซ้ำ)
        if (removedItem && !this.removedNews.some((n) => n.id === newsId)) {
          this.removedNews.push({ ...removedItem, status: 'removed' })
        }

        // อัปเดต currentNews (ถ้ากำลังดูข่าวที่ลบอยู่)
        if (this.currentNews?.id === newsId) {
          this.currentNews.status = 'removed'
        }
      } catch (error: any) {
        console.error('Error removing news:', error)
        this.error = error.response?.data?.message || 'Failed to remove news'
        throw error
      }
    },
    
    /**
     * ดึงข่าวที่ถูกลบทั้งหมด (สำหรับ Admin)
     */
    async fetchRemovedNews() {
      this.loading = true
      this.error = null
      try {
        const response = await apiClient.getRemovedNews()

        // เก็บใน removedNews แยกจาก allNews
        this.removedNews = response.data.map((news: News) => ({
          ...news,
          status: 'removed' as const,
        }))
      } catch (error) {
        console.error('Error fetching removed news:', error)
        this.error = 'Failed to fetch removed news'
        this.removedNews = []
      } finally {
        this.loading = false
      }
    },
  },
})