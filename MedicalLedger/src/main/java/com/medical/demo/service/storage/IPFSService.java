package com.medical.demo.service.storage;
import lombok.extern.slf4j.Slf4j; import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets; import java.security.MessageDigest;
import java.util.*; import java.util.concurrent.ConcurrentHashMap;

@Slf4j @Service
public class IPFSService implements StorageService {
    private final Map<String,byte[]> mockStorage = new ConcurrentHashMap<>();
    @Override public String uploadFile(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            String fullHash = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
            String ipfsHash;
            if(fullHash.length()>=44) ipfsHash = "Qm"+fullHash.substring(0,44);
            else { StringBuilder padded=new StringBuilder(fullHash); while(padded.length()<44) padded.append("0"); ipfsHash="Qm"+padded.toString(); }
            mockStorage.put(ipfsHash,data);
            log.info("File uploaded to IPFS. Hash: {}, Size: {} bytes",ipfsHash,data.length);
            return ipfsHash;
        } catch(Exception e) { log.error("Failed to generate IPFS hash",e); String fallback="Qm"+Integer.toHexString(Arrays.hashCode(data)); while(fallback.length()<46) fallback+="0"; mockStorage.put(fallback,data); return fallback; }
    }
    @Override public byte[] downloadFile(String hash) { byte[] data=mockStorage.get(hash); if(data==null) throw new RuntimeException("File not found: "+hash); return data; }
    @Override public void deleteFile(String hash) { mockStorage.remove(hash); }
    @Override public String getFileMetadata(String hash) { return mockStorage.containsKey(hash)?"Size: "+mockStorage.get(hash).length+" bytes":"File not found"; }
}