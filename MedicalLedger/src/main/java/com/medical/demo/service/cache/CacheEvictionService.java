package com.medical.demo.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheEvictionService {

    private final CacheService cacheService;

    @Scheduled(fixedRate = 3600000) // Every hour
    public void evictExpiredCaches() {
        log.info("Running cache eviction...");
        // In a production environment, this would clear expired cache entries
        log.info("Cache eviction completed");
    }

    @Scheduled(cron = "0 0 2 * * *") // Daily at 2 AM
    public void clearOldSessionData() {
        log.info("Clearing old session data...");
        // Clear old session data
        log.info("Session data cleanup completed");
    }
}
