<script setup>
defineProps({
  modelValue: { type: Number, default: 0 },
  readonly: { type: Boolean, default: false },
  size: { type: String, default: '1.2rem' },
})
const emit = defineEmits(['update:modelValue'])
</script>

<template>
  <span
    v-if="readonly"
    class="stars"
    role="img"
    :aria-label="`${modelValue} out of 5 stars`"
    :style="{ fontSize: size }"
  >
    <span v-for="n in 5" :key="n" :class="n <= Math.round(modelValue) ? 'on' : 'off'" aria-hidden="true">★</span>
  </span>

  <span v-else class="stars" role="radiogroup" aria-label="Rating">
    <button
      v-for="n in 5"
      :key="n"
      type="button"
      role="radio"
      :aria-checked="modelValue === n"
      :aria-label="`${n} star${n > 1 ? 's' : ''}`"
      :class="n <= modelValue ? 'on' : 'off'"
      :style="{ fontSize: size }"
      @click="emit('update:modelValue', n)"
    >★</button>
  </span>
</template>

<style scoped>
.stars {
  display: inline-flex;
  gap: 0.1rem;
  line-height: 1;
}
.on {
  color: #e0a526;
}
.off {
  color: #d0d6d2;
}
button {
  background: none;
  border: none;
  padding: 0.1rem;
  cursor: pointer;
}
button:focus-visible {
  outline: 2px solid #2f6b4f;
  border-radius: 4px;
}
</style>