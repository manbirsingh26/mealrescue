package com.mealrescue.controller;

import com.mealrescue.dto.CreateListingRequest;
import com.mealrescue.dto.ListingResponse;
import com.mealrescue.model.User;
import com.mealrescue.service.ListingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.List;

import com.mealrescue.dto.ListingDetailsResponse;
import com.mealrescue.model.FoodCategory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @PostMapping("/api/listings")
    public ResponseEntity<ListingResponse> create(@AuthenticationPrincipal User business,
                                                  @Valid @RequestBody CreateListingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(listingService.create(business, request));
    }

    @GetMapping("/api/business/listings")
    public List<ListingResponse> myListings(@AuthenticationPrincipal User business) {
        return listingService.getMyListings(business);
    }

        @GetMapping("/api/business/listings/{id}")
    public ListingResponse myListing(@AuthenticationPrincipal User business, @PathVariable Long id) {
        return listingService.getMyListing(business, id);
    }

    @PutMapping("/api/listings/{id}")
    public ListingResponse update(@AuthenticationPrincipal User business, @PathVariable Long id,
                                  @Valid @RequestBody CreateListingRequest request) {
        return listingService.update(business, id, request);
    }

    @PostMapping("/api/listings/{id}/cancel")
    public ListingResponse cancel(@AuthenticationPrincipal User business, @PathVariable Long id) {
        return listingService.cancel(business, id);
    }

    @GetMapping("/api/listings")
    public List<ListingResponse> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) FoodCategory category,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime pickupBefore,
            @RequestParam(defaultValue = "ending") String sort) {
        return listingService.search(q, category, maxPrice, pickupBefore, sort);
    }

    @GetMapping("/api/listings/{id}")
    public ListingDetailsResponse details(@PathVariable Long id) {
        return listingService.getPublicListing(id);
    }
}
