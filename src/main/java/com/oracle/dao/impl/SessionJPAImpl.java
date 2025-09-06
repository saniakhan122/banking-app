package com.oracle.dao.impl;

import com.oracle.beans.Session;
import com.oracle.dao.SessionDAO;
import com.oracle.dao.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionJPAImpl implements SessionDAO {

    private static final Logger logger = Logger.getLogger(SessionJPAImpl.class.getName());

    @Override
    public boolean saveSession(Session session) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(session);
            em.getTransaction().commit();
            logger.info("Session saved successfully with ID: " + session.getSessionId());
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error saving session", e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateSession(Session session) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(session);
            em.getTransaction().commit();
            logger.info("Session updated successfully: " + session.getSessionId());
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error updating session", e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public Session getSession(String sessionId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Session.class, sessionId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving session by ID: " + sessionId, e);
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Session> getActiveSessionsByUser(String customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Session> query = em.createQuery(
                "SELECT s FROM Session s " +
                "WHERE s.customerId = :customerId " +
                "AND s.expiryTime > :now", Session.class);
            query.setParameter("customerId", customerId);
            query.setParameter("now", LocalDateTime.now());
            return query.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting active sessions for user " + customerId, e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Session> getExpiredSessions() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Session> query = em.createQuery(
                "SELECT s FROM Session s WHERE s.expiryTime <= :now", Session.class);
            query.setParameter("now", LocalDateTime.now());
            return query.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving expired sessions", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean deleteSession(String sessionId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Session session = em.find(Session.class, sessionId);
            if (session != null) {
                em.remove(session);
                em.getTransaction().commit();
                logger.info("Session deleted successfully: " + sessionId);
                return true;
            } else {
                em.getTransaction().rollback();
                logger.warning("No session found to delete: " + sessionId);
                return false;
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error deleting session: " + sessionId, e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean deleteExpiredSessions() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            int deletedCount = em.createQuery(
                "DELETE FROM Session s WHERE s.expiryTime <= :now")
                .setParameter("now", LocalDateTime.now())
                .executeUpdate();
            em.getTransaction().commit();
            logger.info("Deleted expired sessions: " + deletedCount);
            return deletedCount > 0;
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error deleting expired sessions", e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean deleteAllUserSessions(String customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            int deletedCount = em.createQuery(
                "DELETE FROM Session s WHERE s.customerId = :customerId")
                .setParameter("customerId", customerId)
                .executeUpdate();
            em.getTransaction().commit();
            logger.info("Deleted all sessions for user: " + customerId + ", count: " + deletedCount);
            return deletedCount > 0;
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error deleting all sessions for user: " + customerId, e);
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public long getActiveSessionCount() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(s) FROM Session s WHERE s.expiryTime > :now", Long.class);
            query.setParameter("now", LocalDateTime.now());
            return query.getSingleResult();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting active session count", e);
            return 0;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Session> getSessionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Session> query = em.createQuery(
                "SELECT s FROM Session s WHERE s.createdAt BETWEEN :startDate AND :endDate",
                Session.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving sessions by date range", e);
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }
}
