package com.medical.demo.controller;

import com.medical.demo.dto.request.ConsentGrantRequest;
import com.medical.demo.dto.response.ApiResponse;
import com.medical.demo.dto.response.ConsentResponse;
import com.medical.demo.model.ConsentPolicy;
import com.medical.demo.model.User;
import com.medical.demo.service.consent.ConsentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/consents")
@RequiredArgsConstructor
@Tag(name = "Consent", description = "Consent management APIs")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ConsentController {

    private final ConsentService consentService;

    @PostMapping("/{id}/consents/grant")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Grant consent to doctor")
    public ResponseEntity<ApiResponse<ConsentResponse>> grantConsent(
            @PathVariable Long id,
            @Valid @RequestBody ConsentGrantRequest request,
            @AuthenticationPrincipal User currentUser) {

        var consent = consentService.grantConsent(id, request, currentUser);
        ConsentResponse response = consentService.getConsentDetails(consent.getId());
        return ResponseEntity.ok(ApiResponse.success("Consent granted successfully", response));
    }

    @PutMapping("/{patientId}/consents/{consentId}/revoke")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Revoke consent")
    public ResponseEntity<ApiResponse<ConsentResponse>> revokeConsent(
            @PathVariable Long patientId,
            @PathVariable Long consentId,
            @AuthenticationPrincipal User currentUser) {

        var consent = consentService.revokeConsent(consentId, patientId, currentUser);
        ConsentResponse response = consentService.getConsentDetails(consent.getId());
        return ResponseEntity.ok(ApiResponse.success("Consent revoked successfully", response));
    }
    @PutMapping("/{id}/renew")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Renew consent")
    public ResponseEntity<ApiResponse<ConsentResponse>> renewConsent(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newEndDate) {
        ConsentPolicy consent = consentService.renewConsent(id, newEndDate);
        ConsentResponse response = consentService.getConsentDetails(consent.getId());
        return ResponseEntity.ok(ApiResponse.success("Consent renewed", response));
    }

    @GetMapping("/patients/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    @Operation(summary = "Get patient consents")
    public ResponseEntity<ApiResponse<List<ConsentResponse>>> getPatientConsents(
            @PathVariable Long patientId) {
        List<ConsentResponse> consents = consentService.getPatientConsents(patientId);
        return ResponseEntity.ok(ApiResponse.success("Consents retrieved", consents));
    }

    @GetMapping("/doctors/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "Get doctor consents")
    public ResponseEntity<ApiResponse<List<ConsentResponse>>> getDoctorConsents(
            @PathVariable Long doctorId) {
        List<ConsentResponse> consents = consentService.getDoctorConsents(doctorId);
        return ResponseEntity.ok(ApiResponse.success("Consents retrieved", consents));
    }

    @GetMapping("/verify")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "Verify consent is active")
    public ResponseEntity<ApiResponse<Boolean>> verifyConsent(
            @RequestParam Long patientId,
            @RequestParam Long doctorId) {
        boolean isActive = consentService.verifyConsent(patientId, doctorId);
        return ResponseEntity.ok(ApiResponse.success(
                isActive ? "Consent is active" : "Consent is not active", isActive));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Get consent details")
    public ResponseEntity<ApiResponse<ConsentResponse>> getConsentDetails(@PathVariable Long id) {
        ConsentResponse response = consentService.getConsentDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Consent retrieved", response));
    }
}
