package com.medical.demo.service.consent;

import com.medical.demo.dto.request.ConsentGrantRequest;
import com.medical.demo.dto.response.ConsentResponse;
import com.medical.demo.exception.BusinessException;
import com.medical.demo.exception.ResourceNotFoundException;
import com.medical.demo.exception.UnauthorizedAccessException;
import com.medical.demo.model.*;
import com.medical.demo.model.enums.AccessAction;
import com.medical.demo.model.enums.ConsentStatus;
import com.medical.demo.model.enums.RecordType;
import com.medical.demo.repository.ConsentPolicyRepository;
import com.medical.demo.repository.DoctorRepository;
import com.medical.demo.repository.PatientRepository;
import com.medical.demo.service.audit.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsentServiceImpl implements ConsentService {

    private final ConsentPolicyRepository consentPolicyRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AuditService auditService;

    @Override
    @Transactional
    public ConsentPolicy grantConsent(Long patientId, ConsentGrantRequest request, User currentUser) {
        log.info("Granting consent: Patient={}, Doctor={}", patientId, request.getDoctorId());

        // Find patient
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        // Verify the current user is the patient
        if (!patient.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You can only grant consent for your own records");
        }

        // Find doctor
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + request.getDoctorId()));

        // Validate dates
        if (request.getStartDate() == null) {
            request.setStartDate(LocalDateTime.now());
        }
        if (request.getEndDate() == null) {
            request.setEndDate(LocalDateTime.now().plusYears(1));
        }

        // Create consent
        ConsentPolicy consent = new ConsentPolicy();
        consent.setPatient(patient);
        consent.setDoctor(doctor);
        consent.setRecordType(request.getRecordType() != null ? request.getRecordType() : RecordType.ALL);
        consent.setStartDate(request.getStartDate());
        consent.setEndDate(request.getEndDate());
        consent.setStatus(ConsentStatus.ACTIVE);
        consent.setPurpose(request.getPurpose());

        // Generate blockchain TX ID
        String txId = "tx_consent_" + System.currentTimeMillis() + "_" + patientId + "_" + request.getDoctorId();
        consent.setBlockchainTxId(txId);

        // Save
        consent = consentPolicyRepository.save(consent);

        log.info("Consent granted successfully. ID: {}", consent.getId());

        // Create audit log
        try {
            auditService.logAccess(null, patient, currentUser, AccessAction.GRANT_ACCESS,
                    "Granted consent to Dr. " + doctor.getFirstName() + " " + doctor.getLastName());
        } catch (Exception e) {
            log.warn("Audit log failed: {}", e.getMessage());
        }

        return consent;
    }

    @Override
    @Transactional
    public ConsentPolicy revokeConsent(Long consentId, Long patientId, User currentUser) {
        ConsentPolicy consent = consentPolicyRepository.findById(consentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consent not found"));

        if (!consent.getPatient().getId().equals(patientId)) {
            throw new BusinessException("Consent does not belong to this patient");
        }

        consent.setStatus(ConsentStatus.REVOKED);
        consent = consentPolicyRepository.save(consent);

        try {
            auditService.logAccess(null, consent.getPatient(), currentUser, AccessAction.REVOKE_ACCESS,
                    "Revoked consent for Dr. " + consent.getDoctor().getLastName());
        } catch (Exception e) {
            log.warn("Audit log failed: {}", e.getMessage());
        }

        return consent;
    }

    @Override
    public List<ConsentResponse> getPatientConsents(Long patientId) {
        return consentPolicyRepository.findByPatientId(patientId)
                .stream()
                .map(this::mapToConsentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsentResponse> getDoctorConsents(Long doctorId) {
        return consentPolicyRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::mapToConsentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ConsentResponse getConsentDetails(Long consentId) {
        ConsentPolicy consent = consentPolicyRepository.findById(consentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consent not found"));
        return mapToConsentResponse(consent);
    }

    @Override
    public boolean verifyConsent(Long patientId, Long doctorId) {
        Patient patient = patientRepository.findById(patientId).orElse(null);
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (patient == null || doctor == null) return false;
        return consentPolicyRepository.findActiveConsent(patient, doctor, LocalDateTime.now()).isPresent();
    }

    @Override
    @Transactional
    public ConsentPolicy renewConsent(Long consentId, LocalDateTime newEndDate) {
        ConsentPolicy consent = consentPolicyRepository.findById(consentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consent not found"));
        consent.setEndDate(newEndDate);
        return consentPolicyRepository.save(consent);
    }

    @Override
    @Transactional
    public void expireConsents() {
        List<ConsentPolicy> expired = consentPolicyRepository
                .findByStatusAndEndDateBefore(ConsentStatus.ACTIVE, LocalDateTime.now());
        expired.forEach(c -> c.setStatus(ConsentStatus.EXPIRED));
        consentPolicyRepository.saveAll(expired);
        if (!expired.isEmpty()) log.info("Expired {} consents", expired.size());
    }

    private ConsentResponse mapToConsentResponse(ConsentPolicy consent) {
        return ConsentResponse.builder()
                .id(consent.getId())
                .patientId(consent.getPatient().getId())
                .patientName(consent.getPatient().getFirstName() + " " + consent.getPatient().getLastName())
                .doctorId(consent.getDoctor().getId())
                .doctorName("Dr. " + consent.getDoctor().getFirstName() + " " + consent.getDoctor().getLastName())
                .recordType(consent.getRecordType())
                .startDate(consent.getStartDate())
                .endDate(consent.getEndDate())
                .status(consent.getStatus())
                .purpose(consent.getPurpose())
                .blockchainTxId(consent.getBlockchainTxId())
                .createdAt(consent.getCreatedAt())
                .updatedAt(consent.getUpdatedAt())
                .build();
    }
}