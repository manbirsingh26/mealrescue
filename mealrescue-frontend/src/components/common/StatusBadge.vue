<script setup>
import { computed } from 'vue'
import { formatStatus } from '@/utils/formatters'

const props = defineProps({ status: { type: String, required: true } })

const tone = computed(() => {
  if (['ACTIVE', 'RESERVED', 'PICKED_UP'].includes(props.status)) return 'good'
  if (props.status === 'PENDING') return 'warn'
  return 'bad'
})
</script>

<template>
  <span class="badge" :class="tone">{{ formatStatus(status) }}</span>
</template>

<style scoped>
.badge {
  display: inline-block;
  padding: 0.1rem 0.6rem;
  border-radius: 999px;
  font-size: 0.85rem;
  border: 1px solid;
}
.good { color: #1f5c3f; background: #e4f1ea; border-color: #b8d8c6; }
.warn { color: #7a5200; background: #fbf0d6; border-color: #ebd49b; }
.bad { color: #8f2f21; background: #f8e5e1; border-color: #e6bcb4; }
</style>