export const ROLES = {
  CUSTOMER: 'CUSTOMER',
  ORGANIZATION: 'ORGANIZATION',
  BUSINESS: 'BUSINESS',
  ADMIN: 'ADMIN',
}

export const FOOD_CATEGORIES = [
  { value: 'BAKERY', label: 'Bakery' },
  { value: 'PREPARED_MEALS', label: 'Prepared meals' },
  { value: 'PRODUCE', label: 'Produce' },
  { value: 'DAIRY', label: 'Dairy' },
  { value: 'GROCERIES', label: 'Groceries' },
  { value: 'BEVERAGES', label: 'Beverages' },
  { value: 'OTHER', label: 'Other' },
]

export const REPORT_REASONS = [
  { value: 'UNSAFE_FOOD', label: 'Unsafe or spoiled food' },
  { value: 'WRONG_INFORMATION', label: 'Wrong or misleading information' },
  { value: 'NOT_AVAILABLE_AT_PICKUP', label: 'Food was not available at pickup' },
  { value: 'RUDE_OR_INAPPROPRIATE', label: 'Rude or inappropriate behaviour' },
  { value: 'OTHER', label: 'Other' },
]

// Where each role goes after logging in
export const HOME_BY_ROLE = {
  CUSTOMER: '/listings',
  ORGANIZATION: '/listings',
  BUSINESS: '/business',
  ADMIN: '/admin',
}

// Keys used to save the login in the browser's localStorage
export const TOKEN_KEY = 'mealrescue_token'
export const USER_KEY = 'mealrescue_user'

// must match CUSTOMER_LIMIT and ORGANIZATION_LIMIT in ReservationService.java
export const RESERVATION_LIMITS = {
  CUSTOMER: 10,
  ORGANIZATION: 50,
}