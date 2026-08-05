package com.medical.demo.dto.request;
import com.medical.demo.model.enums.RecordType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor; import lombok.Data; import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class ConsentGrantRequest {
    @NotNull(message="Doctor ID is required") private Long doctorId;
    private RecordType recordType = RecordType.ALL;
    @NotNull(message="Start date is required") private LocalDateTime startDate;
    @NotNull(message="End date is required") private LocalDateTime endDate; // Removed @Future
    private String purpose;
}