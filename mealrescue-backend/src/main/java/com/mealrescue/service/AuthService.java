package com.mealrescue.service;

import com.mealrescue.dto.RegisterRequest;
import com.mealrescue.exception.ApiException;
import com.mealrescue.model.AccountProfile;
import com.mealrescue.model.AccountStatus;
import com.mealrescue.model.Role;
import com.mealrescue.model.User;
import com.mealrescue.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (request.role() == Role.ADMIN) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Admin accounts cannot be created here.");
        }

        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "An account with this email already exists.");
        }

        boolean needsVerification = request.role() != Role.CUSTOMER;
        if (needsVerification && (request.address() == null || request.address().isBlank())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Address is required.");
        }

        User user = new User();
        user.setEmail(email);
        user.setName(request.name().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setStatus(needsVerification ? AccountStatus.PENDING : AccountStatus.ACTIVE);

        if (needsVerification) {
            AccountProfile profile = new AccountProfile();
            profile.setUser(user);
            profile.setAddress(request.address().trim());
            profile.setBusinessType(request.businessType());
            user.setProfile(profile);
        }

        return userRepository.save(user);
    }
}
