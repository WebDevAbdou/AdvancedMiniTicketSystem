package com.ticketbooking.dao;

import com.ticketbooking.database.DBConnection;
import com.ticketbooking.model.Booking;
import com.ticketbooking.model.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Booking entities
 */
public class BookingDAO {
    private static final Logger logger = LogManager.getLogger(BookingDAO.class);
    private final EventDAO eventDAO = new EventDAO();

    /**
     * Get all bookings from the database
     * @return List of all bookings
     */
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY booking_time DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Booking booking = mapResultSetToBooking(rs);
                // Load associated event
                Event event = eventDAO.getEventById(booking.getEventId());
                booking.setEvent(event);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all bookings", e);
        }

        return bookings;
    }

    /**
     * Get bookings for a specific event
     * @param eventId event ID
     * @return List of bookings for the event
     */
    public List<Booking> getBookingsByEventId(int eventId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE event_id = ? ORDER BY booking_time DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, eventId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    // Load associated event
                    Event event = eventDAO.getEventById(booking.getEventId());
                    booking.setEvent(event);
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving bookings for event ID: " + eventId, e);
        }

        return bookings;
    }

    /**
     * Get booking by ID
     * @param id booking ID
     * @return Booking object if found, null otherwise
     */
    public Booking getBookingById(int id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    // Load associated event
                    Event event = eventDAO.getEventById(booking.getEventId());
                    booking.setEvent(event);
                    return booking;
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving booking by ID: " + id, e);
        }

        return null;
    }

    /**
     * Add a new booking to the database
     * @param booking Booking object to add
     * @return true if successful, false otherwise
     */
    public boolean addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (customer_name, customer_email, customer_phone, " +
                     "event_id, seat_type, quantity, total_price) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getInstance().getConnection();

            // Important: Set autoCommit to false to start a transaction
            conn.setAutoCommit(false);
            logger.info("Starting transaction for new booking by: {}", booking.getCustomerName());

            // First check if the event exists and has enough seats
            String checkEventSql = "SELECT available_seats FROM events WHERE id = ? FOR UPDATE";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkEventSql)) {
                checkStmt.setInt(1, booking.getEventId());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        logger.error("Event with ID {} not found", booking.getEventId());
                        conn.rollback();
                        return false;
                    }

                    int availableSeats = rs.getInt("available_seats");
                    if (availableSeats < booking.getQuantity()) {
                        logger.error("Not enough seats available. Requested: {}, Available: {}",
                                    booking.getQuantity(), availableSeats);
                        conn.rollback();
                        return false;
                    }
                }
            }

            // Update available seats in the event
            String updateSeatsSql = "UPDATE events SET available_seats = available_seats - ? WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSeatsSql)) {
                updateStmt.setInt(1, booking.getQuantity());
                updateStmt.setInt(2, booking.getEventId());

                int updatedRows = updateStmt.executeUpdate();
                if (updatedRows == 0) {
                    logger.error("Failed to update available seats for event ID: {}", booking.getEventId());
                    conn.rollback();
                    return false;
                }

                logger.info("Updated available seats for event ID: {}, reduced by: {}",
                           booking.getEventId(), booking.getQuantity());
            }

            // Then create the booking
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, booking.getCustomerName());
            pstmt.setString(2, booking.getCustomerEmail());
            pstmt.setString(3, booking.getCustomerPhone());
            pstmt.setInt(4, booking.getEventId());
            pstmt.setString(5, booking.getSeatType());
            pstmt.setInt(6, booking.getQuantity());
            pstmt.setBigDecimal(7, booking.getTotalPrice());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        booking.setId(generatedKeys.getInt(1));
                        conn.commit();
                        logger.info("Booking transaction committed successfully. Booking ID: {}", booking.getId());
                        return true;
                    }
                }
            }

            logger.error("Failed to insert booking record");
            conn.rollback();
            return false;
        } catch (SQLException e) {
            logger.error("Error adding booking: {}", e.getMessage(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.info("Transaction rolled back due to error");
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction: {}", ex.getMessage(), ex);
                }
            }
            return false;
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    logger.error("Error closing prepared statement: {}", e.getMessage(), e);
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                    logger.info("Database connection closed after booking operation");
                } catch (SQLException e) {
                    logger.error("Error closing connection: {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Delete a booking from the database
     * @param id booking ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteBooking(int id) {
        String sql = "DELETE FROM bookings WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting booking with ID: " + id, e);
            return false;
        }
    }

    /**
     * Map a ResultSet row to a Booking object
     * @param rs ResultSet containing booking data
     * @return Booking object
     * @throws SQLException if a database access error occurs
     */
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getInt("id"));
        booking.setCustomerName(rs.getString("customer_name"));
        booking.setCustomerEmail(rs.getString("customer_email"));
        booking.setCustomerPhone(rs.getString("customer_phone"));
        booking.setEventId(rs.getInt("event_id"));
        booking.setSeatType(rs.getString("seat_type"));
        booking.setQuantity(rs.getInt("quantity"));
        booking.setTotalPrice(rs.getBigDecimal("total_price"));

        Timestamp bookingTime = rs.getTimestamp("booking_time");
        if (bookingTime != null) {
            booking.setBookingTime(bookingTime.toLocalDateTime());
        } else {
            booking.setBookingTime(LocalDateTime.now());
        }

        return booking;
    }
}
