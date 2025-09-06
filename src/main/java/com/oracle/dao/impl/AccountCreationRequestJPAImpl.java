package com.oracle.dao.impl;

import com.oracle.beans.AccountCreationRequest;
import com.oracle.beans.AdminUser;
import com.oracle.dao.AccountCreationRequestDAO;
import com.oracle.dao.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountCreationRequestJPAImpl implements AccountCreationRequestDAO {

    private static final Logger logger = Logger.getLogger(AccountCreationRequestJPAImpl.class.getName());

    @Override
    public boolean save(AccountCreationRequest request) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (request.getSubmittedAt() == null) {
                request.setSubmittedAt(LocalDateTime.now());
            }
            em.persist(request);
            em.getTransaction().commit();
            logger.info("Account request saved: " + request.getRequestId());
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error saving account request", e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean update(AccountCreationRequest request) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(request);
            em.getTransaction().commit();
            logger.info("Account request updated: " + request.getRequestId());
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error updating account request", e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public AccountCreationRequest findByRequestId(String requestId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(AccountCreationRequest.class, requestId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding request by ID: " + requestId, e);
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public AccountCreationRequest findByServiceReferenceNo(String serviceRefNo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<AccountCreationRequest> q = em.createQuery(
                "SELECT r FROM AccountCreationRequest r WHERE r.serviceReferenceNo = :refNo", 
                AccountCreationRequest.class);
            q.setParameter("refNo", serviceRefNo);
            return q.getSingleResult();
        } catch (NoResultException e) {
            logger.info("No request found for service reference: " + serviceRefNo);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving request by service reference", e);
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<AccountCreationRequest> findByStatus(String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<AccountCreationRequest> q = em.createQuery(
                "SELECT r FROM AccountCreationRequest r WHERE r.status = :status ORDER BY r.submittedAt DESC", 
                AccountCreationRequest.class);
            q.setParameter("status", status);
            return q.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding requests by status", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<AccountCreationRequest> findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<AccountCreationRequest> q = em.createQuery(
                "SELECT r FROM AccountCreationRequest r WHERE r.email = :email", 
                AccountCreationRequest.class);
            q.setParameter("email", email);
            return q.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding requests by email", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<AccountCreationRequest> findByMobile(String mobileNumber) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<AccountCreationRequest> q = em.createQuery(
                "SELECT r FROM AccountCreationRequest r WHERE r.mobileNumber = :mobile", 
                AccountCreationRequest.class);
            q.setParameter("mobile", mobileNumber);
            return q.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding requests by mobile", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<AccountCreationRequest> findByAadhar(String aadharNumber) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<AccountCreationRequest> q = em.createQuery(
                "SELECT r FROM AccountCreationRequest r WHERE r.aadharNumber = :aadhar", 
                AccountCreationRequest.class);
            q.setParameter("aadhar", aadharNumber);
            return q.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding requests by aadhar", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean approveRequest(String requestId, String username) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            AccountCreationRequest req = em.find(AccountCreationRequest.class, requestId);
            if (req != null) {
                req.setStatus("APPROVED");
                req.setProcessedAt(LocalDateTime.now());
                req.setRejectionReason(null);

                if (username != null) {
                    // Fetch admin by username instead of using getReference
                    AdminUser admin = em.createQuery(
                            "SELECT a FROM AdminUser a WHERE a.username = :username", AdminUser.class)
                            .setParameter("username", username)
                            .getSingleResult();
                    req.setProcessedBy(admin);
                }

                em.merge(req);
                em.getTransaction().commit();
                logger.info("Approved account request: " + requestId);
                return true;
            }

            em.getTransaction().rollback();
            logger.warning("No such account request to approve: " + requestId);
            return false;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error approving account request", e);
            return false;
        } finally {
            em.close();
        }
    }


    @Override
    public boolean rejectRequest(String requestId, String username, String rejectionReason) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            AccountCreationRequest req = em.find(AccountCreationRequest.class, requestId);
            if (req != null) {
                req.setStatus("REJECTED");
                req.setProcessedAt(LocalDateTime.now());
                req.setRejectionReason(rejectionReason);

                if (username != null) {
                    // Fetch admin by username instead of using getReference
                    AdminUser admin = em.createQuery(
                            "SELECT a FROM AdminUser a WHERE a.username = :username", AdminUser.class)
                            .setParameter("username", username)
                            .getSingleResult();
                    req.setProcessedBy(admin);
                }

                em.merge(req);
                em.getTransaction().commit();
                logger.info("Rejected account request: " + requestId);
                return true;
            }

            em.getTransaction().rollback();
            logger.warning("No such account request to reject: " + requestId);
            return false;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error rejecting account request", e);
            return false;
        } finally {
            em.close();
        }
    }


    @Override
    public List<AccountCreationRequest> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<AccountCreationRequest> q = em.createQuery(
                "SELECT r FROM AccountCreationRequest r ORDER BY r.submittedAt DESC", 
                AccountCreationRequest.class);
            return q.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving all account requests", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<AccountCreationRequest> findSubmittedBetween(LocalDateTime start, LocalDateTime end) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<AccountCreationRequest> q = em.createQuery(
                "SELECT r FROM AccountCreationRequest r WHERE r.submittedAt BETWEEN :start AND :end", 
                AccountCreationRequest.class);
            q.setParameter("start", start);
            q.setParameter("end", end);
            return q.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving requests between dates", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean delete(String requestId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            AccountCreationRequest req = em.find(AccountCreationRequest.class, requestId);
            if (req != null) {
                em.remove(req);
                em.getTransaction().commit();
                logger.info("Deleted request: " + requestId);
                return true;
            }
            em.getTransaction().rollback();
            logger.warning("No such account request to delete: " + requestId);
            return false;
        } catch (Exception e) {
            if(em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error deleting account request", e);
            return false;
        } finally {
            em.close();
        }
    }
}
