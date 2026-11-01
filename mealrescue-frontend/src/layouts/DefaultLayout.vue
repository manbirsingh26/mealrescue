<script setup>
import AppNavbar from '@/components/common/AppNavbar.vue'
</script>

<template>
  <div class="shell">
    <AppNavbar />
    <main class="content">
      <router-view :key="$route.fullPath" />
    </main>
  </div>
</template>

<style scoped>
.shell {
  min-height: 100vh;
  background: #f6f7f4;
  color: #1d2521;
}
.content {
  max-width: 1100px;
  margin: 0 auto;
  padding: 1.5rem 1rem 3rem;
}
</style>