import { defineStore } from 'pinia'
import * as authApi from '@/api/auth.api'
import { HOME_BY_ROLE, TOKEN_KEY, USER_KEY } from '@/utils/constants'

function readUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY))
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', {
  // The data the store keeps (restored from localStorage on page refresh)
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY),
    user: readUser(),
  }),

  // Values worked out from the state
  getters: {
    isAuthenticated: (state) => !!state.token && !!state.user,
    role: (state) => state.user?.role ?? null,
    homeRoute: (state) => HOME_BY_ROLE[state.user?.role] ?? '/',
  },

  // Functions that change the state
  actions: {
    async login(credentials) {
      const { data } = await authApi.login(credentials)
      this.setSession(data.token, data.user)
      return data.user
    },

    async register(payload) {
      const { data } = await authApi.register(payload)
      return data
    },

    setSession(token, user) {
      this.token = token
      this.user = user
      localStorage.setItem(TOKEN_KEY, token)
      localStorage.setItem(USER_KEY, JSON.stringify(user))
    },

    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    },
  },
})