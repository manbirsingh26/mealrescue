package com.mealrescue.dto;

import java.time.LocalDateTime;

import com.mealrescue.model.AccountStatus;
import com.mealrescue.model.Listing;
import com.mealrescue.model.ListingStatus;
import com.mealrescue.model.Report;
import com.mealrescue.model.ReportReason;
import com.mealrescue.model.ReportStatus;
import com.mealrescue.model.Role;
import com.mealrescue.model.User;

public record AdminReportResponse(
        Long id,
        ReportReason reason, String details, ReportStatus status,
        LocalDateTime createdAt, String adminNote, LocalDateTime handledAt,
        String reporterName, String reporterEmail, Role reporterRole,
        Long listingId, String listingTitle, ListingStatus listingStatus,
        Long businessId, String businessName, AccountStatus businessStatus) {

    public static AdminReportResponse from(Report r) {
        User reporter = r.getReporter();
        Listing l = r.getListing();
        User b = r.getBusiness();
        return new AdminReportResponse(
                r.getId(),
                r.getReason(), r.getDetails(), r.getStatus(),
                r.getCreatedAt(), r.getAdminNote(), r.getHandledAt(),
                reporter.getName(), reporter.getEmail(), reporter.getRole(),
                l == null ? null : l.getId(),
                l == null ? null : l.getTitle(),
                l == null ? null : l.getStatus(),
                b.getId(), b.getName(), b.getStatus());
    }
}
