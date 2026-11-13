package com.mealrescue.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mealrescue.dto.AdminUserResponse;
import com.mealrescue.dto.ListingResponse;
import com.mealrescue.model.AccountStatus;
import com.mealrescue.model.ListingStatus;
import com.mealrescue.model.Role;
import com.mealrescue.service.AdminManageService;

@RestController
public class AdminManageController {

    private final AdminManageService adminManageService;

    public AdminManageController(AdminManageService adminManageService) {
        this.adminManageService = adminManageService;
    }

    @GetMapping("/api/admin/users")
    public List<AdminUserResponse> users(@RequestParam(required = false) Role role,
                                         @RequestParam(required = false) AccountStatus status,
                                         @RequestParam(required = false) String q) {
        return adminManageService.users(role, status, q);
    }

    @PostMapping("/api/admin/users/{id}/deactivate")
    public AdminUserResponse deactivate(@PathVariable Long id) {
        return adminManageService.deactivate(id);
    }

    @PostMapping("/api/admin/users/{id}/reactivate")
    public AdminUserResponse reactivate(@PathVariable Long id) {
        return adminManageService.reactivate(id);
    }

    @GetMapping("/api/admin/listings")
    public List<ListingResponse> listings(@RequestParam(required = false) ListingStatus status,
                                          @RequestParam(required = false) String q) {
        return adminManageService.listings(status, q);
    }

    @PostMapping("/api/admin/listings/{id}/remove")
    public ListingResponse removeListing(@PathVariable Long id) {
        return adminManageService.removeListing(id);
    }
}
