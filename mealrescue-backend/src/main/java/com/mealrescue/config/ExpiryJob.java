package com.mealrescue.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mealrescue.service.ExpiryService;

@Component
public class ExpiryJob {

    private static final Logger log = LoggerFactory.getLogger(ExpiryJob.class);

    private final ExpiryService expiryService;

    public ExpiryJob(ExpiryService expiryService) {
        this.expiryService = expiryService;
    }

    // first run 10 s after startup, then 60 s after each run finishes
    @Scheduled(initialDelay = 10_000, fixedDelay = 60_000)
    public void run() {
        try {
            int[] counts = expiryService.expireEnded();
            if (counts[0] > 0 || counts[1] > 0) {
                log.info("Expiry job: {} listing(s) and {} reservation(s) expired.", counts[0], counts[1]);
            }
        } catch (ObjectOptimisticLockingFailureException e) {
            // someone changed a listing at the same moment; the next run picks it up
            log.warn("Expiry job skipped this run because of a concurrent update.");
        }
    }
}
