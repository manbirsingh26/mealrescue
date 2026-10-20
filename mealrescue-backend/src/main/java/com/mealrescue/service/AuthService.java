package com.mealrescue.service;

import com.mealrescue.dto.AuthResponse;
import com.mealrescue.dto.LoginRequest;
import com.mealrescue.dto.UserResponse;
import com.mealrescue.security.JwtService;
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
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;    
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
        public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        // Same message for "no such email" and "wrong password", so nobody can probe which emails exist
        User user = userRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect."));

        switch (user.getStatus()) {
            case PENDING -> throw new ApiException(HttpStatus.FORBIDDEN, "Your account is waiting for admin verification.");
            case REJECTED -> throw new ApiException(HttpStatus.FORBIDDEN, "Your account request was not approved.");
            case DEACTIVATED -> throw new ApiException(HttpStatus.FORBIDDEN, "This account has been deactivated.");
            default -> { }
        }

        return new AuthResponse(jwtService.generateToken(user), UserResponse.from(user));
    }
}
