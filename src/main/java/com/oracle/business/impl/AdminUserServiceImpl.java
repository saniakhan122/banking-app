package com.oracle.business.impl;

import com.oracle.beans.AdminUser;
import com.oracle.dao.AdminUserDAO;
import com.oracle.business.AdminUserService;
import com.oracle.business.util.ServiceFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminUserServiceImpl implements AdminUserService {

    private static final Logger logger = Logger.getLogger(AdminUserServiceImpl.class.getName());
    private AdminUserDAO adminUserDAO;

    public AdminUserServiceImpl() {
        this.adminUserDAO = ServiceFactory.getAdminUserDAO();
    }
    
    public AdminUserServiceImpl(AdminUserDAO adminUserDAO) {
        this.adminUserDAO = adminUserDAO;
    }

    @Override
    public boolean createAdmin(AdminUser admin) {
        try {
            if (admin == null) {
                logger.warning("Attempted to create null admin");
                return false;
            }
            // Could add more validations here if needed
            return adminUserDAO.saveAdmin(admin);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating admin", e);
            return false;
        }
    }

    @Override
    public AdminUser getAdminById(String adminId) {
        try {
            return adminUserDAO.getAdminById(adminId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting admin by ID: " + adminId, e);
            return null;
        }
    }

    @Override
    public AdminUser getAdminByUsername(String username) {
        try {
            return adminUserDAO.getAdminByUsername(username);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting admin by username: " + username, e);
            return null;
        }
    }

    @Override
    public boolean updateAdmin(AdminUser admin) {
        try {
            if (admin == null || admin.getAdminId() == null) {
                logger.warning("Invalid admin data for update");
                return false;
            }
            return adminUserDAO.updateAdmin(admin);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating admin: " + (admin != null ? admin.getAdminId() : "null"), e);
            return false;
        }
    }

    @Override
    public boolean deleteAdmin(String adminId) {
        try {
            return adminUserDAO.deleteAdmin(adminId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting admin: " + adminId, e);
            return false;
        }
    }

    @Override
    public boolean authenticate(String username, String password) {
        try {
            if (username == null || password == null) {
                logger.warning("Username or password is null during authentication");
                return false;
            }
            // Note: password should be hashed before passing here, depending on your app design
            return adminUserDAO.validateLogin(username, password);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error authenticating admin: " + username, e);
            return false;
        }
    }

    @Override
    public boolean updateLastLogin(String adminId) {
        try {
            return adminUserDAO.updateLastLogin(adminId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating last login for admin: " + adminId, e);
            return false;
        }
    }

    @Override
    public boolean setAdminStatus(String adminId, boolean active) {
        try {
            return adminUserDAO.changeAdminStatus(adminId, active);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error changing status for admin: " + adminId, e);
            return false;
        }
    }

    @Override
    public boolean approveAccountRequest(String serviceReferenceNumber, String approvedByAdminId) {
        try {
            return adminUserDAO.approveAccount(serviceReferenceNumber, approvedByAdminId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error approving account request: " + serviceReferenceNumber, e);
            return false;
        }
    }

    @Override
    public boolean rejectAccountRequest(String serviceReferenceNumber, String rejectedByAdminId, String reason) {
        try {
            return adminUserDAO.rejectAccount(serviceReferenceNumber, rejectedByAdminId, reason);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error rejecting account request: " + serviceReferenceNumber, e);
            return false;
        }
    }

    @Override
    public List<AdminUser> getAllAdmins() {
        try {
            return adminUserDAO.getAllAdmins();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving all admins", e);
            return null;
        }
    }

    @Override
    public List<AdminUser> getActiveAdmins() {
        try {
            return adminUserDAO.getActiveAdmins();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving active admins", e);
            return null;
        }
    }

    @Override
    public List<AdminUser> getInactiveAdmins() {
        try {
            return adminUserDAO.getInactiveAdmins();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving inactive admins", e);
            return null;
        }
    }
}
