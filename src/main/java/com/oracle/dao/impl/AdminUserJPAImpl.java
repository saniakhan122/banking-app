// com.oracle.dao.impl.AdminUserJPAImpl.java
package com.oracle.dao.impl;

import com.oracle.beans.AdminUser;
import com.oracle.dao.AdminUserDAO;
import com.oracle.dao.util.JPAUtil;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

public class AdminUserJPAImpl implements AdminUserDAO {

    private static final Logger logger = Logger.getLogger(AdminUserJPAImpl.class.getName());

    @Override
    public boolean saveAdmin(AdminUser admin) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            admin.setCreatedAt(LocalDateTime.now());
            em.persist(admin);
            em.getTransaction().commit();
            logger.info("Admin created successfully with ID: " + admin.getAdminId());
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.severe("Error creating admin: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public AdminUser getAdminById(String adminId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(AdminUser.class, adminId);
        } catch (Exception e) {
            logger.severe("Error finding admin by ID: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public AdminUser getAdminByUsername(String username) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<AdminUser> query = em.createQuery(
                    "SELECT a FROM AdminUser a WHERE a.username = :username", AdminUser.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.info("No admin found with username: " + username);
            return null;
        } catch (Exception e) {
            logger.severe("Error finding admin by username: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateAdmin(AdminUser admin) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(admin);
            em.getTransaction().commit();
            logger.info("Admin updated successfully: " + admin.getAdminId());
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.severe("Error updating admin: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean deleteAdmin(String adminId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            AdminUser admin = em.find(AdminUser.class, adminId);
            if (admin != null) {
                em.remove(admin);
                em.getTransaction().commit();
                logger.info("Admin deleted: " + adminId);
                return true;
            }
            em.getTransaction().rollback();
            logger.warning("Admin not found for deletion: " + adminId);
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.severe("Error deleting admin: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean validateLogin(String username, String passwordHash) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<AdminUser> query = em.createQuery(
                    "SELECT a FROM AdminUser a WHERE a.username = :username AND a.passwordHash = :passwordHash AND a.isActive = 'Y'",
                    AdminUser.class);
            query.setParameter("username", username);
            query.setParameter("passwordHash", passwordHash);
            return query.getSingleResult() != null;
        } catch (NoResultException e) {
            logger.info("Invalid login attempt for username: " + username);
            return false;
        } catch (Exception e) {
            logger.severe("Error validating login: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateLastLogin(String adminId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            AdminUser admin = em.find(AdminUser.class, adminId);
            if (admin != null) {
                admin.setLastLogin(LocalDateTime.now());
                em.merge(admin);
                em.getTransaction().commit();
                logger.info("Updated last login for admin: " + adminId);
                return true;
            }
            em.getTransaction().rollback();
            logger.warning("Admin not found for last login update: " + adminId);
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.severe("Error updating last login: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean changeAdminStatus(String adminId, boolean active) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            AdminUser admin = em.find(AdminUser.class, adminId);
            if (admin != null) {
            	admin.setIsActive(active ? "Y" : "N");

                em.merge(admin);
                em.getTransaction().commit();
                logger.info("Admin status changed: " + adminId + " to " + active);
                return true;
            }
            em.getTransaction().rollback();
            logger.warning("Admin not found for status change: " + adminId);
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.severe("Error changing admin status: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean approveAccount(String serviceReferenceNumber, String approvedBy) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            int updated = em.createQuery(
                    "UPDATE AccountRequest ar SET ar.status = 'APPROVED', ar.approvedBy = :approvedBy, ar.approvedAt = :approvedAt " +
                    "WHERE ar.serviceReferenceNumber = :ref AND ar.status = 'PENDING'")
                    .setParameter("approvedBy", approvedBy)
                    .setParameter("approvedAt", LocalDateTime.now())
                    .setParameter("ref", serviceReferenceNumber)
                    .executeUpdate();
            em.getTransaction().commit();
            return updated > 0;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.severe("Error approving account request: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean rejectAccount(String serviceReferenceNumber, String rejectedBy, String reason) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            int updated = em.createQuery(
                    "UPDATE AccountRequest ar SET ar.status = 'REJECTED', ar.rejectedBy = :rejectedBy, ar.rejectionReason = :reason, ar.rejectedAt = :rejectedAt " +
                    "WHERE ar.serviceReferenceNumber = :ref AND ar.status = 'PENDING'")
                    .setParameter("rejectedBy", rejectedBy)
                    .setParameter("reason", reason)
                    .setParameter("rejectedAt", LocalDateTime.now())
                    .setParameter("ref", serviceReferenceNumber)
                    .executeUpdate();
            em.getTransaction().commit();
            return updated > 0;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.severe("Error rejecting account request: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public List<AdminUser> getAllAdmins() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT a FROM AdminUser a ORDER BY a.createdAt DESC", AdminUser.class)
                    .getResultList();
        } catch (Exception e) {
            logger.severe("Error fetching all admins: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<AdminUser> getActiveAdmins() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT a FROM AdminUser a WHERE a.isActive = 'Y' ORDER BY a.createdAt DESC", AdminUser.class)
                    .getResultList();
        } catch (Exception e) {
            logger.severe("Error fetching active admins: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<AdminUser> getInactiveAdmins() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT a FROM AdminUser a WHERE a.isActive = 'N' ORDER BY a.createdAt DESC", AdminUser.class)
                    .getResultList();
        } catch (Exception e) {
            logger.severe("Error fetching inactive admins: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
}
