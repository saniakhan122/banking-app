// com.oracle.dao.BankAccountDAO.java
package com.oracle.dao;

import com.oracle.beans.BankAccount;
import java.math.BigDecimal;
import java.util.List;

public interface BankAccountDAO {
    
    // Create operations
    boolean createAccount(BankAccount account);
    
    // Read operations
    BankAccount findAccountByNumber(String accountNumber);
    List<BankAccount> findAccountsByCustomerId(String customerId);
//    List<BankAccount> findAccountsByUserId(String userId);

    List<BankAccount> findAllAccounts();
    List<BankAccount> findAccountsByType(String accountType);
    List<BankAccount> findActiveAccounts();
    List<BankAccount> findInactiveAccounts();
    BankAccount findAccountByAccountNumber(String accountNumber);

    
    // Update operations
    boolean updateAccount(BankAccount account);
    boolean updateAccountBalance(String accountNumber, BigDecimal newBalance);
    boolean debitAccount(String accountNumber, BigDecimal amount);
    boolean creditAccount(String accountNumber, BigDecimal amount);
    boolean activateAccount(String accountNumber);
    boolean deactivateAccount(String accountNumber);
    
    // Delete operations
    boolean deleteAccount(String accountNumber);
    
    // Utility operations
    boolean accountExists(String accountNumber);
    BigDecimal getAccountBalance(String accountNumber);
    boolean hasInsufficientBalance(String accountNumber, BigDecimal amount);
    boolean isAccountActive(String accountNumber);
    long getTotalAccountsCount();
    BigDecimal getTotalBankBalance();
    List<BankAccount> findAccountsWithBalanceGreaterThan(BigDecimal amount);
    List<BankAccount> findAccountsWithBalanceLessThan(BigDecimal amount);
}