package com.medical.demo.service.blockchain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockchainServiceImpl implements BlockchainService {

    private final FabricGatewayWrapper fabricGateway;
    private final TransactionService transactionService;

    @Override
    public String recordHash(Long recordId, String ipfsHash, Map<String, Object> metadata) {
        log.info("Recording hash on blockchain for record: {}", recordId);

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("recordId", recordId.toString());
            payload.put("ipfsHash", ipfsHash);
            payload.put("timestamp", LocalDateTime.now().toString());
            payload.put("metadata", metadata);

            // In a real implementation, this would call Hyperledger Fabric
            String txId = fabricGateway.submitTransaction("recordHash",
                    recordId.toString(),
                    ipfsHash,
                    LocalDateTime.now().toString());

            // Save transaction record
            transactionService.saveTransaction(txId, "recordHash", payload, "CONFIRMED");

            return txId;
        } catch (Exception e) {
            log.error("Failed to record hash on blockchain", e);
            // For hackathon, generate mock transaction ID
            String mockTxId = "tx_" + UUID.randomUUID().toString().substring(0, 16);
            Map<String, Object> payload = new HashMap<>();
            payload.put("recordId", recordId.toString());
            payload.put("ipfsHash", ipfsHash);
            transactionService.saveTransaction(mockTxId, "recordHash", payload, "PENDING");
            return mockTxId;
        }
    }

    @Override
    public String createConsent(Long consentId, Map<String, Object> consentData) {
        log.info("Creating consent on blockchain: {}", consentId);

        String txId = "tx_consent_" + UUID.randomUUID().toString().substring(0, 16);
        Map<String, Object> payload = new HashMap<>(consentData);
        payload.put("consentId", consentId.toString());
        payload.put("timestamp", LocalDateTime.now().toString());

        transactionService.saveTransaction(txId, "createConsent", payload, "CONFIRMED");
        return txId;
    }

    @Override
    public String updateConsentStatus(Long consentId, String status) {
        log.info("Updating consent status on blockchain: {} -> {}", consentId, status);

        String txId = "tx_update_" + UUID.randomUUID().toString().substring(0, 16);
        Map<String, Object> payload = new HashMap<>();
        payload.put("consentId", consentId.toString());
        payload.put("status", status);
        payload.put("timestamp", LocalDateTime.now().toString());

        transactionService.saveTransaction(txId, "updateConsent", payload, "CONFIRMED");
        return txId;
    }

    @Override
    public boolean verifyConsent(Long patientId, Long doctorId) {
        log.info("Verifying consent on blockchain: Patient={}, Doctor={}", patientId, doctorId);
        // Simulated blockchain verification
        return true;
    }

    @Override
    public String logAccess(Map<String, Object> accessData) {
        String txId = "tx_access_" + UUID.randomUUID().toString().substring(0, 16);
        accessData.put("timestamp", LocalDateTime.now().toString());

        transactionService.saveTransaction(txId, "logAccess", accessData, "CONFIRMED");
        return txId;
    }

    @Override
    public Map<String, Object> getRecordHash(String recordId) {
        Map<String, Object> result = new HashMap<>();
        result.put("recordId", recordId);
        result.put("hash", "mock_hash_" + recordId);
        result.put("verified", true);
        return result;
    }

    @Override
    public Map<String, Object> getConsent(String consentId) {
        Map<String, Object> result = new HashMap<>();
        result.put("consentId", consentId);
        result.put("status", "ACTIVE");
        result.put("verified", true);
        return result;
    }

    @Override
    public boolean verifyHash(String hash) {
        // Simulated hash verification
        return true;
    }

    @Override
    public Map<String, Object> getTransactionDetails(String txId) {
        return transactionService.getTransactionDetails(txId);
    }

    @Override
    public boolean checkHealth() {
        // Simulated health check
        return true;
    }

    @Override
    public Map<String, Object> getBlockchainStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTransactions", 1000L);
        stats.put("blocks", 100L);
        stats.put("peers", 3);
        stats.put("status", "HEALTHY");
        return stats;
    }
}
