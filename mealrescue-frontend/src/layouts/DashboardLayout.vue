<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ROLES } from '@/utils/constants'

const auth = useAuthStore()
const router = useRouter()

const links = computed(() =>
  auth.role === ROLES.ADMIN
    ? [{ to: '/admin/verification', label: 'Verification queue' }]
    : [
        { to: '/business/listings', label: 'My listings' },
        { to: '/business/listings/new', label: 'New listing' },
      ],
)

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <div class="dash">
    <aside class="side">
      <router-link to="/" class="brand">MealRescue</router-link>
            <nav>
        <router-link v-for="l in links" :key="l.to" :to="l.to" exact-active-class="active">
          {{ l.label }}
        </router-link>
      </nav>
      <div class="side-foot">
        <span>{{ auth.user?.name }}</span>
        <button class="btn" @click="logout">Log out</button>
      </div>
    </aside>
    <main class="content">
      <router-view :key="$route.fullPath" />
    </main>
  </div>
</template>

<style scoped>
.dash {
  display: grid;
  grid-template-columns: 240px 1fr;
  min-height: 100vh;
}
.side {
  background: #fff;
  border-right: 1px solid #d9dfda;
  padding: 1.25rem 1rem;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}
.brand {
  font-size: 1.25rem;
  font-weight: 700;
  text-decoration: none;
  color: #2f6b4f;
}
nav {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}
nav a {
  padding: 0.5rem 0.75rem;
  border-radius: 8px;
  text-decoration: none;
  color: #1d2521;
}
nav a.active {
  background: #2f6b4f;
  color: #fff;
}
.side-foot {
  margin-top: auto;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
.content {
  padding: 1.5rem;
}
</style>