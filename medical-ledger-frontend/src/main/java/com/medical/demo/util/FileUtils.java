
package com.medical.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class FileUtils {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".pdf", ".jpg", ".jpeg", ".png", ".dcm", ".txt", ".doc", ".docx"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }

    public boolean isValidFileType(String fileName) {
        String extension = getFileExtension(fileName);
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    public boolean isValidFileSize(long fileSize) {
        return fileSize <= MAX_FILE_SIZE;
    }

    public String generateFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        return System.currentTimeMillis() + "_" + Math.random() + extension;
    }

    public File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir") + "/" +
                multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }
        return file;
    }

    public byte[] readFileToBytes(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    public void writeBytesToFile(byte[] data, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.write(path, data);
    }

    public void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
            log.info("File deleted: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath, e);
        }
    }
}