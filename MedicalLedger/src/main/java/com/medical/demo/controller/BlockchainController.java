
package com.medical.demo.controller;

import com.medical.demo.dto.response.ApiResponse;
import com.medical.demo.service.blockchain.BlockchainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/blockchain")
@RequiredArgsConstructor
@Tag(name = "Blockchain", description = "Blockchain verification APIs")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class BlockchainController {

    private final BlockchainService blockchainService;

    @GetMapping("/verify/{hash}")
    @Operation(summary = "Verify hash on blockchain")
    public ResponseEntity<ApiResponse<Boolean>> verifyHash(@PathVariable String hash) {
        boolean verified = blockchainService.verifyHash(hash);
        return ResponseEntity.ok(ApiResponse.success(
                verified ? "Hash verified" : "Hash not found", verified));
    }

    @GetMapping("/transaction/{txId}")
    @Operation(summary = "Get transaction details")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTransaction(@PathVariable String txId) {
        Map<String, Object> details = blockchainService.getTransactionDetails(txId);
        return ResponseEntity.ok(ApiResponse.success("Transaction retrieved", details));
    }

    @GetMapping("/health")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Check blockchain health")
    public ResponseEntity<ApiResponse<Boolean>> checkHealth() {
        boolean healthy = blockchainService.checkHealth();
        return ResponseEntity.ok(ApiResponse.success(
                healthy ? "Blockchain is healthy" : "Blockchain is unhealthy", healthy));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get blockchain statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        Map<String, Object> stats = blockchainService.getBlockchainStats();
        return ResponseEntity.ok(ApiResponse.success("Blockchain stats retrieved", stats));
    }
}