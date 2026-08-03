package com.medical.demo.service.consent;

import com.medical.demo.dto.request.ConsentGrantRequest;
import com.medical.demo.dto.response.ConsentResponse;
import com.medical.demo.model.ConsentPolicy;
import com.medical.demo.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsentService {
    ConsentPolicy grantConsent(Long patientId, ConsentGrantRequest request, User currentUser);
    ConsentPolicy revokeConsent(Long consentId, Long patientId, User currentUser);
    ConsentPolicy renewConsent(Long consentId, LocalDateTime newEndDate);
    List<ConsentResponse> getPatientConsents(Long patientId);
    List<ConsentResponse> getDoctorConsents(Long doctorId);
    ConsentResponse getConsentDetails(Long consentId);
    boolean verifyConsent(Long patientId, Long doctorId);
    void expireConsents();
}