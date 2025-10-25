<template>
  <div class="bg-gray-50 p-4 rounded-lg mb-4 border">
    <div class="flex items-center justify-between mb-3">
      <div class="flex items-center">
        <div
          class="w-8 h-8 rounded-full flex items-center justify-center mr-3 border border-gray-300 shadow-sm"
          :style="{ 'background-color': getProfileColor(comment.user) }"
        >
          <span class="text-sm font-bold text-gray-800">{{ comment.user.charAt(0).toUpperCase() }}</span>
        </div>
        <p class="font-bold text-gray-800">{{ comment.user }}</p>
      </div>
      <div class="text-sm text-gray-500">
        <span>{{ new Date(comment.time).toLocaleDateString() }}</span>
        <span class="mx-2">|</span>
        <span>{{ new Date(comment.time).toLocaleTimeString() }}</span>
      </div>
    </div>
    <div class="ml-11">
      <p v-if="comment.text" class="text-gray-700 mb-2">{{ comment.text }}</p>

      <div class="flex items-center text-sm mb-1">
        <span class="text-gray-600">Vote : </span>
        <span
          :class="{
            'text-green-600': comment.vote === 'real',
            'text-red-600': comment.vote === 'fake'
          }"
          class="font-semibold ml-1"
        >
          {{ comment.vote === 'real' ? 'Real' : 'Fake' }}
        </span>
      </div>

      <img
        v-if="comment.image"
        :src="comment.image"
        alt="Comment Image"
        class="mt-4 w-full h-auto rounded-lg max-h-64 object-cover cursor-pointer"
        @click="handleImageClick"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue';
import type { Comment } from '../stores/news';

const props = defineProps<{
  comment: Comment;
}>();

const emit = defineEmits(['show-full-image']);

// ฟังก์ชันสำหรับสร้างสีพาสเทลที่สดใสจากชื่อผู้ใช้
const getProfileColor = (username: string) => {
  let hash = 0;
  for (let i = 0; i < username.length; i++) {
    hash = username.charCodeAt(i) + ((hash << 5) - hash);
  }
  const hue = hash % 360;
  const saturation = 70;
  const lightness = 85;
  return `hsl(${hue}, ${saturation}%, ${lightness}%)`;
};

const handleImageClick = () => {
  if (props.comment.image) {
    emit('show-full-image', props.comment.image);
  }
};
</script>