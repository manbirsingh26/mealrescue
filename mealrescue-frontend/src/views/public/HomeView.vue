<script setup>
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
</script>

<template>
  <h1>MealRescue</h1>
  <p>Logged in: {{ auth.isAuthenticated }}</p>
  <p v-if="auth.isAuthenticated">{{ auth.user.name }} ({{ auth.role }})</p>
  <button v-if="auth.isAuthenticated" @click="auth.logout()">Log out</button>
</template>