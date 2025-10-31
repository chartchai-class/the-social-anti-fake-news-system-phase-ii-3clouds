<template>
  <div
    class="relative block bg-white shadow-lg rounded-xl overflow-hidden transition-all duration-300 hover:scale-105 border-b-4 cursor-pointer group"
    :class="[ 
      'transition-all duration-300',
      {
        // Active Statuses (ใช้ statusForDisplay แทน news.status)
        'border-green-600 hover:scale-105': statusForDisplay === 'not fake',
        'border-red-600 hover:scale-105': statusForDisplay === 'fake',
        'border-gray-500 hover:scale-105': statusForDisplay === 'equal',

        // Removed Status (Admin Only) - ลด opacity และไม่ให้ hover scale
        'opacity-60 grayscale hover:scale-100 border-gray-400': news.status === 'removed'
      }
    ]" 
    @click="handleNewsClick">

    <!-- ปุ่มลบ (Admin Only) - ซ่อนถ้าข่าวถูกลบแล้ว -->
    <button 
      v-if="isAdmin && news.status !== 'removed'" 
      @click.stop="handleDeleteNews($event)"
      class="absolute top-2 right-2 z-20 p-1.5 bg-red-600 text-white rounded-full shadow-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2 transition-transform duration-200 hover:scale-110 opacity-70 group-hover:opacity-100"
      title="Remove News">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"
        stroke-width="3">
        <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
      </svg>
    </button>

    <!-- Badge แสดงว่าข่าวถูกลบ (แสดงแยกจาก Badge Real/Fake/Equal) -->
    <!-- 
      แสดง badge "Removed" แยกต่างหากที่มุมซ้ายบน 
      ใช้เมื่อข่าวมีสถานะเป็น 'removed'
      แสดงเฉพาะ badge นี้ ไม่ซ้อนกับ badge Real/Fake/Equal
    -->
    <div v-if="news.status === 'removed'" class="absolute top-2 left-2 z-10">
      <span class="inline-flex items-center px-2 py-1 rounded-full text-xs bg-rose-100 text-rose-800 font-medium">
        <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L4.082 16.5c-.77.833.192 2.5 1.732 2.5z"/>
        </svg>
        Removed
      </span>
    </div>

    <!-- News Image Section -->
    <!-- 
      แสดงภาพข่าวถ้ามี URL ที่ถูกต้อง 
      ถ้าโหลดภาพไม่สำเร็จจะซ่อนภาพอัตโนมัติ
    -->
    <div v-if="news.image && (news.image.startsWith('http') || news.image.startsWith('https'))" class="relative">
      <img :src="news.image" :alt="news.topic"
        class="w-full h-48 object-cover transition-transform duration-300 hover:scale-105" @error="handleImageError">
      <div class="absolute inset-0 bg-black bg-opacity-30 flex items-center justify-center p-4">
        <h3 class="text-white text-lg font-bold text-center drop-shadow-lg line-clamp-2">
          {{ news.topic }}
        </h3>
      </div>
    </div>

    <!-- No Image Section -->
    <!-- 
      แสดงเมื่อข่าวไม่มีภาพ หรือภาพไม่ถูกต้อง
      แสดงไอคอนรูปภาพแทนและแสดงหัวข้อข่าว
    -->
    <div v-else
      class="relative bg-gradient-to-br from-gray-100 to-gray-200 w-full h-48 flex items-center justify-center">
      <div class="text-center">
        <svg class="w-12 h-12 text-gray-400 mx-auto mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z">
          </path>
        </svg>
        <h3 class="text-gray-500 text-lg text-center font-bold px-4 line-clamp-2">
          {{ news.topic }}
        </h3>
      </div>
    </div>

    <!-- Status Badge Section - แสดงเฉพาะ Real/Fake/Equal -->
    <!-- 
      แสดงเฉพาะสถานะโหวต (Real/Fake/Equal) 
    -->
    <div class="p-4 flex justify-start space-x-2">
      <span class="px-3 py-1 rounded-full text-xs font-bold transition-colors duration-200" :class="{
        'bg-green-100 text-green-700': statusForDisplay === 'not fake',  // Real - สีเขียว
        'bg-red-100 text-red-700': statusForDisplay === 'fake',          // Fake - สีแดง
        'bg-gray-200 text-gray-700': statusForDisplay === 'equal',       // Equal - สีเทา
      }">
        {{ getStatusText() }}
      </span>

      <!-- Vote counts -->
      <!-- 
        แสดงจำนวนโหวต Real และ Fake 
        ใช้ไอคอนสีเขียวสำหรับ Real และสีแดงสำหรับ Fake
      -->
      <div class="flex items-center space-x-2 text-xs text-gray-500">
        <span class="flex items-center">
          <svg class="w-3 h-3 mr-1 text-green-500" fill="currentColor" viewBox="0 0 20 20">
            <path
              d="M2 10.5a1.5 1.5 0 113 0v6a1.5 1.5 0 01-3 0v-6zM6 10.333v5.43a2 2 0 001.106 1.79l.05.025A4 4 0 008.943 18h5.416a2 2 0 001.962-1.608l1.2-6A2 2 0 0015.56 8H12V4a2 2 0 00-2-2 1 1 0 00-1 1v.667a4 4 0 01-.8 2.4L6.8 7.933a4 4 0 00-.8 2.4z">
            </path>
          </svg>
          {{ news.voteSummary?.real || 0 }}  <!-- แสดงจำนวนโหวต Real -->
        </span>
        <span class="flex items-center">
          <svg class="w-3 h-3 mr-1 text-red-500" fill="currentColor" viewBox="0 0 20 20">
            <path
              d="M18 9.5a1.5 1.5 0 11-3 0v-6a1.5 1.5 0 013 0v6zM14 9.667v-5.43a2 2 0 00-1.106-1.79l-.05-.025A4 4 0 0011.057 2H5.641a2 2 0 00-1.962 1.608l-1.2 6A2 2 0 004.44 12H8v4a2 2 0 002 2 1 1 0 001-1v-.667a4 4 0 01.8-2.4l1.4-1.866a4 4 0 00.8-2.4z">
            </path>
          </svg>
          {{ news.voteSummary?.fake || 0 }}  <!-- แสดงจำนวนโหวต Fake -->
        </span>
      </div>
    </div>

    <!-- Content Section -->
    <!-- 
      ส่วนเนื้อหาข่าว 
      แสดงรายละเอียดสั้นๆ ผู้รายงาน วันที่ และจำนวนความคิดเห็น
    -->
    <div class="p-6 pt-0">
      <p class="text-gray-600 mb-4 line-clamp-3 leading-relaxed">
        {{ news.shortDetail }}  <!-- แสดงรายละเอียดสั้นๆ ของข่าว -->
      </p>

      <!-- Meta Information -->
      <!-- 
        แสดงข้อมูลเมตา: ผู้รายงานและวันที่ 
        ใช้ไอคอนสำหรับผู้ใช้และปฏิทิน
      -->
      <div class="flex justify-between items-center text-sm text-gray-500 mb-4">
        <p class="flex items-center">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
          </svg>
          {{ news.reporter }}  <!-- แสดงชื่อผู้รายงาน -->
        </p>
        <p class="flex items-center">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
          </svg>
          {{ formatDate(news.dateTime) }}  <!-- แสดงวันที่ที่ฟอร์แมตแล้ว -->
        </p>
      </div>

      <!-- Comments count -->
      <!-- 
        แสดงจำนวนความคิดเห็น 
        ใช้ไอคอนความคิดเห็นและแสดงจำนวน
      -->
      <div class="flex items-center text-sm text-gray-500">
        <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z">
          </path>
        </svg>
        {{ news.comments?.length || 0 }} comments  <!-- แสดงจำนวนความคิดเห็น -->
      </div>

      <!-- Read More Indicator -->
      <div class="mt-4 flex items-center justify-between">
        <span class="text-sm font-medium text-blue-600 transition-colors duration-200">
          Click to read more  <!-- ข้อความเรียกให้คลิกดูเพิ่มเติม -->
        </span>
        <svg 
          class="w-4 h-4 text-blue-600 transition-transform duration-200 group-hover:translate-x-1" 
          fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
        </svg>
      </div>

    </div>
  </div>

  <!-- Loading Overlay -->
  <!-- 
    แสดงเมื่อกำลังนำทางไปยังหน้ารายละเอียดข่าว 
    มี spinner และข้อความ loading
  -->
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

// Interface สำหรับ Props ที่รับเข้ามา
interface Props {
  news: News;  // รับข้อมูลข่าวเป็น props
  searchQuery?: string;
}

// กำหนดค่า default สำหรับ props
const props = withDefaults(defineProps<Props>(), {
  searchQuery: ''  // ✅ ค่า default
});

// กำหนด Events ที่ component นี้สามารถ emit ออกไปได้
const emit = defineEmits<{
  (e: 'news-removed', newsId: number): void  // Event เมื่อข่าวถูกลบ
}>();

// Vue Compositions
const router = useRouter();        // สำหรับการนำทาง
const route = useRoute();          // สำหรับอ่าน route ปัจจุบัน
const isNavigating = ref(false);   // สถานะกำลังนำทาง
const authStore = useAuthStore();  // Store สำหรับการ authentication
const newsStore = useNewsStore();  // Store สำหรับจัดการข่าว
const messageStore = useMessageStore();  // Store สำหรับแสดงข้อความ

/**
 * ตรวจสอบว่าผู้ใช้เป็น Admin หรือไม่
 * ใช้สำหรับแสดง/ซ่อนปุ่มลบข่าว
 */
const isAdmin = computed(() => authStore.hasRole('ROLE_ADMIN'));

/**
 * คำนวณสถานะสำหรับแสดงผล (Real/Fake/Equal เท่านั้น)
 * ไม่รวม Removed - จะแสดงเป็น Badge แยกแทน
 * 
 * Logic:
 * - Real votes > Fake votes => 'not fake'
 * - Fake votes > Real votes => 'fake'  
 * - Equal votes => 'equal'
 */
const statusForDisplay = computed(() => {
  const realVotes = props.news.voteSummary?.real || 0;
  const fakeVotes = props.news.voteSummary?.fake || 0;

  if (realVotes > fakeVotes) {
    return 'not fake';  // Real 
  } else if (realVotes < fakeVotes) {
    return 'fake';      // Fake
  } else {
    return 'equal';     // เสมอกัน
  }
});

/**
 * จัดการเมื่อคลิกที่ News card
 * นำทางไปยังหน้ารายละเอียดข่าว
 * รักษาสถานะ filter เดิมจากหน้า home
 */
const handleNewsClick = async () => {
  try {
    isNavigating.value = true;  // เริ่มสถานะ loading

    // คัดลอก query parameters ปัจจุบันเพื่อรักษาสถานะ filter
    const currentQuery = { ...route.query };

    // นำทางไปยังหน้ารายละเอียดข่าว
    await router.push({
      path: `/news/${props.news.id}`,
      query: {
        from: route.path,    // เก็บ path ต้นทาง
        ...currentQuery      // เก็บ query parameters เดิม
      }
    });
  } catch (error) {
    console.error('Navigation error:', error);
  } finally {
    isNavigating.value = false;  // สิ้นสุดสถานะ loading
  }
};

/**
 * แสดงข้อความสถานะ (Real/Fake/Equal เท่านั้น)
 * 
 * @returns {string} ข้อความสถานะ ('Real', 'Fake', หรือ 'Equal')
 */
const getStatusText = () => {
  const realVotes = props.news.voteSummary?.real || 0;
  const fakeVotes = props.news.voteSummary?.fake || 0;

  if (realVotes > fakeVotes) {
    return 'Real';    // Real 
  } else if (fakeVotes > realVotes) {
    return 'Fake';    // Fake 
  } else {
    return 'Equal';   // เสมอกัน
  }
};

/**
 * จัดการเมื่อโหลดภาพไม่สำเร็จ
 * ซ่อน element ภาพเพื่อไม่ให้แสดงภาพเสีย
 */
const handleImageError = (event: Event) => {
  const target = event.target as HTMLImageElement;
  target.style.display = 'none';  // ซ่อนภาพเมื่อโหลดไม่สำเร็จ
};

/**
 * ฟอร์แมตวันที่ให้อยู่ในรูปแบบที่อ่านง่าย
 * 
 * @param dateString - string วันที่จาก API
 * @returns {string} วันที่ที่ฟอร์แมตแล้ว (เช่น "Jan 15, 2024")
 */
const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', {
    month: 'short',   // เดือนแบบย่อ (Jan, Feb, ...)
    day: 'numeric',   // วันที่ (1, 2, ...)
    year: 'numeric'   // ปี (2024)
  });
};

/**
 * จัดการการลบข่าว (Admin Only)
 * 
 * @param event - MouseEvent เพื่อป้องกันการ propagate ไปยัง parent
 */
const handleDeleteNews = async (event: MouseEvent) => {
  event.stopPropagation(); // ป้องกัน click ลามไปที่ card หลัก

  // ตรวจสอบว่าข่าวมี ID ที่ถูกต้อง
  if (!props.news || props.news.id < 0) {
    console.warn("Cannot remove unsaved or invalid news item.");
    return;
  }

  // ยืนยันการลบกับผู้ใช้
  if (confirm(`Are you sure you want to remove this news: "${props.news.topic}"? \n(It will be hidden from regular users)`)) {
    try {
      // เรียก API ลบข่าว
      await newsStore.removeNews(props.news.id);

      // แสดงข้อความสำเร็จ
      messageStore.updateMessage(`News "${props.news.topic}" removed successfully.`);
      setTimeout(() => messageStore.resetMessage(), 3000);

      // รีเฟรชหน้าเว็บทั้งหมดหลังจากลบสำเร็จ
      setTimeout(() => {
        window.location.reload();
      }, 500);

    } catch (error: unknown) {
      console.error('Error removing news (in component):', error);
      
      // ข้อความ error 
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