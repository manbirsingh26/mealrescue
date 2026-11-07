package com.mealrescue.dto;

import java.util.List;

public record BusinessReviewsResponse(
        Long businessId, String businessName,
        Double averageRating, int reviewCount,
        List<ReviewResponse> reviews) {
}
