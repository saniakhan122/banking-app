package com.oracle.controller;

import com.oracle.beans.AccountCreationRequest;
import com.oracle.business.AccountCreationRequestService;
import com.oracle.business.util.ServiceFactory;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/v1/account-requests")
public class AccountCreationRequestRestController {
	
	 public static class ApproveRequestDTO {
	        private String requestId;
	        private String username;

	        public String getRequestId() {
	            return requestId;
	        }
	        public void setRequestId(String requestId) {
	            this.requestId = requestId;
	        }

	        public String getUsername() {
	            return username;
	        }
	        public void setUsername(String username) {
	            this.username = username;
	        }
	    }
	 public static class RejectRequestDTO {
		    private String requestId;
		    private String username;
		    private String reason;

		    public String getRequestId() {
		        return requestId;
		    }
		    public void setRequestId(String requestId) {
		        this.requestId = requestId;
		    }

		    public String getUsername() {
		        return username;
		    }
		    public void setUsername(String username) {
		        this.username = username;
		    }

		    public String getReason() {
		        return reason;
		    }
		    public void setReason(String reason) {
		        this.reason = reason;
		    }
		}
    private AccountCreationRequestService accountReqService = ServiceFactory.getAccountCreationRequestService();
    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRequest(AccountCreationRequest request) {
        // Generate IDs if not provided
        if (request.getRequestId() == null || request.getRequestId().isEmpty()) {
            request.setRequestId("REQ" + System.currentTimeMillis());
        }
        if (request.getServiceReferenceNo() == null || request.getServiceReferenceNo().isEmpty()) {
            request.setServiceReferenceNo("SRV" + System.currentTimeMillis());
        }

        // Set submitted timestamp
        if (request.getSubmittedAt() == null) {
            request.setSubmittedAt(LocalDateTime.now());
        }
        request.setStatus("PENDING");
        request.setInitialDeposit(new BigDecimal("5000"));


        boolean success = accountReqService.createRequest(request);
        if (success) {
            // ✅ Return useful info back to customer
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", true);
            responseMap.put("serviceReferenceNo", request.getServiceReferenceNo());
            responseMap.put("message", "Account creation request submitted successfully");

            return Response.status(Response.Status.CREATED).entity(responseMap).build();
        } else {
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", false);
            responseMap.put("message", "Failed to create account request");

            return Response.status(Response.Status.BAD_REQUEST).entity(responseMap).build();
        }
    }

    @POST
    @Path("/approve")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response approveRequest(ApproveRequestDTO dto) {
        boolean success = accountReqService.approveRequestAndCreateCustomer(dto.getRequestId(), dto.getUsername());
        return Response.status(success ? 200 : 400).entity(success).build();
    }
    
    @POST
    @Path("/reject")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response rejectRequest(RejectRequestDTO dto) {
        boolean success = accountReqService.rejectRequest(dto.getRequestId(), dto.getUsername(), dto.getReason());
        return Response.status(success ? 200 : 400).entity(success).build();
    }




    @GET
    @Path("/status/{status}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRequestsByStatus(@PathParam("status") String status) {
        List<AccountCreationRequest> requests = accountReqService.getRequestsByStatus(status);
        return Response.status(200).entity(requests).build();
    }
    
    @GET
    @Path("/pending")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPendingRequests() {
        List<AccountCreationRequest> requests = accountReqService.getRequestsByStatus("PENDING");

        if (requests == null || requests.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build(); 
            // 204 No Content if nothing found
        }

        return Response.ok(requests).build(); 
        // 200 OK with JSON array of AccountCreationRequest
    }
}
