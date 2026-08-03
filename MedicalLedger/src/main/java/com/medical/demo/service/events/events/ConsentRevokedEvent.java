package com.medical.demo.service.events.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsentRevokedEvent {
    private Long consentId;
    private Long patientId;
    private Long doctorId;
    private String reason;
    private LocalDateTime timestamp;
}
