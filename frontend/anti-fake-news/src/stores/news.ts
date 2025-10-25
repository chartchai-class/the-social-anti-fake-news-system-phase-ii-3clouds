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

// 2. สร้าง Pinia Store ในรูปแบบ Setup Store
export const useNewsStore = defineStore('news', () => {
  // State: ใช้ ref()
  const allNews = ref<News[]>([]);
  // เพิ่ม State ใหม่สำหรับข่าวที่ยังไม่ถูกบันทึก
  const unsavedNews = ref<News[]>([]); 

  // Getters: ใช้ computed()
  // รวมข่าวทั้งหมดเข้าด้วยกันก่อนทำการ Filter
  const getCombinedNews = computed(() => {
    return [...allNews.value, ...unsavedNews.value];
  });

  // แก้ไข Getter เพื่อให้สามารถหาข่าวจากทั้ง API และข่าวใหม่ได้
  const getNewsById = computed(() => (id: number): News | undefined => {
    const newsItem = getCombinedNews.value.find((news) => news.id === id);
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
    const allNewsWithStatus = getCombinedNews.value.map((news) => {
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

  // เพิ่ม Action ใหม่สำหรับเพิ่มข่าวที่ยังไม่ถูกบันทึก
  function addUnsavedNews(newNews: Omit<News, 'id'>) {
    // สร้าง ID ชั่วคราวที่เป็นค่าติดลบเพื่อไม่ให้ซ้ำกับ ID จาก API
    const tempId = (Math.random() * -1000000) - Date.now();
    unsavedNews.value.push({ ...newNews, id: tempId });
  }

  function addCommentToNews(
    newsId: number,
    user: string,
    text: string,
    image: string | null,
    vote: 'real' | 'fake',
  ) {
    // ค้นหาข่าวจากทั้งสองแหล่งข้อมูล
    const newsItem = getNewsById.value(newsId);
    if (!newsItem) {
      console.error('News item not found!');
      return;
    }

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

    newsItem.comments.push(newComment);

    if (vote === 'real') {
      newsItem.voteSummary.real++;
    } else {
      newsItem.voteSummary.fake++;
    }
  }

  // 3. return ทุกอย่างที่ต้องการให้ Component เรียกใช้ได้
  // เพิ่ม unsavedNews และ addUnsavedNews ใน return ด้วย
  return { allNews, unsavedNews, getCombinedNews, getNewsById, getNewsWithStatus, fetchNews, addCommentToNews, addUnsavedNews };
});