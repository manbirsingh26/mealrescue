package com.mealrescue.repository;

import java.util.Optional;
import com.mealrescue.model.Listing;
import com.mealrescue.model.ListingStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing>{
    List<Listing> findByBusinessIdOrderByCreatedAtDesc(Long businessId);
    Optional<Listing> findByIdAndBusinessId(Long id, Long businessId);

        List<Listing> findByStatusAndPickupEndLessThanEqual(ListingStatus status, LocalDateTime time);

    
}