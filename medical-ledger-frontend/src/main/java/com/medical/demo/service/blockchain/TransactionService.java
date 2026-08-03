package com.medical.demo.service.blockchain;

import com.medical.demo.model.BlockchainTransaction;
import com.medical.demo.repository.BlockchainTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final BlockchainTransactionRepository transactionRepository;

    @Transactional
    public BlockchainTransaction saveTransaction(String txId, String function,
                                                 Map<String, Object> payload, String status) {
        BlockchainTransaction transaction = new BlockchainTransaction();
        transaction.setTxId(txId);
        transaction.setChaincodeFunction(function);
        transaction.setPayload(payload);
        transaction.setStatus(status);
        transaction.setTimestamp(LocalDateTime.now());

        if ("CONFIRMED".equals(status)) {
            transaction.setConfirmedAt(LocalDateTime.now());
        }

        return transactionRepository.save(transaction);
    }

    public Map<String, Object> getTransactionDetails(String txId) {
        return transactionRepository.findByTxId(txId)
                .map(tx -> {
                    Map<String, Object> details = new HashMap<>();
                    details.put("txId", tx.getTxId());
                    details.put("function", tx.getChaincodeFunction());
                    details.put("status", tx.getStatus());
                    details.put("timestamp", tx.getTimestamp().toString());
                    details.put("confirmedAt", tx.getConfirmedAt() != null ?
                            tx.getConfirmedAt().toString() : null);
                    details.put("payload", tx.getPayload());
                    return details;
                })
                .orElseGet(() -> {
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "Transaction not found");
                    return error;
                });
    }
}