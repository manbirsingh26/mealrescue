import http from './http'

export const createReservation = (listingId, quantity) =>
  http.post('/reservations', { listingId, quantity })