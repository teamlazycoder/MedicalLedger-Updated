package com.medical.demo.dto.response;

import com.medical.demo.model.enums.AccessAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private Long id;
    private Long recordId;
    private String patientName;
    private String actorName;
    private String actorRole;
    private AccessAction action;
    private String ipAddress;
    private String userAgent;
    private String blockchainTxId;
    private Map<String, Object> metadata;
    private LocalDateTime timestamp;
}
