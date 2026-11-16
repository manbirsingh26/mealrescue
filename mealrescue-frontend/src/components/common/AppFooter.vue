<script setup>
const year = new Date().getFullYear()

function backToTop() {
  // respect users who turned off animations in their system settings
  const reduce = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  window.scrollTo({ top: 0, behavior: reduce ? 'auto' : 'smooth' })
}
</script>

<template>
  <footer class="foot">
    <div class="inner">
      <span>&copy; {{ year }} MealRescue</span>
      <nav class="links" aria-label="Footer">
        <router-link to="/contact">Contact us</router-link>
        <span class="sep" aria-hidden="true">|</span>
        <button type="button" class="top" @click="backToTop">Back to top &uarr;</button>
      </nav>
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
  max-width: var(--page-width);
  margin: 0 auto;
  padding: 1.25rem 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
  
}
.links {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  
}
.sep {
  color: #b6c0ba;
}
a,
.top {
  color: #2f6b4f;
  text-decoration: none;
}
a:hover,
.top:hover {
  text-decoration: underline;
}
.top {
  background: none;
  border: none;
  padding: 0;
  font: inherit;
  cursor: pointer;
}
.top:focus-visible,
a:focus-visible {
  outline: 2px solid #2f6b4f;
  outline-offset: 2px;
  border-radius: 2px;
}
</style>