<script setup>
import { computed, onMounted, ref } from 'vue'
import { getListing } from '@/api/listings.api'
import { createReservation } from '@/api/reservations.api'
import { errorMessage } from '@/api/http'
import { useAuthStore } from '@/stores/auth'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { categoryLabel, formatDateTime, formatPrice } from '@/utils/formatters'
import ReviewList from '@/components/reviews/ReviewList.vue'
import { getBusinessReviews } from '@/api/reviews.api'
import ReportDialog from '@/components/reports/ReportDialog.vue'
import { RESERVATION_LIMITS } from '@/utils/constants'

const props = defineProps({ id: { type: String, required: true } })
const auth = useAuthStore()

const data = ref(null)
const loading = ref(true)
const error = ref('')

const quantity = ref(1)
const reserving = ref(false)
const reserveError = ref('')
const confirmation = ref(null) // the reservation the server just created
const reviews = ref(null)

const reportTarget = ref(null)
const reportNotice = ref('')

const canReserve = computed(() => ['CUSTOMER', 'ORGANIZATION'].includes(auth.role))
const roleLimit = computed(() => RESERVATION_LIMITS[auth.role] ?? 0)
// the most this user can take right now: their limit, or what's left, whichever is smaller
const maxQuantity = computed(() =>
  data.value ? Math.min(roleLimit.value, data.value.listing.quantityRemaining) : 0,
)

// display only; the server calculates the real total
const total = computed(() => {
  if (!data.value) return 0
  const qty = Number(quantity.value) || 0
  return Math.round(data.value.listing.price * qty * 100) / 100
})

async function load(quiet = false) {
  try {
    const response = await getListing(props.id)
    data.value = response.data
  } catch (e) {
    if (!quiet) error.value = errorMessage(e, 'Could not load this listing.')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await load()
  if (data.value) {
    try {
      const { data: result } = await getBusinessReviews(data.value.listing.businessId)
      reviews.value = result
    } catch {
      // reviews are extra; the listing still works without them
    }
  }
})

async function reserve() {
  reserveError.value = ''
  const qty = Number(quantity.value)
  const left = data.value.listing.quantityRemaining

  if (!Number.isInteger(qty) || qty < 1) {
    reserveError.value = 'Enter a whole number of 1 or more.'
    return
  }
  if (qty > roleLimit.value) {
    reserveError.value = `You can reserve up to ${roleLimit.value} portions at a time.`
    return
  }
  if (qty > left) {
    reserveError.value = `Only ${left} left.`
    return
  }

  reserving.value = true
  try {
    const { data: result } = await createReservation(data.value.listing.id, qty)
    confirmation.value = result
    quantity.value = 1
    await load(true) // refresh the remaining quantity without losing the confirmation
  } catch (e) {
    reserveError.value = errorMessage(e, 'Could not reserve this food.')
    // 409 means the listing changed (sold out, expired): show the fresh numbers
    if (e.response?.status === 409) await load(true)
  } finally {
    reserving.value = false
  }
}

function openReport(kind) {
  reportNotice.value = ''
  const l = data.value.listing
  reportTarget.value =
    kind === 'listing'
      ? { listingId: l.id, title: 'Report this listing' }
      : { businessId: l.businessId, title: `Report ${l.businessName}` }
}

function reportSent() {
  reportTarget.value = null
  reportNotice.value = 'Thanks. Your report was sent to an admin.'
}
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

    <section v-if="confirmation" class="confirmation" role="status">
      <h2>Reservation confirmed</h2>
      <p>Show this code when you pick up your food:</p>
      <p class="code">{{ confirmation.pickupCode }}</p>
      <p>
        {{ confirmation.quantity }} &times; {{ confirmation.listingTitle }}, total
        {{ formatPrice(confirmation.totalPrice) }}
      </p>
      <p>
        Pickup {{ formatDateTime(confirmation.pickupStart) }} to {{ formatDateTime(confirmation.pickupEnd) }}
      </p>
            <router-link to="/reservations">View my reservations</router-link>
    </section>

    <template v-if="data.available">
      <form v-if="canReserve" class="reserve" @submit.prevent="reserve">
        <div class="field">
          <label for="qty">Quantity</label>
          <input
            id="qty"
            v-model.number="quantity"
            type="number"
            min="1"
            :max="maxQuantity"
            step="1"
          />
        </div>
        <p class="limit">Up to {{ roleLimit }} per {{ auth.role === 'ORGANIZATION' ? 'organization' : 'person' }} on this listing</p>
        <p class="total">Total: {{ formatPrice(total) }}</p>
        <p v-if="reserveError" class="error" role="alert">{{ reserveError }}</p>
        <button class="btn" :disabled="reserving">{{ reserving ? 'Reserving...' : 'Reserve' }}</button>
      </form>

      <p v-else-if="auth.isAuthenticated" class="hint">
        Only customers and organizations can reserve food.
      </p>
      <p v-else class="hint">
        <router-link to="/login">Log in</router-link> or
        <router-link to="/register">register</router-link> to reserve this food.
      </p>
    </template>
        <div v-if="canReserve" class="report-links">
      <button type="button" class="link-btn" @click="openReport('listing')">Report this listing</button>
      <span aria-hidden="true">|</span>
      <button type="button" class="link-btn" @click="openReport('business')">Report this business</button>
    </div>
    <p v-if="reportNotice" class="success" role="status">{{ reportNotice }}</p>
  </article>
    <section v-if="reviews" class="details reviews-box">
    <h2>Reviews of {{ reviews.businessName }}</h2>
    <ReviewList :summary="reviews" />
    <ReportDialog :target="reportTarget" @close="reportTarget = null" @sent="reportSent" />
  </section>
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
.confirmation {
  background: #e4f1ea;
  border: 1px solid #b9d9c7;
  border-radius: 10px;
  padding: 0.75rem 1.25rem;
  margin-bottom: 1.25rem;
}
.confirmation h2 {
  margin: 0 0 0.5rem;
  color: #1f5c3f;
}
.confirmation p {
  margin: 0.25rem 0;
}
.code {
  font-size: 2rem;
  font-weight: 700;
  letter-spacing: 0.25rem;
  color: #1f5c3f;
}
.reserve {
  border-top: 1px solid #d9dfda;
  padding-top: 1rem;
  max-width: 260px;
}
.total {
  font-weight: 600;
}
.hint {
  color: #5d6a63;
}
.reviews-box {
  margin-top: 1.5rem;
}
.reviews-box h2 {
  margin-top: 0;
}
.report-links {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1.25rem;
  padding-top: 0.75rem;
  border-top: 1px solid #d9dfda;
  color: #b6c0ba;
  font-size: 0.9rem;
}
.link-btn {
  background: none;
  border: none;
  padding: 0;
  color: #8f2f21;
  text-decoration: underline;
  cursor: pointer;
  font: inherit;
}
.limit {
  color: #5d6a63;
  font-size: 0.85rem;
  margin: -0.5rem 0 0.5rem;
}
</style>