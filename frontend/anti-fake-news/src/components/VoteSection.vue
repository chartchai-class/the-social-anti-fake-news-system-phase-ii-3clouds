<template>
  <div v-if="news" class="grid grid-cols-1 lg:grid-cols-2 gap-8">
    <!-- Vote Form Section -->
    <div class="bg-gray-100 p-6 rounded-xl">
      <!-- Vote Selection -->
      <div class="mb-6">
        <label class="block text-lg font-semibold text-gray-800 mb-4">Vote:</label>
        <div class="flex space-x-4">
          <button
            @click="selectedVote = 'fake'"
            :class="{ 'bg-red-500 text-white': selectedVote === 'fake', 'bg-red-100 text-red-600 hover:bg-red-200': selectedVote !== 'fake' }"
            class="px-6 py-2 rounded-full font-bold transition-colors duration-200"
          >
            FAKE ✕
          </button>
          <button
            @click="selectedVote = 'real'"
            :class="{ 'bg-green-500 text-white': selectedVote === 'real', 'bg-green-100 text-green-600 hover:bg-green-200': selectedVote !== 'real' }"
            class="px-6 py-2 rounded-full font-bold transition-colors duration-200"
          >
            REAL ✓
          </button>
        </div>
      </div>

      <!-- Username Display -->
      <div class="mb-4">
        <div class="w-full p-3 border border-gray-300 bg-gray-50 rounded-lg text-gray-700 font-medium">
          Voting as: {{ username }}
        </div>
      </div>

      <!-- Comment Textarea -->
      <div class="mb-4">
        <textarea
          v-model="voterComment"
          placeholder="Write a comment (optional)"
          rows="4"
          class="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
        ></textarea>
      </div>

      <!-- Image Upload -->
      <div class="mb-6">
        <ImageUpload v-model="voterImages" :max-files="1" />
      </div>

      <!-- Submit Button -->
      <button
        @click="submitVote"
        :disabled="!selectedVote || isSubmitting"
        class="w-full bg-gray-800 text-white font-bold py-3 rounded-lg hover:bg-gray-900 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors duration-200"
      >
        {{ isSubmitting ? 'Submitting...' : 'Submit' }}
      </button>
    </div>

    <!-- Vote Summary Section -->
    <div>
      <div class="bg-white p-6 rounded-xl border">
        <!-- Summary Header -->
        <div class="mb-4">
          <h3 class="text-lg font-bold text-gray-800 mb-2">
            Vote Summary : 
            <span
              :class="{ 'bg-red-600': fakeVotes > realVotes, 'bg-green-600': realVotes > fakeVotes, 'bg-gray-500': realVotes === fakeVotes }"
              class="text-white font-bold px-3 py-1 rounded text-sm uppercase ml-2"
            >
              {{ fakeVotes > realVotes ? 'FAKE' : realVotes > fakeVotes ? 'REAL' : 'EQUAL' }}
            </span>
          </h3>
        </div>

        <!-- Real Votes Progress Bar -->
        <div class="mb-4">
          <div class="flex items-center justify-between mb-2">
            <span class="text-sm font-medium text-gray-700">Real</span>
            <span class="text-sm text-gray-600">{{ realPercentage }}% ({{ realVotes }} votes)</span>
          </div>
          <div class="w-full bg-gray-200 rounded-full h-4">
            <div class="bg-green-500 h-4 rounded-full transition-all duration-300" :style="{ width: `${realPercentage}%` }"></div>
          </div>
        </div>

        <!-- Fake Votes Progress Bar -->
        <div class="mb-4">
          <div class="flex items-center justify-between mb-2">
            <span class="text-sm font-medium text-gray-700">Fake</span>
            <span class="text-sm text-gray-600">{{ fakePercentage }}% ({{ fakeVotes }} votes)</span>
          </div>
          <div class="w-full bg-gray-200 rounded-full h-4">
            <div class="bg-red-500 h-4 rounded-full transition-all duration-300" :style="{ width: `${fakePercentage}%` }"></div>
          </div>
        </div>

        <!-- Total Votes -->
        <div class="text-right border-t pt-3 mt-4">
          <span class="text-sm font-semibold text-gray-800">Total: {{ totalVotes }} Votes</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick } from 'vue';
import { useNewsStore } from '../stores/news';
import { useAuthStore } from '../stores/auth';
import { useNotificationStore } from '../stores/notification';
import type { News } from '../stores/news';
import ImageUpload from './ImageUpload.vue';

// ========================================
// Props & Emits
// ========================================
const props = defineProps<{ news: News }>();
const emit = defineEmits<{ 'vote-success': [] }>();

// ========================================
// Store Injections
// ========================================
const newsStore = useNewsStore();
const authStore = useAuthStore();
const notificationStore = useNotificationStore();

// ========================================
// State Management
// ========================================
const selectedVote = ref<'fake' | 'real' | null>(null);
const voterComment = ref<string>('');
const voterImages = ref<string[]>([]);
const isSubmitting = ref(false);

// ========================================
// Computed Properties
// ========================================
/**
 * ดึง username จาก authStore
 */
const username = computed(() => authStore.user?.username || 'Anonymous');

/**
 * ดึงรูปภาพแรก (รองรับเฉพาะ 1 รูป)
 */
const voterImage = computed(() => voterImages.value[0] || '');

/**
 * นับคะแนนโหวต Fake
 */
const fakeVotes = computed(() => props.news.voteSummary?.fake || 0);

/**
 * นับคะแนนโหวต Real
 */
const realVotes = computed(() => props.news.voteSummary?.real || 0);

/**
 * รวมคะแนนโหวตทั้งหมด
 */
const totalVotes = computed(() => fakeVotes.value + realVotes.value);

/**
 * คำนวณเปอร์เซ็นต์โหวต Real
 */
const realPercentage = computed(() => {
  if (totalVotes.value === 0) return 0;
  return Math.round((realVotes.value / totalVotes.value) * 100);
});

/**
 * คำนวณเปอร์เซ็นต์โหวต Fake
 */
const fakePercentage = computed(() => {
  if (totalVotes.value === 0) return 0;
  return Math.round((fakeVotes.value / totalVotes.value) * 100);
});

// ========================================
// Scroll Position Management
// ========================================
/**
 * บันทึกตำแหน่ง scroll ปัจจุบันลง sessionStorage
 */
const saveScrollPosition = () => {
  sessionStorage.setItem('voteScrollPosition', window.scrollY.toString());
};

/**
 * กู้คืนตำแหน่ง scroll จาก sessionStorage
 */
const restoreScrollPosition = () => {
  const savedPosition = sessionStorage.getItem('voteScrollPosition');
  if (savedPosition) {
    setTimeout(() => {
      window.scrollTo({
        top: parseInt(savedPosition),
        behavior: 'smooth'
      });
      sessionStorage.removeItem('voteScrollPosition');
    }, 100);
  }
};

// ========================================
// Methods
// ========================================
/**
 * เลื่อนไปที่ตำแหน่งเดิมหลังโหวตเสร็จ
 */
const scrollToPreviousPosition = async () => {
  // บันทึกตำแหน่งปัจจุบันก่อนเปลี่ยน tab
  saveScrollPosition();
  
  // รอให้ tab เปลี่ยนเป็น Comments และข้อมูลโหลดเสร็จ
  await nextTick();
  
  // รออีกนิดเพื่อให้แน่ใจว่า Comments section โหลดเสร็จ
  setTimeout(() => {
    restoreScrollPosition();
  }, 800);
};

/**
 * ส่งโหวต
 */
const submitVote = async () => {
  if (!selectedVote.value) {
    notificationStore.addNotification('Please select your vote (Real or Fake)', 'error');
    return;
  }

  isSubmitting.value = true;

  try {
    // ตรวจสอบว่าเป็นข่าวที่บันทึกแล้ว (id > 0) หรือยัง
    if (props.news.id > 0) {
      // ส่งไปยัง API
      const result = await newsStore.submitComment(props.news.id, {
        username: username.value,
        text: voterComment.value,
        image: voterImage.value,
        vote: selectedVote.value
      });

      if (result.success) {
        notificationStore.addNotification('Vote submitted successfully!', 'success');
        resetForm();
        emit('vote-success');
        await scrollToPreviousPosition();
      } else {
        notificationStore.addNotification(result.error || 'Failed to submit vote', 'error');
      }
    } else {
      // ใช้ local method สำหรับ unsaved news
      newsStore.addCommentToNews(
        props.news.id,
        username.value,
        voterComment.value,
        voterImage.value || null,
        selectedVote.value
      );
      resetForm();
      emit('vote-success');
      await scrollToPreviousPosition();
    }
  } catch (error) {
    console.error('Error submitting vote:', error);
    notificationStore.addNotification('Failed to submit vote. Please try again.', 'error');
  } finally {
    isSubmitting.value = false;
  }
};

/**
 * รีเซ็ตฟอร์ม
 */
const resetForm = () => {
  selectedVote.value = null;
  voterComment.value = '';
  voterImages.value = [];
};
</script>