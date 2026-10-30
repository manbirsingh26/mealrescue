<script setup>
defineProps({
  open: { type: Boolean, default: false },
  title: { type: String, default: 'Are you sure?' },
  message: { type: String, default: '' },
  confirmLabel: { type: String, default: 'Confirm' },
  cancelLabel: { type: String, default: 'Keep it' },
  busy: { type: Boolean, default: false },
})
defineEmits(['confirm', 'cancel'])
</script>

<template>
  <div v-if="open" class="backdrop" @click.self="$emit('cancel')">
    <div class="dialog" role="dialog" aria-modal="true">
      <h2>{{ title }}</h2>
      <p v-if="message">{{ message }}</p>
      <div class="buttons">
        <button class="btn btn-light" :disabled="busy" @click="$emit('cancel')">{{ cancelLabel }}</button>
        <button class="btn btn-danger" :disabled="busy" @click="$emit('confirm')">{{ confirmLabel }}</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: grid;
  place-items: center;
  z-index: 50;
}
.dialog {
  background: #fff;
  border-radius: 8px;
  padding: 1.25rem 1.5rem;
  max-width: 420px;
  width: calc(100% - 2rem);
}
.buttons {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}
</style>