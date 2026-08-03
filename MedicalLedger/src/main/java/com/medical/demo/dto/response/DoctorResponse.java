package com.medical.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String specialty;
    private String licenseNumber;
    private String hospitalAffiliation;
    private Integer yearsOfExperience;
    private String qualifications;
    private Boolean isAvailable;
    private Boolean isVerified;
    private Long totalPatients;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
