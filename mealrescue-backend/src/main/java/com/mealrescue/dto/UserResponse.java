package com.mealrescue.dto;

import com.mealrescue.model.AccountStatus;
import com.mealrescue.model.Role;
import com.mealrescue.model.User;

public record UserResponse(Long id, String name, String email, Role role, AccountStatus status) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getStatus());
    }
}
