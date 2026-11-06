<script setup>
import { computed, nextTick, onMounted, ref } from 'vue'
import { confirmPickup, getBusinessReservations } from '@/api/reservations.api'
import { errorMessage } from '@/api/http'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { formatDateTime, formatPrice } from '@/utils/formatters'

const reservations = ref([])
const loading = ref(true)
const error = ref('')

const filter = ref('RESERVED') // what the business usually needs: who's still coming

const code = ref('')
const codeInput = ref(null)
const confirming = ref(false)
const confirmError = ref('')
const confirmNotice = ref('')

const FILTERS = [
  { value: 'RESERVED', label: 'Waiting for pickup' },
  { value: 'PICKED_UP', label: 'Picked up' },
  { value: 'CANCELLED', label: 'Cancelled' },
  { value: 'EXPIRED', label: 'Not collected' },
  { value: 'ALL', label: 'All' },
]

const shown = computed(() =>
  filter.value === 'ALL' ? reservations.value : reservations.value.filter((r) => r.status === filter.value),
)

async function load() {
  try {
    const { data } = await getBusinessReservations()
    reservations.value = data
  } catch (e) {
    error.value = errorMessage(e, 'Could not load reservations.')
  } finally {
    loading.value = false
  }
}

onMounted(load)

async function submitCode() {
  confirmError.value = ''
  confirmNotice.value = ''
  const value = code.value.trim()

  if (!/^\d{6}$/.test(value)) {
    confirmError.value = 'The pickup code has 6 digits.'
    return
  }

  confirming.value = true
  try {
    const { data } = await confirmPickup(value)
    reservations.value = reservations.value.map((r) => (r.id === data.id ? data : r))
    confirmNotice.value = `Pickup confirmed: ${data.quantity} × ${data.listingTitle} for ${data.customerName}.`
    code.value = ''
  } catch (e) {
    confirmError.value = errorMessage(e, 'Could not confirm the pickup.')
  } finally {
    confirming.value = false
    await nextTick()
    codeInput.value?.focus() // ready for the next customer
  }
}
</script>

<template>
  <h1>Reservations</h1>

  <form class="confirm" @submit.prevent="submitCode">
    <div class="field">
      <label for="code">Confirm a pickup</label>
      <div class="row">
        <input
          id="code"
          ref="codeInput"
          v-model="code"
          type="text"
          inputmode="numeric"
          maxlength="6"
          autocomplete="off"
          placeholder="6-digit code"
        />
        <button class="btn" :disabled="confirming">
          {{ confirming ? 'Confirming...' : 'Confirm pickup' }}
        </button>
      </div>
    </div>
    <p v-if="confirmNotice" class="success" role="status">{{ confirmNotice }}</p>
    <p v-if="confirmError" class="error" role="alert">{{ confirmError }}</p>
  </form>

  <div class="list-head">
    <h2>Your reservations</h2>
    <select v-model="filter" aria-label="Show">
      <option v-for="f in FILTERS" :key="f.value" :value="f.value">{{ f.label }}</option>
    </select>
  </div>

  <p v-if="error" class="error" role="alert">{{ error }}</p>
  <p v-if="loading">Loading...</p>

  <template v-else-if="!error">
    <p v-if="shown.length === 0" class="muted">No reservations here.</p>

    <div v-else class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Listing</th>
            <th>Reserved by</th>
            <th>Qty</th>
            <th>Total</th>
            <th>Pickup window</th>
            <th>Status</th>
            <th>Reserved</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in shown" :key="r.id">
            <td>{{ r.listingTitle }}</td>
            <td>
              {{ r.customerName }}
              <span v-if="r.customerRole === 'ORGANIZATION'" class="org">Organization</span>
            </td>
            <td>{{ r.quantity }}</td>
            <td>{{ formatPrice(r.totalPrice) }}</td>
            <td>{{ formatDateTime(r.pickupStart) }} to {{ formatDateTime(r.pickupEnd) }}</td>
            <td><StatusBadge :status="r.status" /></td>
            <td>{{ formatDateTime(r.createdAt) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </template>
</template>

<style scoped>
.confirm {
  background: #fff;
  border: 1px solid #d9dfda;
  border-radius: 10px;
  padding: 1rem 1.25rem;
  max-width: 480px;
  margin-bottom: 2rem;
}
.row {
  display: flex;
  gap: 0.75rem;
}
.row input {
  flex: 1;
  font-size: 1.25rem;
  letter-spacing: 0.2rem;
}
.row .btn {
  white-space: nowrap;
}
.list-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
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
.org {
  display: inline-block;
  margin-left: 0.35rem;
  padding: 0.05rem 0.5rem;
  border-radius: 999px;
  background: #e8eef8;
  color: #2c4a7a;
  font-size: 0.8rem;
}
</style>