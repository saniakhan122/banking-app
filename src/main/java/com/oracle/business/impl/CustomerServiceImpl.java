// com.oracle.business.CustomerServiceImpl.java
package com.oracle.business.impl;

import com.oracle.beans.Customer;
import com.oracle.dao.CustomerDAO;
import com.oracle.business.CustomerService;
import com.oracle.business.util.ServiceFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class CustomerServiceImpl implements CustomerService {
    
    private static final Logger logger = Logger.getLogger(CustomerServiceImpl.class.getName());
    private CustomerDAO customerDAO;
    
    // Email and mobile validation patterns
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern MOBILE_PATTERN = 
        Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern AADHAR_PATTERN = 
        Pattern.compile("^[0-9]{12}$");
    
    public CustomerServiceImpl() {
        this.customerDAO = ServiceFactory.getCustomerDAO();
    }
    
    @Override
    public String registerCustomer(Customer customer) {
        try {
            // Validate customer data
            if (!validateCustomerData(customer)) {
                logger.warning("Invalid customer data provided for registration");
                return "VALIDATION_FAILED";
            }
            
            // Check if customer already exists
//            if (isEmailAlreadyRegistered(customer.getEmail())) {
//                logger.warning("Email already registered: " + customer.getEmail());
//                return "EMAIL_EXISTS";
//            }
            
            if (isMobileAlreadyRegistered(customer.getMobileNumber())) {
                logger.warning("Mobile already registered: " + customer.getMobileNumber());
                return "MOBILE_EXISTS";
            }
            
            if (isAadharAlreadyRegistered(customer.getAadharNumber())) {
                logger.warning("Aadhar already registered: " + customer.getAadharNumber());
                return "AADHAR_EXISTS";
            }
            
            // Generate customer ID if not provided
            if (customer.getCustomerId() == null || customer.getCustomerId().isEmpty()) {
                customer.setCustomerId(generateCustomerId());
            }
            
            // Generate service reference number
            customer.setServiceReferenceNo(generateServiceReferenceNumber());
            
            // Set default status
            customer.setStatus("ACTIVE");
            customer.setCreatedAt(LocalDateTime.now());
            
            // Create customer
            if (customerDAO.createCustomer(customer)) {
                logger.info("Customer registered successfully: " + customer.getCustomerId());
                return "SUCCESS";
            } else {
                logger.severe("Failed to create customer in database");
                return "DATABASE_ERROR";
            }
            
        } catch (Exception e) {
            logger.severe("Error in customer registration: " + e.getMessage());
            return "SYSTEM_ERROR";
        }
    }
    
    @Override
    public boolean validateCustomerData(Customer customer) {
        try {
            // Check null values
            if (customer == null) {
                return false;
            }
            
            if (customer.getFullName() == null || customer.getFullName().trim().isEmpty()) {
                logger.warning("Full name is required");
                return false;
            }
            
            if (customer.getEmail() == null || !EMAIL_PATTERN.matcher(customer.getEmail()).matches()) {
                logger.warning("Valid email is required");
                return false;
            }
            
            if (customer.getMobileNumber() == null || !MOBILE_PATTERN.matcher(customer.getMobileNumber()).matches()) {
                logger.warning("Valid mobile number is required");
                return false;
            }
            
            if (customer.getAadharNumber() == null || !AADHAR_PATTERN.matcher(customer.getAadharNumber()).matches()) {
                logger.warning("Valid Aadhar number is required");
                return false;
            }
            
            if (customer.getDateOfBirth() == null || customer.getDateOfBirth().isAfter(LocalDate.now().minusYears(18))) {
                logger.warning("Customer must be at least 18 years old");
                return false;
            }
            
            if (customer.getResidentialAddress() == null || customer.getResidentialAddress().trim().isEmpty()) {
                logger.warning("Residential address is required");
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            logger.severe("Error validating customer data: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public Customer getCustomerProfile(String customerId) {
        try {
            if (customerId == null || customerId.trim().isEmpty()) {
                logger.warning("Customer ID is required");
                return null;
            }
            
            Customer customer = customerDAO.findCustomerById(customerId);
            if (customer != null) {
                logger.info("Customer profile retrieved: " + customerId);
            }
            return customer;
            
        } catch (Exception e) {
            logger.severe("Error retrieving customer profile: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public boolean updateCustomerProfile(Customer customer) {
        try {
            if (customer == null || customer.getCustomerId() == null) {
                logger.warning("Invalid customer data for update");
                return false;
            }
            
            // Check if customer exists
            if (!isCustomerExists(customer.getCustomerId())) {
                logger.warning("Customer not found: " + customer.getCustomerId());
                return false;
            }
            
            // Validate updated data
            if (!validateCustomerData(customer)) {
                logger.warning("Invalid customer data for update");
                return false;
            }
            
            return customerDAO.updateCustomer(customer);
            
        } catch (Exception e) {
            logger.severe("Error updating customer profile: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean changeCustomerStatus(String customerId, String status) {
        try {
            if (customerId == null || status == null) {
                return false;
            }
            
            // Validate status
            if (!status.equals("ACTIVE") && !status.equals("INACTIVE") && !status.equals("SUSPENDED")) {
                logger.warning("Invalid status: " + status);
                return false;
            }
            
            return customerDAO.updateCustomerStatus(customerId, status);
            
        } catch (Exception e) {
            logger.severe("Error changing customer status: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public Customer findCustomerByEmail(String email) {
        try {
            if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
                return null;
            }
            return customerDAO.findCustomerByEmail(email);
        } catch (Exception e) {
            logger.severe("Error finding customer by email: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public Customer findCustomerByMobile(String mobileNumber) {
        try {
            if (mobileNumber == null || !MOBILE_PATTERN.matcher(mobileNumber).matches()) {
                return null;
            }
            return customerDAO.findCustomerByMobile(mobileNumber);
        } catch (Exception e) {
            logger.severe("Error finding customer by mobile: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public Customer findCustomerByAadhar(String aadharNumber) {
        try {
            if (aadharNumber == null || !AADHAR_PATTERN.matcher(aadharNumber).matches()) {
                return null;
            }
            return customerDAO.findCustomerByAadhar(aadharNumber);
        } catch (Exception e) {
            logger.severe("Error finding customer by Aadhar: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public List<Customer> getAllCustomers() {
        try {
            return customerDAO.findAllCustomers();
        } catch (Exception e) {
            logger.severe("Error getting all customers: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public List<Customer> getActiveCustomers() {
        try {
            return customerDAO.findCustomersByStatus("ACTIVE");
        } catch (Exception e) {
            logger.severe("Error getting active customers: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public List<Customer> getInactiveCustomers() {
        try {
            return customerDAO.findCustomersByStatus("INACTIVE");
        } catch (Exception e) {
            logger.severe("Error getting inactive customers: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public boolean isCustomerExists(String customerId) {
        return customerDAO.customerExists(customerId);
    }
    
    @Override
    public boolean isEmailAlreadyRegistered(String email) {
        return customerDAO.emailExists(email);
    }
    
    @Override
    public boolean isMobileAlreadyRegistered(String mobileNumber) {
        return customerDAO.mobileExists(mobileNumber);
    }
    
    @Override
    public boolean isAadharAlreadyRegistered(String aadharNumber) {
        return customerDAO.aadharExists(aadharNumber);
    }
    
    @Override
    public boolean isCustomerActive(String customerId) {
        Customer customer = getCustomerProfile(customerId);
        return customer != null && "ACTIVE".equals(customer.getStatus());
    }
    
    @Override
    public boolean deactivateCustomer(String customerId, String reason) {
        try {
            logger.info("Deactivating customer: " + customerId + ", Reason: " + reason);
            return changeCustomerStatus(customerId, "INACTIVE");
        } catch (Exception e) {
            logger.severe("Error deactivating customer: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean reactivateCustomer(String customerId) {
        try {
            logger.info("Reactivating customer: " + customerId);
            return changeCustomerStatus(customerId, "ACTIVE");
        } catch (Exception e) {
            logger.severe("Error reactivating customer: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String generateCustomerId() {
        // Generate customer ID with format: CUST + timestamp + random
        return "CUST" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
    
    @Override
    public String generateServiceReferenceNumber() {
        // Generate service reference with format: SRV + timestamp + UUID
        return "SRV" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
    
    @Override
    public long getTotalCustomerCount() {
        try {
            return customerDAO.getTotalCustomerCount();
        } catch (Exception e) {
            logger.severe("Error getting customer count: " + e.getMessage());
            return 0;
        }
    }
    
    @Override
    public long getActiveCustomerCount() {
        try {
            List<Customer> activeCustomers = getActiveCustomers();
            return activeCustomers != null ? activeCustomers.size() : 0;
        } catch (Exception e) {
            logger.severe("Error getting active customer count: " + e.getMessage());
            return 0;
        }
    }
    
    @Override
    public List<Customer> getRecentlyJoinedCustomers(int limit) {
        try {
            List<Customer> allCustomers = getAllCustomers();
            if (allCustomers != null && allCustomers.size() > limit) {
                return allCustomers.subList(0, limit);
            }
            return allCustomers;
        } catch (Exception e) {
            logger.severe("Error getting recently joined customers: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public boolean updateCustomerContact(String customerId, String newEmail, String newMobile) {
        try {
            // Validate inputs
            if (customerId == null || newEmail == null || newMobile == null) {
                return false;
            }
            
            if (!EMAIL_PATTERN.matcher(newEmail).matches()) {
                logger.warning("Invalid email format: " + newEmail);
                return false;
            }
            
            if (!MOBILE_PATTERN.matcher(newMobile).matches()) {
                logger.warning("Invalid mobile format: " + newMobile);
                return false;
            }
            
            // Check if email/mobile already exists for other customers
            Customer existingEmailCustomer = findCustomerByEmail(newEmail);
            if (existingEmailCustomer != null && !existingEmailCustomer.getCustomerId().equals(customerId)) {
                logger.warning("Email already exists for another customer");
                return false;
            }
            
            Customer existingMobileCustomer = findCustomerByMobile(newMobile);
            if (existingMobileCustomer != null && !existingMobileCustomer.getCustomerId().equals(customerId)) {
                logger.warning("Mobile already exists for another customer");
                return false;
            }
            
            return customerDAO.updateCustomerContact(customerId, newEmail, newMobile);
            
        } catch (Exception e) {
            logger.severe("Error updating customer contact: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean verifyCustomerDetails(String customerId, String aadharNumber, String mobileNumber) {
        try {
            Customer customer = getCustomerProfile(customerId);
            if (customer == null) {
                return false;
            }
            
            return customer.getAadharNumber().equals(aadharNumber) && 
                   customer.getMobileNumber().equals(mobileNumber);
                   
        } catch (Exception e) {
            logger.severe("Error verifying customer details: " + e.getMessage());
            return false;
        }
    }
}