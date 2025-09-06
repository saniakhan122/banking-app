// com.oracle.beans.Customer.java
package com.oracle.beans;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
public class Customer {
    
    @Id
    @Column(name = "customer_id", length = 20)
    private String customerId;
    
    @Column(name = "service_reference_no", length = 20, unique = true)
    private String serviceReferenceNo;
    
    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;
    
    @Column(name = "email", length = 100, unique = false, nullable = false)
    private String email;
    
    @Column(name = "mobile_number", length = 15, unique = true, nullable = false)
    private String mobileNumber;
    
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;
    
    @Column(name = "aadhar_number", length = 12, unique = true, nullable = false)
    private String aadharNumber;
    
    @Column(name = "residential_address", length = 500, nullable = false)
    private String residentialAddress;
    
    @Column(name = "permanent_address", length = 500)
    private String permanentAddress;
    
    @Column(name = "occupation", length = 100)
    private String occupation;
    
    @Column(name = "annual_income", precision = 12, scale = 2)
    private BigDecimal annualIncome;
    
    @Column(name = "status", length = 15)
    private String status = "ACTIVE";
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "created_by", length = 20)
    private String createdBy;
    
    // Default constructor
    public Customer() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with essential fields
    public Customer(String customerId, String fullName, String email, String mobileNumber, 
                   LocalDate dateOfBirth, String aadharNumber, String residentialAddress) {
        this();
        this.customerId = customerId;
        this.fullName = fullName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.dateOfBirth = dateOfBirth;
        this.aadharNumber = aadharNumber;
        this.residentialAddress = residentialAddress;
    }
    
    // Getters and Setters
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}