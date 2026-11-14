<script setup>
const year = new Date().getFullYear()
</script>

<template>
  <footer class="foot">
    <div class="inner">
      <span>&copy; {{ year }} MealRescue</span>
      <router-link to="/contact">Contact us</router-link>
    </div>
  </footer>
</template>

<style scoped>
.foot {
  border-top: 1px solid #d9dfda;
  background: #fff;
  color: #5d6a63;
  font-size: 0.9rem;
}
.inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 1.25rem 1rem;
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
  
}

a {
  color: #2f6b4f;
}
</style>