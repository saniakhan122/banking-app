package com.oracle.beans;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ADMIN_USERS")
public class AdminUser {

    @Id
    @Column(name = "ADMIN_ID", length = 20, nullable = false)
    private String adminId;

    @Column(name = "USERNAME", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "FULL_NAME", length = 100, nullable = false)
    private String fullName;

    @Column(name = "EMAIL", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "PASSWORD_HASH", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "ROLE", length = 20)
    private String role;

    @Column(name = "IS_ACTIVE", length = 1)
    private String isActive; // Store 'Y' or 'N'

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "LAST_LOGIN")
    private LocalDateTime lastLogin;

    public AdminUser(String adminId, String username, String fullName, String email, String passwordHash, String role,
			String isActive, LocalDateTime createdAt, LocalDateTime lastLogin, String createdBy) {
		super();
		this.adminId = adminId;
		this.username = username;
		this.fullName = fullName;
		this.email = email;
		this.passwordHash = passwordHash;
		this.role = role;
		this.isActive = isActive;
		this.createdAt = createdAt;
		this.lastLogin = lastLogin;
		this.createdBy = createdBy;
	}

	@Override
	public String toString() {
		return "AdminUser [adminId=" + adminId + ", username=" + username + ", fullName=" + fullName + ", email="
				+ email + ", passwordHash=" + passwordHash + ", role=" + role + ", isActive=" + isActive
				+ ", createdAt=" + createdAt + ", lastLogin=" + lastLogin + ", createdBy=" + createdBy + "]";
	}

	@Column(name = "CREATED_BY", length = 20)
    private String createdBy;

    public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public AdminUser() {
        this.createdAt = LocalDateTime.now();
        this.isActive = "Y";
        this.role = "ADMIN";
    }}