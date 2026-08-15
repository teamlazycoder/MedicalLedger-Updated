package com.medical.demo.service.record;

import com.medical.demo.dto.request.RecordUploadRequest;
import com.medical.demo.dto.response.RecordResponse;
import com.medical.demo.exception.BusinessException;
import com.medical.demo.exception.ResourceNotFoundException;
import com.medical.demo.exception.UnauthorizedAccessException;
import com.medical.demo.model.*;
import com.medical.demo.model.enums.AccessAction;
import com.medical.demo.model.enums.Role;
import com.medical.demo.repository.*;
import com.medical.demo.service.audit.AuditService;
import com.medical.demo.service.events.EventPublisher;
import com.medical.demo.service.storage.IPFSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final MedicalRecordRepository recordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AuditService auditService;
    private final IPFSService ipfsService;
    private final EventPublisher eventPublisher;
    private final RecordEncryptionService encryptionService;

    @Override
    @Transactional
    public RecordResponse uploadRecord(RecordUploadRequest request, User currentUser) {
        log.info("Uploading medical record for patient: {}", request.getPatientId());

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        if (currentUser.getRole() == Role.PATIENT &&
                !patient.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You can only upload records for yourself");
        }

        Doctor doctor = null;
        if (currentUser.getRole() == Role.DOCTOR) {
            doctor = doctorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));
        } else if (request.getDoctorId() != null) {
            doctor = doctorRepository.findById(request.getDoctorId()).orElse(null);
        }

        try {
            MultipartFile file = request.getFile();
            if (file == null || file.isEmpty()) {
                throw new BusinessException("File is required");
            }

            byte[] fileBytes = file.getBytes();
            String encryptionKey = encryptionService.generateEncryptionKey();
            byte[] encryptedData = encryptionService.encryptFile(fileBytes, encryptionKey);
            String ipfsHash = ipfsService.uploadFile(encryptedData);
            String blockchainTxId = "tx_" + System.currentTimeMillis() + "_" +
                    patient.getId() + "_" + request.getRecordType();

            MedicalRecord record = new MedicalRecord();
            record.setPatient(patient);
            record.setDoctor(doctor);
            record.setRecordType(request.getRecordType());
            record.setFileName(file.getOriginalFilename());
            record.setFileSize(file.getSize());
            record.setIpfsHash(ipfsHash);
            record.setEncryptionKey(encryptionKey);
            record.setBlockchainTxId(blockchainTxId);
            record.setDescription(request.getDescription());
            record.setDiagnosis(request.getDiagnosis());
            record.setTreatment(request.getTreatment());
            record.setAccessCount(0L);
            record.setIsDeleted(false);

            record = recordRepository.save(record);
            auditService.logAccess(record, patient, currentUser, AccessAction.UPLOAD, "Record uploaded");

            log.info("Medical record uploaded successfully. ID: {}, IPFS: {}", record.getId(), ipfsHash);
            return mapToRecordResponse(record);

        } catch (IOException e) {
            log.error("Failed to process file upload", e);
            throw new BusinessException("Failed to process file upload: " + e.getMessage());
        }
    }

    @Override
    public MedicalRecord getRecordById(Long id) {
        return recordRepository.findById(id)
                .filter(record -> !record.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));
    }

    @Override
    @Cacheable(value = "recordDetails", key = "#id")
    public RecordResponse getRecordDetails(Long id) {
        MedicalRecord record = getRecordById(id);
        return mapToRecordResponse(record);
    }

    @Override
    public List<RecordResponse> getPatientRecords(Long patientId, User currentUser) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        if (currentUser.getRole() == Role.PATIENT &&
                !patient.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You can only view your own records");
        }

        auditService.logAccess(null, patient, currentUser, AccessAction.VIEW, "Viewed patient records list");

        return recordRepository.findByPatientIdAndIsDeletedFalse(patientId)
                .stream()
                .map(this::mapToRecordResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecordResponse> getDoctorRecords(Long doctorId) {
        return recordRepository.findByDoctorIdAndIsDeletedFalse(doctorId)
                .stream()
                .map(this::mapToRecordResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "recordDetails", key = "#id")
    public RecordResponse updateRecord(Long id, MedicalRecord recordDetails, User currentUser) {
        MedicalRecord record = getRecordById(id);

        if (currentUser.getRole() == Role.PATIENT) {
            throw new UnauthorizedAccessException("Patients cannot update medical records");
        }

        record.setDescription(recordDetails.getDescription());
        record.setDiagnosis(recordDetails.getDiagnosis());
        record.setTreatment(recordDetails.getTreatment());
        record = recordRepository.save(record);

        auditService.logAccess(record, record.getPatient(), currentUser, AccessAction.UPDATE, "Record updated");
        return mapToRecordResponse(record);
    }

    @Override
    @Transactional
    @CacheEvict(value = "recordDetails", key = "#id")
    public void deleteRecord(Long id, User currentUser) {
        MedicalRecord record = getRecordById(id);
        record.setIsDeleted(true);
        recordRepository.save(record);
        auditService.logAccess(record, record.getPatient(), currentUser, AccessAction.DELETE, "Record deleted");
        log.info("Record soft deleted: {}", id);
    }

    @Override
    public RecordResponse verifyRecordOnBlockchain(Long id) {
        MedicalRecord record = getRecordById(id);
        return mapToRecordResponse(record);
    }

    @Override
    public byte[] downloadRecord(Long id, User currentUser) {
        MedicalRecord record = getRecordById(id);
        Patient patient = record.getPatient();

        if (currentUser.getRole() == Role.PATIENT &&
                !patient.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You can only download your own records");
        }

        record.setAccessCount(record.getAccessCount() + 1);
        recordRepository.save(record);
        auditService.logAccess(record, patient, currentUser, AccessAction.DOWNLOAD, "Record downloaded");

        try {
            byte[] encryptedData = ipfsService.downloadFile(record.getIpfsHash());
            return encryptionService.decryptFile(encryptedData, record.getEncryptionKey());
        } catch (Exception e) {
            log.error("Failed to download record", e);
            throw new BusinessException("Failed to download record: " + e.getMessage());
        }
    }

    @Override
    public List<RecordResponse> getRecordAccessHistory(Long recordId) {
        MedicalRecord record = getRecordById(recordId);
        return auditService.getAuditLogsByRecord(recordId)
                .stream()
                .map(audit -> RecordResponse.builder()
                        .id(record.getId())
                        .fileName(record.getFileName())
                        .accessCount(record.getAccessCount())
                        .build())
                .collect(Collectors.toList());
    }

    private RecordResponse mapToRecordResponse(MedicalRecord record) {
        return RecordResponse.builder()
                .id(record.getId())
                .patientId(record.getPatient().getId())
                .patientName(record.getPatient().getFirstName() + " " + record.getPatient().getLastName())
                .doctorId(record.getDoctor() != null ? record.getDoctor().getId() : null)
                .doctorName(record.getDoctor() != null ?
                        "Dr. " + record.getDoctor().getFirstName() + " " + record.getDoctor().getLastName() : null)
                .recordType(record.getRecordType())
                .fileName(record.getFileName())
                .fileSize(record.getFileSize())
                .ipfsHash(record.getIpfsHash())
                .blockchainTxId(record.getBlockchainTxId())
                .description(record.getDescription())
                .diagnosis(record.getDiagnosis())
                .treatment(record.getTreatment())
                .accessCount(record.getAccessCount())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}