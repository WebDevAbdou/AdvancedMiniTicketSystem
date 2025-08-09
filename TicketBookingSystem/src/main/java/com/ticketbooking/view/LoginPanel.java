package com.ticketbooking.view;

import com.ticketbooking.controller.UserController;
import com.ticketbooking.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for user login with separate tabs for user and admin login
 */
@SuppressWarnings("unused")
public class LoginPanel extends JPanel {
    private static final Logger logger = LogManager.getLogger(LoginPanel.class);

    // Colors
    private static final Color PRIMARY_COLOR = new Color(45, 125, 154);
    private static final Color ACCENT_COLOR = new Color(57, 105, 138);
    private static final Color ERROR_COLOR = new Color(200, 0, 0);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);

    // Fonts
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 28);
    private static final Font SUBTITLE_FONT = new Font("Arial", Font.ITALIC, 16);
    private static final Font HEADING_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font BOLD_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font SMALL_FONT = new Font("Arial", Font.ITALIC, 12);

    private final MainFrame mainFrame;
    private final UserController userController;

    // User login fields
    private JTextField userUsernameField;
    private JPasswordField userPasswordField;
    private JLabel userStatusLabel;

    // Admin login fields
    private JTextField adminUsernameField;
    private JPasswordField adminPasswordField;
    private JLabel adminStatusLabel;

    /**
     * Constructor
     * @param mainFrame reference to the main frame
     */
    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userController = new UserController();

        // Set up the panel
        setLayout(new BorderLayout());

        // Create components
        initializeComponents();

        logger.info("Login panel initialized");
    }

    /**
     * Initialize panel components
     */
    private void initializeComponents() {
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        // Create header panel with logo and title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Try to load logo image
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/ticket_logo.png"));
            if (logoIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                logoLabel.setIcon(logoIcon);
                headerPanel.add(logoLabel, BorderLayout.WEST);
            }
        } catch (Exception e) {
            logger.warn("Could not load logo image", e);
        }

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Smart Ticket Booking System Pro");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Your one-stop solution for event ticketing");
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(new Color(240, 240, 240));

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        headerPanel.add(titlePanel, BorderLayout.CENTER);

        // Create login form panel with card-like appearance
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(30, 30, 30, 30),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            )
        ));
        formWrapper.setBackground(Color.WHITE);

        // Create tabbed pane for user and admin login
        JTabbedPane loginTabs = new JTabbedPane();
        loginTabs.setFont(BOLD_FONT);

        // Create user login panel
        JPanel userLoginPanel = createLoginPanel("User Login", false);

        // Create admin login panel
        JPanel adminLoginPanel = createLoginPanel("Admin Login", true);

        // Add tabs
        loginTabs.addTab("User", userLoginPanel);
        loginTabs.addTab("Admin", adminLoginPanel);

        // Add tabbed pane to form wrapper
        formWrapper.add(loginTabs, BorderLayout.CENTER);

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formWrapper, BorderLayout.CENTER);

        // Add main panel to this panel
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Create a login panel (user or admin)
     * @param title Panel title
     * @param isAdmin Whether this is an admin login panel
     * @return The created panel
     */
    private JPanel createLoginPanel(String title, boolean isAdmin) {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(HEADING_FONT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(REGULAR_FONT);

        JTextField usernameField;
        if (isAdmin) {
            adminUsernameField = new JTextField(20);
            usernameField = adminUsernameField;
        } else {
            userUsernameField = new JTextField(20);
            usernameField = userUsernameField;
        }

        usernameField.setFont(REGULAR_FONT);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(usernameField, gbc);

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(REGULAR_FONT);

        JPasswordField passwordField;
        if (isAdmin) {
            adminPasswordField = new JPasswordField(20);
            passwordField = adminPasswordField;
        } else {
            userPasswordField = new JPasswordField(20);
            passwordField = userPasswordField;
        }

        passwordField.setFont(REGULAR_FONT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Add action listener for Enter key
        passwordField.addActionListener(e -> {
            if (isAdmin) {
                handleAdminLogin();
            } else {
                handleUserLogin();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        // Login button
        JButton loginButton = new JButton(isAdmin ? "Admin Login" : "Login");
        loginButton.setFont(BOLD_FONT);
        loginButton.setBackground(isAdmin ? new Color(128, 0, 0) : PRIMARY_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (isAdmin) {
            loginButton.addActionListener(e -> handleAdminLogin());
        } else {
            loginButton.addActionListener(e -> handleUserLogin());
        }

        // Guest login button (only for user panel)
        JButton secondButton;
        if (isAdmin) {
            secondButton = new JButton("Cancel");
            secondButton.setBackground(LIGHT_GRAY);
            secondButton.setForeground(new Color(80, 80, 80));
            secondButton.addActionListener(e -> clearAdminFields());
        } else {
            secondButton = new JButton("Continue as Guest");
            secondButton.setBackground(LIGHT_GRAY);
            secondButton.setForeground(new Color(80, 80, 80));
            secondButton.addActionListener(e -> handleGuestLogin());
        }

        secondButton.setFont(REGULAR_FONT);
        secondButton.setFocusPainted(false);
        secondButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        secondButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(loginButton);
        buttonPanel.add(secondButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        formPanel.add(buttonPanel, gbc);

        // Status label
        JLabel statusLabel;
        if (isAdmin) {
            adminStatusLabel = new JLabel(" ");
            statusLabel = adminStatusLabel;
        } else {
            userStatusLabel = new JLabel(" ");
            statusLabel = userStatusLabel;
        }

        statusLabel.setForeground(ERROR_COLOR);
        statusLabel.setFont(REGULAR_FONT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 8, 8, 8);
        formPanel.add(statusLabel, gbc);

        // Help text and registration link (only for user panel)
        JPanel helpPanel = new JPanel(new BorderLayout());
        helpPanel.setBackground(Color.WHITE);
        helpPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        if (isAdmin) {
            // Admin help text
            JLabel helpLabel = new JLabel("<html><body style='text-align: center;'>" +
                "Admin login is restricted to authorized personnel only.<br>" +
                "Default admin: username = 'admin', password = 'admin123'" +
                "</body></html>");
            helpLabel.setFont(SMALL_FONT);
            helpLabel.setForeground(new Color(100, 100, 100));
            helpLabel.setHorizontalAlignment(SwingConstants.CENTER);
            helpPanel.add(helpLabel, BorderLayout.CENTER);
        } else {
            // User help text
            JLabel helpLabel = new JLabel("<html><body style='text-align: center;'>" +
                "Guest login allows browsing events without an account" +
                "</body></html>");
            helpLabel.setFont(SMALL_FONT);
            helpLabel.setForeground(new Color(100, 100, 100));
            helpLabel.setHorizontalAlignment(SwingConstants.CENTER);
            helpPanel.add(helpLabel, BorderLayout.NORTH);

            // Registration link (only for user panel)
            JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            registerPanel.setBackground(Color.WHITE);
            registerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

            JLabel registerLabel = new JLabel("Don't have an account?");
            registerLabel.setFont(REGULAR_FONT);

            JLabel registerLink = new JLabel("Register here");
            registerLink.setFont(BOLD_FONT);
            registerLink.setForeground(PRIMARY_COLOR);
            registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
            registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    mainFrame.showPanel(MainFrame.REGISTRATION_PANEL);
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    registerLink.setText("<html><u>Register here</u></html>");
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    registerLink.setText("Register here");
                }
            });

            registerPanel.add(registerLabel);
            registerPanel.add(Box.createHorizontalStrut(5));
            registerPanel.add(registerLink);

            helpPanel.add(registerPanel, BorderLayout.CENTER);
        }

        // Assemble panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(helpPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Handle user login button click
     */
    private void handleUserLogin() {
        String username = userUsernameField.getText();
        String password = new String(userPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            userStatusLabel.setText("Username and password cannot be empty");
            return;
        }

        User user = userController.authenticateUser(username, password);

        if (user != null) {
            // Check if this is a regular user (not admin)
            if (!user.isAdmin()) {
                logger.info("User logged in: {}", username);
                mainFrame.setCurrentUser(user);

                // Clear fields
                clearUserFields();

                // Show home panel
                mainFrame.showPanel(MainFrame.HOME_PANEL);
            } else {
                logger.warn("Admin attempted to log in through user login: {}", username);
                userStatusLabel.setText("Please use the Admin tab to login as administrator");
            }
        } else {
            logger.warn("Failed login attempt for username: {}", username);
            userStatusLabel.setText("Invalid username or password");
        }
    }

    /**
     * Handle admin login button click
     */
    private void handleAdminLogin() {
        String username = adminUsernameField.getText();
        String password = new String(adminPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            adminStatusLabel.setText("Username and password cannot be empty");
            return;
        }

        User user = userController.authenticateUser(username, password);

        if (user != null && user.isAdmin()) {
            logger.info("Admin logged in: {}", username);
            mainFrame.setCurrentUser(user);

            // Clear fields
            clearAdminFields();

            // Show admin panel directly
            mainFrame.showPanel(MainFrame.ADMIN_PANEL);
        } else {
            logger.warn("Failed admin login attempt for username: {}", username);
            adminStatusLabel.setText("Invalid admin credentials");
        }
    }

    /**
     * Handle guest login button click
     */
    private void handleGuestLogin() {
        logger.info("Guest login");

        // Clear fields
        clearUserFields();

        // Set null user (guest)
        mainFrame.setCurrentUser(null);

        // Show home panel
        mainFrame.showPanel(MainFrame.HOME_PANEL);
    }

    /**
     * Clear user login fields
     */
    private void clearUserFields() {
        userUsernameField.setText("");
        userPasswordField.setText("");
        userStatusLabel.setText(" ");
    }

    /**
     * Clear admin login fields
     */
    private void clearAdminFields() {
        adminUsernameField.setText("");
        adminPasswordField.setText("");
        adminStatusLabel.setText(" ");
    }
}
