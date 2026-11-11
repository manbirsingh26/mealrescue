package com.mealrescue.dto;

import jakarta.validation.constraints.Size;

public record HandleReportRequest(
        @Size(max = 1000, message = "The note can be at most 1000 characters.") String note) {
}
