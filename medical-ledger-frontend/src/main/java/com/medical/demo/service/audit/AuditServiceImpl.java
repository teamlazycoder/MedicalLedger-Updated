package com.medical.demo.service.audit;

import com.medical.demo.model.AuditLog;
import com.medical.demo.model.MedicalRecord;
import com.medical.demo.model.Patient;
import com.medical.demo.model.User;
import com.medical.demo.model.enums.AccessAction;
import com.medical.demo.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final HttpServletRequest request;

    @Override
    @Transactional
    public void logAccess(MedicalRecord record, Patient patient, User actor,
                          AccessAction action, String description) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setRecord(record);
            auditLog.setPatient(patient);
            auditLog.setActor(actor);
            auditLog.setAction(action);
            auditLog.setIpAddress(getClientIp());
            auditLog.setUserAgent(request.getHeader("User-Agent"));

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("description", description);
            metadata.put("timestamp", System.currentTimeMillis());

            if (record != null) {
                metadata.put("recordId", record.getId());
                metadata.put("recordType", record.getRecordType().toString());
            }

            auditLog.setMetadata(metadata);
            auditLog.setCreatedAt(LocalDateTime.now());

            auditLogRepository.save(auditLog);
            log.debug("Audit log created: {} - {}", action, description);

        } catch (Exception e) {
            log.error("Failed to create audit log: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<AuditLog> getPatientAuditLogs(Long patientId) {
        return auditLogRepository.findByPatientId(patientId);
    }

    @Override
    public List<AuditLog> getDoctorAuditLogs(Long doctorId) {
        return auditLogRepository.findByActorId(doctorId);
    }

    @Override
    public List<AuditLog> getRecordAuditLogs(Long recordId) {
        return auditLogRepository.findByRecordId(recordId);
    }

    @Override
    public List<AuditLog> getGlobalAuditLogs() {
        return auditLogRepository.findAll();
    }

    @Override
    public List<AuditLog> getAuditLogsByRecord(Long recordId) {
        return auditLogRepository.findByRecordId(recordId);
    }

    @Override
    public Map<String, Object> exportAuditReport() {
        Map<String, Object> report = new HashMap<>();
        List<AuditLog> allLogs = auditLogRepository.findAll();

        report.put("totalLogs", allLogs.size());
        report.put("generatedAt", LocalDateTime.now().toString());
        report.put("logs", allLogs);

        return report;
    }

    private String getClientIp() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}