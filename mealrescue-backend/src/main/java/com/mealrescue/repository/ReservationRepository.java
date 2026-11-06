package com.mealrescue.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mealrescue.model.Reservation;
import com.mealrescue.model.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
@EntityGraph(attributePaths = {"listing", "listing.business"})
    List<Reservation> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<Reservation> findByIdAndCustomerId(Long id, Long customerId);

@EntityGraph(attributePaths = {"listing", "customer"})
    List<Reservation> findByListingBusinessIdOrderByCreatedAtDesc(Long businessId);

    Optional<Reservation> findFirstByPickupCodeAndStatusAndListingBusinessId(
            String pickupCode, ReservationStatus status, Long businessId);

    boolean existsByPickupCodeAndStatus(String pickupCode, ReservationStatus status);

    List<Reservation> findByListingIdAndStatus(Long listingId, ReservationStatus status);

        List<Reservation> findByStatusAndListingPickupEndLessThanEqual(ReservationStatus status, LocalDateTime time);
}