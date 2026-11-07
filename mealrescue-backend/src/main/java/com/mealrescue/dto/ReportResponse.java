package com.mealrescue.dto;

import java.time.LocalDateTime;

import com.mealrescue.model.Listing;
import com.mealrescue.model.Report;
import com.mealrescue.model.ReportReason;
import com.mealrescue.model.ReportStatus;

public record ReportResponse(
        Long id, Long listingId, String listingTitle,
        Long businessId, String businessName,
        ReportReason reason, String details, ReportStatus status,
        LocalDateTime createdAt) {

    public static ReportResponse from(Report r) {
        Listing l = r.getListing();
        return new ReportResponse(
                r.getId(),
                l == null ? null : l.getId(),
                l == null ? null : l.getTitle(),
                r.getBusiness().getId(), r.getBusiness().getName(),
                r.getReason(), r.getDetails(), r.getStatus(),
                r.getCreatedAt());
    }
}
