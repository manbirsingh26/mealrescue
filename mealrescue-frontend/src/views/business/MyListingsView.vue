<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { cancelListing, getMyListings } from '@/api/listings.api'
import { errorMessage } from '@/api/http'
import StatusBadge from '@/components/common/StatusBadge.vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import { categoryLabel, formatDateTime, formatPrice } from '@/utils/formatters'

const route = useRoute()
const listings = ref([])
const loading = ref(true)
const error = ref('')
const notice = ref(
  route.query.created ? 'Listing created.' : route.query.updated ? 'Listing updated.' : '',
)

const cancelTarget = ref(null) // the listing the dialog is asking about
const cancelling = ref(false)

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

const canChange = (l) => l.status === 'ACTIVE' || l.status === 'SOLD_OUT'

async function confirmCancel() {
  cancelling.value = true
  error.value = ''
  notice.value = ''
  try {
    const { data } = await cancelListing(cancelTarget.value.id)
    // swap the old row for the updated one the server returned
    listings.value = listings.value.map((l) => (l.id === data.id ? data : l))
    notice.value = 'Listing cancelled.'
  } catch (e) {
    error.value = errorMessage(e, 'Could not cancel the listing.')
  } finally {
    cancelling.value = false
    cancelTarget.value = null
  }
}
</script>

<template>
  <div class="head">
    <h1>My listings</h1>
    <router-link to="/business/listings/new" class="btn">New listing</router-link>
  </div>

  <p v-if="notice" class="success" role="status">{{ notice }}</p>
  <p v-if="error" class="error" role="alert">{{ error }}</p>
  <p v-if="loading">Loading...</p>

  <template v-else>
    <p v-if="listings.length === 0">You have no listings yet. Create your first one.</p>
<div v-else class="table-wrap">
    <table >
      <thead>
        <tr>
          <th>Title</th>
          <th>Category</th>
          <th>Left / total</th>
          <th>Price</th>
          <th>Pickup window</th>
          <th>Status</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="l in listings" :key="l.id">
          <td>{{ l.title }}</td>
          <td>{{ categoryLabel(l.category) }}</td>
          <td class="nowrap">{{ l.quantityRemaining }} / {{ l.quantityTotal }}</td>
          <td class="nowrap">{{ formatPrice(l.price) }}</td>
          <td class="nowrap">
            {{ formatDateTime(l.pickupStart) }}<br />
            <span class="muted">to {{ formatDateTime(l.pickupEnd) }}</span>
          </td>
          <td class="nowrap"><StatusBadge :status="l.status" /></td>
          <td >
            <div v-if="canChange(l)" class="actions">
              <router-link :to="`/business/listings/${l.id}/edit`" class="btn btn-light">Edit</router-link>
              <button class="btn btn-danger" @click="cancelTarget = l">Cancel</button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
  </template>

  <ConfirmDialog
    :open="!!cancelTarget"
    title="Cancel this listing?"
    :message="cancelTarget ? `“${cancelTarget.title}” will no longer be available to customers.` : ''"
    confirm-label="Cancel listing"
    cancel-label="Keep listing"
    :busy="cancelling"
    @confirm="confirmCancel"
    @cancel="cancelTarget = null"
  />
</template>

<style scoped>
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 0.5rem 1rem;

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
  vertical-align: middle;
  border-bottom: 1px solid #d9dfda;
}
.actions {
  display: flex;
  gap: 0.5rem;
}
.table-wrap {
  overflow-x: auto;
}
.nowrap {
  white-space: nowrap;
}
.muted {
  color: #5d6a63;
}
</style>