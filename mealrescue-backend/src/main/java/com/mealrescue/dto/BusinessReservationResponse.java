package com.mealrescue.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.mealrescue.model.Listing;
import com.mealrescue.model.Reservation;
import com.mealrescue.model.ReservationStatus;
import com.mealrescue.model.Role;
import com.mealrescue.model.User;

public record BusinessReservationResponse(
        Long id, Long listingId, String listingTitle,
        String customerName, Role customerRole,
        Integer quantity, BigDecimal totalPrice, ReservationStatus status,
        LocalDateTime pickupStart, LocalDateTime pickupEnd,
        LocalDateTime createdAt, LocalDateTime pickedUpAt) {

    public static BusinessReservationResponse from(Reservation r) {
        Listing l = r.getListing();
        User c = r.getCustomer();
        return new BusinessReservationResponse(
                r.getId(), l.getId(), l.getTitle(),
                c.getName(), c.getRole(),
                r.getQuantity(), r.getTotalPrice(), r.getStatus(),
                l.getPickupStart(), l.getPickupEnd(),
                r.getCreatedAt(), r.getPickedUpAt());
    }
}
