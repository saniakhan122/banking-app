// com.oracle.beans.BankAccount.java
package com.oracle.beans;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {
    
    @Id
    @Column(name = "account_number", length = 20)
    private String accountNumber;
    
    @Column(name = "customer_id", length = 20, nullable = false)
    private String customerId;
    
    @Column(name = "account_type", length = 20, nullable = false)
    private String accountType;
    
    @Column(name = "balance", precision = 15, scale = 2)
    private BigDecimal balance = new BigDecimal("0.00");
    
    @Column(name = "is_active", length = 1)
    private String isActive = "Y";
    
    @Column(name = "opened_date")
    private LocalDateTime openedDate;
    
    @Column(name = "last_transaction_date")
    private LocalDateTime lastTransactionDate;
    
    // JPA Relationship - Many to One with Customer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;
    
    // Default constructor
    public BankAccount() {
        this.openedDate = LocalDateTime.now();
    }
    
    // Constructor with essential fields
    public BankAccount(String accountNumber, String customerId, String accountType, BigDecimal balance) {
        this();
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
    }
    
    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getAccountType() {
        return accountType;
    }
    
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public String getIsActive() {
        return isActive;
    }
    
    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getOpenedDate() {
        return openedDate;
    }
    
    public void setOpenedDate(LocalDateTime openedDate) {
        this.openedDate = openedDate;
    }
    
    public LocalDateTime getLastTransactionDate() {
        return lastTransactionDate;
    }
    
    public void setLastTransactionDate(LocalDateTime lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    @Override
    public String toString() {
        return "BankAccount{" +
                "accountNumber='" + accountNumber + '\'' +
                ", customerId='" + customerId + '\'' +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                ", isActive='" + isActive + '\'' +
                '}';
    }
}