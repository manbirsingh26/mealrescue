package com.mealrescue.dto;

import com.mealrescue.model.FoodCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateListingRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 120, message = "Title must be at most 120 characters") String title,

        @NotBlank(message = "Description is required")
        @Size(max = 1000, message = "Description must be at most 1000 characters") String description,

        @NotNull(message = "Category is required") FoodCategory category,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 1000, message = "Quantity cannot be more than 1000") Integer quantity,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00", message = "Price cannot be negative")
        @Digits(integer = 6, fraction = 2, message = "Price can have at most 2 decimal places") BigDecimal price,

        @NotNull(message = "Pickup start time is required") LocalDateTime pickupStart,
        @NotNull(message = "Pickup end time is required") LocalDateTime pickupEnd) {
}