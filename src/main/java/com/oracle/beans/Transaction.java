// com.oracle.beans.Transaction.java
package com.oracle.beans;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @Column(name = "transaction_id", length = 25)
    private String transactionId;
    
    @Column(name = "transaction_ref_no", length = 30, unique = true, nullable = false)
    private String transactionRefNo;
    
    @Column(name = "from_account_number", length = 20)
   private String fromAccountNumber;
//    
   @Column(name = "to_account_number", length = 20)
    private String toAccountNumber;
    
    @Column(name = "transaction_type", length = 15, nullable = false)
    private String TransactionType;
    
    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "transfer_method", length = 10)
    private String transferMethod;
    
    @Column(name = "status", length = 15)
    private String status = "COMPLETED";
    
    @Column(name = "description", length = 200)
    private String description;
    
    @Column(name = "remarks", length = 300)
    private String remarks;
    
    @Column(name = "opening_balance", precision = 15, scale = 2)
    private BigDecimal openingBalance;
    
    @Column(name = "closing_balance", precision = 15, scale = 2)
    private BigDecimal closingBalance;
    
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
    
    @Column(name = "value_date")
    private LocalDate valueDate;
    
    @Column(name = "processed_by", length = 50)
    private String processedBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_number", insertable = false, updatable = false)
    private BankAccount fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_number", insertable = false, updatable = false)
    private BankAccount toAccount;


    
    // Default constructor
    public Transaction() {
        this.transactionDate = LocalDateTime.now();
        this.valueDate = LocalDate.now();
    }
    
    // Constructor with essential fields
    public Transaction(String transactionId, String transactionRefNo, String fromAccountNumber,
                      String toAccountNumber, String TransactionType, BigDecimal amount) {
        this();
        this.transactionId = transactionId;
        this.transactionRefNo = transactionRefNo;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.TransactionType = TransactionType;
        this.amount = amount;
    }
    
    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getTransactionRefNo() {
        return transactionRefNo;
    }
    
    public void setTransactionRefNo(String transactionRefNo) {
        this.transactionRefNo = transactionRefNo;
    }
    
    public String getFromAccountNumber() {
        return fromAccountNumber;
    }
    
    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }
    
    public String getToAccountNumber() {
        return toAccountNumber;
    }
    
    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }
    
    public String getTransactionType() {
        return TransactionType;
    }
    
    public void setTransactionType(String transactionType) {
        this.TransactionType = transactionType;
    }

    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getTransferMethod() {
        return transferMethod;
    }
    
    public void setTransferMethod(String transferMethod) {
        this.transferMethod = transferMethod;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }
    
    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }
    
    public BigDecimal getClosingBalance() {
        return closingBalance;
    }
    
    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public LocalDate getValueDate() {
        return valueDate;
    }
    
    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }
    
    public String getProcessedBy() {
        return processedBy;
    }
    
    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
    }
    
    public BankAccount getFromAccount() {
        return fromAccount;
    }
    
    public void setFromAccount(BankAccount fromAccount) {
        this.fromAccount = fromAccount;
    }
    
    public BankAccount getToAccount() {
        return toAccount;
    }
    
    public void setToAccount(BankAccount toAccount) {
        this.toAccount = toAccount;
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", transactionRefNo='" + transactionRefNo + '\'' +
                ", transactionType='" + TransactionType + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", transactionDate=" + transactionDate +
                '}';
    }
}