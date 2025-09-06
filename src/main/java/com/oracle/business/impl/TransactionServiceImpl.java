package com.oracle.business.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.oracle.beans.BankAccount;
import com.oracle.beans.Transaction;
import com.oracle.dao.TransactionDAO;
import com.oracle.business.TransactionService;

public class TransactionServiceImpl implements TransactionService {
    
    private TransactionDAO transactionDAO;
    
    // Business rule constants
    private static final BigDecimal DAILY_TRANSACTION_LIMIT = new BigDecimal("100000");
    private static final BigDecimal MONTHLY_TRANSACTION_LIMIT = new BigDecimal("500000");
    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("50000");
    private static final BigDecimal APPROVAL_THRESHOLD = new BigDecimal("25000");
    
    // Constructor
    public TransactionServiceImpl(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }
    
    // Core transaction operations
    @Override
    public boolean processTransaction(Transaction transaction) {
        try {
            // Validate transaction
            if (!validateTransaction(transaction)) {
                return false;
            }
            
            // Check business rules
            if (!verifyTransactionLimits(transaction.getFromAccountNumber(), transaction.getAmount())) {
                return false;
            }

            
            // Generate transaction reference if not present
            if (transaction.getTransactionRefNo() == null || transaction.getTransactionRefNo().isEmpty()) {
                transaction.setTransactionRefNo(generateTransactionReference());
            }
            
            // Set transaction timestamp
            transaction.setTransactionDate(LocalDateTime.now());
            
            // Process the transaction
            return transactionDAO.createTransaction(transaction);
            
        } catch (Exception e) {
            System.err.println("Error processing transaction: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean transferFunds(String fromAccount, String toAccount, BigDecimal amount, String remarks) {
        try {
            Transaction transaction = new Transaction();
            transaction.setFromAccountNumber(fromAccount);
            transaction.setToAccountNumber(toAccount);
            transaction.setAmount(amount);
            transaction.setRemarks(remarks);
            transaction.setTransactionType("TRANSFER");
            transaction.setStatus("PENDING");
            transaction.setTransferMethod("ONLINE");
            
            return processTransaction(transaction);
            
        } catch (Exception e) {
            System.err.println("Error in fund transfer: " + e.getMessage());
            return false;
        }
    }
    
    // Transaction retrieval operations
    @Override
    public Transaction getTransactionById(String transactionId) {
        return transactionDAO.findTransactionById(transactionId);
    }
    
    @Override
    public Transaction getTransactionByRefNo(String transactionRefNo) {
        return transactionDAO.findTransactionByRefNo(transactionRefNo);
    }
    
    @Override
    public List<Transaction> getAllTransactions() {
        return transactionDAO.findAllTransactions();
    }
    
    @Override
    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        return transactionDAO.findTransactionsByAccount(accountNumber);
    }
    
    @Override
    public List<Transaction> getTransactionsByToAccount(String accountNumber) {
        return transactionDAO.findTransactionsByToAccount(accountNumber);
    }
    
    @Override
    public List<Transaction> getTransactionsByType(String transactionType) {
        return transactionDAO.findTransactionsByType(transactionType);
    }
    
    @Override
    public List<Transaction> getTransactionsByStatus(String status) {
        return transactionDAO.findTransactionsByStatus(status);
    }
    
    @Override
    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionDAO.findTransactionsByDateRange(startDate, endDate);
    }
    
    @Override
    public List<Transaction> getTransactionsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return transactionDAO.findTransactionsByAmountRange(minAmount, maxAmount);
    }
    
    @Override
    public List<Transaction> getTransactionsByTransferMethod(String transferMethod) {
        return transactionDAO.findTransactionsByTransferMethod(transferMethod);
    }
    
    @Override
    public List<Transaction> getTransactionsByCustomer(String customerId) {
        return transactionDAO.findTransactionsByCustomer(customerId);
    }
    
    // Transaction modification operations
    @Override
    public boolean updateTransaction(Transaction transaction) {
        if (validateTransaction(transaction)) {
            return transactionDAO.updateTransaction(transaction);
        }
        return false;
    }
    
    @Override
    public boolean updateTransactionStatus(String transactionId, String status) {
        return transactionDAO.updateTransactionStatus(transactionId, status);
    }
    
    @Override
    public boolean addTransactionRemarks(String transactionId, String remarks) {
        return transactionDAO.updateTransactionRemarks(transactionId, remarks);
    }
    
    @Override
    public boolean cancelTransaction(String transactionId) {
        Transaction transaction = transactionDAO.findTransactionById(transactionId);
        if (transaction != null && "PENDING".equals(transaction.getStatus())) {
            return transactionDAO.updateTransactionStatus(transactionId, "CANCELLED");
        }
        return false;
    }
    
    @Override
    public boolean reverseTransaction(String transactionId, String reason) {
        Transaction originalTransaction = transactionDAO.findTransactionById(transactionId);
        if (originalTransaction != null && ("SUCCESS".equals(originalTransaction.getStatus()) || "COMPLETED".equals(originalTransaction.getStatus()))) {
            // Create reverse transaction
            Transaction reverseTransaction = new Transaction();
            reverseTransaction.setFromAccountNumber(originalTransaction.getToAccountNumber());
            reverseTransaction.setToAccountNumber(originalTransaction.getFromAccountNumber());
            reverseTransaction.setAmount(originalTransaction.getAmount());
            reverseTransaction.setTransactionType("REVERSAL");
            reverseTransaction.setRemarks("Reversal of " + transactionId + " - " + reason);
            reverseTransaction.setStatus("SUCCESS");
            reverseTransaction.setTransferMethod(originalTransaction.getTransferMethod());
            
            boolean reverseCreated = transactionDAO.createTransaction(reverseTransaction);
            if (reverseCreated) {
                return transactionDAO.updateTransactionStatus(transactionId, "REVERSED");
            }
        }
        return false;
    }
    
    // Account statement and reporting operations
    @Override
    public List<Transaction> generateAccountStatement(String accountNumber, LocalDate fromDate, LocalDate toDate) {
        return transactionDAO.getAccountStatement(accountNumber, fromDate, toDate);
    }
    
    @Override
    public List<Transaction> getRecentTransactions(String accountNumber, int limit) {
        return transactionDAO.getRecentTransactions(accountNumber, limit);
    }
    
    @Override
    public List<Transaction> getHighValueTransactions(BigDecimal threshold) {
        return transactionDAO.findHighValueTransactions(threshold);
    }
    
    @Override
    public List<Transaction> getPendingTransactions() {
        return transactionDAO.findTransactionsByStatus("PENDING");
    }
    
    @Override
    public List<Transaction> getFailedTransactions() {
        return transactionDAO.findTransactionsByStatus("FAILED");
    }
    
    // Balance and amount calculations
    @Override
    public BigDecimal getAccountBalance(String accountNumber) {
        BigDecimal creditAmount = transactionDAO.getTotalCreditAmountByAccount(accountNumber);
        BigDecimal debitAmount = transactionDAO.getTotalDebitAmountByAccount(accountNumber);
        
        if (creditAmount == null) creditAmount = BigDecimal.ZERO;
        if (debitAmount == null) debitAmount = BigDecimal.ZERO;
        
        return creditAmount.subtract(debitAmount);
    }
    
    @Override
    public BigDecimal getTotalTransactionAmount(String accountNumber) {
        return transactionDAO.getTotalTransactionAmountByAccount(accountNumber);
    }
    
    @Override
    public BigDecimal getTotalDebitAmount(String accountNumber) {
        return transactionDAO.getTotalDebitAmountByAccount(accountNumber);
    }
    
    @Override
    public BigDecimal getTotalCreditAmount(String accountNumber) {
        return transactionDAO.getTotalCreditAmountByAccount(accountNumber);
    }
    
   
    @Override
    public BigDecimal calculateDailyTransactionTotal(String accountNumber, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        // Get all transactions for the date range first
        List<Transaction> allTransactions = transactionDAO.findTransactionsByDateRange(startOfDay, endOfDay);
        if (allTransactions == null) {
            allTransactions = Collections.emptyList();
        }

        System.out.println("DEBUG: Found " + allTransactions.size() + " transactions for date " + date);

        // Filter for outgoing transactions from the specific account
        BigDecimal dailyTotal = allTransactions.stream()
            .filter(t -> {
                // Check if this is an outgoing transaction from the account
                boolean isFromAccount = (t.getFromAccountNumber() != null && accountNumber.equals(t.getFromAccountNumber())) ||
                                       (t.getFromAccount() != null && accountNumber.equals(t.getFromAccount().getAccountNumber()));
                
                // Check transaction type - count TRANSFER, DEBIT, and outgoing transactions
                boolean isOutgoingTransaction = "TRANSFER".equalsIgnoreCase(t.getTransactionType()) || 
                                              "DEBIT".equalsIgnoreCase(t.getTransactionType()) ||
                                              "NEFT".equalsIgnoreCase(t.getTransferMethod()) ||
                                              "RTGS".equalsIgnoreCase(t.getTransferMethod()) ||
                                              "IMPS".equalsIgnoreCase(t.getTransferMethod());
                
                // Check status - only count successful/completed transactions
                boolean isSuccessful = "SUCCESS".equalsIgnoreCase(t.getStatus()) || 
                                     "COMPLETED".equalsIgnoreCase(t.getStatus());
                
                boolean shouldCount = isFromAccount && isOutgoingTransaction && isSuccessful;
                
                if (shouldCount) {
                    System.out.println("DEBUG: Counting transaction: " + t.getTransactionId() + 
                                     " Amount: " + t.getAmount() + 
                                     " Type: " + t.getTransactionType() + 
                                     " Status: " + t.getStatus());
                }
                
                return shouldCount;
            })
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("DEBUG: Daily total for account " + accountNumber + " on " + date + " is " + dailyTotal);
        return dailyTotal;
    }

    
    @Override
    public BigDecimal calculateMonthlyTransactionTotal(String accountNumber, int month, int year) {
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        
        List<Transaction> allTransactions = transactionDAO.findTransactionsByDateRange(startOfMonth, endOfMonth);
        if (allTransactions == null) {
            allTransactions = Collections.emptyList();
        }
        
        return allTransactions.stream()
            .filter(t -> {
                // Check if this is an outgoing transaction from the account
                boolean isFromAccount = (t.getFromAccountNumber() != null && accountNumber.equals(t.getFromAccountNumber())) ||
                                       (t.getFromAccount() != null && accountNumber.equals(t.getFromAccount().getAccountNumber()));
                
                // Check transaction type and status
                boolean isOutgoingTransaction = "TRANSFER".equalsIgnoreCase(t.getTransactionType()) || 
                                              "DEBIT".equalsIgnoreCase(t.getTransactionType());
                boolean isSuccessful = "SUCCESS".equalsIgnoreCase(t.getStatus()) || 
                                     "COMPLETED".equalsIgnoreCase(t.getStatus());
                
                return isFromAccount && isOutgoingTransaction && isSuccessful;
            })
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Validation and verification operations
    @Override
    public boolean validateTransaction(Transaction transaction) {
        if (transaction == null) return false;
        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) return false;
        
        // Check if we have either fromAccountNumber or fromAccount
        boolean hasFromAccount = (transaction.getFromAccountNumber() != null && !transaction.getFromAccountNumber().isEmpty()) ||
                               (transaction.getFromAccount() != null && transaction.getFromAccount().getAccountNumber() != null && !transaction.getFromAccount().getAccountNumber().isEmpty());
        
        if (!hasFromAccount && !"CREDIT".equalsIgnoreCase(transaction.getTransactionType())) {
            return false; // Only CREDIT transactions can have no from account
        }
        
        if (transaction.getTransactionType() == null || transaction.getTransactionType().isEmpty()) return false;
        
        return true;
    }
    
    @Override
    public boolean verifyTransactionLimits(String accountNumber, BigDecimal amount) {
        // Skip limit check if accountNumber is null (for CREDIT transactions)
        if (accountNumber == null || accountNumber.isEmpty()) {
            return true;
        }
        
        // Check daily limit
        if (!checkDailyLimit(accountNumber, amount)) {
            System.out.println("Daily limit exceeded for account: " + accountNumber);
            return false;
        }
        
        // Check monthly limit
        if (!checkMonthlyLimit(accountNumber, amount)) {
            System.out.println("Monthly limit exceeded for account: " + accountNumber);
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean isTransactionValid(String transactionId) {
        Transaction transaction = transactionDAO.findTransactionById(transactionId);
        return transaction != null && validateTransaction(transaction);
    }
    
    @Override
    public boolean hasTransactionExpired(String transactionId) {
        Transaction transaction = transactionDAO.findTransactionById(transactionId);
        if (transaction != null && "PENDING".equals(transaction.getStatus())) {
            LocalDateTime transactionTime = transaction.getTransactionDate();
            return transactionTime.isBefore(LocalDateTime.now().minusHours(24));
        }
        return false;
    }
    
    // Business rule operations
    @Override
    public boolean checkDailyLimit(String accountNumber, BigDecimal amount) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            return true; // No limit for system transactions
        }
        
        BigDecimal dailyTotal = calculateDailyTransactionTotal(accountNumber, LocalDate.now());
        BigDecimal newTotal = dailyTotal.add(amount);
        
        System.out.println("DEBUG: Account: " + accountNumber + 
                          " Current daily total: " + dailyTotal + 
                          " Transaction amount: " + amount + 
                          " New total: " + newTotal + 
                          " Limit: " + DAILY_TRANSACTION_LIMIT);
        
        boolean withinLimit = newTotal.compareTo(DAILY_TRANSACTION_LIMIT) <= 0;
        
        if (!withinLimit) {
            System.out.println("LIMIT EXCEEDED: Daily limit of " + DAILY_TRANSACTION_LIMIT + " exceeded. Current: " + dailyTotal + " + " + amount + " = " + newTotal);
        }
        
        return withinLimit;
    }

    
    @Override
    public boolean checkMonthlyLimit(String accountNumber, BigDecimal amount) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            return true; // No limit for system transactions
        }
        
        LocalDate now = LocalDate.now();
        BigDecimal monthlyTotal = calculateMonthlyTransactionTotal(accountNumber, now.getMonthValue(), now.getYear());
        return monthlyTotal.add(amount).compareTo(MONTHLY_TRANSACTION_LIMIT) <= 0;
    }
    
    @Override
    public boolean isAccountEligibleForTransaction(String accountNumber) {
        return accountNumber != null && !accountNumber.trim().isEmpty();
    }
    
    @Override
    public boolean requiresApproval(Transaction transaction) {
        return transaction.getAmount().compareTo(APPROVAL_THRESHOLD) > 0;
    }
    
    // Utility operations
    @Override
    public long getTotalTransactionCount() {
        return transactionDAO.getTotalTransactionCount();
    }
    
    @Override
    public long getTransactionCountByStatus(String status) {
        List<Transaction> transactions = transactionDAO.findTransactionsByStatus(status);
        return transactions != null ? transactions.size() : 0;
    }
    
    @Override
    public boolean transactionExists(String transactionId) {
        return transactionDAO.transactionExists(transactionId);
    }
    
    @Override
    public boolean transactionRefExists(String transactionRefNo) {
        return transactionDAO.transactionRefExists(transactionRefNo);
    }
    
    @Override
    public String generateTransactionReference() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "TXN" + timestamp + uuid;
    }
    
    // Reporting operations
    @Override
    public List<Transaction> getTransactionSummaryByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return transactionDAO.findTransactionsByDateRange(startOfDay, endOfDay);
    }
    
    @Override
    public List<Transaction> getTransactionSummaryByDateRange(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime = toDate.atTime(23, 59, 59);
        return transactionDAO.findTransactionsByDateRange(startDateTime, endDateTime);
    }
    
    @Override
    public BigDecimal getTotalSystemTransactionAmount() {
        return transactionDAO.getTotalTransactionAmount();
    }
}