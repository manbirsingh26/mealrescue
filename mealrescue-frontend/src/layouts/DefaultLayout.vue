<script setup>
import AppNavbar from '@/components/common/AppNavbar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
</script>

<template>
  <div class="shell">
    <AppNavbar />
    <main class="content">
      <router-view :key="$route.fullPath" />
    </main>
    <AppFooter />
  </div>
</template>

<style scoped>
.shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f6f7f4;
  color: #1d2521;
}
.content {
  flex: 1;
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  padding: 1.5rem 1rem 3rem;
  box-sizing: border-box;
}
</style>