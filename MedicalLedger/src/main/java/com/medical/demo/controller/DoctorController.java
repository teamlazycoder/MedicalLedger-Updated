package com.medical.demo.controller;

import com.medical.demo.dto.response.*;
import com.medical.demo.model.Doctor;
import com.medical.demo.service.consent.ConsentService;
import com.medical.demo.service.doctor.DoctorService;
import com.medical.demo.service.record.RecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Tag(name="Doctor", description="Doctor management APIs")
public class DoctorController {
    private final DoctorService doctorService;
    private final ConsentService consentService;
    private final RecordService recordService;

    // Get doctor by DOCTOR ID (from doctors table)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Doctor retrieved", doctorService.getDoctorDetails(id)));
    }

    // NEW: Get doctor by USER ID (from users table)
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorByUserId(@PathVariable Long userId) {
        Doctor doctor = doctorService.getDoctorByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Doctor retrieved", doctorService.getDoctorDetails(doctor.getId())));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> searchDoctors(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success("Doctors found", doctorService.searchDoctors(q)));
    }

    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getDoctorsBySpecialty(@PathVariable String specialty) {
        return ResponseEntity.ok(ApiResponse.success("Doctors retrieved", doctorService.getDoctorsBySpecialty(specialty)));
    }

    @GetMapping("/{id}/consents")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<ConsentResponse>>> getDoctorConsents(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Consents retrieved", consentService.getDoctorConsents(id)));
    }

    @GetMapping("/{id}/records")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<RecordResponse>>> getDoctorRecords(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Records retrieved", recordService.getDoctorRecords(id)));
    }
}