package com.oracle.beans;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_login")
public class CustomerLogin implements Serializable {

    @Id
    @Column(name = "customer_id", length = 20, nullable = false)
    private String customerId;

    // Relationship to Customer entity
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id",
                insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_login_customer"))
    private Customer customer;

    @Column(name = "user_id", length = 50, unique = true)
    private String userId; // Generated login ID

    @Column(name = "login_password", length = 255, nullable = false)
    private String loginPassword;

    @Column(name = "transaction_password", length = 255, nullable = false)
    private String transactionPassword;

    // Internet Banking Status
    @Column(name = "internet_banking_enabled", length = 1)
    private String internetBankingEnabled = "N"; // Y/N

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    // Security Features
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;

    @Column(name = "is_locked", length = 1)
    private String isLocked = "N"; // Y/N

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    // ===== Getters & Setters =====

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getTransactionPassword() {
        return transactionPassword;
    }

    public void setTransactionPassword(String transactionPassword) {
        this.transactionPassword = transactionPassword;
    }

    public String getInternetBankingEnabled() {
        return internetBankingEnabled;
    }

    public void setInternetBankingEnabled(String internetBankingEnabled) {
        this.internetBankingEnabled = internetBankingEnabled;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getPasswordChangedAt() {
        return passwordChangedAt;
    }

    public void setPasswordChangedAt(LocalDateTime passwordChangedAt) {
        this.passwordChangedAt = passwordChangedAt;
    }
}
