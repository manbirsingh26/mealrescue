package com.mealrescue.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.mealrescue.model.Listing;
import com.mealrescue.model.Reservation;
import com.mealrescue.model.ReservationStatus;

public record ReservationResponse(
        Long id, Long listingId, String listingTitle, String businessName,
        Integer quantity, BigDecimal totalPrice, ReservationStatus status,
        String pickupCode, LocalDateTime pickupStart, LocalDateTime pickupEnd,
        LocalDateTime createdAt) {

    public static ReservationResponse from(Reservation r) {
        Listing l = r.getListing();
        return new ReservationResponse(
                r.getId(), l.getId(), l.getTitle(), l.getBusiness().getName(),
                r.getQuantity(), r.getTotalPrice(), r.getStatus(),
                r.getPickupCode(), l.getPickupStart(), l.getPickupEnd(),
                r.getCreatedAt());
    }
}
