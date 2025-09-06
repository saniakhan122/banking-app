package com.oracle.business;

import com.oracle.beans.OtpRequest;

public interface OTPService {

    /**
     * Generate a numeric OTP of specified length
     * 
     * @param length length of OTP
     * @return generated OTP string
     */
    String generateOTP(int length);

    /**
     * Create and save OTP record in database
     * 
     * @param customerId customer identifier
     * @param accountNumber bank account number
     * @param mobileNumber customer's mobile number
     * @param purpose purpose of OTP (e.g. ACCOUNT_APPROVAL)
     * @param ipAddress IP address of request origin
     * @return true if OTP saved successfully
     */
    String createAndSaveOtp(String customerId, String accountNumber, String mobileNumber, String purpose, String ipAddress);

    /**
     * Validate supplied OTP against stored record
     * 
     * @param otpId unique OTP identifier
     * @param otpCode OTP code input by user
     * @return true if OTP is valid and matches
     */
//    boolean validateOtp(String otpId, String otpCode);

    /**
     * Mark OTP as used after successful verification
     * 
     * @param otp OTP entity
     * @return true if update successful
     */
//    boolean markOtpUsed(OtpRequest otp);

    /**
     * Delete all expired OTP records from DB
     * 
     * @return true if deletion executed successfully
     */
//    boolean deleteExpiredOtps();
}
