package com.mealrescue.dto;

import java.time.LocalDateTime;

import com.mealrescue.model.Review;

public record ReviewResponse(
        Long id, Integer rating, String comment,
        String customerName, String listingTitle, LocalDateTime createdAt) {

    public static ReviewResponse from(Review r) {
        return new ReviewResponse(
                r.getId(), r.getRating(), r.getComment(),
                r.getCustomer().getName(), r.getReservation().getListing().getTitle(),
                r.getCreatedAt());
    }
}
