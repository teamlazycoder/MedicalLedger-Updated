package com.medical.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 100)
    private String specialty;

    @Column(name = "license_number", unique = true, length = 50)
    private String licenseNumber;

    @Column(name = "hospital_affiliation", nullable = false, length = 200)
    private String hospitalAffiliation;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(columnDefinition = "TEXT")
    private String qualifications;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MedicalRecord> medicalRecords = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ConsentPolicy> consentPolicies = new ArrayList<>();
}
