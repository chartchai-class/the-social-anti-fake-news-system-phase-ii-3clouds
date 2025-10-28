import { defineStore } from 'pinia'
import apiClient from '../services/NewsService'

export interface Comment {
  id: number
  username: string
  text: string
  image: string | null
  time: string
  vote: 'real' | 'fake'
}

export interface News {
  id: number
  topic: string
  shortDetail: string
  fullDetail: string
  image: string
  reporter: string
  dateTime: string
  voteSummary: {
    real: number
    fake: number
  }
  totalVotes: number
  comments: Comment[]
  status?: 'fake' | 'not fake' | 'equal' | 'removed'
}

export interface VoteSummary {
  real: number
  fake: number
}

export type Vote = 'real' | 'fake'

interface NewsState {
  allNews: News[]
  unsavedNews: News[]
  removedNews: News[]
  currentNews: News | null
  loading: boolean
  error: string | null
}

type CommentResponse = {
  id?: number
  username?: string
  user?: string
  text?: string
  image?: string | null
  time?: string
  vote?: string
}

export interface CreateNewsPayload {
  topic: string
  shortDetail: string
  fullDetail: string
  image: string
  reporter: string
  dateTime?: string
}

export const useNewsStore = defineStore('news', {
  state: (): NewsState => ({
    allNews: [],
    unsavedNews: [],
    removedNews: [],
    currentNews: null,
    loading: false,
    error: null,
  }),

  getters: {
    getCombinedNews: (state): News[] => {
      return [...state.allNews, ...state.unsavedNews]
    },

    getNewsById: (state) => {
      return (id: number): News | undefined => {
        const combinedNews = [...state.allNews, ...state.unsavedNews]
        const newsItem = combinedNews.find((news) => news.id === id)
        if (!newsItem) return undefined

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

    getNewsWithStatus: (state) => {
      return (statusFilter: 'all' | 'fake' | 'not fake' | 'equal'): News[] => {
        // กรองเฉพาะข่าวที่ไม่ได้ถูกลบ และไม่อยู่ใน removedNews
        const removedIds = new Set(state.removedNews.map(n => n.id));
        const combinedNews = [...state.allNews, ...state.unsavedNews].filter(
          (n) => n.status !== 'removed' && !removedIds.has(n.id)
        )

        const allNewsWithStatus = combinedNews.map((news) => {
          const realVotes = news.voteSummary?.real || 0
          const fakeVotes = news.voteSummary?.fake || 0

          let calculatedStatus: 'fake' | 'not fake' | 'equal'
          if (realVotes > fakeVotes) calculatedStatus = 'not fake'
          else if (realVotes < fakeVotes) calculatedStatus = 'fake'
          else calculatedStatus = 'equal'

          return { ...news, status: calculatedStatus }
        })

        if (statusFilter === 'all') return allNewsWithStatus
        return allNewsWithStatus.filter((news) => news.status === statusFilter)
      }
    },
  },

  actions: {
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
          if (news.status === 'removed') {
            removed.push(news)
          } else {
            activeNews.push(news)
          }
        })
        
        // เก็บเฉพาะข่าวที่ไม่ถูกลบใน allNews
        this.allNews = activeNews
        
        // อัปเดต removedNews สำหรับ Admin (ไม่ซ้ำ)
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

    async createNews(payload: CreateNewsPayload) {
      this.error = null
      try {
        const response = await apiClient.createNews(payload)
        const createdNews = response?.data

        if (createdNews) {
          const existingIndex = this.allNews.findIndex((newsItem) => newsItem.id === createdNews.id)

          if (existingIndex !== -1) {
            this.allNews[existingIndex] = {
              ...this.allNews[existingIndex],
              ...createdNews,
            }
          } else {
            this.allNews.unshift({
              comments: [],
              totalVotes: 0,
              voteSummary: { real: 0, fake: 0 },
              ...createdNews,
            })
          }
        } else {
          await this.fetchNews()
        }

        return createdNews
      } catch (error) {
        console.error('Error creating news:', error)
        this.error = 'Failed to create news'
        throw error
      }
    },

    async fetchNewsById(id: number) {
      this.loading = true
      this.error = null
      this.currentNews = null

      try {
        // ดึงข้อมูลข่าว
        const newsResponse = await apiClient.getNewsById(id)

        // ดึง comments แยกต่างหาก
        const commentsResponse = await apiClient.getCommentsByNewsId(id)

        // ตรวจสอบว่ามีข้อมูล comments หรือไม่
        const commentsData = commentsResponse.data?.content || commentsResponse.data || []

        // แปลงข้อมูล comment และกำหนดค่าเริ่มต้นหากข้อมูลบางส่วนหายไป
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

        // อัปเดตใน allNews ด้วยถ้ามีอยู่แล้ว
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

    addUnsavedNews(newNews: Omit<News, 'id'>) {
      const tempId = Math.random() * -1000000 - Date.now()
      this.unsavedNews.push({ ...newNews, id: tempId })
    },

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

      // ตรวจสอบให้แน่ใจว่า comments array มีอยู่
      if (!newsItem.comments) {
        newsItem.comments = []
      }

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

    async removeNews(newsId: number) {
      this.error = null
      try {
        await apiClient.removeNews(newsId)

        // หา news ก่อนลบ
        const removedItem =
          this.allNews.find((n) => n.id === newsId) || this.unsavedNews.find((n) => n.id === newsId)

        // ลบออกจาก allNews และ unsavedNews ก่อน
        this.allNews = this.allNews.filter((n) => n.id !== newsId)
        this.unsavedNews = this.unsavedNews.filter((n) => n.id !== newsId)

        // เพิ่มเข้า removedNews
        if (removedItem && !this.removedNews.some((n) => n.id === newsId)) {
          this.removedNews.push({ ...removedItem, status: 'removed' })
        }

        // อัปเดต currentNews
        if (this.currentNews?.id === newsId) {
          this.currentNews.status = 'removed'
        }
      } catch (error: any) {
        console.error('Error removing news:', error)
        this.error = error.response?.data?.message || 'Failed to remove news'
        throw error
      }
    },
    
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
