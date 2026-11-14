import http from './http'

export const login = (credentials) => http.post('/auth/login', credentials)
export const register = (payload) => http.post('/auth/register', payload)
export const logout = () => http.post('/users/me/logout')