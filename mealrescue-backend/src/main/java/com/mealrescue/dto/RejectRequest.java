package com.mealrescue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectRequest(
        @NotBlank(message = "A reason is required") @Size(max = 500, message = "The reason is too long") String reason) {
}
