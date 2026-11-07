import http from '@/api/http'

export const createReport = (payload) => http.post('/reports', payload)