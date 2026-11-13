package com.mealrescue.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record AdminStatsResponse(
        Map<String, Long> activeUsersByRole,
        long pendingAccounts,
        long deactivatedAccounts,
        Map<String, Long> listingsByStatus,
        Map<String, Long> reservationsByStatus,
        long portionsRescued,
        BigDecimal valueRecovered,
        Double pickupRate,
        long openReports,
        long reviewCount,
        Double averageRating,
        List<TopBusiness> topBusinesses,
        List<DailyCount> lastTwoWeeks) {

    public record TopBusiness(Long id, String name, long portions) {
    }

    public record DailyCount(LocalDate date, long portions) {
    }
}
