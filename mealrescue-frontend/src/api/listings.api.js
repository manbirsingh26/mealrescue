import http from './http'

export const createListing = (payload) => http.post('/listings', payload)
export const getMyListings = () => http.get('/business/listings')