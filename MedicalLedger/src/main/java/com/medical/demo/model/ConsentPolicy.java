package com.medical.demo.model;

import com.medical.demo.model.enums.ConsentStatus;
import com.medical.demo.model.enums.RecordType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "consent_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsentPolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", length = 50)
    private RecordType recordType = RecordType.ALL;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConsentStatus status = ConsentStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String purpose;

    @Column(name = "blockchain_tx_id", length = 100)
    private String blockchainTxId;

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (status == null) {
            status = ConsentStatus.ACTIVE;
        }
    }
}
