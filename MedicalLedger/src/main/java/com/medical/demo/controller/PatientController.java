package com.medical.demo.controller;

import com.medical.demo.dto.request.ConsentGrantRequest;
import com.medical.demo.dto.response.ApiResponse;
import com.medical.demo.dto.response.ConsentResponse;
import com.medical.demo.dto.response.PatientResponse;
import com.medical.demo.dto.response.RecordResponse;
import com.medical.demo.model.ConsentPolicy;
import com.medical.demo.model.Patient;
import com.medical.demo.model.User;
import com.medical.demo.repository.UserRepository;
import com.medical.demo.service.consent.ConsentService;
import com.medical.demo.service.patient.PatientService;
import com.medical.demo.service.record.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Tag(name = "Patient", description = "Patient management APIs")
public class PatientController {

    private final PatientService patientService;
    private final ConsentService consentService;
    private final RecordService recordService;
    private final UserRepository userRepository;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Get patient details")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatient(@PathVariable Long id) {
        PatientResponse response = patientService.getPatientDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Patient retrieved", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Update patient profile")
    public ResponseEntity<ApiResponse<PatientResponse>> updatePatient(
            @PathVariable Long id,
            @RequestBody Patient patientDetails) {
        Patient updatedPatient = patientService.updatePatient(id, patientDetails);
        PatientResponse response = patientService.getPatientDetails(updatedPatient.getId());
        return ResponseEntity.ok(ApiResponse.success("Patient updated successfully", response));
    }

    @GetMapping("/{id}/records")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @Operation(summary = "Get patient medical records")
    public ResponseEntity<ApiResponse<List<RecordResponse>>> getPatientRecords(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<RecordResponse> records = recordService.getPatientRecords(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Records retrieved", records));
    }

    @GetMapping("/{id}/consents")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get patient consent policies")
    public ResponseEntity<ApiResponse<List<ConsentResponse>>> getPatientConsents(@PathVariable Long id) {
        List<ConsentResponse> consents = consentService.getPatientConsents(id);
        return ResponseEntity.ok(ApiResponse.success("Consents retrieved", consents));
    }

    @PostMapping("/{id}/consents/grant")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Grant consent to doctor")
    public ResponseEntity<ApiResponse<ConsentResponse>> grantConsent(
            @PathVariable Long id,
            @Valid @RequestBody ConsentGrantRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ConsentPolicy consentPolicy = consentService.grantConsent(id, request, currentUser);
        ConsentResponse response = consentService.getConsentDetails(consentPolicy.getId());
        return ResponseEntity.ok(ApiResponse.success("Consent granted successfully", response));
    }

    @PutMapping("/{patientId}/consents/{consentId}/revoke")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Revoke consent")
    public ResponseEntity<ApiResponse<ConsentResponse>> revokeConsent(
            @PathVariable Long patientId,
            @PathVariable Long consentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ConsentPolicy consentPolicy = consentService.revokeConsent(consentId, patientId, currentUser);
        ConsentResponse response = consentService.getConsentDetails(consentPolicy.getId());
        return ResponseEntity.ok(ApiResponse.success("Consent revoked successfully", response));
    }
}