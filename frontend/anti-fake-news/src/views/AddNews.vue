<template>
  <!-- Toast Notification Component -->
  <ToastNotification />

  <div class="container mx-auto p-4 max-w-4xl">
    <!-- Back to Home Button (matching news detail style) -->
    <router-link
      to="/"
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

    <!-- Main Content Card (matching news detail style) -->
    <div class="bg-white shadow-lg rounded-xl overflow-hidden">
      <div class="p-6">
        <!-- Header (matching news detail style) -->
        <h1 class="text-4xl font-extrabold mb-4 text-gray-900">Add New News</h1>
        
        <div class="flex items-center mb-6 space-x-4 text-sm text-gray-600">
          <span class="bg-blue-600 text-white font-bold px-3 py-1 rounded-full uppercase">
            Draft
          </span>
          <p>Date: <span class="font-semibold">{{ new Date().toLocaleDateString() }}</span></p>
        </div>

        <!-- Form -->
        <form @submit.prevent="handleAddNews" class="space-y-8">

          <!-- Reporter -->
          <div class="group">
            <label for="reporter" class="block text-lg font-semibold text-gray-900 mb-3">
              Reporter
            </label>
            <input 
              v-model="formData.reporter" 
              type="text" 
              id="reporter" 
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 text-gray-800 text-lg" 
              placeholder="enter reporter name"
              required
            >
          </div>
          
          <!-- Topic -->
          <div class="group">
            <label for="topic" class="block text-lg font-semibold text-gray-900 mb-3">
              News Title
            </label>
            <input 
              v-model="formData.topic" 
              type="text" 
              id="topic" 
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 text-gray-800 text-lg" 
              placeholder="enter an interesting news title..."
              required
            >
          </div>

          <!-- Short Detail -->
          <div class="group">
            <label for="shortDetail" class="block text-lg font-semibold text-gray-900 mb-3">
              Short Description
            </label>
            <textarea 
              v-model="formData.shortDetail" 
              id="shortDetail" 
              rows="4" 
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 text-gray-800 text-lg leading-relaxed resize-none" 
              placeholder="brief summary..."
              required
            ></textarea>
          </div>

          <!-- Full Detail -->
          <div class="group">
            <label for="fullDetail" class="block text-lg font-semibold text-gray-900 mb-3">
              Full News Content
            </label>
            <textarea 
              v-model="formData.fullDetail" 
              id="fullDetail" 
              rows="8" 
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 text-gray-800 text-lg leading-relaxed resize-none" 
              placeholder="write the complete news content here..."
              required
            ></textarea>
          </div>

          <!-- Image URL -->
          <div class="group">
            <label for="image" class="block text-lg font-semibold text-gray-900 mb-3">
              Image URL
            </label>
            <input 
              v-model="formData.image" 
              type="url" 
              id="image" 
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 text-gray-800 text-lg" 
              placeholder="https://example.com/image.jpg" 
              required
            >
          </div>

          <!-- Image Preview (if URL is provided) -->
          <div v-if="formData.image && isValidImageUrl" class="group">
            <label class="block text-lg font-semibold text-gray-900 mb-3">
              Image Preview
            </label>
            <img
              :src="formData.image"
              alt="News image preview"
              class="w-full h-auto rounded-lg shadow-md"
              @load="handleImageLoad"
              @error="handleImageError"
            >
          </div>

          <!-- Submit Button Section -->
          <div class="border-t border-gray-200 pt-8">
            <div class="flex justify-end space-x-4">
              <router-link
                to="/"
                class="px-6 py-3 border border-gray-300 rounded-lg text-gray-700 bg-white hover:bg-gray-50 transition-colors duration-200 font-medium"
              >
                Cancel
              </router-link>
              <button 
                type="submit"
                :disabled="isSubmitting || !canSubmit"
                class="px-8 py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition-all duration-200 transform hover:scale-105 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 shadow-lg hover:shadow-xl disabled:opacity-60 disabled:cursor-not-allowed"
                :class="{ 'pointer-events-none': isSubmitting || !canSubmit }"
              >
                <span class="flex items-center space-x-2">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                  </svg>
                  <span>Add News</span>
                </span>
              </button>
            </div>
            <p v-if="!canSubmit" class="mt-2 text-sm text-red-600">
              Only members or admins can publish news. Please sign in with the appropriate role.
            </p>
          </div>

        </form>
      </div>
    </div>

    <!-- Footer Note -->
    <div class="text-center mt-6">
      <p class="text-sm text-gray-500">
        Please double-check all information before adding the news
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useNewsStore } from '../stores/news';
import { useNotificationStore } from '../stores/notification';
import ToastNotification from '../components/ToastNotification.vue';
import type { CreateNewsPayload } from '../stores/news';
import { useAuthStore } from '../stores/auth';
import axios from 'axios';

const router = useRouter();
const newsStore = useNewsStore();
const notificationStore = useNotificationStore();
const authStore = useAuthStore();

const formData = reactive({
  topic: '',
  shortDetail: '',
  fullDetail: '',
  image: '',
  reporter: '',
});

const isSubmitting = ref(false);
const canSubmit = computed(() => authStore.roles.includes('ROLE_MEMBER') || authStore.roles.includes('ROLE_ADMIN'));

const isValidImageUrl = computed(() => {
  if (!formData.image) return false;
  try {
    const url = new URL(formData.image);
    return url.protocol === 'http:' || url.protocol === 'https:';
  } catch {
    return false;
  }
});

const handleImageLoad = () => {
  // Image loaded successfully
};

const handleImageError = () => {
  console.warn('Failed to load preview image');
};

const handleAddNews = async () => {
  if (isSubmitting.value) {
    return;
  }

  if (!authStore.isAuthenticated || !canSubmit.value) {
    notificationStore.addNotification('You need a member or admin account to add news.', 'error');
    return;
  }

  isSubmitting.value = true;

  try {
    const newNews: CreateNewsPayload = {
      ...formData,
      dateTime: new Date().toISOString(),
    };

    await newsStore.createNews(newNews);
    
    // Show success notification
    notificationStore.addNotification('News added successfully.', 'success');
    
    // Reset form
    Object.keys(formData).forEach(key => {
      formData[key as keyof typeof formData] = '';
    });
    
    // Navigate to home after a short delay
    setTimeout(() => {
      router.push('/');
    }, 700);
    
  } catch (error) {
    console.error('Error adding news:', error);
    if (axios.isAxiosError(error)) {
      if (error.response?.status === 403) {
        notificationStore.addNotification('You are not authorized to add news.', 'error');
      } else if (error.response?.status === 401) {
        notificationStore.addNotification('Session expired. Please log in again.', 'error');
      } else {
        notificationStore.addNotification('Failed to add news. Please try again.', 'error');
      }
    } else {
      notificationStore.addNotification('Failed to add news. Please try again.', 'error');
    }
  } finally {
    isSubmitting.value = false;
  }
};
</script>
