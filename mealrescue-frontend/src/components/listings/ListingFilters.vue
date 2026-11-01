<script setup>
import { reactive } from 'vue'
import { FOOD_CATEGORIES } from '@/utils/constants'

defineProps({ busy: { type: Boolean, default: false } })
const emit = defineEmits(['search'])

const empty = () => ({ q: '', category: '', maxPrice: '', pickupBefore: '', sort: 'ending' })
const form = reactive(empty())

// Only send the filters the customer actually used
function toParams() {
  const params = { sort: form.sort }
  if (form.q.trim()) params.q = form.q.trim()
  if (form.category) params.category = form.category
  if (form.maxPrice !== '' && form.maxPrice !== null) params.maxPrice = form.maxPrice
  if (form.pickupBefore) {
    // datetime-local gives "2026-12-01T17:00"; the back end likes seconds too
    params.pickupBefore = form.pickupBefore.length === 16 ? `${form.pickupBefore}:00` : form.pickupBefore
  }
  return params
}

function submit() {
  emit('search', toParams())
}

function clear() {
  Object.assign(form, empty())
  submit()
}
</script>

<template>
  <form class="filters" @submit.prevent="submit">
    <div class="field wide">
      <label for="q">Search</label>
      <input id="q" v-model="form.q" type="search" placeholder="Title or business name" />
    </div>
    <div class="field">
      <label for="category">Category</label>
      <select id="category" v-model="form.category">
        <option value="">All categories</option>
        <option v-for="c in FOOD_CATEGORIES" :key="c.value" :value="c.value">{{ c.label }}</option>
      </select>
    </div>
    <div class="field">
      <label for="maxPrice">Max price (CAD)</label>
      <input id="maxPrice" v-model.number="form.maxPrice" type="number" min="0" step="0.01" placeholder="Any" />
    </div>
    <div class="field">
      <label for="pickupBefore">Pickup starts by</label>
      <input id="pickupBefore" v-model="form.pickupBefore" type="datetime-local" />
    </div>
    <div class="field">
      <label for="sort">Sort by</label>
      <select id="sort" v-model="form.sort">
        <option value="ending">Ending soonest</option>
        <option value="price">Lowest price</option>
        <option value="newest">Newest</option>
      </select>
    </div>
    <div class="buttons">
      <button class="btn" :disabled="busy">Search</button>
      <button type="button" class="btn btn-light" :disabled="busy" @click="clear">Clear filters</button>
    </div>
  </form>
</template>

<style scoped>
.filters {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(190px, 1fr));
  gap: 0 1rem;
  background: #fff;
  border: 1px solid #d9dfda;
  border-radius: 10px;
  padding: 1rem 1.25rem 0.25rem;
  margin-bottom: 1.5rem;
}
.wide,
.buttons {
  grid-column: 1 / -1;
}
.buttons {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1rem;
}
</style>