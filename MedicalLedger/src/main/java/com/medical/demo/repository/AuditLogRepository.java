package com.medical.demo.repository;

import com.medical.demo.model.AuditLog;
import com.medical.demo.model.enums.AccessAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByPatientId(Long patientId);
    List<AuditLog> findByActorId(Long actorId);
    List<AuditLog> findByRecordId(Long recordId);
    List<AuditLog> findByAction(AccessAction action);

    @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY a.createdAt DESC")
    List<AuditLog> findAuditLogsByDateRange(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM AuditLog a WHERE a.patient.id = :patientId " +
            "ORDER BY a.createdAt DESC LIMIT :limit")
    List<AuditLog> findRecentAuditLogsByPatient(@Param("patientId") Long patientId,
                                                @Param("limit") int limit);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.action = :action")
    Long countByAction(@Param("action") AccessAction action);
}
