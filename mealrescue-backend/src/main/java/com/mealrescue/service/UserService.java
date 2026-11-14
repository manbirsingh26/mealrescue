package com.mealrescue.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mealrescue.dto.ProfileResponse;
import com.mealrescue.dto.UpdateProfileRequest;
import com.mealrescue.exception.ApiException;
import com.mealrescue.model.AccountProfile;
import com.mealrescue.model.Role;
import com.mealrescue.model.User;
import com.mealrescue.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        return ProfileResponse.from(find(userId));
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = find(userId);

        String email = request.email().trim();
        if (!email.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "This email is already in use.");
        }

        String address = blankToNull(request.address());
        if (user.getRole() == Role.BUSINESS && address == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "A business needs a pickup address.");
        }

        user.setName(request.name().trim());
        user.setEmail(email);

        // create a profile the first time there is something to put in it
        AccountProfile profile = user.getProfile();
        if (profile == null && (address != null || request.businessType() != null)) {
            profile = new AccountProfile();
            profile.setUser(user);
            user.setProfile(profile);
        }
        if (profile != null) {
            profile.setAddress(address);
            // only businesses and organizations have a type
            if (user.getRole() == Role.BUSINESS || user.getRole() == Role.ORGANIZATION) {
                profile.setBusinessType(blankToNull(request.businessType()));
            }
        }

        return ProfileResponse.from(user);
    }

    @Transactional
    public void logout(Long userId) {
        find(userId).bumpTokenVersion();
    }

    private User find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found."));
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
