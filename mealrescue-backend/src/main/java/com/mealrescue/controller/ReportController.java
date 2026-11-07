package com.mealrescue.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mealrescue.dto.CreateReportRequest;
import com.mealrescue.dto.ReportResponse;
import com.mealrescue.model.User;
import com.mealrescue.service.ReportService;

import jakarta.validation.Valid;

@RestController
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/api/reports")
    public ResponseEntity<ReportResponse> create(@AuthenticationPrincipal User reporter,
                                                 @Valid @RequestBody CreateReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reportService.create(reporter.getId(), request));
    }
}