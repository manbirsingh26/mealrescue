package com.mealrescue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mealrescue.model.Report;
import com.mealrescue.model.ReportStatus;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporterIdAndListingIdAndStatus(Long reporterId, Long listingId, ReportStatus status);

    boolean existsByReporterIdAndBusinessIdAndListingIsNullAndStatus(
            Long reporterId, Long businessId, ReportStatus status);

    @EntityGraph(attributePaths = {"reporter", "listing", "business"})
    List<Report> findByStatusOrderByCreatedAtAsc(ReportStatus status);

    @EntityGraph(attributePaths = {"reporter", "listing", "business"})
    List<Report> findByStatusOrderByHandledAtDesc(ReportStatus status);

    @EntityGraph(attributePaths = {"reporter", "listing", "business"})
    List<Report> findAllByOrderByCreatedAtDesc();

    long countByStatus(ReportStatus status);

    @EntityGraph(attributePaths = {"listing", "business"})
    List<Report> findByReporterIdOrderByCreatedAtDesc(Long reporterId);

        }
