package com.medical.demo.service.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class IPFSService implements StorageService {

    // Simulated IPFS storage for hackathon
    private final Map<String, byte[]> mockStorage = new ConcurrentHashMap<>();

    @Override
    public String uploadFile(byte[] data) {
        try {
            // Generate hash from data (simulating IPFS content addressing)
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            String ipfsHash = "Qm" + Base64.getUrlEncoder().withoutPadding().encodeToString(hash).substring(0, 44);

            // Store in mock storage
            mockStorage.put(ipfsHash, data);

            log.info("File uploaded to IPFS. Hash: {}", ipfsHash);
            return ipfsHash;
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate IPFS hash", e);
            throw new RuntimeException("Failed to upload file to IPFS", e);
        }
    }

    @Override
    public byte[] downloadFile(String hash) {
        byte[] data = mockStorage.get(hash);
        if (data == null) {
            log.error("File not found in IPFS: {}", hash);
            throw new RuntimeException("File not found with hash: " + hash);
        }
        return data;
    }

    @Override
    public void deleteFile(String hash) {
        mockStorage.remove(hash);
        log.info("File deleted from IPFS. Hash: {}", hash);
    }

    @Override
    public String getFileMetadata(String hash) {
        byte[] data = mockStorage.get(hash);
        if (data != null) {
            return "Size: " + data.length + " bytes, Status: Available";
        }
        return "File not found";
    }
}
