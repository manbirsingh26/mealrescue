<script setup>
import { computed, reactive, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { errorMessage } from '@/api/http'
import { isEmail, minLength } from '@/utils/validators'
import { ROLES } from '@/utils/constants'

const auth = useAuthStore()

const form = reactive({
  role: ROLES.CUSTOMER,
  name: '',
  email: '',
  password: '',
  confirmPassword: '',
  address: '',
  businessType: '',
})
const error = ref('')
const success = ref('')
const loading = ref(false)

// Businesses and organizations need extra details and admin approval
const needsVerification = computed(() => form.role !== ROLES.CUSTOMER)
const nameLabel = computed(
  () => ({ CUSTOMER: 'Full name', BUSINESS: 'Business name', ORGANIZATION: 'Organization name' })[form.role],
)

async function submit() {
  error.value = ''
  success.value = ''
  if (!form.name) return (error.value = `${nameLabel.value} is required.`)
  if (!isEmail(form.email)) return (error.value = 'Enter a valid email address.')
  if (!minLength(form.password, 8)) return (error.value = 'Password must be at least 8 characters.')
  if (form.password !== form.confirmPassword) return (error.value = 'Passwords do not match.')
  if (needsVerification.value && !form.address) return (error.value = 'Address is required.')

  loading.value = true
  try {
    const { confirmPassword, ...payload } = form // don't send the confirm field
    await auth.register(payload)
    success.value = needsVerification.value
      ? 'Account created. An admin must verify it before you can log in.'
      : 'Account created. You can log in now.'
  } catch (e) {
    error.value = errorMessage(e, 'Could not create the account.')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <h1>Create an account</h1>
  <form @submit.prevent="submit" novalidate>
    <div class="field">
      <label for="role">I am a</label>
      <select id="role" v-model="form.role">
        <option :value="ROLES.CUSTOMER">Customer</option>
        <option :value="ROLES.BUSINESS">Business</option>
        <option :value="ROLES.ORGANIZATION">Community organization</option>
      </select>
    </div>
    <div class="field">
      <label for="name">{{ nameLabel }}</label>
      <input id="name" v-model.trim="form.name" />
    </div>
    <div class="field">
      <label for="email">Email</label>
      <input id="email" v-model.trim="form.email" type="email" autocomplete="email" />
    </div>
    <template v-if="needsVerification">
      <div class="field">
        <label for="address">Address</label>
        <input id="address" v-model.trim="form.address" />
      </div>
      <div class="field">
        <label for="type">{{ form.role === ROLES.BUSINESS ? 'Business type' : 'Organization type' }}</label>
        <input id="type" v-model.trim="form.businessType" placeholder="e.g. Bakery, Food bank" />
      </div>
    </template>
    <div class="field">
      <label for="password">Password (8+ characters)</label>
      <input id="password" v-model="form.password" type="password" autocomplete="new-password" />
    </div>
    <div class="field">
      <label for="confirm">Confirm password</label>
      <input id="confirm" v-model="form.confirmPassword" type="password" autocomplete="new-password" />
    </div>
    <p v-if="error" class="error" role="alert">{{ error }}</p>
    <p v-if="success" class="success" role="status">{{ success }}</p>
    <button class="btn" :disabled="loading">{{ loading ? 'Creating...' : 'Create account' }}</button>
  </form>
  <p>Already registered? <router-link to="/login">Log in</router-link></p>
</template>