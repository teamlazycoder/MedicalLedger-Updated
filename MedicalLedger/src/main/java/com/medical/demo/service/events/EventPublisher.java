package com.medical.demo.service.events;

import com.medical.demo.model.ConsentPolicy;
import com.medical.demo.model.MedicalRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishConsentGrantedEvent(ConsentPolicy consent) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "CONSENT_GRANTED");
        event.put("consentId", consent.getId());
        event.put("patientId", consent.getPatient().getId());
        event.put("doctorId", consent.getDoctor().getId());
        event.put("timestamp", LocalDateTime.now().toString());

        kafkaTemplate.send("consent-events", event);
        log.info("Published consent granted event: {}", consent.getId());
    }

    public void publishConsentRevokedEvent(ConsentPolicy consent) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "CONSENT_REVOKED");
        event.put("consentId", consent.getId());
        event.put("patientId", consent.getPatient().getId());
        event.put("doctorId", consent.getDoctor().getId());
        event.put("timestamp", LocalDateTime.now().toString());

        kafkaTemplate.send("consent-events", event);
        log.info("Published consent revoked event: {}", consent.getId());
    }

    public void publishRecordUploadedEvent(MedicalRecord record) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "RECORD_UPLOADED");
        event.put("recordId", record.getId());
        event.put("patientId", record.getPatient().getId());
        event.put("recordType", record.getRecordType().toString());
        event.put("timestamp", LocalDateTime.now().toString());

        kafkaTemplate.send("record-events", event);
        log.info("Published record uploaded event: {}", record.getId());
    }

    public void publishRecordViewedEvent(MedicalRecord record) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "RECORD_VIEWED");
        event.put("recordId", record.getId());
        event.put("patientId", record.getPatient().getId());
        event.put("timestamp", LocalDateTime.now().toString());

        kafkaTemplate.send("record-events", event);
        log.info("Published record viewed event: {}", record.getId());
    }
}
