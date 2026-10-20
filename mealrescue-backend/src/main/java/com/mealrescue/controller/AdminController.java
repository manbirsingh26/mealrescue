package com.mealrescue.controller;

import com.mealrescue.dto.PendingAccountResponse;
import com.mealrescue.dto.RejectRequest;
import com.mealrescue.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/pending-accounts")
    public List<PendingAccountResponse> pendingAccounts() {
        return adminService.getPendingAccounts();
    }

    @PostMapping("/accounts/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        adminService.approve(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/accounts/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long id, @Valid @RequestBody RejectRequest request) {
        adminService.reject(id, request.reason());
        return ResponseEntity.noContent().build();
    }
}
