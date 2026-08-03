package com.medical.demo.dto.response;

import com.medical.demo.model.enums.ConsentStatus;
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
public class ConsentResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private RecordType recordType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ConsentStatus status;
    private String purpose;
    private String blockchainTxId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
