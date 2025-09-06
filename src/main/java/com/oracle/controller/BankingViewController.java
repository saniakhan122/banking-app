//// com.oracle.controller.BankingViewController.java
//package com.oracle.controller;
//
//import com.oracle.beans.Customer;
//import com.oracle.beans.BankAccount;
//import com.oracle.beans.Transaction;
//import com.oracle.business.CustomerService;
//import com.oracle.business.BankingService;
//import com.oracle.business.util.ServiceFactory;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.List;
//import java.util.Scanner;
//import java.util.logging.Logger;
//
///**
// * Banking View Controller - Acts as both View and Controller in console application
// * Handles user interactions and coordinates with Service layer
// */
//public class BankingViewController {
//    
//    private static final Logger logger = Logger.getLogger(BankingViewController.class.getName());
//    private Scanner scanner;
//    
//    // Service layer references (using interface for loose coupling)
//    private CustomerService customerService;
//    private BankingService bankingService;
//    
//    public BankingViewController() {
//        this.scanner = new Scanner(System.in);
//        // Get service instances using Factory Pattern
//        this.customerService = ServiceFactory.getCustomerService();
//        this.bankingService = ServiceFactory.getBankingService();
//        logger.info("BankingViewController initialized");
//    }
//    
//    /**
//     * Main menu display and navigation
//     */
//    public void showMainMenu() {
//        while (true) {
//            try {
//                System.out.println("\n" + "=".repeat(60));
//                System.out.println("         ONLINE BANKING SYSTEM");
//                System.out.println("=".repeat(60));
//                System.out.println("1. Customer Management");
//                System.out.println("2. Account Management");
//                System.out.println("3. Fund Transfer");
//                System.out.println("4. Transaction History");
//                System.out.println("5. Account Balance Inquiry");
//                System.out.println("6. Reports");
//                System.out.println("7. Exit");
//                System.out.println("=".repeat(60));
//                System.out.print("Enter your choice: ");
//                
//                int choice = getIntInput();
//                
//                switch (choice) {
//                    case 1:
//                        showCustomerManagementMenu();
//                        break;
//                    case 2:
//                        showAccountManagementMenu();
//                        break;
//                    case 3:
//                        showFundTransferMenu();
//                        break;
//                    case 4:
//                        showTransactionHistoryMenu();
//                        break;
//                    case 5:
//                        handleBalanceInquiry();
//                        break;
//                    case 6:
//                        showReportsMenu();
//                        break;
//                    case 7:
//                        System.out.println("Thank you for using Online Banking System!");
//                        return;
//                    default:
//                        System.out.println("Invalid choice. Please try again.");
//                }
//            } catch (Exception e) {
//                logger.severe("Error in main menu: " + e.getMessage());
//                System.out.println("An error occurred. Please try again.");
//            }
//        }
//    }
//    
//    private void showTransactionHistoryMenu() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	/**
//     * Customer Management Menu
//     */
//    private void showCustomerManagementMenu() {
//        while (true) {
//            System.out.println("\n" + "=".repeat(50));
//            System.out.println("      CUSTOMER MANAGEMENT");
//            System.out.println("=".repeat(50));
//            System.out.println("1. Register New Customer");
//            System.out.println("2. View Customer Profile");
//            System.out.println("3. Update Customer Profile");
//            System.out.println("4. Search Customer");
//            System.out.println("5. List All Customers");
//            System.out.println("6. Customer Statistics");
//            System.out.println("7. Back to Main Menu");
//            System.out.print("Enter your choice: ");
//            
//            int choice = getIntInput();
//            
//            switch (choice) {
//                case 1:
//                    handleCustomerRegistration();
//                    break;
//                case 2:
//                    handleViewCustomerProfile();
//                    break;
//                case 3:
//                    handleUpdateCustomerProfile();
//                    break;
//                case 4:
//                    handleSearchCustomer();
//                    break;
//                case 5:
//                    handleListAllCustomers();
//                    break;
//                case 6:
//                    handleCustomerStatistics();
//                    break;
//                case 7:
//                    return;
//                default:
//                    System.out.println("Invalid choice. Please try again.");
//            }
//        }
//    }
//    
//    /**
//     * Handle Customer Registration
//     */
//    private void handleCustomerRegistration() {
//        System.out.println("\n" + "=".repeat(40));
//        System.out.println("    NEW CUSTOMER REGISTRATION");
//        System.out.println("=".repeat(40));
//        
//        try {
//            Customer customer = new Customer();
//            
//            System.out.print("Enter Full Name: ");
//            customer.setFullName(getStringInput());
//            
//            System.out.print("Enter Email: ");
//            customer.setEmail(getStringInput());
//            
//            System.out.print("Enter Mobile Number: ");
//            customer.setMobileNumber(getStringInput());
//            
//            System.out.print("Enter Date of Birth (yyyy-mm-dd): ");
//            String dobString = getStringInput();
//            try {
//                LocalDate dob = LocalDate.parse(dobString);
//                customer.setDateOfBirth(dob);
//            } catch (DateTimeParseException e) {
//                System.out.println("Invalid date format. Please use yyyy-mm-dd.");
//                return;
//            }
//            
//            System.out.print("Enter Aadhar Number (12 digits): ");
//            customer.setAadharNumber(getStringInput());
//            
//            System.out.print("Enter Residential Address: ");
//            customer.setResidentialAddress(getStringInput());
//            
//            System.out.print("Enter Permanent Address (Press Enter if same as residential): ");
//            String permanentAddress = getStringInput();
//            if (permanentAddress.isEmpty()) {
//                customer.setPermanentAddress(customer.getResidentialAddress());
//            } else {
//                customer.setPermanentAddress(permanentAddress);
//            }
//            
//            System.out.print("Enter Occupation: ");
//            customer.setOccupation(getStringInput());
//            
//            System.out.print("Enter Annual Income: ");
//            BigDecimal income = getBigDecimalInput();
//            customer.setAnnualIncome(income);
//            
//            // Register customer
//            String result = customerService.registerCustomer(customer);
//            
//            switch (result) {
//                case "SUCCESS":
//                    System.out.println("\n✓ Customer registered successfully!");
//                    System.out.println("Customer ID: " + customer.getCustomerId());
//                    System.out.println("Service Reference: " + customer.getServiceReferenceNo());
//                    break;
//                case "EMAIL_EXISTS":
//                    System.out.println("\n✗ Email already registered. Please use different email.");
//                    break;
//                case "MOBILE_EXISTS":
//                    System.out.println("\n✗ Mobile number already registered. Please use different mobile.");
//                    break;
//                case "AADHAR_EXISTS":
//                    System.out.println("\n✗ Aadhar number already registered. Please verify your Aadhar.");
//                    break;
//                case "VALIDATION_FAILED":
//                    System.out.println("\n✗ Invalid customer data. Please check all fields.");
//                    break;
//                default:
//                    System.out.println("\n✗ Registration failed. Please try again.");
//            }
//            
//        } catch (Exception e) {
//            logger.severe("Error in customer registration: " + e.getMessage());
//            System.out.println("An error occurred during registration. Please try again.");
//        }
//    }
//    
//    /**
//     * Handle View Customer Profile
//     */
//    private void handleViewCustomerProfile() {
//        System.out.println("\n" + "=".repeat(40));
//        System.out.println("      VIEW CUSTOMER PROFILE");
//        System.out.println("=".repeat(40));
//        
//        System.out.print("Enter Customer ID: ");
//        String customerId = getStringInput();
//        
//        Customer customer = customerService.getCustomerProfile(customerId);
//        
//        if (customer != null) {
//            displayCustomerProfile(customer);
//        } else {
//            System.out.println("✗ Customer not found with ID: " + customerId);
//        }
//    }
//    
//    /**
//     * Display customer profile in formatted way
//     */
//    private void displayCustomerProfile(Customer customer) {
//        System.out.println("\n" + "=".repeat(50));
//        System.out.println("         CUSTOMER PROFILE");
//        System.out.println("=".repeat(50));
//        System.out.println("Customer ID        : " + customer.getCustomerId());
//        System.out.println("Service Reference  : " + customer.getServiceReferenceNo());
//        System.out.println("Full Name          : " + customer.getFullName());
//        System.out.println("Email              : " + customer.getEmail());
//        System.out.println("Mobile Number      : " + customer.getMobileNumber());
//        System.out.println("Date of Birth      : " + customer.getDateOfBirth());
//        System.out.println("Aadhar Number      : " + customer.getAadharNumber());
//        System.out.println("Occupation         : " + customer.getOccupation());
//        System.out.println("Annual Income      : ₹" + customer.getAnnualIncome());
//        System.out.println("Status             : " + customer.getStatus());
//        System.out.println("Created At         : " + customer.getCreatedAt());
//        System.out.println("Residential Address: " + customer.getResidentialAddress());
//        System.out.println("Permanent Address  : " + customer.getPermanentAddress());
//        System.out.println("=".repeat(50));
//    }
//    
//    /**
//     * Handle Search Customer
//     */
//    private void handleSearchCustomer() {
//        System.out.println("\n" + "=".repeat(40));
//        System.out.println("       SEARCH CUSTOMER");
//        System.out.println("=".repeat(40));
//        System.out.println("1. Search by Email");
//        System.out.println("2. Search by Mobile");
//        System.out.println("3. Search by Aadhar");
//        System.out.print("Enter your choice: ");
//        
//        int choice = getIntInput();
//        Customer customer = null;
//        
//        switch (choice) {
//            case 1:
//                System.out.print("Enter Email: ");
//                String email = getStringInput();
//                customer = customerService.findCustomerByEmail(email);
//                break;
//            case 2:
//                System.out.print("Enter Mobile Number: ");
//                String mobile = getStringInput();
//                customer = customerService.findCustomerByMobile(mobile);
//                break;
//            case 3:
//                System.out.print("Enter Aadhar Number: ");
//                String aadhar = getStringInput();
//                customer = customerService.findCustomerByAadhar(aadhar);
//                break;
//            default:
//                System.out.println("Invalid choice.");
//                return;
//        }
//        
//        if (customer != null) {
//            displayCustomerProfile(customer);
//        } else {
//            System.out.println("✗ No customer found with the provided information.");
//        }
//    }
//    
//    /**
//     * Handle List All Customers
//     */
//    private void handleListAllCustomers() {
//        System.out.println("\n" + "=".repeat(80));
//        System.out.println("                           ALL CUSTOMERS");
//        System.out.println("=".repeat(80));
//        
//        List<Customer> customers = customerService.getAllCustomers();
//        
//        if (customers != null && !customers.isEmpty()) {
//            System.out.printf("%-15s %-25s %-30s %-15s %-10s%n", 
//                            "Customer ID", "Full Name", "Email", "Mobile", "Status");
//            System.out.println("-".repeat(80));
//            
//            for (Customer customer : customers) {
//                System.out.printf("%-15s %-25s %-30s %-15s %-10s%n",
//                    customer.getCustomerId(),
//                    customer.getFullName().length() > 24 ? 
//                        customer.getFullName().substring(0, 21) + "..." : customer.getFullName(),
//                    customer.getEmail().length() > 29 ? 
//                        customer.getEmail().substring(0, 26) + "..." : customer.getEmail(),
//                    customer.getMobileNumber(),
//                    customer.getStatus()
//                );
//            }
//            System.out.println("-".repeat(80));
//            System.out.println("Total Customers: " + customers.size());
//        } else {
//            System.out.println("No customers found.");
//        }
//    }
//    
//    /**
//     * Handle Customer Statistics
//     */
//    private void handleCustomerStatistics() {
//        System.out.println("\n" + "=".repeat(40));
//        System.out.println("     CUSTOMER STATISTICS");
//        System.out.println("=".repeat(40));
//        
//        long totalCustomers = customerService.getTotalCustomerCount();
//        long activeCustomers = customerService.getActiveCustomerCount();
//        
//        System.out.println("Total Customers    : " + totalCustomers);
//        System.out.println("Active Customers   : " + activeCustomers);
//        System.out.println("Inactive Customers : " + (totalCustomers - activeCustomers));
//        
//        if (totalCustomers > 0) {
//            double activePercentage = (double) activeCustomers / totalCustomers * 100;
//            System.out.printf("Active Percentage  : %.2f%%\n", activePercentage);
//        }
//    }
//    
//    /**
//     * Account Management Menu
//     */
//    private void showAccountManagementMenu() {
//        System.out.println("\n" + "=".repeat(40));
//        System.out.println("      ACCOUNT MANAGEMENT");
//        System.out.println("=".repeat(40));
//        System.out.println("1. Create New Account");
//        System.out.println("2. View Account Details");
//        System.out.println("3. View Customer Accounts");
//        System.out.println("4. List All Accounts");
//        System.out.println("5. Back to Main Menu");
//        System.out.print("Enter your choice: ");
//        
//        int choice = getIntInput();
//        
//        switch (choice) {
//            case 1:
//                handleCreateAccount();
//                break;
//            case 2:
//                handleViewAccountDetails();
//                break;
//            case 3:
//                handleViewCustomerAccounts();
//                break;
//            case 4:
//                handleListAllAccounts();
//                break;
//            case 5:
//                return;
//            default:
//                System.out.println("Invalid choice. Please try again.");
//        }
//    }
//    
//    /**
//     * Handle Create Account
//     */
//    private void handleCreateAccount() {
//        System.out.println("\n" + "=".repeat(40));
//        System.out.println("      CREATE NEW ACCOUNT");
//        System.out.println("=".repeat(40));
//        
//        System.out.print("Enter Customer ID: ");
//        String customerId = getStringInput();
//        
//        // Verify customer exists
//        if (!customerService.isCustomerExists(customerId)) {
//            System.out.println("✗ Customer not found with ID: " + customerId);
//            return;
//        }
//        
//        System.out.println("Select Account Type:");
//        System.out.println("1. SAVINGS");
//        System.out.println("2. CURRENT");
//        System.out.print("Enter your choice: ");
//        
//        int typeChoice = getIntInput();
//        String accountType;
//        
//        switch (typeChoice) {
//            case 1:
//                accountType = "SAVINGS";
//                break;
//            case 2:
//                accountType = "CURRENT";
//                break;
//            default:
//                System.out.println("Invalid choice. Defaulting to SAVINGS.");
//                accountType = "SAVINGS";
//        }
//        
//        System.out.print("Enter Initial Deposit Amount: ₹");
//        BigDecimal initialDeposit = getBigDecimalInput();
//        
//        if (initialDeposit.compareTo(new BigDecimal("1000")) < 0) {
//            System.out.println("✗ Minimum initial deposit is ₹1000.");
//            return;
//        }
//        
//        String result = bankingService.createBankAccount(customerId, accountType, initialDeposit);
//        
//        if (result.startsWith("ACC")) {
//            System.out.println("\n✓ Account created successfully!");
//            System.out.println("Account Number: " + result);
//            System.out.println("Account Type: " + accountType);
//            System.out.println("Initial Balance: ₹" + initialDeposit);
//        } 
//    }
//    
//    // Input utility methods
//    private int getIntInput() {
//        try {
//            return Integer.parseInt(scanner.nextLine().trim());
//        } catch (NumberFormatException e) {
//            System.out.println("Invalid number format. Please enter a valid number.");
//            return -1;
//        }
//    }
//    
//    private String getStringInput() {
//        return scanner.nextLine().trim();
//    }
//    
//    private BigDecimal getBigDecimalInput() {
//        try {
//            return new BigDecimal(scanner.nextLine().trim());
//        } catch (NumberFormatException e) {
//            System.out.println("Invalid amount format. Please enter a valid number.");
//            return BigDecimal.ZERO;
//        }
//    }
//    
//    // Placeholder methods for remaining functionality
//    private void handleUpdateCustomerProfile() {
//        System.out.println("Update Customer Profile - Feature under development");
//    }
//    
//    private void handleViewAccountDetails() {
//        System.out.println("\n" + "=".repeat(40));
//        System.out.println("      VIEW ACCOUNT DETAILS");
//        System.out.println("=".repeat(40));
//        
//        System.out.print("Enter Account Number: ");
//        String accountNumber = getStringInput();
//        
//        if (accountNumber.isEmpty()) {
//            System.out.println("✗ Account number cannot be empty.");
//            return;
//        }
//        
//        BankAccount account = bankingService.getAccountDetails(accountNumber);
//        
//        if (account != null) {
//            System.out.println("\n✓ Account Details:");
//            System.out.println("─".repeat(40));
//            System.out.println("Account Number    : " + account.getAccountNumber());
//            System.out.println("Customer ID       : " + account.getCustomerId());
//            System.out.println("Account Type      : " + account.getAccountType());
//            System.out.println("Current Balance   : ₹" + account.getBalance());
//            System.out.println("Account Status    : " + ("Y".equals(account.getIsActive()) ? "Active" : "Inactive"));
//            System.out.println("Opened Date       : " + account.getOpenedDate().toLocalDate());
//            
//            // Check minimum balance
//            if (bankingService.checkMinimumBalance(accountNumber)) {
//                System.out.println("Minimum Balance   : ✓ Maintained");
//            } else {
//                System.out.println("Minimum Balance   : ✗ Below minimum");
//            }
//        } else {
//            System.out.println("✗ Account not found with number: " + accountNumber);
//        }
//    }
//    
//    private void handleViewCustomerAccounts() {
//        System.out.println("\n" + "=".repeat(40));
//        System.out.println("      CUSTOMER ACCOUNTS");
//        System.out.println("=".repeat(40));
//        
//        System.out.print("Enter Customer ID: ");
//        String customerId = getStringInput();
//        
//        if (customerId.isEmpty()) {
//            System.out.println("✗ Customer ID cannot be empty.");
//            return;
//        }
//        
//        List<BankAccount> accounts = bankingService.getCustomerAccounts(customerId);
//        
//        if (accounts != null && !accounts.isEmpty()) {
//            System.out.println("\n✓ Customer Accounts Found: " + accounts.size());
//            System.out.println("─".repeat(80));
//            System.out.printf("%-15s %-12s %-15s %-8s %-12s%n", 
//                "Account No", "Type", "Balance", "Status", "Opened Date");
//            System.out.println("─".repeat(80));
//            
//            BigDecimal totalBalance = BigDecimal.ZERO;
//            for (BankAccount account : accounts) {
//                System.out.printf("%-15s %-12s ₹%-14s %-8s %-12s%n",
//                    account.getAccountNumber(),
//                    account.getAccountType(),
//                    account.getBalance(),
//                    ("Y".equals(account.getIsActive()) ? "Active" : "Inactive"),
//                    account.getOpenedDate().toLocalDate()
//                );
//                totalBalance = totalBalance.add(account.getBalance());
//            }
//            System.out.println("─".repeat(80));
//            System.out.println("Total Balance across all accounts: ₹" + totalBalance);
//        } else {
//            System.out.println("✗ No accounts found for customer ID: " + customerId);
//        }
//    }
//
//    
//    private void handleListAllAccounts() {
//        System.out.println("\n" + "=".repeat(40));
//        System.out.println("      ALL BANK ACCOUNTS");
//        System.out.println("=".repeat(40));
//        
//        try {
//            // Note: You'll need to add this method to BankingService interface and implementation
//            // For now, we'll show a message about implementation
//            System.out.println("Fetching all accounts...");
//            
//            BigDecimal totalBalance = bankingService.getTotalBankBalance();
//            System.out.println("✓ Total Bank Balance: ₹" + totalBalance);
//            
//            List<BankAccount> accounts = bankingService.getAllAccounts();
//            for (BankAccount account : accounts) {
//                System.out.println("Account Number: " + account.getAccountNumber());
//                System.out.println("Customer Name : " + account.getCustomer().getFullName());
//                System.out.println("Balance        : ₹" + account.getBalance());
//                System.out.println("----------------------------------------");
//            }
//
//            
//        } catch (Exception e) {
//            System.out.println("✗ Error retrieving accounts: " + e.getMessage());
//        }
//    }
//    private void showFundTransferMenu() {
//        System.out.println("\n" + "=".repeat(40));
//        System.out.println("        FUND TRANSFER");
//        System.out.println("=".repeat(40));
//        System.out.println("1. NEFT Transfer");
//        System.out.println("2. RTGS Transfer");
//        System.out.println("3. IMPS Transfer");
//        System.out.println("4. Back to Main Menu");
//        System.out.print("Enter your choice: ");
//        
//        int choice = getIntInput();
//        
//        switch (choice) {
//            case 1:
//                handleFundTransfer("NEFT");
//                break;
//            case 2:
//                handleFundTransfer("RTGS");
//                break;
//            case 3:
//                handleFundTransfer("IMPS");
//                break;
//            case 4:
//                return;
//            default:
//                System.out.println("Invalid choice. Please try again.");
//        }
//    }
//    
//    private void handleFundTransfer(String transferMethod) {
//        System.out.println("\n" + "=".repeat(40));
//        System.out.println("      " + transferMethod + " TRANSFER");
//        System.out.println("=".repeat(40));
//        
//        System.out.print("From Account Number: ");
//        String fromAccount = getStringInput();
//        
//        System.out.print("To Account Number: ");
//        String toAccount = getStringInput();
//        
//        System.out.print("Transfer Amount: ₹");
//        BigDecimal amount = getBigDecimalInput();
//        
//        System.out.print("Remarks (optional): ");
//        String remarks = getStringInput();
//        
//        // Show transfer limits
//        BigDecimal transferLimit = bankingService.getTransferLimit(transferMethod);
//        System.out.println("\n" + transferMethod + " Transfer Limit: ₹" + transferLimit);
//        System.out.println("Daily Transfer Limit: ₹" + bankingService.getDailyTransferLimit());
//        
//        // Validate transfer amount for method
//        if (!bankingService.isValidTransferAmount(amount, transferMethod)) {
//            System.out.println("✗ Invalid amount for " + transferMethod + " transfer.");
//            displayTransferLimits(transferMethod);
//            return;
//        }
//        
//        // Show account balances before transfer
//        BigDecimal fromBalance = bankingService.getAccountBalance(fromAccount);
//        BigDecimal toBalance = bankingService.getAccountBalance(toAccount);
//        
//        System.out.println("\n" + "─".repeat(40));
//        System.out.println("TRANSFER SUMMARY:");
//        System.out.println("From Account: " + fromAccount + " (Balance: ₹" + fromBalance + ")");
//        System.out.println("To Account  : " + toAccount + " (Balance: ₹" + toBalance + ")");
//        System.out.println("Amount      : ₹" + amount);
//        System.out.println("Method      : " + transferMethod);
//        System.out.println("Remarks     : " + (remarks.isEmpty() ? "N/A" : remarks));
//        System.out.println("─".repeat(40));
//        
//        System.out.print("Confirm transfer? (Y/N): ");
//        String confirm = getStringInput();
//        
//        if (!"Y".equalsIgnoreCase(confirm)) {
//            System.out.println("✗ Transfer cancelled.");
//            return;
//        }
//        
//        String result = bankingService.transferFunds(fromAccount, toAccount, amount, transferMethod, remarks);
//        
//        if ("SUCCESS".equals(result)) {
//            System.out.println("\n✓ Transfer completed successfully!");
//            System.out.println("Transfer Method: " + transferMethod);
//            System.out.println("Amount: ₹" + amount);
//            System.out.println("From: " + fromAccount);
//            System.out.println("To: " + toAccount);
//            
//            // Show updated balances
//            BigDecimal newFromBalance = bankingService.getAccountBalance(fromAccount);
//            BigDecimal newToBalance = bankingService.getAccountBalance(toAccount);
//            System.out.println("\nUpdated Balances:");
//            System.out.println("From Account: ₹" + newFromBalance);
//            System.out.println("To Account: ₹" + newToBalance);
//        } else {
//            System.out.println("✗ Transfer failed: " + result);
//            displayTransferErrorHelp(result);
//        }
//    }
//    
//    private void displayTransferLimits(String method) {
//        System.out.println("\n" + method + " Transfer Limits:");
//        switch (method.toUpperCase()) {
//            case "NEFT":
//                System.out.println("Minimum: ₹1, Maximum: ₹10,00,000");
//                break;
//            case "RTGS":
//                System.out.println("Minimum: ₹2,00,000, Maximum: ₹1,00,00,000");
//                break;
//            case "IMPS":
//                System.out.println("Minimum: ₹1, Maximum: ₹5,00,000");
//                break;
//        }
//    }
//    
//    private void displayTransferErrorHelp(String error) {
//        System.out.println("\nError Details:");
//        switch (error) {
//            case "VALIDATION_FAILED":
//                System.out.println("• Check if accounts exist and are active");
//                System.out.println("• Ensure sufficient balance");
//                System.out.println("• Cannot transfer to same account");
//                break;
//            case "INVALID_AMOUNT_FOR_METHOD":
//                System.out.println("• Check transfer method limits");
//                break;
//            case "DAILY_LIMIT_EXCEEDED":
//                System.out.println("• Daily transfer limit exceeded");
//                System.out.println("• Current limit: ₹" + bankingService.getDailyTransferLimit());
//                break;
//            default:
//                System.out.println("• " + error);
//        }
//    }

//    
//    
//    
//
//    
//    private void handleBalanceInquiry() {
//        System.out.println("Balance Inquiry - Feature under development");
//    }
//    
//    private void showReportsMenu() {
//        System.out.println("Reports - Feature under development");
//    }
//}


//package com.oracle.controller;
//
//import com.oracle.beans.AccountCreationRequest;
//import com.oracle.beans.AdminUser;
//import com.oracle.beans.Customer;
//import com.oracle.beans.CustomerLogin;
//import com.oracle.business.*;
//import com.oracle.business.util.ServiceFactory;
//
//import java.math.BigDecimal;
//import java.security.SecureRandom;
//import java.time.LocalDateTime;
//import java.util.Base64;
//import java.util.List;
//import java.util.Scanner;
//
//public class BankingViewController {
//
//    private static final Scanner scanner = new Scanner(System.in);
//
//    // Services
//    private static AdminUserService adminService = ServiceFactory.getAdminUserService();
//    private static AccountCreationRequestService accountReqService = ServiceFactory.getAccountCreationRequestService();
//    private static CustomerLoginService customerLoginService = ServiceFactory.getCustomerLoginService();
//    private static CustomerService customerService = ServiceFactory.getCustomerService();
//    private static BankingService bankingService = ServiceFactory.getBankingService();
//
//    public static void main(String[] args) {
//        mainMenu();
//    }
//
//    private static void mainMenu() {
//        while (true) {
//            System.out.println("\n=== Welcome to Online Banking System ===");
//            System.out.println("1. Admin Login");
//            System.out.println("2. Customer Login");
//            System.out.println("3. Customer Registration (Account Creation Request)");
//            System.out.println("0. Exit");
//
//            System.out.print("Enter choice: ");
//            String choice = scanner.nextLine().trim();
//
//            switch (choice) {
//                case "1":
//                    AdminController.adminLogin();
//                    break;
//                case "2":
//                    CustomerController.customerLoginMenu();
//                    break;
//                case "3":
//                    CustomerController.registerAccountCreationRequest();
//                    break;
//                case "0":
//                    System.out.println("Thank you for using Online Banking System. Exiting.");
//                    System.exit(0);
//                    break;
//                default:
//                    System.out.println("Invalid option! Please try again.");
//            }
//        }
//    }
//
//    // Admin module controller
//    private static class AdminController {
//
//        private static AdminUser loggedInAdmin = null;
//
//        public static void adminLogin() {
//            System.out.println("\n--- Admin Login ---");
//            System.out.print("Username: ");
//            String username = scanner.nextLine().trim();
//            System.out.print("Password: ");
//            String password = scanner.nextLine().trim();
//
//            // Assume password hashing is done inside service or before calling
//            boolean isAuthenticated = adminService.authenticate(username, password);
//
//            if (isAuthenticated) {
//                loggedInAdmin = adminService.getAdminByUsername(username);
//                System.out.println("Login successful. Welcome, " + loggedInAdmin.getAdminId());
//                adminMenu();
//            } else {
//                System.out.println("Invalid username or password.");
//            }
//        }
//
//        private static void adminMenu() {
//            if (loggedInAdmin == null) {
//                System.out.println("Admin not logged in.");
//                return;
//            }
//
//            while (true) {
//                System.out.println("\n--- Admin Menu ---");
//                System.out.println("1. View Pending Account Creation Requests");
//                System.out.println("2. Approve Account Creation Request");
//                System.out.println("3. Reject Account Creation Request");
//                System.out.println("4. Logout");
//
//                System.out.print("Enter choice: ");
//                String choice = scanner.nextLine().trim();
//
//                switch (choice) {
//                    case "1":
//                        viewPendingRequests();
//                        break;
//                    case "2":
//                        approveRequest();
//                        break;
//                    case "3":
//                        rejectRequest();
//                        break;
//                    case "4":
//                        System.out.println("Logging out admin...");
//                        loggedInAdmin = null;
//                        return; // Exit to main menu
//                    default:
//                        System.out.println("Invalid option! Please try again.");
//                }
//            }
//        }
//
//        private static void viewPendingRequests() {
//            List<AccountCreationRequest> requests = accountReqService.getRequestsByStatus("PENDING");
//            if (requests.isEmpty()) {
//                System.out.println("No pending account creation requests.");
//            } else {
//                System.out.println("--- Pending Account Requests ---");
//                for (AccountCreationRequest r : requests) {
//                    System.out.printf("Request ID: %s, Name: %s, Mobile: %s, Email: %s, Submitted At: %s\n",
//                            r.getRequestId(), r.getFullName(), r.getMobileNumber(), r.getEmail(), r.getSubmittedAt());
//                }
//            }
//        }
//
//        private static void approveRequest() {
//            System.out.print("Enter Request ID to approve: ");
//            String requestId = scanner.nextLine().trim();
//            boolean success = accountReqService.approveRequestAndCreateCustomer(requestId, loggedInAdmin.getAdminId());
//            if (success) {
//                System.out.println("Request approved successfully.");
//                // Optionally: trigger account creation, send notifications etc.
//            } else {
//                System.out.println("Failed to approve request. Please check the ID and try again.");
//            }
//        }
//
//        private static void rejectRequest() {
//            System.out.print("Enter Request ID to reject: ");
//            String requestId = scanner.nextLine().trim();
//            System.out.print("Enter rejection reason: ");
//            String reason = scanner.nextLine().trim();
//            boolean success = accountReqService.rejectRequest(requestId, loggedInAdmin.getAdminId(), reason);
//            if (success) {
//                System.out.println("Request rejected successfully.");
//            } else {
//                System.out.println("Failed to reject request. Please check the ID and try again.");
//            }
//        }
//    }
//
//    // Customer module controller
//    private static class CustomerController {
//
//        private static CustomerLogin loggedInCustomer = null;
//
//        private static void customerLogin() {
//            System.out.println("\n--- Customer Login ---");
//            System.out.print("User ID: ");
//            String userId = scanner.nextLine().trim();
//            System.out.print("Password: ");
//            String password = scanner.nextLine().trim();
//
//            boolean authenticated = customerLoginService.authenticate(userId, password);
//            if (authenticated) {
//                loggedInCustomer = customerLoginService.getByUserId(userId);
//                System.out.println("Login successful! Welcome, " + loggedInCustomer.getCustomerId());
//                customerMenu();
//            } else {
//                System.out.println("Login failed. If you exceed 3 failed attempts, your account will be locked.");
//            }
//        }
//
//
//        public static void registerAccountCreationRequest() {
//            System.out.println("\n--- Account Creation Request ---");
//            AccountCreationRequest request = new AccountCreationRequest();
//
//            System.out.print("Full Name: ");
//            request.setFullName(scanner.nextLine().trim());
//
//            System.out.print("Email: ");
//            request.setEmail(scanner.nextLine().trim());
//
//            System.out.print("Mobile Number: ");
//            request.setMobileNumber(scanner.nextLine().trim());
//
//            System.out.print("Date of Birth (YYYY-MM-DD): ");
//            try {
//                request.setDateOfBirth(java.time.LocalDate.parse(scanner.nextLine().trim()));
//            } catch (Exception e) {
//                System.out.println("Invalid date format. Aborting registration.");
//                return;
//            }
//
//            System.out.print("Aadhar Number: ");
//            request.setAadharNumber(scanner.nextLine().trim());
//
//            System.out.print("Residential Address: ");
//            request.setResidentialAddress(scanner.nextLine().trim());
//
//            System.out.print("Permanent Address: ");
//            request.setPermanentAddress(scanner.nextLine().trim());
//
//            System.out.print("Occupation: ");
//            request.setOccupation(scanner.nextLine().trim());
//
//            System.out.print("Annual Income: ");
//            try {
//                request.setAnnualIncome(new BigDecimal(scanner.nextLine().trim()));
//            } catch (Exception e) {
//                System.out.println("Invalid income amount. Aborting registration.");
//                return;
//            }
//
//            // Set default values for other fields
//            request.setAccountType("SAVINGS");
//            request.setInitialDeposit(new BigDecimal("1000"));
//            request.setStatus("PENDING");
//            request.setSubmittedAt(LocalDateTime.now());
//
//            // Generate unique request ID and service ref no - you may want to call some utility or service for this
//            request.setRequestId("REQ" + System.currentTimeMillis());
//            request.setServiceReferenceNo("SRV" + System.currentTimeMillis());
//
//            boolean success = accountReqService.createRequest(request);
//            if (success) {
//                System.out.println("Account creation request submitted successfully!");
//                System.out.println("Your service reference number: " + request.getServiceReferenceNo());
//                System.out.println("Use this number to track your account creation status.");
//            } else {
//                System.out.println("Failed to submit account creation request. Please try again.");
//            }
//        }
//        
//        private static void customerLoginMenu() {
//            while (true) {
//                System.out.println("\n=== Customer Access ===");
//                System.out.println("1. Login");
//                System.out.println("2. Forgot User ID");
//                System.out.println("3. Forgot Password");
//                System.out.println("0. Back to Main Menu");
//                System.out.print("Enter choice: ");
//
//                String choice = scanner.nextLine().trim();
//                switch (choice) {
//                    case "1":
//                        customerLogin();
//                        break;
//                    case "2":
//                        forgotUserId();
//                        break;
//                    case "3":
//                        forgotPassword();
//                        break;
//                    case "0":
//                        return;
//                    default:
//                        System.out.println("Invalid option! Try again.");
//                }
//            }
//        }
//        
//        private static void forgotUserId() {
//            System.out.println("\n--- Forgot User ID ---");
//            System.out.print("Enter registered Email: ");
//            String email = scanner.nextLine().trim();
//            Customer customer = customerService.findCustomerByEmail(email);
//            if (customer != null) {
//                CustomerLogin login = customerLoginService.getByCustomerId(customer.getCustomerId());
//                if (login != null) {
//                    System.out.println("Your User ID is: " + login.getUserId());
//                } else {
//                    System.out.println("No login account found for this customer.");
//                }
//            } else {
//                System.out.println("No customer found with that email.");
//            }
//        }
//
//        private static void forgotPassword() {
//            System.out.println("\n--- Forgot Password ---");
//            System.out.print("Enter your User ID: ");
//            String userId = scanner.nextLine().trim();
//
//            CustomerLogin login = customerLoginService.getByUserId(userId);
//            if (login != null) {
//                String tempPassword = generateRandomPassword(10);
//
//                // For production: hash it before saving
//                // String hashedPassword = PasswordUtil.hashPassword(tempPassword);
//                // customerLoginService.updateLoginPassword(login.getCustomerId(), hashedPassword);
//
//                // For now, save as plain text if your authenticate() is plain text
//                customerLoginService.updateLoginPassword(login.getCustomerId(), tempPassword);
//
//                customerLoginService.resetFailedLoginAttempts(login.getCustomerId());
//                customerLoginService.unlockAccount(login.getCustomerId());
//
//                System.out.println("Your temporary password is: " + tempPassword);
//                System.out.println("Please login and change it immediately.");
//            } else {
//                System.out.println("User ID not found.");
//            }
//        }
//
//
//        private static String generateRandomPassword(int length) {
//            SecureRandom random = new SecureRandom();
//            byte[] bytes = new byte[length];
//            random.nextBytes(bytes);
//            return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, length);
//        }
//
//
//
//        private static void customerMenu() {
//            if (loggedInCustomer == null) {
//                System.out.println("Customer not logged in.");
//                return;
//            }
//
//            while (true) {
//                System.out.println("\n--- Customer Menu ---");
//                System.out.println("1. Fund Transfer");
//                System.out.println("2. Change Login Password");
//                System.out.println("3. Change Transaction Password");
//                System.out.println("4. Unlock Account");
//                System.out.println("5. Forgot Customer ID / Password (Customer Support)");
//                System.out.println("6. Logout");
//
//                System.out.print("Enter choice: ");
//                String choice = scanner.nextLine().trim();
//
//                switch (choice) {
//                    case "1":
//                        fundTransfer();
//                        break;
//                    case "2":
//                        changeLoginPassword();
//                        break;
//                    case "3":
//                        changeTransactionPassword();
//                        break;
//                    case "4":
//                        unlockAccount();
//                        break;
//                    case "5":
//                        forgotCredentials();
//                        break;
//                    case "6":
//                        System.out.println("Logging out customer...");
//                        loggedInCustomer = null;
//                        return;
//                    default:
//                        System.out.println("Invalid option! Please try again.");
//                }
//            }
//        }
//
//        private static void fundTransfer() {
//            System.out.println("\n--- Fund Transfer ---");
//
//            System.out.print("From Account Number: ");
//            String fromAccount = scanner.nextLine().trim();
//
//            System.out.print("To Account Number: ");
//            String toAccount = scanner.nextLine().trim();
//
//            System.out.print("Amount: ");
//            BigDecimal amount;
//            try {
//                amount = new BigDecimal(scanner.nextLine().trim());
//            } catch (Exception e) {
//                System.out.println("Invalid amount.");
//                return;
//            }
//
//            System.out.print("Transfer Method (NEFT/RTGS/IMPS): ");
//            String method = scanner.nextLine().trim().toUpperCase();
//
//            System.out.print("Remarks: ");
//            String remarks = scanner.nextLine().trim();
//
//            String result = bankingService.transferFunds(fromAccount, toAccount, amount, method, remarks);
//            switch (result) {
//                case "SUCCESS":
//                    System.out.println("Transfer successful.");
//                    break;
//                case "INVALID_AMOUNT_FOR_METHOD":
//                    System.out.println("Invalid amount for the selected transfer method.");
//                    break;
//                case "DAILY_LIMIT_EXCEEDED":
//                    System.out.println("Daily transfer limit exceeded.");
//                    break;
//                case "VALIDATION_FAILED":
//                    System.out.println("Validation failed. Please check account details and balance.");
//                    break;
//                case "TRANSFER_FAILED":
//                    System.out.println("Transfer failed due to system error.");
//                    break;
//                default:
//                    System.out.println("Unknown response: " + result);
//            }
//        }
//
//        private static void changeLoginPassword() {
//            System.out.println("\n--- Change Login Password ---");
//
//            System.out.print("Old Password: ");
//            String oldPass = scanner.nextLine().trim();
//
//            System.out.print("New Password: ");
//            String newPass = scanner.nextLine().trim();
//
//            boolean success = customerLoginService.changeLoginPassword(loggedInCustomer.getCustomerId(), oldPass, newPass);
//            if (success) {
//                System.out.println("Login password changed successfully.");
//            } else {
//                System.out.println("Failed to change login password. Check the old password.");
//            }
//        }
//
//        private static void changeTransactionPassword() {
//            System.out.println("\n--- Change Transaction Password ---");
//
//            System.out.print("Old Transaction Password: ");
//            String oldPass = scanner.nextLine().trim();
//
//            System.out.print("New Transaction Password: ");
//            String newPass = scanner.nextLine().trim();
//
//            boolean success = customerLoginService.changeTransactionPassword(loggedInCustomer.getCustomerId(), oldPass, newPass);
//            if (success) {
//                System.out.println("Transaction password changed successfully.");
//            } else {
//                System.out.println("Failed to change transaction password. Check the old transaction password.");
//            }
//        }
//
//        private static void unlockAccount() {
//            System.out.println("\n--- Account Unlock ---");
//
//            // You might add more verification/OTP here in real app
//            boolean success = customerLoginService.unlockAccount(loggedInCustomer.getCustomerId());
//            if (success) {
//                System.out.println("Account unlocked successfully.");
//            } else {
//                System.out.println("Failed to unlock account or account was not locked.");
//            }
//        }
//
//        private static void forgotCredentials() {
//            System.out.println("\n--- Forgot Customer ID / Password ---");
//            System.out.println("Currently, please contact customer support to recover your credentials.");
//            // You may implement OTP verification and reset password flow here
//        }
//    }
//}
