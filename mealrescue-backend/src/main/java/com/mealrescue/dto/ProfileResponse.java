package com.mealrescue.dto;

import com.mealrescue.model.AccountProfile;
import com.mealrescue.model.AccountStatus;
import com.mealrescue.model.Role;
import com.mealrescue.model.User;

public record ProfileResponse(
        Long id, String name, String email, Role role, AccountStatus status,
        String address, String businessType) {

    public static ProfileResponse from(User u) {
        AccountProfile p = u.getProfile();
        return new ProfileResponse(
                u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getStatus(),
                p != null ? p.getAddress() : null,
                p != null ? p.getBusinessType() : null);
    }
}
