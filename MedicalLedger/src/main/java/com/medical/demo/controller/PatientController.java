package com.medical.demo.controller;
import com.medical.demo.dto.request.*; import com.medical.demo.dto.response.*;
import com.medical.demo.model.*; import com.medical.demo.repository.UserRepository;
import com.medical.demo.service.consent.ConsentService; import com.medical.demo.service.patient.PatientService;
import com.medical.demo.service.record.RecordService;
import io.swagger.v3.oas.annotations.*; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*; import java.util.List;

@RestController @RequestMapping("/api/v1/patients") @RequiredArgsConstructor
@Tag(name="Patient",description="Patient management APIs")
public class PatientController {
    private final PatientService patientService; private final ConsentService consentService;
    private final RecordService recordService; private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null&&auth.isAuthenticated()&&!"anonymousUser".equals(auth.getName()))
            return userRepository.findByEmail(auth.getName()).orElse(null);
        return null;
    }

    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatient(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Patient retrieved",patientService.getPatientDetails(id)));
    }
    @GetMapping("/{id}/records") @PreAuthorize("hasAnyRole('PATIENT','DOCTOR')")
    public ResponseEntity<ApiResponse<List<RecordResponse>>> getPatientRecords(@PathVariable Long id) {
        User u = getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Records retrieved",recordService.getPatientRecords(id,u)));
    }
    @GetMapping("/{id}/consents") @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<List<ConsentResponse>>> getPatientConsents(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Consents retrieved",consentService.getPatientConsents(id)));
    }
    @PostMapping("/{id}/consents/grant") @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<ConsentResponse>> grantConsent(@PathVariable Long id, @Valid @RequestBody ConsentGrantRequest request) {
        User u = getCurrentUser();
        ConsentPolicy cp = consentService.grantConsent(id,request,u);
        if(cp==null) return ResponseEntity.badRequest().body(ApiResponse.error("Failed to grant consent"));
        return ResponseEntity.ok(ApiResponse.success("Consent granted",consentService.getConsentDetails(cp.getId())));
    }
    @PutMapping("/{patientId}/consents/{consentId}/revoke") @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<ConsentResponse>> revokeConsent(@PathVariable Long patientId, @PathVariable Long consentId) {
        User u = getCurrentUser();
        ConsentPolicy cp = consentService.revokeConsent(consentId,patientId,u);
        return ResponseEntity.ok(ApiResponse.success("Consent revoked",consentService.getConsentDetails(cp.getId())));
    }
}