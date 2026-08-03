package com.medical.demo.dto.request;

import com.medical.demo.model.enums.RecordType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordUploadRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Record type is required")
    private RecordType recordType;

    @NotNull(message = "File is required")
    private MultipartFile file;

    private String description;
    private String diagnosis;
    private String treatment;
    private String notes;
}
