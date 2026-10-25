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

// Where each role goes after logging in
export const HOME_BY_ROLE = {
  CUSTOMER: '/',
  ORGANIZATION: '/',
  BUSINESS: '/business',
  ADMIN: '/admin',
}

// Keys used to save the login in the browser's localStorage
export const TOKEN_KEY = 'mealrescue_token'
export const USER_KEY = 'mealrescue_user'