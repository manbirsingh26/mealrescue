<script setup>
import { onMounted, ref } from 'vue'
import { getListing } from '@/api/listings.api'
import { errorMessage } from '@/api/http'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { categoryLabel, formatDateTime, formatPrice } from '@/utils/formatters'

const props = defineProps({ id: { type: String, required: true } })

const data = ref(null)
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    const response = await getListing(props.id)
    data.value = response.data
  } catch (e) {
    error.value = errorMessage(e, 'Could not load this listing.')
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <router-link to="/listings" class="back">&larr; Back to listings</router-link>

  <p v-if="loading">Loading...</p>
  <p v-else-if="error" class="error" role="alert">{{ error }}</p>

  <article v-else class="details">
    <div class="top">
      <h1>{{ data.listing.title }}</h1>
      <StatusBadge :status="data.listing.status" />
    </div>
    <p class="business">{{ data.listing.businessName }} &middot; {{ categoryLabel(data.listing.category) }}</p>

    <p v-if="!data.available" class="unavailable" role="status">
      This listing is no longer available.
    </p>

    <p class="description">{{ data.listing.description }}</p>

    <dl>
      <div>
        <dt>Price</dt>
        <dd class="price">{{ formatPrice(data.listing.price) }}</dd>
      </div>
      <div>
        <dt>Left</dt>
        <dd>{{ data.listing.quantityRemaining }} of {{ data.listing.quantityTotal }}</dd>
      </div>
      <div>
        <dt>Pickup window</dt>
        <dd>{{ formatDateTime(data.listing.pickupStart) }} to {{ formatDateTime(data.listing.pickupEnd) }}</dd>
      </div>
      <div>
        <dt>Pickup address</dt>
        <dd>{{ data.businessAddress || 'Not provided' }}</dd>
      </div>
    </dl>

    <button class="btn" disabled>Reserve (coming next)</button>
  </article>
</template>

<style scoped>
.back {
  display: inline-block;
  margin-bottom: 1rem;
  color: #2f6b4f;
  text-decoration: none;
}
.details {
  background: #fff;
  border: 1px solid #d9dfda;
  border-radius: 10px;
  padding: 1.25rem 1.5rem;
  max-width: 640px;
}
.top {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
}
h1 {
  margin: 0;
}
.business {
  color: #5d6a63;
  margin: 0.25rem 0 1rem;
}
.unavailable {
  padding: 0.6rem 0.9rem;
  border-radius: 8px;
  background: #f8e5e1;
  color: #8f2f21;
}
.description {
  line-height: 1.6;
}
dl {
  display: grid;
  gap: 0.75rem;
  margin: 1.25rem 0;
}
dt {
  color: #5d6a63;
  font-size: 0.85rem;
}
dd {
  margin: 0;
  font-weight: 600;
}
.price {
  color: #7a5200;
  font-size: 1.25rem;
}
</style>