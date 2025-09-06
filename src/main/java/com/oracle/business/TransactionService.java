package com.oracle.business;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.oracle.beans.Transaction;

public interface TransactionService {
    
    // Core transaction operations
    boolean processTransaction(Transaction transaction);
    boolean transferFunds(String fromAccount, String toAccount, BigDecimal amount, String remarks);
    
    // Transaction retrieval operations
    Transaction getTransactionById(String transactionId);
    Transaction getTransactionByRefNo(String transactionRefNo);
    List<Transaction> getAllTransactions();
    List<Transaction> getTransactionsByAccount(String accountNumber);
    List<Transaction> getTransactionsByToAccount(String accountNumber);
    List<Transaction> getTransactionsByType(String transactionType);
    List<Transaction> getTransactionsByStatus(String status);
    List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<Transaction> getTransactionsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount);
    List<Transaction> getTransactionsByTransferMethod(String transferMethod);
    List<Transaction> getTransactionsByCustomer(String customerId);
    
    // Transaction modification operations
    boolean updateTransaction(Transaction transaction);
    boolean updateTransactionStatus(String transactionId, String status);
    boolean addTransactionRemarks(String transactionId, String remarks);
    boolean cancelTransaction(String transactionId);
    boolean reverseTransaction(String transactionId, String reason);
    
    // Account statement and reporting operations
    List<Transaction> generateAccountStatement(String accountNumber, LocalDate fromDate, LocalDate toDate);
    List<Transaction> getRecentTransactions(String accountNumber, int limit);
    List<Transaction> getHighValueTransactions(BigDecimal threshold);
    List<Transaction> getPendingTransactions();
    List<Transaction> getFailedTransactions();
    
    // Balance and amount calculations
    BigDecimal getAccountBalance(String accountNumber);
    BigDecimal getTotalTransactionAmount(String accountNumber);
    BigDecimal getTotalDebitAmount(String accountNumber);
    BigDecimal getTotalCreditAmount(String accountNumber);
    BigDecimal calculateDailyTransactionTotal(String accountNumber, LocalDate date);
    BigDecimal calculateMonthlyTransactionTotal(String accountNumber, int month, int year);
    
    // Validation and verification operations
    boolean validateTransaction(Transaction transaction);
    boolean verifyTransactionLimits(String accountNumber, BigDecimal amount);
    boolean isTransactionValid(String transactionId);
    boolean hasTransactionExpired(String transactionId);
    
    // Business rule operations
    boolean checkDailyLimit(String accountNumber, BigDecimal amount);
    boolean checkMonthlyLimit(String accountNumber, BigDecimal amount);
    boolean isAccountEligibleForTransaction(String accountNumber);
    boolean requiresApproval(Transaction transaction);
    
    // Utility operations
    long getTotalTransactionCount();
    long getTransactionCountByStatus(String status);
    boolean transactionExists(String transactionId);
    boolean transactionRefExists(String transactionRefNo);
    String generateTransactionReference();
    
    // Reporting operations
    List<Transaction> getTransactionSummaryByDate(LocalDate date);
    List<Transaction> getTransactionSummaryByDateRange(LocalDate fromDate, LocalDate toDate);
    BigDecimal getTotalSystemTransactionAmount();
}