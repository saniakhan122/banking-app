// com.oracle.dao.CustomerDAO.java
package com.oracle.dao;

import com.oracle.beans.Customer;

import java.time.LocalDate;
import java.util.List;

public interface CustomerDAO {

    // ===== CREATE =====
    boolean createCustomer(Customer customer);

    // ===== READ =====
    Customer findCustomerById(String customerId);
    Customer findCustomerByEmail(String email);
    Customer findCustomerByMobile(String mobileNumber);
    Customer findCustomerByAadhar(String aadharNumber);
    List<Customer> findAllCustomers();
    List<Customer> findCustomersByStatus(String status);
    List<Customer> findCreatedBetween(LocalDate startDate, LocalDate endDate);

    // ===== UPDATE =====
    boolean updateCustomer(Customer customer);
    boolean updateCustomerStatus(String customerId, String status);
    boolean updateCustomerContact(String customerId, String email, String mobile);

    // ===== DELETE =====
    boolean deleteCustomer(String customerId);             // Hard delete
    boolean softDeleteCustomer(String customerId);         // Set status to INACTIVE

    // ===== VALIDATION / CHECKS =====
    boolean customerExists(String customerId);
    boolean emailExists(String email);
    boolean mobileExists(String mobileNumber);
    boolean aadharExists(String aadharNumber);

    // ===== STATISTICS =====
    long getTotalCustomerCount();
}