package com.oracle.business.impl;

import com.oracle.beans.Session;
import com.oracle.business.SessionService;
import com.oracle.dao.SessionDAO;
import com.oracle.business.util.ServiceFactory;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionServiceImpl implements SessionService {

    private static final Logger logger = Logger.getLogger(SessionServiceImpl.class.getName());
    private SessionDAO sessionDAO;

    public SessionServiceImpl() {
        this.sessionDAO = ServiceFactory.getSessionDAO();
    }

    @Override
    public boolean saveSession(Session session) {
        try {
            if (session == null) {
                logger.warning("Attempted to save null session");
                return false;
            }
            return sessionDAO.saveSession(session);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving session", e);
            return false;
        }
    }

    @Override
    public boolean updateSession(Session session) {
        try {
            if (session == null) {
                logger.warning("Attempted to update null session");
                return false;
            }
            return sessionDAO.updateSession(session);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating session", e);
            return false;
        }
    }

    @Override
    public Session getSessionById(String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                logger.warning("Session ID is null or empty");
                return null;
            }
            return sessionDAO.getSession(sessionId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving session by id: " + sessionId, e);
            return null;
        }
    }

    @Override
    public boolean deleteSession(String sessionId) {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                logger.warning("Session ID is null or empty");
                return false;
            }
            return sessionDAO.deleteSession(sessionId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting session with id: " + sessionId, e);
            return false;
        }
    }

    @Override
    public List<Session> getActiveSessionsByUser(String customerId) {
        try {
            if (customerId == null || customerId.isEmpty()) {
                logger.warning("User ID is null or empty");
                return Collections.emptyList();
            }
            List<Session> sessions = sessionDAO.getActiveSessionsByUser(customerId);
            return sessions != null ? sessions : Collections.emptyList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving active sessions for user: " + customerId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Session> getExpiredSessions() {
        try {
            List<Session> sessions = sessionDAO.getExpiredSessions();
            return sessions != null ? sessions : Collections.emptyList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving expired sessions", e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean deleteExpiredSessions() {
        try {
            return sessionDAO.deleteExpiredSessions();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting expired sessions", e);
            return false;
        }
    }

    @Override
    public boolean deleteAllSessionsByUser(String customerId) {
        try {
            if (customerId == null || customerId.isEmpty()) {
                logger.warning("User ID is null or empty");
                return false;
            }
            return sessionDAO.deleteAllUserSessions(customerId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting all sessions for user: " + customerId, e);
            return false;
        }
    }

    @Override
    public long getActiveSessionCount() {
        try {
            return sessionDAO.getActiveSessionCount();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving active session count", e);
            return 0L;
        }
    }

    @Override
    public List<Session> getSessionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            if (startDate == null || endDate == null) {
                logger.warning("Start date or end date is null");
                return Collections.emptyList();
            }
            List<Session> sessions = sessionDAO.getSessionsByDateRange(startDate, endDate);
            return sessions != null ? sessions : Collections.emptyList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving sessions by date range", e);
            return Collections.emptyList();
        }
    }
}
