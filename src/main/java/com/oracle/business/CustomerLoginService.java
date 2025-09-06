package com.oracle.business;

import com.oracle.beans.CustomerLogin;
import com.oracle.business.impl.CustomerLoginServiceImpl.AuthResult;
import com.oracle.business.impl.CustomerLoginServiceImpl.RegistrationForm;

import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomerLoginService {

    // Create and update CustomerLogin records
    boolean saveCustomerLogin(CustomerLogin customerLogin);
    boolean updateCustomerLogin(CustomerLogin customerLogin);

    // Lookup login records
    CustomerLogin getByCustomerId(String customerId);
    CustomerLogin getByUserId(String userId);
    List<CustomerLogin> getLockedAccounts();
    

    // Authentication and security
    /**
     * Authenticates a customer login by userId and plaintext password.
     * Returns true if login is successful, false otherwise.
     * Implements failed login attempts tracking and account lockout.
     */
    AuthResult authenticate(String userId, String password);

    // Manage failed login attempts and locking
    boolean incrementFailedLoginAttempts(String customerId);
    boolean resetFailedLoginAttempts(String customerId);
    boolean lockAccount(String customerId, LocalDateTime lockedUntil);
    boolean unlockAccount(String customerId);

    // Update last login timestamp
    boolean updateLastLogin(String customerId, LocalDateTime lastLogin);

    // Password changes
    boolean changeLoginPassword(String customerId, String oldPassword, String newPassword);
    boolean changeTransactionPassword(String customerId, String oldPassword, String newPassword);
    boolean updateLoginPassword(String customerId, String newPassword);
    boolean updateTransactionPassword(String customerId, String newPassword);


    // Internet banking enable/disable
    boolean isInternetBankingEnabled(String customerId);
    boolean enableInternetBanking(String customerId);
    boolean disableInternetBanking(String customerId);
   Response registerForInternetBanking(RegistrationForm form);
boolean verifyOtpForCustomer(String customerId, String otp);
String getCustomerEmail(String customerId);
}
