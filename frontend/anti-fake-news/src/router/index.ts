import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../views/HomePage.vue'
import NewsDetail from '../views/NewsDetail.vue'
import AddNews from '../views/AddNews.vue'


const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage,
    },
    {
      path: '/news/:id',
      name: 'news-detail',
      component: NewsDetail,
    },
    {
      path: '/add-news',
      name: 'addNews',
      component: AddNews,
      meta: { requiredRoles: ['ROLE_MEMBER', 'ROLE_ADMIN'] },
    },
  ],
})

export default router
