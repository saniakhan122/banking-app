package com.oracle.business;

import com.oracle.beans.AdminUser;
import java.util.List;

public interface AdminUserService {

    // Basic Admin CRUD Operations
    boolean createAdmin(AdminUser admin);
    AdminUser getAdminById(String adminId);
    AdminUser getAdminByUsername(String username);
    boolean updateAdmin(AdminUser admin);
    boolean deleteAdmin(String adminId);

    // Authentication & Status
    boolean authenticate(String username, String password);
    boolean updateLastLogin(String adminId);
    boolean setAdminStatus(String adminId, boolean active);
    
    // Account Request Approvals
    boolean approveAccountRequest(String serviceReferenceNumber, String approvedByAdminId);
    boolean rejectAccountRequest(String serviceReferenceNumber, String rejectedByAdminId, String reason);

    // Admin Listing & Searching
    List<AdminUser> getAllAdmins();
    List<AdminUser> getActiveAdmins();
    List<AdminUser> getInactiveAdmins();
}
