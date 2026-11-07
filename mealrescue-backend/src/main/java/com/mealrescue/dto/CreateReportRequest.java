package com.mealrescue.dto;

import com.mealrescue.model.ReportReason;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReportRequest(
        Long listingId,
        Long businessId,
        @NotNull(message = "Choose a reason.") ReportReason reason,
        @Size(max = 1000, message = "Details can be at most 1000 characters.") String details) {
}
