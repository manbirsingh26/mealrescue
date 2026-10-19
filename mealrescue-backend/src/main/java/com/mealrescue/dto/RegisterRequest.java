package com.mealrescue.dto;

import com.mealrescue.model.Role;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotNull(message = "Account type is required") Role role,
        @NotBlank(message = "Name is required") @Size(max = 150, message = "Name is too long") String name,
        @NotBlank(message = "Email is required") @Email(message = "Enter a valid email address") String email,
        @NotBlank(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters") String password,
        String address,
        String businessType) {
}