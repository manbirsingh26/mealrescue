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
        validateTimes(request);

        Listing listing = new Listing();
        listing.setBusiness(business);
        applyFields(listing, request);
        listing.setQuantityTotal(request.quantity());
        listing.setQuantityRemaining(request.quantity());
        listing.setStatus(ListingStatus.ACTIVE);

        return ListingResponse.from(listingRepository.save(listing));
    }

    @Transactional(readOnly = true)
    public List<ListingResponse> getMyListings(User business) {
        return listingRepository.findByBusinessIdOrderByCreatedAtDesc(business.getId()).stream()
                .map(ListingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ListingResponse getMyListing(User business, Long id) {
        return ListingResponse.from(findOwned(business, id));
    }

    @Transactional
    public ListingResponse update(User business, Long id, CreateListingRequest request) {
        Listing listing = findOwned(business, id);
        if (!isEditable(listing)) {
            throw new ApiException(HttpStatus.CONFLICT, "Only active listings can be edited.");
        }
        validateTimes(request);

        // Portions already reserved by customers
        int reserved = listing.getQuantityTotal() - listing.getQuantityRemaining();
        if (request.quantity() < reserved) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Quantity cannot be lower than the " + reserved + " already reserved.");
        }

        applyFields(listing, request);
        listing.setQuantityTotal(request.quantity());
        listing.setQuantityRemaining(request.quantity() - reserved);
        listing.setStatus(listing.getQuantityRemaining() > 0 ? ListingStatus.ACTIVE : ListingStatus.SOLD_OUT);

        // No save() needed: the listing is already managed by Hibernate inside this transaction
        return ListingResponse.from(listing);
    }

    @Transactional
    public ListingResponse cancel(User business, Long id) {
        Listing listing = findOwned(business, id);
        if (!isEditable(listing)) {
            throw new ApiException(HttpStatus.CONFLICT, "This listing can no longer be cancelled.");
        }
        listing.setStatus(ListingStatus.CANCELLED);
        // Later, when reservations exist, this is also where we cancel the listing's active reservations
        return ListingResponse.from(listing);
    }

    private Listing findOwned(User business, Long id) {
        return listingRepository.findByIdAndBusinessId(id, business.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Listing not found."));
    }

    private boolean isEditable(Listing listing) {
        return listing.getStatus() == ListingStatus.ACTIVE || listing.getStatus() == ListingStatus.SOLD_OUT;
    }

    private void validateTimes(CreateListingRequest request) {
        if (!request.pickupEnd().isAfter(request.pickupStart())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Pickup end time must be after the start time.");
        }
        if (!request.pickupEnd().isAfter(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Pickup end time must be in the future.");
        }
    }

    // Fields that are the same for create and update
    private void applyFields(Listing listing, CreateListingRequest request) {
        listing.setTitle(request.title().trim());
        listing.setDescription(request.description().trim());
        listing.setCategory(request.category());
        listing.setPrice(request.price());
        listing.setPickupStart(request.pickupStart());
        listing.setPickupEnd(request.pickupEnd());
    }
}
