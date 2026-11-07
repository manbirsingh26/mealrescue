package com.mealrescue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mealrescue.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByReservationId(Long reservationId);

    @EntityGraph(attributePaths = {"customer", "reservation", "reservation.listing"})
    List<Review> findByBusinessIdOrderByCreatedAtDesc(Long businessId);

    @Query("select r.reservation.id from Review r where r.customer.id = :customerId")
    List<Long> findReservationIdsByCustomerId(@Param("customerId") Long customerId);
}
