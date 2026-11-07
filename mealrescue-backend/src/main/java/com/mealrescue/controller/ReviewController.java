package com.mealrescue.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mealrescue.dto.BusinessReviewsResponse;
import com.mealrescue.dto.CreateReviewRequest;
import com.mealrescue.dto.ReviewResponse;
import com.mealrescue.model.User;
import com.mealrescue.service.ReviewService;

import jakarta.validation.Valid;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/api/reservations/{id}/review")
    public ResponseEntity<ReviewResponse> create(@AuthenticationPrincipal User customer,
                                                 @PathVariable Long id,
                                                 @Valid @RequestBody CreateReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.create(customer.getId(), id, request));
    }

    @GetMapping("/api/businesses/{id}/reviews")
    public BusinessReviewsResponse forBusiness(@PathVariable Long id) {
        return reviewService.forBusiness(id);
    }
}
