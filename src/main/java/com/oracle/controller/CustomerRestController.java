package com.oracle.controller;

import com.oracle.beans.AccountCreationRequest;
import com.oracle.beans.Customer;
import com.oracle.beans.OtpRequest;
import com.oracle.business.AccountCreationRequestService;
import com.oracle.business.CustomerLoginService;
import com.oracle.business.CustomerService;
import com.oracle.business.util.EmailUtil;
import com.oracle.business.util.ServiceFactory;
import com.oracle.dao.OTPDAO;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Path("/v1/customers")
public class CustomerRestController {

    private CustomerService customerService = ServiceFactory.getCustomerService();
    private static AccountCreationRequestService accountReqService = ServiceFactory.getAccountCreationRequestService();
private static CustomerLoginService customerLoginService=ServiceFactory.getCustomerLoginService();
    public static class CustomerRequest {
        private String customerId;

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }
    }
    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerCustomer(AccountCreationRequest request) {
        // Set default values just like your console method
    	
        request.setAccountType("SAVINGS");
        request.setInitialDeposit(new BigDecimal("1000"));
        request.setStatus("PENDING");
        request.setSubmittedAt(LocalDateTime.now());
        request.setRequestId("REQ" + System.currentTimeMillis());
        request.setServiceReferenceNo("SRV" + System.currentTimeMillis());

        boolean success = accountReqService.createRequest(request);

        if (success) {
            return Response.status(Response.Status.CREATED)
                    .entity(Map.of(
                        "success", true,
                        "message", "Account creation request submitted successfully!",
                        "serviceReferenceNo", request.getServiceReferenceNo(),
                        "status", request.getStatus()
                    ))
                    .build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "success", false,
                        "message", "Failed to submit account creation request. Please try again."
                    ))
                    .build();
        }

    }


    @POST
    @Path("/customerDetails")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerProfile(CustomerRequest customerRequest) {
        Customer customer = customerService.getCustomerProfile(customerRequest.getCustomerId());
        return Response.status(customer != null ? 200 : 404).entity(customer).build();
    }
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return Response.status(200).entity(customers).build();
    }
    
    // DTO for JSON input
    public static class EmailRequest {
        private String email;
        private String subject;
        private String body;

        // getters and setters
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getSubject() {
            return subject;
        }
        public void setSubject(String subject) {
            this.subject = subject;
        }
        public String getBody() {
            return body;
        }
        public void setBody(String body) {
            this.body = body;
        }
    }
    
    @POST
    @Path("/sendEmail")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendEmailNotification(EmailRequest emailRequest) {
        try {
            EmailUtil.sendEmail(emailRequest.getEmail(), emailRequest.getSubject(), emailRequest.getBody());
            return Response.ok("Email sent successfully").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to send email: " + e.getMessage())
                    .build();
        }
    }
    
   
    
    
}
