<script setup>
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
</script>

<template>
  <h1>MealRescue</h1>
  <p>Frontend is working.</p>
  <p>Logged in: {{ auth.isAuthenticated }}</p>
  <p>Home route: {{ auth.homeRoute }}</p>
</template>