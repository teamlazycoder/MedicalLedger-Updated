package com.medical.demo.service.consent;

import com.medical.demo.dto.request.ConsentGrantRequest;
import com.medical.demo.dto.response.ConsentResponse;
import com.medical.demo.exception.BusinessException;
import com.medical.demo.exception.ResourceNotFoundException;
import com.medical.demo.exception.UnauthorizedAccessException;
import com.medical.demo.model.*;
import com.medical.demo.model.enums.AccessAction;
import com.medical.demo.model.enums.ConsentStatus;
import com.medical.demo.model.enums.Role;
import com.medical.demo.repository.ConsentPolicyRepository;
import com.medical.demo.repository.DoctorRepository;
import com.medical.demo.repository.PatientRepository;
import com.medical.demo.service.audit.AuditService;
import com.medical.demo.service.events.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final EventPublisher eventPublisher;

    @Override
    public ConsentPolicy grantConsent(Long patientId, ConsentGrantRequest request, User currentUser) {
        return null;
    }

    @Override
    public ConsentPolicy revokeConsent(Long consentId, Long patientId, User currentUser) {
        return null;
    }

    @Override
    public ConsentPolicy renewConsent(Long consentId, LocalDateTime newEndDate) {
        return null;
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
        return false;
    }

    @Override
    public void expireConsents() {

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
