import http from '@/api/http'

export const createReview = (reservationId, rating, comment) =>
  http.post(`/reservations/${reservationId}/review`, { rating, comment })

export const getBusinessReviews = (businessId) => http.get(`/businesses/${businessId}/reviews`)