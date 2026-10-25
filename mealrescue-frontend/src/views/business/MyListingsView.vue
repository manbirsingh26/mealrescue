<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getMyListings } from '@/api/listings.api'
import { errorMessage } from '@/api/http'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { categoryLabel, formatDateTime, formatPrice } from '@/utils/formatters'

const route = useRoute()
const listings = ref([])
const loading = ref(true)
const error = ref('')
const justCreated = route.query.created === '1'

onMounted(async () => {
  try {
    const { data } = await getMyListings()
    listings.value = data
  } catch (e) {
    error.value = errorMessage(e, 'Could not load your listings.')
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="head">
    <h1>My listings</h1>
    <router-link to="/business/listings/new" class="btn">New listing</router-link>
  </div>

  <p v-if="justCreated" class="success" role="status">Listing created.</p>
  <p v-if="error" class="error" role="alert">{{ error }}</p>
  <p v-if="loading">Loading...</p>

  <template v-else-if="!error">
    <p v-if="listings.length === 0">You have no listings yet. Create your first one.</p>

    <table v-else>
      <thead>
        <tr>
          <th>Title</th>
          <th>Category</th>
          <th>Left / total</th>
          <th>Price</th>
          <th>Pickup window</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="l in listings" :key="l.id">
          <td>{{ l.title }}</td>
          <td>{{ categoryLabel(l.category) }}</td>
          <td>{{ l.quantityRemaining }} / {{ l.quantityTotal }}</td>
          <td>{{ formatPrice(l.price) }}</td>
          <td>{{ formatDateTime(l.pickupStart) }} to {{ formatDateTime(l.pickupEnd) }}</td>
          <td><StatusBadge :status="l.status" /></td>
        </tr>
      </tbody>
    </table>
  </template>
</template>

<style scoped>
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
}
th,
td {
  text-align: left;
  padding: 0.6rem 0.75rem;
  border-bottom: 1px solid #d9dfda;
}
</style>