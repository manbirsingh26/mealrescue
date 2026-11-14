package com.mealrescue.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mealrescue.dto.PlatformImpactResponse;
import com.mealrescue.model.ReservationStatus;
import com.mealrescue.repository.ReservationRepository;

@Service
public class ImpactService {

    // estimates, explained on the homepage; change them here if you use different figures
    public static final double KG_PER_PORTION = 0.4;
    public static final double CO2_PER_KG = 2.5;

    private final ReservationRepository reservationRepository;

    public ImpactService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional(readOnly = true)
    public PlatformImpactResponse platform() {
        Object[] row = reservationRepository.platformTotals(ReservationStatus.PICKED_UP).get(0);
        long pickups = ((Number) row[0]).longValue();
        long portions = ((Number) row[1]).longValue();
        long businesses = ((Number) row[2]).longValue();

        double foodKg = round1(portions * KG_PER_PORTION);
        double co2Kg = round1(foodKg * CO2_PER_KG);
        return new PlatformImpactResponse(pickups, portions, foodKg, co2Kg, businesses,
                KG_PER_PORTION, CO2_PER_KG);
    }

    private static double round1(double value) {
        return Math.round(value * 10) / 10.0;
    }
}
