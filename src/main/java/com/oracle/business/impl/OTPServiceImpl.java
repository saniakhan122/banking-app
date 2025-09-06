package com.oracle.business.impl;

import com.oracle.beans.OtpRequest;
import com.oracle.business.OTPService;
import com.oracle.dao.OTPDAO;
import com.oracle.business.util.ServiceFactory;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;


public class OTPServiceImpl implements OTPService {

    private static final Logger logger = Logger.getLogger(OTPServiceImpl.class.getName());
    private OTPDAO otpDAO;

    public OTPServiceImpl() {
        this.otpDAO = ServiceFactory.getOTPDAO();
    }

    @Override
    public String generateOTP(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // generate digit 0-9
        }
        return sb.toString();
    }

    public String createAndSaveOtp(String customerId, String accountNumber, String mobileNumber, String purpose, String ipAddress) {
        String otpCode = generateOTP(6);
        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setOtpId(UUID.randomUUID().toString());
        otpRequest.setCustomerId(customerId);
        otpRequest.setAccountNumber(accountNumber);
        otpRequest.setMobileNumber(mobileNumber);
        otpRequest.setOtpCode(otpCode);
        otpRequest.setPurpose(purpose);
        otpRequest.setGeneratedAt(LocalDateTime.now());
        otpRequest.setExpiresAt(LocalDateTime.now().plusMinutes(100000000));
        otpRequest.setIsUsed("N");
        otpRequest.setVerificationAttempts(0);
        otpRequest.setIpAddress(ipAddress);

        boolean saved = otpDAO.saveOTP(otpRequest);
        if (saved) {
            return otpCode;  // return the generated OTP
        } else {
            return null;  // or throw exception
        }
    }
    





   
}

    