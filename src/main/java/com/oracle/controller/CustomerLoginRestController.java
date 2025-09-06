package com.oracle.controller;

import java.util.List;
import java.util.Map;

import com.oracle.beans.Customer;
import com.oracle.beans.CustomerLogin;
import com.oracle.business.CustomerLoginService;
import com.oracle.business.CustomerService;
import com.oracle.business.impl.CustomerLoginServiceImpl.AuthResult;
import com.oracle.business.impl.CustomerLoginServiceImpl.RegistrationForm;
import com.oracle.business.util.EmailUtil;
import com.oracle.business.util.ServiceFactory;

import jakarta.mail.MessagingException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;



@Path("/v1/customer-login")
public class CustomerLoginRestController {

    private CustomerLoginService customerLoginService = ServiceFactory.getCustomerLoginService();
    private CustomerService customerService = ServiceFactory.getCustomerService();

    
    
 // DTO to accept input JSON
    public static class UnlockRequest {
        public String customerId;

		public String getCustomerId() {
			return customerId;
		}

		public void setCustomerId(String customerId) {
			this.customerId = customerId;
		}
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(Map<String, Object> jsonBody) {
        if (jsonBody == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("error", "Missing request body"))
                           .build();
        }

        String customerId = (String) jsonBody.get("customerId");
        String password = (String) jsonBody.get("password");

        if (customerId == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("error", "Missing credentials"))
                           .build();
        }

        AuthResult auth = customerLoginService.authenticate(customerId, password);

        if (auth.isSuccess()) {
            return Response.ok(Map.of(
                "success", true,
                "message", auth.getMessage(),
                "customerId", customerId
            )).build();
        } else if (auth.isLocked()) {
            return Response.status(Response.Status.FORBIDDEN)
                           .entity(Map.of(
                               "success", false,
                               "error", "Account locked until " + auth.getLockedUntil()
                           )).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity(Map.of(
                               "success", false,
                               "error", auth.getMessage()
                           )).build();
        }}




    @POST
    @Path("/{customerId}/change-password")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response changeLoginPassword(@PathParam("customerId") String customerId,
                                        @FormParam("oldPassword") String oldPassword,
                                        @FormParam("newPassword") String newPassword) {
        boolean success = customerLoginService.changeLoginPassword(customerId, oldPassword, newPassword);
        return Response.status(success ? 200 : 400).entity(success).build();
    }

    @POST
    @Path("/{customerId}/reset-password")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response resetLoginPassword(@PathParam("customerId") String customerId,
                                       @FormParam("newPassword") String newPassword) {
        boolean success = customerLoginService.updateLoginPassword(customerId, newPassword);
        return Response.status(success ? 200 : 400).entity(success).build();
    }
    
    @GET
    @Path("/locked-accounts")
    public Response getLockedAccounts() {
        List<CustomerLogin> lockedAccounts = customerLoginService.getLockedAccounts();
        return Response.ok(lockedAccounts).build();
    }
    
    
    @POST
    @Path("/unlock")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unlockAccount(UnlockRequest request) {
        if (request == null || request.customerId == null || request.customerId.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"message\": \"customerId is required\"}")
                           .build();
        }

        try {
            boolean success = customerLoginService.unlockAccount(request.customerId);

            if (success) {
                return Response.ok()
                               .entity("{\"message\": \"Account unlocked successfully\"}")
                               .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"message\": \"Account not found or already unlocked\"}")
                               .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"message\": \"Internal error unlocking account\"}")
                           .build();
        }
    }
    
    
    @POST
    @Path("/change-password")
    public Response changePassword(Map<String, Object> jsonBody) {
        if (jsonBody == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("success", false, "message", "Missing request body"))
                           .build();
        }

        String customerId = (String) jsonBody.get("customerId");
        String oldPassword = (String) jsonBody.get("oldPassword");
        String newPassword = (String) jsonBody.get("newPassword");

        if (customerId == null || oldPassword == null || newPassword == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("success", false, "message", "Missing required fields"))
                           .build();
        }

        try {
            boolean changed = customerLoginService.changeLoginPassword(customerId, oldPassword, newPassword);
            if (changed) {
                return Response.ok(Map.of(
                    "success", true,
                    "message", "Password changed successfully"
                )).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                               .entity(Map.of(
                                   "success", false,
                                   "message", "Old password is incorrect or unable to change password"
                               )).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Map.of("success", false, "message", "Internal server error"))
                           .build();
        }
    }
    

    @POST
    @Path("/register-internet-banking")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerInternetBanking(Map<String, Object> jsonBody) {
        if (jsonBody == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("success", false, "message", "Missing request body"))
                           .build();
        }

        String customerId = (String) jsonBody.get("customerId");
        Object accNoObj = jsonBody.get("accountNumber");
        String accountNumber = accNoObj == null ? null : accNoObj.toString();


        String loginPassword = (String) jsonBody.get("loginPassword");
        String confirmLoginPassword = (String) jsonBody.get("confirmLoginPassword");
        String transactionPassword = (String) jsonBody.get("transactionPassword");
        String confirmTransactionPassword = (String) jsonBody.get("confirmTransactionPassword");
        String otp = (String) jsonBody.get("otp");

        if (customerId == null || loginPassword == null || confirmLoginPassword == null ||
            transactionPassword == null || confirmTransactionPassword == null || otp == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("success", false, "message", "Missing required fields"))
                           .build();
        }

        try {
            RegistrationForm form = new RegistrationForm();
            form.setCustomerId(customerId);
            form.setAccountNumber(accountNumber);
            form.setLoginPassword(loginPassword);
            form.setConfirmLoginPassword(confirmLoginPassword);
            form.setTransactionPassword(transactionPassword);
            form.setConfirmTransactionPassword(confirmTransactionPassword);
            form.setOtp(otp);

            // ✅ Now uses simplified OTP validation (no purpose)
            boolean otpValid = customerLoginService.verifyOtpForCustomer(customerId, otp);
            if (!otpValid) {
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity(Map.of("success", false, "message", "Invalid or expired OTP"))
                               .build();
            }

            // Continue with registration
            Response serviceResponse = customerLoginService.registerForInternetBanking(form);

            if (serviceResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                return Response.ok(Map.of(
                    "success", true,
                    "message", "Internet banking registration successful! Now you can access online banking services after logging in with your new password."
                )).build();
            } else {
                return Response.status(serviceResponse.getStatus())
                               .entity(Map.of(
                                   "success", false,
                                   "message", serviceResponse.getEntity().toString()
                               )).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Map.of("success", false, "message", "Internal server error"))
                           .build();
        }
    }
    


    public static class CustomerIdRequest {
        private String customerId;
        // Getter and setter
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
    }

    @POST
    @Path("/is-internet-banking-enabled")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response isInternetBankingEnabled(CustomerIdRequest request) {
        try {
            boolean enabled = customerLoginService.isInternetBankingEnabled(request.getCustomerId());
            // Return as JSON object
            return Response.ok("{\"enabled\":" + enabled + "}").build();
            // Or, as a POJO:
            // return Response.ok(Collections.singletonMap("enabled", enabled)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Server error.\"}")
                .build();
        }
    }
    

 // DTO classes for requests
 public static class ForgotCustomerIdRequest {
     private String email;
     
     public String getEmail() { return email; }
     public void setEmail(String email) { this.email = email; }
 }

 public static class ForgotPasswordRequest {
     private String customerId;
     
     public String getCustomerId() { return customerId; }
     public void setCustomerId(String customerId) { this.customerId = customerId; }
 }

 @POST
 @Path("/forgot-customer-id")
 @Consumes(MediaType.APPLICATION_JSON)
 @Produces(MediaType.APPLICATION_JSON)
 public Response forgotCustomerId(ForgotCustomerIdRequest request) {
     if (request == null || request.getEmail() == null || request.getEmail().trim().isEmpty()) {
         return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("success", false, "message", "Email is required"))
                        .build();
     }

     try {
         String email = request.getEmail().trim();
         
         // Find customer by email using existing service method
         Customer login = customerService.findCustomerByEmail(email);
         
         if (login != null) {
             // Send customer ID via email using EmailUtil
             String subject = "Your Customer ID - Oracle Banking";
             String messageBody = "Dear Customer,\n\n" +
                                "Your Customer ID is: " + login.getCustomerId() + "\n\n" +
                                "Please use this Customer ID to access your account.\n\n" +
                                "Best regards,\n" +
                                "Oracle Banking Team";
             
             try {
                 EmailUtil.sendEmail(email, subject, messageBody);
                 return Response.ok(Map.of(
                     "success", true,
                     "message", "Customer ID has been sent to your registered email address"
                 )).build();
             } catch (MessagingException e) {
                 return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                .entity(Map.of("success", false, "message", "Failed to send email"))
                                .build();
             }
         } else {
             return Response.status(Response.Status.NOT_FOUND)
                            .entity(Map.of("success", false, "message", "No customer found with that email"))
                            .build();
         }
     } catch (Exception e) {
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(Map.of("success", false, "message", "Internal server error"))
                        .build();
     }
 }

 @POST
 @Path("/forgot-password")
 @Consumes(MediaType.APPLICATION_JSON)
 @Produces(MediaType.APPLICATION_JSON)
 public Response forgotPassword(ForgotPasswordRequest request) {
     if (request == null || request.getCustomerId() == null || request.getCustomerId().trim().isEmpty()) {
         return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("success", false, "message", "Customer ID is required"))
                        .build();
     }

     try {
         String customerId = request.getCustomerId().trim();
         
         // Check if customer exists
         CustomerLogin login = customerLoginService.getByCustomerId(customerId);
         
         if (login != null) {
             // Generate temporary password
             String tempPassword = generateRandomPassword(10);
             
             // Update password in database
             boolean passwordUpdated = customerLoginService.updateLoginPassword(customerId, tempPassword);
             
             if (passwordUpdated) {
                 // Reset failed login attempts and unlock account
                 customerLoginService.resetFailedLoginAttempts(customerId);
                 customerLoginService.unlockAccount(customerId);
                 
                 // Get customer email using existing customerService
                 Customer customer = customerService.getCustomerProfile(customerId);
                 
                 if (customer != null && customer.getEmail() != null) {
                     String customerEmail = customer.getEmail();
                     
                     // Send temporary password via email using EmailUtil
                     String subject = "Temporary Password - Oracle Banking";
                     String messageBody = "Dear Customer,\n\n" +
                                        "Your temporary password is: " + tempPassword + "\n\n" +
                                        "Please login and change it immediately for security purposes.\n\n" +
                                        "Best regards,\n" +
                                        "Oracle Banking Team";
                     
                     try {
                         EmailUtil.sendEmail(customerEmail, subject, messageBody);
                         return Response.ok(Map.of(
                             "success", true,
                             "message", "A temporary password has been sent to your registered email address. Please login and change it immediately."
                         )).build();
                     } catch (MessagingException e) {
                         return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(Map.of("success", false, "message", "Password reset but failed to send email"))
                                        .build();
                     }
                 } else {
                     return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                    .entity(Map.of("success", false, "message", "Customer email not found"))
                                    .build();
                 }
             } else {
                 return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                .entity(Map.of("success", false, "message", "Failed to reset password"))
                                .build();
             }
         } else {
             return Response.status(Response.Status.NOT_FOUND)
                            .entity(Map.of("success", false, "message", "Customer ID not found"))
                            .build();
         }
     } catch (Exception e) {
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(Map.of("success", false, "message", "Internal server error"))
                        .build();
     }
 }

 // Utility method to generate random password
 private String generateRandomPassword(int length) {
     String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
     StringBuilder password = new StringBuilder();
     java.util.Random random = new java.util.Random();
     
     for (int i = 0; i < length; i++) {
         password.append(chars.charAt(random.nextInt(chars.length())));
     }
     
     return password.toString();
 }

}
