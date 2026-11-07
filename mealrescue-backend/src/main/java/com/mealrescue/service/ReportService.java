package com.mealrescue.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mealrescue.dto.CreateReportRequest;
import com.mealrescue.dto.ReportResponse;
import com.mealrescue.exception.ApiException;
import com.mealrescue.model.Listing;
import com.mealrescue.model.Report;
import com.mealrescue.model.ReportReason;
import com.mealrescue.model.ReportStatus;
import com.mealrescue.model.Role;
import com.mealrescue.model.User;
import com.mealrescue.repository.ListingRepository;
import com.mealrescue.repository.ReportRepository;
import com.mealrescue.repository.UserRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository,
                         ListingRepository listingRepository,
                         UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReportResponse create(Long reporterId, CreateReportRequest request) {
        boolean aboutListing = request.listingId() != null;
        if (aboutListing == (request.businessId() != null)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Report either a listing or a business.");
        }

        String details = request.details() == null || request.details().isBlank()
                ? null : request.details().trim();
        if (request.reason() == ReportReason.OTHER && details == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Please describe the problem.");
        }

        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "Your account cannot send reports."));

        Listing listing = null;
        User business;

        if (aboutListing) {
            listing = listingRepository.findById(request.listingId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Listing not found."));
            business = listing.getBusiness();
            if (reportRepository.existsByReporterIdAndListingIdAndStatus(
                    reporterId, listing.getId(), ReportStatus.OPEN)) {
                throw new ApiException(HttpStatus.CONFLICT,
                        "You have already reported this listing. An admin will review it.");
            }
        } else {
            business = userRepository.findById(request.businessId())
                    .filter(u -> u.getRole() == Role.BUSINESS)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Business not found."));
            if (reportRepository.existsByReporterIdAndBusinessIdAndListingIsNullAndStatus(
                    reporterId, business.getId(), ReportStatus.OPEN)) {
                throw new ApiException(HttpStatus.CONFLICT,
                        "You have already reported this business. An admin will review it.");
            }
        }

        Report report = new Report();
        report.setReporter(reporter);
        report.setListing(listing);
        report.setBusiness(business);
        report.setReason(request.reason());
        report.setDetails(details);

        return ReportResponse.from(reportRepository.save(report));
    }
}
