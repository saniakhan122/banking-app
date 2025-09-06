package com.oracle.business;

import com.oracle.beans.AccountCreationRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface AccountCreationRequestService {

    // Create & Update
    boolean createRequest(AccountCreationRequest request);
    boolean updateRequest(AccountCreationRequest request);

    // Basic retrieval
    AccountCreationRequest getRequestById(String requestId);
    AccountCreationRequest getRequestByServiceReferenceNo(String serviceReferenceNo);

    // Search filters
    List<AccountCreationRequest> getRequestsByStatus(String status);
    List<AccountCreationRequest> getRequestsByEmail(String email);
    List<AccountCreationRequest> getRequestsByMobile(String mobileNumber);
    List<AccountCreationRequest> getRequestsByAadhar(String aadharNumber);

    // Admin actions
//    boolean approveRequest(String requestId, String adminId);
    boolean approveRequestAndCreateCustomer(String requestId, String username);
    boolean rejectRequest(String requestId, String username, String rejectionReason);

    // Bulk retrieval
    List<AccountCreationRequest> getAllRequests();
    List<AccountCreationRequest> getRequestsSubmittedBetween(LocalDateTime start, LocalDateTime end);

    // Delete
    boolean deleteRequest(String requestId);
}
