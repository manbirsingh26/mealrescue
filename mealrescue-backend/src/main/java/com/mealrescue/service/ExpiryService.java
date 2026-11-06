package com.mealrescue.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mealrescue.model.Listing;
import com.mealrescue.model.ListingStatus;
import com.mealrescue.model.Reservation;
import com.mealrescue.model.ReservationStatus;
import com.mealrescue.repository.ListingRepository;
import com.mealrescue.repository.ReservationRepository;

@Service
public class ExpiryService {

    private final ListingRepository listingRepository;
    private final ReservationRepository reservationRepository;

    public ExpiryService(ListingRepository listingRepository,
                         ReservationRepository reservationRepository) {
        this.listingRepository = listingRepository;
        this.reservationRepository = reservationRepository;
    }

    /** Returns {listings expired, reservations expired}. */
    @Transactional
    public int[] expireEnded() {
        LocalDateTime now = LocalDateTime.now();

        List<Listing> listings = listingRepository
                .findByStatusAndPickupEndLessThanEqual(ListingStatus.ACTIVE, now);
        listings.forEach(l -> l.setStatus(ListingStatus.EXPIRED));

        List<Reservation> reservations = reservationRepository
                .findByStatusAndListingPickupEndLessThanEqual(ReservationStatus.RESERVED, now);
        reservations.forEach(r -> r.setStatus(ReservationStatus.EXPIRED));

        return new int[] { listings.size(), reservations.size() };
    }
}
