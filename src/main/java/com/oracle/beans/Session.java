package com.oracle.beans;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
public class Session implements Serializable {

    @Id
    @Column(name = "session_id", length = 50, nullable = false)
    private String sessionId;

    @Column(name = "customerId", length = 20, nullable = false)
    private String userId;

    @Column(name = "user_type", length = 20, nullable = false)
    private String userType; // CUSTOMER or ADMIN

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "last_activity", nullable = false)
    private LocalDateTime lastActivity;

    @Column(name = "is_active", length = 1, nullable = false)
    private String isActive; // 'Y'/'N'

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    public Session() {
        this.loginTime = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        this.isActive = "Y";
    }

    public Session(String sessionId, String userId, String userType) {
        this();
        this.sessionId = sessionId;
        this.userId = userId;
        this.userType = userType;
    }

    // Session timeout check (30 minutes)
    @Transient
    public boolean isExpired() {
        return lastActivity.plusMinutes(30).isBefore(LocalDateTime.now());
    }

    @Transient
    public void updateActivity() {
        this.lastActivity = LocalDateTime.now();
    }

    // Getters and Setters...
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getCustomerId() { return userId; }
    public void setCustomerId(String userId) { this.userId = userId; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }

    public LocalDateTime getLastActivity() { return lastActivity; }
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }

    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
}
