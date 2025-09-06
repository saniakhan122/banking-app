package com.oracle.dao;

import com.oracle.beans.AdminUser;
import java.util.List;

public interface AdminUserDAO {
    // Basic Admin CRUD
    boolean saveAdmin(AdminUser admin);
    AdminUser getAdminById(String adminId);
    AdminUser getAdminByUsername(String username);
    boolean updateAdmin(AdminUser admin);
    boolean deleteAdmin(String adminId);

    // Authentication & Status
    boolean validateLogin(String username, String passwordHash);
    boolean updateLastLogin(String adminId);
    boolean changeAdminStatus(String adminId, boolean active);

    // Approve/Reject Account Creation Requests
    boolean approveAccount(String serviceReferenceNumber, String approvedBy);
    boolean rejectAccount(String serviceReferenceNumber, String rejectedBy, String reason);

    // Admin List & Search
    List<AdminUser> getAllAdmins();
    List<AdminUser> getActiveAdmins();
    List<AdminUser> getInactiveAdmins();
}
