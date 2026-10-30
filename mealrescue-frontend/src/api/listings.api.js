import http from './http'

export const createListing = (payload) => http.post('/listings', payload)
export const getMyListings = () => http.get('/business/listings')
export const getMyListing = (id) => http.get(`/business/listings/${id}`)
export const updateListing = (id, payload) => http.put(`/listings/${id}`, payload)
export const cancelListing = (id) => http.post(`/listings/${id}/cancel`)