package com.ticketbooking.dao;

import com.ticketbooking.database.DBConnection;
import com.ticketbooking.model.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Event entities
 */
public class EventDAO {
    private static final Logger logger = LogManager.getLogger(EventDAO.class);
    
    /**
     * Get all events from the database
     * @return List of all events
     */
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY date, time";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Event event = mapResultSetToEvent(rs);
                events.add(event);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all events", e);
        }
        
        return events;
    }
    
    /**
     * Get events by date range
     * @param startDate start date of the range
     * @param endDate end date of the range
     * @return List of events within the date range
     */
    public List<Event> getEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE date BETWEEN ? AND ? ORDER BY date, time";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Event event = mapResultSetToEvent(rs);
                    events.add(event);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving events by date range", e);
        }
        
        return events;
    }
    
    /**
     * Get event by ID
     * @param id event ID
     * @return Event object if found, null otherwise
     */
    public Event getEventById(int id) {
        String sql = "SELECT * FROM events WHERE id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvent(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving event by ID: " + id, e);
        }
        
        return null;
    }
    
    /**
     * Add a new event to the database
     * @param event Event object to add
     * @return true if successful, false otherwise
     */
    public boolean addEvent(Event event) {
        String sql = "INSERT INTO events (name, description, date, time, venue, total_seats, available_seats, base_price) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, event.getName());
            pstmt.setString(2, event.getDescription());
            pstmt.setDate(3, Date.valueOf(event.getDate()));
            pstmt.setTime(4, Time.valueOf(event.getTime()));
            pstmt.setString(5, event.getVenue());
            pstmt.setInt(6, event.getTotalSeats());
            pstmt.setInt(7, event.getAvailableSeats());
            pstmt.setBigDecimal(8, event.getBasePrice());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        event.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error adding event", e);
        }
        
        return false;
    }
    
    /**
     * Update an existing event in the database
     * @param event Event object to update
     * @return true if successful, false otherwise
     */
    public boolean updateEvent(Event event) {
        String sql = "UPDATE events SET name = ?, description = ?, date = ?, time = ?, " +
                     "venue = ?, total_seats = ?, available_seats = ?, base_price = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, event.getName());
            pstmt.setString(2, event.getDescription());
            pstmt.setDate(3, Date.valueOf(event.getDate()));
            pstmt.setTime(4, Time.valueOf(event.getTime()));
            pstmt.setString(5, event.getVenue());
            pstmt.setInt(6, event.getTotalSeats());
            pstmt.setInt(7, event.getAvailableSeats());
            pstmt.setBigDecimal(8, event.getBasePrice());
            pstmt.setInt(9, event.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating event with ID: " + event.getId(), e);
            return false;
        }
    }
    
    /**
     * Update available seats for an event
     * @param eventId event ID
     * @param bookedSeats number of seats booked
     * @return true if successful, false otherwise
     */
    public boolean updateAvailableSeats(int eventId, int bookedSeats) {
        String sql = "UPDATE events SET available_seats = available_seats - ? " +
                     "WHERE id = ? AND available_seats >= ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookedSeats);
            pstmt.setInt(2, eventId);
            pstmt.setInt(3, bookedSeats);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating available seats for event ID: " + eventId, e);
            return false;
        }
    }
    
    /**
     * Delete an event from the database
     * @param id event ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteEvent(int id) {
        String sql = "DELETE FROM events WHERE id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting event with ID: " + id, e);
            return false;
        }
    }
    
    /**
     * Map a ResultSet row to an Event object
     * @param rs ResultSet containing event data
     * @return Event object
     * @throws SQLException if a database access error occurs
     */
    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setName(rs.getString("name"));
        event.setDescription(rs.getString("description"));
        event.setDate(rs.getDate("date").toLocalDate());
        event.setTime(rs.getTime("time").toLocalTime());
        event.setVenue(rs.getString("venue"));
        event.setTotalSeats(rs.getInt("total_seats"));
        event.setAvailableSeats(rs.getInt("available_seats"));
        event.setBasePrice(rs.getBigDecimal("base_price"));
        return event;
    }
}
