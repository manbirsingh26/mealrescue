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

import java.util.List;

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
}
