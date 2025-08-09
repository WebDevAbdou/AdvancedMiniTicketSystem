package com.ticketbooking.dao;

import com.ticketbooking.database.DBConnection;
import com.ticketbooking.model.User;
import com.ticketbooking.utils.EmailUtils;
import com.ticketbooking.utils.PasswordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User entities
 */
public class UserDAO {
    private static final Logger logger = LogManager.getLogger(UserDAO.class);

    /**
     * Get all users from the database
     * @return List of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                users.add(user);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all users", e);
        }

        return users;
    }

    /**
     * Get user by ID
     * @param id user ID
     * @return User object if found, null otherwise
     */
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving user by ID: " + id, e);
        }

        return null;
    }

    /**
     * Get user by username
     * @param username username to search for
     * @return User object if found, null otherwise
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving user by username: " + username, e);
        }

        return null;
    }

    /**
     * Authenticate a user
     * @param username username to authenticate
     * @param password password to verify
     * @return User object if authentication successful, null otherwise
     */
    public User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);

                    // Check if user is verified
                    boolean verified = false;
                    try {
                        verified = rs.getBoolean("verified");
                    } catch (SQLException e) {
                        // Field might not exist in older database schema
                        verified = true; // Assume verified for backward compatibility
                    }

                    if (!verified) {
                        logger.warn("User not verified: {}", username);
                        return null;
                    }

                    // Verify password
                    String storedPassword = user.getPassword();
                    boolean passwordMatches;

                    // Check if password is hashed (contains : character)
                    if (storedPassword.contains(":")) {
                        // Verify using password hash
                        passwordMatches = PasswordUtils.verifyPassword(password, storedPassword);
                    } else {
                        // Legacy plain text password (for backward compatibility)
                        passwordMatches = password.equals(storedPassword);
                    }

                    if (passwordMatches) {
                        // Update last login date
                        updateLastLogin(user.getId());
                        user.updateLastLogin();

                        logger.info("User authenticated successfully: {}", username);
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error authenticating user: {}", username, e);
        }

        logger.warn("Authentication failed for user: {}", username);
        return null;
    }

    /**
     * Add a new user to the database
     * @param user User object to add
     * @return true if successful, false otherwise
     */
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, role, email, full_name, phone, registration_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getFullName());
            pstmt.setString(6, user.getPhone());
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error adding user: {}", user.getUsername(), e);
        }

        return false;
    }

    /**
     * Register a new user
     * @param username Username
     * @param password Password
     * @param email Email
     * @param fullName Full name
     * @param phone Phone number
     * @return User object if registration successful, null otherwise
     */
    public User registerUser(String username, String password, String email, String fullName, String phone) {
        // Check if username or email already exists
        if (getUserByUsername(username) != null) {
            logger.warn("Username already exists: {}", username);
            return null;
        }

        if (getUserByEmail(email) != null) {
            logger.warn("Email already exists: {}", email);
            return null;
        }

        // Hash the password
        String hashedPassword = PasswordUtils.hashPassword(password);
        if (hashedPassword == null) {
            logger.error("Error hashing password for user: {}", username);
            return null;
        }

        // Create new user with regular user role
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setRole("user");
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setRegistrationDate(LocalDateTime.now());

        // Generate verification token
        String verificationToken = EmailUtils.generateVerificationToken();

        // Add user to database with verification token
        if (addUserWithVerification(user, verificationToken)) {
            // Send verification email
            boolean emailSent = EmailUtils.sendVerificationEmail(email, username, verificationToken);
            if (!emailSent) {
                logger.warn("Failed to send verification email to: {}", email);
            }

            logger.info("User registered successfully: {}", username);
            return user;
        }

        return null;
    }

    /**
     * Add a new user with verification token
     * @param user User object to add
     * @param verificationToken Verification token
     * @return true if successful, false otherwise
     */
    public boolean addUserWithVerification(User user, String verificationToken) {
        String sql = "INSERT INTO users (username, password, role, email, full_name, phone, " +
                     "registration_date, verification_token, verified, verification_expiry) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getFullName());
            pstmt.setString(6, user.getPhone());
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(8, verificationToken);
            pstmt.setBoolean(9, false); // Not verified initially

            // Set verification token expiry (24 hours from now)
            LocalDateTime expiryTime = LocalDateTime.now().plusHours(24);
            pstmt.setTimestamp(10, Timestamp.valueOf(expiryTime));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error adding user with verification: {}", user.getUsername(), e);
        }

        return false;
    }

    /**
     * Verify a user's email using verification token
     * @param token Verification token
     * @return true if verification successful, false otherwise
     */
    public boolean verifyUser(String token) {
        String sql = "UPDATE users SET verified = TRUE WHERE verification_token = ? AND verification_expiry > ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, token);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("User verified successfully with token: {}", token);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error verifying user with token: {}", token, e);
        }

        logger.warn("Verification failed for token: {}", token);
        return false;
    }

    /**
     * Get user by email
     * @param email Email to search for
     * @return User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving user by email: {}", email, e);
        }

        return null;
    }

    /**
     * Update an existing user in the database
     * @param user User object to update
     * @return true if successful, false otherwise
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, email = ?, " +
                     "full_name = ?, phone = ? WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getFullName());
            pstmt.setString(6, user.getPhone());
            pstmt.setInt(7, user.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating user with ID: {}", user.getId(), e);
            return false;
        }
    }

    /**
     * Update user's last login date
     * @param userId User ID
     * @return true if successful, false otherwise
     */
    public boolean updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login_date = ? WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating last login for user ID: {}", userId, e);
            return false;
        }
    }

    /**
     * Delete a user from the database
     * @param id user ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting user with ID: " + id, e);
            return false;
        }
    }

    /**
     * Map a ResultSet row to a User object
     * @param rs ResultSet containing user data
     * @return User object
     * @throws SQLException if a database access error occurs
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setEmail(rs.getString("email"));

        // Get additional fields if they exist
        try {
            user.setFullName(rs.getString("full_name"));
        } catch (SQLException e) {
            // Field might not exist in older database schema
        }

        try {
            user.setPhone(rs.getString("phone"));
        } catch (SQLException e) {
            // Field might not exist in older database schema
        }

        try {
            Timestamp registrationDate = rs.getTimestamp("registration_date");
            if (registrationDate != null) {
                user.setRegistrationDate(registrationDate.toLocalDateTime());
            }
        } catch (SQLException e) {
            // Field might not exist in older database schema
        }

        try {
            Timestamp lastLoginDate = rs.getTimestamp("last_login_date");
            if (lastLoginDate != null) {
                user.setLastLoginDate(lastLoginDate.toLocalDateTime());
            }
        } catch (SQLException e) {
            // Field might not exist in older database schema
        }

        return user;
    }
}
