package com.medical.demo.service.blockchain;

import java.util.Map;

public interface BlockchainService {
    String recordHash(Long recordId, String ipfsHash, Map<String, Object> metadata);
    String createConsent(Long consentId, Map<String, Object> consentData);
    String updateConsentStatus(Long consentId, String status);
    boolean verifyConsent(Long patientId, Long doctorId);
    String logAccess(Map<String, Object> accessData);
    Map<String, Object> getRecordHash(String recordId);
    Map<String, Object> getConsent(String consentId);
    boolean verifyHash(String hash);
    Map<String, Object> getTransactionDetails(String txId);
    boolean checkHealth();
    Map<String, Object> getBlockchainStats();
}
