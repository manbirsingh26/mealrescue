package com.mealrescue.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mealrescue.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
@EntityGraph(attributePaths = {"listing", "listing.business"})
    List<Reservation> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<Reservation> findByIdAndCustomerId(Long id, Long customerId);
}