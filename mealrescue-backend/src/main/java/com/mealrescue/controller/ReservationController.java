package com.mealrescue.controller;

import com.mealrescue.dto.CreateReservationRequest;
import com.mealrescue.dto.ReservationResponse;
import com.mealrescue.model.User;
import com.mealrescue.service.ReservationService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/api/reservations")
    public ResponseEntity<ReservationResponse> reserve(@AuthenticationPrincipal User customer,
                                                       @Valid @RequestBody CreateReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.reserve(customer.getId(), request));
    }

    @GetMapping("/api/reservations")
    public List<ReservationResponse> myReservations(@AuthenticationPrincipal User customer) {
        return reservationService.myReservations(customer.getId());
    }

    @PostMapping("/api/reservations/{id}/cancel")
    public ReservationResponse cancel(@AuthenticationPrincipal User customer, @PathVariable Long id) {
        return reservationService.cancel(customer.getId(), id);
    }
}