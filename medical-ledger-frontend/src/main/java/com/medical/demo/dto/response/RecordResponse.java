package com.medical.demo.dto.response;

import com.medical.demo.model.enums.RecordType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private RecordType recordType;
    private String fileName;
    private Long fileSize;
    private String ipfsHash;
    private String blockchainTxId;
    private String description;
    private String diagnosis;
    private String treatment;
    private Integer accessCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
