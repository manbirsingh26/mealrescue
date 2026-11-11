package com.mealrescue.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mealrescue.dto.AdminReportResponse;
import com.mealrescue.dto.HandleReportRequest;
import com.mealrescue.model.ReportStatus;
import com.mealrescue.service.ReportService;

import jakarta.validation.Valid;

@RestController
public class AdminReportController {

    private final ReportService reportService;

    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/api/admin/reports")
    public List<AdminReportResponse> list(@RequestParam(required = false) ReportStatus status) {
        return reportService.adminList(status);
    }

    @PostMapping("/api/admin/reports/{id}/resolve")
    public AdminReportResponse resolve(@PathVariable Long id,
                                       @Valid @RequestBody(required = false) HandleReportRequest request) {
        return reportService.handle(id, ReportStatus.RESOLVED, request == null ? null : request.note());
    }

    @PostMapping("/api/admin/reports/{id}/dismiss")
    public AdminReportResponse dismiss(@PathVariable Long id,
                                       @Valid @RequestBody(required = false) HandleReportRequest request) {
        return reportService.handle(id, ReportStatus.DISMISSED, request == null ? null : request.note());
    }
}
