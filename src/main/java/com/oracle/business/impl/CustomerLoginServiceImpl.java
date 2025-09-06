package com.oracle.business.impl;

import com.oracle.beans.CustomerLogin;
import com.oracle.beans.OtpRequest;
import com.oracle.business.CustomerLoginService;
import com.oracle.dao.CustomerLoginDAO;
import com.oracle.dao.OTPDAO;

import jakarta.ws.rs.core.Response;

import com.oracle.business.util.ServiceFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerLoginServiceImpl implements CustomerLoginService {

    private static final Logger logger = Logger.getLogger(CustomerLoginServiceImpl.class.getName());
    private CustomerLoginDAO customerLoginDAO;
    private OTPDAO otpDAO;

    // You may want to use a secure password encoder instead of plain equals checks
    // For example, BCryptPasswordEncoder in Spring Security or similar

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int LOCK_DURATION_MINUTES = 30;

    public CustomerLoginServiceImpl() {
        this.customerLoginDAO = ServiceFactory.getCustomerLoginDAO();
        this.otpDAO=ServiceFactory.getOTPDAO();
    }

    @Override
    public boolean saveCustomerLogin(CustomerLogin customerLogin) {
        try {
            if (customerLogin == null) {
                logger.warning("Cannot save null CustomerLogin");
                return false;
            }
            return customerLoginDAO.save(customerLogin);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving CustomerLogin", e);
            return false;
        }
    }
    
    public boolean verifyOtpForCustomer(String customerId, String enteredOtp) {
        try {
            // Fetch latest OTP for this customer (ignoring purpose)
            OtpRequest otpRequest = otpDAO.getLatestOTPByCustomerId(customerId);

            if (otpRequest == null) {
                logger.warning("No OTP found for customer: " + customerId);
                return false;
            }

            boolean otpMatches = otpRequest.getOtpCode().equals(enteredOtp);
            boolean notExpired = otpRequest.getExpiresAt().isAfter(LocalDateTime.now());
            boolean notUsed = "N".equalsIgnoreCase(otpRequest.getIsUsed());

            return otpMatches && notExpired && notUsed;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error verifying OTP for customer: " + customerId, e);
            return false;
        }
    }

    @Override
    public boolean updateCustomerLogin(CustomerLogin customerLogin) {
        try {
            if (customerLogin == null) {
                logger.warning("Cannot update null CustomerLogin");
                return false;
            }
            return customerLoginDAO.update(customerLogin);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating CustomerLogin", e);
            return false;
        }
    }

    @Override
    public CustomerLogin getByCustomerId(String customerId) {
        try {
            return customerLoginDAO.findByCustomerId(customerId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting CustomerLogin by customerId: " + customerId, e);
            return null;
        }
    }

    @Override
    public CustomerLogin getByUserId(String userId) {
        try {
            return customerLoginDAO.findByUserId(userId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting CustomerLogin by userId: " + userId, e);
            return null;
        }
    }
    public static class AuthResult {
        private boolean success;
        private boolean locked;
        private LocalDateTime lockedUntil;
        private String message;
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public boolean isLocked() {
			return locked;
		}
		public void setLocked(boolean locked) {
			this.locked = locked;
		}
		public LocalDateTime getLockedUntil() {
			return lockedUntil;
		}
		public void setLockedUntil(LocalDateTime lockedUntil) {
			this.lockedUntil = lockedUntil;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}

        // constructor(s), getters, setters
    }

    @Override
    public AuthResult authenticate(String customerId, String password) {
        AuthResult result = new AuthResult();
        try {
            if (customerId == null || password == null) {
                logger.warning("CustomerId or password is null for authentication");
                result.setSuccess(false);
                result.setMessage("Missing credentials");
                return result;
            }

            CustomerLogin cl = customerLoginDAO.findByCustomerId(customerId);
            if (cl == null) {
                logger.info("Authentication failed: customerId not found - " + customerId);
                result.setSuccess(false);
                result.setMessage("Customer ID not found");
                return result;
            }

            // 🔒 Check if locked
            if ("Y".equalsIgnoreCase(cl.getIsLocked())) {
                LocalDateTime lockedUntil = cl.getLockedUntil();
                if (lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now())) {
                    logger.warning("Account is locked for customerId: " + customerId);
                    result.setSuccess(false);
                    result.setLocked(true);
                    result.setLockedUntil(lockedUntil);
                    result.setMessage("Account locked until " + lockedUntil);
                    return result;
                } else {
                    // Lock expired -> unlock
                    customerLoginDAO.unlockAccount(cl.getCustomerId());
                    cl.setIsLocked("N");
                    cl.setLockedUntil(null);
                }
            }

            // 🔑 Check password
            boolean passwordMatches = password.equals(cl.getLoginPassword());

            if (passwordMatches) {
                customerLoginDAO.resetFailedLoginAttempts(cl.getCustomerId());
                customerLoginDAO.updateLastLogin(cl.getCustomerId(), LocalDateTime.now());

                result.setSuccess(true);
                result.setMessage("Login successful");
                return result;
            } else {
                customerLoginDAO.incrementFailedLoginAttempts(cl.getCustomerId());
                int attempts = cl.getFailedLoginAttempts() != null ? cl.getFailedLoginAttempts() + 1 : 1;

                if (attempts >= MAX_FAILED_ATTEMPTS) {
                    LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
                    customerLoginDAO.lockAccount(cl.getCustomerId(), lockUntil);
                    logger.warning("Account locked due to too many failed login attempts: " + customerId);

                    result.setSuccess(false);
                    result.setLocked(true);
                    result.setLockedUntil(lockUntil);
                    result.setMessage("Account locked until " + lockUntil);
                    return result;
                }

                result.setSuccess(false);
                result.setMessage("Invalid password");
                return result;
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during authenticate for customerId: " + customerId, e);
            result.setSuccess(false);
            result.setMessage("Internal error during authentication");
            return result;
        }
    }



    @Override
    public boolean incrementFailedLoginAttempts(String customerId) {
        try {
            return customerLoginDAO.incrementFailedLoginAttempts(customerId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error incrementing failed login attempts for: " + customerId, e);
            return false;
        }
    }

    @Override
    public boolean resetFailedLoginAttempts(String customerId) {
        try {
            return customerLoginDAO.resetFailedLoginAttempts(customerId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error resetting failed login attempts for: " + customerId, e);
            return false;
        }
    }

    @Override
    public boolean lockAccount(String customerId, LocalDateTime lockedUntil) {
        try {
            return customerLoginDAO.lockAccount(customerId, lockedUntil);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error locking account for: " + customerId, e);
            return false;
        }
    }

    @Override
    public boolean unlockAccount(String customerId) {
        try {
            return customerLoginDAO.unlockAccount(customerId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error unlocking account for: " + customerId, e);
            return false;
        }
    }

    @Override
    public boolean updateLastLogin(String customerId, LocalDateTime lastLogin) {
        try {
            return customerLoginDAO.updateLastLogin(customerId, lastLogin);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating last login for: " + customerId, e);
            return false;
        }
    }
    
    @Override
    public List<CustomerLogin> getLockedAccounts() {
        try {
            return customerLoginDAO.findAllLockedAccounts();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching locked accounts", e);
            throw new RuntimeException("Unable to fetch locked accounts", e);
        }
    }

    @Override
    public boolean changeLoginPassword(String customerId, String oldPassword, String newPassword) {
        try {
            CustomerLogin cl = customerLoginDAO.findByCustomerId(customerId);
            if (cl == null) {
                logger.warning("CustomerLogin not found for password change: " + customerId);
                return false;
            }
            // Check old password match, again apply secure hashing in real apps
            if (!oldPassword.equals(cl.getLoginPassword())) {
                logger.warning("Old password does not match for: " + customerId);
                return false;
            }
            return customerLoginDAO.updateLoginPassword(customerId, newPassword);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error changing login password for: " + customerId, e);
            return false;
        }
    }

    @Override
    public boolean changeTransactionPassword(String customerId, String oldPassword, String newPassword) {
        try {
            CustomerLogin cl = customerLoginDAO.findByCustomerId(customerId);
            if (cl == null) {
                logger.warning("CustomerLogin not found for transaction password change: " + customerId);
                return false;
            }
            if (!oldPassword.equals(cl.getTransactionPassword())) {
                logger.warning("Old transaction password does not match for: " + customerId);
                return false;
            }
            return customerLoginDAO.updateTransactionPassword(customerId, newPassword);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error changing transaction password for: " + customerId, e);
            return false;
        }
    }

    @Override
    public boolean isInternetBankingEnabled(String customerId) {
        try {
            return customerLoginDAO.isInternetBankingEnabled(customerId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking internet banking enabled for: " + customerId, e);
            return false;
        }
    }

    @Override
    public boolean enableInternetBanking(String customerId) {
        try {
            return customerLoginDAO.enableInternetBanking(customerId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error enabling internet banking for: " + customerId, e);
            return false;
        }
    }

    @Override
    public boolean disableInternetBanking(String customerId) {
        try {
            return customerLoginDAO.disableInternetBanking(customerId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error disabling internet banking for: " + customerId, e);
            return false;
        }
    }

    @Override
    public boolean updateLoginPassword(String customerId, String newPassword) {
        try {
            CustomerLogin cl = customerLoginDAO.findByCustomerId(customerId);
            if (cl == null) {
                logger.warning("CustomerLogin not found for update password: " + customerId);
                return false;
            }
            return customerLoginDAO.updateLoginPassword(customerId, newPassword);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating login password for: " + customerId, e);
            return false;
        }
    }
    
    @Override
    public boolean updateTransactionPassword(String customerId, String newPassword) {
        try {
            CustomerLogin cl = customerLoginDAO.findByCustomerId(customerId);
            if (cl == null) {
                logger.warning("CustomerLogin not found for update password: " + customerId);
                return false;
            }
            return customerLoginDAO.updateTransactionPassword(customerId, newPassword);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating login password for: " + customerId, e);
            return false;
        }
    }
    
    public static class RegistrationForm {
        private String accountNumber;
        private String loginPassword;
        private String confirmLoginPassword;
        private String transactionPassword;
        private String confirmTransactionPassword;
        private String otp;
        private String customerId;

        // Getters and setters for all fields

        public String getAccountNumber() {
            return accountNumber;
        }
        public String getCustomerId() {
			return customerId;
		}
		public void setCustomerId(String customerId) {
			this.customerId = customerId;
		}
		public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getLoginPassword() {
            return loginPassword;
        }
        public void setLoginPassword(String loginPassword) {
            this.loginPassword = loginPassword;
        }

        public String getConfirmLoginPassword() {
            return confirmLoginPassword;
        }
        public void setConfirmLoginPassword(String confirmLoginPassword) {
            this.confirmLoginPassword = confirmLoginPassword;
        }

        public String getTransactionPassword() {
            return transactionPassword;
        }
        public void setTransactionPassword(String transactionPassword) {
            this.transactionPassword = transactionPassword;
        }

        public String getConfirmTransactionPassword() {
            return confirmTransactionPassword;
        }
        public void setConfirmTransactionPassword(String confirmTransactionPassword) {
            this.confirmTransactionPassword = confirmTransactionPassword;
        }

        public String getOtp() {
            return otp;
        }
        public void setOtp(String otp) {
            this.otp = otp;
        }
    }
    
    public Response registerForInternetBanking(RegistrationForm form) {
        try {
            // 1. Validate OTP (✅ now using simplified method)
            boolean otpValid = verifyOtpForCustomer(form.getCustomerId(), form.getOtp());
            if (!otpValid) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid or expired OTP.")
                    .build();
            }

            // 2. Validate passwords match
            if (!form.getLoginPassword().equals(form.getConfirmLoginPassword())) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Login passwords do not match.")
                    .build();
            }

            if (!form.getTransactionPassword().equals(form.getConfirmTransactionPassword())) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Transaction passwords do not match.")
                    .build();
            }

            // 3. Update passwords in DB (hash passwords inside these methods)
            boolean loginPwdUpdateSuccess = updateLoginPassword(form.getCustomerId(), form.getLoginPassword());
            boolean txnPwdUpdateSuccess = updateTransactionPassword(form.getCustomerId(), form.getTransactionPassword());

            if (!loginPwdUpdateSuccess || !txnPwdUpdateSuccess) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to update passwords.")
                    .build();
            }

            // 4. Enable internet banking service for the customer
            boolean enabled = enableInternetBanking(form.getCustomerId());
            if (!enabled) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to enable internet banking.")
                    .build();
            }

            // 5. Mark OTP as used

            return Response.ok("Internet banking registration successful.").build();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during internet banking registration for customer: " + form.getCustomerId(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Server error occurred.").build();
        }
    }

	@Override
	public String getCustomerEmail(String customerId) {
		// TODO Auto-generated method stub
		return null;
	}

    
    

}
