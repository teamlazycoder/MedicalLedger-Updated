package com.medical.demo.dto.request;

import com.medical.demo.model.enums.RecordType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RecordUploadRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    private Long doctorId;  // ADDED

    @NotNull(message = "Record type is required")
    private RecordType recordType;

    private String description;
    private String diagnosis;
    private String treatment;

    private MultipartFile file;
}