
package com.medical.demo.controller;

import com.medical.demo.dto.response.ApiResponse;
import com.medical.demo.dto.response.AuditLogResponse;
import com.medical.demo.model.AuditLog;
import com.medical.demo.service.audit.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Audit", description = "Audit log management APIs")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/patients/{patientId}")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get patient audit logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getPatientAuditLogs(@PathVariable Long patientId) {
        List<AuditLog> logs = auditService.getPatientAuditLogs(patientId);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved", logs));
    }

    @GetMapping("/doctors/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Get doctor audit logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getDoctorAuditLogs(@PathVariable Long doctorId) {
        List<AuditLog> logs = auditService.getDoctorAuditLogs(doctorId);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved", logs));
    }

    @GetMapping("/records/{recordId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    @Operation(summary = "Get record audit logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getRecordAuditLogs(@PathVariable Long recordId) {
        List<AuditLog> logs = auditService.getRecordAuditLogs(recordId);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved", logs));
    }

    @GetMapping("/global")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get global audit logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getGlobalAuditLogs() {
        List<AuditLog> logs = auditService.getGlobalAuditLogs();
        return ResponseEntity.ok(ApiResponse.success("Global audit logs retrieved", logs));
    }

    @PostMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Export audit report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> exportAuditReport() {
        Map<String, Object> report = auditService.exportAuditReport();
        return ResponseEntity.ok(ApiResponse.success("Audit report exported", report));
    }
}