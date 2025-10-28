<template>
  <div class="container mx-auto p-4">

    <div class="flex flex-col md:flex-row justify-between items-center mb-6 space-y-4 md:space-y-0">

      <!-- Logo/Title -->
      <div class="md:w-1/4">
        <h1 class="text-4xl font-normal text-blue-500 font-alfa-slab">3Clouds News</h1>
      </div>

      <!-- Filter Buttons -->
      <div class="flex space-x-2 md:flex-1 md:justify-center">
        <button @click="handleFilterChange('all')" :disabled="isFilterLoading" :class="[
          'px-4 py-2 rounded-lg font-medium border transition-colors duration-200 relative',
          {
            'bg-blue-600 text-white': filterStatus === 'all',
            'bg-white text-gray-800 hover:bg-blue-500 hover:text-white': filterStatus !== 'all',
            'opacity-75 cursor-not-allowed': isFilterLoading
          }
        ]">
          <span :class="{ 'opacity-50': isFilterLoading && pendingFilter === 'all' }">All</span>
          <div v-if="isFilterLoading && pendingFilter === 'all'"
            class="absolute inset-0 flex items-center justify-center">
            <div class="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
          </div>
        </button>

        <button @click="handleFilterChange('not fake')" :disabled="isFilterLoading" :class="[
          'px-4 py-2 rounded-lg font-medium border transition-colors duration-200 relative',
          {
            'bg-green-600 text-white': filterStatus === 'not fake',
            'bg-white text-gray-800 hover:bg-green-500 hover:text-white': filterStatus !== 'not fake',
            'opacity-75 cursor-not-allowed': isFilterLoading
          }
        ]">
          <span :class="{ 'opacity-50': isFilterLoading && pendingFilter === 'not fake' }">Real</span>
          <div v-if="isFilterLoading && pendingFilter === 'not fake'"
            class="absolute inset-0 flex items-center justify-center">
            <div class="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
          </div>
        </button>

        <button @click="handleFilterChange('fake')" :disabled="isFilterLoading" :class="[
          'px-4 py-2 rounded-lg font-medium border transition-colors duration-200 relative',
          {
            'bg-red-600 text-white': filterStatus === 'fake',
            'bg-white text-gray-800 hover:bg-red-500 hover:text-white': filterStatus !== 'fake',
            'opacity-75 cursor-not-allowed': isFilterLoading
          }
        ]">
          <span :class="{ 'opacity-50': isFilterLoading && pendingFilter === 'fake' }">Fake</span>
          <div v-if="isFilterLoading && pendingFilter === 'fake'"
            class="absolute inset-0 flex items-center justify-center">
            <div class="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
          </div>
        </button>

        <button @click="handleFilterChange('equal')" :disabled="isFilterLoading" :class="[
          'px-4 py-2 rounded-lg font-medium border transition-colors duration-200 relative',
          {
            'bg-gray-600 text-white': filterStatus === 'equal',
            'bg-white text-gray-800 hover:bg-gray-500 hover:text-white': filterStatus !== 'equal',
            'opacity-75 cursor-not-allowed': isFilterLoading
          }
        ]">
          <span :class="{ 'opacity-50': isFilterLoading && pendingFilter === 'equal' }">Equal</span>
          <div v-if="isFilterLoading && pendingFilter === 'equal'"
            class="absolute inset-0 flex items-center justify-center">
            <div class="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
          </div>
        </button>

        <button v-if="isAdmin" @click="handleFilterChange('removed')" :disabled="isFilterLoading" :class="[
          'px-4 py-2 rounded-lg font-medium border transition-colors duration-200 relative',
          {
            'bg-rose-600 text-white': filterStatus === 'removed',
            'bg-white text-gray-800 hover:bg-rose-500 hover:text-white': filterStatus !== 'removed',
            'opacity-75 cursor-not-allowed': isFilterLoading
          }
        ]">
          <span :class="{ 'opacity-50': isFilterLoading && pendingFilter === 'removed' }">Removed</span>
          <div v-if="isFilterLoading && pendingFilter === 'removed'"
            class="absolute inset-0 flex items-center justify-center">
            <div class="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
          </div>
        </button>
      </div>

      <!-- News per page -->
      <div class="flex items-center space-x-2 md:w-1/4 md:justify-end">
        <span class="text-gray-600">News per page:</span>
        <select v-model="newsPerPage" :disabled="isFilterLoading || isDataLoading"
          class="p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed">
          <option>6</option>
          <option>12</option>
          <option>24</option>
        </select>
      </div>
    </div>

    <SearchBar v-model="searchQuery" />

    <div v-if="isDataLoading || isFilterLoading" class="text-center text-gray-500 text-xl py-20">
      <div class="inline-flex flex-col items-center">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mb-4"></div>
        <span v-if="isDataLoading">Loading news...</span>
        <span v-else-if="isFilterLoading">Loading news...</span>
      </div>
    </div>

    <div v-else-if="paginatedNews.length" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <NewsCard v-for="news in paginatedNews" :key="news.id" :news="news" :search-query="searchQuery"
        @news-removed="handleNewsRemoved" />
    </div>

    <div v-else class="text-center text-gray-500 text-xl py-10">
      <div class="flex flex-col items-center">
        <svg class="w-16 h-16 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z">
          </path>
        </svg>
        <h3 class="text-lg font-medium mb-1">No news found</h3>
        <p class="text-sm text-gray-400">Try changing the filter or search terms.</p>
      </div>
    </div>

    <Pagination v-if="!isDataLoading && !isFilterLoading && paginatedNews.length"
      :total-items="filteredAndSearchedNews.length" :items-per-page="newsPerPage" :current-page="currentPage"
      @page-changed="onPageChanged" class="mt-8" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useNewsStore } from '../stores/news';
import { useLoadingStore } from '../stores/loading';
import NewsCard from '@/components/NewsCard.vue';
import Pagination from '@/components/BasePagination.vue';
import SearchBar from '@/components/SearchBar.vue';
import type { News } from '../stores/news';
import { useAuthStore } from '../stores/auth';

const route = useRoute();
const router = useRouter();
const loadingStore = useLoadingStore();
const authStore = useAuthStore();
const newsStore = useNewsStore();

const isAdmin = computed(() => authStore.hasRole('ROLE_ADMIN'));

type FilterStatus = 'all' | 'fake' | 'not fake' | 'equal' | 'removed';

const filterStatus = ref<FilterStatus>(
  (route.query.filter as FilterStatus) || 'all'
);

const currentPage = ref<number>(Number(route.query.page) || 1);
const newsPerPage = ref<number>(Number(route.query.perPage) || 6);
const isDataLoading = ref(false);
const isFilterLoading = ref(false);
const pendingFilter = ref<FilterStatus | null>(null);

// ** เพิ่มตัวแปรสำหรับ Search **
const searchQuery = ref<string>((route.query.search as string) || '');

const fetchNewsData = async (filterToFetch: FilterStatus = filterStatus.value) => {
  // ไม่ต้อง fetch ใหม่ถ้า loading อยู่แล้ว
  if (isDataLoading.value || isFilterLoading.value) return;

  try {
    isDataLoading.value = true; // ใช้ isDataLoading สำหรับการโหลดข้อมูลหลัก
    loadingStore.startLoading();

    // ถ้า Filter เป็น 'removed' และเป็น Admin ให้เรียก API ใหม่
    if (filterToFetch === 'removed' && isAdmin.value) {
      await newsStore.fetchRemovedNews();
    } else {
      // ถ้า Filter อื่นๆ หรือไม่ใช่ Admin ให้เรียก API เดิม
      await newsStore.fetchNews();
    }

    // (เอา Simulate delay ออกถ้าไม่ต้องการ)
    // await new Promise(resolve => setTimeout(resolve, 300));

  } catch (error) {
    console.error('Error fetching news:', error);
  } finally {
    isDataLoading.value = false;
    loadingStore.finishLoading();
  }
};

const handleFilterChange = async (newFilter: FilterStatus) => {
  if (filterStatus.value === newFilter || isFilterLoading.value) return;

  if (newFilter === 'removed' && !isAdmin.value) return;

  isFilterLoading.value = true;
  pendingFilter.value = newFilter;

  const wasRemoved = filterStatus.value === 'removed';

  filterStatus.value = newFilter;
  currentPage.value = 1;

  // ✅ fetch ใหม่เสมอเมื่อ:
  // 1. ไปหน้า removed
  // 2. กลับจากหน้า removed (เพื่อให้ได้ข้อมูลล่าสุด)
  if (newFilter === 'removed') {
    await newsStore.fetchRemovedNews();
  } else {
    // กลับจากหน้า removed หรือเปลี่ยน filter อื่นๆ -> fetch ข้อมูลใหม่
    await newsStore.fetchNews();
  }

  await updateURL();

  isFilterLoading.value = false;
  pendingFilter.value = null;
};

const updateURL = async () => {
  await nextTick(() => { });

  // ให้ type ชัดเจน
  const query: Record<string, string> = {};

  if (filterStatus.value !== 'all') {
    query.filter = filterStatus.value;
  }
  if (currentPage.value !== 1) {
    query.page = String(currentPage.value);
  }
  if (newsPerPage.value !== 6) {
    query.perPage = String(newsPerPage.value);
  }
  // ** เพิ่มคำค้นหาลงใน URL **
  if (searchQuery.value) {
    query.search = searchQuery.value;
  }

  // อัปเดต URL โดยไม่ trigger navigation ใหม่
  await router.replace({
    path: route.path,
    query
  });
};

onMounted(() => {
  fetchNewsData();
});

// Watch for URL changes (browser back/forward)
watch(() => route.query, (newQuery) => {
  const newFilter = newQuery.filter as FilterStatus | undefined;
  if (newFilter && newFilter !== filterStatus.value) {
    if (newFilter === 'removed' && !isAdmin.value) {
      console.warn("Attempted to access removed filter via URL without admin rights.");
      filterStatus.value = 'all'; // กลับไป all
      updateURL();
    } else {
      filterStatus.value = newFilter;
      fetchNewsData(newFilter); // Fetch ใหม่ถ้า filter เปลี่ยนจาก URL
    }
  }
  if (newQuery.page && Number(newQuery.page) !== currentPage.value) {
    currentPage.value = Number(newQuery.page);
  }
  if (newQuery.perPage && Number(newQuery.perPage) !== newsPerPage.value) {
    newsPerPage.value = Number(newQuery.perPage);
  }
  // ** ตรวจสอบการเปลี่ยนแปลงของคำค้นหาใน URL **
  if (newQuery.search && newQuery.search !== searchQuery.value) {
    searchQuery.value = newQuery.search as string;
  }
}, { deep: true });

watch(newsPerPage, () => {
  currentPage.value = 1;
  updateURL();
});

// ** Watch คำค้นหา **
watch(searchQuery, () => {
  currentPage.value = 1;
  updateURL();
});

const onPageChanged = (page: number) => {
  currentPage.value = page;
  updateURL();
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const newsListSource = computed<News[]>(() => {
  if (filterStatus.value === 'removed') {
    return newsStore.removedNews || [];
  }

  // Admin / User ทั้งหมด ใช้ getNewsWithStatus
  return newsStore.getNewsWithStatus(filterStatus.value);
});

const filteredAndSearchedNews = computed<News[]>(() => {
  if (!searchQuery.value) {
    return newsListSource.value;
  }
  const query = searchQuery.value.toLowerCase();
  return newsListSource.value.filter(news =>
    news.topic.toLowerCase().includes(query) ||
    (news.fullDetail && news.fullDetail.toLowerCase().includes(query)) || // Check null/undefined
    (news.reporter && news.reporter.toLowerCase().includes(query))
  );
});

const handleNewsRemoved = (removedNewsId: number) => {
  console.log(`News with ID ${removedNewsId} was successfully removed from the main list.`);

  // ตรวจสอบว่าหน้าปัจจุบันว่างหรือไม่หลังจากการลบ
  if (paginatedNews.value.length === 0 && currentPage.value > 1) {
    currentPage.value--;
    updateURL(); // อัปเดต URL และ re-render
  }
};

// ** แก้ไข paginatedNews ให้ใช้ข้อมูลที่ถูกกรองและค้นหาแล้ว **
const paginatedNews = computed<News[]>(() => {
  const start = (currentPage.value - 1) * newsPerPage.value;
  const end = start + newsPerPage.value;
  return filteredAndSearchedNews.value.slice(start, end);
});
</script>