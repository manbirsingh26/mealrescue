<script setup>
import AppLogo from '@/components/common/AppLogo.vue'
</script>

<template>
  <div class="auth-shell">
    <div class="top">
      <router-link to="/" class="btn btn-light">&larr; Back to home</router-link>
    </div>

    <main class="center">
      <div class="panel">
        <router-link to="/" class="home-link" aria-label="MealRescue home"><AppLogo /></router-link>
        <router-view />
      </div>
    </main>
  </div>
</template>

<style scoped>
.auth-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #1f5c3f url('@/assets/images/auth-bg.svg') center / cover no-repeat;
}
.top {
  padding: 1rem;
}
.center {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.5rem 1rem 2rem;
}
.panel {
  width: 100%;
  max-width: 440px;
  background: #fff;
  border-radius: 12px;
  padding: 1.5rem 1.75rem 1.75rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.18);
}
.home-link {
  display: flex;
  align-items: center;
  width: fit-content;
  margin-bottom: 0.75rem;
  text-decoration: none;
}
</style>