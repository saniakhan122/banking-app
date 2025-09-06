package com.oracle.business.impl;

import com.oracle.beans.BankAccount;
import com.oracle.beans.Transaction;
import com.oracle.dao.BankAccountDAO;
import com.oracle.dao.TransactionDAO;
import com.oracle.business.TransactionService;
import com.oracle.dao.CustomerDAO;
import com.oracle.business.BankingService;
import com.oracle.business.util.ServiceFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.Random;

public class BankingServiceImpl implements BankingService {
    
    private static final Logger logger = Logger.getLogger(BankingServiceImpl.class.getName());
    
    // DAO references using Factory Pattern
    private BankAccountDAO bankAccountDAO;
    private TransactionDAO transactionDAO;
    private CustomerDAO customerDAO;
    
    // Transfer limits
    private static final BigDecimal NEFT_MIN_AMOUNT = new BigDecimal("1");
    private static final BigDecimal NEFT_MAX_AMOUNT = new BigDecimal("1000000");
    private static final BigDecimal RTGS_MIN_AMOUNT = new BigDecimal("200000");
    private static final BigDecimal RTGS_MAX_AMOUNT = new BigDecimal("10000000");
    private static final BigDecimal IMPS_MIN_AMOUNT = new BigDecimal("1");
    private static final BigDecimal IMPS_MAX_AMOUNT = new BigDecimal("500000");
    private static final BigDecimal DAILY_TRANSFER_LIMIT = new BigDecimal("100000");
    private static final BigDecimal MINIMUM_BALANCE_SAVINGS = new BigDecimal("1000");
    private static final BigDecimal MINIMUM_BALANCE_CURRENT = new BigDecimal("5000");
    
    public BankingServiceImpl() {
        // Initialize DAO instances using Factory Pattern
        this.bankAccountDAO = ServiceFactory.getBankAccountDAO();
        this.transactionDAO = ServiceFactory.getTransactionDAO();
        this.customerDAO = ServiceFactory.getCustomerDAO();
    }
    
    @Override
    public String createBankAccount(String customerId, String accountType, BigDecimal initialDeposit) {
        try {
            // Validate customer exists
            if (!customerDAO.customerExists(customerId)) {
                logger.warning("Customer not found: " + customerId);
                return "CUSTOMER_NOT_FOUND";
            }
            
            // Validate initial deposit
            BigDecimal minDeposit = "SAVINGS".equals(accountType) ? MINIMUM_BALANCE_SAVINGS : MINIMUM_BALANCE_CURRENT;
            if (initialDeposit.compareTo(minDeposit) < 0) {
                logger.warning("Insufficient initial deposit: " + initialDeposit);
                return "INSUFFICIENT_INITIAL_DEPOSIT";
            }
            
            // Validate account type
            if (!accountType.equals("SAVINGS") && !accountType.equals("CURRENT")) {
                logger.warning("Invalid account type: " + accountType);
                return "INVALID_ACCOUNT_TYPE";
            }
            
            // Generate account number
            String accountNumber = generateAccountNumber();
            
            // Create bank account
            BankAccount account = new BankAccount();
            account.setAccountNumber(accountNumber);
            account.setCustomerId(customerId);
            account.setAccountType(accountType);
            account.setBalance(initialDeposit);
            account.setIsActive("Y");
            account.setOpenedDate(LocalDateTime.now());
            
            // Save account
            if (bankAccountDAO.createAccount(account)) {
                // Create initial deposit transaction
                createInitialDepositTransaction(accountNumber, initialDeposit);
                
                logger.info("Bank account created successfully: " + accountNumber);
                return accountNumber;
            } else {
                logger.severe("Failed to create bank account");
                return "DATABASE_ERROR";
            }
            
        } catch (Exception e) {
            logger.severe("Error creating bank account: " + e.getMessage());
            return "SYSTEM_ERROR";
        }
    }
    
    private void createInitialDepositTransaction(String accountNumber, BigDecimal amount) {
        try {
            Transaction transaction = new Transaction();
            transaction.setTransactionId(generateTransactionId());
            transaction.setTransactionRefNo(generateTransactionRefNumber());
            transaction.setToAccountNumber(accountNumber);
            transaction.setTransactionType("CREDIT");
            transaction.setAmount(amount);
            transaction.setDescription("Initial Deposit");
            transaction.setRemarks("Account Opening Deposit");
            transaction.setStatus("COMPLETED");
            transaction.setProcessedBy("SYSTEM");
            transaction.setOpeningBalance(BigDecimal.ZERO);
            transaction.setClosingBalance(amount);
            transaction.setTransactionDate(LocalDateTime.now());
            
            transactionDAO.createTransaction(transaction);
            logger.info("Initial deposit transaction created: " + transaction.getTransactionId());
            
        } catch (Exception e) {
            logger.severe("Error creating initial deposit transaction: " + e.getMessage());
        }
    }
    
    @Override
    public BankAccount getAccountDetails(String accountNumber) {
        try {
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                logger.warning("Account number is required");
                return null;
            }
            
            return bankAccountDAO.findAccountByNumber(accountNumber);
            
        } catch (Exception e) {
            logger.severe("Error getting account details: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public List<BankAccount> getCustomerAccounts(String customerId) {
        try {
            if (customerId == null || customerId.trim().isEmpty()) {
                logger.warning("Customer ID is required");
                return null;
            }
            
            return bankAccountDAO.findAccountsByCustomerId(customerId);
            
        } catch (Exception e) {
            logger.severe("Error getting customer accounts: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public boolean doesAccountBelongToCustomer(String accountNumber, String customerId) {
        try {
            if (accountNumber == null || accountNumber.trim().isEmpty() ||
                customerId == null || customerId.trim().isEmpty()) {
                logger.warning("Account number and customer ID are required");
                return false;
            }

            // You may have a DAO method for this, or you can look up the account:
            BankAccount account = bankAccountDAO.findAccountByAccountNumber(accountNumber);

            if (account == null) {
                logger.warning("Account not found: " + accountNumber);
                return false;
            }

            boolean belongs = customerId.equals(account.getCustomerId());
            if (!belongs) {
                logger.warning("Account " + accountNumber + " does not belong to customer " + customerId);
            }
            return belongs;
        } catch (Exception e) {
            logger.severe("Error checking account ownership: " + e.getMessage());
            return false;
        }
    }
    @Override
    public boolean activateAccount(String accountNumber) {
        try {
            if (!isAccountExists(accountNumber)) {
                logger.warning("Account not found: " + accountNumber);
                return false;
            }
            
            return bankAccountDAO.activateAccount(accountNumber);
            
        } catch (Exception e) {
            logger.severe("Error activating account: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean deactivateAccount(String accountNumber) {
        try {
            if (!isAccountExists(accountNumber)) {
                logger.warning("Account not found: " + accountNumber);
                return false;
            }
            
            return bankAccountDAO.deactivateAccount(accountNumber);
            
        } catch (Exception e) {
            logger.severe("Error deactivating account: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public BigDecimal getAccountBalance(String accountNumber) {
        try {
            return bankAccountDAO.getAccountBalance(accountNumber);
        } catch (Exception e) {
            logger.severe("Error getting account balance: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    @Override
    public String transferFunds(String fromAccount, String toAccount, BigDecimal amount, 
                               String transferMethod, String remarks) {
        try {
            // Validate transfer
            if (!validateTransfer(fromAccount, toAccount, amount)) {
                return "VALIDATION_FAILED";
            }
            
            // Validate transfer method and amount
            if (!isValidTransferAmount(amount, transferMethod)) {
                return "INVALID_AMOUNT_FOR_METHOD";
            }
            
            // Check daily transfer limit
            if (!validateTransactionLimits(fromAccount, amount, transferMethod)) {
                return "DAILY_LIMIT_EXCEEDED";
            }
            
            // Process transfer based on method
            boolean transferResult = false;
            switch (transferMethod.toUpperCase()) {
                case "NEFT":
                    transferResult = processNEFTTransfer(fromAccount, toAccount, amount, remarks);
                    break;
                case "RTGS":
                    transferResult = processRTGSTransfer(fromAccount, toAccount, amount, remarks);
                    break;
                case "IMPS":
                    transferResult = processIMPSTransfer(fromAccount, toAccount, amount, remarks);
                    break;
                default:
                    return "INVALID_TRANSFER_METHOD";
            }
            
            if (transferResult) {
                logger.info("Fund transfer successful: " + fromAccount + " to " + toAccount + 
                           " amount: " + amount + " method: " + transferMethod);
                return "SUCCESS";
            } else {
                return "TRANSFER_FAILED";
            }
            
        } catch (Exception e) {
            logger.severe("Error in fund transfer: " + e.getMessage());
            return "SYSTEM_ERROR";
        }
    }
    
    @Override
    public boolean validateTransfer(String fromAccount, String toAccount, BigDecimal amount) {
        try {
            // Check if accounts exist
            if (!isAccountExists(fromAccount)) {
                logger.warning("From account not found: " + fromAccount);
                return false;
            }
            
            if (!isAccountExists(toAccount)) {
                logger.warning("To account not found: " + toAccount);
                return false;
            }
            
            // Check if accounts are active
            if (!isAccountActive(fromAccount)) {
                logger.warning("From account is not active: " + fromAccount);
                return false;
            }
            
            if (!isAccountActive(toAccount)) {
                logger.warning("To account is not active: " + toAccount);
                return false;
            }
            
            // Check if same account
            if (fromAccount.equals(toAccount)) {
                logger.warning("Cannot transfer to same account");
                return false;
            }
            
            // Check sufficient balance
            if (!hasSufficientBalance(fromAccount, amount)) {
                logger.warning("Insufficient balance in account: " + fromAccount);
                return false;
            }
            
            // Check minimum amount
            if (amount.compareTo(new BigDecimal("1")) < 0) {
                logger.warning("Transfer amount must be at least ₹1");
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            logger.severe("Error validating transfer: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean processNEFTTransfer(String fromAccount, String toAccount, BigDecimal amount, String remarks) {
        return processTransfer(fromAccount, toAccount, amount, "NEFT", remarks);
    }
    
    @Override
    public boolean processRTGSTransfer(String fromAccount, String toAccount, BigDecimal amount, String remarks) {
        return processTransfer(fromAccount, toAccount, amount, "RTGS", remarks);
    }
    
    @Override
    public boolean processIMPSTransfer(String fromAccount, String toAccount, BigDecimal amount, String remarks) {
        return processTransfer(fromAccount, toAccount, amount, "IMPS", remarks);
    }
    
//    private boolean processTransfer(String fromAccount, String toAccount, BigDecimal amount, 
//                                  String method, String remarks) {
//        try {
//            // Get current balances
//            BigDecimal fromBalance = getAccountBalance(fromAccount);
//            BigDecimal toBalance = getAccountBalance(toAccount);
//            
//            // Debit from source account
//            if (!bankAccountDAO.debitAccount(fromAccount, amount)) {
//                logger.severe("Failed to debit from account: " + fromAccount);
//                return false;
//            }
//            
//            // Credit to destination account
//            if (!bankAccountDAO.creditAccount(toAccount, amount)) {
//                logger.severe("Failed to credit to account: " + toAccount);
//                // Rollback debit (re-credit the from account)
//                bankAccountDAO.creditAccount(fromAccount, amount);
//                return false;
//            }
//            
//            // Create debit transaction
//            Transaction debitTransaction = new Transaction();
//            debitTransaction.setTransactionId(generateTransactionId());
//            debitTransaction.setTransactionRefNo(generateTransactionRefNumber());
//            debitTransaction.setFromAccountNumber(fromAccount);
//            debitTransaction.setToAccountNumber(toAccount);
//            debitTransaction.setTransactionType("DEBIT");
//            debitTransaction.setTransferMethod(method);
//            debitTransaction.setAmount(amount);
//            debitTransaction.setDescription(method + " Transfer to " + toAccount);
//            debitTransaction.setRemarks(remarks);
//            debitTransaction.setStatus("COMPLETED");
//            debitTransaction.setProcessedBy("SYSTEM");
//            debitTransaction.setOpeningBalance(fromBalance);
//            debitTransaction.setClosingBalance(fromBalance.subtract(amount));
//            debitTransaction.setTransactionDate(LocalDateTime.now());
//            
//            // Create credit transaction
//            Transaction creditTransaction = new Transaction();
//            creditTransaction.setTransactionId(generateTransactionId());
//            creditTransaction.setTransactionRefNo(debitTransaction.getTransactionRefNo()); // Same ref
//            creditTransaction.setFromAccountNumber(fromAccount);
//            creditTransaction.setToAccountNumber(toAccount);
//            creditTransaction.setTransactionType("CREDIT");
//            creditTransaction.setTransferMethod(method);
//            creditTransaction.setAmount(amount);
//            creditTransaction.setDescription(method + " Transfer from " + fromAccount);
//            creditTransaction.setRemarks(remarks);
//            creditTransaction.setStatus("COMPLETED");
//            creditTransaction.setProcessedBy("SYSTEM");
//            creditTransaction.setOpeningBalance(toBalance);
//            creditTransaction.setClosingBalance(toBalance.add(amount));
//            creditTransaction.setTransactionDate(LocalDateTime.now());
//            
//            // Save transactions
//            boolean debitSaved = transactionDAO.createTransaction(debitTransaction);
//            boolean creditSaved = transactionDAO.createTransaction(creditTransaction);
//            
//            if (debitSaved && creditSaved) {
//                logger.info("Transfer completed successfully: " + method + " from " + fromAccount + " to " + toAccount);
//                return true;
//            } else {
//                logger.severe("Failed to save transaction records");
//                return false;
//            }
//            
//        } catch (Exception e) {
//            logger.severe("Error processing transfer: " + e.getMessage());
//            return false;
//        }
//    }
    
    private boolean processTransfer(String fromAccount, String toAccount, BigDecimal amount, 
            String method, String remarks) {
try {
// Get balances
BigDecimal fromBalance = getAccountBalance(fromAccount);
BigDecimal toBalance = getAccountBalance(toAccount);

if (fromBalance == null || toBalance == null) {
logger.severe("Account balance retrieval failed for " + 
     (fromBalance == null ? fromAccount : toAccount));
return false;
}

// Check sufficient balance
if (fromBalance.compareTo(amount) < 0) {
logger.warning("Insufficient balance in account: " + fromAccount);
return false;
}

// Perform debit
if (!bankAccountDAO.debitAccount(fromAccount, amount)) {
logger.severe("Failed to debit from account: " + fromAccount);
return false;
}

// Perform credit
if (!bankAccountDAO.creditAccount(toAccount, amount)) {
logger.severe("Failed to credit to account: " + toAccount);
// Rollback debit
bankAccountDAO.creditAccount(fromAccount, amount);
return false;
}

// Generate shared details
LocalDateTime now = LocalDateTime.now();
String transactionRef = generateTransactionRefNumber();

// Create transaction records
Transaction debitTransaction = buildTransaction(
generateTransactionId(), transactionRef, fromAccount, toAccount,
"DEBIT", method, amount, method + " Transfer to " + toAccount,
remarks, fromBalance, fromBalance.subtract(amount), now);

Transaction creditTransaction = buildTransaction(
generateTransactionId(), transactionRef, fromAccount, toAccount,
"CREDIT", method, amount, method + " Transfer from " + fromAccount,
remarks, toBalance, toBalance.add(amount), now);

// Save both transactions
if (transactionDAO.createTransaction(debitTransaction) && 
transactionDAO.createTransaction(creditTransaction)) {
logger.info(String.format("Transfer successful: %s from %s to %s, Amount: %s", 
                  method, fromAccount, toAccount, amount));
return true;
} else {
logger.severe("Failed to save transaction records. Manual review required.");
return false;
}

} catch (Exception e) {
logger.severe("Error processing transfer: " + e.getMessage());
e.printStackTrace();
return false;
}
}

/**
* Helper method to build a Transaction object.
*/
private Transaction buildTransaction(String id, String refNo, String fromAccount, String toAccount,
                 String type, String method, BigDecimal amount, String description,
                 String remarks, BigDecimal openingBalance, BigDecimal closingBalance,
                 LocalDateTime date) {
Transaction txn = new Transaction();
txn.setTransactionId(id);
txn.setTransactionRefNo(refNo);
txn.setFromAccountNumber(fromAccount);
txn.setToAccountNumber(toAccount);
txn.setTransactionType(type);
txn.setTransferMethod(method);
txn.setAmount(amount);
txn.setDescription(description);
txn.setRemarks(remarks);
txn.setStatus("COMPLETED");
txn.setProcessedBy("SYSTEM");
txn.setOpeningBalance(openingBalance);
txn.setClosingBalance(closingBalance);
txn.setTransactionDate(date);
return txn;
}

    
    @Override
    public boolean creditAccount(String accountNumber, BigDecimal amount, String description) {
        try {
            if (!isAccountExists(accountNumber) || !isAccountActive(accountNumber)) {
                logger.warning("Account not found or inactive: " + accountNumber);
                return false;
            }
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warning("Credit amount must be positive: " + amount);
                return false;
            }
            
            BigDecimal currentBalance = getAccountBalance(accountNumber);
            
            // Credit the account
            if (bankAccountDAO.creditAccount(accountNumber, amount)) {
                // Create transaction record
                Transaction transaction = new Transaction();
                transaction.setTransactionId(generateTransactionId());
                transaction.setTransactionRefNo(generateTransactionRefNumber());
                transaction.setToAccountNumber(accountNumber);
                transaction.setTransactionType("CREDIT");
                transaction.setAmount(amount);
                transaction.setDescription(description != null ? description : "Credit Transaction");
                transaction.setStatus("COMPLETED");
                transaction.setProcessedBy("SYSTEM");
                transaction.setOpeningBalance(currentBalance);
                transaction.setClosingBalance(currentBalance.add(amount));
                transaction.setTransactionDate(LocalDateTime.now());
                
                transactionDAO.createTransaction(transaction);
                logger.info("Account credited successfully: " + accountNumber + " amount: " + amount);
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            logger.severe("Error crediting account: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean debitAccount(String accountNumber, BigDecimal amount, String description) {
        try {
            if (!isAccountExists(accountNumber) || !isAccountActive(accountNumber)) {
                logger.warning("Account not found or inactive: " + accountNumber);
                return false;
            }
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warning("Debit amount must be positive: " + amount);
                return false;
            }
            
            if (!hasSufficientBalance(accountNumber, amount)) {
                logger.warning("Insufficient balance for debit: " + accountNumber);
                return false;
            }
            
            BigDecimal currentBalance = getAccountBalance(accountNumber);
            
            // Debit the account
            if (bankAccountDAO.debitAccount(accountNumber, amount)) {
                // Create transaction record
                Transaction transaction = new Transaction();
                transaction.setTransactionId(generateTransactionId());
                transaction.setTransactionRefNo(generateTransactionRefNumber());
                transaction.setFromAccountNumber(accountNumber);
                transaction.setTransactionType("DEBIT");
                transaction.setAmount(amount);
                transaction.setDescription(description != null ? description : "Debit Transaction");
                transaction.setStatus("COMPLETED");
                transaction.setProcessedBy("SYSTEM");
                transaction.setOpeningBalance(currentBalance);
                transaction.setClosingBalance(currentBalance.subtract(amount));
                transaction.setTransactionDate(LocalDateTime.now());
                
                transactionDAO.createTransaction(transaction);
                logger.info("Account debited successfully: " + accountNumber + " amount: " + amount);
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            logger.severe("Error debiting account: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<Transaction> getAccountStatement(String accountNumber, LocalDate fromDate, LocalDate toDate) {
        try {
            if (!isAccountExists(accountNumber)) {
                logger.warning("Account not found: " + accountNumber);
                return null;
            }
            
            return transactionDAO.getAccountStatement(accountNumber, fromDate, toDate);
            
        } catch (Exception e) {
            logger.severe("Error getting account statement: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public List<Transaction> getRecentTransactions(String accountNumber, int limit) {
        try {
            if (!isAccountExists(accountNumber)) {
                logger.warning("Account not found: " + accountNumber);
                return null;
            }
            
            return transactionDAO.getRecentTransactions(accountNumber, limit);
            
        } catch (Exception e) {
            logger.severe("Error getting recent transactions: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public Transaction getTransactionDetails(String transactionId) {
        try {
            if (transactionId == null || transactionId.trim().isEmpty()) {
                logger.warning("Transaction ID is required");
                return null;
            }
            
            return transactionDAO.findTransactionById(transactionId);
            
        } catch (Exception e) {
            logger.severe("Error getting transaction details: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public boolean isAccountExists(String accountNumber) {
        try {
            return bankAccountDAO.accountExists(accountNumber);
        } catch (Exception e) {
            logger.severe("Error checking account existence: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean isAccountActive(String accountNumber) {
        try {
            BankAccount account = bankAccountDAO.findAccountByNumber(accountNumber);
            return account != null && "Y".equals(account.getIsActive());
        } catch (Exception e) {
            logger.severe("Error checking account status: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean hasSufficientBalance(String accountNumber, BigDecimal amount) {
        try {
            BigDecimal balance = getAccountBalance(accountNumber);
            BankAccount account = getAccountDetails(accountNumber);
            
            if (account == null) return false;
            
            BigDecimal minBalance = "SAVINGS".equals(account.getAccountType()) 
                ? MINIMUM_BALANCE_SAVINGS : MINIMUM_BALANCE_CURRENT;
            
            return balance.subtract(amount).compareTo(minBalance) >= 0;
            
        } catch (Exception e) {
            logger.severe("Error checking sufficient balance: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean isValidTransferAmount(BigDecimal amount, String transferMethod) {
        try {
            if (amount == null || transferMethod == null) return false;
            
            switch (transferMethod.toUpperCase()) {
                case "NEFT":
                    return amount.compareTo(NEFT_MIN_AMOUNT) >= 0 && 
                           amount.compareTo(NEFT_MAX_AMOUNT) <= 0;
                case "RTGS":
                    return amount.compareTo(RTGS_MIN_AMOUNT) >= 0 && 
                           amount.compareTo(RTGS_MAX_AMOUNT) <= 0;
                case "IMPS":
                    return amount.compareTo(IMPS_MIN_AMOUNT) >= 0 && 
                           amount.compareTo(IMPS_MAX_AMOUNT) <= 0;
                default:
                    return false;
            }
        } catch (Exception e) {
            logger.severe("Error validating transfer amount: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean validateTransactionLimits(String accountNumber, BigDecimal amount, String transferMethod) {
        // Create a TransactionService instance if you don't have one
        TransactionService transactionService = new TransactionServiceImpl(transactionDAO);
        
        // Check daily limits using the transaction service
        return transactionService.checkDailyLimit(accountNumber, amount) && 
               transactionService.checkMonthlyLimit(accountNumber, amount);
    }
    @Override
    public boolean isValidTransferMethod(String transferMethod) {
        if (transferMethod == null) return false;
        String method = transferMethod.toUpperCase();
        return "NEFT".equals(method) || "RTGS".equals(method) || "IMPS".equals(method);
    }
    
    @Override
    public BigDecimal getTransferLimit(String transferMethod) {
        if (transferMethod == null) return BigDecimal.ZERO;
        
        switch (transferMethod.toUpperCase()) {
            case "NEFT":
                return NEFT_MAX_AMOUNT;
            case "RTGS":
                return RTGS_MAX_AMOUNT;
            case "IMPS":
                return IMPS_MAX_AMOUNT;
            default:
                return BigDecimal.ZERO;
        }
    }
    
    @Override
    public BigDecimal getDailyTransferLimit() {
        return DAILY_TRANSFER_LIMIT;
    }
    
    @Override
    public String generateAccountNumber() {
        // Generate 12-digit account number starting with bank code
        String bankCode = "1234";
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder(bankCode);
        
        for (int i = 0; i < 8; i++) {
            accountNumber.append(random.nextInt(10));
        }
        
        return accountNumber.toString();
    }
    
    @Override
    public String generateTransactionId() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
    
    @Override
    public String generateTransactionRefNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Random random = new Random();
        String randomNum = String.format("%04d", random.nextInt(10000));
        return "REF" + timestamp + randomNum;
    }
    
    @Override
    public List<Transaction> getAllTransactions() {
        try {
            return transactionDAO.findAllTransactions();
        } catch (Exception e) {
            logger.severe("Error getting all transactions: " + e.getMessage());
            return null;
        }
    }
    
    @	Override
    public List<BankAccount> getAllAccounts() {
        return bankAccountDAO.findAllAccounts();
    }

    
    @Override
    public BigDecimal getTotalBankBalance() {
        try {
            List<BankAccount> allAccounts = bankAccountDAO.findAllAccounts();
            return allAccounts.stream()
                .map(BankAccount::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            logger.severe("Error getting total bank balance: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    @Override
    public List<Transaction> getHighValueTransactions(BigDecimal threshold) {
        try {
            return transactionDAO.findHighValueTransactions(threshold);
        } catch (Exception e) {
            logger.severe("Error getting high value transactions: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public List<Transaction> getTransactionsByCustomer(String customerId) {
        try {
            return transactionDAO.findTransactionsByCustomer(customerId);
        } catch (Exception e) {
            logger.severe("Error getting transactions by customer: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public String getAccountBalanceInquiry(String accountNumber) {
        try {
            if (!isAccountExists(accountNumber)) {
                return "ACCOUNT_NOT_FOUND";
            }
            
            if (!isAccountActive(accountNumber)) {
                return "ACCOUNT_INACTIVE";
            }
            
            BigDecimal balance = getAccountBalance(accountNumber);
            return "BALANCE_" + balance.toString();
            
        } catch (Exception e) {
            logger.severe("Error in balance inquiry: " + e.getMessage());
            return "SYSTEM_ERROR";
        }
    }
    
    @Override
    public boolean checkMinimumBalance(String accountNumber) {
        try {
            BankAccount account = getAccountDetails(accountNumber);
            if (account == null) return false;
            
            BigDecimal currentBalance = getAccountBalance(accountNumber);
            BigDecimal minBalance = "SAVINGS".equals(account.getAccountType()) 
                ? MINIMUM_BALANCE_SAVINGS : MINIMUM_BALANCE_CURRENT;
            
            return currentBalance.compareTo(minBalance) >= 0;
            
        } catch (Exception e) {
            logger.severe("Error checking minimum balance: " + e.getMessage());
            return false;
        }
    }
    
//    @Override
//    public List<BankAccount> getCustomerAccountsByUserId(String userId) {
//        try {
//            if (userId == null || userId.trim().isEmpty()) {
//                logger.warning("User ID is required");
//                return null;
//            }
//            
//            return bankAccountDAO.findAccountsByUserId(userId);
//            
//        } catch (Exception e) {
//            logger.severe("Error getting customer accounts: " + e.getMessage());
//            return null;
//        }
//
//	
//}
}