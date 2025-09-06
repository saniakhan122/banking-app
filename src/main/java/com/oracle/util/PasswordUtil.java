package com.oracle.util;




import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    // Hash a password using SHA-256 and return the hex string
    public static String hashPassword(String password) {
        if (password == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            // Convert byte array into hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Optionally, add a method to verify password matches the hashed password
    public static boolean verifyPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) return false;
        return hashPassword(password).equals(hashedPassword);
    }
}
