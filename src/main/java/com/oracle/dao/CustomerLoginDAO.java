package com.oracle.dao;

import com.oracle.beans.CustomerLogin;
import java.time.LocalDateTime;
import java.util.List;

public interface CustomerLoginDAO {

    // Basic CRUD
    boolean save(CustomerLogin customerLogin);
    boolean update(CustomerLogin customerLogin);
    CustomerLogin findByCustomerId(String customerId);
    CustomerLogin findByUserId(String userId);
    List<CustomerLogin> findAllLockedAccounts();


    // Security / Login management
    boolean updateFailedLoginAttempts(String customerId, int attempts);
    boolean incrementFailedLoginAttempts(String customerId);
    boolean resetFailedLoginAttempts(String customerId);

    boolean lockAccount(String customerId, LocalDateTime lockedUntil);
    boolean unlockAccount(String customerId);

    boolean updateLastLogin(String customerId, LocalDateTime lastLogin);

    // Password management
    boolean updateLoginPassword(String customerId, String hashedPassword);
    boolean updateTransactionPassword(String customerId, String hashedPassword);

    // Internet Banking status
    boolean isInternetBankingEnabled(String customerId);
    boolean enableInternetBanking(String customerId);
    boolean disableInternetBanking(String customerId);
}
