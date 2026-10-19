export const ROLES = {
  CUSTOMER: 'CUSTOMER',
  ORGANIZATION: 'ORGANIZATION',
  BUSINESS: 'BUSINESS',
  ADMIN: 'ADMIN',
}

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