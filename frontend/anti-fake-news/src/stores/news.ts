import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
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

export interface CreateNewsPayload {
  topic: string;
  shortDetail: string;
  fullDetail: string;
  image: string;
  reporter: string;
  dateTime?: string;
}

// 2. สร้าง Pinia Store ในรูปแบบ Setup Store
export const useNewsStore = defineStore('news', () => {
  const allNews = ref<News[]>([]);

  // Getters: ใช้ computed()
  const getNewsById = computed(() => (id: number): News | undefined => {
    const newsItem = allNews.value.find((news) => news.id === id);
    if (!newsItem) return undefined;

    let calculatedStatus: 'fake' | 'not fake' | 'equal';
    if (newsItem.voteSummary.real > newsItem.voteSummary.fake) {
      calculatedStatus = 'not fake';
    } else if (newsItem.voteSummary.real < newsItem.voteSummary.fake) {
      calculatedStatus = 'fake';
    } else {
      calculatedStatus = 'equal';
    }

    return { ...newsItem, status: calculatedStatus };
  });

  // แก้ไข Getter เพื่อให้สามารถ Filter ข่าวจากทั้งหมดได้
  const getNewsWithStatus = computed(() => (statusFilter: 'all' | 'fake' | 'not fake' | 'equal'): News[] => {
    const allNewsWithStatus = allNews.value.map((news) => {
      let calculatedStatus: 'fake' | 'not fake' | 'equal';
      if (news.voteSummary.real > news.voteSummary.fake) {
        calculatedStatus = 'not fake';
      } else if (news.voteSummary.real < news.voteSummary.fake) {
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
  });

  // Actions: สร้างฟังก์ชันปกติ
  async function fetchNews() {
    try {
      const response = await apiClient.getNews();
      allNews.value = response.data;
    } catch (error) {
      console.error('Error fetching news:', error);
      allNews.value = [];
    }
  }

  const createNews = async (newNews: CreateNewsPayload) => {
    try {
      const response = await apiClient.createNews(newNews);
      const createdNews: News = response.data;
      allNews.value = [createdNews, ...allNews.value];
      return createdNews;
    } catch (error) {
      console.error('Error creating news:', error);
      throw error;
    }
  };

  function addCommentToNews(
    newsId: number,
    user: string,
    text: string,
    image: string | null,
    vote: 'real' | 'fake',
  ) {
    const newsIndex = allNews.value.findIndex((news) => news.id === newsId);
    if (newsIndex === -1) {
      console.error('News item not found!');
      return;
    }

    const newsItem = allNews.value[newsIndex];

    const newCommentId =
      newsItem.comments.length > 0 ? Math.max(...newsItem.comments.map((c) => c.id)) + 1 : 1;

    const newComment: Comment = {
      id: newCommentId,
      user,
      text,
      image,
      time: new Date().toISOString(),
      vote,
    };

    newsItem.comments = [...newsItem.comments, newComment];

    if (vote === 'real') {
      newsItem.voteSummary.real++;
    } else {
      newsItem.voteSummary.fake++;
    }

    newsItem.totalVotes = newsItem.voteSummary.real + newsItem.voteSummary.fake;

    allNews.value = [
      ...allNews.value.slice(0, newsIndex),
      newsItem,
      ...allNews.value.slice(newsIndex + 1),
    ];
  }

  // 3. return ทุกอย่างที่ต้องการให้ Component เรียกใช้ได้
  return {
    allNews,
    getNewsById,
    getNewsWithStatus,
    fetchNews,
    createNews,
    addCommentToNews,
  };
});
