package com.medical.demo.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService implements NotificationService {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendConsentNotification(String patientEmail, String doctorName, String action) {
        String subject = "Consent " + action + " Notification";
        String message = String.format(
                "Your medical record consent has been %s for Dr. %s.\n\n" +
                        "This is an automated notification from your Medical History Ledger.\n" +
                        "Please login to your account for more details.",
                action.toLowerCase(), doctorName
        );

        sendEmail(patientEmail, subject, message);
    }

    @Override
    @Async
    public void sendRecordAccessNotification(String patientEmail, String doctorName, String recordType) {
        String subject = "Medical Record Accessed";
        String message = String.format(
                "Dr. %s has accessed your %s medical record.\n\n" +
                        "This access has been logged on the blockchain for your security.\n" +
                        "Please login to view the complete audit trail.",
                doctorName, recordType
        );

        sendEmail(patientEmail, subject, message);
    }

    @Override
    @Async
    public void sendConsentExpiryNotification(String patientEmail, String doctorName) {
        String subject = "Consent Expiring Soon";
        String message = String.format(
                "Your consent for Dr. %s is about to expire.\n\n" +
                        "Please login to renew the consent if you wish to continue sharing your records.",
                doctorName
        );

        sendEmail(patientEmail, subject, message);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String email, String resetToken) {
        String subject = "Password Reset Request";
        String message = String.format(
                "You have requested to reset your password.\n\n" +
                        "Your reset token is: %s\n\n" +
                        "Please use this token to reset your password.\n" +
                        "If you did not request this, please ignore this email.",
                resetToken
        );

        sendEmail(email, subject, message);
    }

    @Override
    @Async
    public void sendWelcomeEmail(String email, String username) {
        String subject = "Welcome to Medical History Ledger";
        String message = String.format(
                "Welcome %s!\n\n" +
                        "Your account has been successfully created on the Decentralized Medical History Ledger.\n" +
                        "You now have full control over your medical records.\n\n" +
                        "Features:\n" +
                        "- Upload and manage your medical records\n" +
                        "- Grant and revoke access to doctors\n" +
                        "- Track all access to your records via blockchain\n" +
                        "- Secure encryption of your medical data\n\n" +
                        "Thank you for choosing our platform!",
                username
        );

        sendEmail(email, subject, message);
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);

            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }
}
