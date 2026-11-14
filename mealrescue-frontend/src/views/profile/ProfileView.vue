<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { getMyReports, getProfile, updateProfile } from '@/api/profile.api'
import { errorMessage } from '@/api/http'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { formatDateTime, formatStatus, reportReasonLabel } from '@/utils/formatters'

const auth = useAuthStore()

const form = reactive({ name: '', email: '', address: '', businessType: '' })
const profile = ref(null)
const loading = ref(true)
const loadError = ref('')
const saving = ref(false)
const saveError = ref('')
const notice = ref('')

const reports = ref([])
const reportsError = ref('')

const isBusiness = computed(() => auth.role === 'BUSINESS')
const hasType = computed(() => ['BUSINESS', 'ORGANIZATION'].includes(auth.role))
const canReport = computed(() => ['CUSTOMER', 'ORGANIZATION'].includes(auth.role))

function fill(p) {
  profile.value = p
  form.name = p.name
  form.email = p.email
  form.address = p.address ?? ''
  form.businessType = p.businessType ?? ''
}

onMounted(async () => {
  try {
    const { data } = await getProfile()
    fill(data)
  } catch (e) {
    loadError.value = errorMessage(e, 'Could not load your profile.')
  } finally {
    loading.value = false
  }

  if (canReport.value) {
    try {
      const { data } = await getMyReports()
      reports.value = data
    } catch (e) {
      reportsError.value = errorMessage(e, 'Could not load your reports.')
    }
  }
})

async function save() {
  saveError.value = ''
  notice.value = ''

  if (!form.name.trim()) {
    saveError.value = 'Name is required.'
    return
  }
  if (isBusiness.value && !form.address.trim()) {
    saveError.value = 'A business needs a pickup address.'
    return
  }

  saving.value = true
  try {
    const { data } = await updateProfile({ ...form })
    fill(data)
    auth.updateUser({ name: data.name, email: data.email }) // navbar updates right away
    notice.value = 'Profile saved.'
  } catch (e) {
    saveError.value = errorMessage(e, 'Could not save your profile.')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <h1>Your profile</h1>

  <p v-if="loading">Loading...</p>
  <p v-else-if="loadError" class="error" role="alert">{{ loadError }}</p>

  <template v-else>
    <form class="panel" @submit.prevent="save">
      <p class="muted role">
        {{ formatStatus(profile.role) }} account
        <StatusBadge :status="profile.status" />
      </p>

      <div class="field">
        <label for="p-name">{{ hasType ? 'Name of business or organization' : 'Name' }}</label>
        <input id="p-name" v-model="form.name" maxlength="100" autocomplete="name" />
      </div>

      <div class="field">
        <label for="p-email">Email</label>
        <input id="p-email" v-model="form.email" type="email" autocomplete="email" />
        <p class="hint">You log in with this email.</p>
      </div>

      <div class="field">
        <label for="p-address">
          {{ isBusiness ? 'Pickup address' : 'Address' }}
          <span class="muted">({{ isBusiness ? 'required' : 'optional' }})</span>
        </label>
        <input id="p-address" v-model="form.address" maxlength="255" autocomplete="street-address" />
        <p v-if="isBusiness" class="hint">Customers see this address on your listings.</p>
      </div>

      <div v-if="hasType" class="field">
        <label for="p-type">{{ isBusiness ? 'Type of business' : 'Type of organization' }}</label>
        <input
          id="p-type"
          v-model="form.businessType"
          maxlength="100"
          :placeholder="isBusiness ? 'For example: Bakery' : 'For example: Food bank'"
        />
      </div>

      <p v-if="notice" class="success" role="status">{{ notice }}</p>
      <p v-if="saveError" class="error" role="alert">{{ saveError }}</p>

      <button class="btn" :disabled="saving">{{ saving ? 'Saving...' : 'Save changes' }}</button>
    </form>

    <section v-if="canReport" class="panel">
      <h2>Your reports</h2>
      <p v-if="reportsError" class="error" role="alert">{{ reportsError }}</p>
      <p v-else-if="reports.length === 0" class="muted">You haven't reported anything.</p>

      <ul v-else class="reports">
        <li v-for="r in reports" :key="r.id">
          <div class="top">
            <strong>{{ reportReasonLabel(r.reason) }}</strong>
            <StatusBadge :status="r.status" />
          </div>
          <p class="muted">
            About
            <router-link v-if="r.listingId" :to="`/listings/${r.listingId}`">{{ r.listingTitle }}</router-link>
            <template v-else>{{ r.businessName }}</template>
            , sent {{ formatDateTime(r.createdAt) }}
          </p>
          <p v-if="r.adminNote" class="note">
            <span class="muted">Note from MealRescue:</span> {{ r.adminNote }}
          </p>
        </li>
      </ul>
    </section>
  </template>
</template>

<style scoped>
.panel {
  background: #fff;
  border: 1px solid #d9dfda;
  border-radius: 10px;
  padding: 1.25rem 1.5rem;
  max-width: 560px;
  margin-bottom: 1.5rem;
}
.panel h2 {
  margin-top: 0;
  font-size: 1.15rem;
}
.muted {
  color: #5d6a63;
  font-weight: 400;
}
.role {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 0;
}
.hint {
  color: #5d6a63;
  font-size: 0.85rem;
  margin: 0.25rem 0 0;
}
.reports {
  list-style: none;
  padding: 0;
  margin: 0;
}
.reports li {
  padding: 0.75rem 0;
  border-top: 1px solid #d9dfda;
}
.reports li:first-child {
  border-top: none;
  padding-top: 0;
}
.reports p {
  margin: 0.25rem 0 0;
}
.top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
}
.reports a {
  color: #2f6b4f;
}
.note {
  white-space: pre-line;
  background: #f6f7f4;
  border-radius: 8px;
  padding: 0.5rem 0.75rem;
}
</style>