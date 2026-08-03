package com.medical.demo.service.events;

import com.medical.demo.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "consent-events", groupId = "healthchain-group")
    public void consumeConsentEvent(Map<String, Object> event) {
        log.info("Received consent event: {}", event);

        String type = (String) event.get("type");
        // Process consent events
        // In a real implementation, this would trigger notifications
    }

    @KafkaListener(topics = "record-events", groupId = "healthchain-group")
    public void consumeRecordEvent(Map<String, Object> event) {
        log.info("Received record event: {}", event);

        String type = (String) event.get("type");
        // Process record events
        // In a real implementation, this would trigger notifications
    }

    @KafkaListener(topics = "audit-events", groupId = "healthchain-group")
    public void consumeAuditEvent(Map<String, Object> event) {
        log.info("Received audit event: {}", event);
        // Process audit events
    }

    @KafkaListener(topics = "notification-events", groupId = "healthchain-group")
    public void consumeNotificationEvent(Map<String, Object> event) {
        log.info("Received notification event: {}", event);
        // Process notification events
    }
}
