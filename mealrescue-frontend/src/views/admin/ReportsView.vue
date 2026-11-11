<script setup>
import { onMounted, reactive, ref } from 'vue'
import { dismissReport, getReports, resolveReport } from '@/api/admin.api'
import { errorMessage } from '@/api/http'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { formatDateTime, formatStatus, reportReasonLabel } from '@/utils/formatters'

const FILTERS = [
  { value: 'OPEN', label: 'Open' },
  { value: 'RESOLVED', label: 'Resolved' },
  { value: 'DISMISSED', label: 'Dismissed' },
  { value: '', label: 'All' },
]

const filter = ref('OPEN')
const reports = ref([])
const loading = ref(true)
const error = ref('')
const notice = ref('')

const notes = reactive({}) // note text per report id
const busyId = ref(null)   // report currently being handled

async function load() {
  loading.value = true
  error.value = ''
  try {
    const { data } = await getReports(filter.value)
    reports.value = data
  } catch (e) {
    reports.value = []
    error.value = errorMessage(e, 'Could not load reports.')
  } finally {
    loading.value = false
  }
}

function setFilter(value) {
  if (filter.value === value) return
  filter.value = value
  notice.value = ''
  load()
}

onMounted(load)

async function handle(report, action) {
  busyId.value = report.id
  error.value = ''
  notice.value = ''
  try {
    const call = action === 'resolve' ? resolveReport : dismissReport
    const { data } = await call(report.id, notes[report.id] || '')
    // in the Open view the handled report leaves the list; elsewhere it updates in place
    reports.value =
      filter.value === 'OPEN'
        ? reports.value.filter((r) => r.id !== data.id)
        : reports.value.map((r) => (r.id === data.id ? data : r))
    delete notes[report.id]
    notice.value = action === 'resolve' ? 'Report resolved.' : 'Report dismissed.'
  } catch (e) {
    error.value = errorMessage(e, 'Could not update the report.')
    if (e.response?.status === 409) load()
  } finally {
    busyId.value = null
  }
}
</script>

<template>
  <h1>Reports</h1>

  <div class="filters" role="group" aria-label="Show reports">
    <button
      v-for="f in FILTERS"
      :key="f.label"
      type="button"
      class="filter"
      :class="{ active: filter === f.value }"
      :aria-pressed="filter === f.value"
      @click="setFilter(f.value)"
    >
      {{ f.label }}
    </button>
  </div>

  <p v-if="notice" class="success" role="status">{{ notice }}</p>
  <p v-if="error" class="error" role="alert">{{ error }}</p>
  <p v-if="loading">Loading...</p>

  <template v-else-if="!error">
    <p v-if="reports.length === 0" class="muted">
      {{ filter === 'OPEN' ? 'No open reports. Nothing needs your attention.' : 'No reports here.' }}
    </p>

    <article v-for="r in reports" :key="r.id" class="report">
      <div class="top">
        <h2>{{ reportReasonLabel(r.reason) }}</h2>
        <StatusBadge :status="r.status" />
      </div>
      <p class="muted small">Reported {{ formatDateTime(r.createdAt) }}</p>

      <dl>
        <div>
          <dt>About</dt>
          <dd>
            <template v-if="r.listingId">
              Listing
              <router-link :to="`/listings/${r.listingId}`">{{ r.listingTitle }}</router-link>
              <StatusBadge :status="r.listingStatus" />
              <br />
            </template>
            Business {{ r.businessName }}
            <StatusBadge :status="r.businessStatus" />
          </dd>
        </div>
        <div>
          <dt>Reported by</dt>
          <dd>
            {{ r.reporterName }} ({{ formatStatus(r.reporterRole) }}),
            <a :href="`mailto:${r.reporterEmail}`">{{ r.reporterEmail }}</a>
          </dd>
        </div>
        <div v-if="r.details">
          <dt>Details</dt>
          <dd class="details">{{ r.details }}</dd>
        </div>
        <template v-if="r.status !== 'OPEN'">
          <div>
            <dt>Handled</dt>
            <dd>{{ formatDateTime(r.handledAt) }}</dd>
          </div>
          <div v-if="r.adminNote">
            <dt>Admin note</dt>
            <dd class="details">{{ r.adminNote }}</dd>
          </div>
        </template>
      </dl>

      <div v-if="r.status === 'OPEN'" class="handle">
        <label :for="`note-${r.id}`">Note (optional)</label>
        <textarea
          :id="`note-${r.id}`"
          v-model="notes[r.id]"
          rows="2"
          maxlength="1000"
          placeholder="What did you find or do?"
        ></textarea>
        <div class="buttons">
          <button class="btn" :disabled="busyId === r.id" @click="handle(r, 'resolve')">Resolve</button>
          <button class="btn btn-light" :disabled="busyId === r.id" @click="handle(r, 'dismiss')">Dismiss</button>
        </div>
      </div>
    </article>
  </template>
</template>

<style scoped>
.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1.25rem;
}
.filter {
  background: #fff;
  border: 1px solid #d9dfda;
  border-radius: 999px;
  padding: 0.35rem 1rem;
  font: inherit;
  cursor: pointer;
}
.filter.active {
  background: #2f6b4f;
  border-color: #2f6b4f;
  color: #fff;
}
.muted {
  color: #5d6a63;
}
.small {
  font-size: 0.85rem;
  margin: 0.25rem 0 0;
}
.report {
  background: #fff;
  border: 1px solid #d9dfda;
  border-radius: 10px;
  padding: 1rem 1.25rem;
  margin-bottom: 1rem;
  max-width: 760px;
}
.top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  flex-wrap: wrap;
}
h2 {
  margin: 0;
  font-size: 1.1rem;
}
dl {
  display: grid;
  gap: 0.6rem;
  margin: 1rem 0 0;
}
dt {
  color: #5d6a63;
  font-size: 0.85rem;
}
dd {
  margin: 0.1rem 0 0;
  line-height: 1.6;
}
dd a {
  color: #2f6b4f;
}
.details {
  white-space: pre-line;
  background: #f6f7f4;
  border-radius: 8px;
  padding: 0.5rem 0.75rem;
}
.handle {
  margin-top: 1rem;
  padding-top: 0.75rem;
  border-top: 1px solid #d9dfda;
}
.handle textarea {
  display: block;
  width: 100%;
  box-sizing: border-box;
  font: inherit;
  padding: 0.5rem;
  margin: 0.25rem 0 0.75rem;
}
.buttons {
  display: flex;
  gap: 0.75rem;
}
</style>