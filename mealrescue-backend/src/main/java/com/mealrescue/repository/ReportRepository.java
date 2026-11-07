package com.mealrescue.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mealrescue.model.Report;
import com.mealrescue.model.ReportStatus;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporterIdAndListingIdAndStatus(Long reporterId, Long listingId, ReportStatus status);

    boolean existsByReporterIdAndBusinessIdAndListingIsNullAndStatus(
            Long reporterId, Long businessId, ReportStatus status);
}
