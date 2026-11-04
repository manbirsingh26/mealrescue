package com.mealrescue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ConfirmPickupRequest(
        @NotBlank(message = "Enter the pickup code.")
        @Pattern(regexp = "\\d{6}", message = "The pickup code has 6 digits.") String code) {
}
