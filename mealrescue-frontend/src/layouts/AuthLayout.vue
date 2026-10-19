<template>
  <div class="auth-shell">
    <router-link to="/" class="brand">MealRescue</router-link>
    <div class="auth-card">
      <router-view />
    </div>
  </div>
</template>

<style scoped>
.auth-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 2rem 1rem;
}
.brand {
  font-size: 1.5rem;
  font-weight: 700;
  text-decoration: none;
  color: #2f6b4f;
}
.auth-card {
  width: 100%;
  max-width: 440px;
  background: #fff;
  border: 1px solid #d9dfda;
  border-radius: 8px;
  padding: 1.25rem 1.5rem;
}
</style>