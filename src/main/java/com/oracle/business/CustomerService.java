// com.oracle.business.CustomerService.java
package com.oracle.business;

import com.oracle.beans.Customer;
import java.util.List;

public interface CustomerService {
    
    // Customer Registration & Management
    String registerCustomer(Customer customer);
    boolean validateCustomerData(Customer customer);
    Customer getCustomerProfile(String customerId);
    boolean updateCustomerProfile(Customer customer);
    boolean changeCustomerStatus(String customerId, String status);
    
    // Customer Search Operations
    Customer findCustomerByEmail(String email);
    Customer findCustomerByMobile(String mobileNumber);
    Customer findCustomerByAadhar(String aadharNumber);
    List<Customer> getAllCustomers();
    List<Customer> getActiveCustomers();
    List<Customer> getInactiveCustomers();
    
    // Customer Validation Services
    boolean isCustomerExists(String customerId);
    boolean isEmailAlreadyRegistered(String email);
    boolean isMobileAlreadyRegistered(String mobileNumber);
    boolean isAadharAlreadyRegistered(String aadharNumber);
    boolean isCustomerActive(String customerId);
    
    // Customer Business Logic
    boolean deactivateCustomer(String customerId, String reason);
    boolean reactivateCustomer(String customerId);
    String generateCustomerId();
    String generateServiceReferenceNumber();
    
    // Customer Statistics
    long getTotalCustomerCount();
    long getActiveCustomerCount();
    List<Customer> getRecentlyJoinedCustomers(int limit);
    
    // Customer Contact Update
    boolean updateCustomerContact(String customerId, String newEmail, String newMobile);
    
    // Customer Verification
    boolean verifyCustomerDetails(String customerId, String aadharNumber, String mobileNumber);
}