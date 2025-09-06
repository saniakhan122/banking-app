package com.oracle.dao;

import com.oracle.beans.Transaction;
import com.oracle.dao.impl.TransactionJPAImpl.TransactionWithType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionDAO {

    // Basic CRUD operations
    Transaction save(Transaction transaction);
    Transaction findById(String transactionId);
    Transaction findByTransactionRefNo(String transactionRefNo);
    List<Transaction> findByAccountNumber(String accountNumber);
    List<Transaction> findTransactionsByDateRange(String accountNumber, LocalDate fromDate, LocalDate toDate);
    List<Transaction> findAll();
    Transaction update(Transaction transaction);
    void delete(String transactionId);

    // The methods you stubbed in impl (more detailed queries)
    boolean createTransaction(Transaction transaction);
    Transaction findTransactionById(String transactionId);
    Transaction findTransactionByRefNo(String transactionRefNo);
    List<Transaction> findAllTransactions();
    List<Transaction> findTransactionsByFromAccount(String accountNumber);
    List<Transaction> findTransactionsByToAccount(String accountNumber);
    List<Transaction> findTransactionsByAccount(String accountNumber);
    List<Transaction> findTransactionsByType(String transactionType);
    List<Transaction> findTransactionsByStatus(String status);
    List<Transaction> findTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<Transaction> findTransactionsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount);
    List<Transaction> findTransactionsByTransferMethod(String transferMethod);
    boolean updateTransaction(Transaction transaction);
    boolean updateTransactionStatus(String transactionId, String status);
    boolean updateTransactionRemarks(String transactionId, String remarks);
    boolean deleteTransaction(String transactionId);
    boolean transactionExists(String transactionId);
    boolean transactionRefExists(String transactionRefNo);
    long getTotalTransactionCount();
    BigDecimal getTotalTransactionAmount();
    BigDecimal getTotalTransactionAmountByAccount(String accountNumber);
    BigDecimal getTotalDebitAmountByAccount(String accountNumber);
    BigDecimal getTotalCreditAmountByAccount(String accountNumber);
    List<Transaction> getAccountStatement(String accountNumber, LocalDate fromDate, LocalDate toDate);
    List<Transaction> getRecentTransactions(String accountNumber, int limit);
    List<Transaction> findHighValueTransactions(BigDecimal threshold);
    List<Transaction> findTransactionsByCustomer(String customerId);
	List<Transaction> findDebitTransactionsByDateRange(String accountNumber, LocalDate fromDate, LocalDate toDate);
	List<Transaction> findCreditTransactionsByDateRange(String accountNumber, LocalDate fromDate, LocalDate toDate);
}
