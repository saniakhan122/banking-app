package com.oracle.controller;

import com.oracle.beans.BankAccount;
import com.oracle.beans.Transaction;
import com.oracle.business.BankingService;
import com.oracle.business.util.ServiceFactory;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Path("/v1/banking")
public class BankingRestController {

    private BankingService bankingService = ServiceFactory.getBankingService();
    // DTO for request body
    public static class BalanceRequest {
        public String accountNumber;
        public String customerId;
    }
    
    // ✅ DTO for Statement Request
    public static class AccountStatementRequest {
        private String accountNumber;
        private String fromDate; // format: yyyy-MM-dd
        private String toDate;   // format: yyyy-MM-dd
        public String customerId;


        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

        public String getFromDate() { return fromDate; }
        public void setFromDate(String fromDate) { this.fromDate = fromDate; }

        public String getToDate() { return toDate; }
        public void setToDate(String toDate) { this.toDate = toDate; }
        
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
    }
    
    public static class AccountOwnershipRequest {
        private String accountNumber;
        private String customerId;

        // *** Add this default constructor ***
        public AccountOwnershipRequest() {
        }

        // Getters and setters
        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
    }
    
    public static class CustomerRequest {
        private String customerId;

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }
    }
    
    public static class DepositRequest {
        private String accountNumber;
        private java.math.BigDecimal amount;
        private String description;
        private String customerId;


        public DepositRequest() {} // Default constructor required for JSON-B

        // Getters and setters
        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

        public java.math.BigDecimal getAmount() { return amount; }
        public void setAmount(java.math.BigDecimal amount) { this.amount = amount; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
    }
    
    public static class WithdrawRequest {
        private String accountNumber;
        private java.math.BigDecimal amount;
        private String description;
        private String customerId;


        public WithdrawRequest() {} // Default constructor for JSON-B

        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

        public java.math.BigDecimal getAmount() { return amount; }
        public void setAmount(java.math.BigDecimal amount) { this.amount = amount; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
    }

    @POST
    @Path("/fund-transfer")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response transferFunds(Map<String, Object> jsonBody) {
        if (jsonBody == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("error", "Missing request body"))
                           .build();
        }

        String fromAccount = (String) jsonBody.get("fromAccount");
        String toAccount   = (String) jsonBody.get("toAccount");
        String customerId  = (String) jsonBody.get("customerId");  // <-- passed from frontend/session

        BigDecimal amount = null;
        try {
            Object amtObj = jsonBody.get("amount");
            if (amtObj instanceof Number) {
                amount = new BigDecimal(((Number) amtObj).toString());
            } else if (amtObj instanceof String) {
                amount = new BigDecimal((String) amtObj);
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("error", "Invalid amount format"))
                           .build();
        }

        String method  = (String) jsonBody.get("method");
        String remarks = (String) jsonBody.getOrDefault("remarks", "");

        if (fromAccount == null || toAccount == null || amount == null || method == null || customerId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("error", "Missing required parameters"))
                           .build();
        }

        try {
            // 🔒 Ownership validation
            boolean isOwner = bankingService.doesAccountBelongToCustomer(fromAccount, customerId);
            if (!isOwner) {
                return Response.status(Response.Status.FORBIDDEN)
                               .entity(Map.of("success", false, "error", "You are not authorized to transfer funds from this account."))
                               .build();
            }

            // 🔄 Proceed with transfer
            String result = bankingService.transferFunds(fromAccount, toAccount, amount, method, remarks);

            switch (result) {
                case "SUCCESS":
                    return Response.ok(Map.of("success", true, "message", "Transfer successful.")).build();
                case "INVALID_AMOUNT_FOR_METHOD":
                    return Response.status(Response.Status.BAD_REQUEST)
                                   .entity(Map.of("success", false, "error", "Invalid amount for the selected transfer method."))
                                   .build();
                case "DAILY_LIMIT_EXCEEDED":
                    return Response.status(Response.Status.BAD_REQUEST)
                                   .entity(Map.of("success", false, "error", "Daily transfer limit exceeded."))
                                   .build();
                case "VALIDATION_FAILED":
                    return Response.status(Response.Status.BAD_REQUEST)
                                   .entity(Map.of("success", false, "error", "Validation failed. Please check account details and balance."))
                                   .build();
                case "TRANSFER_FAILED":
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                   .entity(Map.of("success", false, "error", "Transfer failed due to system error."))
                                   .build();
                default:
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                   .entity(Map.of("success", false, "error", "Unknown error: " + result))
                                   .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Map.of("success", false, "error", "Internal server error."))
                           .build();
        }
    }

    @POST
    @Path("/balance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBalance(BalanceRequest request) {
        if (request == null ||
            request.accountNumber == null || request.accountNumber.isEmpty() ||
            request.customerId == null || request.customerId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("success", false, "message", "Account number and customerId are required"))
                           .build();
        }

        try {
            // 🔒 Ownership check
            boolean isOwner = bankingService.doesAccountBelongToCustomer(request.accountNumber, request.customerId);
            if (!isOwner) {
                return Response.status(Response.Status.FORBIDDEN)
                               .entity(Map.of("success", false, "message", "You are not authorized to view this account balance."))
                               .build();
            }

            // ✅ Fetch balance only if owner
            BigDecimal balance = bankingService.getAccountBalance(request.accountNumber);

            if (balance != null) {
                return Response.ok(Map.of(
                    "success", true,
                    "accountNumber", request.accountNumber,
                    "balance", balance
                )).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity(Map.of(
                                   "success", false,
                                   "message", "Account not found or balance unavailable"
                               ))
                               .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Map.of("success", false, "message", "Internal server error"))
                           .build();
        }
    }



    
//    @GET
//    @Path("/{userId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getAccountsByUserId(@PathParam("userId") String userId) {
//        List<BankAccount> accounts = bankingService.getCustomerAccountsByUserId(userId);
//
//        if (accounts == null || accounts.isEmpty()) {
//            return Response.status(Response.Status.NOT_FOUND)
//                           .entity("No accounts found for userId: " + userId)
//                           .build();
//        }
//
//        return Response.ok(accounts).build();
//    }
    
 // ✅ Account Statement API
    @POST
    @Path("/statement")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountStatement(AccountStatementRequest request) {
        try {
            // ✅ Validate input
            if (request == null ||
                request.getAccountNumber() == null || request.getAccountNumber().trim().isEmpty() ||
                request.getCustomerId() == null || request.getCustomerId().trim().isEmpty() ||
                request.getFromDate() == null || request.getFromDate().trim().isEmpty() ||
                request.getToDate() == null || request.getToDate().trim().isEmpty()) {
                
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity(Map.of(
                                   "success", false,
                                   "message", "Missing required fields: accountNumber, customerId, fromDate, toDate"
                               ))
                               .build();
            }

            // 🔒 Ownership validation
            boolean isOwner = bankingService.doesAccountBelongToCustomer(
                request.getAccountNumber(),
                request.getCustomerId()
            );

            if (!isOwner) {
                return Response.status(Response.Status.FORBIDDEN)
                               .entity(Map.of(
                                   "success", false,
                                   "message", "You are not authorized to view this account statement"
                               ))
                               .build();
            }

            // 📅 Parse dates
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate fromDate = LocalDate.parse(request.getFromDate(), formatter);
            LocalDate toDate = LocalDate.parse(request.getToDate(), formatter);

            // 📜 Fetch transactions
            List<Transaction> transactions = bankingService.getAccountStatement(
                request.getAccountNumber(),
                fromDate,
                toDate
            );

            if (transactions == null || transactions.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity(Map.of(
                                   "success", false,
                                   "message", "No transactions found for this period"
                               ))
                               .build();
            }

            return Response.ok(Map.of(
                "success", true,
                "accountNumber", request.getAccountNumber(),
                "customerId", request.getCustomerId(),
                "fromDate", fromDate.toString(),
                "toDate", toDate.toString(),
                "transactions", transactions
            )).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Map.of(
                               "success", false,
                               "message", "Server error while fetching account statement"
                           ))
                           .build();
        }
    }

    
    @POST
    @Path("/accounts")
    public Response getCustomerAccounts(CustomerRequest request) {
        try {
            if (request == null || request.getCustomerId() == null || request.getCustomerId().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity("Customer ID is required")
                               .build();
            }

            List<BankAccount> accounts = bankingService.getCustomerAccounts(request.getCustomerId());

            if (accounts == null || accounts.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("No accounts found for customer ID: " + request.getCustomerId())
                               .build();
            }

            return Response.ok(accounts).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error retrieving accounts")
                           .build();
        }
    }
    
    @POST
    @Path("/ownership")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkAccountOwnership(AccountOwnershipRequest request) {
        try {
            if (request == null ||
                request.getAccountNumber() == null || request.getAccountNumber().trim().isEmpty() ||
                request.getCustomerId() == null || request.getCustomerId().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity("{\"error\":\"Both accountNumber and customerId are required.\"}")
                               .build();
            }
            boolean isOwner = bankingService.doesAccountBelongToCustomer(
                request.getAccountNumber(), 
                request.getCustomerId()
            );
            return Response.ok("{\"owner\":" + isOwner + "}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\":\"Internal server error.\"}")
                           .build();
        }
    }
    

    @POST
    @Path("/deposit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response depositMoney(DepositRequest request) {
        // Validate request
        if (request == null
                || request.getAccountNumber() == null
                || request.getAccountNumber().isEmpty()
                || request.getAmount() == null
                || request.getCustomerId() == null
                || request.getCustomerId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "success", false,
                        "message", "Customer ID, Account number and amount are required"
                    ))
                    .build();
        }

        // ✅ Ownership check
        boolean ownsAccount = bankingService.doesAccountBelongToCustomer(
                request.getAccountNumber(),
                request.getCustomerId()
        );

        if (!ownsAccount) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of(
                        "success", false,
                        "message", "You are not authorized to deposit into this account"
                    ))
                    .build();
        }

        // Business logic
        boolean deposited = bankingService.creditAccount(
            request.getAccountNumber(),
            request.getAmount(),
            request.getDescription()
        );

        if (deposited) {
            return Response.ok(Map.of(
                "success", true,
                "message", "Amount credited successfully",
                "accountNumber", request.getAccountNumber(),
                "amount", request.getAmount()
            )).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "success", false,
                        "message", "Deposit failed (account may not exist or is inactive)"
                    ))
                    .build();
        }
    }

        
        
    @POST
    @Path("/withdraw")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response withdrawMoney(WithdrawRequest request) {
        // Validate input
        if (request == null
                || request.getAccountNumber() == null
                || request.getAccountNumber().isEmpty()
                || request.getAmount() == null
                || request.getCustomerId() == null
                || request.getCustomerId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "success", false,
                            "message", "Customer ID, Account number and amount are required"
                    ))
                    .build();
        }

        // ✅ Ownership check
        boolean ownsAccount = bankingService.doesAccountBelongToCustomer(
                request.getAccountNumber(),
                request.getCustomerId()
        );

        if (!ownsAccount) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of(
                            "success", false,
                            "message", "You are not authorized to withdraw from this account"
                    ))
                    .build();
        }

        // Business logic
        boolean withdrawn = bankingService.debitAccount(
            request.getAccountNumber(),
            request.getAmount(),
            request.getDescription()
        );

        if (withdrawn) {
            return Response.ok(Map.of(
                    "success", true,
                    "message", "Amount withdrawn successfully",
                    "accountNumber", request.getAccountNumber(),
                    "amount", request.getAmount()
            )).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "success", false,
                            "message", "Withdrawal failed (account may not exist, is inactive, or insufficient funds)"
                    ))
                    .build();
        }
    }

    }
    
    

