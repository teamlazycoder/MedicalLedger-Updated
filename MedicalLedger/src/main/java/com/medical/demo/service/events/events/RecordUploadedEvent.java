package com.medical.demo.service.events.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordUploadedEvent {
    private Long recordId;
    private Long patientId;
    private Long doctorId;
    private String recordType;
    private String ipfsHash;
    private LocalDateTime timestamp;
}
