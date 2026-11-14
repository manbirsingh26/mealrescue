import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ROLES } from '@/utils/constants'
import HomeView from '@/views/public/HomeView.vue'
import AuthLayout from '@/layouts/AuthLayout.vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'

const routes = [
  { path: '/', name: 'home', component: HomeView },
  { path: '/forbidden', name: 'forbidden', component: () => import('@/views/public/ForbiddenView.vue') },
  {
    path: '/',
    component: DefaultLayout,
    children: [
      { path: 'listings', name: 'listings', component: () => import('@/views/customer/BrowseListingsView.vue') },
      { path: 'contact', name: 'contact', component: () => import('@/views/public/ContactView.vue') },
      {
        path: 'reservations',
        name: 'my-reservations',
        component: () => import('@/views/customer/MyReservationsView.vue'),
        meta: { requiresAuth: true, roles: ['CUSTOMER', 'ORGANIZATION'] },
      },
      { path: 'listings/:id', name: 'listing-details', component: () => import('@/views/customer/ListingDetailsView.vue'), props: true },
      {
        path: 'profile',
        name: 'profile',
        component: () => import('@/views/profile/ProfileView.vue'),
        meta: { requiresAuth: true },
      },
    ],
  },
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
    component: DefaultLayout,
    meta: { requiresAuth: true, roles: [ROLES.ADMIN] },
    children: [
      { path: '', redirect: { name: 'admin-dashboard' } },
      { path: 'verification', name: 'admin-verification', component: () => import('@/views/admin/VerificationQueueView.vue') },
      { path: 'reports', name: 'admin-reports', component: () => import('@/views/admin/ReportsView.vue') },
      { path: 'users', name: 'admin-users', component: () => import('@/views/admin/UsersView.vue') },
      { path: 'listings', name: 'admin-listings', component: () => import('@/views/admin/ListingsView.vue') },
      { path: 'dashboard', name: 'admin-dashboard', component: () => import('@/views/admin/DashboardView.vue') },
    ],
  },
    {
    path: '/business',
    component: DefaultLayout,
    meta: { requiresAuth: true, roles: [ROLES.BUSINESS] },
    children: [
      { path: '', redirect: { name: 'business-listings' } },
      { path: 'listings', name: 'business-listings', component: () => import('@/views/business/MyListingsView.vue') },
      { path: 'reservations', name: 'business-reservations', component: () => import('@/views/business/BusinessReservationsView.vue') },
      { path: 'listings/new', name: 'business-listing-new', component: () => import('@/views/business/ListingEditView.vue') },
      { path: 'listings/:id/edit', name: 'business-listing-edit', component: () => import('@/views/business/ListingEditView.vue'), props: true },
      { path: 'reviews', name: 'business-reviews', component: () => import('@/views/business/BusinessReviewsView.vue') },
      
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