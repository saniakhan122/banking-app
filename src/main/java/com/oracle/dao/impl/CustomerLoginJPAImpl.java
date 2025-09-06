package com.oracle.dao.impl;

import com.oracle.beans.CustomerLogin;
import com.oracle.dao.CustomerLoginDAO;
import com.oracle.dao.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

public class CustomerLoginJPAImpl implements CustomerLoginDAO {

    private static final Logger logger = Logger.getLogger(CustomerLoginJPAImpl.class.getName());

    @Override
    public boolean save(CustomerLogin customerLogin) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(customerLogin);
            em.getTransaction().commit();
            logger.info("CustomerLogin saved for: " + customerLogin.getCustomerId());
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error saving CustomerLogin", e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean update(CustomerLogin customerLogin) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(customerLogin);
            em.getTransaction().commit();
            logger.info("CustomerLogin updated for: " + customerLogin.getCustomerId());
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error updating CustomerLogin", e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public CustomerLogin findByCustomerId(String customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(CustomerLogin.class, customerId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding CustomerLogin by customerId " + customerId, e);
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public CustomerLogin findByUserId(String userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<CustomerLogin> query = em.createQuery(
                "SELECT cl FROM CustomerLogin cl WHERE cl.userId = :userId", CustomerLogin.class);
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.info("No CustomerLogin found for userId " + userId);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding CustomerLogin by userId " + userId, e);
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateFailedLoginAttempts(String customerId, int attempts) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            CustomerLogin cl = em.find(CustomerLogin.class, customerId);
            if (cl != null) {
                cl.setFailedLoginAttempts(attempts);
                em.merge(cl);
                em.getTransaction().commit();
                return true;
            } else {
                em.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error updating failed attempts for " + customerId, e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean incrementFailedLoginAttempts(String customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            CustomerLogin cl = em.find(CustomerLogin.class, customerId);
            if (cl != null) {
                int current = cl.getFailedLoginAttempts() != null ? cl.getFailedLoginAttempts() : 0;
                cl.setFailedLoginAttempts(current + 1);
                em.merge(cl);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error incrementing failed login attempts for " + customerId, e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean resetFailedLoginAttempts(String customerId) {
        return updateFailedLoginAttempts(customerId, 0);
    }

    @Override
    public boolean lockAccount(String customerId, LocalDateTime lockedUntil) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            CustomerLogin cl = em.find(CustomerLogin.class, customerId);
            if (cl != null) {
                cl.setIsLocked("Y");
                cl.setLockedUntil(lockedUntil);
                em.merge(cl);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error locking account for " + customerId, e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean unlockAccount(String customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            CustomerLogin cl = em.find(CustomerLogin.class, customerId);
            if (cl != null) {
                cl.setIsLocked("N");
                cl.setLockedUntil(null);
                cl.setFailedLoginAttempts(0);
                em.merge(cl);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error unlocking account for " + customerId, e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateLastLogin(String customerId, LocalDateTime lastLogin) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            CustomerLogin cl = em.find(CustomerLogin.class, customerId);
            if (cl != null) {
                cl.setLastLogin(lastLogin);
                em.merge(cl);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error updating last login for " + customerId, e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateLoginPassword(String customerId, String hashedPassword) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            CustomerLogin cl = em.find(CustomerLogin.class, customerId);
            if (cl != null) {
                cl.setLoginPassword(hashedPassword);
                cl.setPasswordChangedAt(LocalDateTime.now());
                em.merge(cl);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error updating login password for " + customerId, e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateTransactionPassword(String customerId, String hashedPassword) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            CustomerLogin cl = em.find(CustomerLogin.class, customerId);
            if (cl != null) {
                cl.setTransactionPassword(hashedPassword);
                em.merge(cl);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error updating transaction password for " + customerId, e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean isInternetBankingEnabled(String customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            CustomerLogin cl = em.find(CustomerLogin.class, customerId);
            return cl != null && "Y".equalsIgnoreCase(cl.getInternetBankingEnabled());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking internet banking status for " + customerId, e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean enableInternetBanking(String customerId) {
        return setInternetBankingStatus(customerId, "Y");
    }

    @Override
    public boolean disableInternetBanking(String customerId) {
        return setInternetBankingStatus(customerId, "N");
    }

    private boolean setInternetBankingStatus(String customerId, String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            CustomerLogin cl = em.find(CustomerLogin.class, customerId);
            if (cl != null) {
                cl.setInternetBankingEnabled(status);
                em.merge(cl);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error setting internet banking status for " + customerId, e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public List<CustomerLogin> findAllLockedAccounts() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<CustomerLogin> query = em.createQuery(
                "SELECT c FROM CustomerLogin c " +
                "WHERE c.isLocked = 'Y' AND c.lockedUntil IS NOT NULL AND c.lockedUntil > :now",
                CustomerLogin.class
            );
            query.setParameter("now", LocalDateTime.now());
            return query.getResultList();
        } finally {
            em.close();
        }
    }

}
