package com.medical.demo.service.consent;

import com.medical.demo.exception.BusinessException;
import com.medical.demo.model.ConsentPolicy;
import com.medical.demo.model.enums.ConsentStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ConsentValidator {

    public void validateConsentDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new BusinessException("Start date and end date are required");
        }

        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date cannot be after end date");
        }

        if (endDate.isBefore(LocalDateTime.now())) {
            throw new BusinessException("End date cannot be in the past");
        }

        if (startDate.plusYears(1).isBefore(endDate)) {
            throw new BusinessException("Consent duration cannot exceed 1 year");
        }
    }

    public void validateConsentStatus(ConsentPolicy consent, ConsentStatus expectedStatus) {
        if (consent.getStatus() != expectedStatus) {
            throw new BusinessException("Consent must be in " + expectedStatus + " status");
        }
    }

    public boolean isConsentValid(ConsentPolicy consent) {
        return consent.getStatus() == ConsentStatus.ACTIVE &&
                LocalDateTime.now().isAfter(consent.getStartDate()) &&
                LocalDateTime.now().isBefore(consent.getEndDate());
    }
}
