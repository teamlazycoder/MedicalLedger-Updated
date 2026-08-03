package com.medical.demo.repository;

import com.medical.demo.model.ConsentPolicy;
import com.medical.demo.model.Doctor;
import com.medical.demo.model.Patient;
import com.medical.demo.model.enums.ConsentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsentPolicyRepository extends JpaRepository<ConsentPolicy, Long> {
    List<ConsentPolicy> findByPatientId(Long patientId);
    List<ConsentPolicy> findByDoctorId(Long doctorId);
    List<ConsentPolicy> findByPatientIdAndStatus(Long patientId, ConsentStatus status);

    @Query("SELECT c FROM ConsentPolicy c WHERE c.patient = :patient AND c.doctor = :doctor " +
            "AND c.status = 'ACTIVE' AND c.startDate <= :now AND c.endDate >= :now")
    Optional<ConsentPolicy> findActiveConsent(@Param("patient") Patient patient,
                                              @Param("doctor") Doctor doctor,
                                              @Param("now") LocalDateTime now);

    List<ConsentPolicy> findByStatusAndEndDateBefore(ConsentStatus status, LocalDateTime date);

    @Query("SELECT c FROM ConsentPolicy c WHERE c.endDate BETWEEN :start AND :end " +
            "AND c.status = 'ACTIVE'")
    List<ConsentPolicy> findExpiringConsents(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);
}
