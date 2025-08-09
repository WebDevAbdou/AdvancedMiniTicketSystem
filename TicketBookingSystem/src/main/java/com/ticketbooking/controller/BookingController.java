package com.ticketbooking.controller;

import com.ticketbooking.dao.BookingDAO;
import com.ticketbooking.model.Booking;
import com.ticketbooking.model.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Controller for Booking-related operations
 */
public class BookingController {
    private static final Logger logger = LogManager.getLogger(BookingController.class);
    private final BookingDAO bookingDAO;
    private final EventController eventController;
    
    // Regular expression for email validation
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    // Regular expression for phone number validation
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\+?[0-9]{10,15}$");
    
    public BookingController() {
        this.bookingDAO = new BookingDAO();
        this.eventController = new EventController();
    }
    
    /**
     * Get all bookings
     * @return List of all bookings
     */
    public List<Booking> getAllBookings() {
        logger.info("Getting all bookings");
        return bookingDAO.getAllBookings();
    }
    
    /**
     * Get bookings for a specific event
     * @param eventId event ID
     * @return List of bookings for the event
     */
    public List<Booking> getBookingsByEventId(int eventId) {
        logger.info("Getting bookings for event ID: {}", eventId);
        return bookingDAO.getBookingsByEventId(eventId);
    }
    
    /**
     * Get booking by ID
     * @param id booking ID
     * @return Booking object if found, null otherwise
     */
    public Booking getBookingById(int id) {
        logger.info("Getting booking with ID: {}", id);
        return bookingDAO.getBookingById(id);
    }
    
    /**
     * Create a new booking
     * @param customerName customer name
     * @param customerEmail customer email
     * @param customerPhone customer phone
     * @param eventId event ID
     * @param seatType type of seat
     * @param quantity number of tickets
     * @return true if successful, false otherwise
     */
    public boolean createBooking(String customerName, String customerEmail, String customerPhone, 
                                int eventId, String seatType, int quantity) {
        logger.info("Creating new booking for event ID: {} by customer: {}", eventId, customerName);
        
        // Validate input
        if (customerName == null || customerName.trim().isEmpty()) {
            logger.error("Customer name cannot be empty");
            return false;
        }
        
        if (customerEmail != null && !customerEmail.trim().isEmpty() && 
            !EMAIL_PATTERN.matcher(customerEmail).matches()) {
            logger.error("Invalid email format: {}", customerEmail);
            return false;
        }
        
        if (customerPhone != null && !customerPhone.trim().isEmpty() && 
            !PHONE_PATTERN.matcher(customerPhone).matches()) {
            logger.error("Invalid phone number format: {}", customerPhone);
            return false;
        }
        
        if (quantity <= 0) {
            logger.error("Quantity must be greater than zero");
            return false;
        }
        
        // Check if event exists and has enough available seats
        Event event = eventController.getEventById(eventId);
        if (event == null) {
            logger.error("Event with ID {} not found", eventId);
            return false;
        }
        
        if (!event.hasAvailableSeats(quantity)) {
            logger.error("Not enough available seats for event ID: {}", eventId);
            return false;
        }
        
        // Validate seat type
        if (!isValidSeatType(seatType)) {
            logger.error("Invalid seat type: {}", seatType);
            return false;
        }
        
        // Calculate total price
        BigDecimal totalPrice = Booking.calculateTotalPrice(event.getBasePrice(), seatType, quantity);
        
        // Create booking
        Booking booking = new Booking(customerName, customerEmail, customerPhone, eventId, seatType, quantity, totalPrice);
        return bookingDAO.addBooking(booking);
    }
    
    /**
     * Delete a booking
     * @param id booking ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteBooking(int id) {
        logger.info("Deleting booking with ID: {}", id);
        return bookingDAO.deleteBooking(id);
    }
    
    /**
     * Calculate total price for a booking
     * @param eventId event ID
     * @param seatType type of seat
     * @param quantity number of tickets
     * @return calculated total price
     */
    public BigDecimal calculateTotalPrice(int eventId, String seatType, int quantity) {
        Event event = eventController.getEventById(eventId);
        if (event == null) {
            logger.error("Event with ID {} not found", eventId);
            return BigDecimal.ZERO;
        }
        
        return Booking.calculateTotalPrice(event.getBasePrice(), seatType, quantity);
    }
    
    /**
     * Check if seat type is valid
     * @param seatType type of seat to check
     * @return true if valid, false otherwise
     */
    private boolean isValidSeatType(String seatType) {
        return Booking.SEAT_TYPE_STANDARD.equals(seatType) || 
               Booking.SEAT_TYPE_VIP.equals(seatType) || 
               Booking.SEAT_TYPE_PREMIUM.equals(seatType);
    }
}
