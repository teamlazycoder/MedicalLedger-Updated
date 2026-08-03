package com.medical.demo.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AuditInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        log.debug("Request: {} {} from IP: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        if (ex != null) {
            log.error("Request failed: {} {} - Error: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    ex.getMessage());
        } else {
            log.debug("Request completed: {} {} - Status: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus());
        }
    }
}