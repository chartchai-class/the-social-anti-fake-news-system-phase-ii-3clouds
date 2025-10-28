<template>
  <div
    class="relative block bg-white shadow-lg rounded-xl overflow-hidden transition-all duration-300 hover:scale-105 border-b-4 cursor-pointer"
    :class="[
      'transition-all duration-300',
      {
        // Active Statuses
        'border-green-600 hover:scale-105': news.status === 'not fake',
        'border-red-600 hover:scale-105': news.status === 'fake',
        'border-gray-500 hover:scale-105': news.status === 'equal',

        // Removed Status (Admin Only)
        // 'opacity-60 grayscale hover:scale-100 border-gray-400': isAdmin && news.status === 'removed'
      }
    ]" @click="handleNewsClick">

    <!-- ปุ่มลบ (Admin Only) -->
    <button v-if="isAdmin && news.status != 'removed'" @click.stop="handleDeleteNews($event)"
      class="absolute top-2 right-2 z-20 p-1.5 bg-red-600 text-white rounded-full shadow-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2 transition-transform duration-200 hover:scale-110 opacity-70 group-hover:opacity-100"
      title="Remove News">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"
        stroke-width="3">
        <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
      </svg>
    </button>

    <!-- News Image Section -->
    <div v-if="news.image && (news.image.startsWith('http') || news.image.startsWith('https'))" class="relative">
      <img :src="news.image" :alt="news.topic"
        class="w-full h-48 object-cover transition-transform duration-300 hover:scale-105" @error="handleImageError">
      <div class="absolute inset-0 bg-black bg-opacity-30 flex items-center justify-center p-4">
        <h3 class="text-white text-lg font-bold text-center drop-shadow-lg line-clamp-2">
          <span v-html="highlightText(news.topic, searchQuery)"></span>
        </h3>
      </div>
    </div>

    <!-- No Image Section -->
    <div v-else
      class="relative bg-gradient-to-br from-gray-100 to-gray-200 w-full h-48 flex items-center justify-center">
      <div class="text-center">
        <svg class="w-12 h-12 text-gray-400 mx-auto mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z">
          </path>
        </svg>
        <h3 class="text-gray-500 text-lg text-center font-bold px-4 line-clamp-2">
          <span v-html="highlightText(news.topic, searchQuery)"></span>
        </h3>
      </div>
    </div>

    <!-- Status Badge Section -->
    <div class="p-4 flex justify-start space-x-2">
      <span class="px-3 py-1 rounded-full text-xs font-bold transition-colors duration-200" :class="{
        'bg-green-100 text-green-700': news.voteSummary.real > news.voteSummary.fake,
        'bg-red-100 text-red-700': news.voteSummary.fake > news.voteSummary.real,
        'bg-gray-200 text-gray-700': news.voteSummary.real === news.voteSummary.fake,
      }">
        {{ getStatusText() }}
      </span>

      <!-- Vote counts -->
      <div class="flex items-center space-x-2 text-xs text-gray-500">
        <span class="flex items-center">
          <svg class="w-3 h-3 mr-1 text-green-500" fill="currentColor" viewBox="0 0 20 20">
            <path
              d="M2 10.5a1.5 1.5 0 113 0v6a1.5 1.5 0 01-3 0v-6zM6 10.333v5.43a2 2 0 001.106 1.79l.05.025A4 4 0 008.943 18h5.416a2 2 0 001.962-1.608l1.2-6A2 2 0 0015.56 8H12V4a2 2 0 00-2-2 1 1 0 00-1 1v.667a4 4 0 01-.8 2.4L6.8 7.933a4 4 0 00-.8 2.4z">
            </path>
          </svg>
          {{ news.voteSummary.real }}
        </span>
        <span class="flex items-center">
          <svg class="w-3 h-3 mr-1 text-red-500" fill="currentColor" viewBox="0 0 20 20">
            <path
              d="M18 9.5a1.5 1.5 0 11-3 0v-6a1.5 1.5 0 013 0v6zM14 9.667v-5.43a2 2 0 00-1.106-1.79l-.05-.025A4 4 0 0011.057 2H5.641a2 2 0 00-1.962 1.608l-1.2 6A2 2 0 004.44 12H8v4a2 2 0 002 2 1 1 0 001-1v-.667a4 4 0 01.8-2.4l1.4-1.866a4 4 0 00.8-2.4z">
            </path>
          </svg>
          {{ news.voteSummary.fake }}
        </span>
      </div>
    </div>

    <!-- Content Section -->
    <div class="p-6 pt-0">
      <p class="text-gray-600 mb-4 line-clamp-3 leading-relaxed">
        <span v-html="highlightText(news.shortDetail, searchQuery)"></span>
      </p>

      <!-- Meta Information -->
      <div class="flex justify-between items-center text-sm text-gray-500 mb-4">
        <p class="flex items-center">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
          </svg>
          <span v-html="highlightText(news.reporter, searchQuery)"></span>
        </p>
        <p class="flex items-center">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
          </svg>
          {{ formatDate(news.dateTime) }}
        </p>
      </div>

      <!-- Comments count -->
      <div class="flex items-center text-sm text-gray-500">
        <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z">
          </path>
        </svg>
        {{ news.comments?.length || 0 }} comments
      </div>

      <!-- Read More Indicator -->
      <div class="mt-4 flex items-center justify-between">
        <span class="text-blue-600 text-sm font-medium">Click to read more</span>
        <svg class="w-4 h-4 text-blue-600 transition-transform duration-200 group-hover:translate-x-1" fill="none"
          stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
        </svg>
      </div>

      <!-- Search Match Indicator -->
      <div v-if="searchQuery && hasMatch" class="mt-3 flex items-center text-xs text-blue-600">
        <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
        </svg>
        Search match found
      </div>
    </div>
  </div>

  <div v-if="isNavigating"
    class="absolute inset-0 bg-white bg-opacity-90 z-10 flex items-center justify-center rounded-xl">
    <div class="flex flex-col items-center">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500 mb-2"></div>
      <span class="text-gray-600 text-sm">Loading news...</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import type { News } from '../stores/news';
import { useNewsStore } from '../stores/news';
import { useMessageStore } from '../stores/message';
import { useAuthStore } from '../stores/auth';

interface Props {
  news: News;
  searchQuery?: string;
}

const props = withDefaults(defineProps<Props>(), {
  searchQuery: ''
});

const emit = defineEmits<{
  (e: 'news-removed', newsId: number): void
}>();

const router = useRouter();
const route = useRoute();
const isNavigating = ref(false);
const authStore = useAuthStore();
const newsStore = useNewsStore();
const messageStore = useMessageStore();

const isAdmin = computed(() => authStore.hasRole('ROLE_ADMIN'));

// Highlight function
const highlightText = (text: string, searchQuery: string): string => {
  if (!searchQuery || !text) return text;

  const query = searchQuery.trim();
  if (!query) return text;

  // Escape special regex characters and create case-insensitive regex
  const escapedQuery = query.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  const regex = new RegExp(`(${escapedQuery})`, 'gi');

  return text.replace(regex, '<mark class="bg-yellow-200 text-yellow-900 px-0.5 py-0.5 rounded font-semibold">$1</mark>');
};

// Check if search query matches any content
const hasMatch = computed(() => {
  if (!props.searchQuery) return false;

  const query = props.searchQuery.toLowerCase();
  return props.news.topic.toLowerCase().includes(query) ||
    props.news.shortDetail.toLowerCase().includes(query) ||
    props.news.fullDetail.toLowerCase().includes(query) ||
    props.news.reporter.toLowerCase().includes(query);
});

const handleNewsClick = async () => {
  try {
    isNavigating.value = true;

    // Preserve current filter state in URL
    const currentQuery = { ...route.query };

    // Navigate to news detail
    await router.push({
      path: `/news/${props.news.id}`,
      query: {
        from: route.path,
        ...currentQuery
      }
    });
  } catch (error) {
    console.error('Navigation error:', error);
  } finally {
    isNavigating.value = false;
  }
};

const getStatusText = () => {
  if (props.news.voteSummary.real > props.news.voteSummary.fake) {
    return 'Real';
  } else if (props.news.voteSummary.fake > props.news.voteSummary.real) {
    return 'Fake';
  } else {
    return 'Equal';
  }
};

const handleImageError = (event: Event) => {
  const target = event.target as HTMLImageElement;
  target.style.display = 'none';
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  });
};

const handleDeleteNews = async (event: MouseEvent) => {
  event.stopPropagation(); // ป้องกัน click ลามไปที่ card หลัก

  if (!props.news || props.news.id < 0) {
    console.warn("Cannot remove unsaved or invalid news item.");
    return;
  }

  if (confirm(`Are you sure you want to remove this news: "${props.news.topic}"? \n(It will be hidden from regular users)`)) {
    try {
      await newsStore.removeNews(props.news.id);

      messageStore.updateMessage(`News "${props.news.topic}" removed successfully.`);
      setTimeout(() => messageStore.resetMessage(), 3000);

      // Card จะหายไปเองเมื่อ State update

    } catch (error: unknown) {
      console.error('Error removing news (in component):', error);
      let errorMsg = 'Failed to remove news.';
      if (typeof error === 'object' && error !== null && 'response' in error) {
        const apiError = error as { response?: { data?: { message?: string } } };
        errorMsg = apiError.response?.data?.message || 'Failed to remove news.';
      } else if (error instanceof Error) {
        errorMsg = error.message;
      }
      messageStore.updateMessage(errorMsg);
      setTimeout(() => messageStore.resetMessage(), 4000);
    }
  }
};
</script>