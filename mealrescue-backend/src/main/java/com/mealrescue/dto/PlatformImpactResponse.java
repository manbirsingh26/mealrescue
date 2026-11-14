package com.mealrescue.dto;

public record PlatformImpactResponse(
        long pickups,
        long portions,
        double foodKg,
        double co2Kg,
        long businesses,
        double kgPerPortion,
        double co2PerKg) {
}
