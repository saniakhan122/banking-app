package com.oracle.beans;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "beneficiaries")
public class Beneficiary implements Serializable {

    @Id
    @Column(name = "beneficiary_id", length = 20, nullable = false)
    private String beneficiaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_beneficiary_customer"))
    private Customer customer;  // Linked to customers table

    @Column(name = "beneficiary_name", length = 100, nullable = false)
    private String beneficiaryName;

    @Column(name = "account_number", length = 20, nullable = false)
    private String accountNumber;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "ifsc_code", length = 11)
    private String ifscCode;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "is_verified", length = 1)
    private String isVerified = "N"; // Y/N

    @Column(name = "is_active", length = 1)
    private String isActive = "Y"; // Y/N

    @Column(name = "added_date")
    private LocalDateTime addedDate;

    @Column(name = "last_used_date")
    private LocalDateTime lastUsedDate;

    // ===== Getters and Setters =====
    public String getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(String beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }

    public LocalDateTime getLastUsedDate() {
        return lastUsedDate;
    }

    public Beneficiary(String beneficiaryId, Customer customer, String beneficiaryName, String accountNumber,
			String bankName, String ifscCode, String nickname, String isVerified, String isActive,
			LocalDateTime addedDate, LocalDateTime lastUsedDate) {
		super();
		this.beneficiaryId = beneficiaryId;
		this.customer = customer;
		this.beneficiaryName = beneficiaryName;
		this.accountNumber = accountNumber;
		this.bankName = bankName;
		this.ifscCode = ifscCode;
		this.nickname = nickname;
		this.isVerified = isVerified;
		this.isActive = isActive;
		this.addedDate = addedDate;
		this.lastUsedDate = lastUsedDate;
	}

	public void setLastUsedDate(LocalDateTime lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }

    // Optional: Constructors, equals, hashCode, toString
}
