package com.medical.demo.repository;

import com.medical.demo.model.MedicalRecord;
import com.medical.demo.model.enums.RecordType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientIdAndIsDeletedFalse(Long patientId);
    List<MedicalRecord> findByPatientId(Long patientId);
    List<MedicalRecord> findByDoctorIdAndIsDeletedFalse(Long doctorId);
    List<MedicalRecord> findByRecordTypeAndIsDeletedFalse(RecordType recordType);
    List<MedicalRecord> findByIpfsHash(String ipfsHash);

    @Query("SELECT m FROM MedicalRecord m WHERE m.patient.id = :patientId " +
            "AND m.recordType = :recordType AND m.isDeleted = false")
    List<MedicalRecord> findByPatientAndType(@Param("patientId") Long patientId,
                                             @Param("recordType") RecordType recordType);

    @Query("SELECT m FROM MedicalRecord m WHERE m.createdAt BETWEEN :startDate AND :endDate " +
            "AND m.isDeleted = false")
    List<MedicalRecord> findRecordsByDateRange(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(m) FROM MedicalRecord m WHERE m.patient.id = :patientId AND m.isDeleted = false")
    Long countByPatientId(@Param("patientId") Long patientId);
}
