
package com.medical.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "rate-limiting")
public class RateLimiterConfig {

    private boolean enabled = true;
    private int defaultLimit = 100;
    private int defaultDuration = 60; // seconds
    private Map<String, Integer> endpoints = new HashMap<>();

    {
        endpoints.put("upload", 10);
        endpoints.put("login", 5);
        endpoints.put("consent", 20);
    }
}