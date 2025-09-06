// com.oracle.business.util.ServiceFactory.java
package com.oracle.business.util;
import com.oracle.dao.impl.*;
import com.oracle.business.CustomerService;
import com.oracle.business.OTPService;
import com.oracle.business.SessionService;
import com.oracle.business.TransactionService;
import com.oracle.business.impl.CustomerServiceImpl;
import com.oracle.business.impl.OTPServiceImpl;
import com.oracle.business.impl.SessionServiceImpl;
import com.oracle.business.impl.TransactionServiceImpl;
import com.oracle.business.AccountCreationRequestService;
import com.oracle.business.AdminUserService;
import com.oracle.business.BankingService;
import com.oracle.business.CustomerLoginService;
import com.oracle.business.impl.AccountCreationRequestServiceImpl;
import com.oracle.business.impl.AdminUserServiceImpl;
import com.oracle.business.impl.BankingServiceImpl;
import com.oracle.business.impl.CustomerLoginServiceImpl;
import com.oracle.dao.CustomerDAO;
import com.oracle.dao.CustomerLoginDAO;
import com.oracle.dao.OTPDAO;
import com.oracle.dao.SessionDAO;
import com.oracle.dao.impl.CustomerJPAImpl;
import com.oracle.dao.AccountCreationRequestDAO;
import com.oracle.dao.AdminUserDAO;
import com.oracle.dao.BankAccountDAO;
import com.oracle.dao.impl.AccountCreationRequestJPAImpl;
import com.oracle.dao.impl.AdminUserJPAImpl;
import com.oracle.dao.impl.BankAccountJPAImpl;
import com.oracle.dao.TransactionDAO;
import com.oracle.dao.impl.TransactionJPAImpl;

import java.util.logging.Logger;

/**
 * Factory class implementing Factory Design Pattern
 * Used to create instances of Service and DAO classes
 * Ensures loose coupling between layers
 */
public class ServiceFactory {
    
    private static final Logger logger = Logger.getLogger(ServiceFactory.class.getName());
    
    // Service instances (Singleton pattern)
    private static CustomerService customerService;
    private static BankingService bankingService;
    private static AdminUserService adminService;
    private static AccountCreationRequestService accreqService;
    private static CustomerLoginService loginService;
    private static TransactionService transactionService;
    private static OTPService otpService;
    private static SessionService sessionService;

    
    // DAO instances (Singleton pattern)
    private static CustomerDAO customerDAO;
    private static BankAccountDAO bankAccountDAO;
    private static TransactionDAO transactionDAO;
    private static AdminUserDAO adminDAO;
    private static AccountCreationRequestDAO accreqDAO;
    private static CustomerLoginDAO loginDAO;
    private static OTPDAO otpDAO;
    private static SessionDAO sessionDAO;
    
    // Private constructor to prevent instantiation
    private ServiceFactory() {
        // Utility class - no instantiation needed
    }
    
    // ===== SERVICE FACTORY METHODS =====
    
    /**
     * Get CustomerService instance using Factory Pattern
     * @return CustomerService implementation
     */
    public static CustomerService getCustomerService() {
        if (customerService == null) {
            synchronized (ServiceFactory.class) {
                if (customerService == null) {
                    customerService = new CustomerServiceImpl();
                    logger.info("CustomerService instance created");
                }
            }
        }
        return customerService;
    }
    
    /**
     * Get BankingService instance using Factory Pattern
     * @return BankingService implementation
     */
    public static BankingService getBankingService() {
        if (bankingService == null) {
            synchronized (ServiceFactory.class) {
                if (bankingService == null) {
                    bankingService = new BankingServiceImpl();
                    logger.info("BankingService instance created");
                }
            }
        }
        return bankingService;
    }
    
    public static TransactionService getTransactionService() {
        if (transactionService == null) {
            synchronized (ServiceFactory.class) {
                if (transactionService == null) {
                    transactionService = new TransactionServiceImpl(getTransactionDAO());
                    logger.info("TransactionService instance created");
                }
            }
        }
        return transactionService;
    }
    
    public static AdminUserService getAdminUserService() {
        if (adminService == null) {
            synchronized (ServiceFactory.class) {
                if (adminService == null) {
                    adminService = new AdminUserServiceImpl();
                    logger.info("AdminService instance created");
                }
            }
        }
        return adminService;
    }
    
    public static AccountCreationRequestService getAccountCreationRequestService() {
        if (accreqService == null) {
            synchronized (ServiceFactory.class) {
                if (accreqService == null) {
                    accreqService = new AccountCreationRequestServiceImpl();
                    logger.info("AccountCreationRequestService instance created");
                }
            }
        }
        return accreqService;
    }
    
    public static CustomerLoginService getCustomerLoginService() {
        if (loginService == null) {
            synchronized (ServiceFactory.class) {
                if (loginService == null) {
                    loginService = new CustomerLoginServiceImpl();
                    logger.info("CustomerLoginService instance created");
                }
            }
        }
        return loginService;
    }
    
    public static OTPService getOTPService() {
        if (otpService == null) {
            synchronized (ServiceFactory.class) {
                if (otpService == null) {
                    otpService = new OTPServiceImpl();
                    logger.info("OTPService instance created");
                }
            }
        }
        return otpService;
    }
    
    public static SessionService getSessionService() {
        if (sessionService == null) {
            synchronized (ServiceFactory.class) {
                if (sessionService == null) {
                    sessionService = new SessionServiceImpl();
                    logger.info("SessionService instance created");
                }
            }
        }
        return sessionService;
    }
    
    // ===== DAO FACTORY METHODS =====
    
    /**
     * Get CustomerDAO instance using Factory Pattern
     * @return CustomerDAO implementation
     */
    public static CustomerDAO getCustomerDAO() {
        if (customerDAO == null) {
            synchronized (ServiceFactory.class) {
                if (customerDAO == null) {
                    customerDAO = new CustomerJPAImpl();
                    logger.info("CustomerDAO instance created");
                }
            }
        }
        return customerDAO;
    }
    
    /**
     * Get BankAccountDAO instance using Factory Pattern
     * @return BankAccountDAO implementation
     */
    public static BankAccountDAO getBankAccountDAO() {
        if (bankAccountDAO == null) {
            synchronized (ServiceFactory.class) {
                if (bankAccountDAO == null) {
                    bankAccountDAO = new BankAccountJPAImpl();
                    logger.info("BankAccountDAO instance created");
                }
            }
        }
        return bankAccountDAO;
    }
    
    /**
     * Get TransactionDAO instance using Factory Pattern
     * @return TransactionDAO implementation
     */
    public static TransactionDAO getTransactionDAO() {
        if (transactionDAO == null) {
            synchronized (ServiceFactory.class) {
                if (transactionDAO == null) {
                    transactionDAO = new TransactionJPAImpl();
                    logger.info("TransactionDAO instance created");
                }
            }
        }
        return transactionDAO;
    }
    public static AdminUserDAO getAdminUserDAO() {
        if (transactionDAO == null) {
            synchronized (ServiceFactory.class) {
                if (adminDAO == null) {
                    adminDAO = new AdminUserJPAImpl();
                    logger.info("AdminUserDAO instance created");
                }
            }
        }
        return adminDAO;
    }
    
    public static AccountCreationRequestDAO getAccountCreationRequestDAO() {
        if (accreqDAO == null) {
            synchronized (ServiceFactory.class) {
                if (accreqDAO == null) {
                	accreqDAO = new AccountCreationRequestJPAImpl();
                    logger.info("AccountCreationRequestDAO instance created");
                }
            }
        }
        return accreqDAO;
    }
    
    public static CustomerLoginDAO getCustomerLoginDAO() {
        if (loginDAO == null) {
            synchronized (ServiceFactory.class) {
                if (loginDAO == null) {
                	loginDAO = new CustomerLoginJPAImpl();
                    logger.info("CustomerLoginDAO instance created");
                }
            }
        }
        return loginDAO;
    }
    
    public static OTPDAO getOTPDAO() {
        if (otpDAO == null) {
            synchronized (ServiceFactory.class) {
                if (otpDAO == null) {
                	otpDAO = new OTPJPAImpl();
                    logger.info("OTPDAO instance created");
                }
            }
        }
        return otpDAO;
    }
    
    
    public static SessionDAO getSessionDAO() {
        if (sessionDAO == null) {
            synchronized (ServiceFactory.class) {
                if (sessionDAO == null) {
                	sessionDAO = new SessionJPAImpl();
                    logger.info("SessionDAO instance created");
                }
            }
        }
        return sessionDAO;
    }
    









}
    


    
   