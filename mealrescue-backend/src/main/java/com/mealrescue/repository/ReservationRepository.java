package com.mealrescue.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

        List<Reservation> findByCustomerIdAndStatus(Long customerId, ReservationStatus status);

           @Query("select r.status, count(r), coalesce(sum(r.quantity), 0), coalesce(sum(r.totalPrice), 0) "
         + "from Reservation r group by r.status")
    List<Object[]> totalsByStatus();

    @Query("select b.id, b.name, sum(r.quantity) from Reservation r join r.listing l join l.business b "
         + "where r.status = :status group by b.id, b.name order by sum(r.quantity) desc")
    List<Object[]> topBusinesses(@Param("status") ReservationStatus status, Pageable pageable);

    @Query("select r.pickedUpAt, r.quantity from Reservation r "
         + "where r.status = :status and r.pickedUpAt >= :from")
    List<Object[]> pickupsSince(@Param("status") ReservationStatus status, @Param("from") LocalDateTime from); 
}