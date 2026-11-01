<script setup>
import { onMounted, ref } from 'vue'
import ListingCard from '@/components/listings/ListingCard.vue'
import ListingFilters from '@/components/listings/ListingFilters.vue'
import { searchListings } from '@/api/listings.api'
import { errorMessage } from '@/api/http'

const listings = ref([])
const loading = ref(true)
const error = ref('')

async function load(params = { sort: 'ending' }) {
  loading.value = true
  error.value = ''
  try {
    const { data } = await searchListings(params)
    listings.value = data
  } catch (e) {
    listings.value = []
    error.value = errorMessage(e, 'Could not load the listings.')
  } finally {
    loading.value = false
  }
}

onMounted(() => load())
</script>

<template>
  <h1>Browse food</h1>
  <p class="intro">Surplus food available for pickup right now.</p>

  <ListingFilters :busy="loading" @search="load" />

  <p v-if="error" class="error" role="alert">{{ error }}</p>
  <p v-if="loading">Loading...</p>

  <template v-else-if="!error">
    <p v-if="listings.length === 0">
      No listings match your search. Try removing a filter.
    </p>
    <template v-else>
      <p class="count">{{ listings.length }} {{ listings.length === 1 ? 'listing' : 'listings' }} found</p>
      <div class="grid">
        <ListingCard v-for="l in listings" :key="l.id" :listing="l" />
      </div>
    </template>
  </template>
</template>

<style scoped>
.intro {
  color: #5d6a63;
}
.count {
  color: #5d6a63;
}
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1rem;
}
</style>