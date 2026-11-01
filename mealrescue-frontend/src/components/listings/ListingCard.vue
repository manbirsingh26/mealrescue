<script setup>
import { categoryLabel, formatDateTime, formatPrice } from '@/utils/formatters'

defineProps({ listing: { type: Object, required: true } })
</script>

<template>
  <article class="listing-card">
    <span class="category">{{ categoryLabel(listing.category) }}</span>
    <h3>
      <router-link :to="`/listings/${listing.id}`">{{ listing.title }}</router-link>
    </h3>
    <p class="business">{{ listing.businessName }}</p>
    <p class="line">
      <span class="price">{{ formatPrice(listing.price) }}</span>
      <span class="left">{{ listing.quantityRemaining }} left</span>
    </p>
    <p class="when">
      Pickup {{ formatDateTime(listing.pickupStart) }} to {{ formatDateTime(listing.pickupEnd) }}
    </p>
  </article>
</template>

<style scoped>
.listing-card {
  background: #fff;
  border: 1px solid #d9dfda;
  border-radius: 10px;
  padding: 1rem 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}
.category {
  align-self: flex-start;
  padding: 0.1rem 0.6rem;
  border-radius: 999px;
  background: #e4f1ea;
  color: #1f5c3f;
  font-size: 0.8rem;
  font-weight: 600;
}
h3 {
  margin: 0.35rem 0 0;
}
h3 a {
  color: #1d2521;
  text-decoration: none;
}
h3 a:hover {
  color: #2f6b4f;
  text-decoration: underline;
}
p {
  margin: 0;
}
.business,
.when {
  color: #5d6a63;
  font-size: 0.95rem;
}
.line {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin: 0.4rem 0;
}
.price {
  padding: 0.1rem 0.6rem;
  border-radius: 8px;
  background: #fbf0d6;
  color: #7a5200;
  font-weight: 700;
  font-size: 1.15rem;
}
.left {
  color: #1f5c3f;
  font-weight: 600;
}
</style>