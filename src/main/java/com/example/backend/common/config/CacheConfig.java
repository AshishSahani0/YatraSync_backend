package com.example.backend.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Define custom cache specs:
        // 1. "osrm_routes" - OSRM route calculations: expire after 24 hours to prevent memory leaks while keeping high hit-rate
        cacheManager.registerCustomCache("osrm_routes", Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .maximumSize(500)
                .build());

        // 2. "compact_destinations" - list of destinations: expire after 6 hours
        cacheManager.registerCustomCache("compact_destinations", Caffeine.newBuilder()
                .expireAfterWrite(6, TimeUnit.HOURS)
                .maximumSize(50)
                .build());

        return cacheManager;
    }
}
