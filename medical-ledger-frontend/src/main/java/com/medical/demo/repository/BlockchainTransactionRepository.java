package com.medical.demo.repository;

import com.medical.demo.model.BlockchainTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockchainTransactionRepository extends JpaRepository<BlockchainTransaction, Long> {
    Optional<BlockchainTransaction> findByTxId(String txId);
    List<BlockchainTransaction> findByStatus(String status);
    List<BlockchainTransaction> findByChaincodeFunction(String function);
    List<BlockchainTransaction> findAllByOrderByTimestampDesc();
}
