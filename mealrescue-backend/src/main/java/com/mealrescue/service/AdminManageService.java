package com.mealrescue.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mealrescue.dto.AdminUserResponse;
import com.mealrescue.dto.ListingResponse;
import com.mealrescue.exception.ApiException;
import com.mealrescue.model.AccountStatus;
import com.mealrescue.model.Listing;
import com.mealrescue.model.ListingStatus;
import com.mealrescue.model.Reservation;
import com.mealrescue.model.ReservationStatus;
import com.mealrescue.model.Role;
import com.mealrescue.model.User;
import com.mealrescue.repository.ListingRepository;
import com.mealrescue.repository.ReservationRepository;
import com.mealrescue.repository.UserRepository;

@Service
public class AdminManageService {

    private static final List<ListingStatus> OPEN_LISTING = List.of(ListingStatus.ACTIVE, ListingStatus.SOLD_OUT);

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final ReservationRepository reservationRepository;

    public AdminManageService(UserRepository userRepository,
                              ListingRepository listingRepository,
                              ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
        this.reservationRepository = reservationRepository;
    }

    // ---------- users ----------

    @Transactional(readOnly = true)
    public List<AdminUserResponse> users(Role role, AccountStatus status, String q) {
        String text = q == null ? "" : q.trim().toLowerCase();
        return userRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(u -> role == null || u.getRole() == role)
                .filter(u -> status == null || u.getStatus() == status)
                .filter(u -> text.isEmpty()
                        || u.getName().toLowerCase().contains(text)
                        || u.getEmail().toLowerCase().contains(text))
                .map(AdminUserResponse::from)
                .toList();
    }

    @Transactional
    public AdminUserResponse deactivate(Long userId) {
        User user = findUser(userId);
        if (user.getRole() == Role.ADMIN) {
            throw new ApiException(HttpStatus.CONFLICT, "Admin accounts cannot be deactivated.");
        }
        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new ApiException(HttpStatus.CONFLICT, "Only active accounts can be deactivated.");
        }

        user.setStatus(AccountStatus.DEACTIVATED);

        if (user.getRole() == Role.BUSINESS) {
            listingRepository.findByBusinessIdAndStatusIn(userId, OPEN_LISTING)
                    .forEach(l -> closeListing(l, ListingStatus.CANCELLED));
        } else {
            reservationRepository.findByCustomerIdAndStatus(userId, ReservationStatus.RESERVED)
                    .forEach(this::releaseReservation);
        }
        return AdminUserResponse.from(user);
    }

    @Transactional
    public AdminUserResponse reactivate(Long userId) {
        User user = findUser(userId);
        if (user.getStatus() != AccountStatus.DEACTIVATED) {
            throw new ApiException(HttpStatus.CONFLICT, "Only deactivated accounts can be reactivated.");
        }
        user.setStatus(AccountStatus.ACTIVE);
        return AdminUserResponse.from(user);
    }

    // ---------- listings ----------

    @Transactional(readOnly = true)
    public List<ListingResponse> listings(ListingStatus status, String q) {
        String text = q == null ? "" : q.trim().toLowerCase();
        return listingRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(l -> status == null || l.getStatus() == status)
                .filter(l -> text.isEmpty()
                        || l.getTitle().toLowerCase().contains(text)
                        || l.getBusiness().getName().toLowerCase().contains(text))
                .map(ListingResponse::from)
                .toList();
    }

    @Transactional
    public ListingResponse removeListing(Long listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Listing not found."));
        if (!OPEN_LISTING.contains(listing.getStatus())) {
            throw new ApiException(HttpStatus.CONFLICT, "Only active or sold-out listings can be removed.");
        }
        closeListing(listing, ListingStatus.REMOVED);
        return ListingResponse.from(listing);
    }

    // ---------- helpers ----------

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found."));
    }

    // close a listing and cancel everyone's open reservations on it
    private void closeListing(Listing listing, ListingStatus newStatus) {
        listing.setStatus(newStatus);
        reservationRepository.findByListingIdAndStatus(listing.getId(), ReservationStatus.RESERVED)
                .forEach(r -> r.setStatus(ReservationStatus.CANCELLED));
    }

    // cancel one reservation and give its portions back, like a customer cancelling
    private void releaseReservation(Reservation r) {
        r.setStatus(ReservationStatus.CANCELLED);
        Listing listing = r.getListing();
        listing.setQuantityRemaining(listing.getQuantityRemaining() + r.getQuantity());
        if (listing.getStatus() == ListingStatus.SOLD_OUT && listing.getPickupEnd().isAfter(LocalDateTime.now())) {
            listing.setStatus(ListingStatus.ACTIVE);
        }
    }
}
