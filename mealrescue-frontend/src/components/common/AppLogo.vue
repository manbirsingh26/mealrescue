<script setup>
defineProps({
  size: { type: Number, default: 36 },
  showName: { type: Boolean, default: true },
})
</script>

<template>
  <span class="logo">
    <svg :width="size" :height="size" viewBox="0 0 40 40" aria-hidden="true" focusable="false">
      <rect width="40" height="40" rx="10" fill="#2f6b4f" />
      <!-- bowl -->
      <path d="M9 22h22a11 11 0 0 1-22 0z" fill="#f6f7f4" />
      <!-- leaf -->
      <path d="M20 20C14 19 13 12 17 8c6 1 8 7 3 12z" fill="#e0a43a" />
    </svg>
    <span v-if="showName" class="name">Meal<span class="accent">Rescue</span></span>
    <span v-else class="sr-only">MealRescue</span>
  </span>
</template>

<style scoped>
.logo {
  display: inline-flex;
  align-items: center;
  gap: 0.6rem;
}
.name {
  font-size: 1.35rem;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: #1d2521;
}
.accent {
  color: #2f6b4f;
}
@media (max-width: 420px) {
  .logo {
    gap: 0.4rem;
  }
  .name {
    font-size: 1.1rem;
  }
}
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
}
</style>