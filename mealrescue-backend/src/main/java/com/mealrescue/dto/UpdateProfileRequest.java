package com.mealrescue.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "Name is required.")
        @Size(max = 100, message = "Name can be at most 100 characters.") String name,
        @NotBlank(message = "Email is required.")
        @Email(message = "Enter a valid email address.") String email,
        @Size(max = 255, message = "Address can be at most 255 characters.") String address,
        @Size(max = 100, message = "Type can be at most 100 characters.") String businessType) {
}