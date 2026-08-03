
package com.medical.demo.controller;

import com.medical.demo.dto.request.RecordUploadRequest;
import com.medical.demo.dto.response.ApiResponse;
import com.medical.demo.dto.response.RecordResponse;
import com.medical.demo.model.User;
import com.medical.demo.service.record.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
@Tag(name = "Medical Records", description = "Medical record management APIs")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class RecordController {

    private final RecordService recordService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @Operation(summary = "Upload medical record")
    public ResponseEntity<ApiResponse<RecordResponse>> uploadRecord(
            @Valid @ModelAttribute RecordUploadRequest request,
            @AuthenticationPrincipal User currentUser) {
        RecordResponse response = recordService.uploadRecord(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Record uploaded successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Get record details")
    public ResponseEntity<ApiResponse<RecordResponse>> getRecord(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        RecordResponse response = recordService.getRecordDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Record retrieved", response));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @Operation(summary = "Get patient records")
    public ResponseEntity<ApiResponse<List<RecordResponse>>> getPatientRecords(
            @PathVariable Long patientId,
            @AuthenticationPrincipal User currentUser) {
        List<RecordResponse> records = recordService.getPatientRecords(patientId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Records retrieved", records));
    }

    @GetMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Verify record on blockchain")
    public ResponseEntity<ApiResponse<RecordResponse>> verifyRecord(@PathVariable Long id) {
        RecordResponse response = recordService.verifyRecordOnBlockchain(id);
        return ResponseEntity.ok(ApiResponse.success("Record verified", response));
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @Operation(summary = "Download medical record")
    public ResponseEntity<byte[]> downloadRecord(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        byte[] data = recordService.downloadRecord(id, currentUser);
        RecordResponse record = recordService.getRecordDetails(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + record.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Delete medical record (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        recordService.deleteRecord(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Record deleted successfully", null));
    }

    @GetMapping("/{id}/access-log")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    @Operation(summary = "Get record access history")
    public ResponseEntity<ApiResponse<List<RecordResponse>>> getRecordAccessLog(@PathVariable Long id) {
        List<RecordResponse> history = recordService.getRecordAccessHistory(id);
        return ResponseEntity.ok(ApiResponse.success("Access log retrieved", history));
    }
}