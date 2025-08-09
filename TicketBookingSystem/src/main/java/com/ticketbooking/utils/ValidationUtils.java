package com.ticketbooking.utils;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtils {
    
    // Regular expression for email validation
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    // Regular expression for phone number validation
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\+?[0-9]{10,15}$");
    
    // Regular expression for username validation
    private static final Pattern USERNAME_PATTERN = 
        Pattern.compile("^[A-Za-z0-9_]{3,20}$");
    
    /**
     * Validate email format
     * @param email email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate phone number format
     * @param phone phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Validate username format
     * @param username username to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * Validate password
     * @param password password to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    /**
     * Validate event date (must be in the future)
     * @param date date to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEventDate(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.now());
    }
    
    /**
     * Validate quantity (must be positive)
     * @param quantity quantity to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidQuantity(int quantity) {
        return quantity > 0;
    }
    
    /**
     * Validate seat type
     * @param seatType seat type to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidSeatType(String seatType) {
        if (seatType == null || seatType.trim().isEmpty()) {
            return false;
        }
        
        return seatType.equals("Standard") || 
               seatType.equals("VIP") || 
               seatType.equals("Premium");
    }
    
    /**
     * Validate user role
     * @param role role to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return false;
        }
        
        return role.equals("user") || role.equals("admin");
    }
}
