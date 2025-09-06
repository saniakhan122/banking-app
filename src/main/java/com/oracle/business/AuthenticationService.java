
// com.oracle.business.AuthenticationService.java
package com.oracle.business;

import com.oracle.beans.*;

/**
 * Authentication Service Interface
 */
public interface AuthenticationService {
    
    // Customer Authentication
    String authenticateCustomer(String username, String password);
    String authenticateCustomerTransaction(String customerId, String transactionPassword);
    
    // Admin Authentication
    String authenticateAdmin(String username, String password);
    
    // Session Management
    Session createSession(String userId, String userType, String ipAddress, String userAgent);
    boolean validateSession(String sessionId);
    void invalidateSession(String sessionId);
    void updateSessionActivity(String sessionId);
    
    // Account Management
    boolean lockAccount(String userId, String userType);
    boolean unlockAccount(String userId, String userType);
    boolean isAccountLocked(String userId, String userType);
    
    // Password Management
    boolean changePassword(String userId, String userType, String oldPassword, String newPassword);
    boolean changeTransactionPassword(String customerId, String oldPassword, String newPassword);
    boolean resetPassword(String userId, String userType, String newPassword);
    
    // Password Recovery
    String generateOTP(String accountNumber, String mobileNumber, String purpose);
    boolean verifyOTP(String otpId, String otpCode);
    String recoverCustomerId(String accountNumber, String otpCode);
    boolean resetPasswordWithOTP(String userId, String otpCode, String newPassword);
    
    // Registration
    String registerCustomerForInternetBanking(String accountNumber, String loginPassword, 
                                              String transactionPassword, String confirmPassword);
    
    // Validation
    boolean validatePasswordStrength(String password);
    String getPasswordValidationRules();
}
