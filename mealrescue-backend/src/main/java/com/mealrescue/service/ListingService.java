package com.mealrescue.service;

import com.mealrescue.dto.CreateListingRequest;
import com.mealrescue.dto.ListingResponse;
import com.mealrescue.exception.ApiException;
import com.mealrescue.model.Listing;
import com.mealrescue.model.ListingStatus;
import com.mealrescue.model.User;
import com.mealrescue.repository.ListingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ListingService {

    private final ListingRepository listingRepository;

    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Transactional
    public ListingResponse create(User business, CreateListingRequest request) {
        if (!request.pickupEnd().isAfter(request.pickupStart())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Pickup end time must be after the start time.");
        }
        if (!request.pickupEnd().isAfter(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Pickup end time must be in the future.");
        }

        Listing listing = new Listing();
        listing.setBusiness(business);
        listing.setTitle(request.title().trim());
        listing.setDescription(request.description().trim());
        listing.setCategory(request.category());
        listing.setQuantityTotal(request.quantity());
        listing.setQuantityRemaining(request.quantity());
        listing.setPrice(request.price());
        listing.setPickupStart(request.pickupStart());
        listing.setPickupEnd(request.pickupEnd());
        listing.setStatus(ListingStatus.ACTIVE);

        return ListingResponse.from(listingRepository.save(listing));
    }

    @Transactional(readOnly = true)
    public List<ListingResponse> getMyListings(User business) {
        return listingRepository.findByBusinessIdOrderByCreatedAtDesc(business.getId()).stream()
                .map(ListingResponse::from)
                .toList();
    }
}
