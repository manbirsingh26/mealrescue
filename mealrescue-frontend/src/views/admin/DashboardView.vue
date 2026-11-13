<script setup>
import { computed, onMounted, ref } from 'vue'
import { getStats } from '@/api/admin.api'
import { errorMessage } from '@/api/http'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { formatPrice, formatStatus } from '@/utils/formatters'

const stats = ref(null)
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    const { data } = await getStats()
    stats.value = data
  } catch (e) {
    error.value = errorMessage(e, 'Could not load the statistics.')
  } finally {
    loading.value = false
  }
})

// "2026-11-12" must be split by hand: new Date("2026-11-12") is read as UTC
// midnight, which is the previous evening in Vancouver and would shift every label
function parseDay(text) {
  const [y, m, d] = text.split('-').map(Number)
  return new Date(y, m - 1, d)
}

const days = computed(() => {
  if (!stats.value) return []
  const max = Math.max(1, ...stats.value.lastTwoWeeks.map((d) => d.portions))
  return stats.value.lastTwoWeeks.map((d) => {
    const date = parseDay(d.date)
    return {
      key: d.date,
      portions: d.portions,
      height: (d.portions / max) * 100,
      short: date.getDate(),
      long: date.toLocaleDateString('en-CA', { weekday: 'short', month: 'short', day: 'numeric' }),
    }
  })
})

const peak = computed(() => Math.max(0, ...days.value.map((d) => d.portions)))
const twoWeekTotal = computed(() => days.value.reduce((sum, d) => sum + d.portions, 0))
</script>

<template>
  <h1>Dashboard</h1>

  <p v-if="loading">Loading...</p>
  <p v-else-if="error" class="error" role="alert">{{ error }}</p>

  <template v-else>
    <!-- headline numbers -->
    <section class="tiles" aria-label="Key figures">
      <div class="tile">
        <span class="label">Portions rescued</span>
        <span class="value">{{ stats.portionsRescued }}</span>
      </div>
      <div class="tile">
        <span class="label">Value recovered</span>
        <span class="value">{{ formatPrice(stats.valueRecovered) }}</span>
      </div>
      <div class="tile">
        <span class="label">Pickup rate</span>
        <span class="value">{{ stats.pickupRate === null ? 'No data yet' : `${stats.pickupRate}%` }}</span>
        <span class="hint">Collected out of picked up + missed</span>
      </div>
      <div class="tile">
        <span class="label">Average rating</span>
        <span class="value">{{ stats.averageRating === null ? 'No reviews' : `${stats.averageRating} / 5` }}</span>
        <span class="hint">{{ stats.reviewCount }} {{ stats.reviewCount === 1 ? 'review' : 'reviews' }}</span>
      </div>
      <router-link to="/admin/verification" class="tile link" :class="{ attention: stats.pendingAccounts > 0 }">
        <span class="label">Waiting for verification</span>
        <span class="value">{{ stats.pendingAccounts }}</span>
      </router-link>
      <router-link to="/admin/reports" class="tile link" :class="{ attention: stats.openReports > 0 }">
        <span class="label">Open reports</span>
        <span class="value">{{ stats.openReports }}</span>
      </router-link>
    </section>

    <!-- last 14 days -->
    <section class="panel">
      <h2>Portions rescued, last 14 days</h2>
      <p class="muted">{{ twoWeekTotal }} in total, busiest day {{ peak }}</p>

      <div class="chart" aria-hidden="true">
        <div class="peak-line"><span>{{ peak }}</span></div>
        <div class="bars">
          <div v-for="d in days" :key="d.key" class="col" tabindex="0">
            <div class="bar-area">
              <div class="bar" :class="{ zero: d.portions === 0 }" :style="{ height: `${d.height}%` }"></div>
            </div>
            <span class="day">{{ d.short }}</span>
            <span class="tip">{{ d.long }}: {{ d.portions }} {{ d.portions === 1 ? 'portion' : 'portions' }}</span>
          </div>
        </div>
      </div>

      <!-- same data for screen readers -->
      <table class="sr-only">
        <caption>Portions rescued per day, last 14 days</caption>
        <thead><tr><th>Day</th><th>Portions</th></tr></thead>
        <tbody>
          <tr v-for="d in days" :key="d.key"><td>{{ d.long }}</td><td>{{ d.portions }}</td></tr>
        </tbody>
      </table>
    </section>

    <div class="columns">
      <section class="panel">
        <h2>Active users</h2>
        <ul class="counts">
          <li v-for="(count, role) in stats.activeUsersByRole" :key="role">
            <span>{{ formatStatus(role) }}</span><strong>{{ count }}</strong>
          </li>
          <li>
            <span>Deactivated accounts</span><strong>{{ stats.deactivatedAccounts }}</strong>
          </li>
        </ul>
      </section>

      <section class="panel">
        <h2>Listings</h2>
        <ul class="counts">
          <li v-for="(count, status) in stats.listingsByStatus" :key="status">
            <StatusBadge :status="status" /><strong>{{ count }}</strong>
          </li>
        </ul>
      </section>

      <section class="panel">
        <h2>Reservations</h2>
        <ul class="counts">
          <li v-for="(count, status) in stats.reservationsByStatus" :key="status">
            <StatusBadge :status="status" /><strong>{{ count }}</strong>
          </li>
        </ul>
      </section>

      <section class="panel">
        <h2>Top businesses</h2>
        <p v-if="stats.topBusinesses.length === 0" class="muted">No pickups yet.</p>
        <ol v-else class="top">
          <li v-for="b in stats.topBusinesses" :key="b.id">
            <span>{{ b.name }}</span><strong>{{ b.portions }}</strong>
          </li>
        </ol>
      </section>
    </div>
  </template>
</template>

<style scoped>
.muted {
  color: #5d6a63;
}

/* ----- number cards ----- */
.tiles {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(170px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}
.tile {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
  background: #fff;
  border: 1px solid #d9dfda;
  border-radius: 10px;
  padding: 0.9rem 1rem;
  color: #1d2521;
  text-decoration: none;
}
.tile .label {
  color: #5d6a63;
  font-size: 0.85rem;
}
.tile .value {
  font-size: 1.6rem;
  font-weight: 700;
}
.tile .hint {
  color: #5d6a63;
  font-size: 0.8rem;
}
.tile.link:hover {
  border-color: #2f6b4f;
}
.tile.attention {
  border-color: #e0a526;
  background: #fdf7e8;
}

/* ----- panels ----- */
.panel {
  background: #fff;
  border: 1px solid #d9dfda;
  border-radius: 10px;
  padding: 1rem 1.25rem;
  margin-bottom: 1rem;
}
.panel h2 {
  margin: 0 0 0.25rem;
  font-size: 1.05rem;
}
.columns {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 0 1rem;
}
.counts,
.top {
  margin: 0.5rem 0 0;
  padding: 0;
}
.counts {
  list-style: none;
}
.top {
  padding-left: 1.25rem;
}
.counts li,
.top li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.75rem;
  padding: 0.35rem 0;
  border-bottom: 1px solid #eef1ee;
}
.top li {
  display: list-item;
}
.top li span {
  display: inline-block;
  max-width: 75%;
}
.top li strong {
  float: right;
}

/* ----- bar chart ----- */
.chart {
  position: relative;
  margin-top: 1rem;
}
.peak-line {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  border-top: 1px dashed #d9dfda;
}
.peak-line span {
  position: absolute;
  top: -0.6rem;
  right: 0;
  background: #fff;
  padding-left: 0.25rem;
  font-size: 0.75rem;
  color: #5d6a63;
}
.bars {
  display: flex;
  gap: 2px;
}
.col {
  position: relative;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  outline: none;
}
.bar-area {
  height: 160px;
  width: 100%;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  border-bottom: 1px solid #b6c0ba;
}
.bar {
  width: 70%;
  max-width: 28px;
  background: #2f6b4f;
  border-radius: 4px 4px 0 0;
}
.bar.zero {
  height: 0 !important;
}
.col:hover .bar,
.col:focus-visible .bar {
  background: #1f5c3f;
}
.col:focus-visible .bar-area {
  outline: 2px solid #2f6b4f;
  outline-offset: 2px;
}
.day {
  font-size: 0.75rem;
  color: #5d6a63;
  margin-top: 0.3rem;
}
.tip {
  display: none;
  position: absolute;
  bottom: calc(100% - 1rem);
  left: 50%;
  transform: translateX(-50%);
  white-space: nowrap;
  background: #1d2521;
  color: #fff;
  font-size: 0.8rem;
  padding: 0.3rem 0.55rem;
  border-radius: 6px;
  pointer-events: none;
  z-index: 2;
}
.col:hover .tip,
.col:focus-visible .tip {
  display: block;
}
.col:first-child .tip {
  left: 0;
  transform: none;
}
.col:last-child .tip {
  left: auto;
  right: 0;
  transform: none;
}

/* visible to screen readers only */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
</style>