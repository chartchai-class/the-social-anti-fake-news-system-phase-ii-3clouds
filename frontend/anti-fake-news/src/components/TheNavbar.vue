<template>
  <nav class="bg-white shadow-lg">
    <div class="container mx-auto px-4">
      <div class="flex justify-between items-center py-4 relative">
        <router-link to="/" class="flex items-center space-x-3" @click="refreshPage">
          <img src="/logo.png" alt="3Clouds News Logo" class="h-12 w-auto" />
        </router-link>

        <router-link to="/" class="absolute left-1/2 -translate-x-1/2" @click="refreshPage">
          <span
            class="text-xl font-extrabold text-my-blue hover:text-blue-400 transition-colors duration-200 sm:text-2xl md:text-3xl lg:text-4xl"
          >
            3Clouds News
          </span>
        </router-link>

        <router-link 
          to="/add-news" 
          class="flex items-center space-x-1 px-2 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors duration-200 text-xs sm:text-sm sm:space-x-2 sm:px-3 md:px-4 md:py-2"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 sm:h-5 sm:w-5" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11h-4v4h-2v-4H7v-2h4V7h2v4h4v2z"/>
          </svg>
          <span class="font-medium hidden xs:inline sm:inline">Add News</span>
        </router-link>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const refreshPage = () => {
  if (router.currentRoute.value.path === '/') {
    window.location.reload()
  } else {
    router.push('/')
  }
}
</script>