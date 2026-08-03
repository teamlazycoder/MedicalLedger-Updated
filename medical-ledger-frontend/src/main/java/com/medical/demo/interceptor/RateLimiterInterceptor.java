package com.medical.demo.interceptor;

import com.medical.demo.service.cache.CacheService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimiterInterceptor implements HandlerInterceptor {

    private final CacheService cacheService;
    private static final int MAX_REQUESTS = 100;
    private static final int TIME_WINDOW = 60; // seconds

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        String clientIp = getClientIp(request);
        String key = "rate_limit:" + clientIp;

        Long requestCount = cacheService.getCounter(key);

        if (requestCount >= MAX_REQUESTS) {
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            response.setStatus(429);
            response.getWriter().write("{\"error\":\"Too many requests. Please try again later.\"}");
            response.setContentType("application/json");
            return false;
        }

        cacheService.incrementCounter(key);

        // Set expiry for the counter
        if (requestCount == 0) {
            cacheService.set(key, 1, TIME_WINDOW, TimeUnit.SECONDS);
        }

        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
