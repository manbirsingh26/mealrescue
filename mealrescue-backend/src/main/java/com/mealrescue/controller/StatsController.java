package com.mealrescue.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mealrescue.dto.AdminStatsResponse;
import com.mealrescue.service.StatsService;

@RestController
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/api/admin/stats")
    public AdminStatsResponse overview() {
        return statsService.overview();
    }
}
