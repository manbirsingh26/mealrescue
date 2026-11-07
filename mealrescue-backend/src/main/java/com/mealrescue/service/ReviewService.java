package com.mealrescue.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mealrescue.dto.BusinessReviewsResponse;
import com.mealrescue.dto.CreateReviewRequest;
import com.mealrescue.dto.ReviewResponse;
import com.mealrescue.exception.ApiException;
import com.mealrescue.model.AccountStatus;
import com.mealrescue.model.Reservation;
import com.mealrescue.model.ReservationStatus;
import com.mealrescue.model.Review;
import com.mealrescue.model.Role;
import com.mealrescue.model.User;
import com.mealrescue.repository.ReservationRepository;
import com.mealrescue.repository.ReviewRepository;
import com.mealrescue.repository.UserRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         ReservationRepository reservationRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReviewResponse create(Long customerId, Long reservationId, CreateReviewRequest request) {
        Reservation reservation = reservationRepository.findByIdAndCustomerId(reservationId, customerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Reservation not found."));

        if (reservation.getStatus() != ReservationStatus.PICKED_UP) {
            throw new ApiException(HttpStatus.CONFLICT, "You can only review food you have picked up.");
        }
        if (reviewRepository.existsByReservationId(reservationId)) {
            throw new ApiException(HttpStatus.CONFLICT, "You have already reviewed this pickup.");
        }

        String comment = request.comment() == null || request.comment().isBlank()
                ? null : request.comment().trim();

        Review review = new Review();
        review.setReservation(reservation);
        review.setCustomer(reservation.getCustomer());
        review.setBusiness(reservation.getListing().getBusiness());
        review.setRating(request.rating());
        review.setComment(comment);

        return ReviewResponse.from(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public BusinessReviewsResponse forBusiness(Long businessId) {
        User business = userRepository.findById(businessId)
                .filter(u -> u.getRole() == Role.BUSINESS && u.getStatus() == AccountStatus.ACTIVE)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Business not found."));

        List<ReviewResponse> reviews = reviewRepository.findByBusinessIdOrderByCreatedAtDesc(businessId)
                .stream().map(ReviewResponse::from).toList();

        Double average = reviews.isEmpty() ? null
                : Math.round(reviews.stream().mapToInt(r -> r.rating()).average().orElse(0) * 10) / 10.0;

        return new BusinessReviewsResponse(business.getId(), business.getName(), average, reviews.size(), reviews);
    }
}
