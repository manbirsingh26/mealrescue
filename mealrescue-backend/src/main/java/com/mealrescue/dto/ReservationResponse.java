package com.mealrescue.dto;

import com.mealrescue.model.AccountProfile;
import com.mealrescue.model.Listing;
import com.mealrescue.model.Reservation;
import com.mealrescue.model.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationResponse(
        Long id, Long listingId, String listingTitle, String businessName, String businessAddress,
        Integer quantity, BigDecimal totalPrice, ReservationStatus status,
        String pickupCode, LocalDateTime pickupStart, LocalDateTime pickupEnd,
        LocalDateTime createdAt, boolean reviewed) {

    public static ReservationResponse from(Reservation r) {
        return from(r, false);
    }

    public static ReservationResponse from(Reservation r, boolean reviewed) {
        Listing l = r.getListing();
        AccountProfile profile = l.getBusiness().getProfile();

        return new ReservationResponse(
                r.getId(), l.getId(), l.getTitle(), l.getBusiness().getName(),
                profile != null ? profile.getAddress() : null,
                r.getQuantity(), r.getTotalPrice(), r.getStatus(),
                r.getPickupCode(), l.getPickupStart(), l.getPickupEnd(),
                r.getCreatedAt(), reviewed);
    }
}