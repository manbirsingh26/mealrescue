package com.mealrescue.dto;

import java.time.LocalDateTime;

import com.mealrescue.model.AccountStatus;
import com.mealrescue.model.Role;
import com.mealrescue.model.User;

public record AdminUserResponse(
        Long id, String name, String email,
        Role role, AccountStatus status, LocalDateTime createdAt) {

    public static AdminUserResponse from(User u) {
        return new AdminUserResponse(
                u.getId(), u.getName(), u.getEmail(),
                u.getRole(), u.getStatus(), u.getCreatedAt());
    }
}
