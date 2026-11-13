<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getAdminListings, removeListing } from '@/api/admin.api'
import { errorMessage } from '@/api/http'
import StatusBadge from '@/components/common/StatusBadge.vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import { formatDateTime, formatPrice } from '@/utils/formatters'

const filters = reactive({ q: '', status: '' })
const listings = ref([])
const loading = ref(true)
const error = ref('')
const notice = ref('')

const removeTarget = ref(null)
const removing = ref(false)

const canRemove = (l) => l.status === 'ACTIVE' || l.status === 'SOLD_OUT'

async function load() {
  loading.value = true
  error.value = ''
  const params = {}
  if (filters.q.trim()) params.q = filters.q.trim()
  if (filters.status) params.status = filters.status
  try {
    const { data } = await getAdminListings(params)
    listings.value = data
  } catch (e) {
    listings.value = []
    error.value = errorMessage(e, 'Could not load listings.')
  } finally {
    loading.value = false
  }
}

onMounted(load)

async function confirmRemove() {
  removing.value = true
  error.value = ''
  notice.value = ''
  try {
    const { data } = await removeListing(removeTarget.value.id)
    listings.value = listings.value.map((l) => (l.id === data.id ? data : l))
    notice.value = `“${data.title}” was removed.`
  } catch (e) {
    error.value = errorMessage(e, 'Could not remove the listing.')
  } finally {
    removing.value = false
    removeTarget.value = null
  }
}
</script>

<template>
  <h1>Listings</h1>

  <form class="filters" @submit.prevent="load">
    <div class="field">
      <label for="listing-q">Search</label>
      <input id="listing-q" v-model="filters.q" type="search" placeholder="Title or business name" />
    </div>
    <div class="field">
      <label for="listing-status">Status</label>
      <select id="listing-status" v-model="filters.status">
        <option value="">All statuses</option>
        <option value="ACTIVE">Active</option>
        <option value="SOLD_OUT">Sold out</option>
        <option value="EXPIRED">Expired</option>
        <option value="CANCELLED">Cancelled</option>
        <option value="REMOVED">Removed</option>
      </select>
    </div>
    <div class="go">
      <button class="btn" :disabled="loading">Search</button>
    </div>
  </form>

  <p v-if="notice" class="success" role="status">{{ notice }}</p>
  <p v-if="error" class="error" role="alert">{{ error }}</p>
  <p v-if="loading">Loading...</p>

  <template v-else-if="!error">
    <p v-if="listings.length === 0" class="muted">No listings match these filters.</p>

    <div v-else class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Title</th>
            <th>Business</th>
            <th>Left / total</th>
            <th>Price</th>
            <th>Pickup window</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="l in listings" :key="l.id">
            <td><router-link :to="`/listings/${l.id}`">{{ l.title }}</router-link></td>
            <td>{{ l.businessName }}</td>
            <td>{{ l.quantityRemaining }} / {{ l.quantityTotal }}</td>
            <td>{{ formatPrice(l.price) }}</td>
            <td>{{ formatDateTime(l.pickupStart) }} to {{ formatDateTime(l.pickupEnd) }}</td>
            <td><StatusBadge :status="l.status" /></td>
            <td>
              <button v-if="canRemove(l)" class="btn btn-danger" @click="removeTarget = l">Remove</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </template>

  <ConfirmDialog
    :open="!!removeTarget"
    title="Remove this listing?"
    :message="removeTarget ? `“${removeTarget.title}” will be taken down and its open reservations cancelled.` : ''"
    confirm-label="Remove listing"
    cancel-label="Keep listing"
    :busy="removing"
    @confirm="confirmRemove"
    @cancel="removeTarget = null"
  />
</template>

<style scoped>
.filters {
  display: grid;
  grid-template-columns: 2fr 1fr auto;
  gap: 0 1rem;
  align-items: end;
  background: #fff;
  border: 1px solid #d9dfda;
  border-radius: 10px;
  padding: 1rem 1.25rem 0;
  margin-bottom: 1.25rem;
}
.go {
  margin-bottom: 1rem;
}
@media (max-width: 700px) {
  .filters {
    grid-template-columns: 1fr;
  }
}
.muted {
  color: #5d6a63;
}
.table-wrap {
  overflow-x: auto;
}
table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
}
th,
td {
  text-align: left;
  vertical-align: middle;
  padding: 0.6rem 0.75rem;
  border-bottom: 1px solid #d9dfda;
}
td a {
  color: #2f6b4f;
}
</style>