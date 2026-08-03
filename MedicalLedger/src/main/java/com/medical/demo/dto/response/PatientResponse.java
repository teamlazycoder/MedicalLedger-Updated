package com.medical.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateOfBirth;
    private String gender;
    private String bloodType;
    private String contactNumber;
    private String address;
    private String allergies;
    private String chronicConditions;
    private Long totalRecords;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
