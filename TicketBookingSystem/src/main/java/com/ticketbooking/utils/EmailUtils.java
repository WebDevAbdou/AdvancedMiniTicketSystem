package com.ticketbooking.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

/**
 * Utility class for email operations
 */
public class EmailUtils {
    private static final Logger logger = LogManager.getLogger(EmailUtils.class);
    private static Properties emailProperties;

    static {
        emailProperties = new Properties();
        try (InputStream input = EmailUtils.class.getClassLoader().getResourceAsStream("email.properties")) {
            if (input != null) {
                emailProperties.load(input);
                logger.info("Email properties loaded successfully");
            } else {
                logger.warn("Unable to find email.properties, using default settings");
                // Set default properties
                emailProperties.setProperty("mail.smtp.host", "smtp.example.com");
                emailProperties.setProperty("mail.smtp.port", "587");
                emailProperties.setProperty("mail.smtp.auth", "true");
                emailProperties.setProperty("mail.smtp.starttls.enable", "true");
                emailProperties.setProperty("mail.from", "noreply@ticketbooking.com");
                emailProperties.setProperty("mail.username", "username");
                emailProperties.setProperty("mail.password", "password");
            }
        } catch (IOException e) {
            logger.error("Error loading email properties", e);
        }
    }

    /**
     * Generate a verification token
     * @return Random UUID as verification token
     */
    public static String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Send verification email to user
     * @param email Recipient email
     * @param username Username
     * @param token Verification token
     * @return true if email sent successfully, false otherwise
     */
    public static boolean sendVerificationEmail(String email, String username, String token) {
        String subject = "Verify Your Ticket Booking System Account";
        String verificationUrl = "http://localhost:8080/verify?token=" + token;

        String content = "Dear " + username + ",<br><br>"
                + "Thank you for registering with the Ticket Booking System. "
                + "Please click the link below to verify your email address:<br><br>"
                + "<a href=\"" + verificationUrl + "\">Verify Email</a><br><br>"
                + "If you did not create an account, please ignore this email.<br><br>"
                + "Best regards,<br>"
                + "The Ticket Booking System Team";

        return sendEmail(email, subject, content);
    }

    /**
     * Send booking confirmation email
     * @param email Recipient email
     * @param customerName Customer name
     * @param eventName Event name
     * @param bookingId Booking ID
     * @return true if email sent successfully, false otherwise
     */
    public static boolean sendBookingConfirmationEmail(String email, String customerName,
                                                      String eventName, int bookingId) {
        String subject = "Booking Confirmation - Ticket Booking System";

        String content = "Dear " + customerName + ",<br><br>"
                + "Thank you for your booking. Your booking has been confirmed.<br><br>"
                + "Booking Details:<br>"
                + "Booking ID: " + bookingId + "<br>"
                + "Event: " + eventName + "<br><br>"
                + "You can view your booking details by logging into your account.<br><br>"
                + "Best regards,<br>"
                + "The Ticket Booking System Team";

        return sendEmail(email, subject, content);
    }

    /**
     * Send email
     * @param to Recipient email
     * @param subject Email subject
     * @param content Email content (HTML)
     * @return true if email sent successfully, false otherwise
     */
    private static boolean sendEmail(String to, String subject, String content) {
        // For development/testing, just log the email and return success
        logger.info("Email would be sent to: {}", to);
        logger.info("Subject: {}", subject);
        logger.info("Content: {}", content);

        // In a production system, this would use JavaMail API to send actual emails
        // For now, we'll just simulate successful email sending

        return true;
    }
}
