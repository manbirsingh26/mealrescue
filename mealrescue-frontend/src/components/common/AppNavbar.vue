<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AppLogo from '@/components/common/AppLogo.vue'

const auth = useAuthStore()
const router = useRouter()

// Businesses and admins have their own area; customers just use Browse food
/*const areaLink = computed(() =>
  ['BUSINESS', 'ADMIN'].includes(auth.role) ? auth.homeRoute : null,
)*/

const LINKS_BY_ROLE = {
  CUSTOMER: [{ to: '/listings', label: 'Browse food' },
    { to: '/reservations', label: 'My reservations' },
  ],
  ORGANIZATION: [{ to: '/listings', label: 'Browse food' },
    { to: '/reservations', label: 'My reservations' },
  ],
  BUSINESS: [
    { to: '/business/listings', label: 'My listings' },
    { to: '/business/listings/new', label: 'New listing' },
    { to: '/business/reservations', label: 'Reservations' },
  ],
  ADMIN: [{ to: '/admin/verification', label: 'Verification queue' }],
}

const links = computed(() =>
  auth.isAuthenticated
    ? LINKS_BY_ROLE[auth.role] || []
    : [{ to: '/listings', label: 'Browse food' }],
)

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
        <router-link v-for="l in links" :key="l.to" :to="l.to">{{ l.label }}</router-link>
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
  gap: 1rem;
  flex: 1;
}
.links a {
  text-decoration: none;
  color: #1d2521;
}
.links a.router-link-exact-active {
  color: #2f6b4f;
  font-weight: 600;
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