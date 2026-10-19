<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { errorMessage } from '@/api/http'
import { isEmail } from '@/utils/validators'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const form = reactive({ email: '', password: '' })
const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  if (!isEmail(form.email) || !form.password) {
    error.value = 'Enter a valid email and your password.'
    return
  }
  loading.value = true
  try {
    await auth.login({ email: form.email, password: form.password })
    router.push(route.query.redirect || auth.homeRoute)
  } catch (e) {
    error.value = errorMessage(e, 'Email or password is incorrect.')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <h1>Log in</h1>
  <form @submit.prevent="submit" novalidate>
    <div class="field">
      <label for="email">Email</label>
      <input id="email" v-model.trim="form.email" type="email" autocomplete="email" />
    </div>
    <div class="field">
      <label for="password">Password</label>
      <input id="password" v-model="form.password" type="password" autocomplete="current-password" />
    </div>
    <p v-if="error" class="error" role="alert">{{ error }}</p>
    <button class="btn" :disabled="loading">{{ loading ? 'Logging in...' : 'Log in' }}</button>
  </form>
  <p>New here? <router-link to="/register">Create an account</router-link></p>
</template>