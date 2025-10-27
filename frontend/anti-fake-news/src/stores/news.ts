import { defineStore } from 'pinia';
import apiClient from '../services/NewsService';

export interface Comment {
  id: number;
  user: string;
  text: string;
  image: string | null;
  time: string;
  vote: 'real' | 'fake';
}

export interface News {
  id: number;
  topic: string;
  shortDetail: string;
  fullDetail: string;
  image: string;
  reporter: string;
  dateTime: string;
  voteSummary: {
    real: number;
    fake: number;
  };
  totalVotes: number;
  comments: Comment[];
  status?: 'fake' | 'not fake' | 'equal';
}

export interface VoteSummary {
  real: number;
  fake: number;
}

export type Vote = 'real' | 'fake';

interface NewsState {
  allNews: News[];
  unsavedNews: News[];
  currentNews: News | null;
  loading: boolean;
  error: string | null;
}

export const useNewsStore = defineStore('news', {
  state: (): NewsState => ({
    allNews: [],
    unsavedNews: [],
    currentNews: null,
    loading: false,
    error: null,
  }),

  getters: {
    getCombinedNews: (state): News[] => {
      return [...state.allNews, ...state.unsavedNews];
    },

    getNewsById: (state) => {
      return (id: number): News | undefined => {
        const combinedNews = [...state.allNews, ...state.unsavedNews];
        const newsItem = combinedNews.find((news) => news.id === id);
        if (!newsItem) return undefined;

        const realVotes = newsItem.voteSummary?.real || 0;
        const fakeVotes = newsItem.voteSummary?.fake || 0;

        let calculatedStatus: 'fake' | 'not fake' | 'equal';
        if (realVotes > fakeVotes) {
          calculatedStatus = 'not fake';
        } else if (realVotes < fakeVotes) {
          calculatedStatus = 'fake';
        } else {
          calculatedStatus = 'equal';
        }

        return {
          ...newsItem,
          status: calculatedStatus,
          comments: newsItem.comments || [],
        };
      };
    },

    getNewsWithStatus: (state) => {
      return (statusFilter: 'all' | 'fake' | 'not fake' | 'equal'): News[] => {
        const combinedNews = [...state.allNews, ...state.unsavedNews];
        const allNewsWithStatus = combinedNews.map((news) => {
          const realVotes = news.voteSummary?.real || 0;
          const fakeVotes = news.voteSummary?.fake || 0;

          let calculatedStatus: 'fake' | 'not fake' | 'equal';
          if (realVotes > fakeVotes) {
            calculatedStatus = 'not fake';
          } else if (realVotes < fakeVotes) {
            calculatedStatus = 'fake';
          } else {
            calculatedStatus = 'equal';
          }
          return { ...news, status: calculatedStatus };
        });

        if (statusFilter === 'all') {
          return allNewsWithStatus;
        }
        return allNewsWithStatus.filter((news) => news.status === statusFilter);
      };
    },
  },

  actions: {
    async fetchNews() {
      this.loading = true;
      this.error = null;
      try {
        const response = await apiClient.getNews();
        this.allNews = response.data;
      } catch (error) {
        console.error('Error fetching news:', error);
        this.error = 'Failed to fetch news';
        this.allNews = [];
      } finally {
        this.loading = false;
      }
    },

    async fetchNewsById(id: number) {
      this.loading = true;
      this.error = null;
      this.currentNews = null;

      try {
        // ดึงข้อมูลข่าว
        const newsResponse = await apiClient.getNewsById(id);

        // ดึง comments แยกต่างหาก
        const commentsResponse = await apiClient.getCommentsByNewsId(id);

        // ตรวจสอบว่ามีข้อมูล comments หรือไม่
        const commentsData =
          commentsResponse.data?.content || commentsResponse.data || [];

        // แปลง username จาก Backend เป็น user สำหรับ Frontend
        const comments = Array.isArray(commentsData)
          ? commentsData.map((comment: any) => ({
              id: comment.id || 0,
              user: comment.username || comment.user || 'Anonymous',
              text: comment.text || '',
              image: comment.image || null,
              time: comment.time || new Date().toISOString(),
              vote:
                comment.vote === 'real' || comment.vote === 'fake'
                  ? comment.vote
                  : 'fake',
            }))
          : [];

        // รวมข้อมูล
        this.currentNews = {
          ...newsResponse.data,
          comments,
        };

        // อัปเดตใน allNews ด้วยถ้ามีอยู่แล้ว
        const index = this.allNews.findIndex((n) => n.id === id);
        if (index !== -1) {
          this.allNews[index] = this.currentNews;
        }

        return this.currentNews;
      } catch (err) {
        console.error('Error fetching news by ID:', err);
        this.error = 'Failed to fetch news details';
        this.currentNews = null;
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async submitComment(
      newsId: number,
      data: {
        username: string;
        text: string;
        image: string;
        vote: 'fake' | 'real';
      }
    ) {
      try {
        await apiClient.createComment({
          username: data.username,
          text: data.text,
          image: data.image || null,
          vote: data.vote,
          newsId: newsId,
        });

        // โหลดข้อมูลใหม่หลังส่งสำเร็จ
        await this.fetchNewsById(newsId);

        return { success: true };
      } catch (err) {
        console.error('Error submitting comment:', err);
        return { success: false, error: 'Failed to submit vote' };
      }
    },

    addUnsavedNews(newNews: Omit<News, 'id'>) {
      const tempId = Math.random() * -1000000 - Date.now();
      this.unsavedNews.push({ ...newNews, id: tempId });
    },

    addCommentToNews(
      newsId: number,
      user: string,
      text: string,
      image: string | null,
      vote: 'real' | 'fake'
    ) {
      const newsItem = this.getNewsById(newsId);
      if (!newsItem) {
        console.error('News item not found!');
        return;
      }

      // ตรวจสอบให้แน่ใจว่า comments array มีอยู่
      if (!newsItem.comments) {
        newsItem.comments = [];
      }

      const newCommentId =
        newsItem.comments.length > 0
          ? Math.max(...newsItem.comments.map((c) => c.id)) + 1
          : 1;

      const newComment: Comment = {
        id: newCommentId,
        user: user || 'Anonymous',
        text: text || '',
        image: image || null,
        time: new Date().toISOString(),
        vote: vote,
      };

      newsItem.comments.push(newComment);

      // อัปเดต vote summary
      if (!newsItem.voteSummary) {
        newsItem.voteSummary = { real: 0, fake: 0 };
      }

      if (vote === 'real') {
        newsItem.voteSummary.real++;
      } else {
        newsItem.voteSummary.fake++;
      }
    },
  },
});