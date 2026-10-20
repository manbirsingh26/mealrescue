package com.mealrescue.dto;

import com.mealrescue.model.AccountProfile;
import com.mealrescue.model.Role;
import com.mealrescue.model.User;

import java.time.LocalDateTime;

public record PendingAccountResponse(Long id, String name, String email, Role role,
                                     String address, String businessType, LocalDateTime createdAt) {

    public static PendingAccountResponse from(User user) {
        AccountProfile profile = user.getProfile();
        return new PendingAccountResponse(
                user.getId(), user.getName(), user.getEmail(), user.getRole(),
                profile != null ? profile.getAddress() : null,
                profile != null ? profile.getBusinessType() : null,
                user.getCreatedAt());
    }
}
