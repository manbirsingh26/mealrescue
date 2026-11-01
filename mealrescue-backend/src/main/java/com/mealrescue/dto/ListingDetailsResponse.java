package com.mealrescue.dto;

import com.mealrescue.model.AccountProfile;
import com.mealrescue.model.Listing;
import com.mealrescue.model.ListingStatus;

import java.time.LocalDateTime;

public record ListingDetailsResponse(ListingResponse listing, String businessAddress, boolean available) {

    public static ListingDetailsResponse from(Listing l) {
        AccountProfile profile = l.getBusiness().getProfile();
        boolean available = l.getStatus() == ListingStatus.ACTIVE
                && l.getQuantityRemaining() > 0
                && l.getPickupEnd().isAfter(LocalDateTime.now());
        return new ListingDetailsResponse(
                ListingResponse.from(l),
                profile != null ? profile.getAddress() : null,
                available);
    }
}