<script setup>
import { nextTick, reactive, ref, watch } from 'vue'
import { createReport } from '@/api/reports.api'
import { errorMessage } from '@/api/http'
import { REPORT_REASONS } from '@/utils/constants'

// target: { listingId, title } or { businessId, title }; null = closed
const props = defineProps({ target: { type: Object, default: null } })
const emit = defineEmits(['close', 'sent'])

const form = reactive({ reason: '', details: '' })
const sending = ref(false)
const error = ref('')
const firstField = ref(null)

// reset the form each time the dialog opens, then focus the first field
watch(
  () => props.target,
  async (t) => {
    if (!t) return
    form.reason = ''
    form.details = ''
    error.value = ''
    await nextTick()
    firstField.value?.focus()
  },
)

async function submit() {
  error.value = ''
  if (!form.reason) {
    error.value = 'Choose a reason.'
    return
  }
  if (form.reason === 'OTHER' && !form.details.trim()) {
    error.value = 'Please describe the problem.'
    return
  }

  sending.value = true
  try {
    await createReport({
      listingId: props.target.listingId ?? null,
      businessId: props.target.businessId ?? null,
      reason: form.reason,
      details: form.details,
    })
    emit('sent')
  } catch (e) {
    error.value = errorMessage(e, 'Could not send the report.')
  } finally {
    sending.value = false
  }
}
</script>

<template>
  <div v-if="target" class="overlay" @click.self="emit('close')" @keydown.esc="emit('close')">
    <form class="dialog" role="dialog" aria-modal="true" aria-labelledby="report-title" @submit.prevent="submit">
      <h2 id="report-title">{{ target.title }}</h2>
      <p class="muted">An admin will look at your report. The business won't see who sent it.</p>

      <div class="field">
        <label for="report-reason">Reason</label>
        <select id="report-reason" ref="firstField" v-model="form.reason">
          <option value="" disabled>Choose a reason</option>
          <option v-for="r in REPORT_REASONS" :key="r.value" :value="r.value">{{ r.label }}</option>
        </select>
      </div>

      <div class="field">
        <label for="report-details">
          Details <span class="muted">({{ form.reason === 'OTHER' ? 'required' : 'optional' }})</span>
        </label>
        <textarea id="report-details" v-model="form.details" rows="4" maxlength="1000"></textarea>
        <p class="muted counter">{{ form.details.length }} / 1000</p>
      </div>

      <p v-if="error" class="error" role="alert">{{ error }}</p>

      <div class="buttons">
        <button class="btn btn-danger" :disabled="sending">{{ sending ? 'Sending...' : 'Send report' }}</button>
        <button type="button" class="btn btn-light" @click="emit('close')">Close</button>
      </div>
    </form>
  </div>
</template>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(20, 30, 25, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  z-index: 50;
}
.dialog {
  background: #fff;
  border-radius: 12px;
  padding: 1.25rem 1.5rem;
  width: 100%;
  max-width: 460px;
  max-height: 90vh;
  overflow-y: auto;
}
h2 {
  margin-top: 0;
}
.muted {
  color: #5d6a63;
  font-weight: 400;
}
textarea {
  display: block;
  width: 100%;
  box-sizing: border-box;
  font: inherit;
  padding: 0.5rem;
}
.counter {
  font-size: 0.8rem;
  text-align: right;
  margin: 0.25rem 0 0;
}
.buttons {
  display: flex;
  gap: 0.75rem;
  margin-top: 0.5rem;
}
</style>