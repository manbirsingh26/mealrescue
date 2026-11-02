import http from './http'

export const createReservation = (listingId, quantity) =>
  http.post('/reservations', { listingId, quantity })

export const getMyReservations = () => http.get('/reservations')
export const cancelReservation = (id) => http.post(`/reservations/${id}/cancel`)