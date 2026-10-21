import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ROLES } from '@/utils/constants'
import HomeView from '@/views/public/HomeView.vue'
import AuthLayout from '@/layouts/AuthLayout.vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'

const routes = [
  { path: '/', name: 'home', component: HomeView },
  { path: '/forbidden', name: 'forbidden', component: () => import('@/views/public/ForbiddenView.vue') },
  {
    path: '/',
    component: AuthLayout,
    children: [
      { path: 'login', name: 'login', component: () => import('@/views/auth/LoginView.vue'), meta: { guestOnly: true } },
      { path: 'register', name: 'register', component: () => import('@/views/auth/RegisterView.vue'), meta: { guestOnly: true } },
    ],
  },
  {
    path: '/admin',
    component: DashboardLayout,
    meta: { requiresAuth: true, roles: [ROLES.ADMIN] },
    children: [
      { path: '', redirect: { name: 'admin-verification' } },
      { path: 'verification', name: 'admin-verification', component: () => import('@/views/admin/VerificationQueueView.vue') },
    ],
  },
  { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('@/views/public/NotFoundView.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Runs before every page change
router.beforeEach((to) => {
  const auth = useAuthStore()
  const needsAuth = to.matched.some((r) => r.meta.requiresAuth)
  const allowedRoles = to.matched.flatMap((r) => r.meta.roles ?? [])
  const guestOnly = to.matched.some((r) => r.meta.guestOnly)

  if (needsAuth && !auth.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (allowedRoles.length && !allowedRoles.includes(auth.role)) {
    return { name: 'forbidden' }
  }
  if (guestOnly && auth.isAuthenticated) {
    return auth.homeRoute
  }
})

export default router