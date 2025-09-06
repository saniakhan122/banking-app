package com.oracle.beans;

import jakarta.persistence.*;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_requests")
public class AccountCreationRequest implements Serializable {

    @Id
    @Column(name = "request_id", length = 20, nullable = false)
    private String requestId;

    @Column(name = "service_reference_no", length = 20, unique = true, nullable = false)
    private String serviceReferenceNo;

    // Personal Details
    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "mobile_number", length = 15, nullable = false)
    private String mobileNumber;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "aadhar_number", length = 12, nullable = false, unique = true)
    private String aadharNumber;

    // Address Details
    @Column(name = "residential_address", length = 500, nullable = false)
    private String residentialAddress;

    @Column(name = "permanent_address", length = 500)
    private String permanentAddress;

    // Professional Details
    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "annual_income", precision = 12, scale = 2)
    private BigDecimal annualIncome;

    // Account Details
    @Column(name = "account_type", length = 20)
    private String accountType = "SAVINGS";

    @Column(name = "initial_deposit", precision = 10, scale = 2)
    private BigDecimal initialDeposit = new BigDecimal("1000");

    // Request Status
    @Column(name = "status", length = 15)
    private String status = "PENDING";

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    // Admin Processing
    @ManyToOne
    @JoinColumn(name = "processed_by", referencedColumnName = "admin_id")
    private AdminUser processedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getServiceReferenceNo() {
		return serviceReferenceNo;
	}

	public void setServiceReferenceNo(String serviceReferenceNo) {
		this.serviceReferenceNo = serviceReferenceNo;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public String getResidentialAddress() {
		return residentialAddress;
	}

	public void setResidentialAddress(String residentialAddress) {
		this.residentialAddress = residentialAddress;
	}

	public String getPermanentAddress() {
		return permanentAddress;
	}

	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public BigDecimal getAnnualIncome() {
		return annualIncome;
	}

	public void setAnnualIncome(BigDecimal annualIncome) {
		this.annualIncome = annualIncome;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public BigDecimal getInitialDeposit() {
		return initialDeposit;
	}

	public void setInitialDeposit(BigDecimal initialDeposit) {
		this.initialDeposit = initialDeposit;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getSubmittedAt() {
		return submittedAt;
	}

	public void setSubmittedAt(LocalDateTime submittedAt) {
		this.submittedAt = submittedAt;
	}

	public AdminUser getProcessedBy() {
		return processedBy;
	}

	public void setProcessedBy(AdminUser processedBy) {
		this.processedBy = processedBy;
	}

	public LocalDateTime getProcessedAt() {
		return processedAt;
	}

	public void setProcessedAt(LocalDateTime processedAt) {
		this.processedAt = processedAt;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}
	
	public AccountCreationRequest() {
	    // No-arg constructor needed by JPA and for your use cases
	}


	public AccountCreationRequest(String requestId, String serviceReferenceNo, String fullName, String email,
			String mobileNumber, LocalDate dateOfBirth, String aadharNumber, String residentialAddress,
			String permanentAddress, String occupation, BigDecimal annualIncome, String accountType,
			BigDecimal initialDeposit, String status, LocalDateTime submittedAt, AdminUser processedBy,
			LocalDateTime processedAt, String rejectionReason) {
		super();
		this.requestId = requestId;
		this.serviceReferenceNo = serviceReferenceNo;
		this.fullName = fullName;
		this.email = email;
		this.mobileNumber = mobileNumber;
		this.dateOfBirth = dateOfBirth;
		this.aadharNumber = aadharNumber;
		this.residentialAddress = residentialAddress;
		this.permanentAddress = permanentAddress;
		this.occupation = occupation;
		this.annualIncome = annualIncome;
		this.accountType = accountType;
		this.initialDeposit = initialDeposit;
		this.status = status;
		this.submittedAt = submittedAt;
		this.processedBy = processedBy;
		this.processedAt = processedAt;
		this.rejectionReason = rejectionReason;
	}

	@Override
	public String toString() {
		return "AccountCreationRequest [requestId=" + requestId + ", serviceReferenceNo=" + serviceReferenceNo
				+ ", fullName=" + fullName + ", email=" + email + ", mobileNumber=" + mobileNumber + ", dateOfBirth="
				+ dateOfBirth + ", aadharNumber=" + aadharNumber + ", residentialAddress=" + residentialAddress
				+ ", permanentAddress=" + permanentAddress + ", occupation=" + occupation + ", annualIncome="
				+ annualIncome + ", accountType=" + accountType + ", initialDeposit=" + initialDeposit + ", status="
				+ status + ", submittedAt=" + submittedAt + ", processedBy=" + processedBy + ", processedAt="
				+ processedAt + ", rejectionReason=" + rejectionReason + "]";
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	@Column(name = "rejection_reason", length = 300)
    private String rejectionReason;

    // Getters and Setters omitted for brevity (generate via IDE)

    // Constructors, equals, hashCode, toString can also be generated
}
