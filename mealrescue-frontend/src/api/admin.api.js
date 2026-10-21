import http from './http'

export const getPendingAccounts = () => http.get('/admin/pending-accounts')
export const approveAccount = (id) => http.post(`/admin/accounts/${id}/approve`)
export const rejectAccount = (id, reason) => http.post(`/admin/accounts/${id}/reject`, { reason })