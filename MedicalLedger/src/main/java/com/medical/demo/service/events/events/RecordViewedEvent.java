package com.medical.demo.service.events.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordViewedEvent {
    private Long recordId;
    private Long patientId;
    private Long viewerId;
    private String viewerRole;
    private LocalDateTime timestamp;
}
