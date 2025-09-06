package com.oracle.beans;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "otp_requests")
public class OtpRequest {

    @Id
    @Column(name = "otp_id", nullable = false, length = 36)
    private String otpId; // generate UUID or custom id

    @Column(name = "customer_id", length = 20)
    private String customerId;

    @Column(name = "account_number", length = 20)
    private String accountNumber;

    @Column(name = "mobile_number", length = 15)
    private String mobileNumber;

    @Column(name = "otp_code", nullable = false, length = 6)
    private String otpCode;

    @Column(name = "purpose", nullable = false, length = 30)
    private String purpose;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_used", length = 1)
    private String isUsed;  // 'Y' or 'N'

    @Column(name = "verification_attempts")
    private Integer verificationAttempts;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    // Getters & setters omitted for brevity

    public OtpRequest() {
        this.isUsed = "N";
        this.verificationAttempts = 0;
        this.generatedAt = LocalDateTime.now();
        // Set expiry 10 minutes from now, adjust as needed
        this.expiresAt = generatedAt.plusMinutes(10);
    }

	public OtpRequest(String otpId, String customerId, String accountNumber, String mobileNumber, String otpCode,
			String purpose, LocalDateTime generatedAt, LocalDateTime expiresAt, String isUsed,
			Integer verificationAttempts, String ipAddress) {
		super();
		this.otpId = otpId;
		this.customerId = customerId;
		this.accountNumber = accountNumber;
		this.mobileNumber = mobileNumber;
		this.otpCode = otpCode;
		this.purpose = purpose;
		this.generatedAt = generatedAt;
		this.expiresAt = expiresAt;
		this.isUsed = isUsed;
		this.verificationAttempts = verificationAttempts;
		this.ipAddress = ipAddress;
	}

	public String getOtpId() {
		return otpId;
	}

	public void setOtpId(String otpId) {
		this.otpId = otpId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getOtpCode() {
		return otpCode;
	}

	public void setOtpCode(String otpCode) {
		this.otpCode = otpCode;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public LocalDateTime getGeneratedAt() {
		return generatedAt;
	}

	public void setGeneratedAt(LocalDateTime generatedAt) {
		this.generatedAt = generatedAt;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public String getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(String isUsed) {
		this.isUsed = isUsed;
	}

	public Integer getVerificationAttempts() {
		return verificationAttempts;
	}

	public void setVerificationAttempts(Integer verificationAttempts) {
		this.verificationAttempts = verificationAttempts;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

    // other constructors, builder methods as needed
}
