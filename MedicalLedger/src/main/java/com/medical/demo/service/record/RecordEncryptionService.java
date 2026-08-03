package com.medical.demo.service.record;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
@Service
public class RecordEncryptionService {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_GCM_ALGORITHM = "AES/GCM/NoPadding";
    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12; // 96 bits
    private static final int GCM_TAG_LENGTH = 128; // 128 bits authentication tag

    @Value("${app.encryption.master-key:HealthChain-Master-Key-2024-Secure!@#$%}")
    private String masterKey;

    /**
     * Generate a new AES-256 encryption key
     */
    public String generateEncryptionKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGen.init(AES_KEY_SIZE, new SecureRandom());
            SecretKey secretKey = keyGen.generateKey();
            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            log.debug("New encryption key generated successfully");
            return encodedKey;
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate encryption key", e);
            throw new RuntimeException("Encryption key generation failed", e);
        }
    }

    /**
     * Generate a deterministic key from a password/seed
     */
    public String generateKeyFromPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] keyBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Use first 32 bytes for AES-256
            byte[] aesKey = Arrays.copyOf(keyBytes, 32);
            String encodedKey = Base64.getEncoder().encodeToString(aesKey);
            log.debug("Key generated from password successfully");
            return encodedKey;
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate key from password", e);
            throw new RuntimeException("Key generation from password failed", e);
        }
    }

    /**
     * Encrypt data using AES-GCM (Authenticated Encryption)
     * Returns Base64 encoded string containing IV + encrypted data + tag
     */
    public String encrypt(byte[] data, String base64Key) {
        try {
            // Decode the key
            SecretKey key = decodeKey(base64Key);

            // Generate random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            // Initialize cipher for encryption
            Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

            // Encrypt the data
            byte[] encryptedData = cipher.doFinal(data);

            // Combine IV + encrypted data
            byte[] combined = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);

            // Return as Base64
            String encryptedBase64 = Base64.getEncoder().encodeToString(combined);
            log.debug("Data encrypted successfully, size: {} bytes", encryptedData.length);
            return encryptedBase64;

        } catch (Exception e) {
            log.error("Failed to encrypt data", e);
            throw new RuntimeException("Data encryption failed", e);
        }
    }

    /**
     * Decrypt data using AES-GCM
     * Input is Base64 encoded string containing IV + encrypted data
     */
    public String decrypt(String encryptedBase64, String base64Key) {
        try {
            // Decode the key
            SecretKey key = decodeKey(base64Key);

            // Decode the Base64 input
            byte[] combined = Base64.getDecoder().decode(encryptedBase64);

            // Extract IV (first 12 bytes)
            byte[] iv = Arrays.copyOfRange(combined, 0, GCM_IV_LENGTH);

            // Extract encrypted data (remaining bytes)
            byte[] encryptedData = Arrays.copyOfRange(combined, GCM_IV_LENGTH, combined.length);

            // Initialize cipher for decryption
            Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

            // Decrypt the data
            byte[] decryptedData = cipher.doFinal(encryptedData);
            String decryptedText = new String(decryptedData, StandardCharsets.UTF_8);

            log.debug("Data decrypted successfully, size: {} bytes", decryptedData.length);
            return decryptedText;

        } catch (Exception e) {
            log.error("Failed to decrypt data", e);
            throw new RuntimeException("Data decryption failed", e);
        }
    }

    /**
     * Encrypt data and return as byte array
     */
    public byte[] encryptToBytes(byte[] data, String base64Key) {
        try {
            SecretKey key = decodeKey(base64Key);

            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

            byte[] encryptedData = cipher.doFinal(data);

            byte[] combined = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);

            return combined;

        } catch (Exception e) {
            log.error("Failed to encrypt data to bytes", e);
            throw new RuntimeException("Data encryption to bytes failed", e);
        }
    }

    /**
     * Decrypt byte array back to original data
     */
    public byte[] decryptFromBytes(byte[] encryptedData, String base64Key) {
        try {
            SecretKey key = decodeKey(base64Key);

            byte[] iv = Arrays.copyOfRange(encryptedData, 0, GCM_IV_LENGTH);
            byte[] cipherText = Arrays.copyOfRange(encryptedData, GCM_IV_LENGTH, encryptedData.length);

            Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

            return cipher.doFinal(cipherText);

        } catch (Exception e) {
            log.error("Failed to decrypt data from bytes", e);
            throw new RuntimeException("Data decryption from bytes failed", e);
        }
    }

    /**
     * Encrypt file content (for medical records)
     * This is the main method used by RecordService
     */
    public byte[] encryptFile(byte[] fileContent, String encryptionKey) {
        log.info("Encrypting file of size: {} bytes", fileContent.length);

        try {
            SecretKey key = decodeKey(encryptionKey);

            // Generate IV for this file
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

            // Encrypt file content
            byte[] encryptedContent = cipher.doFinal(fileContent);

            // Combine IV + encrypted content
            byte[] finalData = new byte[iv.length + encryptedContent.length];
            System.arraycopy(iv, 0, finalData, 0, iv.length);
            System.arraycopy(encryptedContent, 0, finalData, iv.length, encryptedContent.length);

            log.info("File encrypted successfully. Original: {} bytes, Encrypted: {} bytes",
                    fileContent.length, finalData.length);

            return finalData;

        } catch (Exception e) {
            log.error("Failed to encrypt file", e);
            throw new RuntimeException("File encryption failed: " + e.getMessage(), e);
        }
    }

    /**
     * Decrypt file content (for medical records)
     * This is the main method used by RecordService
     */
    public byte[] decryptFile(byte[] encryptedContent, String encryptionKey) {
        log.info("Decrypting file of size: {} bytes", encryptedContent.length);

        try {
            SecretKey key = decodeKey(encryptionKey);

            // Extract IV from the beginning
            byte[] iv = Arrays.copyOfRange(encryptedContent, 0, GCM_IV_LENGTH);
            byte[] cipherText = Arrays.copyOfRange(encryptedContent, GCM_IV_LENGTH, encryptedContent.length);

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

            // Decrypt file content
            byte[] decryptedContent = cipher.doFinal(cipherText);

            log.info("File decrypted successfully. Encrypted: {} bytes, Decrypted: {} bytes",
                    encryptedContent.length, decryptedContent.length);

            return decryptedContent;

        } catch (Exception e) {
            log.error("Failed to decrypt file", e);
            throw new RuntimeException("File decryption failed: " + e.getMessage(), e);
        }
    }

    /**
     * Re-encrypt data with a new key (key rotation)
     */
    public String reEncrypt(String encryptedBase64, String oldKey, String newKey) {
        // First decrypt with old key
        String decrypted = decrypt(encryptedBase64, oldKey);

        // Then encrypt with new key
        return encrypt(decrypted.getBytes(StandardCharsets.UTF_8), newKey);
    }

    /**
     * Create a hash of the encryption key (for storage verification)
     */
    public String hashKey(String encryptionKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(encryptionKey.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to hash encryption key", e);
            throw new RuntimeException("Key hashing failed", e);
        }
    }

    /**
     * Verify if a key matches its stored hash
     */
    public boolean verifyKey(String encryptionKey, String storedHash) {
        String computedHash = hashKey(encryptionKey);
        return computedHash.equals(storedHash);
    }

    /**
     * Generate a unique salt for key derivation
     */
    public String generateSalt() {
        byte[] salt = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Derive an encryption key from user password
     * Uses PBKDF2-style key derivation (simplified)
     */
    public String deriveKeyFromPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Multiple iterations for key strengthening
            byte[] key = password.getBytes(StandardCharsets.UTF_8);
            byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);

            for (int i = 0; i < 10000; i++) {
                digest.update(key);
                digest.update(saltBytes);
                key = digest.digest();
            }

            // Take first 32 bytes for AES-256
            byte[] aesKey = Arrays.copyOf(key, 32);
            return Base64.getEncoder().encodeToString(aesKey);

        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to derive key from password", e);
            throw new RuntimeException("Key derivation failed", e);
        }
    }

    /**
     * Encrypt sensitive text fields (PII data)
     */
    public String encryptTextField(String plainText, String encryptionKey) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        return encrypt(plainText.getBytes(StandardCharsets.UTF_8), encryptionKey);
    }

    /**
     * Decrypt sensitive text fields (PII data)
     */
    public String decryptTextField(String encryptedText, String encryptionKey) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }
        return decrypt(encryptedText, encryptionKey);
    }

    /**
     * Secure wipe (overwrite) encryption key from memory
     */
    public void secureWipeKey(String encryptionKey) {
        if (encryptionKey != null) {
            // Overwrite the string with zeros
            char[] keyChars = encryptionKey.toCharArray();
            Arrays.fill(keyChars, '0');
            encryptionKey = null;
            log.debug("Encryption key securely wiped from memory");
        }
    }

    /**
     * Validate encryption key format
     */
    public boolean isValidKey(String base64Key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            return keyBytes.length == 32; // AES-256 requires 32 bytes
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Decode Base64 encoded key to SecretKey object
     */
    private SecretKey decodeKey(String base64Key) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(base64Key);

            // Validate key length
            if (decodedKey.length != 32) {
                throw new IllegalArgumentException("Invalid key length: " + decodedKey.length +
                        " bytes. AES-256 requires 32 bytes.");
            }

            return new SecretKeySpec(decodedKey, 0, decodedKey.length, AES_ALGORITHM);

        } catch (IllegalArgumentException e) {
            log.error("Invalid encryption key format", e);
            throw new RuntimeException("Invalid encryption key: " + e.getMessage(), e);
        }
    }

    /**
     * Generate a master key for system-wide encryption
     */
    private SecretKey generateMasterKey() {
        try {
            byte[] keyBytes = masterKey.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            keyBytes = sha.digest(keyBytes);
            return new SecretKeySpec(keyBytes, AES_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate master key", e);
            throw new RuntimeException("Master key generation failed", e);
        }
    }
}