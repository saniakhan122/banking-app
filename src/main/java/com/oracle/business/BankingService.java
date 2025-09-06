// com.oracle.business.BankingService.java
package com.oracle.business;

import com.oracle.beans.BankAccount;
import com.oracle.beans.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BankingService {
    
    // Account Management
    String createBankAccount(String customerId, String accountType, BigDecimal initialDeposit);
    BankAccount getAccountDetails(String accountNumber);
    List<BankAccount> getCustomerAccounts(String customerId);
    boolean activateAccount(String accountNumber);
    boolean deactivateAccount(String accountNumber);
    BigDecimal getAccountBalance(String accountNumber);
    
    // Fund Transfer Operations
    String transferFunds(String fromAccount, String toAccount, BigDecimal amount, 
                        String transferMethod, String remarks);
    boolean validateTransfer(String fromAccount, String toAccount, BigDecimal amount);
    boolean processNEFTTransfer(String fromAccount, String toAccount, BigDecimal amount, String remarks);
    boolean processRTGSTransfer(String fromAccount, String toAccount, BigDecimal amount, String remarks);
    boolean processIMPSTransfer(String fromAccount, String toAccount, BigDecimal amount, String remarks);
    boolean doesAccountBelongToCustomer(String accountNumber, String customerId);
    
    // Transaction Operations
    boolean creditAccount(String accountNumber, BigDecimal amount, String description);
    boolean debitAccount(String accountNumber, BigDecimal amount, String description);
    List<Transaction> getAccountStatement(String accountNumber, LocalDate fromDate, LocalDate toDate);
    List<Transaction> getRecentTransactions(String accountNumber, int limit);
    Transaction getTransactionDetails(String transactionId);
    
    // Account Validation
    boolean isAccountExists(String accountNumber);
    boolean isAccountActive(String accountNumber);
    boolean hasSufficientBalance(String accountNumber, BigDecimal amount);
    boolean isValidTransferAmount(BigDecimal amount, String transferMethod);
    
    // Transaction Validation
    boolean validateTransactionLimits(String accountNumber, BigDecimal amount, String transferMethod);
    boolean isValidTransferMethod(String transferMethod);
    BigDecimal getTransferLimit(String transferMethod);
    BigDecimal getDailyTransferLimit();
    
    // Account Utilities
    String generateAccountNumber();
    String generateTransactionId();
    String generateTransactionRefNumber();
    
    // Reports & Statistics
    List<BankAccount> getAllAccounts();
    List<Transaction> getAllTransactions();
    BigDecimal getTotalBankBalance();
    List<Transaction> getHighValueTransactions(BigDecimal threshold);
    List<Transaction> getTransactionsByCustomer(String customerId);
    
    // Balance Inquiry
    String getAccountBalanceInquiry(String accountNumber);
    boolean checkMinimumBalance(String accountNumber);
//	List<BankAccount> getCustomerAccountsByUserId(String userId);
}