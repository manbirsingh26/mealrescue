package com.mealrescue.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mealrescue.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}