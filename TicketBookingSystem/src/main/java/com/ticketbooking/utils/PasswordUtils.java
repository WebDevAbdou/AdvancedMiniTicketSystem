package com.ticketbooking.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing and verification
 */
public class PasswordUtils {
    private static final Logger logger = LogManager.getLogger(PasswordUtils.class);
    
    // Salt length
    private static final int SALT_LENGTH = 16;
    
    // Number of iterations for hashing
    private static final int ITERATIONS = 10000;
    
    /**
     * Hash a password using PBKDF2 with SHA-256
     * @param password Password to hash
     * @return Hashed password with salt in format: iterations:salt:hash
     */
    public static String hashPassword(String password) {
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash the password
            byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS);
            
            // Format: iterations:salt:hash
            return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":" + 
                   Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error hashing password", e);
            return null;
        }
    }
    
    /**
     * Verify a password against a stored hash
     * @param password Password to verify
     * @param storedHash Stored hash to verify against
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Split the stored hash into parts
            String[] parts = storedHash.split(":");
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] hash = Base64.getDecoder().decode(parts[2]);
            
            // Hash the input password with the same salt and iterations
            byte[] testHash = pbkdf2(password.toCharArray(), salt, iterations);
            
            // Compare the hashes
            int diff = hash.length ^ testHash.length;
            for (int i = 0; i < hash.length && i < testHash.length; i++) {
                diff |= hash[i] ^ testHash[i];
            }
            
            return diff == 0;
        } catch (Exception e) {
            logger.error("Error verifying password", e);
            return false;
        }
    }
    
    /**
     * Implementation of PBKDF2 with SHA-256
     * @param password Password to hash
     * @param salt Salt to use
     * @param iterations Number of iterations
     * @return Hashed password
     * @throws NoSuchAlgorithmException if algorithm is not available
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        
        // Convert password to bytes
        byte[] passwordBytes = new byte[password.length * 2];
        for (int i = 0; i < password.length; i++) {
            passwordBytes[i * 2] = (byte) (password[i] >> 8);
            passwordBytes[i * 2 + 1] = (byte) password[i];
        }
        
        // Initial hash with salt
        digest.update(salt);
        byte[] result = digest.digest(passwordBytes);
        
        // Additional iterations
        for (int i = 1; i < iterations; i++) {
            digest.reset();
            result = digest.digest(result);
        }
        
        return result;
    }
}
