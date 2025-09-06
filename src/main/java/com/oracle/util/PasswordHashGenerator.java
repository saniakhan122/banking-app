package com.oracle.util;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        String plainPassword = "admin123"; // your desired password
        String hashedPassword = com.oracle.util.PasswordUtil.hashPassword(plainPassword);
        System.out.println("Hashed password: " + hashedPassword);
    }
}
