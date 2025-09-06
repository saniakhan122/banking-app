// com.oracle.business.util.PasswordUtils.java
package com.oracle.business.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Password utility class for hashing and verification
 */
public class PasswordUtils {
    
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    /**
     * Hash password with salt
     */
    public static String hashPassword(String password) {
        try {
            // Generate salt
            byte[] salt = generateSalt();
            
            // Hash password with salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Combine salt and hash
            byte[] saltedHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltedHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltedHash, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(saltedHash);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verify password against hash
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        try {
            // Decode the stored hash
            byte[] saltedHash = Base64.getDecoder().decode(hashedPassword);
            
            // Extract salt (first 16 bytes)
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(saltedHash, 0, salt, 0, SALT_LENGTH);
            
            // Hash the provided password with the extracted salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPasswordBytes = md.digest(password.getBytes());
            
            // Extract the stored hash (remaining bytes)
            byte[] storedHash = new byte[saltedHash.length - SALT_LENGTH];
            System.arraycopy(saltedHash, SALT_LENGTH, storedHash, 0, storedHash.length);
            
            // Compare hashes
            return MessageDigest.isEqual(hashedPasswordBytes, storedHash);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Generate random salt
     */
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }
    
    /**
     * Generate random password for initial setup
     */
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one character from each required category
        password.append(getRandomChar("ABCDEFGHIJKLMNOPQRSTUVWXYZ", random)); // Uppercase
        password.append(getRandomChar("abcdefghijklmnopqrstuvwxyz", random)); // Lowercase
        password.append(getRandomChar("0123456789", random)); // Digit
        password.append(getRandomChar("!@#$%^&*", random)); // Special char
        
        // Fill remaining length
        for (int i = 4; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        // Shuffle the password
        return shuffleString(password.toString(), random);
    }
    
    private static char getRandomChar(String chars, SecureRandom random) {
        return chars.charAt(random.nextInt(chars.length()));
    }
    
    private static String shuffleString(String string, SecureRandom random) {
        char[] chars = string.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}