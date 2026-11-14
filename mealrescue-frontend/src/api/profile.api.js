import http from './http'

export const getProfile = () => http.get('/users/me/profile')
export const updateProfile = (payload) => http.put('/users/me/profile', payload)
export const getMyReports = () => http.get('/reports')