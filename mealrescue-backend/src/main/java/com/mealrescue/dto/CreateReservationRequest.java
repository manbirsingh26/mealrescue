package com.mealrescue.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateReservationRequest(
        @NotNull(message = "Listing is required.") Long listingId,
        @NotNull(message = "Quantity is required.")
        @Min(value = 1, message = "Quantity must be at least 1.") Integer quantity) {
}
