<script setup>
import { onMounted, ref } from 'vue'
import { approveAccount, getPendingAccounts, rejectAccount } from '@/api/admin.api'
import { errorMessage } from '@/api/http'

const accounts = ref([])
const loading = ref(true)
const error = ref('')
const notice = ref('')

const rejectingId = ref(null) // which row is showing the reject form
const reason = ref('')
const busy = ref(false)

async function load() {
  loading.value = true
  error.value = ''
  try {
    const { data } = await getPendingAccounts()
    accounts.value = data
  } catch (e) {
    error.value = errorMessage(e, 'Could not load the pending accounts.')
  } finally {
    loading.value = false
  }
}

function removeFromList(id) {
  accounts.value = accounts.value.filter((a) => a.id !== id)
}

async function approve(account) {
  busy.value = true
  error.value = ''
  notice.value = ''
  try {
    await approveAccount(account.id)
    removeFromList(account.id)
    notice.value = `${account.name} was approved.`
  } catch (e) {
    error.value = errorMessage(e, 'Could not approve this account.')
  } finally {
    busy.value = false
  }
}

function startReject(account) {
  rejectingId.value = account.id
  reason.value = ''
  error.value = ''
  notice.value = ''
}

function cancelReject() {
  rejectingId.value = null
  reason.value = ''
}

async function confirmReject(account) {
  if (!reason.value.trim()) {
    error.value = 'Please enter a reason.'
    return
  }
  busy.value = true
  error.value = ''
  try {
    await rejectAccount(account.id, reason.value.trim())
    removeFromList(account.id)
    notice.value = `${account.name} was rejected.`
    cancelReject()
  } catch (e) {
    error.value = errorMessage(e, 'Could not reject this account.')
  } finally {
    busy.value = false
  }
}

function roleLabel(role) {
  return role === 'BUSINESS' ? 'Business' : 'Organization'
}

function formatDate(iso) {
  return new Date(iso).toLocaleDateString('en-CA')
}

onMounted(load)
</script>

<template>
  <h1>Verification queue</h1>
  <p class="intro">Businesses and organizations waiting for approval.</p>

  <p v-if="notice" class="success" role="status">{{ notice }}</p>
  <p v-if="error" class="error" role="alert">{{ error }}</p>

  <p v-if="loading">Loading...</p>

  <template v-else>
    <p v-if="accounts.length === 0">No accounts are waiting for verification.</p>

    <table v-else>
      <thead>
        <tr>
          <th>Name</th>
          <th>Type</th>
          <th>Email</th>
          <th>Address</th>
          <th>Details</th>
          <th>Registered</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <template v-for="a in accounts" :key="a.id">
          <tr>
            <td>{{ a.name }}</td>
            <td>{{ roleLabel(a.role) }}</td>
            <td>{{ a.email }}</td>
            <td>{{ a.address }}</td>
            <td>{{ a.businessType }}</td>
            <td>{{ formatDate(a.createdAt) }}</td>
            <td class="actions">
              <button class="btn" :disabled="busy" @click="approve(a)">Approve</button>
              <button class="btn reject" :disabled="busy" @click="startReject(a)">Reject</button>
            </td>
          </tr>
          <tr v-if="rejectingId === a.id">
            <td colspan="7" class="reject-row">
              <input v-model="reason" placeholder="Reason for rejection" maxlength="500" />
              <button class="btn reject" :disabled="busy" @click="confirmReject(a)">Confirm reject</button>
              <button class="btn cancel" :disabled="busy" @click="cancelReject">Cancel</button>
            </td>
          </tr>
        </template>
      </tbody>
    </table>

    <button class="btn refresh" :disabled="loading" @click="load">Refresh</button>
  </template>
</template>

<style scoped>
.intro {
  color: #5d6a63;
}
table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
  margin-bottom: 1rem;
}
th,
td {
  text-align: left;
  padding: 0.6rem 0.75rem;
  border-bottom: 1px solid #d9dfda;
}
.actions {
  display: flex;
  gap: 0.5rem;
}
.reject {
  background: #b3402f;
}
.cancel {
  background: #5d6a63;
}
.reject-row {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  background: #f6f7f4;
}
.reject-row input {
  flex: 1;
  padding: 0.5rem 0.7rem;
  border: 1px solid #d9dfda;
  border-radius: 8px;
  font: inherit;
}
</style>