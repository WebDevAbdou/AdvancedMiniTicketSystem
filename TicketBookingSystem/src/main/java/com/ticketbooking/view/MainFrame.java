package com.ticketbooking.view;

import com.ticketbooking.controller.UserController;
import com.ticketbooking.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application window
 */
@SuppressWarnings("unused")
public class MainFrame extends JFrame {
    private static final Logger logger = LogManager.getLogger(MainFrame.class);

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    private LoginPanel loginPanel;
    private HomePanel homePanel;
    private BookingPanel bookingPanel;
    private AdminPanel adminPanel;
    private RegistrationPanel registrationPanel;

    private User currentUser;
    // Controller for user operations (kept for potential future use)
    private final UserController userController;

    // Panel names for CardLayout
    public static final String LOGIN_PANEL = "LOGIN_PANEL";
    public static final String HOME_PANEL = "HOME_PANEL";
    public static final String BOOKING_PANEL = "BOOKING_PANEL";
    public static final String ADMIN_PANEL = "ADMIN_PANEL";
    public static final String REGISTRATION_PANEL = "REGISTRATION_PANEL";

    /**
     * Constructor
     */
    public MainFrame() {
        userController = new UserController();

        // Set up the frame with a professional look
        setTitle("Smart Ticket Booking System Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        // Set application icon
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/ticket_icon.png"));
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            logger.warn("Could not load application icon", e);
        }

        // Set up the content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize panels
        initializePanels();

        // Add panels to content panel
        contentPanel.add(loginPanel, LOGIN_PANEL);
        contentPanel.add(homePanel, HOME_PANEL);
        contentPanel.add(bookingPanel, BOOKING_PANEL);
        contentPanel.add(adminPanel, ADMIN_PANEL);
        contentPanel.add(registrationPanel, REGISTRATION_PANEL);

        // Show login panel initially
        cardLayout.show(contentPanel, LOGIN_PANEL);

        // Add content panel to frame
        add(contentPanel);

        // Add window listener to handle closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logger.info("Application closing");
                // Close database connection
                try {
                    com.ticketbooking.database.DBConnection.getInstance().closeConnection();
                } catch (Exception ex) {
                    logger.error("Error closing database connection", ex);
                }
            }
        });

        logger.info("Main frame initialized");
    }

    /**
     * Initialize all panels
     */
    private void initializePanels() {
        loginPanel = new LoginPanel(this);
        homePanel = new HomePanel(this);
        bookingPanel = new BookingPanel(this);
        adminPanel = new AdminPanel(this);
        registrationPanel = new RegistrationPanel(this);
    }

    /**
     * Show a specific panel
     * @param panelName name of the panel to show
     */
    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);

        // Refresh panel data if needed
        if (panelName.equals(HOME_PANEL)) {
            homePanel.refreshEventList();
        } else if (panelName.equals(ADMIN_PANEL)) {
            adminPanel.refreshData();
        }
    }

    /**
     * Set the current user
     * @param user User object
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;

        // Update menu bar based on user role
        updateMenuBar();
    }

    /**
     * Get the current user
     * @return current User object
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Update menu bar based on user role
     */
    private void updateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        // Create icons for menu items
        Icon homeIcon = null;
        Icon adminIcon = null;
        Icon logoutIcon = null;
        Icon loginIcon = null;
        Icon exitIcon = null;

        try {
            homeIcon = new ImageIcon(getClass().getResource("/images/home_icon.png"));
            adminIcon = new ImageIcon(getClass().getResource("/images/admin_icon.png"));
            logoutIcon = new ImageIcon(getClass().getResource("/images/logout_icon.png"));
            loginIcon = new ImageIcon(getClass().getResource("/images/login_icon.png"));
            exitIcon = new ImageIcon(getClass().getResource("/images/exit_icon.png"));
        } catch (Exception e) {
            logger.warn("Could not load menu icons", e);
        }

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        JMenuItem exitItem = new JMenuItem("Exit", exitIcon);
        exitItem.setMnemonic('x');
        exitItem.setAccelerator(KeyStroke.getKeyStroke("alt F4"));
        exitItem.addActionListener(e -> {
            dispose();
            System.exit(0);
        });
        fileMenu.add(exitItem);

        // Navigation menu
        JMenu navMenu = new JMenu("Navigation");
        navMenu.setMnemonic('N');

        JMenuItem homeItem = new JMenuItem("Home", homeIcon);
        homeItem.setMnemonic('H');
        homeItem.setAccelerator(KeyStroke.getKeyStroke("ctrl H"));
        homeItem.addActionListener(e -> showPanel(HOME_PANEL));
        navMenu.add(homeItem);

        // Add admin panel option if user is admin
        if (currentUser != null && currentUser.isAdmin()) {
            JMenuItem adminItem = new JMenuItem("Admin Panel", adminIcon);
            adminItem.setMnemonic('A');
            adminItem.setAccelerator(KeyStroke.getKeyStroke("ctrl A"));
            adminItem.addActionListener(e -> showPanel(ADMIN_PANEL));
            navMenu.add(adminItem);
        }

        // User menu
        JMenu userMenu = new JMenu("User");
        userMenu.setMnemonic('U');

        if (currentUser != null) {
            // Show user info
            JMenuItem userInfoItem = new JMenuItem("Logged in as: " + currentUser.getUsername());
            userInfoItem.setEnabled(false);
            userInfoItem.setFont(userInfoItem.getFont().deriveFont(Font.BOLD));
            userMenu.add(userInfoItem);

            userMenu.addSeparator();

            // Add logout option
            JMenuItem logoutItem = new JMenuItem("Logout", logoutIcon);
            logoutItem.setMnemonic('L');
            logoutItem.setAccelerator(KeyStroke.getKeyStroke("ctrl L"));
            logoutItem.addActionListener(e -> {
                currentUser = null;
                showPanel(LOGIN_PANEL);
                updateMenuBar();
                JOptionPane.showMessageDialog(this,
                    "You have been successfully logged out.",
                    "Logout Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            });
            userMenu.add(logoutItem);
        } else {
            // Add login option
            JMenuItem loginItem = new JMenuItem("Login", loginIcon);
            loginItem.setMnemonic('L');
            loginItem.setAccelerator(KeyStroke.getKeyStroke("ctrl L"));
            loginItem.addActionListener(e -> showPanel(LOGIN_PANEL));
            userMenu.add(loginItem);
        }

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setMnemonic('A');
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Smart Ticket Booking System Pro\nVersion 1.0\n\n" +
                "A comprehensive solution for event ticket booking.\n" +
                "© 2023 Smart Ticket Systems",
                "About Smart Ticket Booking System",
                JOptionPane.INFORMATION_MESSAGE);
        });
        helpMenu.add(aboutItem);

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(navMenu);
        menuBar.add(userMenu);
        menuBar.add(helpMenu);

        // Add a status indicator on the right side
        menuBar.add(Box.createHorizontalGlue());
        JLabel statusLabel = new JLabel();
        if (currentUser != null) {
            if (currentUser.isAdmin()) {
                statusLabel.setText("Admin Mode");
                statusLabel.setForeground(new Color(128, 0, 0));
            } else {
                statusLabel.setText("User Mode");
                statusLabel.setForeground(new Color(0, 100, 0));
            }
        } else {
            statusLabel.setText("Guest Mode");
            statusLabel.setForeground(new Color(100, 100, 100));
        }
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        menuBar.add(statusLabel);

        // Set menu bar
        setJMenuBar(menuBar);
    }

    /**
     * Show booking panel for a specific event
     * @param eventId event ID
     */
    public void showBookingPanel(int eventId) {
        bookingPanel.setEventId(eventId);
        showPanel(BOOKING_PANEL);
    }

    /**
     * Main method to start the application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.error("Error setting look and feel", e);
        }

        // Start application
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
