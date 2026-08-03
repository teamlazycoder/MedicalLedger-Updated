package com.medical.demo.controller;

import com.medical.demo.dto.response.ApiResponse;
import com.medical.demo.dto.response.ConsentResponse;
import com.medical.demo.dto.response.DoctorResponse;
import com.medical.demo.dto.response.RecordResponse;
import com.medical.demo.service.consent.ConsentService;
import com.medical.demo.service.doctor.DoctorService;
import com.medical.demo.service.record.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor", description = "Doctor management APIs")
public class DoctorController {

    private final DoctorService doctorService;
    private final ConsentService consentService;
    private final RecordService recordService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "Get doctor details")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctor(@PathVariable Long id) {
        DoctorResponse response = doctorService.getDoctorDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Doctor retrieved", response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search doctors by name or specialty")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> searchDoctors(@RequestParam String q) {
        List<DoctorResponse> doctors = doctorService.searchDoctors(q);
        return ResponseEntity.ok(ApiResponse.success("Doctors found", doctors));
    }

    @GetMapping("/specialty/{specialty}")
    @Operation(summary = "Get doctors by specialty")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getDoctorsBySpecialty(@PathVariable String specialty) {
        List<DoctorResponse> doctors = doctorService.getDoctorsBySpecialty(specialty);
        return ResponseEntity.ok(ApiResponse.success("Doctors retrieved", doctors));
    }

    @GetMapping("/{id}/consents")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Get doctor consent policies")
    public ResponseEntity<ApiResponse<List<ConsentResponse>>> getDoctorConsents(@PathVariable Long id) {
        List<ConsentResponse> consents = consentService.getDoctorConsents(id);
        return ResponseEntity.ok(ApiResponse.success("Consents retrieved", consents));
    }

    //Creating Issue
    @GetMapping("/{id}/records")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Get records uploaded by doctor")
    public ResponseEntity<ApiResponse<List<RecordResponse>>> getDoctorRecords(@PathVariable Long id) {
        List<RecordResponse> records = recordService.getDoctorRecords(id);
        return ResponseEntity.ok(ApiResponse.success("Records retrieved", records));
    }
}