<script setup>
import StarRating from '@/components/reviews/StarRating.vue'
import { formatDateTime } from '@/utils/formatters'

defineProps({ summary: { type: Object, required: true } })
</script>

<template>
  <div class="summary">
    <template v-if="summary.reviewCount > 0">
      <StarRating :model-value="summary.averageRating" readonly size="1.4rem" />
      <strong>{{ summary.averageRating.toFixed(1) }}</strong>
      <span class="muted">
        ({{ summary.reviewCount }} {{ summary.reviewCount === 1 ? 'review' : 'reviews' }})
      </span>
    </template>
    <span v-else class="muted">No reviews yet.</span>
  </div>

  <ul v-if="summary.reviewCount > 0" class="list">
    <li v-for="r in summary.reviews" :key="r.id">
      <div class="head">
        <StarRating :model-value="r.rating" readonly size="1rem" />
        <strong>{{ r.customerName }}</strong>
        <span class="muted small">{{ formatDateTime(r.createdAt) }}</span>
      </div>
      <p class="muted small">{{ r.listingTitle }}</p>
      <p v-if="r.comment" class="comment">{{ r.comment }}</p>
    </li>
  </ul>
</template>

<style scoped>
.summary {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}
.list {
  list-style: none;
  padding: 0;
  margin: 1rem 0 0;
}
.list li {
  padding: 0.75rem 0;
  border-top: 1px solid #d9dfda;
}
.head {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}
p {
  margin: 0.25rem 0 0;
}
.comment {
  line-height: 1.5;
}
.muted {
  color: #5d6a63;
}
.small {
  font-size: 0.85rem;
}
</style>