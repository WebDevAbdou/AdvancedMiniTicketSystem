package com.ticketbooking.view;

import com.ticketbooking.controller.EventController;
import com.ticketbooking.model.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for displaying available events with modern card-based UI
 */
@SuppressWarnings("unused")
public class HomePanel extends JPanel {
    private static final Logger logger = LogManager.getLogger(HomePanel.class);

    private final MainFrame mainFrame;
    private final EventController eventController;

    private JPanel eventsContainer;
    private JComboBox<String> filterComboBox;
    private JTextField searchField;
    private JPanel featuredEventsPanel;
    private List<Event> currentEvents;
    private Event selectedEvent;

    // Constants for styling - Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181); // Material Indigo
    private static final Color SECONDARY_COLOR = new Color(245, 245, 250);
    private static final Color ACCENT_COLOR = new Color(255, 87, 34); // Material Deep Orange
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 255);
    private static final Color CARD_HOVER_COLOR = new Color(237, 242, 251);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    /**
     * Constructor
     * @param mainFrame reference to the main frame
     */
    public HomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.eventController = new EventController();
        this.currentEvents = new ArrayList<>();

        // Set up the panel
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);

        // Create components
        initializeComponents();

        logger.info("Home panel initialized");
    }

    /**
     * Initialize panel components
     */
    private void initializeComponents() {
        // Create main content panel with padding
        JPanel contentPanel = new JPanel(new BorderLayout(10, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Create header panel with gradient background
        JPanel headerPanel = createGradientHeaderPanel();

        // Create search and filter panel
        JPanel searchFilterPanel = createSearchFilterPanel();

        // Create featured events panel (carousel-like)
        featuredEventsPanel = createFeaturedEventsPanel();

        // Create events container with scroll
        JScrollPane scrollPane = createEventsScrollPane();

        // Add components to content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(searchFilterPanel, BorderLayout.CENTER);

        // Create main events panel that will hold featured and regular events
        JPanel mainEventsPanel = new JPanel(new BorderLayout(0, 20));
        mainEventsPanel.setBackground(Color.WHITE);
        mainEventsPanel.add(featuredEventsPanel, BorderLayout.NORTH);
        mainEventsPanel.add(scrollPane, BorderLayout.CENTER);

        // Add main events panel to content
        contentPanel.add(mainEventsPanel, BorderLayout.SOUTH);

        // Add content panel to this panel
        add(contentPanel, BorderLayout.CENTER);

        // Initial data load
        refreshEventList();
    }

    /**
     * Create a modern gradient header panel with visual elements
     */
    private JPanel createGradientHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Create modern gradient background
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, w, h, PRIMARY_COLOR.darker());
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);

                // Add decorative elements
                g2d.setColor(new Color(255, 255, 255, 30));

                // Draw some circles for a modern look
                g2d.fillOval(w-100, -20, 120, 120);
                g2d.fillOval(w-180, h-60, 80, 80);
                g2d.fillOval(w/2, h-40, 40, 40);

                // Add subtle pattern
                g2d.setColor(new Color(255, 255, 255, 10));
                for (int i = 0; i < h; i += 4) {
                    g2d.drawLine(0, i, w, i);
                }
            }
        };

        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(getWidth(), 120));

        JPanel titlePanel = new JPanel(new BorderLayout(0, 5));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Create modern title with shadow effect
        JLabel titleLabel = new JLabel("Discover Amazing Events");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        // Create modern subtitle
        JLabel subtitleLabel = new JLabel("Book tickets for the best events in your area");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        subtitleLabel.setForeground(new Color(240, 240, 240));

        // Create a call-to-action button
        JButton exploreButton = new JButton("Explore All Events");
        exploreButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        exploreButton.setBackground(ACCENT_COLOR);
        exploreButton.setForeground(Color.WHITE);
        exploreButton.setFocusPainted(false);
        exploreButton.setBorderPainted(false);
        exploreButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exploreButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Add hover effect
        exploreButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                exploreButton.setBackground(ACCENT_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exploreButton.setBackground(ACCENT_COLOR);
            }
        });

        exploreButton.addActionListener(e -> {
            filterComboBox.setSelectedItem("All Events");
            refreshEventList();
        });

        // Add components to title panel
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(exploreButton);

        // Add panels to header
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Create modern search and filter panel
     */
    private JPanel createSearchFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 15, 30));

        // Modern search panel with rounded corners
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded rectangle background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // Draw subtle border
                g2d.setColor(new Color(220, 220, 220));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
            }
        };
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));

        // Modern search icon
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        searchIcon.setHorizontalAlignment(SwingConstants.CENTER);
        searchIcon.setPreferredSize(new Dimension(30, 30));
        searchIcon.setForeground(new Color(100, 100, 100));

        // Modern search field with no border
        searchField = new JTextField(20);
        searchField.setFont(REGULAR_FONT);
        searchField.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
        searchField.setOpaque(false);
        searchField.setForeground(new Color(60, 60, 60));

        // Add placeholder text
        searchField.setText("Search for events...");
        searchField.setForeground(new Color(180, 180, 180));

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search for events...")) {
                    searchField.setText("");
                    searchField.setForeground(new Color(60, 60, 60));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search for events...");
                    searchField.setForeground(new Color(180, 180, 180));
                }
            }
        });

        searchField.addActionListener(e -> searchEvents());

        // Modern search button with hover effect
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchButton.setBackground(PRIMARY_COLOR);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Add hover effect
        searchButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                searchButton.setBackground(PRIMARY_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                searchButton.setBackground(PRIMARY_COLOR);
            }
        });

        searchButton.addActionListener(e -> searchEvents());

        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Modern filter panel with material design
        JPanel filterPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded rectangle background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // Draw subtle border
                g2d.setColor(new Color(220, 220, 220));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
            }
        };
        filterPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        filterPanel.setPreferredSize(new Dimension(250, 40));

        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterLabel.setForeground(new Color(80, 80, 80));

        // Modern styled combo box
        filterComboBox = new JComboBox<>(new String[]{"All Events", "Today", "This Week", "This Month"});
        filterComboBox.setFont(REGULAR_FONT);
        filterComboBox.setBackground(Color.WHITE);
        filterComboBox.setForeground(PRIMARY_COLOR);
        filterComboBox.setBorder(BorderFactory.createEmptyBorder());
        filterComboBox.setFocusable(false);
        filterComboBox.addActionListener(e -> refreshEventList());

        filterPanel.add(filterLabel);
        filterPanel.add(filterComboBox);

        // Add search and filter to main panel
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(filterPanel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Create modern featured events panel with carousel-like design
     */
    private JPanel createFeaturedEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 30, 25, 30));

        // Create header with modern styling
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel featuredLabel = new JLabel("Featured Events");
        featuredLabel.setFont(SUBTITLE_FONT);
        featuredLabel.setForeground(new Color(50, 50, 50));

        // Add a decorative accent line
        JPanel accentLine = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw gradient accent line
                GradientPaint gp = new GradientPaint(
                    0, 0, PRIMARY_COLOR,
                    getWidth(), 0, ACCENT_COLOR
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, 80, 3, 3, 3);
            }
        };
        accentLine.setPreferredSize(new Dimension(100, 3));
        accentLine.setOpaque(false);

        JPanel titlePanel = new JPanel(new BorderLayout(0, 5));
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.add(featuredLabel, BorderLayout.CENTER);
        titlePanel.add(accentLine, BorderLayout.SOUTH);

        // Add "View All" link
        JLabel viewAllLink = new JLabel("View All");
        viewAllLink.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewAllLink.setForeground(PRIMARY_COLOR);
        viewAllLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewAllLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                filterComboBox.setSelectedItem("All Events");
                refreshEventList();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                viewAllLink.setText("<html><u>View All</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                viewAllLink.setText("View All");
            }
        });

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(viewAllLink, BorderLayout.EAST);

        // Create modern carousel panel with horizontal scrolling
        JPanel carouselPanel = new JPanel();
        carouselPanel.setLayout(new BoxLayout(carouselPanel, BoxLayout.X_AXIS));
        carouselPanel.setBackground(BACKGROUND_COLOR);

        // Add scroll buttons for better UX
        JButton leftButton = createScrollButton("◀", -1);
        JButton rightButton = createScrollButton("▶", 1);

        // Create scroll pane for horizontal scrolling
        JScrollPane scrollPane = new JScrollPane(carouselPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        // Create navigation panel
        JPanel navigationPanel = new JPanel(new BorderLayout());
        navigationPanel.setBackground(BACKGROUND_COLOR);
        navigationPanel.add(leftButton, BorderLayout.WEST);
        navigationPanel.add(scrollPane, BorderLayout.CENTER);
        navigationPanel.add(rightButton, BorderLayout.EAST);

        // Add components to main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(navigationPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create a scroll button for the carousel
     */
    private JButton createScrollButton(String text, int direction) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(PRIMARY_COLOR);
        button.setBackground(new Color(240, 240, 250));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(230, 230, 245));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(240, 240, 250));
            }
        });

        // Add scroll functionality
        button.addActionListener(e -> {
            Component parent = button.getParent();
            if (parent instanceof JPanel) {
                JPanel panel = (JPanel) parent;
                Component[] components = panel.getComponents();
                for (Component component : components) {
                    if (component instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) component;
                        JViewport viewport = scrollPane.getViewport();
                        Point position = viewport.getViewPosition();
                        position.x += direction * 300; // Scroll by 300 pixels
                        if (position.x < 0) position.x = 0;
                        viewport.setViewPosition(position);
                        break;
                    }
                }
            }
        });

        return button;
    }

    /**
     * Create events scroll pane
     */
    private JScrollPane createEventsScrollPane() {
        // Create events container
        eventsContainer = new JPanel();
        eventsContainer.setLayout(new BoxLayout(eventsContainer, BoxLayout.Y_AXIS));
        eventsContainer.setBackground(Color.WHITE);

        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(eventsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    /**
     * Create an event card panel with modern design
     */
    private JPanel createEventCard(Event event) {
        // Format date and time
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        String formattedDate = event.getDate().format(dateFormatter);
        String formattedTime = event.getTime().format(timeFormatter);

        // Create card panel with shadow effect
        JPanel shadowPanel = new JPanel(new BorderLayout());
        shadowPanel.setBackground(BACKGROUND_COLOR);
        shadowPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        shadowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JPanel cardPanel = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw card background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };

        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        cardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add mouse listener for selection and hover effects
        cardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedEvent = event;
                mainFrame.showBookingPanel(event.getId());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                cardPanel.setBackground(CARD_HOVER_COLOR);
                cardPanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cardPanel.setBackground(Color.WHITE);
                cardPanel.repaint();
            }
        });

        // Create image panel with rounded corners
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create a gradient background as placeholder for image
                GradientPaint gp = new GradientPaint(
                    0, 0, PRIMARY_COLOR.brighter(),
                    0, getHeight(), PRIMARY_COLOR
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Draw event name initial as placeholder
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 36));
                String initial = event.getName().substring(0, 1).toUpperCase();
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initial)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(initial, x, y);
            }
        };
        imagePanel.setPreferredSize(new Dimension(120, 120));

        // Create info panel with modern styling
        JPanel infoPanel = new JPanel(new BorderLayout(0, 8));
        infoPanel.setOpaque(false);

        // Event name with modern font
        JLabel nameLabel = new JLabel(event.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(new Color(50, 50, 50));

        // Event details with icons and modern layout
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        JLabel dateLabel = new JLabel("📅  " + formattedDate + " at " + formattedTime);
        dateLabel.setFont(REGULAR_FONT);
        dateLabel.setForeground(new Color(80, 80, 80));
        dateLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel venueLabel = new JLabel("📍  " + event.getVenue());
        venueLabel.setFont(REGULAR_FONT);
        venueLabel.setForeground(new Color(80, 80, 80));
        venueLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        // Create availability indicator with color based on seats
        String availabilityText = event.getAvailableSeats() + " seats available";
        Color availabilityColor;

        if (event.getAvailableSeats() < 10) {
            availabilityColor = new Color(220, 53, 69); // Danger red
            availabilityText = "Only " + availabilityText + " - Book soon!";
        } else if (event.getAvailableSeats() < 50) {
            availabilityColor = new Color(255, 193, 7); // Warning yellow
        } else {
            availabilityColor = new Color(40, 167, 69); // Success green
        }

        JLabel seatsLabel = new JLabel("🎟️  " + availabilityText);
        seatsLabel.setFont(REGULAR_FONT);
        seatsLabel.setForeground(availabilityColor);

        detailsPanel.add(dateLabel);
        detailsPanel.add(venueLabel);
        detailsPanel.add(seatsLabel);

        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(detailsPanel, BorderLayout.CENTER);

        // Create price panel with modern styling
        JPanel pricePanel = new JPanel(new BorderLayout(0, 10));
        pricePanel.setOpaque(false);

        // Price with currency symbol
        JLabel priceLabel = new JLabel("$" + event.getBasePrice());
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        priceLabel.setForeground(PRIMARY_COLOR);
        priceLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Modern button with hover effect
        JButton bookButton = new JButton("Book Now");
        bookButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bookButton.setBackground(ACCENT_COLOR);
        bookButton.setForeground(Color.WHITE);
        bookButton.setFocusPainted(false);
        bookButton.setBorderPainted(false);
        bookButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Add hover effect to button
        bookButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bookButton.setBackground(ACCENT_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                bookButton.setBackground(ACCENT_COLOR);
            }
        });

        bookButton.addActionListener(e -> {
            selectedEvent = event;
            mainFrame.showBookingPanel(event.getId());
        });

        pricePanel.add(priceLabel, BorderLayout.NORTH);
        pricePanel.add(bookButton, BorderLayout.SOUTH);

        // Add components to card
        cardPanel.add(imagePanel, BorderLayout.WEST);
        cardPanel.add(infoPanel, BorderLayout.CENTER);
        cardPanel.add(pricePanel, BorderLayout.EAST);

        // Add card to shadow panel
        shadowPanel.add(cardPanel, BorderLayout.CENTER);

        return shadowPanel;
    }

    /**
     * Create a modern featured event card
     */
    private JPanel createFeaturedEventCard(Event event) {
        // Format date
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM d");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        String formattedDate = event.getDate().format(dateFormatter);
        String formattedTime = event.getTime().format(timeFormatter);

        // Create shadow panel for 3D effect
        JPanel shadowPanel = new JPanel(new BorderLayout());
        shadowPanel.setBackground(BACKGROUND_COLOR);
        shadowPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        // Create card panel with rounded corners
        JPanel cardPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create a modern gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0, PRIMARY_COLOR,
                    0, getHeight(), PRIMARY_COLOR.darker()
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Add a subtle pattern overlay
                g2d.setColor(new Color(255, 255, 255, 20));
                for (int i = 0; i < getHeight(); i += 4) {
                    g2d.drawLine(0, i, getWidth(), i);
                }
            }
        };

        cardPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        cardPanel.setPreferredSize(new Dimension(280, 180));
        cardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add mouse listener for selection and hover effects
        cardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedEvent = event;
                mainFrame.showBookingPanel(event.getId());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                cardPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 18, 18));
                cardPanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cardPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                cardPanel.repaint();
            }
        });

        // Event name with modern font
        JLabel nameLabel = new JLabel(event.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);

        // Event date, time and venue with icons
        JLabel dateVenueLabel = new JLabel("📅 " + formattedDate + " • ⏰ " + formattedTime);
        dateVenueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateVenueLabel.setForeground(new Color(240, 240, 240));

        JLabel venueLabel = new JLabel("📍 " + event.getVenue());
        venueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        venueLabel.setForeground(new Color(240, 240, 240));

        // Available seats indicator
        JLabel seatsLabel = new JLabel("🎟️ " + event.getAvailableSeats() + " seats available");
        seatsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        seatsLabel.setForeground(new Color(240, 240, 240));

        // Modern price tag with shadow effect
        JPanel pricePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw price tag background
                g2d.setColor(ACCENT_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
        };
        pricePanel.setLayout(new BorderLayout());
        pricePanel.setOpaque(false);

        JLabel priceLabel = new JLabel("$" + event.getBasePrice());
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        priceLabel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        pricePanel.add(priceLabel, BorderLayout.CENTER);

        // Book now button
        JButton bookButton = new JButton("Book Now");
        bookButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookButton.setForeground(PRIMARY_COLOR.darker());
        bookButton.setBackground(Color.WHITE);
        bookButton.setFocusPainted(false);
        bookButton.setBorderPainted(false);
        bookButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Add hover effect to button
        bookButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bookButton.setBackground(new Color(240, 240, 240));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                bookButton.setBackground(Color.WHITE);
            }
        });

        bookButton.addActionListener(e -> {
            selectedEvent = event;
            mainFrame.showBookingPanel(event.getId());
        });

        // Add components to card with modern layout
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(dateVenueLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(venueLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(seatsLabel);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.add(pricePanel, BorderLayout.WEST);
        bottomPanel.add(bookButton, BorderLayout.EAST);

        cardPanel.add(infoPanel, BorderLayout.CENTER);
        cardPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add card to shadow panel
        shadowPanel.add(cardPanel, BorderLayout.CENTER);

        return shadowPanel;
    }

    /**
     * Search events based on search field text
     */
    private void searchEvents() {
        String searchText = searchField.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            refreshEventList(); // Reset to normal view if search is empty
            return;
        }

        // Get all events and filter by search text
        List<Event> allEvents = eventController.getAllEvents();
        List<Event> filteredEvents = new ArrayList<>();

        for (Event event : allEvents) {
            if (event.getName().toLowerCase().contains(searchText) ||
                event.getVenue().toLowerCase().contains(searchText) ||
                event.getDescription().toLowerCase().contains(searchText)) {
                filteredEvents.add(event);
            }
        }

        // Update UI with filtered events
        updateEventsUI(filteredEvents);
        logger.info("Events searched with query: {}, found {} results", searchText, filteredEvents.size());
    }

    /**
     * Refresh event list based on selected filter
     */
    public void refreshEventList() {
        // Get events based on filter
        List<Event> events;
        String filter = (String) filterComboBox.getSelectedItem();

        if ("Today".equals(filter)) {
            LocalDate today = LocalDate.now();
            events = eventController.getEventsByDateRange(today, today);
        } else if ("This Week".equals(filter)) {
            LocalDate today = LocalDate.now();
            LocalDate endOfWeek = today.plusDays(7);
            events = eventController.getEventsByDateRange(today, endOfWeek);
        } else if ("This Month".equals(filter)) {
            LocalDate today = LocalDate.now();
            LocalDate endOfMonth = today.plusMonths(1);
            events = eventController.getEventsByDateRange(today, endOfMonth);
        } else {
            // All Events
            events = eventController.getAllEvents();
        }

        // Update UI with events
        updateEventsUI(events);
        logger.info("Event list refreshed with filter: {}, found {} events", filter, events.size());
    }

    /**
     * Update UI with events using modern design
     */
    private void updateEventsUI(List<Event> events) {
        // Store current events
        currentEvents = events;

        // Clear containers
        eventsContainer.removeAll();

        // Find the carousel panel inside the navigation panel
        JPanel navigationPanel = (JPanel) featuredEventsPanel.getComponent(1);
        JScrollPane scrollPane = null;

        // Find the scroll pane in the navigation panel
        for (Component comp : navigationPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                scrollPane = (JScrollPane) comp;
                break;
            }
        }

        if (scrollPane != null) {
            JPanel carouselPanel = (JPanel) scrollPane.getViewport().getView();
            carouselPanel.removeAll();

            // Add featured events to carousel
            if (!events.isEmpty()) {
                // Add spacing at the beginning
                carouselPanel.add(Box.createHorizontalStrut(10));

                // Add up to 5 featured events to the carousel
                int featuredCount = Math.min(events.size(), 5);
                for (int i = 0; i < featuredCount; i++) {
                    Event event = events.get(i);
                    JPanel featuredCard = createFeaturedEventCard(event);

                    // Add spacing between cards
                    carouselPanel.add(Box.createHorizontalStrut(15));
                    carouselPanel.add(featuredCard);
                }

                // Add spacing at the end
                carouselPanel.add(Box.createHorizontalStrut(10));
            } else {
                // Show empty message in carousel
                JPanel emptyPanel = new JPanel(new BorderLayout());
                emptyPanel.setBackground(BACKGROUND_COLOR);
                emptyPanel.setPreferredSize(new Dimension(280, 180));

                JLabel emptyLabel = new JLabel("No featured events available", SwingConstants.CENTER);
                emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                emptyLabel.setForeground(new Color(150, 150, 150));

                emptyPanel.add(emptyLabel, BorderLayout.CENTER);
                carouselPanel.add(emptyPanel);
            }
        }

        // Add modern section header for all events
        if (!events.isEmpty()) {
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(BACKGROUND_COLOR);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

            // Create header with accent line
            JPanel titlePanel = new JPanel(new BorderLayout(0, 5));
            titlePanel.setBackground(BACKGROUND_COLOR);

            JLabel allEventsLabel = new JLabel("All Events");
            allEventsLabel.setFont(SUBTITLE_FONT);
            allEventsLabel.setForeground(new Color(50, 50, 50));

            // Add a decorative accent line
            JPanel accentLine = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Draw gradient accent line
                    GradientPaint gp = new GradientPaint(
                        0, 0, PRIMARY_COLOR,
                        getWidth(), 0, ACCENT_COLOR
                    );
                    g2d.setPaint(gp);
                    g2d.fillRoundRect(0, 0, 60, 3, 3, 3);
                }
            };
            accentLine.setPreferredSize(new Dimension(100, 3));
            accentLine.setOpaque(false);

            titlePanel.add(allEventsLabel, BorderLayout.CENTER);
            titlePanel.add(accentLine, BorderLayout.SOUTH);

            headerPanel.add(titlePanel, BorderLayout.WEST);

            // Add to container with modern padding
            JPanel headerWrapper = new JPanel(new BorderLayout());
            headerWrapper.setBackground(BACKGROUND_COLOR);
            headerWrapper.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
            headerWrapper.add(headerPanel, BorderLayout.CENTER);

            eventsContainer.add(headerWrapper);
        }

        // Add event cards with modern styling
        for (Event event : events) {
            // Create event card with modern padding
            JPanel cardWrapper = new JPanel(new BorderLayout());
            cardWrapper.setBackground(BACKGROUND_COLOR);
            cardWrapper.setBorder(BorderFactory.createEmptyBorder(0, 30, 15, 30));
            cardWrapper.add(createEventCard(event), BorderLayout.CENTER);

            eventsContainer.add(cardWrapper);
        }

        // Add empty message with modern styling if no events
        if (events.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBackground(BACKGROUND_COLOR);
            emptyPanel.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));

            // Create a visually appealing empty state
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(BACKGROUND_COLOR);
            contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Empty icon (placeholder)
            JLabel iconLabel = new JLabel("🔍");
            iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            iconLabel.setForeground(new Color(180, 180, 180));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Empty message
            JLabel emptyLabel = new JLabel("No events found");
            emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            emptyLabel.setForeground(new Color(100, 100, 100));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Suggestion message
            JLabel suggestionLabel = new JLabel("Try adjusting your search or filter criteria");
            suggestionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            suggestionLabel.setForeground(new Color(150, 150, 150));
            suggestionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            contentPanel.add(iconLabel);
            contentPanel.add(Box.createVerticalStrut(15));
            contentPanel.add(emptyLabel);
            contentPanel.add(Box.createVerticalStrut(10));
            contentPanel.add(suggestionLabel);

            emptyPanel.add(contentPanel, BorderLayout.CENTER);
            eventsContainer.add(emptyPanel);
        }

        // Refresh UI
        eventsContainer.revalidate();
        eventsContainer.repaint();
        featuredEventsPanel.revalidate();
        featuredEventsPanel.repaint();
    }
}
