package com.mealrescue.controller;

import com.mealrescue.dto.ProfileResponse;
import com.mealrescue.dto.UpdateProfileRequest;
import com.mealrescue.dto.UserResponse;
import com.mealrescue.model.User;
import com.mealrescue.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal User user) {
        return UserResponse.from(user);
    }

    @GetMapping("/me/profile")
    public ProfileResponse profile(@AuthenticationPrincipal User user) {
        return userService.getProfile(user.getId());
    }

    @PutMapping("/me/profile")
    public ProfileResponse updateProfile(@AuthenticationPrincipal User user,
                                         @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(user.getId(), request);
    }

    @PostMapping("/me/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal User user) {
        userService.logout(user.getId());
        return ResponseEntity.noContent().build();
    }
}