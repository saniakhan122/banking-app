package com.oracle.business;

import com.oracle.beans.Session;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionService {

    // CRUD operations
    boolean saveSession(Session session);
    boolean updateSession(Session session);
    Session getSessionById(String sessionId);
    boolean deleteSession(String sessionId);

    // Query operations
    List<Session> getActiveSessionsByUser(String userId);
    List<Session> getExpiredSessions();

    // Bulk delete operations
    boolean deleteExpiredSessions();
    boolean deleteAllSessionsByUser(String userId);

    // Statistics and reporting
    long getActiveSessionCount();
    List<Session> getSessionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
