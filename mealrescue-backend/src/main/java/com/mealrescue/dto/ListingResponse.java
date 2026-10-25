package com.mealrescue.dto;

import com.mealrescue.model.FoodCategory;
import com.mealrescue.model.Listing;
import com.mealrescue.model.ListingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ListingResponse(
        Long id, Long businessId, String businessName,
        String title, String description, FoodCategory category,
        int quantityTotal, int quantityRemaining, BigDecimal price,
        LocalDateTime pickupStart, LocalDateTime pickupEnd,
        ListingStatus status, LocalDateTime createdAt) {

    public static ListingResponse from(Listing l) {
        return new ListingResponse(
                l.getId(), l.getBusiness().getId(), l.getBusiness().getName(),
                l.getTitle(), l.getDescription(), l.getCategory(),
                l.getQuantityTotal(), l.getQuantityRemaining(), l.getPrice(),
                l.getPickupStart(), l.getPickupEnd(), l.getStatus(), l.getCreatedAt());
    }
}
