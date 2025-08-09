package com.ticketbooking.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a user in the ticket booking system
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String email;
    private String fullName;
    private String phone;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLoginDate;

    // Default constructor
    public User() {
        this.registrationDate = LocalDateTime.now();
    }

    // Constructor with all fields except id
    public User(String username, String password, String role, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.registrationDate = LocalDateTime.now();
    }

    // Constructor with all fields including id
    public User(int id, String username, String password, String role, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.registrationDate = LocalDateTime.now();
    }

    // Constructor with additional fields
    public User(String username, String password, String role, String email,
                String fullName, String phone) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.registrationDate = LocalDateTime.now();
    }

    // Constructor with all fields
    public User(int id, String username, String password, String role, String email,
                String fullName, String phone, LocalDateTime registrationDate,
                LocalDateTime lastLoginDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.registrationDate = registrationDate;
        this.lastLoginDate = lastLoginDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get full name
     * @return Full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Set full name
     * @param fullName Full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Get phone number
     * @return Phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Set phone number
     * @param phone Phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Get registration date
     * @return Registration date
     */
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    /**
     * Set registration date
     * @param registrationDate Registration date
     */
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    /**
     * Get last login date
     * @return Last login date
     */
    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    /**
     * Set last login date
     * @param lastLoginDate Last login date
     */
    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    /**
     * Check if user is an admin
     * @return true if user has admin role, false otherwise
     */
    public boolean isAdmin() {
        return "admin".equals(role);
    }

    /**
     * Update last login date to current time
     */
    public void updateLastLogin() {
        this.lastLoginDate = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
               Objects.equals(username, user.username) &&
               Objects.equals(password, user.password) &&
               Objects.equals(role, user.role) &&
               Objects.equals(email, user.email) &&
               Objects.equals(fullName, user.fullName) &&
               Objects.equals(phone, user.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, role, email, fullName, phone);
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", role='" + role + '\'' +
               ", email='" + email + '\'' +
               ", fullName='" + fullName + '\'' +
               ", phone='" + phone + '\'' +
               ", registrationDate=" + registrationDate +
               ", lastLoginDate=" + lastLoginDate +
               '}';
    }
}
