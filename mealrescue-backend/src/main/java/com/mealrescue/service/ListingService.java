package com.mealrescue.service;

import com.mealrescue.dto.CreateListingRequest;
import com.mealrescue.dto.ListingResponse;
import com.mealrescue.exception.ApiException;
import com.mealrescue.model.Listing;
import com.mealrescue.model.ListingStatus;
import com.mealrescue.model.ReservationStatus;
import com.mealrescue.model.User;
import com.mealrescue.repository.ListingRepository;
import com.mealrescue.repository.ReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import com.mealrescue.dto.ListingDetailsResponse;
import com.mealrescue.model.AccountStatus;
import com.mealrescue.model.FoodCategory;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class ListingService {

    private final ListingRepository listingRepository;
   private final ReservationRepository reservationRepository;
    
   public ListingService(ListingRepository listingRepository, ReservationRepository reservationRepository) {
        this.listingRepository = listingRepository;
        this.reservationRepository = reservationRepository;
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
        reservationRepository.findByListingIdAndStatus(listing.getId(), ReservationStatus.RESERVED)
                .forEach(r -> r.setStatus(ReservationStatus.CANCELLED));
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

        // US-10, US-11: public search with optional filters
    @Transactional(readOnly = true)
    public List<ListingResponse> search(String q, FoodCategory category, BigDecimal maxPrice,
                                        LocalDateTime pickupBefore, String sort) {
        LocalDateTime now = LocalDateTime.now();

        Specification<Listing> spec = (root, query, cb) -> {
            List<Predicate> conditions = new ArrayList<>();
            Join<Listing, User> business = root.join("business");

            // Always: only food that can still be reserved, from active businesses
            conditions.add(cb.equal(root.get("status"), ListingStatus.ACTIVE));
            conditions.add(cb.greaterThan(root.<Integer>get("quantityRemaining"), 0));
            conditions.add(cb.greaterThan(root.<LocalDateTime>get("pickupEnd"), now));
            conditions.add(cb.equal(business.get("status"), AccountStatus.ACTIVE));

            // Optional filters: each one is added only if the customer used it
            if (q != null && !q.isBlank()) {
                String pattern = "%" + q.trim().toLowerCase() + "%";
                conditions.add(cb.or(
                        cb.like(cb.lower(root.<String>get("title")), pattern),
                        cb.like(cb.lower(business.<String>get("name")), pattern)));
            }
            if (category != null) {
                conditions.add(cb.equal(root.get("category"), category));
            }
            if (maxPrice != null) {
                conditions.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("price"), maxPrice));
            }
            if (pickupBefore != null) {
                conditions.add(cb.lessThanOrEqualTo(root.<LocalDateTime>get("pickupStart"), pickupBefore));
            }

            return cb.and(conditions.toArray(new Predicate[0]));
        };

        return listingRepository.findAll(spec, sortFor(sort)).stream()
                .map(ListingResponse::from)
                .toList();
    }

    // US-12: one listing for the public details page
    @Transactional(readOnly = true)
    public ListingDetailsResponse getPublicListing(Long id) {
        Listing listing = listingRepository.findById(id)
                .filter(l -> l.getBusiness().getStatus() == AccountStatus.ACTIVE)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Listing not found."));
        return ListingDetailsResponse.from(listing);
    }

    private Sort sortFor(String sort) {
        if ("price".equals(sort)) {
            return Sort.by("price").ascending().and(Sort.by("pickupEnd").ascending());
        }
        if ("newest".equals(sort)) {
            return Sort.by("createdAt").descending();
        }
        return Sort.by("pickupEnd").ascending(); // default: ending soonest first
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
