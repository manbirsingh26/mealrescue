import http from './http'

export const getPendingAccounts = () => http.get('/admin/pending-accounts')
export const approveAccount = (id) => http.post(`/admin/accounts/${id}/approve`)
export const rejectAccount = (id, reason) => http.post(`/admin/accounts/${id}/reject`, { reason })
export const getReports = (status) =>
  http.get('/admin/reports', { params: status ? { status } : {} })

export const resolveReport = (id, note) => http.post(`/admin/reports/${id}/resolve`, { note })

export const dismissReport = (id, note) => http.post(`/admin/reports/${id}/dismiss`, { note })

export const getUsers = (params) => http.get('/admin/users', { params })
export const deactivateUser = (id) => http.post(`/admin/users/${id}/deactivate`)
export const reactivateUser = (id) => http.post(`/admin/users/${id}/reactivate`)

export const getAdminListings = (params) => http.get('/admin/listings', { params })
export const removeListing = (id) => http.post(`/admin/listings/${id}/remove`)