import axios from 'axios'
import { TOKEN_KEY, USER_KEY } from '@/utils/constants'

const http = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

// Before every request, attach the login token if we have one
http.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// If the server says "not logged in" (401), clear the login and go to the login page
http.interceptors.response.use(
  (response) => response,
  (error) => {
    const isAuthCall = error.config.url.includes('/auth/')
    if (error.response?.status === 401 && !isAuthCall) {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
      window.location.href = '/login'
    }
    return Promise.reject(error)
  },
)

// Gets a readable error message from a failed request
export function errorMessage(error, fallback = 'Something went wrong. Please try again.') {
  if (!error?.response) return 'Cannot reach the server. Please try again later.'
  return error.response.data?.message || fallback
}

export default http