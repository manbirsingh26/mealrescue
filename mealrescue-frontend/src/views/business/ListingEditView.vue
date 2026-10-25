<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import ListingForm from '@/components/listings/ListingForm.vue'
import { createListing } from '@/api/listings.api'
import { errorMessage } from '@/api/http'

const router = useRouter()
const submitting = ref(false)
const serverError = ref('')

async function handleSave(payload) {
  submitting.value = true
  serverError.value = ''
  try {
    await createListing(payload)
    router.push({ name: 'business-listings', query: { created: '1' } })
  } catch (e) {
    serverError.value = errorMessage(e, 'Could not create the listing.')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <h1>New listing</h1>
  <ListingForm :submitting="submitting" :server-error="serverError" @save="handleSave" />
</template>