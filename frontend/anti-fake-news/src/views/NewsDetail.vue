<template>
  <div v-if="isLoading" class="container mx-auto p-4 max-w-4xl">
    <!-- Loading State -->
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

  <!-- Main Content - แสดงเมื่อโหลดข้อมูลสำเร็จและผู้ใช้มีสิทธิ์ดู -->
  <div v-else-if="news && isNewsAccessible" class="container mx-auto p-4 max-w-4xl">
    <!-- Back to Home Button -->
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

    <!-- News Content Container -->
    <div class="bg-white shadow-lg rounded-xl overflow-hidden">
      <div class="p-6">
        <!-- Removed News Warning Banner -->
        <!-- 
          แสดงเฉพาะเมื่อข่าวถูกลบ (isNewsRemoved = true)
        -->
        <div v-if="isNewsRemoved" class="mb-4 bg-rose-50 border border-rose-200 rounded-lg p-4">
          <div class="flex items-start">
            <svg class="w-5 h-5 text-rose-600 mt-0.5 mr-3 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L4.082 16.5c-.77.833.192 2.5 1.732 2.5z"/>
            </svg>
            <div>
              <p class="text-sm font-medium text-rose-800">This news has been removed</p>
            </div>
          </div>
        </div>

        <!-- News Title -->
        <h1 class="text-4xl font-extrabold mb-4 text-gray-900">{{ news.topic }}</h1>

        <!-- News Metadata Section -->
        <!-- 
          แสดงข้อมูลเมตาของข่าว: สถานะ, ผู้รายงาน, วันที่
          สถานะแสดงเฉพาะ Real/Fake/Equal เท่านั้น (ไม่แสดง Removed)
        -->
        <div class="flex items-center mb-6 space-x-4 text-sm text-gray-600">
          <!-- Status Badge -->
          <span
            :class="{
              'bg-red-600': newsStatus === 'fake',        // Fake - สีแดง
              'bg-green-600': newsStatus === 'not fake',  // Real - สีเขียว
              'bg-gray-500': newsStatus === 'equal',      // Equal - สีเทา
            }"
            class="text-white font-bold px-3 py-1 rounded-full uppercase"
          >
            {{
              newsStatus === 'fake'
                ? 'Fake'
                : newsStatus === 'not fake'
                  ? 'Real'
                  : 'Equal'
            }}
          </span>
          
          <!-- Reporter and Date -->
          <p>Reporter: <span class="font-semibold">{{ news.reporter }}</span></p>
          <p>Date: <span class="font-semibold">{{ new Date(news.dateTime).toLocaleDateString() }}</span></p>
        </div>

        <!-- News Image URL ถูกต้อง -->
        <img
          v-if="news.image && (news.image.startsWith('http') || news.image.startsWith('https'))"
          :src="news.image"
          :alt="news.topic"
          class="w-full h-auto rounded-lg mb-8"
          @load="handleImageLoad"
          @error="handleImageError"
        >
      
        <!-- News Full Detail -->
        <p class="text-lg text-gray-800 leading-relaxed mb-10 whitespace-pre-line">{{ news.fullDetail }}</p>
        
        <!-- Tab Navigation -->
        <!-- 
          สลับระหว่าง Comments และ Vote tabs
          Vote tab แสดงเฉพาะผู้ใช้ที่มีสิทธิ์โหวต
        -->
        <div class="mb-6">
          <div class="flex space-x-8 border-b border-gray-200">
            <!-- Comments Tab -->
            <button
              @click="switchTab('comments')"
              :disabled="isTabSwitching"
              :class="{
                'text-black border-black': activeTab === 'comments',           // Active state
                'text-gray-600 border-transparent hover:text-gray-800': activeTab !== 'comments', // Inactive state
                'opacity-50 cursor-not-allowed': isTabSwitching               // Disabled state
              }"
              class="pb-3 px-1 border-b-2 font-medium transition-colors duration-200 relative"
            >
              All Comments
              <!-- Loading spinner สำหรับ tab switching -->
              <div v-if="isTabSwitching && pendingTab === 'comments'" class="absolute -top-1 -right-1">
                <div class="animate-spin rounded-full h-3 w-3 border-b border-gray-600"></div>
              </div>
            </button>
            
            <!-- Vote Tab (แสดงเฉพาะผู้ใช้ที่มีสิทธิ์) -->
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
          
          <!-- Message สำหรับผู้ใช้ที่ไม่มีสิทธิ์โหวต -->
          <div v-if="!canViewVoteTab" class="mt-3 text-sm text-gray-500">
            Sign in to vote and comment.
          </div>
        </div>

        <!-- Tab Switching Loading State -->
        <div v-if="isTabSwitching" class="text-center py-8">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500 mx-auto mb-2"></div>
          <span class="text-gray-500">Loading...</span>
        </div>

        <!-- Comments Section -->
        <!-- 
          แสดงเมื่อเลือก Tab Comments
          ส่งข้อมูล comments และ vote summary ไปยัง component ย่อย
        -->
        <CommentsSection
            v-else-if="activeTab === 'comments'"
            :comments="news.comments"
            :vote-summary="news.voteSummary"
        />

        <!-- Vote Section -->
        <!-- 
          แสดงเมื่อเลือก Tab Vote และผู้ใช้มีสิทธิ์โหวต
          ส่งข้อมูลข่าวและรับ event เมื่อโหวตสำเร็จ
        -->
        <VoteSection
            v-else-if="activeTab === 'vote' && canViewVoteTab"
            :news="news"
            @vote-success="handleVoteSuccess"
        />
        
        </div>
    </div>
  </div>

  <!-- News Not Found Section -->
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
import apiClient from '../services/NewsService';

// Vue Router และ Store Injections
const route = useRoute();                          // สำหรับอ่าน route parameters
const newsStore = useNewsStore();                  // Store สำหรับจัดการข่าว
const notificationStore = useNotificationStore();  // Store สำหรับแสดง notifications
const authStore = useAuthStore();                  // Store สำหรับจัดการ authentication

// ดึง newsId จาก URL parameters
const newsId = parseFloat(route.params.id as string);

// Reactive State
const news = ref<any>(null);                       // ข้อมูลข่าวจาก API
const activeTab = ref<'comments' | 'vote'>('comments');  // Tab ที่ active อยู่
const isLoading = ref(false);                      // สถานะกำลังโหลดข้อมูลข่าว
const isTabSwitching = ref(false);                 // สถานะกำลังสลับ tab
const pendingTab = ref<'comments' | 'vote' | null>(null); // Tab ที่กำลังจะสลับไป
const isNavigationLoading = ref(false);            // สถานะกำลังนำทาง

// Roles ที่สามารถโหวตได้
const allowedVoteRoles: Role[] = ['ROLE_READER', 'ROLE_MEMBER', 'ROLE_ADMIN'];

/**
 * ตรวจสอบว่าผู้ใช้มีสิทธิ์โหวตหรือไม่ ต้องมี role ใด role หนึ่งใน allowedVoteRoles
 */
const canViewVoteTab = computed(() => authStore.hasAnyRole(allowedVoteRoles));

/**
 * ตรวจสอบว่าผู้ใช้เป็น Admin หรือไม่ กำหนดสิทธิ์การดูข่าวที่ถูกลบ
 */
const canViewRemovedNews = computed(() => authStore.hasRole('ROLE_ADMIN'));

/**
 * ตรวจสอบว่าข่าวถูกลบหรือไม่
 */
const isNewsRemoved = computed(() => {
  return news.value?.removed === true;
});

/**
 * คำนวณสถานะของข่าว (Real/Fake/Equal)
 * 
 * Logic:
 * - Real votes > Fake votes => 'not fake' (Real)
 * - Fake votes > Real votes => 'fake'
 * - Equal votes => 'equal'
 */
const newsStatus = computed(() => {
  if (!news.value) return 'equal';  // Default value เมื่อยังไม่มีข้อมูล

  const realVotes = news.value.voteSummary?.real || 0;
  const fakeVotes = news.value.voteSummary?.fake || 0;

  if (realVotes > fakeVotes) {
    return 'not fake';  // Real 
  } else if (realVotes < fakeVotes) {
    return 'fake';      // Fake 
  } else {
    return 'equal';     // เสมอกัน
  }
});

/**
 * ตรวจสอบว่าผู้ใช้มีสิทธิ์ดูข่าวนี้หรือไม่
 * 
 * Rules:
 * - ถ้าข่าวไม่ถูกลบ → ทุกคนดูได้
 * - ถ้าข่าวถูกลบ → เฉพาะ Admin ดูได้
 */
const isNewsAccessible = computed(() => {
  if (!news.value) return false;  // ยังไม่มีข้อมูลข่าว
  
  return !isNewsRemoved.value || canViewRemovedNews.value;
});

// Loading Progress Animation
const loadingProgress = ref(0);
let progressInterval: ReturnType<typeof setTimeout> | null = null

/**
 * เริ่ม animation progress bar
 * เพิ่มค่า progress อย่างค่อยเป็นค่อยไปจนถึง 90%
 */
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

/**
 * สิ้นสุด animation progress bar
 */
const finishLoadingProgress = () => {
  loadingProgress.value = 100;
  if (progressInterval) {
    clearInterval(progressInterval);
    progressInterval = null;
  }
};

/**
 * โหลดข้อมูลข่าวจาก Backend API
 * ใช้ apiClient เพื่อดึงข้อมูลข่าวตาม ID
 */
const loadNewsDetail = async () => {
  try {
    isLoading.value = true;
    startLoadingProgress();  // เริ่ม progress animation
    
    // เรียก API เพื่อดึงข้อมูลข่าว
    const response = await apiClient.getNewsById(newsId);
    news.value = response.data;
    
    finishLoadingProgress();  // สิ้นสุด progress animation
    
  } catch (error) {
    console.error('Error loading news detail:', error);
    notificationStore.addNotification('Error loading news details', 'error');
  } finally {
    setTimeout(() => {
      isLoading.value = false;
      loadingProgress.value = 0;
    }, 400);
  }
};

/**
 * สร้าง URL สำหรับกลับไปหน้าแรก พร้อมรักษาสถานะ filter เดิมจาก referrer
 */
const backToHomeUrl = computed(() => {
  const referrer = document.referrer;
  const currentOrigin = window.location.origin;
  
  // ถ้ามาจากหน้า home และมี query parameters
  if (referrer.includes(currentOrigin) && referrer.includes('?')) {
    const url = new URL(referrer);
    return { path: '/', query: Object.fromEntries(url.searchParams) };
  }
  
  return '/';  // กลับไปหน้าแรกปกติ
});

// Watchers
/**
 * ติดตามการเปลี่ยนแปลง route query parameter 'loading'
 * ใช้สำหรับจัดการ navigation loading state
 */
watch(() => route.query.loading, (newVal) => {
  if (newVal === 'true') {
    isNavigationLoading.value = true;
  }
});

/**
 * ติดตามการเปลี่ยนแปลงสิทธิ์การโหวต ถ้าไม่มีสิทธิ์และอยู่ที่ tab Vote ให้สลับกลับไป Comments
 */
watch(canViewVoteTab, (value) => {
  if (!value && activeTab.value === 'vote') {
    activeTab.value = 'comments';
  }
});

/**
 * สลับระหว่าง Tab (Comments <-> Vote)
 * 
 * @param tab - Tab ที่ต้องการสลับไป ('comments' | 'vote')
 */
const switchTab = async (tab: 'comments' | 'vote') => {
  // ไม่ทำอะไรถ้าเป็น tab เดิมหรือกำลังสลับอยู่
  if (activeTab.value === tab || isTabSwitching.value) return;
  
  // ตรวจสอบสิทธิ์ก่อนสลับไป tab Vote
  if (tab === 'vote' && !canViewVoteTab.value) {
    notificationStore.addNotification('You do not have permission to vote on this news.', 'error');
    return;
  }
  
  try {
    isTabSwitching.value = true;
    pendingTab.value = tab;
    
    // Simulate loading delay สำหรับ UX
    await new Promise(resolve => setTimeout(resolve, 300));
    
    activeTab.value = tab;
    
  } catch (error) {
    console.error('Error switching tab:', error);
  } finally {
    isTabSwitching.value = false;
    pendingTab.value = null;
  }
};

/**
 * Handler เมื่อโหวตสำเร็จ
 * โหลดข้อมูลข่าวใหม่และสลับไป tab Comments
 */
const handleVoteSuccess = async () => {
  try {
    // โหลดข้อมูลข่าวใหม่เพื่ออัพเดทจำนวนโหวต
    await loadNewsDetail();
    
    // หน่วงเวลาเล็กน้อยเพื่อให้เห็นการเปลี่ยนแปลง
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // สลับไป tab Comments เพื่อดูผลโหวต
    await switchTab('comments');
    
  } catch (error) {
    console.error('Error after vote submission:', error);
  }
};


const handleImageLoad = () => {
  // Image loaded successfully
};

const handleImageError = () => {
  // Image loaded fail
  console.warn('Failed to load news image');
};


/**
 * เมื่อ component ถูก mount โหลดข้อมูล authentication และข้อมูลข่าว
 */
onMounted(() => {
  authStore.hydrateFromStorage();  // โหลดข้อมูล authentication จาก localStorage
  loadNewsDetail();                // โหลดข้อมูลข่าว
});
</script>