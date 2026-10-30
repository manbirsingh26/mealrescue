<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import ListingForm from '@/components/listings/ListingForm.vue'
import { createListing, getMyListing, updateListing } from '@/api/listings.api'
import { errorMessage } from '@/api/http'

// The router passes :id from /business/listings/:id/edit; on the "new" page it is empty
const props = defineProps({ id: { type: String, default: null } })
const router = useRouter()

const isEdit = computed(() => !!props.id)
const listing = ref(null)
const loading = ref(isEdit.value)
const loadError = ref('')
const submitting = ref(false)
const serverError = ref('')

onMounted(async () => {
  if (!isEdit.value) return
  try {
    const { data } = await getMyListing(props.id)
    listing.value = data
  } catch (e) {
    loadError.value = errorMessage(e, 'Could not load this listing.')
  } finally {
    loading.value = false
  }
})

async function handleSave(payload) {
  submitting.value = true
  serverError.value = ''
  try {
    if (isEdit.value) {
      await updateListing(props.id, payload)
      router.push({ name: 'business-listings', query: { updated: '1' } })
    } else {
      await createListing(payload)
      router.push({ name: 'business-listings', query: { created: '1' } })
    }
  } catch (e) {
    serverError.value = errorMessage(e, 'Could not save the listing.')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <h1>{{ isEdit ? 'Edit listing' : 'New listing' }}</h1>

  <p v-if="loading">Loading...</p>
  <p v-else-if="loadError" class="error" role="alert">{{ loadError }}</p>
  <ListingForm
    v-else
    :initial="listing"
    :submit-label="isEdit ? 'Save changes' : 'Create listing'"
    :submitting="submitting"
    :server-error="serverError"
    @save="handleSave"
  />
</template>