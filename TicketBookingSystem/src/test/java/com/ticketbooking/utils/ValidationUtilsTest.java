package com.ticketbooking.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

/**
 * Unit tests for ValidationUtils class
 */
public class ValidationUtilsTest {
    
    @Test
    public void testIsValidEmail() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"), "Valid email should pass validation");
        assertTrue(ValidationUtils.isValidEmail("test.name@example.co.uk"), "Valid email with dots should pass validation");
        assertTrue(ValidationUtils.isValidEmail("test+name@example.com"), "Valid email with plus sign should pass validation");
        
        assertFalse(ValidationUtils.isValidEmail(""), "Empty email should fail validation");
        assertFalse(ValidationUtils.isValidEmail(null), "Null email should fail validation");
        assertFalse(ValidationUtils.isValidEmail("test"), "Email without @ should fail validation");
        assertFalse(ValidationUtils.isValidEmail("test@"), "Email without domain should fail validation");
        assertFalse(ValidationUtils.isValidEmail("@example.com"), "Email without local part should fail validation");
    }
    
    @Test
    public void testIsValidPhone() {
        assertTrue(ValidationUtils.isValidPhone("1234567890"), "Valid 10-digit phone should pass validation");
        assertTrue(ValidationUtils.isValidPhone("+1234567890"), "Valid phone with plus sign should pass validation");
        assertTrue(ValidationUtils.isValidPhone("123456789012345"), "Valid 15-digit phone should pass validation");
        
        assertFalse(ValidationUtils.isValidPhone(""), "Empty phone should fail validation");
        assertFalse(ValidationUtils.isValidPhone(null), "Null phone should fail validation");
        assertFalse(ValidationUtils.isValidPhone("123456789"), "Phone with less than 10 digits should fail validation");
        assertFalse(ValidationUtils.isValidPhone("1234567890123456"), "Phone with more than 15 digits should fail validation");
        assertFalse(ValidationUtils.isValidPhone("123-456-7890"), "Phone with hyphens should fail validation");
        assertFalse(ValidationUtils.isValidPhone("(123) 456-7890"), "Phone with parentheses should fail validation");
    }
    
    @Test
    public void testIsValidUsername() {
        assertTrue(ValidationUtils.isValidUsername("user123"), "Valid username should pass validation");
        assertTrue(ValidationUtils.isValidUsername("User_123"), "Username with underscore should pass validation");
        
        assertFalse(ValidationUtils.isValidUsername(""), "Empty username should fail validation");
        assertFalse(ValidationUtils.isValidUsername(null), "Null username should fail validation");
        assertFalse(ValidationUtils.isValidUsername("ab"), "Username with less than 3 characters should fail validation");
        assertFalse(ValidationUtils.isValidUsername("abcdefghijklmnopqrstu"), "Username with more than 20 characters should fail validation");
        assertFalse(ValidationUtils.isValidUsername("user@123"), "Username with special characters should fail validation");
    }
    
    @Test
    public void testIsValidPassword() {
        assertTrue(ValidationUtils.isValidPassword("password"), "Valid password should pass validation");
        assertTrue(ValidationUtils.isValidPassword("pass123"), "Valid password with numbers should pass validation");
        assertTrue(ValidationUtils.isValidPassword("Pass@123"), "Valid password with special characters should pass validation");
        
        assertFalse(ValidationUtils.isValidPassword(""), "Empty password should fail validation");
        assertFalse(ValidationUtils.isValidPassword(null), "Null password should fail validation");
        assertFalse(ValidationUtils.isValidPassword("pass"), "Password with less than 6 characters should fail validation");
    }
    
    @Test
    public void testIsValidEventDate() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);
        
        assertTrue(ValidationUtils.isValidEventDate(today), "Today's date should pass validation");
        assertTrue(ValidationUtils.isValidEventDate(tomorrow), "Future date should pass validation");
        
        assertFalse(ValidationUtils.isValidEventDate(null), "Null date should fail validation");
        assertFalse(ValidationUtils.isValidEventDate(yesterday), "Past date should fail validation");
    }
    
    @Test
    public void testIsValidQuantity() {
        assertTrue(ValidationUtils.isValidQuantity(1), "Quantity of 1 should pass validation");
        assertTrue(ValidationUtils.isValidQuantity(100), "Large quantity should pass validation");
        
        assertFalse(ValidationUtils.isValidQuantity(0), "Zero quantity should fail validation");
        assertFalse(ValidationUtils.isValidQuantity(-1), "Negative quantity should fail validation");
    }
    
    @Test
    public void testIsValidSeatType() {
        assertTrue(ValidationUtils.isValidSeatType("Standard"), "Standard seat type should pass validation");
        assertTrue(ValidationUtils.isValidSeatType("VIP"), "VIP seat type should pass validation");
        assertTrue(ValidationUtils.isValidSeatType("Premium"), "Premium seat type should pass validation");
        
        assertFalse(ValidationUtils.isValidSeatType(""), "Empty seat type should fail validation");
        assertFalse(ValidationUtils.isValidSeatType(null), "Null seat type should fail validation");
        assertFalse(ValidationUtils.isValidSeatType("Regular"), "Invalid seat type should fail validation");
    }
    
    @Test
    public void testIsValidRole() {
        assertTrue(ValidationUtils.isValidRole("user"), "User role should pass validation");
        assertTrue(ValidationUtils.isValidRole("admin"), "Admin role should pass validation");
        
        assertFalse(ValidationUtils.isValidRole(""), "Empty role should fail validation");
        assertFalse(ValidationUtils.isValidRole(null), "Null role should fail validation");
        assertFalse(ValidationUtils.isValidRole("guest"), "Invalid role should fail validation");
    }
}
