package com.oracle.dao;

import java.time.LocalDateTime;
import java.util.List;

import com.oracle.beans.Session;

/**
 * Session Data Access Object Interface
 */
public interface SessionDAO {
    
    boolean saveSession(Session session);
    boolean updateSession(Session session);
    Session getSession(String sessionId);
    List<Session> getActiveSessionsByUser(String userId);
    List<Session> getExpiredSessions();
    boolean deleteSession(String sessionId);
    boolean deleteExpiredSessions();
    boolean deleteAllUserSessions(String userId);
    
    // Session statistics
    long getActiveSessionCount();
    List<Session> getSessionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}