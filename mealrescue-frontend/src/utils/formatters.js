import { FOOD_CATEGORIES } from './constants'

export function formatPrice(value) {
  if (Number(value) === 0) return 'Free'
  return new Intl.NumberFormat('en-CA', { style: 'currency', currency: 'CAD' }).format(value)
}

export function formatDateTime(iso) {
  if (!iso) return ''
  return new Intl.DateTimeFormat('en-CA', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(iso))
}

// "PICKED_UP" -> "Picked up"
export function formatStatus(status) {
  if (!status) return ''
  const text = status.toLowerCase().replace(/_/g, ' ')
  return text.charAt(0).toUpperCase() + text.slice(1)
}

// "PREPARED_MEALS" -> "Prepared meals"
export function categoryLabel(value) {
  return FOOD_CATEGORIES.find((c) => c.value === value)?.label ?? value
}