package com.medical.demo.controller;
import com.medical.demo.dto.request.*; import com.medical.demo.dto.response.*;
import com.medical.demo.model.User; import com.medical.demo.repository.UserRepository;
import com.medical.demo.service.record.RecordService;
import io.swagger.v3.oas.annotations.*; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor;
import org.springframework.http.*; import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*; import java.util.List;

@RestController @RequestMapping("/api/v1/records") @RequiredArgsConstructor
@Tag(name="Medical Records",description="Medical record management APIs")
public class RecordController {
    private final RecordService recordService; private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null&&auth.isAuthenticated()&&!"anonymousUser".equals(auth.getName()))
            return userRepository.findByEmail(auth.getName()).orElse(null);
        return null;
    }

    @PostMapping(value="/upload",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR')")
    public ResponseEntity<ApiResponse<RecordResponse>> uploadRecord(@Valid @ModelAttribute RecordUploadRequest request) {
        User u = getCurrentUser(); if(u==null) return ResponseEntity.status(401).body(ApiResponse.error("Authentication required"));
        return ResponseEntity.ok(ApiResponse.success("Record uploaded",recordService.uploadRecord(request,u)));
    }
    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<RecordResponse>> getRecord(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Record retrieved",recordService.getRecordDetails(id)));
    }
    @GetMapping("/patient/{patientId}") @PreAuthorize("hasAnyRole('PATIENT','DOCTOR')")
    public ResponseEntity<ApiResponse<List<RecordResponse>>> getPatientRecords(@PathVariable Long patientId) {
        User u = getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Records retrieved",recordService.getPatientRecords(patientId,u)));
    }
    @GetMapping("/{id}/verify") @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<RecordResponse>> verifyRecord(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Record verified",recordService.verifyRecordOnBlockchain(id)));
    }
    @GetMapping("/{id}/download") @PreAuthorize("hasAnyRole('PATIENT','DOCTOR')")
    public ResponseEntity<byte[]> downloadRecord(@PathVariable Long id) {
        User u = getCurrentUser();
        byte[] data = recordService.downloadRecord(id,u);
        RecordResponse rec = recordService.getRecordDetails(id);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+rec.getFileName()+"\"").contentType(MediaType.APPLICATION_OCTET_STREAM).body(data);
    }
    @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable Long id) {
        User u = getCurrentUser(); recordService.deleteRecord(id,u);
        return ResponseEntity.ok(ApiResponse.success("Record deleted",null));
    }
}