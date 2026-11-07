<script setup>
import { computed, onMounted, ref } from 'vue'
import { cancelReservation, getMyReservations } from '@/api/reservations.api'
import { errorMessage } from '@/api/http'
import StatusBadge from '@/components/common/StatusBadge.vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import { formatDateTime, formatPrice } from '@/utils/formatters'
import StarRating from '@/components/reviews/StarRating.vue'
import { createReview } from '@/api/reviews.api'

const reservations = ref([])
const loading = ref(true)
const error = ref('')
const notice = ref('')

const cancelTarget = ref(null)
const cancelling = ref(false)

// upcoming = still reserved and the pickup window hasn't ended
const isUpcoming = (r) => r.status === 'RESERVED' && new Date(r.pickupEnd) > new Date()

const sections = computed(() => [
  {
    key: 'upcoming',
    title: 'Upcoming',
    items: reservations.value.filter(isUpcoming),
    empty: 'No upcoming pickups.',
  },
  {
    key: 'past',
    title: 'Past',
    items: reservations.value.filter((r) => !isUpcoming(r)),
    empty: 'No past reservations yet.',
  },
])

async function load() {
  try {
    const { data } = await getMyReservations()
    reservations.value = data
  } catch (e) {
    error.value = errorMessage(e, 'Could not load your reservations.')
  } finally {
    loading.value = false
  }
}

onMounted(load)

async function confirmCancel() {
  cancelling.value = true
  error.value = ''
  notice.value = ''
  try {
    const { data } = await cancelReservation(cancelTarget.value.id)
    // swap the old row for the updated one; it moves to Past by itself
    reservations.value = reservations.value.map((r) => (r.id === data.id ? data : r))
    notice.value = 'Reservation cancelled.'
  } catch (e) {
    error.value = errorMessage(e, 'Could not cancel the reservation.')
    if (e.response?.status === 409) await load()
  } finally {
    cancelling.value = false
    cancelTarget.value = null
  }
}
const reviewFor = ref(null) // id of the reservation whose form is open
const rating = ref(0)
const comment = ref('')
const reviewing = ref(false)
const reviewError = ref('')

function openReview(r) {
  reviewFor.value = r.id
  rating.value = 0
  comment.value = ''
  reviewError.value = ''
}

async function submitReview(r) {
  if (rating.value < 1) {
    reviewError.value = 'Choose a rating.'
    return
  }
  reviewing.value = true
  reviewError.value = ''
  try {
    await createReview(r.id, rating.value, comment.value)
    r.reviewed = true
    reviewFor.value = null
    notice.value = 'Thanks for your review.'
  } catch (e) {
    reviewError.value = errorMessage(e, 'Could not save your review.')
    if (e.response?.status === 409) await load()
  } finally {
    reviewing.value = false
  }
}
</script>

<template>
  <h1>My reservations</h1>

  <p v-if="notice" class="success" role="status">{{ notice }}</p>
  <p v-if="error" class="error" role="alert">{{ error }}</p>
  <p v-if="loading">Loading...</p>

  <template v-else>
    <p v-if="reservations.length === 0">
      You haven't reserved anything yet.
      <router-link to="/listings">Browse food</router-link> to find something.
    </p>

    <template v-else>
      <section v-for="s in sections" :key="s.key" class="section">
        <h2>{{ s.title }} ({{ s.items.length }})</h2>
        <p v-if="s.items.length === 0" class="muted">{{ s.empty }}</p>

        <div v-else class="grid">
          <article v-for="r in s.items" :key="r.id" class="res" :class="{ faded: s.key === 'past' }">
            <div class="top">
              <h3>
                <router-link :to="`/listings/${r.listingId}`">{{ r.listingTitle }}</router-link>
              </h3>
              <StatusBadge :status="r.status" />
            </div>
            <p class="muted">{{ r.businessName }}</p>
            <p>Quantity {{ r.quantity }}, total {{ formatPrice(r.totalPrice) }}</p>
            <p class="muted">
              Pickup {{ formatDateTime(r.pickupStart) }} to {{ formatDateTime(r.pickupEnd) }}
            </p>

            <div v-if="s.key === 'upcoming'" class="bottom">
              <p class="code-line">
                Pickup code <span class="code">{{ r.pickupCode }}</span>
              </p>
              <button class="btn btn-danger" @click="cancelTarget = r">Cancel</button>
            </div>
           
                  <template v-if="r.status === 'PICKED_UP'">
              <p v-if="r.reviewed" class="muted reviewed">You reviewed this pickup.</p>

              <form v-else-if="reviewFor === r.id" class="review-form" @submit.prevent="submitReview(r)">
                <StarRating v-model="rating" size="1.6rem" />
                <textarea
                  v-model="comment"
                  rows="3"
                  maxlength="500"
                  placeholder="What was it like? (optional)"
                  aria-label="Comment"
                ></textarea>
                <p class="muted counter">{{ comment.length }} / 500</p>
                <p v-if="reviewError" class="error" role="alert">{{ reviewError }}</p>
                <div class="review-buttons">
                  <button class="btn" :disabled="reviewing">
                    {{ reviewing ? 'Saving...' : 'Submit review' }}
                  </button>
                  <button type="button" class="btn btn-light" @click="reviewFor = null">Not now</button>
                </div>
              </form>

              <button v-else class="btn btn-light review-open" @click="openReview(r)">Leave a review</button>
            </template>
          </article>
        </div>
      </section>
    </template>
  </template>

  <ConfirmDialog
    :open="!!cancelTarget"
    title="Cancel this reservation?"
    :message="cancelTarget ? `Your reservation for “${cancelTarget.listingTitle}” will be released for others.` : ''"
    confirm-label="Cancel reservation"
    cancel-label="Keep reservation"
    :busy="cancelling"
    @confirm="confirmCancel"
    @cancel="cancelTarget = null"
  />
</template>

<style scoped>
.section {
  margin-top: 1.5rem;
}
h2 {
  font-size: 1.15rem;
}
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1rem;
}
.res {
  background: #fff;
  border: 1px solid #d9dfda;
  border-radius: 10px;
  padding: 1rem 1.25rem;
}
.res.faded {
  background: #fafbf9;
}
.res p {
  margin: 0.25rem 0;
}
.top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 0.75rem;
}
h3 {
  margin: 0;
}
h3 a {
  color: #1d2521;
  text-decoration: none;
}
h3 a:hover {
  color: #2f6b4f;
  text-decoration: underline;
}
.muted {
  color: #5d6a63;
}
.bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  flex-wrap: wrap;
  margin-top: 0.75rem;
  padding-top: 0.75rem;
  border-top: 1px solid #d9dfda;
}
.code {
  font-size: 1.35rem;
  font-weight: 700;
  letter-spacing: 0.15rem;
  color: #1f5c3f;
  margin-left: 0.35rem;
}
.reviewed,
.review-open,
.review-form {
  margin-top: 0.75rem;
}
.review-form textarea {
  display: block;
  width: 100%;
  margin-top: 0.5rem;
  font: inherit;
  padding: 0.5rem;
  box-sizing: border-box;
}
.counter {
  font-size: 0.8rem;
  text-align: right;
}
.review-buttons {
  display: flex;
  gap: 0.75rem;
}
</style>