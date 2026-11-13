<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deactivateUser, getUsers, reactivateUser } from '@/api/admin.api'
import { errorMessage } from '@/api/http'
import StatusBadge from '@/components/common/StatusBadge.vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import { formatDateTime, formatStatus } from '@/utils/formatters'

const filters = reactive({ q: '', role: '', status: '' })
const users = ref([])
const loading = ref(true)
const error = ref('')
const notice = ref('')

const deactivateTarget = ref(null)
const busyId = ref(null)

// only send the filters that are filled in
function params() {
  const p = {}
  if (filters.q.trim()) p.q = filters.q.trim()
  if (filters.role) p.role = filters.role
  if (filters.status) p.status = filters.status
  return p
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const { data } = await getUsers(params())
    users.value = data
  } catch (e) {
    users.value = []
    error.value = errorMessage(e, 'Could not load users.')
  } finally {
    loading.value = false
  }
}

onMounted(load)

function replace(updated) {
  users.value = users.value.map((u) => (u.id === updated.id ? updated : u))
}

const deactivateMessage = computed(() => {
  const u = deactivateTarget.value
  if (!u) return ''
  return u.role === 'BUSINESS'
    ? `${u.name} will be logged out. Their active listings and the reservations on them will be cancelled.`
    : `${u.name} will be logged out. Their open reservations will be cancelled and the food released.`
})

async function confirmDeactivate() {
  const u = deactivateTarget.value
  busyId.value = u.id
  error.value = ''
  notice.value = ''
  try {
    const { data } = await deactivateUser(u.id)
    replace(data)
    notice.value = `${data.name} was deactivated.`
  } catch (e) {
    error.value = errorMessage(e, 'Could not deactivate this account.')
  } finally {
    busyId.value = null
    deactivateTarget.value = null
  }
}

async function reactivate(u) {
  busyId.value = u.id
  error.value = ''
  notice.value = ''
  try {
    const { data } = await reactivateUser(u.id)
    replace(data)
    notice.value = `${data.name} was reactivated.`
  } catch (e) {
    error.value = errorMessage(e, 'Could not reactivate this account.')
  } finally {
    busyId.value = null
  }
}
</script>

<template>
  <h1>Users</h1>

  <form class="filters" @submit.prevent="load">
    <div class="field">
      <label for="user-q">Search</label>
      <input id="user-q" v-model="filters.q" type="search" placeholder="Name or email" />
    </div>
    <div class="field">
      <label for="user-role">Role</label>
      <select id="user-role" v-model="filters.role">
        <option value="">All roles</option>
        <option value="CUSTOMER">Customer</option>
        <option value="ORGANIZATION">Organization</option>
        <option value="BUSINESS">Business</option>
        <option value="ADMIN">Admin</option>
      </select>
    </div>
    <div class="field">
      <label for="user-status">Status</label>
      <select id="user-status" v-model="filters.status">
        <option value="">All statuses</option>
        <option value="ACTIVE">Active</option>
        <option value="PENDING">Pending</option>
        <option value="REJECTED">Rejected</option>
        <option value="DEACTIVATED">Deactivated</option>
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
    <p v-if="users.length === 0" class="muted">No users match these filters.</p>

    <div v-else class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Role</th>
            <th>Status</th>
            <th>Joined</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in users" :key="u.id">
            <td>{{ u.name }}</td>
            <td>{{ u.email }}</td>
            <td>{{ formatStatus(u.role) }}</td>
            <td><StatusBadge :status="u.status" /></td>
            <td>{{ formatDateTime(u.createdAt) }}</td>
            <td>
              <button
                v-if="u.status === 'ACTIVE' && u.role !== 'ADMIN'"
                class="btn btn-danger"
                :disabled="busyId === u.id"
                @click="deactivateTarget = u"
              >Deactivate</button>
              <button
                v-else-if="u.status === 'DEACTIVATED'"
                class="btn btn-light"
                :disabled="busyId === u.id"
                @click="reactivate(u)"
              >Reactivate</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </template>

  <ConfirmDialog
    :open="!!deactivateTarget"
    title="Deactivate this account?"
    :message="deactivateMessage"
    confirm-label="Deactivate"
    cancel-label="Keep active"
    :busy="busyId !== null"
    @confirm="confirmDeactivate"
    @cancel="deactivateTarget = null"
  />
</template>

<style scoped>
.filters {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr auto;
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
</style>