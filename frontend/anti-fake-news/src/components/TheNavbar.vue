<template>
  <nav class="bg-white shadow-lg">
    <div class="container mx-auto px-4">
      <div class="flex justify-between items-center py-4 relative">
        <router-link to="/" class="flex items-center space-x-3" @click="refreshPage">
          <img src="/logo.png" alt="3Clouds News Logo" class="h-12 w-auto" />
        </router-link>

        <router-link to="/" class="absolute left-1/2 -translate-x-1/2" @click="refreshPage">
          <span
            class="text-xl font-extrabold text-my-blue hover:text-blue-400 transition-colors duration-200 sm:text-2xl md:text-3xl lg:text-4xl">
            3Clouds News
          </span>
        </router-link>

        <div class="flex items-center space-x-2 sm:space-x-4">

          <router-link v-if="canCreateNews" to="/add-news"
            class="flex items-center space-x-1 px-2 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors duration-200 text-xs sm:text-sm sm:space-x-2 sm:px-3 md:px-4 md:py-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 sm:h-5 sm:w-5" viewBox="0 0 24 24"
              fill="currentColor">
              <path
                d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11h-4v4h-2v-4H7v-2h4V7h2v4h4v2z" />
            </svg>
            <span class="font-medium hidden xs:inline sm:inline">Add News</span>
          </router-link>

          <router-link v-if="!isLoggedIn" to="/signin" class="px-4 py-2 text-sm sm:text-base font-semibold 
         bg-blue-500 text-white rounded-lg 
         hover:bg-blue-700 transition-colors duration-200">
            Sign in
          </router-link>

          <div v-else class="flex items-center space-x-2 sm:space-x-4">

            <!-- <router-link v-if="isAdmin" to="/admin/users"
              class="px-3 py-2 text-sm sm:text-base font-medium text-red-600 bg-red-100 rounded-lg hover:bg-red-200 transition-colors">
              Admin
            </router-link> -->

            <span class="text-base font-medium text-gray-700 hidden lg:inline">
              Hi, {{ userFirstName }}
            </span>

            <button @click="handleLogout"
              class="p-2 text-gray-700 bg-gray-100 rounded-full hover:bg-gray-200 transition-colors" title="Logout">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 48 48">
                <path fill="none" stroke="#000000" stroke-linecap="round" stroke-linejoin="round" stroke-width="4"
                  d="M23.992 6H6v36h18m9-9l9-9l-9-9m-17 8.992h26" />
              </svg>

            </button>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore, type Role } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const creatorRoles: Role[] = ['ROLE_MEMBER', 'ROLE_ADMIN']
const canCreateNews = computed(() => authStore.hasAnyRole(creatorRoles))

const isLoggedIn = computed(() => authStore.isAuthenticated)
const userFirstName = computed(() => authStore.user?.firstname || authStore.user?.username || 'User')

const refreshPage = () => {
  if (router.currentRoute.value.path === '/') {
    window.location.reload()
  } else {
    router.push('/')
  }
}

const handleLogout = () => {
  authStore.logout()
  refreshPage()
}

onMounted(() => {
  authStore.hydrateFromStorage()
})
</script>