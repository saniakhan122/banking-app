package com.oracle.dao.util;

import com.oracle.dao.*;
import com.oracle.dao.impl.*;

public class DAOFactory {
    
    private static DAOFactory instance;
    
    private DAOFactory() {}
    
    public static DAOFactory getInstance() {
        if (instance == null) {
            synchronized (DAOFactory.class) {
                if (instance == null) {
                    instance = new DAOFactory();
                }
            }
        }
        return instance;
    }
    
    public AdminUserDAO getAdminUserDAO() {
        return new AdminUserJPAImpl();
    }
    
 
    
    public CustomerDAO getCustomerDAO() {
        return new CustomerJPAImpl();
    }
    
    public BankAccountDAO getBankAccountDAO() {
        return new BankAccountJPAImpl();
    }
    

    public TransactionDAO getTransactionDAO() {
        return new TransactionJPAImpl();
    }
    
    public OTPDAO getOtpRequestDAO() {
        return new OTPJPAImpl();
    }
}