package com.ticketbooking.view;

import com.ticketbooking.controller.BookingController;
import com.ticketbooking.controller.EventController;
import com.ticketbooking.model.Booking;
import com.ticketbooking.model.Event;
import com.ticketbooking.utils.ValidationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Panel for booking tickets
 */
@SuppressWarnings("unused")
public class BookingPanel extends JPanel {
    private static final Logger logger = LogManager.getLogger(BookingPanel.class);

    private final MainFrame mainFrame;
    private final EventController eventController;
    private final BookingController bookingController;

    private int eventId;
    private Event currentEvent;

    private JLabel eventNameLabel;
    private JLabel eventDateLabel;
    private JLabel eventTimeLabel;
    private JLabel eventVenueLabel;
    private JLabel availableSeatsLabel;
    private JLabel basePriceLabel;

    private JTextField customerNameField;
    private JTextField customerEmailField;
    private JTextField customerPhoneField;
    private JComboBox<String> seatTypeComboBox;
    private JSpinner quantitySpinner;
    private JLabel totalPriceLabel;

    /**
     * Constructor
     * @param mainFrame reference to the main frame
     */
    public BookingPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.eventController = new EventController();
        this.bookingController = new BookingController();

        // Set up the panel
        setLayout(new BorderLayout());

        // Create components
        initializeComponents();

        logger.info("Booking panel initialized");
    }

    // Constants for styling
    private static final Color PRIMARY_COLOR = new Color(45, 125, 154);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color ACCENT_COLOR = new Color(255, 153, 51);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);

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

        JLabel titleLabel = new JLabel("Book Your Tickets");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Complete your booking information below");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(220, 220, 220));

        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Create main panel with card-like appearance
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Create event details card
        JPanel eventDetailsCard = createEventDetailsCard();

        // Create booking form card
        JPanel bookingFormCard = createBookingFormCard();

        // Create a container for the two cards
        JPanel cardsContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        cardsContainer.setBackground(Color.WHITE);
        cardsContainer.add(eventDetailsCard);
        cardsContainer.add(bookingFormCard);

        // Add cards container to main panel
        mainPanel.add(cardsContainer);

        // Create payment summary panel
        JPanel paymentSummaryPanel = createPaymentSummaryPanel();
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(paymentSummaryPanel);

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
        cancelButton.addActionListener(e -> mainFrame.showPanel(MainFrame.HOME_PANEL));

        JButton bookButton = new JButton("Book Tickets");
        bookButton.setFont(new Font("Arial", Font.BOLD, 14));
        bookButton.setBackground(ACCENT_COLOR);
        bookButton.setForeground(Color.WHITE);
        bookButton.setFocusPainted(false);
        bookButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bookButton.addActionListener(e -> handleBooking());

        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(bookButton);

        // Add button panel to main panel
        mainPanel.add(buttonPanel);

        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
    }

    /**
     * Create event details card
     */
    private JPanel createEventDetailsCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Card header
        JLabel headerLabel = new JLabel("Event Details");
        headerLabel.setFont(SUBTITLE_FONT);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Event details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Event image placeholder
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Create a gradient background as placeholder for image
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(100, 180, 200),
                    0, getHeight(), new Color(45, 125, 154)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Draw event icon as placeholder
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 36));
                String text = "🎭";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(text, x, y);
            }
        };
        imagePanel.setPreferredSize(new Dimension(0, 150));

        // Event name
        JLabel eventNameTitle = new JLabel("Event:");
        eventNameTitle.setFont(new Font("Arial", Font.BOLD, 14));
        eventNameLabel = new JLabel();
        eventNameLabel.setFont(REGULAR_FONT);

        // Event date
        JLabel eventDateTitle = new JLabel("Date:");
        eventDateTitle.setFont(new Font("Arial", Font.BOLD, 14));
        eventDateLabel = new JLabel();
        eventDateLabel.setFont(REGULAR_FONT);

        // Event time
        JLabel eventTimeTitle = new JLabel("Time:");
        eventTimeTitle.setFont(new Font("Arial", Font.BOLD, 14));
        eventTimeLabel = new JLabel();
        eventTimeLabel.setFont(REGULAR_FONT);

        // Event venue
        JLabel eventVenueTitle = new JLabel("Venue:");
        eventVenueTitle.setFont(new Font("Arial", Font.BOLD, 14));
        eventVenueLabel = new JLabel();
        eventVenueLabel.setFont(REGULAR_FONT);

        // Available seats
        JLabel availableSeatsTitle = new JLabel("Available Seats:");
        availableSeatsTitle.setFont(new Font("Arial", Font.BOLD, 14));
        availableSeatsLabel = new JLabel();
        availableSeatsLabel.setFont(REGULAR_FONT);

        // Base price
        JLabel basePriceTitle = new JLabel("Base Price:");
        basePriceTitle.setFont(new Font("Arial", Font.BOLD, 14));
        basePriceLabel = new JLabel();
        basePriceLabel.setFont(REGULAR_FONT);

        // Add components to details panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        detailsPanel.add(imagePanel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        detailsPanel.add(eventNameTitle, gbc);
        gbc.gridx = 1;
        detailsPanel.add(eventNameLabel, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        detailsPanel.add(eventDateTitle, gbc);
        gbc.gridx = 1;
        detailsPanel.add(eventDateLabel, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        detailsPanel.add(eventTimeTitle, gbc);
        gbc.gridx = 1;
        detailsPanel.add(eventTimeLabel, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        detailsPanel.add(eventVenueTitle, gbc);
        gbc.gridx = 1;
        detailsPanel.add(eventVenueLabel, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        detailsPanel.add(availableSeatsTitle, gbc);
        gbc.gridx = 1;
        detailsPanel.add(availableSeatsLabel, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        detailsPanel.add(basePriceTitle, gbc);
        gbc.gridx = 1;
        detailsPanel.add(basePriceLabel, gbc);

        // Add components to card
        card.add(headerLabel, BorderLayout.NORTH);
        card.add(detailsPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Create booking form card
     */
    private JPanel createBookingFormCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Card header
        JLabel headerLabel = new JLabel("Your Information");
        headerLabel.setFont(SUBTITLE_FONT);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Customer name
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        customerNameField = new JTextField(20);
        customerNameField.setFont(REGULAR_FONT);
        customerNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Customer email
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        customerEmailField = new JTextField(20);
        customerEmailField.setFont(REGULAR_FONT);
        customerEmailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Customer phone
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        customerPhoneField = new JTextField(20);
        customerPhoneField.setFont(REGULAR_FONT);
        customerPhoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Seat type
        JLabel seatTypeLabel = new JLabel("Seat Type:");
        seatTypeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        seatTypeComboBox = new JComboBox<>(new String[]{
            Booking.SEAT_TYPE_STANDARD,
            Booking.SEAT_TYPE_VIP,
            Booking.SEAT_TYPE_PREMIUM
        });
        seatTypeComboBox.setFont(REGULAR_FONT);
        seatTypeComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        seatTypeComboBox.addActionListener(e -> updateTotalPrice());

        // Quantity
        JLabel quantityLabel = new JLabel("Number of Tickets:");
        quantityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 10, 1);
        quantitySpinner = new JSpinner(spinnerModel);
        quantitySpinner.setFont(REGULAR_FONT);
        quantitySpinner.addChangeListener(e -> updateTotalPrice());
        JComponent editor = quantitySpinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
            spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
        }

        // Add components to form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(nameLabel, gbc);

        gbc.gridy = 1;
        formPanel.add(customerNameField, gbc);

        gbc.gridy = 2;
        formPanel.add(emailLabel, gbc);

        gbc.gridy = 3;
        formPanel.add(customerEmailField, gbc);

        gbc.gridy = 4;
        formPanel.add(phoneLabel, gbc);

        gbc.gridy = 5;
        formPanel.add(customerPhoneField, gbc);

        gbc.gridy = 6;
        formPanel.add(seatTypeLabel, gbc);

        gbc.gridy = 7;
        formPanel.add(seatTypeComboBox, gbc);

        gbc.gridy = 8;
        formPanel.add(quantityLabel, gbc);

        gbc.gridy = 9;
        formPanel.add(quantitySpinner, gbc);

        // Add components to card
        card.add(headerLabel, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Create payment summary panel
     */
    private JPanel createPaymentSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel summaryLabel = new JLabel("Payment Summary");
        summaryLabel.setFont(SUBTITLE_FONT);

        JPanel pricePanel = new JPanel(new BorderLayout(10, 0));
        pricePanel.setOpaque(false);

        JLabel priceLabel = new JLabel("Total Price:");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));

        totalPriceLabel = new JLabel("$0.00");
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        totalPriceLabel.setForeground(ACCENT_COLOR);

        pricePanel.add(priceLabel, BorderLayout.WEST);
        pricePanel.add(totalPriceLabel, BorderLayout.EAST);

        panel.add(summaryLabel, BorderLayout.NORTH);
        panel.add(pricePanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Set the event ID and load event details
     * @param eventId event ID
     */
    public void setEventId(int eventId) {
        this.eventId = eventId;
        loadEventDetails();
    }

    /**
     * Load event details
     */
    private void loadEventDetails() {
        currentEvent = eventController.getEventById(eventId);

        if (currentEvent != null) {
            // Use more user-friendly date and time formats
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

            eventNameLabel.setText(currentEvent.getName());
            eventDateLabel.setText(currentEvent.getDate().format(dateFormatter));
            eventTimeLabel.setText(currentEvent.getTime().format(timeFormatter));
            eventVenueLabel.setText(currentEvent.getVenue());
            availableSeatsLabel.setText(String.valueOf(currentEvent.getAvailableSeats()));
            basePriceLabel.setText("$" + currentEvent.getBasePrice());

            // Reset form fields
            customerNameField.setText("");
            customerEmailField.setText("");
            customerPhoneField.setText("");
            seatTypeComboBox.setSelectedIndex(0);
            quantitySpinner.setValue(1);

            // Update total price
            updateTotalPrice();

            logger.info("Loaded event details for event ID: {}", eventId);
        } else {
            logger.error("Failed to load event details for event ID: {}", eventId);
            JOptionPane.showMessageDialog(this,
                "Failed to load event details.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            mainFrame.showPanel(MainFrame.HOME_PANEL);
        }
    }

    /**
     * Update total price based on seat type and quantity
     */
    private void updateTotalPrice() {
        if (currentEvent != null) {
            String seatType = (String) seatTypeComboBox.getSelectedItem();
            int quantity = (int) quantitySpinner.getValue();

            BigDecimal totalPrice = Booking.calculateTotalPrice(currentEvent.getBasePrice(), seatType, quantity);
            totalPriceLabel.setText("$" + totalPrice);
        }
    }

    /**
     * Handle booking button click
     */
    private void handleBooking() {
        if (currentEvent == null) {
            return;
        }

        // Get form values
        String customerName = customerNameField.getText().trim();
        String customerEmail = customerEmailField.getText().trim();
        String customerPhone = customerPhoneField.getText().trim();
        String seatType = (String) seatTypeComboBox.getSelectedItem();
        int quantity = (int) quantitySpinner.getValue();

        // Validate input
        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter your name.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            customerNameField.requestFocus();
            return;
        }

        if (!customerEmail.isEmpty() && !ValidationUtils.isValidEmail(customerEmail)) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            customerEmailField.requestFocus();
            return;
        }

        if (!customerPhone.isEmpty() && !ValidationUtils.isValidPhone(customerPhone)) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid phone number.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            customerPhoneField.requestFocus();
            return;
        }

        if (quantity > currentEvent.getAvailableSeats()) {
            JOptionPane.showMessageDialog(this,
                "Not enough seats available. Please select a smaller quantity.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            quantitySpinner.requestFocus();
            return;
        }

        // Create booking
        boolean success = bookingController.createBooking(
            customerName, customerEmail, customerPhone, eventId, seatType, quantity);

        if (success) {
            logger.info("Booking created for event ID: {} by customer: {}", eventId, customerName);

            // Format date and time for confirmation
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
            String formattedDate = currentEvent.getDate().format(dateFormatter);
            String formattedTime = currentEvent.getTime().format(timeFormatter);

            // Create a custom confirmation dialog
            JPanel confirmPanel = new JPanel(new BorderLayout(0, 20));
            confirmPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Header
            JLabel headerLabel = new JLabel("Booking Confirmed!");
            headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
            headerLabel.setForeground(new Color(0, 150, 0));
            headerLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Confirmation message
            JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));

            // Add booking details
            addConfirmationDetail(detailsPanel, "Event:", currentEvent.getName());
            addConfirmationDetail(detailsPanel, "Date:", formattedDate);
            addConfirmationDetail(detailsPanel, "Time:", formattedTime);
            addConfirmationDetail(detailsPanel, "Venue:", currentEvent.getVenue());
            addConfirmationDetail(detailsPanel, "Seat Type:", seatType);
            addConfirmationDetail(detailsPanel, "Quantity:", String.valueOf(quantity));
            addConfirmationDetail(detailsPanel, "Total Price:", totalPriceLabel.getText());
            addConfirmationDetail(detailsPanel, "Booking ID:", "TKT-" + System.currentTimeMillis() % 10000);

            // Footer message
            JLabel footerLabel = new JLabel("<html><body style='width: 300px; text-align: center;'>" +
                "Thank you for your booking! A confirmation email will be sent to " +
                customerEmail + " with your ticket details.</body></html>");
            footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            footerLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Add components to panel
            confirmPanel.add(headerLabel, BorderLayout.NORTH);
            confirmPanel.add(detailsPanel, BorderLayout.CENTER);
            confirmPanel.add(footerLabel, BorderLayout.SOUTH);

            // Show custom dialog
            JOptionPane.showMessageDialog(
                this,
                confirmPanel,
                "Booking Confirmation",
                JOptionPane.PLAIN_MESSAGE
            );

            // Return to home panel
            mainFrame.showPanel(MainFrame.HOME_PANEL);
        } else {
            logger.error("Failed to create booking for event ID: {}", eventId);

            // Create a custom error dialog
            JPanel errorPanel = new JPanel(new BorderLayout(0, 15));
            errorPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel errorIcon = new JLabel("❌");
            errorIcon.setFont(new Font("Arial", Font.PLAIN, 48));
            errorIcon.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel errorMessage = new JLabel("<html><body style='width: 250px; text-align: center;'>" +
                "We couldn't complete your booking at this time. This could be due to:<br><br>" +
                "• Insufficient available seats<br>" +
                "• Connection issues with our booking system<br>" +
                "• The event may no longer be available<br><br>" +
                "Please try again or contact customer support.</body></html>");
            errorMessage.setFont(REGULAR_FONT);

            errorPanel.add(errorIcon, BorderLayout.NORTH);
            errorPanel.add(errorMessage, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(
                this,
                errorPanel,
                "Booking Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Helper method to add a detail row to the confirmation panel
     */
    private void addConfirmationDetail(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(REGULAR_FONT);

        panel.add(labelComponent);
        panel.add(valueComponent);
    }
}
