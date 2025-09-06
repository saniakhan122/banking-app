package com.oracle.beans;

public class TransactionWithType {
    private Transaction transaction;
    private String transactionType; // "DEBIT" or "CREDIT"
    private String counterpartyAccount;
    
    // Default constructor
    public TransactionWithType() {
    }
    
    // Constructor with all fields
    public TransactionWithType(Transaction transaction, String transactionType, String counterpartyAccount) {
        this.transaction = transaction;
        this.transactionType = transactionType;
        this.counterpartyAccount = counterpartyAccount;
    }
    
    // Getters and Setters
    public Transaction getTransaction() {
        return transaction;
    }
    
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
    
    public String getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    
    public String getCounterpartyAccount() {
        return counterpartyAccount;
    }
    
    public void setCounterpartyAccount(String counterpartyAccount) {
        this.counterpartyAccount = counterpartyAccount;
    }
    
    // Convenience methods to get transaction details
    public String getTransactionId() {
        return transaction != null ? transaction.getTransactionId() : null;
    }
    
    public String getDescription() {
        if (transaction == null) return null;
        
        // Create a user-friendly description based on transaction type
        if ("DEBIT".equals(transactionType)) {
            return "Transfer to " + counterpartyAccount;
        } else if ("CREDIT".equals(transactionType)) {
            return "Transfer from " + counterpartyAccount;
        }
        
        return transaction.getDescription();
    }
    
    @Override
    public String toString() {
        return "TransactionWithType{" +
                "transactionId='" + (transaction != null ? transaction.getTransactionId() : null) + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", counterpartyAccount='" + counterpartyAccount + '\'' +
                ", amount=" + (transaction != null ? transaction.getAmount() : null) +
                '}';
    }
}