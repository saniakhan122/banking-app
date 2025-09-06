package com.oracle.dao;

import com.oracle.beans.AccountCreationRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface AccountCreationRequestDAO {

    // Create & Update
    boolean save(AccountCreationRequest request);
    boolean update(AccountCreationRequest request);

    // Basic Retrieval
    AccountCreationRequest findByRequestId(String requestId);
    AccountCreationRequest findByServiceReferenceNo(String serviceRefNo);

    // Search Filters
    List<AccountCreationRequest> findByStatus(String status);
    List<AccountCreationRequest> findByEmail(String email);
    List<AccountCreationRequest> findByMobile(String mobileNumber);
    List<AccountCreationRequest> findByAadhar(String aadharNumber);

    // Admin Actions
    boolean approveRequest(String requestId, String username);
    boolean rejectRequest(String requestId, String username, String rejectionReason);

    // Bulk Retrieval
    List<AccountCreationRequest> findAll();
    List<AccountCreationRequest> findSubmittedBetween(LocalDateTime start, LocalDateTime end);

    // Delete
    boolean delete(String requestId);
}
