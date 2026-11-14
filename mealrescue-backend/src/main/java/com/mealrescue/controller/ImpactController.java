package com.mealrescue.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mealrescue.dto.PlatformImpactResponse;
import com.mealrescue.service.ImpactService;

@RestController
public class ImpactController {

    private final ImpactService impactService;

    public ImpactController(ImpactService impactService) {
        this.impactService = impactService;
    }

    @GetMapping("/api/impact")
    public PlatformImpactResponse platform() {
        return impactService.platform();
    }
}