package com.mealrescue.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReviewRequest(
        @NotNull(message = "Choose a rating.")
        @Min(value = 1, message = "The rating must be between 1 and 5.")
        @Max(value = 5, message = "The rating must be between 1 and 5.") Integer rating,
        @Size(max = 500, message = "The comment can be at most 500 characters.") String comment) {
}
