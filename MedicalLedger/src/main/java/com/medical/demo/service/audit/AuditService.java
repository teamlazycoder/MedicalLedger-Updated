package com.medical.demo.service.audit;

import com.medical.demo.dto.response.AuditLogResponse;
import com.medical.demo.model.*;
import com.medical.demo.model.enums.AccessAction;

import java.util.List;
import java.util.Map;

public interface AuditService {
    void logAccess(MedicalRecord record, Patient patient, User actor,
                   AccessAction action, String description);
    List<AuditLog> getPatientAuditLogs(Long patientId);
    List<AuditLog> getDoctorAuditLogs(Long doctorId);
    List<AuditLog> getRecordAuditLogs(Long recordId);
    List<AuditLog> getGlobalAuditLogs();
    List<AuditLog> getAuditLogsByRecord(Long recordId);
    Map<String, Object> exportAuditReport();
}
