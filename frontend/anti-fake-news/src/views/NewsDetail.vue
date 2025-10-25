<template>
  <div v-if="isLoading" class="container mx-auto p-4 max-w-4xl">
    <div class="text-center py-20">
      <div class="inline-flex flex-col items-center w-full max-w-md">
        <div class="w-full bg-gray-200 rounded-full h-2 mb-4 overflow-hidden">
          <div 
            class="h-full bg-gradient-to-r from-blue-500 via-blue-600 to-purple-600 transition-all duration-300 rounded-full relative"
            :style="{ width: `${loadingProgress}%` }"
          >
            <div class="absolute inset-0 bg-gradient-to-r from-transparent via-white to-transparent opacity-20 animate-shimmer"></div>
          </div>
        </div>
        <span class="text-gray-500 text-xl">Loading news details... {{ Math.round(loadingProgress) }}%</span>
      </div>
    </div>
  </div>

  <div v-else-if="news" class="container mx-auto p-4 max-w-4xl">
    <router-link
      :to="backToHomeUrl"
      class="group inline-flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50 transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 mb-4"
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="h-4 w-4 text-gray-500 transition-transform duration-200 group-hover:-translate-x-1"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
        stroke-width="2"
      >
        <path stroke-linecap="round" stroke-linejoin="round" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
      </svg>
      <span class="font-medium">Back to Home</span>
    </router-link>

    <div class="bg-white shadow-lg rounded-xl overflow-hidden">
      <div class="p-6">
        <h1 class="text-4xl font-extrabold mb-4 text-gray-900">{{ news.topic }}</h1>

        <div class="flex items-center mb-6 space-x-4 text-sm text-gray-600">
          <span
            :class="{
              'bg-red-600': news.status === 'fake',
              'bg-green-600': news.status === 'not fake',
              'bg-gray-500': news.status === 'equal',
            }"
            class="text-white font-bold px-3 py-1 rounded-full uppercase"
          >
            {{
              news.status === 'fake'
                ? 'Fake'
                : news.status === 'not fake'
                  ? 'Real'
                  : 'Equal'
            }}
          </span>
          <p>Reporter: <span class="font-semibold">{{ news.reporter }}</span></p>
          <p>Date: <span class="font-semibold">{{ new Date(news.dateTime).toLocaleDateString() }}</span></p>
        </div>

        <img
          v-if="news.image && (news.image.startsWith('http') || news.image.startsWith('https'))"
          :src="news.image"
          :alt="news.topic"
          class="w-full h-auto rounded-lg mb-8"
          @load="handleImageLoad"
          @error="handleImageError"
        >
      
        <p class="text-lg text-gray-800 leading-relaxed mb-10 whitespace-pre-line">{{ news.fullDetail }}</p>
        
        <div class="mb-6">
          <div class="flex space-x-8 border-b border-gray-200">
            <button
              @click="switchTab('comments')"
              :disabled="isTabSwitching"
              :class="{
                'text-black border-black': activeTab === 'comments',
                'text-gray-600 border-transparent hover:text-gray-800': activeTab !== 'comments',
                'opacity-50 cursor-not-allowed': isTabSwitching
              }"
              class="pb-3 px-1 border-b-2 font-medium transition-colors duration-200 relative"
            >
              All Comments
              <div v-if="isTabSwitching && pendingTab === 'comments'" class="absolute -top-1 -right-1">
                <div class="animate-spin rounded-full h-3 w-3 border-b border-gray-600"></div>
              </div>
            </button>
            <button
              v-if="canViewVoteTab"
              @click="switchTab('vote')"
              :disabled="isTabSwitching"
              :class="{
                'text-black border-black': activeTab === 'vote',
                'text-gray-600 border-transparent hover:text-gray-800': activeTab !== 'vote',
                'opacity-50 cursor-not-allowed': isTabSwitching
              }"
              class="pb-3 px-1 border-b-2 font-medium transition-colors duration-200 relative"
            >
              Vote
              <div v-if="isTabSwitching && pendingTab === 'vote'" class="absolute -top-1 -right-1">
                <div class="animate-spin rounded-full h-3 w-3 border-b border-gray-600"></div>
              </div>
            </button>
          </div>
          <div v-if="!canViewVoteTab" class="mt-3 text-sm text-gray-500">
            Sign in to vote and comment.
          </div>
        </div>

        <div v-if="isTabSwitching" class="text-center py-8">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500 mx-auto mb-2"></div>
          <span class="text-gray-500">Loading...</span>
        </div>

        <CommentsSection
            v-else-if="activeTab === 'comments'"
            :comments="news.comments"
            :vote-summary="news.voteSummary"
        />

        <VoteSection
            v-else-if="activeTab === 'vote' && canViewVoteTab"
            :news="news"
            @submit-vote="handleVoteSubmission"
        />
        
        </div>
    </div>
  </div>
  
  <div v-else class="container mx-auto p-4 text-center py-20">
    <div class="flex flex-col items-center">
      <svg class="w-16 h-16 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
      </svg>
      <h1 class="text-2xl font-bold text-gray-500 mb-2">News not found</h1>
      <p class="text-gray-400">The news article you're looking for doesn't exist or has been removed.</p>
      <router-link 
        to="/" 
        class="mt-4 px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors duration-200"
      >
        Go back to Home
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useNewsStore } from '../stores/news';
import { useNotificationStore } from '../stores/notification';
import { useAuthStore, type Role } from '../stores/auth';
import CommentsSection from '../components/CommentsVotes.vue';
import VoteSection from '../components/VoteSection.vue';
import type { Vote } from '../stores/news';

const route = useRoute();
const newsStore = useNewsStore();
const notificationStore = useNotificationStore();
const authStore = useAuthStore();

// **แก้ไข: ใช้ parseFloat เพื่อรองรับ ID ที่เป็นตัวเลขทศนิยม**
const newsId = parseFloat(route.params.id as string);

// **แก้ไข: ดึงข้อมูลข่าวโดยใช้ Getter จาก Store**
const news = computed(() => newsStore.getNewsById(newsId));

const activeTab = ref<'comments' | 'vote'>('comments');
const isLoading = ref(false);
const isTabSwitching = ref(false);
const pendingTab = ref<'comments' | 'vote' | null>(null);
const isNavigationLoading = ref(false);

const allowedVoteRoles: Role[] = ['ROLE_READER', 'ROLE_MEMBER', 'ROLE_ADMIN'];
const canViewVoteTab = computed(() => authStore.hasAnyRole(allowedVoteRoles));

const loadingProgress = ref(0);
// let progressInterval: NodeJS.Timeout | null = null;
let progressInterval: ReturnType<typeof setTimeout> | null = null

const startLoadingProgress = () => {
  loadingProgress.value = 0;
  if (progressInterval) clearInterval(progressInterval);
  
  progressInterval = setInterval(() => {
    if (loadingProgress.value < 90) {
      loadingProgress.value += Math.random() * 15;
      if (loadingProgress.value > 90) {
        loadingProgress.value = 90;
      }
    }
  }, 150);
};

const finishLoadingProgress = () => {
  loadingProgress.value = 100;
  if (progressInterval) {
    clearInterval(progressInterval);
    progressInterval = null;
  }
};

const loadNewsDetail = async () => {
  try {
    isLoading.value = true;
    startLoadingProgress();
    
    // **เปลี่ยนมาใช้ fetchNews() จาก Store**
    await Promise.all([
      newsStore.fetchNews(),
      new Promise(resolve => setTimeout(resolve, 800))
    ]);
    
    finishLoadingProgress();
    
  } catch (error) {
    console.error('Error loading news detail:', error);
    notificationStore.addNotification('Error loading news details', 'error');
  } finally {
    setTimeout(() => {
      isLoading.value = false;
      loadingProgress.value = 0;
    }, 400); // Small delay to show 100%
  }
};


// Preserve the filter state when going back to home
const backToHomeUrl = computed(() => {
  const referrer = document.referrer;
  const currentOrigin = window.location.origin;
  
  // If user came from the home page with filters, preserve them
  if (referrer.includes(currentOrigin) && referrer.includes('?')) {
    const url = new URL(referrer);
    return { path: '/', query: Object.fromEntries(url.searchParams) };
  }
  
  return '/';
});

// Watch for route changes to handle navigation loading
watch(() => route.query.loading, (newVal) => {
  if (newVal === 'true') {
    isNavigationLoading.value = true;
  }
});

watch(canViewVoteTab, (value) => {
  if (!value && activeTab.value === 'vote') {
    activeTab.value = 'comments';
  }
});

const switchTab = async (tab: 'comments' | 'vote') => {
  if (activeTab.value === tab || isTabSwitching.value) return;
  if (tab === 'vote' && !canViewVoteTab.value) {
    notificationStore.addNotification('You do not have permission to vote on this news.', 'error');
    return;
  }
  
  try {
    isTabSwitching.value = true;
    pendingTab.value = tab;
    
    // Simulate tab switching delay
    await new Promise(resolve => setTimeout(resolve, 300));
    
    activeTab.value = tab;
    
  } catch (error) {
    console.error('Error switching tab:', error);
  } finally {
    isTabSwitching.value = false;
    pendingTab.value = null;
  }
};

const handleVoteSubmission = async (data: {
  userName: string;
  vote: Vote;
  commentText: string;
  imageUrl: string | null;
}) => {
  try {
    if (!canViewVoteTab.value) {
      notificationStore.addNotification('You do not have permission to vote on this news.', 'error');
      return;
    }

    // Removed loadingStore usage for vote submission
    
    newsStore.addCommentToNews(
      newsId,
      data.userName || 'Anonymous',
      data.commentText || '',
      data.imageUrl,
      data.vote
    );
    
    // Simulate submission processing
    await new Promise(resolve => setTimeout(resolve,300));
    
    notificationStore.addNotification('Vote submitted successfully.', 'success');
    
    // Switch to comments tab with loading animation
    await switchTab('comments');
    
  } catch (error) {
    console.error('Error submitting vote:', error);
    notificationStore.addNotification('Error submitting vote', 'error');
  }
};

const handleImageLoad = () => {
  // Image loaded successfully
};

const handleImageError = () => {
  console.warn('Failed to load news image');
};

onMounted(() => {
  authStore.hydrateFromStorage();
  loadNewsDetail();
});
</script>
