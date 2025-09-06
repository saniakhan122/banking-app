package com.oracle.dao;

import java.util.List;

import com.oracle.beans.OtpRequest;

/**
 * OTP Data Access Object Interface
 */
public interface OTPDAO {
    
    boolean saveOTP(OtpRequest otp);

	OtpRequest getActiveOTPByCustomerIdAndPurpose(String customerId);

	OtpRequest getLatestOTPByCustomerId(String customerId);
    
//    OtpRequest getActiveOTPByMobile(String mobileNumber, String purpose);
//    
//    boolean updateOTP(OtpRequest otp);
//    
//    boolean isValidOTP(String otpId, String otpCode);
//    
//    boolean deleteExpiredOTPs();
//    
//    List<OtpRequest> getExpiredOTPs();
}
