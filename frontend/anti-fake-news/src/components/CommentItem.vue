<template>
  <div v-if="comment" class="bg-gray-50 p-4 rounded-lg mb-4 border">
    <div class="flex items-center justify-between mb-3">
      <div class="flex items-center">
        <img
          :src="getProfileImage(comment.username)"
          :alt="`${comment.username}'s profile`"
          class="w-8 h-8 rounded-full mr-3 border border-gray-300 shadow-sm object-cover"
          @error="handleProfileImageError"
        />
        <p class="font-bold text-gray-800">
          {{ comment.username || 'Anonymous' }}
        </p>
      </div>
      <div v-if="comment.time" class="text-sm text-gray-500">
        <span>{{ formatDate(comment.time) }}</span>
        <span class="mx-2">|</span>
        <span>{{ formatTime(comment.time) }}</span>
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
        class="mt-4 w-full h-auto rounded-lg max-h-64 object-cover cursor-pointer hover:opacity-90 transition-opacity"
        @click="handleImageClick"
        @error="handleImageError"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue';
import { useAuthStore } from '../stores/auth';
import type { Comment } from '../stores/news';

const props = defineProps<{
  comment: Comment;
}>();

const emit = defineEmits(['show-full-image']);
const authStore = useAuthStore();

const defaultProfileImage = 'https://cdn-icons-png.flaticon.com/512/3177/3177440.png';

// ฟังก์ชันดึงรูปโปรไฟล์ตาม username
const getProfileImage = (username: string): string => {
  // ถ้าเป็น user ปัจจุบันที่ login อยู่
  if (authStore.user?.username === username && authStore.userProfileImage) {
    return authStore.userProfileImage;
  }
  
  // TODO: ในอนาคตอาจจะดึงจาก API หรือ store ที่เก็บ profile ของ user อื่นๆ
  
  return defaultProfileImage;
};

const handleProfileImageError = (event: Event) => {
  const img = event.target as HTMLImageElement;
  img.src = defaultProfileImage;
};

const formatDate = (dateString: string): string => {
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('th-TH', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  } catch (error) {
    return '';
  }
};

const formatTime = (dateString: string): string => {
  try {
    const date = new Date(dateString);
    return date.toLocaleTimeString('th-TH', {
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (error) {
    return '';
  }
};

const handleImageClick = () => {
  if (props.comment.image) {
    emit('show-full-image', props.comment.image);
  }
};

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement;
  img.style.display = 'none';
  console.error('Failed to load image:', props.comment.image);
};
</script>