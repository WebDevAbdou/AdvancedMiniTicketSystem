package com.ticketbooking.view;

import com.ticketbooking.controller.BookingController;
import com.ticketbooking.controller.EventController;
import com.ticketbooking.controller.UserController;
import com.ticketbooking.model.Booking;
import com.ticketbooking.model.Event;
import com.ticketbooking.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel for admin functions
 */
@SuppressWarnings("unused")
public class AdminPanel extends JPanel {
    private static final Logger logger = LogManager.getLogger(AdminPanel.class);

    // Reference to main frame (kept for potential future use)
    private final MainFrame mainFrame;
    private final EventController eventController;
    private final BookingController bookingController;
    private final UserController userController;

    private JTabbedPane tabbedPane;

    // Event management
    private JTable eventTable;
    private DefaultTableModel eventTableModel;

    // Booking management
    private JTable bookingTable;
    private DefaultTableModel bookingTableModel;

    // User management
    private JTable userTable;
    private DefaultTableModel userTableModel;

    /**
     * Constructor
     * @param mainFrame reference to the main frame
     */
    public AdminPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.eventController = new EventController();
        this.bookingController = new BookingController();
        this.userController = new UserController();

        // Set up the panel
        setLayout(new BorderLayout());

        // Create components
        initializeComponents();

        logger.info("Admin panel initialized");
    }

    /**
     * Initialize panel components
     */
    private void initializeComponents() {
        // Set panel background
        setBackground(new Color(240, 240, 245));

        // Create header panel with gradient background
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(41, 128, 185);
                Color color2 = new Color(44, 62, 80);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(getWidth(), 100));

        // Create title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/admin_icon.png"));
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                iconLabel.setIcon(icon);
            }
        } catch (Exception e) {
            // Icon not available, use text instead
            iconLabel.setText("👑");
            iconLabel.setFont(new Font("Arial", Font.BOLD, 24));
            iconLabel.setForeground(Color.WHITE);
        }

        JLabel titleLabel = new JLabel("Admin Control Panel", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        titlePanel.add(iconLabel);
        titlePanel.add(Box.createHorizontalStrut(10));
        titlePanel.add(titleLabel);

        // Add subtitle
        JLabel subtitleLabel = new JLabel("Manage your system with ease", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Create dashboard panel
        JPanel dashboardPanel = createDashboardPanel();

        // Create tabbed pane with custom styling
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(240, 240, 245));
        tabbedPane.setForeground(new Color(44, 62, 80));

        // Create events tab
        JPanel eventsTab = createEventsTab();
        tabbedPane.addTab("Manage Events", eventsTab);

        // Create bookings tab
        JPanel bookingsTab = createBookingsTab();
        tabbedPane.addTab("View Bookings", bookingsTab);

        // Create users tab
        JPanel usersTab = createUsersTab();
        tabbedPane.addTab("Manage Users", usersTab);

        // Create reports tab
        JPanel reportsTab = createReportsTab();
        tabbedPane.addTab("Reports", reportsTab);

        // Create settings tab
        JPanel settingsTab = createSettingsTab();
        tabbedPane.addTab("Settings", settingsTab);

        // Add icons to tabs if available
        try {
            ImageIcon eventsIcon = new ImageIcon(getClass().getResource("/images/events_icon.png"));
            tabbedPane.setIconAt(0, eventsIcon);

            ImageIcon bookingsIcon = new ImageIcon(getClass().getResource("/images/bookings_icon.png"));
            tabbedPane.setIconAt(1, bookingsIcon);

            ImageIcon usersIcon = new ImageIcon(getClass().getResource("/images/users_icon.png"));
            tabbedPane.setIconAt(2, usersIcon);

            ImageIcon reportsIcon = new ImageIcon(getClass().getResource("/images/reports_icon.png"));
            tabbedPane.setIconAt(3, reportsIcon);

            ImageIcon settingsIcon = new ImageIcon(getClass().getResource("/images/settings_icon.png"));
            tabbedPane.setIconAt(4, settingsIcon);
        } catch (Exception e) {
            logger.warn("Could not load tab icons", e);
        }

        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(dashboardPanel, BorderLayout.NORTH);
        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Add status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(44, 62, 80));
        statusBar.setPreferredSize(new Dimension(getWidth(), 25));

        // Get current user safely
        String username = "Guest";
        if (mainFrame.getCurrentUser() != null) {
            username = mainFrame.getCurrentUser().getUsername();
        }

        JLabel statusLabel = new JLabel(" Admin Mode | " + username + " | " +
                                       LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusBar.add(statusLabel, BorderLayout.WEST);

        add(statusBar, BorderLayout.SOUTH);

        // Initial data load
        refreshData();
    }

    /**
     * Create dashboard panel with summary statistics
     * @return Dashboard panel
     */
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Events card
        JPanel eventsCard = createDashboardCard("Total Events", "0", new Color(41, 128, 185));
        panel.add(eventsCard);

        // Bookings card
        JPanel bookingsCard = createDashboardCard("Total Bookings", "0", new Color(39, 174, 96));
        panel.add(bookingsCard);

        // Users card
        JPanel usersCard = createDashboardCard("Registered Users", "0", new Color(142, 68, 173));
        panel.add(usersCard);

        // Revenue card
        JPanel revenueCard = createDashboardCard("Total Revenue", "$0.00", new Color(230, 126, 34));
        panel.add(revenueCard);

        return panel;
    }

    /**
     * Create a dashboard card with statistics
     * @param title Card title
     * @param value Card value
     * @param color Card color
     * @return Dashboard card panel
     */
    private JPanel createDashboardCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = color;
                Color color2 = color.darker();
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 15, 15);
            }
        };
        card.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Create events tab
     * @return JPanel containing events tab
     */
    private JPanel createEventsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model with non-editable cells
        eventTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Add columns to table model
        eventTableModel.addColumn("ID");
        eventTableModel.addColumn("Event Name");
        eventTableModel.addColumn("Date");
        eventTableModel.addColumn("Time");
        eventTableModel.addColumn("Venue");
        eventTableModel.addColumn("Total Seats");
        eventTableModel.addColumn("Available Seats");
        eventTableModel.addColumn("Base Price");

        // Create table
        eventTable = new JTable(eventTableModel);
        eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventTable.getTableHeader().setReorderingAllowed(false);

        // Set column widths
        eventTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        eventTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        eventTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        eventTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        eventTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        eventTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        eventTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        eventTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(eventTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("Add Event");
        addButton.addActionListener(e -> handleAddEvent());

        JButton editButton = new JButton("Edit Event");
        editButton.addActionListener(e -> handleEditEvent());

        JButton deleteButton = new JButton("Delete Event");
        deleteButton.addActionListener(e -> handleDeleteEvent());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshEventList());

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create bookings tab
     * @return JPanel containing bookings tab
     */
    private JPanel createBookingsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model with non-editable cells
        bookingTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Add columns to table model
        bookingTableModel.addColumn("ID");
        bookingTableModel.addColumn("Customer Name");
        bookingTableModel.addColumn("Event");
        bookingTableModel.addColumn("Seat Type");
        bookingTableModel.addColumn("Quantity");
        bookingTableModel.addColumn("Total Price");
        bookingTableModel.addColumn("Booking Time");

        // Create table
        bookingTable = new JTable(bookingTableModel);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingTable.getTableHeader().setReorderingAllowed(false);

        // Set column widths
        bookingTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        bookingTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        bookingTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        bookingTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        bookingTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        bookingTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        bookingTable.getColumnModel().getColumn(6).setPreferredWidth(150);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> handleViewBooking());

        JButton deleteButton = new JButton("Delete Booking");
        deleteButton.addActionListener(e -> handleDeleteBooking());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshBookingList());

        buttonPanel.add(refreshButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create users tab
     * @return JPanel containing users tab
     */
    private JPanel createUsersTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model with non-editable cells
        userTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Add columns to table model
        userTableModel.addColumn("ID");
        userTableModel.addColumn("Username");
        userTableModel.addColumn("Email");
        userTableModel.addColumn("Full Name");
        userTableModel.addColumn("Phone");
        userTableModel.addColumn("Role");
        userTableModel.addColumn("Registration Date");
        userTableModel.addColumn("Last Login");

        // Create table
        userTable = new JTable(userTableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setReorderingAllowed(false);

        // Set column widths
        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        userTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(6).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(7).setPreferredWidth(150);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("Add User");
        addButton.addActionListener(e -> handleAddUser());

        JButton editButton = new JButton("Edit User");
        editButton.addActionListener(e -> handleEditUser());

        JButton deleteButton = new JButton("Delete User");
        deleteButton.addActionListener(e -> handleDeleteUser());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshUserList());

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create reports tab
     * @return JPanel containing reports tab
     */
    private JPanel createReportsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create tabbed pane for different reports
        JTabbedPane reportsTabbedPane = new JTabbedPane();
        reportsTabbedPane.setFont(new Font("Arial", Font.PLAIN, 12));

        // Sales by event report
        JPanel salesByEventPanel = createSalesByEventReport();
        reportsTabbedPane.addTab("Sales by Event", salesByEventPanel);

        // User activity report
        JPanel userActivityPanel = createUserActivityReport();
        reportsTabbedPane.addTab("User Activity", userActivityPanel);

        // Revenue report
        JPanel revenuePanel = createRevenueReport();
        reportsTabbedPane.addTab("Revenue Analysis", revenuePanel);

        panel.add(reportsTabbedPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create sales by event report
     * @return JPanel containing sales by event report
     */
    private JPanel createSalesByEventReport() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Add columns
        tableModel.addColumn("Event ID");
        tableModel.addColumn("Event Name");
        tableModel.addColumn("Total Bookings");
        tableModel.addColumn("Total Tickets");
        tableModel.addColumn("Total Revenue");
        tableModel.addColumn("Average Ticket Price");

        // Create table
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters"));

        JLabel dateRangeLabel = new JLabel("Date Range:");
        JComboBox<String> dateRangeComboBox = new JComboBox<>(new String[]{
            "All Time", "This Month", "Last Month", "This Year"
        });

        JButton generateButton = new JButton("Generate Report");
        generateButton.addActionListener(e -> {
            // This would be implemented to generate the actual report
            JOptionPane.showMessageDialog(this,
                "Report generation would be implemented in a production system.",
                "Report Generation",
                JOptionPane.INFORMATION_MESSAGE);
        });

        filterPanel.add(dateRangeLabel);
        filterPanel.add(dateRangeComboBox);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(generateButton);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Add sample data
        tableModel.addRow(new Object[]{
            1, "Summer Music Festival", 25, 75, "$3,750.00", "$50.00"
        });
        tableModel.addRow(new Object[]{
            2, "Comedy Night", 15, 30, "$750.00", "$25.00"
        });
        tableModel.addRow(new Object[]{
            3, "Tech Conference", 10, 20, "$1,500.00", "$75.00"
        });

        return panel;
    }

    /**
     * Create user activity report
     * @return JPanel containing user activity report
     */
    private JPanel createUserActivityReport() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create a message label
        JLabel messageLabel = new JLabel(
            "<html><div style='text-align: center;'>" +
            "<h3>User Activity Report</h3>" +
            "<p>This report would show user registration and login activity over time.</p>" +
            "<p>It would include charts showing user growth and engagement metrics.</p>" +
            "<p>This feature would be implemented in a production system.</p>" +
            "</div></html>",
            SwingConstants.CENTER
        );
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(messageLabel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create revenue report
     * @return JPanel containing revenue report
     */
    private JPanel createRevenueReport() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create a message label
        JLabel messageLabel = new JLabel(
            "<html><div style='text-align: center;'>" +
            "<h3>Revenue Analysis</h3>" +
            "<p>This report would show revenue trends over time with charts and graphs.</p>" +
            "<p>It would include breakdowns by event type, venue, and time period.</p>" +
            "<p>This feature would be implemented in a production system.</p>" +
            "</div></html>",
            SwingConstants.CENTER
        );
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(messageLabel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create settings tab
     * @return JPanel containing settings tab
     */
    private JPanel createSettingsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create settings form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("System Settings"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email settings section
        JLabel emailSettingsLabel = new JLabel("Email Settings");
        emailSettingsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(emailSettingsLabel, gbc);

        // SMTP Server
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("SMTP Server:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField smtpServerField = new JTextField("smtp.example.com", 20);
        formPanel.add(smtpServerField, gbc);

        // SMTP Port
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("SMTP Port:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField smtpPortField = new JTextField("587", 20);
        formPanel.add(smtpPortField, gbc);

        // Email Username
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Email Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField emailUsernameField = new JTextField("noreply@ticketbooking.com", 20);
        formPanel.add(emailUsernameField, gbc);

        // Email Password
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Email Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JPasswordField emailPasswordField = new JPasswordField("password", 20);
        formPanel.add(emailPasswordField, gbc);

        // Separator
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 15, 5);
        formPanel.add(new JSeparator(), gbc);
        gbc.insets = new Insets(5, 5, 5, 5);

        // System settings section
        JLabel systemSettingsLabel = new JLabel("System Settings");
        systemSettingsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(systemSettingsLabel, gbc);

        // Enable Email Verification
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Enable Email Verification:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        JCheckBox emailVerificationCheckbox = new JCheckBox();
        emailVerificationCheckbox.setSelected(true);
        formPanel.add(emailVerificationCheckbox, gbc);

        // Enable Password Hashing
        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(new JLabel("Enable Password Hashing:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 8;
        JCheckBox passwordHashingCheckbox = new JCheckBox();
        passwordHashingCheckbox.setSelected(true);
        formPanel.add(passwordHashingCheckbox, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Save Settings");
        saveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Settings would be saved in a production system.",
                "Settings",
                JOptionPane.INFORMATION_MESSAGE);
        });

        JButton resetButton = new JButton("Reset to Defaults");
        resetButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Settings would be reset in a production system.",
                "Settings",
                JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(resetButton);
        buttonPanel.add(saveButton);

        // Add components to panel
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Refresh all data
     */
    public void refreshData() {
        refreshEventList();
        refreshBookingList();
        refreshUserList();
        updateDashboardStats();
    }

    /**
     * Update dashboard statistics
     */
    private void updateDashboardStats() {
        try {
            // Get counts and statistics
            List<Event> events = eventController.getAllEvents();
            List<Booking> bookings = bookingController.getAllBookings();
            List<User> users = userController.getAllUsers();

            // Calculate total revenue
            BigDecimal totalRevenue = BigDecimal.ZERO;
            for (Booking booking : bookings) {
                if (booking.getTotalPrice() != null) {
                    totalRevenue = totalRevenue.add(booking.getTotalPrice());
                }
            }

            // Check if the component structure is as expected
            if (getComponentCount() < 2 || !(getComponent(1) instanceof JPanel)) {
                logger.warn("Dashboard panel structure not as expected");
                return;
            }

            JPanel contentPanel = (JPanel) getComponent(1);
            if (contentPanel.getComponentCount() < 1 || !(contentPanel.getComponent(0) instanceof JPanel)) {
                logger.warn("Dashboard panel not found");
                return;
            }

            // Update dashboard cards
            JPanel dashboardPanel = (JPanel) contentPanel.getComponent(0);

            // Make sure we have all the expected components
            if (dashboardPanel.getComponentCount() < 4) {
                logger.warn("Dashboard cards not found");
                return;
            }

            // Update events card
            try {
                JPanel eventsCard = (JPanel) dashboardPanel.getComponent(0);
                if (eventsCard.getComponentCount() > 1 && eventsCard.getComponent(1) instanceof JLabel) {
                    JLabel eventsValueLabel = (JLabel) eventsCard.getComponent(1);
                    eventsValueLabel.setText(String.valueOf(events.size()));
                }
            } catch (Exception e) {
                logger.warn("Error updating events card", e);
            }

            // Update bookings card
            try {
                JPanel bookingsCard = (JPanel) dashboardPanel.getComponent(1);
                if (bookingsCard.getComponentCount() > 1 && bookingsCard.getComponent(1) instanceof JLabel) {
                    JLabel bookingsValueLabel = (JLabel) bookingsCard.getComponent(1);
                    bookingsValueLabel.setText(String.valueOf(bookings.size()));
                }
            } catch (Exception e) {
                logger.warn("Error updating bookings card", e);
            }

            // Update users card
            try {
                JPanel usersCard = (JPanel) dashboardPanel.getComponent(2);
                if (usersCard.getComponentCount() > 1 && usersCard.getComponent(1) instanceof JLabel) {
                    JLabel usersValueLabel = (JLabel) usersCard.getComponent(1);
                    usersValueLabel.setText(String.valueOf(users.size()));
                }
            } catch (Exception e) {
                logger.warn("Error updating users card", e);
            }

            // Update revenue card
            try {
                JPanel revenueCard = (JPanel) dashboardPanel.getComponent(3);
                if (revenueCard.getComponentCount() > 1 && revenueCard.getComponent(1) instanceof JLabel) {
                    JLabel revenueValueLabel = (JLabel) revenueCard.getComponent(1);
                    revenueValueLabel.setText("$" + totalRevenue.toString());
                }
            } catch (Exception e) {
                logger.warn("Error updating revenue card", e);
            }
        } catch (Exception e) {
            logger.error("Error updating dashboard statistics", e);
        }
    }

    /**
     * Refresh event list
     */
    private void refreshEventList() {
        // Clear table
        eventTableModel.setRowCount(0);

        // Get all events
        List<Event> events = eventController.getAllEvents();

        // Add events to table
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Event event : events) {
            eventTableModel.addRow(new Object[]{
                event.getId(),
                event.getName(),
                event.getDate().format(dateFormatter),
                event.getTime().format(timeFormatter),
                event.getVenue(),
                event.getTotalSeats(),
                event.getAvailableSeats(),
                "$" + event.getBasePrice()
            });
        }

        logger.info("Event list refreshed");
    }

    /**
     * Refresh booking list
     */
    private void refreshBookingList() {
        // Clear table
        bookingTableModel.setRowCount(0);

        // Get all bookings
        List<Booking> bookings = bookingController.getAllBookings();

        // Add bookings to table
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Booking booking : bookings) {
            String eventName = booking.getEvent() != null ? booking.getEvent().getName() : "Unknown";

            bookingTableModel.addRow(new Object[]{
                booking.getId(),
                booking.getCustomerName(),
                eventName,
                booking.getSeatType(),
                booking.getQuantity(),
                "$" + booking.getTotalPrice(),
                booking.getBookingTime().format(dateTimeFormatter)
            });
        }

        logger.info("Booking list refreshed");
    }

    /**
     * Handle add event button click
     */
    private void handleAddEvent() {
        // Create dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Event", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Event name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Event Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Event description
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descriptionScrollPane, gbc);

        // Event date
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField dateField = new JTextField(10);
        formPanel.add(dateField, gbc);

        // Event time
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Time (HH:mm):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField timeField = new JTextField(10);
        formPanel.add(timeField, gbc);

        // Event venue
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Venue:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField venueField = new JTextField(20);
        formPanel.add(venueField, gbc);

        // Total seats
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Total Seats:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        JSpinner totalSeatsSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 1));
        formPanel.add(totalSeatsSpinner, gbc);

        // Base price
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Base Price:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        JTextField priceField = new JTextField(10);
        formPanel.add(priceField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                // Get form values
                String name = nameField.getText().trim();
                String description = descriptionArea.getText().trim();
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                LocalTime time = LocalTime.parse(timeField.getText().trim());
                String venue = venueField.getText().trim();
                int totalSeats = (int) totalSeatsSpinner.getValue();
                BigDecimal basePrice = new BigDecimal(priceField.getText().trim());

                // Create event
                boolean success = eventController.createEvent(
                    name, description, date, time, venue, totalSeats, basePrice);

                if (success) {
                    logger.info("Event created: {}", name);
                    dialog.dispose();
                    refreshEventList();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to create event. Please check your input.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Invalid date or time format. Please use yyyy-MM-dd for date and HH:mm for time.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Invalid price format. Please enter a valid number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                logger.error("Error creating event", ex);
                JOptionPane.showMessageDialog(dialog,
                    "An error occurred. Please check your input.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Add panels to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Show dialog
        dialog.setVisible(true);
    }

    /**
     * Handle edit event button click
     */
    private void handleEditEvent() {
        int selectedRow = eventTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an event to edit.",
                "No Event Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventId = (int) eventTableModel.getValueAt(selectedRow, 0);
        Event event = eventController.getEventById(eventId);

        if (event == null) {
            JOptionPane.showMessageDialog(this,
                "Failed to load event details.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Event", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Event name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Event Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField nameField = new JTextField(event.getName(), 20);
        formPanel.add(nameField, gbc);

        // Event description
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextArea descriptionArea = new JTextArea(event.getDescription(), 3, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descriptionScrollPane, gbc);

        // Event date
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField dateField = new JTextField(event.getDate().toString(), 10);
        formPanel.add(dateField, gbc);

        // Event time
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Time (HH:mm):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField timeField = new JTextField(event.getTime().toString(), 10);
        formPanel.add(timeField, gbc);

        // Event venue
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Venue:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField venueField = new JTextField(event.getVenue(), 20);
        formPanel.add(venueField, gbc);

        // Total seats
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Total Seats:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        JSpinner totalSeatsSpinner = new JSpinner(new SpinnerNumberModel(
            event.getTotalSeats(), 1, 10000, 1));
        formPanel.add(totalSeatsSpinner, gbc);

        // Available seats
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Available Seats:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        JSpinner availableSeatsSpinner = new JSpinner(new SpinnerNumberModel(
            event.getAvailableSeats(), 0, event.getTotalSeats(), 1));
        formPanel.add(availableSeatsSpinner, gbc);

        // Base price
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Base Price:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        JTextField priceField = new JTextField(event.getBasePrice().toString(), 10);
        formPanel.add(priceField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                // Get form values
                String name = nameField.getText().trim();
                String description = descriptionArea.getText().trim();
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                LocalTime time = LocalTime.parse(timeField.getText().trim());
                String venue = venueField.getText().trim();
                int totalSeats = (int) totalSeatsSpinner.getValue();
                int availableSeats = (int) availableSeatsSpinner.getValue();
                BigDecimal basePrice = new BigDecimal(priceField.getText().trim());

                // Update event
                boolean success = eventController.updateEvent(
                    eventId, name, description, date, time, venue, totalSeats, availableSeats, basePrice);

                if (success) {
                    logger.info("Event updated: {}", name);
                    dialog.dispose();
                    refreshEventList();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to update event. Please check your input.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Invalid date or time format. Please use yyyy-MM-dd for date and HH:mm for time.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Invalid price format. Please enter a valid number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                logger.error("Error updating event", ex);
                JOptionPane.showMessageDialog(dialog,
                    "An error occurred. Please check your input.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Add panels to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Show dialog
        dialog.setVisible(true);
    }

    /**
     * Handle delete event button click
     */
    private void handleDeleteEvent() {
        int selectedRow = eventTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an event to delete.",
                "No Event Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventId = (int) eventTableModel.getValueAt(selectedRow, 0);
        String eventName = (String) eventTableModel.getValueAt(selectedRow, 1);

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete the event: " + eventName + "?\n" +
            "This will also delete all bookings for this event.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = eventController.deleteEvent(eventId);

            if (success) {
                logger.info("Event deleted: {}", eventName);
                refreshEventList();
                refreshBookingList();
            } else {
                logger.error("Failed to delete event: {}", eventName);
                JOptionPane.showMessageDialog(this,
                    "Failed to delete event.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handle view booking button click
     */
    private void handleViewBooking() {
        int selectedRow = bookingTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a booking to view.",
                "No Booking Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (int) bookingTableModel.getValueAt(selectedRow, 0);
        Booking booking = bookingController.getBookingById(bookingId);

        if (booking == null) {
            JOptionPane.showMessageDialog(this,
                "Failed to load booking details.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Format booking details
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String eventDetails = "";
        if (booking.getEvent() != null) {
            Event event = booking.getEvent();
            eventDetails = "Event: " + event.getName() + "\n" +
                           "Date: " + event.getDate().format(dateFormatter) + "\n" +
                           "Time: " + event.getTime().format(timeFormatter) + "\n" +
                           "Venue: " + event.getVenue() + "\n";
        }

        String bookingDetails = "Booking ID: " + booking.getId() + "\n" +
                               "Customer: " + booking.getCustomerName() + "\n" +
                               "Email: " + (booking.getCustomerEmail() != null ? booking.getCustomerEmail() : "N/A") + "\n" +
                               "Phone: " + (booking.getCustomerPhone() != null ? booking.getCustomerPhone() : "N/A") + "\n" +
                               eventDetails +
                               "Seat Type: " + booking.getSeatType() + "\n" +
                               "Quantity: " + booking.getQuantity() + "\n" +
                               "Total Price: $" + booking.getTotalPrice() + "\n" +
                               "Booking Time: " + booking.getBookingTime().format(dateTimeFormatter);

        // Show booking details
        JOptionPane.showMessageDialog(this,
            bookingDetails,
            "Booking Details",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Handle delete booking button click
     */
    private void handleDeleteBooking() {
        int selectedRow = bookingTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a booking to delete.",
                "No Booking Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (int) bookingTableModel.getValueAt(selectedRow, 0);
        String customerName = (String) bookingTableModel.getValueAt(selectedRow, 1);

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete the booking for: " + customerName + "?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = bookingController.deleteBooking(bookingId);

            if (success) {
                logger.info("Booking deleted for customer: {}", customerName);
                refreshBookingList();
                refreshEventList(); // Refresh events to update available seats
            } else {
                logger.error("Failed to delete booking for customer: {}", customerName);
                JOptionPane.showMessageDialog(this,
                    "Failed to delete booking.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Refresh user list
     */
    private void refreshUserList() {
        // Clear table
        userTableModel.setRowCount(0);

        // Get all users
        List<User> users = userController.getAllUsers();

        // Add users to table
        for (User user : users) {
            userTableModel.addRow(new Object[]{
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName() != null ? user.getFullName() : "",
                user.getPhone() != null ? user.getPhone() : "",
                user.getRole(),
                user.getRegistrationDate() != null ? user.getRegistrationDate().toString().replace("T", " ") : "",
                user.getLastLoginDate() != null ? user.getLastLoginDate().toString().replace("T", " ") : ""
            });
        }

        logger.info("User list refreshed");
    }

    /**
     * Handle add user button click
     */
    private void handleAddUser() {
        // Create dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add User", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JPasswordField passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Full Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField fullNameField = new JTextField(20);
        formPanel.add(fullNameField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Phone:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"user", "admin"});
        formPanel.add(roleComboBox, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                // Get form values
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String email = emailField.getText().trim();
                String fullName = fullNameField.getText().trim();
                String phone = phoneField.getText().trim();
                String role = (String) roleComboBox.getSelectedItem();

                // Validate input
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Username and password are required.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Create user
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.setFullName(fullName);
                user.setPhone(phone);
                user.setRole(role);
                user.setRegistrationDate(LocalDateTime.now());

                boolean success = userController.createUser(username, password, role, email);

                if (success) {
                    logger.info("User created: {}", username);
                    dialog.dispose();
                    refreshUserList();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to create user. Username or email may already exist.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                logger.error("Error creating user", ex);
                JOptionPane.showMessageDialog(dialog,
                    "An error occurred. Please check your input.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Add panels to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Show dialog
        dialog.setVisible(true);
    }

    /**
     * Handle edit user button click
     */
    private void handleEditUser() {
        int selectedRow = userTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to edit.",
                "No User Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        User user = userController.getUserById(userId);

        if (user == null) {
            JOptionPane.showMessageDialog(this,
                "Failed to load user details.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit User", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField usernameField = new JTextField(user.getUsername(), 20);
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JPasswordField passwordField = new JPasswordField(user.getPassword(), 20);
        formPanel.add(passwordField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField emailField = new JTextField(user.getEmail(), 20);
        formPanel.add(emailField, gbc);

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Full Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField fullNameField = new JTextField(user.getFullName() != null ? user.getFullName() : "", 20);
        formPanel.add(fullNameField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Phone:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField phoneField = new JTextField(user.getPhone() != null ? user.getPhone() : "", 20);
        formPanel.add(phoneField, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"user", "admin"});
        roleComboBox.setSelectedItem(user.getRole());
        formPanel.add(roleComboBox, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                // Get form values
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String email = emailField.getText().trim();
                String fullName = fullNameField.getText().trim();
                String phone = phoneField.getText().trim();
                String role = (String) roleComboBox.getSelectedItem();

                // Validate input
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Username and password are required.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Update user
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.setFullName(fullName);
                user.setPhone(phone);
                user.setRole(role);

                boolean success = userController.updateUser(user.getId(), username, password, role, email, fullName, phone);

                if (success) {
                    logger.info("User updated: {}", username);
                    dialog.dispose();
                    refreshUserList();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to update user. Username or email may already exist.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                logger.error("Error updating user", ex);
                JOptionPane.showMessageDialog(dialog,
                    "An error occurred. Please check your input.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Add panels to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Show dialog
        dialog.setVisible(true);
    }

    /**
     * Handle delete user button click
     */
    private void handleDeleteUser() {
        int selectedRow = userTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to delete.",
                "No User Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        String username = (String) userTableModel.getValueAt(selectedRow, 1);

        // Don't allow deleting the current user
        User currentUser = mainFrame.getCurrentUser();
        if (currentUser != null && currentUser.getId() == userId) {
            JOptionPane.showMessageDialog(this,
                "You cannot delete your own account while logged in.",
                "Operation Not Allowed",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete the user: " + username + "?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userController.deleteUser(userId);

            if (success) {
                logger.info("User deleted: {}", username);
                refreshUserList();
            } else {
                logger.error("Failed to delete user: {}", username);
                JOptionPane.showMessageDialog(this,
                    "Failed to delete user.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
