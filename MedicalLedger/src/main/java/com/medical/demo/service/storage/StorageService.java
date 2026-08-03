package com.medical.demo.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadFile(byte[] data);
    byte[] downloadFile(String hash);
    void deleteFile(String hash);
    String getFileMetadata(String hash);
}
