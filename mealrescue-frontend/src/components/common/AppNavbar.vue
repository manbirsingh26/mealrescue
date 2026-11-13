<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AppLogo from '@/components/common/AppLogo.vue'

const auth = useAuthStore()
const router = useRouter()

const LISTINGS = { to: '/listings', label: 'Current listings' }
const CONTACT = { to: '/contact', label: 'Contact us' }

const LINKS_BY_ROLE = {
  CUSTOMER: [LISTINGS, { to: '/reservations', label: 'My reservations' }],
  ORGANIZATION: [LISTINGS, { to: '/reservations', label: 'My reservations' }],
  BUSINESS: [
    { to: '/business/listings', label: 'My listings' },
    { to: '/business/listings/new', label: 'New listing' },
    { to: '/business/reservations', label: 'Reservations' },
    { to: '/business/reviews', label: 'Reviews' },
  ],
  ADMIN: [
  { to: '/admin/dashboard', label: 'Dashboard' },
   { to: '/admin/verification', label: 'Verification queue' },
   { to: '/admin/users', label: 'Users' },
   { to: '/admin/listings', label: 'Listings' },
   { to: '/admin/reports', label: 'Reports' },
  ],
}

// Same links on every page; Contact us is always last
const links = computed(() => {
  const own = auth.isAuthenticated ? LINKS_BY_ROLE[auth.role] || [] : [LISTINGS]
  return [...own, CONTACT]
})

function logout() {
  auth.logout()
  router.push('/')
}
</script>

<template>
  <header class="nav">
    <div class="bar">
      <router-link to="/" aria-label="MealRescue home"><AppLogo /></router-link>

      <nav class="links" aria-label="Main">
        <template v-for="(l, i) in links" :key="l.to">
          <span v-if="i > 0" class="sep" aria-hidden="true">|</span>
          <router-link :to="l.to">{{ l.label }}</router-link>
        </template>
      </nav>

      <div class="actions">
        <template v-if="auth.isAuthenticated">
          <span class="who">{{ auth.user.name }}</span>
          <button class="btn btn-light" @click="logout">Log out</button>
        </template>
        <template v-else>
          <router-link to="/login" class="btn btn-light">Log in</router-link>
          <router-link to="/register" class="btn">Register</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<style scoped>
.nav {
  background: #fff;
  border-bottom: 1px solid #d9dfda;
}
.bar {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 1rem;
  min-height: 64px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.5rem 1.5rem;
}
.links {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.6rem;
  flex: 1;
}
.links a {
  color: #1d2521;
  text-decoration: none;
  font-weight: 600;
  white-space: nowrap;
}
.links a:hover {
  color: #2f6b4f;
}
/* the page you're on */
.links a.router-link-exact-active {
  color: #2f6b4f;
  text-decoration: underline;
  text-decoration-thickness: 2px;
  text-underline-offset: 6px;
}
.sep {
  color: #b6c0ba;
}
.actions {
  display: flex;
  align-items: center;
  gap: 0.6rem;
}
.actions .btn {
  white-space: nowrap;
}
.who {
  color: #5d6a63;
}
</style>