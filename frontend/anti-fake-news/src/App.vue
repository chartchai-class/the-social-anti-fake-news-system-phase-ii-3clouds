<template>
  <SpeedInsights />
  <div class="bg-gray-100 min-h-screen font-sans antialiased text-gray-800">
    <!-- Loading Progress Bar -->
    <LoadingProgressBar />
    
    <Navbar />
    <RouterView />
    <ToastNotification />
    <ScrollButtons />
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router';
import Navbar from './components/TheNavbar.vue';
import ToastNotification from './components/ToastNotification.vue';
import ScrollButtons from './components/ScrollButtons.vue';
import LoadingProgressBar from './components/LoadingProgressBar.vue';
import { SpeedInsights } from "@vercel/speed-insights/vue"
</script>