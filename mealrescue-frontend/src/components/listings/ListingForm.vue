<script setup>
import { reactive, ref } from 'vue'
import { FOOD_CATEGORIES } from '@/utils/constants'

defineProps({
  submitting: { type: Boolean, default: false },
  serverError: { type: String, default: '' },
})
const emit = defineEmits(['save'])

const form = reactive({
  title: '',
  description: '',
  category: '',
  quantity: null,
  price: null,
  pickupStart: '',
  pickupEnd: '',
})
const error = ref('')

// <input type="datetime-local"> gives "2026-12-01T17:00"; the back end likes seconds too
const toIso = (value) => (value.length === 16 ? `${value}:00` : value)

function save() {
  error.value = ''
  if (!form.title.trim()) return (error.value = 'Title is required.')
  if (!form.description.trim()) return (error.value = 'Description is required.')
  if (!form.category) return (error.value = 'Choose a category.')
  if (!Number.isInteger(form.quantity) || form.quantity < 1)
    return (error.value = 'Quantity must be a whole number of at least 1.')
  if (form.price === null || form.price === '' || Number(form.price) < 0)
    return (error.value = 'Price must be 0 or more.')
  if (!form.pickupStart || !form.pickupEnd) return (error.value = 'Pickup start and end times are required.')
  if (new Date(form.pickupEnd) <= new Date(form.pickupStart))
    return (error.value = 'Pickup end time must be after the start time.')
  if (new Date(form.pickupEnd) <= new Date()) return (error.value = 'Pickup end time must be in the future.')

  emit('save', {
    title: form.title.trim(),
    description: form.description.trim(),
    category: form.category,
    quantity: form.quantity,
    price: Number(form.price),
    pickupStart: toIso(form.pickupStart),
    pickupEnd: toIso(form.pickupEnd),
  })
}
</script>

<template>
  <form class="listing-form" @submit.prevent="save" novalidate>
    <div class="field">
      <label for="title">Title</label>
      <input id="title" v-model="form.title" maxlength="120" placeholder="e.g. Day-old bread" />
    </div>
    <div class="field">
      <label for="description">Description</label>
      <textarea id="description" v-model="form.description" rows="3" maxlength="1000"></textarea>
    </div>
    <div class="field">
      <label for="category">Category</label>
      <select id="category" v-model="form.category">
        <option value="" disabled>Choose a category</option>
        <option v-for="c in FOOD_CATEGORIES" :key="c.value" :value="c.value">{{ c.label }}</option>
      </select>
    </div>
    <div class="row">
      <div class="field">
        <label for="quantity">Quantity</label>
        <input id="quantity" v-model.number="form.quantity" type="number" min="1" step="1" />
      </div>
      <div class="field">
        <label for="price">Price (CAD, 0 for free)</label>
        <input id="price" v-model.number="form.price" type="number" min="0" step="0.01" />
      </div>
    </div>
    <div class="row">
      <div class="field">
        <label for="start">Pickup starts</label>
        <input id="start" v-model="form.pickupStart" type="datetime-local" />
      </div>
      <div class="field">
        <label for="end">Pickup ends</label>
        <input id="end" v-model="form.pickupEnd" type="datetime-local" />
      </div>
    </div>

    <p v-if="error || serverError" class="error" role="alert">{{ error || serverError }}</p>
    <button class="btn" :disabled="submitting">{{ submitting ? 'Saving...' : 'Create listing' }}</button>
  </form>
</template>

<style scoped>
.listing-form {
  max-width: 560px;
}
.row {
  display: flex;
  gap: 1rem;
}
.row .field {
  flex: 1;
}
</style>