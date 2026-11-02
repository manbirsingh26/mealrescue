package com.mealrescue.service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mealrescue.dto.CreateReservationRequest;
import com.mealrescue.dto.ReservationResponse;
import com.mealrescue.exception.ApiException;
import com.mealrescue.model.AccountStatus;
import com.mealrescue.model.Listing;
import com.mealrescue.model.ListingStatus;
import com.mealrescue.model.Reservation;
import com.mealrescue.model.User;
import com.mealrescue.repository.ListingRepository;
import com.mealrescue.repository.ReservationRepository;
import com.mealrescue.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ReservationService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final ReservationRepository reservationRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              ListingRepository listingRepository,
                              UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReservationResponse reserve(Long customerId, CreateReservationRequest request) {
        User customer = userRepository.findById(customerId)
                .filter(u -> u.getStatus() == AccountStatus.ACTIVE)
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "Your account cannot reserve food."));

        Listing listing = listingRepository.findById(request.listingId())
                .filter(l -> l.getBusiness().getStatus() == AccountStatus.ACTIVE)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Listing not found."));

        boolean open = listing.getStatus() == ListingStatus.ACTIVE
                && listing.getQuantityRemaining() > 0
                && listing.getPickupEnd().isAfter(LocalDateTime.now());
        if (!open) {
            throw new ApiException(HttpStatus.CONFLICT, "This listing is no longer available.");
        }

        int left = listing.getQuantityRemaining();
        if (request.quantity() > left) {
            throw new ApiException(HttpStatus.CONFLICT, "Only " + left + " left.");
        }

        // no save() needed: the listing is managed, so the change is written on commit,
        // and @Version makes that write fail if someone else changed the listing first
        listing.setQuantityRemaining(left - request.quantity());
        if (listing.getQuantityRemaining() == 0) {
            listing.setStatus(ListingStatus.SOLD_OUT);
        }

        Reservation reservation = new Reservation();
        reservation.setListing(listing);
        reservation.setCustomer(customer);
        reservation.setQuantity(request.quantity());
        reservation.setTotalPrice(listing.getPrice().multiply(BigDecimal.valueOf(request.quantity())));
        reservation.setPickupCode(String.format("%06d", RANDOM.nextInt(1_000_000)));

        return ReservationResponse.from(reservationRepository.save(reservation));
    }
}