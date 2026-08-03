package com.medical.demo.repository;

import com.medical.demo.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(Long userId);

    @Query("SELECT d FROM Doctor d WHERE " +
            "LOWER(d.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.specialty) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.hospitalAffiliation) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Doctor> searchDoctors(@Param("searchTerm") String searchTerm);

    List<Doctor> findBySpecialty(String specialty);
    List<Doctor> findByHospitalAffiliation(String hospital);
    List<Doctor> findByIsVerifiedTrue();
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
}