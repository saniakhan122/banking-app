package com.oracle.business.impl;

import com.oracle.beans.AccountCreationRequest;
import com.oracle.beans.Customer;
import com.oracle.beans.CustomerLogin;
import com.oracle.business.AccountCreationRequestService;
import com.oracle.business.BankingService;
import com.oracle.business.CustomerLoginService;
import com.oracle.business.CustomerService;
import com.oracle.dao.AccountCreationRequestDAO;
import com.oracle.business.util.EmailUtil;
import com.oracle.business.util.ServiceFactory;
import com.oracle.business.OTPService;


import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountCreationRequestServiceImpl implements AccountCreationRequestService {
	
	

    private static final Logger logger = Logger.getLogger(AccountCreationRequestServiceImpl.class.getName());
    
    private AccountCreationRequestDAO requestDAO;
    private CustomerService customerService;
    private CustomerLoginService customerLoginService;
    private BankingService bankingService;
    private OTPService otpService;


    public AccountCreationRequestServiceImpl() {
        this.requestDAO = ServiceFactory.getAccountCreationRequestDAO();
        this.customerService = ServiceFactory.getCustomerService();
        this.customerLoginService = ServiceFactory.getCustomerLoginService();
        this.bankingService = ServiceFactory.getBankingService();
        this.otpService=ServiceFactory.getOTPService();
    }


    @Override
    public boolean createRequest(AccountCreationRequest request) {
        try {
            if (request == null) {
                logger.warning("Cannot create null account request");
                return false;
            }
            if (request.getSubmittedAt() == null) {
                request.setSubmittedAt(LocalDateTime.now());
            }
            return requestDAO.save(request);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating account creation request", e);
            return false;
        }
    }

    @Override
    public boolean updateRequest(AccountCreationRequest request) {
        try {
            if (request == null || request.getRequestId() == null) {
                logger.warning("Invalid account request for update");
                return false;
            }
            return requestDAO.update(request);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating account creation request", e);
            return false;
        }
    }

    @Override
    public AccountCreationRequest getRequestById(String requestId) {
        try {
            return requestDAO.findByRequestId(requestId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting account creation request by ID: " + requestId, e);
            return null;
        }
    }

    @Override
    public AccountCreationRequest getRequestByServiceReferenceNo(String serviceReferenceNo) {
        try {
            return requestDAO.findByServiceReferenceNo(serviceReferenceNo);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting account creation request by service reference no: " + serviceReferenceNo, e);
            return null;
        }
    }

    @Override
    public List<AccountCreationRequest> getRequestsByStatus(String status) {
        try {
            return requestDAO.findByStatus(status);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting requests by status: " + status, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<AccountCreationRequest> getRequestsByEmail(String email) {
        try {
            return requestDAO.findByEmail(email);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting requests by email: " + email, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<AccountCreationRequest> getRequestsByMobile(String mobileNumber) {
        try {
            return requestDAO.findByMobile(mobileNumber);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting requests by mobile: " + mobileNumber, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<AccountCreationRequest> getRequestsByAadhar(String aadharNumber) {
        try {
            return requestDAO.findByAadhar(aadharNumber);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting requests by aadhar: " + aadharNumber, e);
            return Collections.emptyList();
        }
    }

    public boolean approveRequestAndCreateCustomer(String requestId, String username) {
        try {
            // Find pending request
            AccountCreationRequest request = requestDAO.findByRequestId(requestId);
            if (request == null) {
                logger.warning("Account creation request not found: " + requestId);
                return false;
            }
            if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
                logger.warning("Account creation request is not pending: " + requestId);
                return false;
            }

            // Approve the request (update status)
            boolean approved = requestDAO.approveRequest(requestId, username);
            if (!approved) {
                logger.warning("Failed to approve account creation request: " + requestId);
                return false;
            }

            // Create new Customer entity
            Customer customer = new Customer();
            String generatedCustomerId = generateCustomerId();
            customer.setCustomerId(generatedCustomerId);
            customer.setFullName(request.getFullName());
            customer.setEmail(request.getEmail());
            customer.setMobileNumber(request.getMobileNumber());
            customer.setDateOfBirth(request.getDateOfBirth());
            customer.setAadharNumber(request.getAadharNumber());
            customer.setResidentialAddress(request.getResidentialAddress());
            customer.setPermanentAddress(request.getPermanentAddress());
            customer.setOccupation(request.getOccupation());
            customer.setAnnualIncome(request.getAnnualIncome());
            customer.setStatus("ACTIVE");
            customer.setCreatedAt(LocalDateTime.now());
            customer.setServiceReferenceNo(request.getServiceReferenceNo());

            if (!customerService.registerCustomer(customer).equals("SUCCESS")) {
                logger.warning("Failed to register customer for request: " + requestId);
                return false;
            }

            // Create CustomerLogin entity with generated passwords
            CustomerLogin customerLogin = new CustomerLogin();
            customerLogin.setCustomerId(generatedCustomerId);
            // Generate unique userId (e.g., prefix + UUID substring)
            String userId = "user_" + UUID.randomUUID().toString().substring(0, 8);
            customerLogin.setUserId(userId);

            // Generate and hash passwords (use your hash method; here just random base64 string as placeholder)
            String loginPasswordPlain = generateRandomPassword(12);
            String transactionPasswordPlain = generateRandomPassword(12);
            
            // You must hash these before saving - here assumed saved in plain (replace with actual hashing logic)
            customerLogin.setLoginPassword(loginPasswordPlain);
            customerLogin.setTransactionPassword(transactionPasswordPlain);

            customerLogin.setInternetBankingEnabled("N");
            customerLogin.setFailedLoginAttempts(0);
            customerLogin.setIsLocked("N");
            customerLogin.setRegistrationDate(LocalDateTime.now());

            if (!customerLoginService.saveCustomerLogin(customerLogin)) {
                logger.warning("Failed to create login credentials for customer: " + generatedCustomerId);
                return false;
            }

            // Create initial bank account for the customer
            String accountType = request.getAccountType() != null ? request.getAccountType() : "SAVINGS";
            BigDecimal initialDeposit = request.getInitialDeposit() != null ? request.getInitialDeposit() : BigDecimal.valueOf(1000);

            String accountNumber = bankingService.createBankAccount(generatedCustomerId, accountType, initialDeposit);
            if (accountNumber == null || accountNumber.isEmpty()) {
                logger.warning("Failed to create bank account for customer: " + generatedCustomerId);
                return false;
            }
            String otpSaved = otpService.createAndSaveOtp(
            	    generatedCustomerId,
            	    accountNumber,
            	    customer.getMobileNumber(),
            	    "INTERNET_BANKING_REG",
            	    "" // IP address if available otherwise empty
            	);

            	if (otpSaved==null) {
            	    logger.warning("Failed to save OTP for customer: " + generatedCustomerId);
            	}

            // TODO: Send Emails/SMS here with customerId, loginPasswordPlain, transactionPasswordPlain, accountNumber
            
            
         // Prepare the email message with customer details
            String emailSubject = "Your Account Request has been Approved";
            String emailBody = "Dear " + customer.getFullName() + ",\n\n"
            		+"Welcome to Springline Bank!"
                    + "Your account creation request has been approved.\n"
                    + "Customer ID: " + generatedCustomerId + "\n"
                    + "Account Number: " + accountNumber + "\n"
                    + "User ID: " + userId + "\n"
                    + "Temporary Login Password: " + loginPasswordPlain + "\n"
                    + "Temporary Transaction Password: " + transactionPasswordPlain + "\n\n"
                    + "Your OTP is: " + otpSaved + "\n\n"
                    + "Please log in and change your passwords at your earliest convenience.\n\n"
                    + "Thank you for choosing our bank.\n";

            // Send email notification using your EmailUtil or EmailService
            EmailUtil.sendEmail(customer.getEmail(), emailSubject, emailBody);

            // TODO: Optionally add SMS notification similarly here

            logger.info("Successfully approved request and created customer with ID: " + generatedCustomerId);
            return true;

        }  catch (Exception e) {
            logger.log(Level.SEVERE, "Error during approveRequestAndCreateCustomer", e);
            return false;
        }
    }

    
//    public String generateOTP(int length) {
//        Random random = new Random();
//        StringBuilder otp = new StringBuilder();
//        for (int i = 0; i < length; i++) {
//            otp.append(random.nextInt(10)); // digit between 0-9
//        }
//        return otp.toString();
//    }

    
    private String generateCustomerId() {
        return "CUST" + System.currentTimeMillis() + (int) (Math.random() * 1000);
    }

    private String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, length);
    }


    @Override
    public boolean rejectRequest(String requestId, String adminId, String rejectionReason) {
        try {
            AccountCreationRequest request = requestDAO.findByRequestId(requestId);
            if (request == null) {
                logger.warning("Account creation request not found for rejection: " + requestId);
                return false;
            }

            boolean rejected = requestDAO.rejectRequest(requestId, adminId, rejectionReason);
            if (!rejected) {
                logger.warning("Failed to reject account creation request: " + requestId);
                return false;
            }

            // Prepare rejection email
            String emailSubject = "Your Account Request has been Rejected";
            String emailBody = "Dear " + request.getFullName() + ",\n\n"
                    + "We regret to inform you that your account creation request (Reference No: "
                    + request.getServiceReferenceNo() + ") has been rejected.\n\n"
                    + "Reason: " + rejectionReason + "\n\n"
                    + "If you believe this was a mistake or would like more information, "
                    + "please contact our support team.\n\n"
                    + "Thank you for considering Springline Bank.\n";

            // Send rejection email
            EmailUtil.sendEmail(request.getEmail(), emailSubject, emailBody);

            // (Optional) SMS notification
            // SmsUtil.sendSms(request.getMobileNumber(), "Your account request has been rejected. Reason: " + rejectionReason);

            logger.info("Successfully rejected request with ID: " + requestId + " for reason: " + rejectionReason);
            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error rejecting request with ID: " + requestId, e);
            return false;
        }
    }


    @Override
    public List<AccountCreationRequest> getAllRequests() {
        try {
            return requestDAO.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting all account requests", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<AccountCreationRequest> getRequestsSubmittedBetween(LocalDateTime start, LocalDateTime end) {
        try {
            return requestDAO.findSubmittedBetween(start, end);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting requests submitted between dates", e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean deleteRequest(String requestId) {
        try {
            return requestDAO.delete(requestId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting request with ID: " + requestId, e);
            return false;
        }
    }
}

