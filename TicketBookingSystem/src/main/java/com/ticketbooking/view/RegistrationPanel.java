package com.ticketbooking.view;

import com.ticketbooking.controller.UserController;
import com.ticketbooking.model.User;
import com.ticketbooking.utils.ValidationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Panel for user registration
 */
public class RegistrationPanel extends JPanel {
    private static final Logger logger = LogManager.getLogger(RegistrationPanel.class);

    private final MainFrame mainFrame;
    private final UserController userController;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JTextField phoneField;

    // Constants for styling
    private static final Color PRIMARY_COLOR = new Color(45, 125, 154);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color ACCENT_COLOR = new Color(255, 153, 51);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);

    /**
     * Constructor
     * @param mainFrame reference to the main frame
     */
    public RegistrationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userController = new UserController();

        // Set up the panel
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);

        // Create components
        initializeComponents();

        logger.info("Registration panel initialized");
    }

    /**
     * Initialize panel components
     */
    private void initializeComponents() {
        // Create header panel with gradient background
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int w = getWidth();
                int h = getHeight();

                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, w, h, new Color(0, 77, 102));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(getWidth(), 100));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Create Your Account");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Join us to book tickets for amazing events");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(220, 220, 220));

        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Create main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Create registration form card
        JPanel formCard = createRegistrationForm();
        contentPanel.add(formCard);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(REGULAR_FONT);
        cancelButton.setBackground(SECONDARY_COLOR);
        cancelButton.setForeground(new Color(80, 80, 80));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                mainFrame.showPanel(MainFrame.LOGIN_PANEL);
            }
        });

        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBackground(ACCENT_COLOR);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        registerButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                handleRegistration();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(registerButton);

        contentPanel.add(buttonPanel);

        // Add login link
        JPanel loginLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginLinkPanel.setBackground(Color.WHITE);
        loginLinkPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel loginLabel = new JLabel("Already have an account?");
        loginLabel.setFont(REGULAR_FONT);

        JLabel loginLink = new JLabel("Login here");
        loginLink.setFont(new Font("Arial", Font.BOLD, 14));
        loginLink.setForeground(PRIMARY_COLOR);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainFrame.showPanel(MainFrame.LOGIN_PANEL);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginLink.setText("<html><u>Login here</u></html>");
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginLink.setText("Login here");
            }
        });

        loginLinkPanel.add(loginLabel);
        loginLinkPanel.add(Box.createHorizontalStrut(5));
        loginLinkPanel.add(loginLink);

        contentPanel.add(loginLinkPanel);

        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
    }

    /**
     * Create registration form
     */
    private JPanel createRegistrationForm() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Card header
        JLabel headerLabel = new JLabel("Registration Information");
        headerLabel.setFont(SUBTITLE_FONT);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameField = new JTextField(20);
        usernameField.setFont(REGULAR_FONT);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        usernameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateUsername();
            }
        });

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordField = new JPasswordField(20);
        passwordField.setFont(REGULAR_FONT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Confirm Password
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(REGULAR_FONT);
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Email
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailField = new JTextField(20);
        emailField.setFont(REGULAR_FONT);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        emailField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateEmail();
            }
        });

        // Full Name
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        fullNameField = new JTextField(20);
        fullNameField.setFont(REGULAR_FONT);
        fullNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Phone
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        phoneField = new JTextField(20);
        phoneField.setFont(REGULAR_FONT);
        phoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Add components to form panel
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);

        gbc.gridy = 3;
        formPanel.add(passwordField, gbc);

        gbc.gridy = 4;
        formPanel.add(confirmPasswordLabel, gbc);

        gbc.gridy = 5;
        formPanel.add(confirmPasswordField, gbc);

        gbc.gridy = 6;
        formPanel.add(emailLabel, gbc);

        gbc.gridy = 7;
        formPanel.add(emailField, gbc);

        gbc.gridy = 8;
        formPanel.add(fullNameLabel, gbc);

        gbc.gridy = 9;
        formPanel.add(fullNameField, gbc);

        gbc.gridy = 10;
        formPanel.add(phoneLabel, gbc);

        gbc.gridy = 11;
        formPanel.add(phoneField, gbc);

        // Add components to card
        card.add(headerLabel, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Validate username
     */
    private boolean validateUsername() {
        String username = usernameField.getText().trim();

        if (!ValidationUtils.isValidUsername(username)) {
            usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            return false;
        }

        if (userController.usernameExists(username)) {
            usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            return false;
        }

        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return true;
    }

    /**
     * Validate email
     */
    private boolean validateEmail() {
        String email = emailField.getText().trim();

        if (!ValidationUtils.isValidEmail(email)) {
            emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            return false;
        }

        if (userController.emailExists(email)) {
            emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            return false;
        }

        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return true;
    }

    /**
     * Handle registration button click
     */
    private void handleRegistration() {
        // Get form values
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();

        // Validate input
        if (!validateUsername()) {
            JOptionPane.showMessageDialog(this,
                "Username must be 3-20 characters long and contain only letters, numbers, and underscores.",
                "Invalid Username",
                JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            JOptionPane.showMessageDialog(this,
                "Password must be at least 6 characters long.",
                "Invalid Password",
                JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match.",
                "Password Mismatch",
                JOptionPane.WARNING_MESSAGE);
            confirmPasswordField.requestFocus();
            return;
        }

        if (!validateEmail()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address.",
                "Invalid Email",
                JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return;
        }

        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter your full name.",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE);
            fullNameField.requestFocus();
            return;
        }

        if (!phone.isEmpty() && !ValidationUtils.isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid phone number.",
                "Invalid Phone Number",
                JOptionPane.WARNING_MESSAGE);
            phoneField.requestFocus();
            return;
        }

        // Register user
        User user = userController.registerUser(username, password, email, fullName, phone);

        if (user != null) {
            logger.info("User registered successfully: {}", username);

            // Show success message
            JOptionPane.showMessageDialog(this,
                "Registration successful! You can now log in with your credentials.",
                "Registration Complete",
                JOptionPane.INFORMATION_MESSAGE);

            // Go to login panel
            mainFrame.showPanel(MainFrame.LOGIN_PANEL);
        } else {
            logger.error("Registration failed for username: {}", username);

            JOptionPane.showMessageDialog(this,
                "Registration failed. Please try again.",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
