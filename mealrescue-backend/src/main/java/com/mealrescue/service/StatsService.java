package com.mealrescue.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mealrescue.dto.AdminStatsResponse;
import com.mealrescue.model.AccountStatus;
import com.mealrescue.model.ListingStatus;
import com.mealrescue.model.ReportStatus;
import com.mealrescue.model.ReservationStatus;
import com.mealrescue.model.Role;
import com.mealrescue.repository.ListingRepository;
import com.mealrescue.repository.ReportRepository;
import com.mealrescue.repository.ReservationRepository;
import com.mealrescue.repository.ReviewRepository;
import com.mealrescue.repository.UserRepository;

@Service
public class StatsService {

    private static final int DAYS = 14;

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final ReservationRepository reservationRepository;
    private final ReportRepository reportRepository;
    private final ReviewRepository reviewRepository;

    public StatsService(UserRepository userRepository,
                        ListingRepository listingRepository,
                        ReservationRepository reservationRepository,
                        ReportRepository reportRepository,
                        ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
        this.reservationRepository = reservationRepository;
        this.reportRepository = reportRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse overview() {
        // ----- users -----
        Map<String, Long> activeByRole = zeroMap(Role.CUSTOMER, Role.ORGANIZATION, Role.BUSINESS);
        long pending = 0;
        long deactivated = 0;
        for (Object[] row : userRepository.countByRoleAndStatus()) {
            Role role = (Role) row[0];
            AccountStatus status = (AccountStatus) row[1];
            long count = toLong(row[2]);
            if (status == AccountStatus.ACTIVE && role != Role.ADMIN) {
                activeByRole.merge(role.name(), count, Long::sum);
            } else if (status == AccountStatus.PENDING) {
                pending += count;
            } else if (status == AccountStatus.DEACTIVATED) {
                deactivated += count;
            }
        }

        // ----- listings -----
        Map<String, Long> listings = zeroMap(ListingStatus.values());
        for (Object[] row : listingRepository.countByStatus()) {
            listings.put(((ListingStatus) row[0]).name(), toLong(row[1]));
        }

        // ----- reservations -----
        Map<String, Long> reservations = zeroMap(ReservationStatus.values());
        long portionsRescued = 0;
        BigDecimal valueRecovered = BigDecimal.ZERO;
        for (Object[] row : reservationRepository.totalsByStatus()) {
            ReservationStatus status = (ReservationStatus) row[0];
            reservations.put(status.name(), toLong(row[1]));
            if (status == ReservationStatus.PICKED_UP) {
                portionsRescued = toLong(row[2]);
                valueRecovered = (BigDecimal) row[3];
            }
        }

        // of the reservations that are finished either way, how many were collected?
        long pickedUp = reservations.get(ReservationStatus.PICKED_UP.name());
        long missed = reservations.get(ReservationStatus.EXPIRED.name());
        Double pickupRate = pickedUp + missed == 0 ? null
                : Math.round(pickedUp * 1000.0 / (pickedUp + missed)) / 10.0;

        // ----- top businesses -----
        List<AdminStatsResponse.TopBusiness> top = reservationRepository
                .topBusinesses(ReservationStatus.PICKED_UP, PageRequest.of(0, 5))
                .stream()
                .map(row -> new AdminStatsResponse.TopBusiness(
                        (Long) row[0], (String) row[1], toLong(row[2])))
                .toList();

        // ----- last 14 days, one entry per day, including days with 0 -----
        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.minusDays(DAYS - 1);
        Map<LocalDate, Long> perDay = new LinkedHashMap<>();
        for (int i = 0; i < DAYS; i++) {
            perDay.put(firstDay.plusDays(i), 0L);
        }
        for (Object[] row : reservationRepository.pickupsSince(ReservationStatus.PICKED_UP, firstDay.atStartOfDay())) {
            LocalDate day = ((LocalDateTime) row[0]).toLocalDate();
            perDay.merge(day, toLong(row[1]), Long::sum);
        }
        List<AdminStatsResponse.DailyCount> lastTwoWeeks = perDay.entrySet().stream()
                .map(e -> new AdminStatsResponse.DailyCount(e.getKey(), e.getValue()))
                .toList();

        // ----- reports and reviews -----
        Double avg = reviewRepository.averageRating();
        Double averageRating = avg == null ? null : Math.round(avg * 10) / 10.0;

        return new AdminStatsResponse(
                activeByRole, pending, deactivated,
                listings, reservations,
                portionsRescued, valueRecovered, pickupRate,
                reportRepository.countByStatus(ReportStatus.OPEN),
                reviewRepository.count(), averageRating,
                top, lastTwoWeeks);
    }

    // every key present with 0, in enum order, so the front end never sees a missing status
    private static Map<String, Long> zeroMap(Enum<?>... keys) {
        Map<String, Long> map = new LinkedHashMap<>();
        for (Enum<?> key : keys) {
            map.put(key.name(), 0L);
        }
        return map;
    }

    private static long toLong(Object value) {
        return value == null ? 0 : ((Number) value).longValue();
    }
}