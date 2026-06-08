import { createRouter, createWebHistory } from 'vue-router'

import MainLayout from '@/layouts/MainLayout.vue'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/demands',
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { public: true },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { public: true },
    },
    {
      path: '/',
      component: MainLayout,
      children: [
        {
          path: 'demands',
          name: 'demands',
          component: () => import('@/views/DemandPlazaView.vue'),
        },
        {
          path: 'demands/create',
          name: 'demand-create',
          component: () => import('@/views/DemandCreateView.vue'),
        },
        {
          path: 'demands/mine',
          name: 'my-demands',
          component: () => import('@/views/MyDemandsView.vue'),
        },
        {
          path: 'demands/:id',
          name: 'demand-detail',
          component: () => import('@/views/DemandDetailView.vue'),
        },
        {
          path: 'messages',
          name: 'messages',
          component: () => import('@/views/ConversationListView.vue'),
        },
        {
          path: 'messages/:conversationId',
          name: 'chat',
          component: () => import('@/views/ChatView.vue'),
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('@/views/ProfileView.vue'),
        },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const authStore = useAuthStore()

  if (!to.meta.public && !authStore.isAuthenticated) {
    return {
      path: '/login',
      query: { redirect: to.fullPath },
    }
  }

  if (
    to.meta.public &&
    authStore.isAuthenticated &&
    (to.name === 'login' || to.name === 'register')
  ) {
    return '/demands'
  }

  return true
})

export default router
