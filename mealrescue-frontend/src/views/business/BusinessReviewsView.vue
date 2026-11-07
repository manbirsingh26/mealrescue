<script setup>
import { onMounted, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { getBusinessReviews } from '@/api/reviews.api'
import { errorMessage } from '@/api/http'
import ReviewList from '@/components/reviews/ReviewList.vue'

const auth = useAuthStore()
const summary = ref(null)
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    const { data } = await getBusinessReviews(auth.user.id)
    summary.value = data
  } catch (e) {
    error.value = errorMessage(e, 'Could not load your reviews.')
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <h1>Reviews</h1>
  <p class="intro">What customers and organizations said after picking up your food.</p>

  <p v-if="loading">Loading...</p>
  <p v-else-if="error" class="error" role="alert">{{ error }}</p>
  <section v-else class="box">
    <ReviewList :summary="summary" />
  </section>
</template>

<style scoped>
.intro {
  color: #5d6a63;
}
.box {
  background: #fff;
  border: 1px solid #d9dfda;
  border-radius: 10px;
  padding: 1.25rem 1.5rem;
  max-width: 720px;
}
</style>