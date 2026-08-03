package com.medical.demo.service.record;

import com.medical.demo.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
public class RecordValidationService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "image/dicom",
            "application/dicom",
            "text/plain"
    );

    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("File is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("File size exceeds maximum limit of 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException("File type not allowed. Allowed types: PDF, JPEG, PNG, DICOM, TXT");
        }
    }

    public void validateRecordData(String description, String diagnosis) {
        if (description != null && description.length() > 2000) {
            throw new BusinessException("Description cannot exceed 2000 characters");
        }
        if (diagnosis != null && diagnosis.length() > 1000) {
            throw new BusinessException("Diagnosis cannot exceed 1000 characters");
        }
    }
}
