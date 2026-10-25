package com.mealrescue.repository;

import com.mealrescue.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByBusinessIdOrderByCreatedAtDesc(Long businessId);
}