// com.oracle.business.util.ValidationUtils.java
package com.oracle.business.util;

import java.util.regex.Pattern;

/**
 * Validation utility class
 */
public class ValidationUtils {
    
    // Email pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    // Mobile number pattern (Indian format)
    private static final Pattern MOBILE_PATTERN = Pattern.compile(
        "^[6-9]\\d{9}$"
    );
    
    // Aadhar number pattern
    private static final Pattern AADHAR_PATTERN = Pattern.compile(
        "^[2-9]{1}[0-9]{11}$"
    );
    
    // Account number pattern
    private static final Pattern ACCOUNT_PATTERN = Pattern.compile(
        "^ACC\\d{10}$"
    );
    
    // Customer ID pattern
    private static final Pattern CUSTOMER_ID_PATTERN = Pattern.compile(
        "^CUST\\d{8}$"
    );
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate mobile number
     */
    public static boolean isValidMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile).matches();
    }
    
    /**
     * Validate Aadhar number
     */
    public static boolean isValidAadhar(String aadhar) {
        if (aadhar == null || !AADHAR_PATTERN.matcher(aadhar).matches()) {
            return false;
        }
        
        // Additional Aadhar validation using Verhoeff algorithm
        return verifyAadharChecksum(aadhar);
    }
    
    /**
     * Validate account number format
     */
    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && ACCOUNT_PATTERN.matcher(accountNumber).matches();
    }
    
    /**
     * Validate customer ID format
     */
    public static boolean isValidCustomerId(String customerId) {
        return customerId != null && CUSTOMER_ID_PATTERN.matcher(customerId).matches();
    }
    
    /**
     * Validate name (only alphabets and spaces)
     */
    public static boolean isValidName(String name) {
        return name != null && name.matches("^[a-zA-Z\\s]{2,50}$");
    }
    
    /**
     * Check if string is not empty
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Verify Aadhar checksum using Verhoeff algorithm
     */
    private static boolean verifyAadharChecksum(String aadhar) {
        // Verhoeff algorithm multiplication table
        int[][] d = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
            {1, 2, 3, 4, 0, 6, 7, 8, 9, 5},
            {2, 3, 4, 0, 1, 7, 8, 9, 5, 6},
            {3, 4, 0, 1, 2, 8, 9, 5, 6, 7},
            {4, 0, 1, 2, 3, 9, 5, 6, 7, 8},
            {5, 9, 8, 7, 6, 0, 4, 3, 2, 1},
            {6, 5, 9, 8, 7, 1, 0, 4, 3, 2},
            {7, 6, 5, 9, 8, 2, 1, 0, 4, 3},
            {8, 7, 6, 5, 9, 3, 2, 1, 0, 4},
            {9, 8, 7, 6, 5, 4, 3, 2, 1, 0}
        };
        
        // Permutation table
        int[][] p = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
            {1, 5, 7, 6, 2, 8, 3, 0, 9, 4},
            {5, 8, 0, 3, 7, 9, 6, 1, 4, 2},
            {8, 9, 1, 6, 0, 4, 3, 5, 2, 7},
            {9, 4, 5, 3, 1, 2, 6, 8, 7, 0},
            {4, 2, 8, 6, 5, 7, 3, 9, 0, 1},
            {2, 7, 9, 3, 8, 0, 6, 4, 1, 5},
            {7, 0, 4, 6, 9, 1, 3, 2, 5, 8}
        };
        
        int c = 0;
        int[] myArray = StringToReversedIntArray(aadhar);
        
        for (int i = 0; i < myArray.length; i++) {
            c = d[c][p[((i + 1) % 8)][myArray[i]]];
        }
        
        return c == 0;
    }
    
    /**
     * Helper method for Aadhar validation
     */
    private static int[] StringToReversedIntArray(String num) {
        int[] myArray = new int[num.length()];
        for (int i = 0; i < num.length(); i++) {
            myArray[i] = Integer.parseInt(num.substring(i, i + 1));
        }
        myArray = Reverse(myArray);
        return myArray;
    }
    
    /**
     * Helper method to reverse array
     */
    private static int[] Reverse(int[] myArray) {
        int[] reversed = new int[myArray.length];
        for (int i = 0; i < myArray.length; i++) {
            reversed[i] = myArray[myArray.length - (i + 1)];
        }
        return reversed;
    }
}