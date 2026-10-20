package com.mealrescue.dto;

public record AuthResponse(String token, UserResponse user) {
}