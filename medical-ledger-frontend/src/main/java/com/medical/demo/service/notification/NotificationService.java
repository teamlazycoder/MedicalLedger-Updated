package com.medical.demo.service.notification;

public interface NotificationService {
    void sendConsentNotification(String patientEmail, String doctorName, String action);
    void sendRecordAccessNotification(String patientEmail, String doctorName, String recordType);
    void sendConsentExpiryNotification(String patientEmail, String doctorName);
    void sendPasswordResetEmail(String email, String resetToken);
    void sendWelcomeEmail(String email, String username);
}
