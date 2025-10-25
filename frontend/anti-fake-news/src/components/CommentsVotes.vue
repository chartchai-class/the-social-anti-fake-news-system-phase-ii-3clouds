<template>
  <div ref="commentsSection">
    <VoteSummary :vote-summary="voteSummary" />

    <h3 class="text-2xl font-bold mb-4 text-gray-800">Comment :</h3>

    <div class="flex flex-col sm:flex-row items-center justify-end gap-4 mb-4">
      <div class="flex items-center gap-2 w-full sm:w-auto">
        <label for="filter-select" class="text-gray-700 font-medium whitespace-nowrap">Filter by:</label>
        <div class="relative w-full">
          <select
            id="filter-select"
            v-model="filterOption"
            class="block w-full appearance-none rounded-md border border-gray-300 bg-white py-2 pl-3 pr-8 text-sm text-gray-700 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          >
            <option value="all">All Comments</option>
            <option value="real">Real</option>
            <option value="fake">Fake</option>
          </select>
          <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-2 text-gray-700">
            <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
            </svg>
          </div>
        </div>
      </div>

      <div class="flex items-center gap-2 w-full sm:w-auto">
        <label for="sort-select" class="text-gray-700 font-medium whitespace-nowrap">Sort by:</label>
        <div class="relative w-full">
          <select
            id="sort-select"
            v-model="sortOption"
            class="block w-full appearance-none rounded-md border border-gray-300 bg-white py-2 pl-3 pr-8 text-sm text-gray-700 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          >
            <option value="newest">Newest</option>
            <option value="oldest">Oldest</option>
          </select>
          <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-2 text-gray-700">
            <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <div v-if="filteredAndSortedComments.length > 0">
      <CommentItem
        v-for="(comment, index) in paginatedComments"
        :key="`${comment.id}-${index}-${commentPage}`"
        :comment="comment"
        @show-full-image="showFullImage"
      />
      
      <Pagination
        :total-items="filteredAndSortedComments.length"
        :items-per-page="commentsPerPage"
        :current-page="commentPage"
        @page-changed="commentPage = $event"
        class="mt-6"
      />
    </div>
    <p v-else class="text-gray-500 italic">No comments match the filter.</p>

    <div v-if="showImagePopup" class="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50 p-4" @click="closePopup">
      <div class="relative max-w-5xl max-h-full" @click.stop>
        <button
          @click="closePopup"
          class="absolute -top-10 right-0 text-white text-4xl font-light hover:text-gray-300 transition-colors"
          aria-label="Close"
        >
          &times;
        </button>
        <img v-if="selectedImage" :src="selectedImage" alt="Full size comment image" class="max-w-full max-h-screen-75 object-contain rounded-lg shadow-xl" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, defineProps, nextTick } from 'vue';
import Pagination from './BasePagination.vue';
import VoteSummary from './VoteSummary.vue';
import CommentItem from './CommentItem.vue';
import type { Comment, VoteSummary as VoteSummaryType } from '../stores/news';

const props = defineProps<{
  comments: Comment[];
  voteSummary: VoteSummaryType;
}>();

const commentPage = ref<number>(1);
const commentsPerPage = ref<number>(3);
const filterOption = ref<'all' | 'real' | 'fake'>('all');
const sortOption = ref<'newest' | 'oldest'>('newest');
const commentsSection = ref<HTMLElement | null>(null);

const showImagePopup = ref<boolean>(false);
const selectedImage = ref<string | null>(null);

// Functions to handle the pop-up
const showFullImage = (imageUrl: string) => {
  selectedImage.value = imageUrl;
  showImagePopup.value = true;
};

const closePopup = () => {
  showImagePopup.value = false;
  selectedImage.value = null;
};

// Watcher to scroll to the bottom of the page when the page changes
watch(commentPage, async () => {
  await nextTick(); // รอให้ DOM อัพเดท
  setTimeout(() => {
    window.scrollTo({ 
      top: document.body.scrollHeight, 
      behavior: 'smooth' 
    });
  }, 100);
});

// Watchers to reset the page to 1 when a filter or sort option changes
watch([filterOption, sortOption], () => {
  commentPage.value = 1;
});

// เพิ่ม watcher สำหรับ props.comments เพื่อรีเซ็ต pagination เมื่อข้อมูลเปลี่ยน
watch(() => props.comments, () => {
  // ตรวจสอบว่าหน้าปัจจุบันยังมีข้อมูลหรือไม่
  const totalPages = Math.ceil(filteredAndSortedComments.value.length / commentsPerPage.value);
  if (commentPage.value > totalPages && totalPages > 0) {
    commentPage.value = totalPages;
  }
}, { deep: true });

const commentsWithContent = computed(() =>
  props.comments.filter(
    (c) => (c.text && c.text.trim().length > 0) || (c.image && c.image.trim().length > 0)
  )
);

const filteredAndSortedComments = computed<Comment[]>(() => {
  let filteredComments = [...commentsWithContent.value]; // สร้าง array ใหม่

  if (filterOption.value !== 'all') {
    filteredComments = filteredComments.filter((c) => c.vote === filterOption.value);
  }

  const sortedComments = filteredComments.sort((a, b) => {
    const timeA = new Date(a.time).getTime();
    const timeB = new Date(b.time).getTime();
    if (sortOption.value === 'newest') {
      return timeB - timeA;
    } else {
      return timeA - timeB;
    }
  });

  return sortedComments;
});

const paginatedComments = computed<Comment[]>(() => {
  const start = (commentPage.value - 1) * commentsPerPage.value;
  const end = start + commentsPerPage.value;
  return filteredAndSortedComments.value.slice(start, end);
});
</script>