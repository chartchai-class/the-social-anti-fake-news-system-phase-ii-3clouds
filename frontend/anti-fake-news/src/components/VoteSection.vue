<template>
  <div v-if="news" class="grid grid-cols-1 lg:grid-cols-2 gap-8">
    <div class="bg-gray-100 p-6 rounded-xl">
      <div class="mb-6">
        <label class="block text-lg font-semibold text-gray-800 mb-4">Vote:</label>
        <div class="flex space-x-4">
          <button
            @click="selectedVote = 'fake'"
            :class="{
              'bg-red-500 text-white': selectedVote === 'fake',
              'bg-red-100 text-red-600 hover:bg-red-200': selectedVote !== 'fake'
            }"
            class="px-6 py-2 rounded-full font-bold transition-colors duration-200"
          >
            FAKE ✕
          </button>
          <button
            @click="selectedVote = 'real'"
            :class="{
              'bg-green-500 text-white': selectedVote === 'real',
              'bg-green-100 text-green-600 hover:bg-green-200': selectedVote !== 'real'
            }"
            class="px-6 py-2 rounded-full font-bold transition-colors duration-200"
          >
            REAL ✓
          </button>
        </div>
      </div>

      <div class="mb-4">
        <div class="w-full p-3 border border-gray-300 bg-gray-50 rounded-lg text-gray-700 font-medium">
          Voting as: {{ username }}
        </div>
      </div>

      <div class="mb-4">
        <textarea
          v-model="voterComment"
          placeholder="Write a comment (optional)"
          rows="4"
          class="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
        ></textarea>
      </div>

      <div class="mb-6">
        <ImageUpload v-model="voterImages" :max-files="1" field-name="image" />
      </div>

      <button
        @click="submitVote"
        :disabled="!selectedVote || isSubmitting"
        class="w-full bg-gray-800 text-white font-bold py-3 rounded-lg hover:bg-gray-900 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors duration-200"
      >
        {{ isSubmitting ? 'Submitting...' : 'Submit' }}
      </button>
    </div>

    <div>
      <div class="bg-white p-6 rounded-xl border">
        <div class="mb-4">
          <h3 class="text-lg font-bold text-gray-800 mb-2">
            Vote Summary : 
            <span
              :class="{
                'bg-red-600': fakeVotes > realVotes,
                'bg-green-600': realVotes > fakeVotes,
                'bg-gray-500': realVotes === fakeVotes
              }"
              class="text-white font-bold px-3 py-1 rounded text-sm uppercase ml-2"
            >
              {{ fakeVotes > realVotes ? 'FAKE' : realVotes > fakeVotes ? 'REAL' : 'EQUAL' }}
            </span>
          </h3>
        </div>

        <div class="mb-4">
          <div class="flex items-center justify-between mb-2">
            <span class="text-sm font-medium text-gray-700">Real</span>
            <span class="text-sm text-gray-600">{{ realPercentage }}% ({{ realVotes }} votes)</span>
          </div>
          <div class="w-full bg-gray-200 rounded-full h-4">
            <div 
              class="bg-green-500 h-4 rounded-full transition-all duration-300"
              :style="{ width: `${realPercentage}%` }"
            ></div>
          </div>
        </div>

        <div class="mb-4">
          <div class="flex items-center justify-between mb-2">
            <span class="text-sm font-medium text-gray-700">Fake</span>
            <span class="text-sm text-gray-600">{{ fakePercentage }}% ({{ fakeVotes }} votes)</span>
          </div>
          <div class="w-full bg-gray-200 rounded-full h-4">
            <div 
              class="bg-red-500 h-4 rounded-full transition-all duration-300"
              :style="{ width: `${fakePercentage}%` }"
            ></div>
          </div>
        </div>

        <div class="text-right border-t pt-3 mt-4">
          <span class="text-sm font-semibold text-gray-800">Total: {{ totalVotes }} Votes</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useNewsStore } from '../stores/news';
import { useAuthStore } from '../stores/auth';
import { useNotificationStore } from '../stores/notification';
import type { News } from '../stores/news';
import ImageUpload from './ImageUpload.vue';

const props = defineProps<{
  news: News
}>();

const newsStore = useNewsStore();
const authStore = useAuthStore();
const notificationStore = useNotificationStore();

const selectedVote = ref<'fake' | 'real' | null>(null);
const voterComment = ref<string>('');
const voterImages = ref<string[]>([]);  // ✅ Changed to array
const isSubmitting = ref(false);

// ดึง username จาก authStore
const username = computed(() => authStore.user?.username || 'Anonymous');

// ✅ Computed property to get first image or empty string
const voterImage = computed(() => voterImages.value[0] || '');

const fakeVotes = computed(() => props.news.voteSummary?.fake || 0);
const realVotes = computed(() => props.news.voteSummary?.real || 0);
const totalVotes = computed(() => fakeVotes.value + realVotes.value);

const realPercentage = computed(() => {
  const total = totalVotes.value;
  if (total === 0) return 0;
  return Math.round((realVotes.value / total) * 100);
});

const fakePercentage = computed(() => {
  const total = totalVotes.value;
  if (total === 0) return 0;
  return Math.round((fakeVotes.value / total) * 100);
});

const submitVote = async () => {
  if (!selectedVote.value) {
    notificationStore.addNotification('Please select your vote (Real or Fake)', 'error');
    return;
  }
  
  isSubmitting.value = true;

  try {
    // ตรวจสอบว่าเป็นข่าวที่บันทึกแล้ว (id > 0) หรือยัง (id < 0)
    if (props.news.id > 0) {
      // ส่งไปยัง API - ใช้ voterImage.value แทน
      const result = await newsStore.submitComment(props.news.id, {
        username: username.value,
        text: voterComment.value,
        image: voterImage.value,  // ✅ This now gets first image from array
        vote: selectedVote.value
      });

      if (result.success) {
        notificationStore.addNotification('Vote submitted successfully!', 'success');
        resetForm();
        scrollToComments();
      } else {
        notificationStore.addNotification(result.error || 'Failed to submit vote', 'error');
      }
    } else {
      // ใช้ local method สำหรับ unsaved news
      newsStore.addCommentToNews(
        props.news.id,
        username.value,
        voterComment.value,
        voterImage.value || null,  // ✅ This now gets first image from array
        selectedVote.value
      );
      notificationStore.addNotification('Vote added locally!', 'success');
      resetForm();
      scrollToComments();
    }
  } catch (error) {
    console.error('Error submitting vote:', error);
    notificationStore.addNotification('Failed to submit vote. Please try again.', 'error');
  } finally {
    isSubmitting.value = false;
  }
};

const scrollToComments = () => {
  setTimeout(() => {
    const commentsSection = document.querySelector('#comments-section');
    if (commentsSection) {
      commentsSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }, 500);
};

const resetForm = () => {
  selectedVote.value = null;
  voterComment.value = '';
  voterImages.value = [];  // ✅ Reset array
};
</script>