package com.medical.demo.model;

import com.medical.demo.model.enums.RecordType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", length = 50)
    private RecordType recordType;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "ipfs_hash", nullable = false, length = 100)
    private String ipfsHash;

    @Column(name = "encryption_key", nullable = false, columnDefinition = "TEXT")
    private String encryptionKey;

    @Column(name = "blockchain_tx_id", length = 100)
    private String blockchainTxId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String treatment;

    @Column(name = "access_count")
    private Long accessCount = 0L;  // Long type

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}